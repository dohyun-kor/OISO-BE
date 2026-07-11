package com.nigahae.tourdata.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class TourDataRunner {

    private final TourDataService tourIngestService;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public TourDataResult run(String trigger) {
        if (!running.compareAndSet(false, true)) {
            log.info("Tour ingest skipped because previous execution is still running. trigger={}", trigger);
            return TourDataResult.skipped("Tour ingest is already running");
        }

        try {
            return tourIngestService.refreshAll();
        } finally {
            running.set(false);
        }
    }
}
