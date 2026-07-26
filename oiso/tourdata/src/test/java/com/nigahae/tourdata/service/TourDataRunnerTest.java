package com.nigahae.tourdata.service;

import com.nigahae.tourdata.domain.TourDataJobType;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TourDataRunnerTest {

    @Test
    void runDelegatesToSelectedJob() {
        TourDataService service = mock(TourDataService.class);
        when(service.refreshFestivals()).thenReturn(TourDataResult.success(2));
        TourDataRunner runner = new TourDataRunner(service);

        assertThat(runner.run(TourDataJobType.FESTIVALS, "manual")).isEqualTo(TourDataResult.success(2));

        verify(service).refreshFestivals();
    }

    @Test
    void sameJobIsSkippedWhileDifferentJobCanRun() throws Exception {
        TourDataService service = mock(TourDataService.class);
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        when(service.refreshCommonDetails()).thenAnswer(invocation -> {
            started.countDown();
            release.await(5, TimeUnit.SECONDS);
            return TourDataResult.success(1);
        });
        when(service.refreshFestivals()).thenReturn(TourDataResult.success(1));
        TourDataRunner runner = new TourDataRunner(service);

        CompletableFuture<TourDataResult> first = CompletableFuture.supplyAsync(
                () -> runner.run(TourDataJobType.COMMON_DETAILS, "first")
        );
        assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();

        TourDataResult duplicate = runner.run(TourDataJobType.COMMON_DETAILS, "duplicate");
        TourDataResult otherJob = runner.run(TourDataJobType.FESTIVALS, "parallel");
        release.countDown();

        assertThat(duplicate.skipped()).isTrue();
        assertThat(otherJob.success()).isTrue();
        assertThat(first.get(5, TimeUnit.SECONDS).success()).isTrue();
    }
}
