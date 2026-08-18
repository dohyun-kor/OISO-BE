package com.nigahae.tourdata.service;

import com.nigahae.tourdata.TourDetailRefreshResult;
import com.nigahae.tourdata.client.AccommodationRoomPayload;
import com.nigahae.tourdata.client.FestivalPayload;
import com.nigahae.tourdata.client.PublicTourDataClient;
import com.nigahae.tourdata.client.TourApiException;
import com.nigahae.tourdata.client.TourContentPayload;
import com.nigahae.tourdata.domain.AccommodationRoom;
import com.nigahae.tourdata.domain.FestivalInfo;
import com.nigahae.tourdata.domain.TourDataExecution;
import com.nigahae.tourdata.domain.TourDataJobType;
import com.nigahae.tourdata.domain.TourDataStatus;
import com.nigahae.tourdata.domain.TourSpot;
import com.nigahae.tourdata.repository.FestivalInfoRepository;
import com.nigahae.tourdata.repository.TourDataExecutionRepository;
import com.nigahae.tourdata.repository.TourSpotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TourIngestServiceTest {

    @Mock
    PublicTourDataClient publicTourDataClient;

    @Mock
    TourSpotRepository tourSpotRepository;

    @Mock
    FestivalInfoRepository festivalInfoRepository;

    @Mock
    AccommodationRoomStore accommodationRoomStore;

    @Mock
    TourDataExecutionRepository executionRepository;

    @InjectMocks
    TourDataService tourDataService;

    @BeforeEach
    void setUpExecutionRepository() {
        when(executionRepository.save(any(TourDataExecution.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void refreshTourSpotsOnlyUpdatesListFieldsAndPreservesDetailFields() {
        TourSpot existing = tourSpot("100", "12");
        existing.updateFromDetail(payload("100", "12", "Old overview", "https://old.example.com"));
        when(publicTourDataClient.fetchAllTourContents()).thenReturn(List.of(payload("100", "12")));
        when(tourSpotRepository.findByContentId(100L)).thenReturn(Optional.of(existing));

        TourDataResult result = tourDataService.refreshTourSpots();

        assertThat(result).isEqualTo(TourDataResult.success(1));
        assertThat(existing.getOverview()).isEqualTo("Old overview");
        assertThat(existing.getHomepage()).isEqualTo("https://old.example.com");
        verify(tourSpotRepository).save(existing);
        verify(publicTourDataClient, never()).fetchTourContentDetail(any());
        assertExecution(TourDataJobType.TOUR_SPOTS, TourDataStatus.SUCCESS, 1, 0);
    }

    @Test
    void refreshFestivalsOnlyUpdatesFestivalListAndPreservesDetailFields() {
        FestivalInfo existing = festivalInfo("300");
        existing.getTourSpot().updateFromDetail(payload("300", "15", "Old overview", "https://old.example.com"));
        FestivalPayload festival = festival("300", LocalDate.of(2026, 8, 15), LocalDate.of(2026, 8, 23));
        when(publicTourDataClient.fetchAllFestivals(any(LocalDate.class))).thenReturn(List.of(festival));
        when(tourSpotRepository.findByContentId(300L)).thenReturn(Optional.of(existing.getTourSpot()));
        when(tourSpotRepository.save(existing.getTourSpot())).thenReturn(existing.getTourSpot());
        when(festivalInfoRepository.findById(300L)).thenReturn(Optional.of(existing));

        TourDataResult result = tourDataService.refreshFestivals();

        assertThat(result).isEqualTo(TourDataResult.success(1));
        assertThat(existing.getOverview()).isEqualTo("Old overview");
        assertThat(existing.getEventStartDate()).isEqualTo(LocalDate.of(2026, 8, 15));
        verify(festivalInfoRepository).save(existing);
        verify(publicTourDataClient, never()).fetchTourContentDetail(any());
        assertExecution(TourDataJobType.FESTIVALS, TourDataStatus.SUCCESS, 1, 0);
    }

    @Test
    void refreshTourDetailUpdatesOnlyRequestedTourSpot() {
        TourSpot tourSpot = tourSpot("300", "15");
        when(tourSpotRepository.findById(300L)).thenReturn(Optional.of(tourSpot));
        when(publicTourDataClient.fetchTourContentDetail(300L))
                .thenReturn(payload("300", "15", "Shared overview", "https://detail.example.com"));

        TourDetailRefreshResult result = tourDataService.refresh(300L);

        assertThat(result.updatedSuccessfully()).isTrue();
        assertThat(tourSpot.getOverview()).isEqualTo("Shared overview");
        assertThat(tourSpot.getHomepage()).isEqualTo("https://detail.example.com");
        verify(publicTourDataClient, times(1)).fetchTourContentDetail(300L);
        verify(tourSpotRepository).save(tourSpot);
        verify(executionRepository, never()).save(any());
    }

    @Test
    void refreshTourDetailReturnsFailureWithoutSavingWhenTourApiFails() {
        TourSpot tourSpot = tourSpot("200", "12");
        tourSpot.updateFromDetail(payload("200", "12", "Stored overview", null));
        when(tourSpotRepository.findById(200L)).thenReturn(Optional.of(tourSpot));
        when(publicTourDataClient.fetchTourContentDetail(200L))
                .thenThrow(new TourApiException("Read timed out"));

        TourDetailRefreshResult result = tourDataService.refresh(200L);

        assertThat(result.failed()).isTrue();
        assertThat(result.failureMessage()).contains("Read timed out");
        assertThat(tourSpot.getOverview()).isEqualTo("Stored overview");
        verify(tourSpotRepository, never()).save(any());
        verify(executionRepository, never()).save(any());
    }

    @Test
    void refreshTourDetailDoesNotCallTourApiWhenSpotDoesNotExist() {
        when(tourSpotRepository.findById(999L)).thenReturn(Optional.empty());

        TourDetailRefreshResult result = tourDataService.refresh(999L);

        assertThat(result.targetNotFound()).isTrue();
        verify(publicTourDataClient, never()).fetchTourContentDetail(any());
        verify(tourSpotRepository, never()).save(any());
        verify(executionRepository, never()).save(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void refreshAccommodationsReplacesSuccessfulRoomsAndKeepsGoingAfterFailure() {
        TourSpot successful = tourSpot("200", "32");
        TourSpot failed = tourSpot("201", "32");
        when(tourSpotRepository.findAllByContentTypeId(32L)).thenReturn(List.of(successful, failed));
        when(publicTourDataClient.fetchAccommodationRooms(200L, 32L))
                .thenReturn(List.of(room("A", "Room A"), room("B", "Room B")));
        when(publicTourDataClient.fetchAccommodationRooms(201L, 32L))
                .thenThrow(new TourApiException("Read timed out"));

        TourDataResult result = tourDataService.refreshAccommodations();

        assertThat(result.partial()).isTrue();
        assertThat(result.processedCount()).isEqualTo(1);
        assertThat(result.failedCount()).isEqualTo(1);
        ArgumentCaptor<List<AccommodationRoom>> roomsCaptor = ArgumentCaptor.forClass(List.class);
        verify(accommodationRoomStore).replace(org.mockito.ArgumentMatchers.eq(200L), roomsCaptor.capture());
        assertThat(roomsCaptor.getValue()).hasSize(2);
        assertThat(roomsCaptor.getValue().get(0).getRoomOrder()).isEqualTo(1);
        verify(accommodationRoomStore, never()).replace(org.mockito.ArgumentMatchers.eq(201L), any());
        assertExecution(TourDataJobType.ACCOMMODATIONS, TourDataStatus.PARTIAL_SUCCESS, 1, 1);
    }

    @Test
    void refreshTourSpotsFailsImmediatelyWhenListApiFails() {
        when(publicTourDataClient.fetchAllTourContents()).thenThrow(new TourApiException("list boom"));

        TourDataResult result = tourDataService.refreshTourSpots();

        assertThat(result).isEqualTo(TourDataResult.failure(1, "list boom"));
        verify(tourSpotRepository, never()).save(any());
        assertExecution(TourDataJobType.TOUR_SPOTS, TourDataStatus.FAILED, 0, 1);
    }

    private void assertExecution(
            TourDataJobType jobType,
            TourDataStatus status,
            int processedCount,
            int failedCount
    ) {
        ArgumentCaptor<TourDataExecution> captor = ArgumentCaptor.forClass(TourDataExecution.class);
        verify(executionRepository, times(2)).save(captor.capture());
        TourDataExecution execution = captor.getValue();
        assertThat(execution.getJobType()).isEqualTo(jobType);
        assertThat(execution.getStatus()).isEqualTo(status);
        assertThat(execution.getProcessedCount()).isEqualTo(processedCount);
        assertThat(execution.getFailedCount()).isEqualTo(failedCount);
    }

    private TourSpot tourSpot(String contentId, String contentTypeId) {
        TourSpot tourSpot = TourSpot.reference(Long.valueOf(contentId));
        tourSpot.updateFromList(payload(contentId, contentTypeId));
        return tourSpot;
    }

    private FestivalInfo festivalInfo(String contentId) {
        TourSpot tourSpot = tourSpot(contentId, "15");
        FestivalInfo festivalInfo = FestivalInfo.forSpot(tourSpot);
        festivalInfo.updateDates(festival(contentId, null, null));
        return festivalInfo;
    }

    private TourContentPayload payload(String contentId, String contentTypeId) {
        return payload(contentId, contentTypeId, null, null);
    }

    private TourContentPayload payload(String contentId, String contentTypeId, String overview, String homepage) {
        return new TourContentPayload(
                contentId, contentTypeId, "Place " + contentId, "1", "2", "Address",
                "127", "37", null, overview, homepage, null, "{}"
        );
    }

    private FestivalPayload festival(String contentId, LocalDate eventStartDate, LocalDate eventEndDate) {
        return new FestivalPayload(payload(contentId, "15"), eventStartDate, eventEndDate);
    }

    private AccommodationRoomPayload room(String roomCode, String roomTitle) {
        return new AccommodationRoomPayload(
                roomCode, roomTitle, "10", "33", "2", "2", "4",
                "60000", "80000", "90000", "100000", "Intro",
                "Y", null, null, "Y", "Y", null, null, "Y", "Y", "Y",
                null, null, "Y", "Y"
        );
    }
}
