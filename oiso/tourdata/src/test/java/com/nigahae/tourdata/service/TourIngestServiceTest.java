package com.nigahae.tourdata.service;

import com.nigahae.tourdata.client.AccommodationRoomPayload;
import com.nigahae.tourdata.client.FestivalPayload;
import com.nigahae.tourdata.client.PublicTourDataClient;
import com.nigahae.tourdata.client.TourApiException;
import com.nigahae.tourdata.client.TourContentPayload;
import com.nigahae.tourdata.domain.AccommodationDetail;
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
import java.util.ArrayList;
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
    AccommodationDetailStore accommodationDetailStore;

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
        existing.updateFromDetail(payload("300", "15", "Old overview", "https://old.example.com"));
        FestivalPayload festival = festival("300", LocalDate.of(2026, 8, 15), LocalDate.of(2026, 8, 23));
        when(publicTourDataClient.fetchAllFestivals(any(LocalDate.class))).thenReturn(List.of(festival));
        when(festivalInfoRepository.findByContentId(300L)).thenReturn(Optional.of(existing));

        TourDataResult result = tourDataService.refreshFestivals();

        assertThat(result).isEqualTo(TourDataResult.success(1));
        assertThat(existing.getOverview()).isEqualTo("Old overview");
        assertThat(existing.getEventStartDate()).isEqualTo(LocalDate.of(2026, 8, 15));
        verify(festivalInfoRepository).save(existing);
        verify(publicTourDataClient, never()).fetchTourContentDetail(any());
        assertExecution(TourDataJobType.FESTIVALS, TourDataStatus.SUCCESS, 1, 0);
    }

    @Test
    void refreshCommonDetailsDeduplicatesContentIdAndUpdatesBothTables() {
        TourSpot tourSpot = tourSpot("300", "15");
        FestivalInfo festivalInfo = festivalInfo("300");
        when(tourSpotRepository.findAll()).thenReturn(List.of(tourSpot));
        when(festivalInfoRepository.findAll()).thenReturn(List.of(festivalInfo));
        when(publicTourDataClient.fetchTourContentDetail(300L))
                .thenReturn(payload("300", "15", "Shared overview", "https://detail.example.com"));

        TourDataResult result = tourDataService.refreshCommonDetails();

        assertThat(result).isEqualTo(TourDataResult.success(1));
        assertThat(tourSpot.getOverview()).isEqualTo("Shared overview");
        assertThat(festivalInfo.getOverview()).isEqualTo("Shared overview");
        verify(publicTourDataClient, times(1)).fetchTourContentDetail(300L);
        verify(tourSpotRepository).save(tourSpot);
        verify(festivalInfoRepository).save(festivalInfo);
    }

    @Test
    void refreshCommonDetailsContinuesAfterTimeoutAndMarksPartialSuccess() {
        TourSpot first = tourSpot("100", "12");
        TourSpot second = tourSpot("200", "12");
        when(tourSpotRepository.findAll()).thenReturn(List.of(first, second));
        when(festivalInfoRepository.findAll()).thenReturn(List.of());
        when(publicTourDataClient.fetchTourContentDetail(100L))
                .thenReturn(payload("100", "12", "Overview", null));
        when(publicTourDataClient.fetchTourContentDetail(200L))
                .thenThrow(new TourApiException("Read timed out"));

        TourDataResult result = tourDataService.refreshCommonDetails();

        assertThat(result.partial()).isTrue();
        assertThat(result.processedCount()).isEqualTo(1);
        assertThat(result.failedCount()).isEqualTo(1);
        assertThat(result.failureMessage()).contains("200").contains("Read timed out");
        verify(tourSpotRepository).save(first);
        verify(tourSpotRepository, never()).save(second);
        assertExecution(TourDataJobType.COMMON_DETAILS, TourDataStatus.PARTIAL_SUCCESS, 1, 1);
    }

    @Test
    void refreshCommonDetailsReturnsFailureWhenEveryItemFails() {
        TourSpot tourSpot = tourSpot("100", "12");
        when(tourSpotRepository.findAll()).thenReturn(List.of(tourSpot));
        when(festivalInfoRepository.findAll()).thenReturn(List.of());
        when(publicTourDataClient.fetchTourContentDetail(100L))
                .thenThrow(new TourApiException("Read timed out"));

        TourDataResult result = tourDataService.refreshCommonDetails();

        assertThat(result.success()).isFalse();
        assertThat(result.partial()).isFalse();
        assertThat(result.processedCount()).isZero();
        assertThat(result.failedCount()).isEqualTo(1);
        assertExecution(TourDataJobType.COMMON_DETAILS, TourDataStatus.FAILED, 0, 1);
    }

    @Test
    void refreshCommonDetailsLimitsRecordedFailuresToFirstTwentyItems() {
        List<TourSpot> tourSpots = new ArrayList<>();
        for (long contentId = 1; contentId <= 21; contentId++) {
            tourSpots.add(tourSpot(String.valueOf(contentId), "12"));
        }
        when(tourSpotRepository.findAll()).thenReturn(tourSpots);
        when(festivalInfoRepository.findAll()).thenReturn(List.of());
        when(publicTourDataClient.fetchTourContentDetail(any()))
                .thenThrow(new TourApiException("Read timed out"));

        TourDataResult result = tourDataService.refreshCommonDetails();

        assertThat(result.failedCount()).isEqualTo(21);
        assertThat(result.failureMessage()).contains("20 (Read timed out)").contains("and 1 more");
        assertThat(result.failureMessage()).doesNotContain("21 (Read timed out)");
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
        ArgumentCaptor<List<AccommodationDetail>> detailsCaptor = ArgumentCaptor.forClass(List.class);
        verify(accommodationDetailStore).replace(org.mockito.ArgumentMatchers.eq(200L), detailsCaptor.capture());
        assertThat(detailsCaptor.getValue()).hasSize(2);
        assertThat(detailsCaptor.getValue().get(0).getTempRoomId()).isEqualTo(1L);
        verify(accommodationDetailStore, never()).replace(org.mockito.ArgumentMatchers.eq(201L), any());
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
        TourSpot tourSpot = new TourSpot(Long.valueOf(contentId));
        tourSpot.updateFromList(payload(contentId, contentTypeId));
        return tourSpot;
    }

    private FestivalInfo festivalInfo(String contentId) {
        FestivalInfo festivalInfo = new FestivalInfo(Long.valueOf(contentId));
        festivalInfo.updateFromList(festival(contentId, null, null));
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
