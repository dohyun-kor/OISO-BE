package com.nigahae.tourdata.service;

import com.nigahae.tourdata.client.PublicTourDataClient;
import com.nigahae.tourdata.client.TourContentPayload;
import com.nigahae.tourdata.domain.TourDataExecution;
import com.nigahae.tourdata.domain.TourSpot;
import com.nigahae.tourdata.repository.TourDataExecutionRepository;
import com.nigahae.tourdata.repository.TourSpotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TourDataService {

    private final PublicTourDataClient publicTourDataClient;
    private final TourSpotRepository tourSpotRepository;
    private final TourDataExecutionRepository executionRepository;

    public TourDataResult refreshAll() {
        TourDataExecution execution = executionRepository.save(TourDataExecution.started(LocalDateTime.now()));

        try {
            List<TourContentPayload> payloads = publicTourDataClient.fetchAllTourContents();

            for (TourContentPayload payload : payloads) {
                Long contentId = payload.contentIdAsLong();
                TourSpot tourSpot = tourSpotRepository.findByContentId(contentId)
                        .orElseGet(() -> new TourSpot(contentId));
                tourSpot.updateFrom(payload);
                tourSpotRepository.save(tourSpot);
            }

            execution.succeed(payloads.size(), LocalDateTime.now());
            executionRepository.save(execution);
            log.info("Tour ingest succeeded. processedCount={}", payloads.size());
            return TourDataResult.success(payloads.size());
        } catch (RuntimeException e) {
            execution.fail(e.getMessage(), LocalDateTime.now());
            executionRepository.save(execution);
            log.warn("Tour ingest failed. message={}", e.getMessage(), e);
            return TourDataResult.failure(e.getMessage());
        }
    }
}
