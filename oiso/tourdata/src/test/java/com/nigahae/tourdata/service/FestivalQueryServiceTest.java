package com.nigahae.tourdata.service;

import com.nigahae.tourdata.client.FestivalPayload;
import com.nigahae.tourdata.client.TourContentPayload;
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

import static org.assertj.core.api.Assertions.assertThat;
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

    private FestivalInfo festival(Long spotId, LocalDate startDate, LocalDate endDate) {
        FestivalInfo festival = new FestivalInfo(spotId);
        festival.updateFromList(new FestivalPayload(
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
        ));
        return festival;
    }
}
