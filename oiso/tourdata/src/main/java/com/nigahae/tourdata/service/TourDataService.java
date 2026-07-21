package com.nigahae.tourdata.service;

import com.nigahae.tourdata.client.FestivalPayload;
import com.nigahae.tourdata.client.PublicTourDataClient;
import com.nigahae.tourdata.client.TourContentPayload;
import com.nigahae.tourdata.domain.AccommodationDetail;
import com.nigahae.tourdata.domain.FestivalInfo;
import com.nigahae.tourdata.domain.TourDataExecution;
import com.nigahae.tourdata.domain.TourDataJobType;
import com.nigahae.tourdata.domain.TourSpot;
import com.nigahae.tourdata.repository.FestivalInfoRepository;
import com.nigahae.tourdata.repository.TourDataExecutionRepository;
import com.nigahae.tourdata.repository.TourSpotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class TourDataService {

    private static final Long ACCOMMODATION_CONTENT_TYPE_ID = 32L;
    private static final ZoneId GYEONGJU_TIME_ZONE = ZoneId.of("Asia/Seoul");
    private static final int MAX_RECORDED_FAILURES = 20;

    private final PublicTourDataClient publicTourDataClient;
    private final TourSpotRepository tourSpotRepository;
    private final FestivalInfoRepository festivalInfoRepository;
    private final AccommodationDetailStore accommodationDetailStore;
    private final TourDataExecutionRepository executionRepository;

    public TourDataResult refreshTourSpots() {
        return execute(TourDataJobType.TOUR_SPOTS, () -> {
            List<TourContentPayload> payloads = publicTourDataClient.fetchAllTourContents();
            for (TourContentPayload payload : payloads) {
                Long contentId = payload.contentIdAsLong();
                TourSpot tourSpot = tourSpotRepository.findByContentId(contentId)
                        .orElseGet(() -> new TourSpot(contentId));
                tourSpot.updateFromList(payload);
                tourSpotRepository.save(tourSpot);
            }
            return TourDataResult.success(payloads.size());
        });
    }

    public TourDataResult refreshFestivals() {
        return execute(TourDataJobType.FESTIVALS, () -> {
            LocalDate eventStartDate = LocalDate.now(GYEONGJU_TIME_ZONE);
            List<FestivalPayload> payloads = publicTourDataClient.fetchAllFestivals(eventStartDate);
            for (FestivalPayload payload : payloads) {
                Long contentId = payload.content().contentIdAsLong();
                FestivalInfo festivalInfo = festivalInfoRepository.findByContentId(contentId)
                        .orElseGet(() -> new FestivalInfo(contentId));
                festivalInfo.updateFromList(payload);
                festivalInfoRepository.save(festivalInfo);
            }
            return TourDataResult.success(payloads.size());
        });
    }

    public TourDataResult refreshCommonDetails() {
        return execute(TourDataJobType.COMMON_DETAILS, () -> {
            Map<Long, TourSpot> tourSpotsByContentId = indexTourSpots();
            Map<Long, FestivalInfo> festivalsByContentId = indexFestivals();
            Set<Long> contentIds = new TreeSet<>(tourSpotsByContentId.keySet());
            contentIds.addAll(festivalsByContentId.keySet());

            FailureCollector failures = new FailureCollector();
            int processedCount = 0;
            for (Long contentId : contentIds) {
                try {
                    TourContentPayload detail = publicTourDataClient.fetchTourContentDetail(contentId);
                    TourSpot tourSpot = tourSpotsByContentId.get(contentId);
                    if (tourSpot != null) {
                        tourSpot.updateFromDetail(detail);
                        tourSpotRepository.save(tourSpot);
                    }
                    FestivalInfo festivalInfo = festivalsByContentId.get(contentId);
                    if (festivalInfo != null) {
                        festivalInfo.updateFromDetail(detail);
                        festivalInfoRepository.save(festivalInfo);
                    }
                    processedCount++;
                } catch (RuntimeException e) {
                    failures.add(contentId, e);
                    log.warn("Common detail ingest failed. contentId={}, message={}", contentId, e.getMessage());
                }
            }
            return failures.toResult(processedCount);
        });
    }

    public TourDataResult refreshAccommodations() {
        return execute(TourDataJobType.ACCOMMODATIONS, () -> {
            List<TourSpot> accommodations = tourSpotRepository.findAllByContentTypeId(ACCOMMODATION_CONTENT_TYPE_ID);
            FailureCollector failures = new FailureCollector();
            int processedCount = 0;
            for (TourSpot accommodation : accommodations) {
                Long contentId = accommodation.getContentId();
                try {
                    List<AccommodationDetail> details = accommodationDetails(contentId, accommodation.getSpotId());
                    accommodationDetailStore.replace(accommodation.getSpotId(), details);
                    processedCount++;
                } catch (RuntimeException e) {
                    failures.add(contentId, e);
                    log.warn("Accommodation ingest failed. contentId={}, message={}", contentId, e.getMessage());
                }
            }
            return failures.toResult(processedCount);
        });
    }

    private TourDataResult execute(TourDataJobType jobType, Supplier<TourDataResult> operation) {
        TourDataExecution execution = executionRepository.save(
                TourDataExecution.started(jobType, LocalDateTime.now())
        );

        try {
            TourDataResult result = operation.get();
            LocalDateTime finishedAt = LocalDateTime.now();
            if (result.success()) {
                execution.succeed(result.processedCount(), finishedAt);
            } else if (result.partial()) {
                execution.partialSuccess(
                        result.processedCount(), result.failedCount(), result.failureMessage(), finishedAt
                );
            } else {
                execution.fail(result.failedCount(), result.failureMessage(), finishedAt);
            }
            executionRepository.save(execution);
            return result;
        } catch (RuntimeException e) {
            execution.fail(1, messageOf(e), LocalDateTime.now());
            executionRepository.save(execution);
            log.warn("Tour data job failed. jobType={}, message={}", jobType, e.getMessage(), e);
            return TourDataResult.failure(1, messageOf(e));
        }
    }

    private Map<Long, TourSpot> indexTourSpots() {
        Map<Long, TourSpot> result = new HashMap<>();
        for (TourSpot tourSpot : tourSpotRepository.findAll()) {
            if (tourSpot.getContentId() != null) {
                result.put(tourSpot.getContentId(), tourSpot);
            }
        }
        return result;
    }

    private Map<Long, FestivalInfo> indexFestivals() {
        Map<Long, FestivalInfo> result = new HashMap<>();
        for (FestivalInfo festivalInfo : festivalInfoRepository.findAll()) {
            if (festivalInfo.getContentId() != null) {
                result.put(festivalInfo.getContentId(), festivalInfo);
            }
        }
        return result;
    }

    private List<AccommodationDetail> accommodationDetails(Long contentId, Long spotId) {
        List<AccommodationDetail> details = new ArrayList<>();
        long tempRoomId = 1L;
        for (var roomPayload : publicTourDataClient.fetchAccommodationRooms(
                contentId, ACCOMMODATION_CONTENT_TYPE_ID
        )) {
            details.add(AccommodationDetail.fromRoomPayload(tempRoomId, spotId, roomPayload));
            tempRoomId++;
        }
        return details;
    }

    private static String messageOf(RuntimeException exception) {
        String message = exception.getMessage();
        return message == null || message.isBlank() ? exception.getClass().getSimpleName() : message;
    }

    private static final class FailureCollector {

        private final List<String> recordedFailures = new ArrayList<>();
        private int failedCount;

        void add(Long contentId, RuntimeException exception) {
            failedCount++;
            if (recordedFailures.size() < MAX_RECORDED_FAILURES) {
                recordedFailures.add(contentId + " (" + messageOf(exception) + ")");
            }
        }

        TourDataResult toResult(int processedCount) {
            if (failedCount == 0) {
                return TourDataResult.success(processedCount);
            }

            String message = "Failed " + failedCount + " contentIds: " + String.join("; ", recordedFailures);
            if (failedCount > recordedFailures.size()) {
                message += "; and " + (failedCount - recordedFailures.size()) + " more";
            }
            if (processedCount > 0) {
                return TourDataResult.partial(processedCount, failedCount, message);
            }
            return TourDataResult.failure(failedCount, message);
        }
    }
}
