package com.nigahae.tourdata.scheduler;

import com.nigahae.tourdata.domain.TourDataJobType;
import com.nigahae.tourdata.service.TourDataResult;
import com.nigahae.tourdata.service.TourDataRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TourDataScheduler {

    private final TourDataRunner tourDataRunner;

    @Scheduled(cron = "${tour-data.schedule.tour-spots-cron}", zone = "Asia/Seoul")
    public void refreshTourSpots() {
        run(TourDataJobType.TOUR_SPOTS);
    }

    @Scheduled(cron = "${tour-data.schedule.festivals-cron}", zone = "Asia/Seoul")
    public void refreshFestivals() {
        run(TourDataJobType.FESTIVALS);
    }

    @Scheduled(cron = "${tour-data.schedule.common-details-cron}", zone = "Asia/Seoul")
    public void refreshCommonDetails() {
        run(TourDataJobType.COMMON_DETAILS);
    }

    @Scheduled(cron = "${tour-data.schedule.accommodations-cron}", zone = "Asia/Seoul")
    public void refreshAccommodations() {
        run(TourDataJobType.ACCOMMODATIONS);
    }

    private void run(TourDataJobType jobType) {
        TourDataResult result = tourDataRunner.run(jobType, "schedule");
        if (!result.success() && !result.skipped()) {
            log.warn("Scheduled tour data job completed with failures. jobType={}, processedCount={}, failedCount={}",
                    jobType, result.processedCount(), result.failedCount());
        }
    }
}
