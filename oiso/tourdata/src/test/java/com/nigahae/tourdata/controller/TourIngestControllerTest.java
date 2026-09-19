package com.nigahae.tourdata.controller;

import com.nigahae.tourdata.TourDetailRefresher;
import com.nigahae.tourdata.TourDetailRefreshResult;
import com.nigahae.tourdata.domain.TourDataJobType;
import com.nigahae.tourdata.service.TourDataResult;
import com.nigahae.tourdata.service.TourDataRunner;
import com.nigahae.tourdata.service.TourDataService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TourIngestControllerTest {

    private final TourDataRunner runner = mock(TourDataRunner.class);
    private final TourDetailRefresher detailRefresher = mock(TourDetailRefresher.class);
    private final TourDataController controller = new TourDataController(runner, detailRefresher, mock(TourDataService.class));

    @Test
    void controllerExposesOnlySeparatedRefreshPaths() {
        Set<String> paths = Arrays.stream(TourDataController.class.getDeclaredMethods())
                .map(method -> method.getAnnotation(PostMapping.class))
                .filter(mapping -> mapping != null)
                .flatMap(mapping -> Arrays.stream(mapping.value()))
                .collect(Collectors.toSet());

        assertThat(paths).containsExactlyInAnyOrder(
                "/tour-spots/refresh",
                "/festivals/refresh",
                "/common-details/{spotId}/refresh",
                "/accommodations/refresh"
        );
        assertThat(paths).doesNotContain("/refresh", "/common-details/refresh");
    }

    @Test
    void batchEndpointsDelegateToTheirOwnJobs() {
        when(runner.run(TourDataJobType.TOUR_SPOTS, "manual-api")).thenReturn(TourDataResult.success(1));
        when(runner.run(TourDataJobType.FESTIVALS, "manual-api")).thenReturn(TourDataResult.success(2));
        when(runner.run(TourDataJobType.ACCOMMODATIONS, "manual-api")).thenReturn(TourDataResult.success(4));

        assertThat(controller.refreshTourSpots().getBody().processedCount()).isEqualTo(1);
        assertThat(controller.refreshFestivals().getBody().processedCount()).isEqualTo(2);
        assertThat(controller.refreshAccommodations().getBody().processedCount()).isEqualTo(4);
    }

    @Test
    void detailEndpointReturnsOkWhenOneSpotIsUpdated() {
        when(detailRefresher.refresh(3L)).thenReturn(TourDetailRefreshResult.updated());

        ResponseEntity<TourDataResult> response = controller.refreshCommonDetail(3L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().processedCount()).isEqualTo(1);
        verify(detailRefresher).refresh(3L);
    }

    @Test
    void detailEndpointReturnsNotFoundWhenSpotDoesNotExist() {
        when(detailRefresher.refresh(999L)).thenReturn(TourDetailRefreshResult.notFound());

        ResponseEntity<TourDataResult> response = controller.refreshCommonDetail(999L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().failureMessage()).contains("999");
    }

    @Test
    void detailEndpointReturnsBadGatewayWhenTourApiFails() {
        when(detailRefresher.refresh(3L))
                .thenReturn(TourDetailRefreshResult.failed("Read timed out"));

        ResponseEntity<TourDataResult> response = controller.refreshCommonDetail(3L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(response.getBody().failureMessage()).isEqualTo("Read timed out");
    }

    @Test
    void responseReturnsOkForSuccess() {
        when(runner.run(TourDataJobType.TOUR_SPOTS, "manual-api")).thenReturn(TourDataResult.success(3));

        ResponseEntity<TourDataResult> response = controller.refreshTourSpots();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
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
