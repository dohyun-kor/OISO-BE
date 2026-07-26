package com.nigahae.tourdata.service;

import com.nigahae.tourdata.domain.TourDataJobType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class TourDataRunner {

    private final TourDataService tourDataService;
    private final Map<TourDataJobType, AtomicBoolean> runningByJob = new EnumMap<>(TourDataJobType.class);

    public TourDataRunner(TourDataService tourDataService) {
        this.tourDataService = tourDataService;
        for (TourDataJobType jobType : TourDataJobType.values()) {
            runningByJob.put(jobType, new AtomicBoolean(false));
        }
    }

    public TourDataResult run(TourDataJobType jobType, String trigger) {
        if (jobType == TourDataJobType.LEGACY_FULL_REFRESH) {
            throw new IllegalArgumentException("LEGACY_FULL_REFRESH cannot be executed");
        }

        AtomicBoolean running = runningByJob.get(jobType);
        if (!running.compareAndSet(false, true)) {
            log.info("Tour data job skipped because the same job is still running. jobType={}, trigger={}",
                    jobType, trigger);
            return TourDataResult.skipped(jobType + " is already running");
        }

        try {
            return switch (jobType) {
                case TOUR_SPOTS -> tourDataService.refreshTourSpots();
                case FESTIVALS -> tourDataService.refreshFestivals();
                case COMMON_DETAILS -> tourDataService.refreshCommonDetails();
                case ACCOMMODATIONS -> tourDataService.refreshAccommodations();
                case LEGACY_FULL_REFRESH -> throw new IllegalStateException("Unsupported legacy job");
            };
        } finally {
            running.set(false);
        }
    }
}
