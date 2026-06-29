package com.nigahae.tourdata.controller;

import com.nigahae.tourdata.service.TourDataResult;
import com.nigahae.tourdata.service.TourDataRunner;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TourIngestControllerTest {

    @Test
    void refreshReturnsOkWhenIngestSucceeds() {
        TourDataRunner runner = mock(TourDataRunner.class);
        when(runner.run("manual-api")).thenReturn(TourDataResult.success(3));
        TourDataController controller = new TourDataController(runner);

        ResponseEntity<TourDataResult> response = controller.refresh();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(TourDataResult.success(3));
    }

    @Test
    void refreshReturnsConflictWhenIngestIsAlreadyRunning() {
        TourDataRunner runner = mock(TourDataRunner.class);
        TourDataResult skipped = TourDataResult.skipped("Tour ingest is already running");
        when(runner.run("manual-api")).thenReturn(skipped);
        TourDataController controller = new TourDataController(runner);

        ResponseEntity<TourDataResult> response = controller.refresh();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isEqualTo(skipped);
    }
}
