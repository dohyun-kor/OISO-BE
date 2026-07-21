package com.nigahae.tourdata.controller;

import com.nigahae.tourdata.domain.TourDataJobType;
import com.nigahae.tourdata.service.TourDataResult;
import com.nigahae.tourdata.service.TourDataRunner;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TourIngestControllerTest {

    private final TourDataRunner runner = mock(TourDataRunner.class);
    private final TourDataController controller = new TourDataController(runner);

    @Test
    void controllerExposesOnlyFourSeparatedRefreshPaths() {
        Set<String> paths = Arrays.stream(TourDataController.class.getDeclaredMethods())
                .map(method -> method.getAnnotation(PostMapping.class))
                .filter(mapping -> mapping != null)
                .flatMap(mapping -> Arrays.stream(mapping.value()))
                .collect(Collectors.toSet());

        assertThat(paths).containsExactlyInAnyOrder(
                "/tour-spots/refresh",
                "/festivals/refresh",
                "/common-details/refresh",
                "/accommodations/refresh"
        );
        assertThat(paths).doesNotContain("/refresh");
    }

    @Test
    void eachEndpointDelegatesToItsOwnJob() {
        when(runner.run(TourDataJobType.TOUR_SPOTS, "manual-api")).thenReturn(TourDataResult.success(1));
        when(runner.run(TourDataJobType.FESTIVALS, "manual-api")).thenReturn(TourDataResult.success(2));
        when(runner.run(TourDataJobType.COMMON_DETAILS, "manual-api")).thenReturn(TourDataResult.success(3));
        when(runner.run(TourDataJobType.ACCOMMODATIONS, "manual-api")).thenReturn(TourDataResult.success(4));

        assertThat(controller.refreshTourSpots().getBody().processedCount()).isEqualTo(1);
        assertThat(controller.refreshFestivals().getBody().processedCount()).isEqualTo(2);
        assertThat(controller.refreshCommonDetails().getBody().processedCount()).isEqualTo(3);
        assertThat(controller.refreshAccommodations().getBody().processedCount()).isEqualTo(4);
    }

    @Test
    void responseReturnsOkForSuccess() {
        when(runner.run(TourDataJobType.TOUR_SPOTS, "manual-api")).thenReturn(TourDataResult.success(3));

        ResponseEntity<TourDataResult> response = controller.refreshTourSpots();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void responseReturnsMultiStatusForPartialSuccess() {
        when(runner.run(TourDataJobType.COMMON_DETAILS, "manual-api"))
                .thenReturn(TourDataResult.partial(3, 1, "one failed"));

        ResponseEntity<TourDataResult> response = controller.refreshCommonDetails();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.MULTI_STATUS);
        assertThat(response.getBody().failedCount()).isEqualTo(1);
    }

    @Test
    void responseReturnsServerErrorWhenAllItemsFail() {
        when(runner.run(TourDataJobType.ACCOMMODATIONS, "manual-api"))
                .thenReturn(TourDataResult.failure(2, "all failed"));

        assertThat(controller.refreshAccommodations().getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void responseReturnsConflictWhenSameJobIsRunning() {
        when(runner.run(TourDataJobType.FESTIVALS, "manual-api"))
                .thenReturn(TourDataResult.skipped("already running"));

        assertThat(controller.refreshFestivals().getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }
}
