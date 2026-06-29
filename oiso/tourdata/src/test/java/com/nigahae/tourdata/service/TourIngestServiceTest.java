package com.nigahae.tourdata.service;

import com.nigahae.tourdata.client.PublicTourDataClient;
import com.nigahae.tourdata.client.TourApiException;
import com.nigahae.tourdata.client.TourContentPayload;
import com.nigahae.tourdata.domain.TourDataExecution;
import com.nigahae.tourdata.domain.TourDataStatus;
import com.nigahae.tourdata.domain.TourSpot;
import com.nigahae.tourdata.repository.TourIngestExecutionRepository;
import com.nigahae.tourdata.repository.TourSpotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TourIngestServiceTest {

    @Mock
    PublicTourDataClient publicTourDataClient;

    @Mock
    TourSpotRepository tourSpotRepository;

    @Mock
    TourIngestExecutionRepository executionRepository;

    @InjectMocks
    TourDataService tourIngestService;

    @Test
    void refreshAllUpsertsFetchedContentsAndMarksSuccess() {
        TourDataExecution execution = execution();
        when(executionRepository.save(any(TourDataExecution.class))).thenReturn(execution);
        when(publicTourDataClient.fetchAllTourContents()).thenReturn(List.of(payload("100")));
        when(tourSpotRepository.findByContentId(100L)).thenReturn(Optional.empty());

        TourDataResult result = tourIngestService.refreshAll();

        assertThat(result.success()).isTrue();
        assertThat(result.processedCount()).isEqualTo(1);
        assertThat(execution.getStatus()).isEqualTo(TourDataStatus.SUCCESS);
        assertThat(execution.getProcessedCount()).isEqualTo(1);
        verify(tourSpotRepository).save(any(TourSpot.class));
    }

    @Test
    void refreshAllUpdatesExistingContent() {
        TourDataExecution execution = execution();
        TourSpot existing = new TourSpot(100L);
        when(executionRepository.save(any(TourDataExecution.class))).thenReturn(execution);
        when(publicTourDataClient.fetchAllTourContents()).thenReturn(List.of(payload("100")));
        when(tourSpotRepository.findByContentId(100L)).thenReturn(Optional.of(existing));

        tourIngestService.refreshAll();

        ArgumentCaptor<TourSpot> captor = ArgumentCaptor.forClass(TourSpot.class);
        verify(tourSpotRepository).save(captor.capture());
        assertThat(captor.getValue()).isSameAs(existing);
    }

    @Test
    void refreshAllMarksFailureWhenClientFails() {
        TourDataExecution execution = execution();
        when(executionRepository.save(any(TourDataExecution.class))).thenReturn(execution);
        when(publicTourDataClient.fetchAllTourContents()).thenThrow(new TourApiException("boom"));

        TourDataResult result = tourIngestService.refreshAll();

        assertThat(result.success()).isFalse();
        assertThat(result.failureMessage()).contains("boom");
        assertThat(execution.getStatus()).isEqualTo(TourDataStatus.FAILED);
    }

    private TourDataExecution execution() {
        return TourDataExecution.started(java.time.LocalDateTime.now());
    }

    private TourContentPayload payload(String contentId) {
        return new TourContentPayload(contentId, "12", "Place", "1", "2", "Address", "127", "37", null, null, null, "{}");
    }
}
