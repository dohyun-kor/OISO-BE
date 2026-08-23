package com.nigahae.tourdata.service;

import com.nigahae.tourdata.TourDetailRefresher;
import com.nigahae.tourdata.TourDetailRefreshResult;
import com.nigahae.tourdata.client.FestivalPayload;
import com.nigahae.tourdata.client.PublicTourDataClient;
import com.nigahae.tourdata.client.TourApiException;
import com.nigahae.tourdata.client.TourContentPayload;
import com.nigahae.tourdata.domain.AccommodationRoom;
import com.nigahae.tourdata.domain.FestivalInfo;
import com.nigahae.tourdata.domain.TourDataExecution;
import com.nigahae.tourdata.domain.TourDataJobType;
import com.nigahae.tourdata.domain.TourSpot;
import com.nigahae.tourdata.domain.TourSpotNearbyPlace;
import com.nigahae.tourdata.repository.FestivalInfoRepository;
import com.nigahae.tourdata.repository.TourDataExecutionRepository;
import com.nigahae.tourdata.repository.TourSpotRepository;
import com.nigahae.tourdata.repository.TourSpotNearbyPlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class TourDataService implements TourDetailRefresher {

    private static final Long ACCOMMODATION_CONTENT_TYPE_ID = 32L;
    private static final Long RESTAURANT_CONTENT_TYPE_ID = 39L;
    private static final List<Long> TOUR_SPOT_CONTENT_TYPE_IDS = List.of(12L, 14L, 15L, 25L, 28L);
    private static final double NEARBY_RADIUS_METERS = 3_000.0;
    private static final int MAX_NEARBY_PLACES_PER_CATEGORY = 20;
    private static final ZoneId GYEONGJU_TIME_ZONE = ZoneId.of("Asia/Seoul");
    private static final int MAX_RECORDED_FAILURES = 20;

    private final PublicTourDataClient publicTourDataClient;
    private final TourSpotRepository tourSpotRepository;
    private final TourSpotNearbyPlaceRepository nearbyPlaceRepository;
    private final FestivalInfoRepository festivalInfoRepository;
    private final AccommodationRoomStore accommodationRoomStore;
    private final TourDataExecutionRepository executionRepository;

    public TourDataResult refreshTourSpots() {
        return execute(TourDataJobType.TOUR_SPOTS, () -> {
            List<TourContentPayload> payloads = publicTourDataClient.fetchAllTourContents();
            List<TourContentPayload> tourSpotPayloads = payloads.stream()
                    .filter(payload -> TOUR_SPOT_CONTENT_TYPE_IDS.contains(payload.contentTypeIdAsLong()))
                    .toList();
            for (TourContentPayload payload : tourSpotPayloads) {
                Long contentId = payload.contentIdAsLong();
                TourSpot tourSpot = tourSpotRepository.findByContentId(contentId)
                        .orElseGet(TourSpot::create);
                tourSpot.updateFromList(payload);
                tourSpotRepository.save(tourSpot);
            }

            List<TourContentPayload> nearbyPayloads = payloads.stream()
                    .filter(payload -> ACCOMMODATION_CONTENT_TYPE_ID.equals(payload.contentTypeIdAsLong())
                            || RESTAURANT_CONTENT_TYPE_ID.equals(payload.contentTypeIdAsLong()))
                    .filter(this::hasCoordinates)
                    .toList();
            List<TourSpot> tourSpots = tourSpotRepository.findAllByContentTypeIdIn(
                    TOUR_SPOT_CONTENT_TYPE_IDS);
            int nearbyCount = refreshNearbyPlaces(tourSpots, nearbyPayloads);
            return TourDataResult.success(tourSpotPayloads.size() + nearbyCount);
        });
    }

    private int refreshNearbyPlaces(List<TourSpot> tourSpots, List<TourContentPayload> candidates) {
        int processedCount = 0;
        Map<Long, TourContentPayload> detailCache = new HashMap<>();
        for (TourSpot tourSpot : tourSpots) {
            if (tourSpot.getMapX() == null || tourSpot.getMapY() == null) continue;
            for (Long contentTypeId : List.of(RESTAURANT_CONTENT_TYPE_ID, ACCOMMODATION_CONTENT_TYPE_ID)) {
                List<TourContentPayload> nearby = candidates.stream()
                        .filter(candidate -> candidate.contentTypeIdAsLong().equals(contentTypeId))
                        .filter(candidate -> distanceMeters(tourSpot.getMapY(), tourSpot.getMapX(),
                                candidate.mapYAsDouble(), candidate.mapXAsDouble()) <= NEARBY_RADIUS_METERS)
                        .sorted(Comparator.comparingDouble(candidate -> distanceMeters(
                                tourSpot.getMapY(), tourSpot.getMapX(),
                                candidate.mapYAsDouble(), candidate.mapXAsDouble())))
                        .limit(MAX_NEARBY_PLACES_PER_CATEGORY)
                        .toList();
                for (TourContentPayload payload : nearby) {
                    Long contentId = payload.contentIdAsLong();
                    String category = contentTypeId.equals(RESTAURANT_CONTENT_TYPE_ID)
                            ? "RESTAURANT" : "ACCOMMODATION";
                    TourSpotNearbyPlace place = nearbyPlaceRepository
                            .findBySpotIdAndContentId(tourSpot.getSpotId(), contentId)
                            .orElseGet(() -> TourSpotNearbyPlace.create(tourSpot.getSpotId(), category, payload));
                    place.update(category, payload);
                    TourContentPayload detail = nearbyDetail(payload, detailCache);
                    if (detail != payload) place.update(category, detail);
                    nearbyPlaceRepository.save(place);
                    processedCount++;
                }
            }
        }
        return processedCount;
    }

    private TourContentPayload nearbyDetail(
            TourContentPayload listPayload, Map<Long, TourContentPayload> detailCache
    ) {
        if (listPayload.overview() != null && !listPayload.overview().isBlank()) return listPayload;
        Long contentId = listPayload.contentIdAsLong();
        if (detailCache.containsKey(contentId)) return detailCache.get(contentId);
        try {
            TourContentPayload detail = publicTourDataClient.fetchTourContentDetail(contentId);
            TourContentPayload result = detail == null ? listPayload : detail;
            detailCache.put(contentId, result);
            return result;
        } catch (TourApiException exception) {
            log.warn("Nearby place detail ingest failed. contentId={}, message={}", contentId, exception.getMessage());
            detailCache.put(contentId, listPayload);
            return listPayload;
        }
    }

    private boolean hasCoordinates(TourContentPayload payload) {
        return payload.mapXAsDouble() != null && payload.mapYAsDouble() != null;
    }

    private double distanceMeters(double latitude1, double longitude1, double latitude2, double longitude2) {
        double earthRadius = 6_371_000.0;
        double latDelta = Math.toRadians(latitude2 - latitude1);
        double lonDelta = Math.toRadians(longitude2 - longitude1);
        double a = Math.sin(latDelta / 2) * Math.sin(latDelta / 2)
                + Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2))
                * Math.sin(lonDelta / 2) * Math.sin(lonDelta / 2);
        return earthRadius * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    public TourDataResult refreshFestivals() {
        return execute(TourDataJobType.FESTIVALS, () -> {
            LocalDate eventStartDate = LocalDate.now(GYEONGJU_TIME_ZONE);
            List<FestivalPayload> payloads = publicTourDataClient.fetchAllFestivals(eventStartDate);
            for (FestivalPayload payload : payloads) {
                Long contentId = payload.content().contentIdAsLong();
                TourSpot tourSpot = tourSpotRepository.findByContentId(contentId)
                        .orElseGet(TourSpot::create);
                tourSpot.updateFromList(payload.content());
                tourSpot = tourSpotRepository.save(tourSpot);

                TourSpot savedTourSpot = tourSpot;
                FestivalInfo festivalInfo = festivalInfoRepository.findById(savedTourSpot.getSpotId())
                        .orElseGet(() -> FestivalInfo.forSpot(savedTourSpot));
                festivalInfo.updateDates(payload);
                festivalInfoRepository.save(festivalInfo);
            }
            return TourDataResult.success(payloads.size());
        });
    }

    @Override
    public TourDetailRefreshResult refresh(Long spotId) {
        TourSpot tourSpot = tourSpotRepository.findById(spotId).orElse(null);
        if (tourSpot == null) {
            return TourDetailRefreshResult.notFound();
        }

        Long contentId = tourSpot.getContentId();
        try {
            TourContentPayload detail = publicTourDataClient.fetchTourContentDetail(contentId);
            tourSpot.updateFromDetail(detail);
            tourSpotRepository.save(tourSpot);
            return TourDetailRefreshResult.updated();
        } catch (TourApiException exception) {
            String failureMessage = messageOf(exception);
            log.warn(
                    "Tour detail refresh failed. spotId={}, contentId={}, message={}",
                    spotId,
                    contentId,
                    failureMessage
            );
            return TourDetailRefreshResult.failed(failureMessage);
        }
    }

    public TourDataResult refreshAccommodations() {
        return execute(TourDataJobType.ACCOMMODATIONS, () -> {
            List<TourSpot> accommodations = tourSpotRepository.findAllByContentTypeId(ACCOMMODATION_CONTENT_TYPE_ID);
            FailureCollector failures = new FailureCollector();
            int processedCount = 0;
            for (TourSpot accommodation : accommodations) {
                Long contentId = accommodation.getContentId();
                try {
                    List<AccommodationRoom> rooms = accommodationRooms(contentId, accommodation.getSpotId());
                    accommodationRoomStore.replace(accommodation.getSpotId(), rooms);
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

    private List<AccommodationRoom> accommodationRooms(Long contentId, Long spotId) {
        List<AccommodationRoom> rooms = new ArrayList<>();
        int roomOrder = 1;
        for (var roomPayload : publicTourDataClient.fetchAccommodationRooms(
                contentId, ACCOMMODATION_CONTENT_TYPE_ID
        )) {
            rooms.add(AccommodationRoom.fromRoomPayload(spotId, roomOrder, roomPayload));
            roomOrder++;
        }
        return rooms;
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
