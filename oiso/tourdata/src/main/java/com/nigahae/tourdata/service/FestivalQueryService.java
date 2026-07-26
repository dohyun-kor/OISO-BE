package com.nigahae.tourdata.service;

import com.nigahae.tourdata.controller.dto.FestivalResponse;
import com.nigahae.tourdata.domain.FestivalInfo;
import com.nigahae.tourdata.repository.FestivalInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class FestivalQueryService {

    private static final ZoneId GYEONGJU_TIME_ZONE = ZoneId.of("Asia/Seoul");
    private static final Comparator<FestivalResponse> FESTIVAL_ORDER = Comparator
            .comparing(FestivalResponse::status)
            .thenComparing(FestivalResponse::eventStartDate)
            .thenComparing(FestivalResponse::spotId);

    private final FestivalInfoRepository festivalInfoRepository;
    private final Clock clock;

    @Autowired
    public FestivalQueryService(FestivalInfoRepository festivalInfoRepository) {
        this(festivalInfoRepository, Clock.system(GYEONGJU_TIME_ZONE));
    }

    FestivalQueryService(FestivalInfoRepository festivalInfoRepository, Clock clock) {
        this.festivalInfoRepository = festivalInfoRepository;
        this.clock = clock;
    }

    public List<FestivalResponse> getAvailableFestivals() {
        LocalDate today = LocalDate.now(clock);

        return festivalInfoRepository.findAllByEventEndDateGreaterThanEqual(today).stream()
                .filter(festival -> hasAvailableEventPeriod(festival, today))
                .map(festival -> FestivalResponse.from(festival, today))
                .sorted(FESTIVAL_ORDER)
                .toList();
    }

    private boolean hasAvailableEventPeriod(FestivalInfo festival, LocalDate today) {
        LocalDate startDate = festival.getEventStartDate();
        LocalDate endDate = festival.getEventEndDate();
        return startDate != null
                && endDate != null
                && !startDate.isAfter(endDate)
                && !endDate.isBefore(today);
    }
}
