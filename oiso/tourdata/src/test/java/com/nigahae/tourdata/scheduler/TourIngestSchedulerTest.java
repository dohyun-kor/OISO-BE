package com.nigahae.tourdata.scheduler;

import com.nigahae.tourdata.service.TourDataResult;
import com.nigahae.tourdata.service.TourDataRunner;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TourIngestSchedulerTest {

    @Test
    void refreshTourContentsDelegatesToService() {
        TourDataRunner runner = mock(TourDataRunner.class);
        when(runner.run("schedule")).thenReturn(TourDataResult.success(1));
        TourDataScheduler scheduler = new TourDataScheduler(runner);

        scheduler.refreshTourContents();

        verify(runner).run("schedule");
    }
}
