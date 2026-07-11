package com.nigahae.tourdata.scheduler;

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

    private final TourDataRunner tourIngestRunner;

    @Scheduled(cron = "${tour-data.schedule.cron}", zone = "Asia/Seoul")
    public void refreshTourContents() {
        TourDataResult result = tourIngestRunner.run("schedule");
        if (!result.success() && !result.skipped()) {
            log.warn("Tour ingest finished with failure. message={}", result.failureMessage());
        }
    }
}
