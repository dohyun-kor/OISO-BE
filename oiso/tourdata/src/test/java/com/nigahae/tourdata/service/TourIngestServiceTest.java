package com.nigahae.tourdata.service;

import com.nigahae.tourdata.client.AccommodationRoomPayload;
import com.nigahae.tourdata.client.PublicTourDataClient;
import com.nigahae.tourdata.client.TourApiException;
import com.nigahae.tourdata.client.TourContentPayload;
import com.nigahae.tourdata.domain.AccommodationDetail;
import com.nigahae.tourdata.domain.TourDataExecution;
import com.nigahae.tourdata.domain.TourDataStatus;
import com.nigahae.tourdata.domain.TourSpot;
import com.nigahae.tourdata.repository.AccommodationDetailRepository;
import com.nigahae.tourdata.repository.TourDataExecutionRepository;
import com.nigahae.tourdata.repository.TourSpotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TourIngestServiceTest {

    @Mock
    PublicTourDataClient publicTourDataClient;

    @Mock
    TourSpotRepository tourSpotRepository;

    @Mock
    AccommodationDetailRepository accommodationDetailRepository;

    @Mock
    TourDataExecutionRepository executionRepository;

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
    @SuppressWarnings("unchecked")
    void refreshAllReplacesAccommodationRoomsWhenContentTypeIs32() {
        TourDataExecution execution = execution();
        when(executionRepository.save(any(TourDataExecution.class))).thenReturn(execution);
        when(publicTourDataClient.fetchAllTourContents()).thenReturn(List.of(payload("200", "32")));
        when(tourSpotRepository.findByContentId(200L)).thenReturn(Optional.empty());
        when(publicTourDataClient.fetchAccommodationRooms(200L, 32L)).thenReturn(List.of(
                room("A", "Room A"),
                room("B", "Room B")
        ));

        tourIngestService.refreshAll();

        verify(accommodationDetailRepository).deleteBySpotId(200L);
        ArgumentCaptor<Iterable<AccommodationDetail>> captor = ArgumentCaptor.forClass(Iterable.class);
        verify(accommodationDetailRepository).saveAll(captor.capture());

        List<AccommodationDetail> savedRooms = StreamSupport.stream(captor.getValue().spliterator(), false).toList();
        assertThat(savedRooms).hasSize(2);
        assertThat(savedRooms.get(0).getTempRoomId()).isEqualTo(1L);
        assertThat(savedRooms.get(0).getSpotId()).isEqualTo(200L);
        assertThat(savedRooms.get(0).getRoomCode()).isEqualTo("A");
        assertThat(savedRooms.get(0).getRoomTitle()).isEqualTo("Room A");
        assertThat(savedRooms.get(1).getTempRoomId()).isEqualTo(2L);
    }

    @Test
    void refreshAllDoesNotFetchAccommodationRoomsWhenContentTypeIsNot32() {
        TourDataExecution execution = execution();
        when(executionRepository.save(any(TourDataExecution.class))).thenReturn(execution);
        when(publicTourDataClient.fetchAllTourContents()).thenReturn(List.of(payload("100", "12")));
        when(tourSpotRepository.findByContentId(100L)).thenReturn(Optional.empty());

        tourIngestService.refreshAll();

        verify(publicTourDataClient, never()).fetchAccommodationRooms(anyLong(), anyLong());
        verify(accommodationDetailRepository, never()).deleteBySpotId(anyLong());
    }

    @Test
    void refreshAllMarksFailureWhenAccommodationRoomFetchFails() {
        TourDataExecution execution = execution();
        when(executionRepository.save(any(TourDataExecution.class))).thenReturn(execution);
        when(publicTourDataClient.fetchAllTourContents()).thenReturn(List.of(payload("200", "32")));
        when(tourSpotRepository.findByContentId(200L)).thenReturn(Optional.empty());
        when(publicTourDataClient.fetchAccommodationRooms(200L, 32L)).thenThrow(new TourApiException("detail boom"));

        TourDataResult result = tourIngestService.refreshAll();

        assertThat(result.success()).isFalse();
        assertThat(result.failureMessage()).contains("detail boom");
        assertThat(execution.getStatus()).isEqualTo(TourDataStatus.FAILED);
        verify(accommodationDetailRepository, never()).deleteBySpotId(anyLong());
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
        return payload(contentId, "12");
    }

    private TourContentPayload payload(String contentId, String contentTypeId) {
        return new TourContentPayload(contentId, contentTypeId, "Place", "1", "2", "Address", "127", "37", null, null, null, "{}");
    }

    private AccommodationRoomPayload room(String roomCode, String roomTitle) {
        return new AccommodationRoomPayload(
                roomCode,
                roomTitle,
                "10",
                "33",
                "2",
                "2",
                "4",
                "60000",
                "80000",
                "90000",
                "100000",
                "Intro",
                "Y",
                null,
                null,
                "Y",
                "Y",
                null,
                null,
                "Y",
                "Y",
                "Y",
                null,
                null,
                "Y",
                "Y"
        );
    }
}
