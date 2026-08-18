package com.nigahae.tourdata.service;

import com.nigahae.common.constant.ErrorCode;
import com.nigahae.common.exception.CustomException;
import com.nigahae.tourdata.client.FestivalPayload;
import com.nigahae.tourdata.client.TourContentPayload;
import com.nigahae.tourdata.controller.dto.FestivalDetailResponse;
import com.nigahae.tourdata.controller.dto.FestivalResponse;
import com.nigahae.tourdata.controller.dto.FestivalStatus;
import com.nigahae.tourdata.domain.FestivalInfo;
import com.nigahae.tourdata.repository.FestivalInfoRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FestivalQueryServiceTest {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");
    private static final LocalDate TODAY = LocalDate.of(2026, 7, 26);
    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-07-25T15:00:00Z"),
            SEOUL_ZONE
    );

    private final FestivalInfoRepository festivalInfoRepository = mock(FestivalInfoRepository.class);
    private final FestivalQueryService festivalQueryService =
            new FestivalQueryService(festivalInfoRepository, FIXED_CLOCK);

    @Test
    void returnsOngoingAndUpcomingFestivalsWithBoundaryStatus() {
        FestivalInfo startsToday = festival(2L, TODAY, TODAY.plusDays(2));
        FestivalInfo endsToday = festival(1L, TODAY.minusDays(10), TODAY);
        FestivalInfo upcoming = festival(3L, TODAY.plusDays(1), TODAY.plusDays(3));
        when(festivalInfoRepository.findAllByEventEndDateGreaterThanEqual(TODAY))
                .thenReturn(List.of(upcoming, startsToday, endsToday));

        List<FestivalResponse> result = festivalQueryService.getAvailableFestivals();

        assertThat(result)
                .extracting(FestivalResponse::spotId, FestivalResponse::status)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(1L, FestivalStatus.ONGOING),
                        org.assertj.core.groups.Tuple.tuple(2L, FestivalStatus.ONGOING),
                        org.assertj.core.groups.Tuple.tuple(3L, FestivalStatus.UPCOMING)
                );
        verify(festivalInfoRepository).findAllByEventEndDateGreaterThanEqual(TODAY);
    }

    @Test
    void excludesEndedMissingAndReversedEventPeriods() {
        FestivalInfo ended = festival(5L, TODAY.minusDays(2), TODAY.minusDays(1));
        FestivalInfo missingStart = festival(1L, null, TODAY.plusDays(1));
        FestivalInfo missingEnd = festival(2L, TODAY, null);
        FestivalInfo reversed = festival(3L, TODAY.plusDays(2), TODAY.plusDays(1));
        FestivalInfo valid = festival(4L, TODAY.plusDays(1), TODAY.plusDays(2));
        when(festivalInfoRepository.findAllByEventEndDateGreaterThanEqual(TODAY))
                .thenReturn(List.of(ended, missingStart, missingEnd, reversed, valid));

        assertThat(festivalQueryService.getAvailableFestivals())
                .extracting(FestivalResponse::spotId)
                .containsExactly(4L);
    }

    @Test
    void sortsByStatusThenStartDateThenSpotId() {
        FestivalInfo upcomingLater = festival(5L, TODAY.plusDays(3), TODAY.plusDays(4));
        FestivalInfo ongoingLaterStart = festival(4L, TODAY.minusDays(1), TODAY.plusDays(1));
        FestivalInfo upcomingSameDateHigherId = festival(3L, TODAY.plusDays(2), TODAY.plusDays(3));
        FestivalInfo ongoingEarlierStart = festival(2L, TODAY.minusDays(2), TODAY.plusDays(2));
        FestivalInfo upcomingSameDateLowerId = festival(1L, TODAY.plusDays(2), TODAY.plusDays(3));
        when(festivalInfoRepository.findAllByEventEndDateGreaterThanEqual(TODAY))
                .thenReturn(List.of(
                        upcomingLater,
                        ongoingLaterStart,
                        upcomingSameDateHigherId,
                        ongoingEarlierStart,
                        upcomingSameDateLowerId
                ));

        assertThat(festivalQueryService.getAvailableFestivals())
                .extracting(FestivalResponse::spotId)
                .containsExactly(2L, 4L, 1L, 3L, 5L);
    }

    @Test
    void returnsEmptyListWhenNoFestivalIsAvailable() {
        when(festivalInfoRepository.findAllByEventEndDateGreaterThanEqual(TODAY))
                .thenReturn(List.of());

        assertThat(festivalQueryService.getAvailableFestivals()).isEmpty();
    }

    @Test
    void returnsFestivalDetailBySpotId() {
        FestivalInfo festival = festival(300L, LocalDate.of(2026, 6, 5), LocalDate.of(2026, 8, 28));
        festival.getTourSpot().updateFromDetail(new TourContentPayload(
                "300",
                "15",
                "2026 Festival",
                "1",
                "2",
                "Gyeongju detail address",
                "129.2123",
                "35.8451",
                "https://example.com/festival.jpg",
                "Festival overview",
                "https://festival.example.com",
                "054-000-0000",
                "{}"
        ));
        when(festivalInfoRepository.findById(300L)).thenReturn(Optional.of(festival));

        FestivalDetailResponse result = festivalQueryService.getFestivalDetail(300L);

        assertThat(result.spotId()).isEqualTo(300L);
        assertThat(result.name()).isEqualTo("2026 Festival");
        assertThat(result.img()).isEqualTo("https://example.com/festival.jpg");
        assertThat(result.address()).isEqualTo("Gyeongju detail address");
        assertThat(result.overview()).isEqualTo("Festival overview");
        assertThat(result.homepage()).isEqualTo("https://festival.example.com");
        assertThat(result.mapX()).isEqualTo(129.2123);
        assertThat(result.mapY()).isEqualTo(35.8451);
        assertThat(result.contentId()).isEqualTo(300L);
        assertThat(result.contentTypeId()).isEqualTo(15L);
        assertThat(result.tel()).isEqualTo("054-000-0000");
        assertThat(result.eventStartDate()).isEqualTo(LocalDate.of(2026, 6, 5));
        assertThat(result.eventEndDate()).isEqualTo(LocalDate.of(2026, 8, 28));
        verify(festivalInfoRepository).findById(300L);
    }

    @Test
    void throwsResourceNotFoundWhenFestivalDoesNotExist() {
        when(festivalInfoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> festivalQueryService.getFestivalDetail(999L))
                .isInstanceOfSatisfying(
                        CustomException.class,
                        exception -> assertThat(exception.getErrorCode())
                                .isEqualTo(ErrorCode.RESOURCE_NOT_FOUND)
                );
    }

    private FestivalInfo festival(Long spotId, LocalDate startDate, LocalDate endDate) {
        com.nigahae.tourdata.domain.TourSpot tourSpot =
                com.nigahae.tourdata.domain.TourSpot.reference(spotId);
        FestivalPayload payload = new FestivalPayload(
                new TourContentPayload(
                        spotId.toString(),
                        "15",
                        "Festival " + spotId,
                        "1",
                        "2",
                        "Gyeongju",
                        "129.2",
                        "35.8",
                        "https://example.com/" + spotId + ".jpg",
                        null,
                        null,
                        null,
                        "{}"
                ),
                startDate,
                endDate
        );
        tourSpot.updateFromList(payload.content());
        FestivalInfo festival = FestivalInfo.forSpot(tourSpot);
        festival.updateDates(payload);
        return festival;
    }
}
