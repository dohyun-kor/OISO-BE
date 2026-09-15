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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public TourDataResult refreshTourSpots() {
        return execute(TourDataJobType.TOUR_SPOTS, () -> {
            List<TourContentPayload> payloads = publicTourDataClient.fetchAllTourContents();
            List<TourContentPayload> tourSpotPayloads = payloads.stream()
                    .filter(payload -> TOUR_SPOT_CONTENT_TYPE_IDS.contains(payload.contentTypeIdAsLong()))
                    .toList();
            Map<Long, TourContentPayload> tourSpotDetailCache = new HashMap<>();
            for (TourContentPayload payload : tourSpotPayloads) {
                Long contentId = payload.contentIdAsLong();
                TourSpot tourSpot = tourSpotRepository.findByContentId(contentId)
                        .orElseGet(TourSpot::create);
                tourSpot.updateFromList(payload);
                TourContentPayload detail = contentDetail(payload, tourSpotDetailCache);
                if (detail != payload) {
                    tourSpot.updateFromDetail(detail);
                }
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

    /** 기존에 저장된 주변 장소를 기준으로 상세정보만 재수집한다. */
    public TourDataResult refreshNearbyPlaceDetails() {
        return execute(TourDataJobType.TOUR_SPOTS, () -> {
            int processed = 0;
            Map<Long, TourContentPayload> cache = new HashMap<>();
            for (TourSpotNearbyPlace place : nearbyPlaceRepository.findAll()) {
                if (!"ACCOMMODATION".equalsIgnoreCase(place.getCategory())) continue;
                if (place.getContentId() == null) continue;
                TourContentPayload detail = nearbyContentDetail(
                        new TourContentPayload(String.valueOf(place.getContentId()),
                                String.valueOf(place.getContentTypeId()), place.getName(), null, null,
                                place.getAddress(), place.getMapX() == null ? null : String.valueOf(place.getMapX()),
                                place.getMapY() == null ? null : String.valueOf(place.getMapY()),
                                place.getImg(), place.getOverview(), place.getHomepageUrl(), null, null), cache);
                JsonNode detailInfo = publicTourDataClient.fetchDetailInfo(
                        place.getContentId(), place.getContentTypeId());
                saveNearbyPlaceDetail(place.getNearbyPlaceId(), detailInfo);
                processed++;
            }
            return TourDataResult.success(processed);
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
                    // 주변 식당·숙소는 목록 응답에 상세 필드가 없을 수 있으므로
                    // overview 유무와 관계없이 detailCommon2를 조회한다.
                    TourContentPayload detail = nearbyContentDetail(payload, detailCache);
                    if (detail != payload) place.update(category, detail);
                    TourSpotNearbyPlace savedPlace = nearbyPlaceRepository.save(place);
                    JsonNode detailInfo = publicTourDataClient.fetchDetailInfo(
                            savedPlace.getContentId(), savedPlace.getContentTypeId());
                    saveNearbyPlaceDetail(savedPlace.getNearbyPlaceId(), detailInfo);
                    processedCount++;
                }
            }
        }
        return processedCount;
    }

    private void saveNearbyPlaceDetail(Long nearbyPlaceId, JsonNode n) {
        if (nearbyPlaceId == null || n == null || n.isMissingNode() || n.isNull()) return;
        try {
            jdbcTemplate.update("""
                    INSERT INTO public."NearbyPlaceDetail"
                    (tour_spot_nearby_course_id, open_time, rest_date, reservation_info,
                     inquiry_info, parking_info, pet_info, first_menu, treat_menu,
                     first_menu_price, treat_menu_price, menu_source, menu_updated_at)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())
                    ON CONFLICT (tour_spot_nearby_course_id) DO UPDATE SET
                      open_time=COALESCE(EXCLUDED.open_time, "NearbyPlaceDetail".open_time),
                      rest_date=COALESCE(EXCLUDED.rest_date, "NearbyPlaceDetail".rest_date),
                      reservation_info=COALESCE(EXCLUDED.reservation_info, "NearbyPlaceDetail".reservation_info),
                      inquiry_info=COALESCE(EXCLUDED.inquiry_info, "NearbyPlaceDetail".inquiry_info),
                      parking_info=COALESCE(EXCLUDED.parking_info, "NearbyPlaceDetail".parking_info),
                      pet_info=COALESCE(EXCLUDED.pet_info, "NearbyPlaceDetail".pet_info),
                      first_menu=COALESCE(EXCLUDED.first_menu, "NearbyPlaceDetail".first_menu),
                      treat_menu=COALESCE(EXCLUDED.treat_menu, "NearbyPlaceDetail".treat_menu),
                      first_menu_price=COALESCE(EXCLUDED.first_menu_price, "NearbyPlaceDetail".first_menu_price),
                      treat_menu_price=COALESCE(EXCLUDED.treat_menu_price, "NearbyPlaceDetail".treat_menu_price),
                      menu_source=COALESCE(EXCLUDED.menu_source, "NearbyPlaceDetail".menu_source),
                      menu_updated_at=now()
                    """,
                    first(n,"usetime", "usetimeid"), first(n,"restdate", "restdateid"), first(n,"reservationinfo", "reservation"),
                    first(n,"infocenterfood", "infocenter"), first(n,"parkingfood", "parking"), first(n,"chkpet", "petinfo"),
                    text(n,"firstmenu"), text(n,"treatmenu"), number(n,"firstmenuprice"),
                    number(n,"treatmenuprice"), "KOREA_TOUR_API");
        } catch (Exception e) {
            log.error("Nearby place detail ingest failed. nearbyPlaceId={}, payload={}, message={}",
                    nearbyPlaceId, n, e.getMessage(), e);
        }
    }

    private TourContentPayload nearbyContentDetail(TourContentPayload payload, Map<Long, TourContentPayload> cache) {
        Long contentId = payload.contentIdAsLong();
        if (cache.containsKey(contentId)) return cache.get(contentId);
        try {
            TourContentPayload detail = publicTourDataClient.fetchTourContentDetail(contentId);
            TourContentPayload result = detail == null ? payload : detail;
            cache.put(contentId, result);
            return result;
        } catch (TourApiException e) {
            log.warn("Nearby place detail fetch failed. contentId={}, message={}", contentId, e.getMessage());
            cache.put(contentId, payload);
            return payload;
        }
    }

    private static String text(JsonNode node, String key) {
        JsonNode v = node.get(key); return v == null || v.isNull() || v.asText().isBlank() ? null : v.asText();
    }
    private static String first(JsonNode node, String... keys) {
        for (String key : keys) { String value = text(node, key); if (value != null) return value; }
        return null;
    }
    private static Long number(JsonNode node, String key) {
        String v = text(node,key); if (v == null) return null;
        try { return Long.valueOf(v.replaceAll("[^0-9-]", "")); } catch (Exception e) { return null; }
    }

    private TourContentPayload contentDetail(
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
            log.warn("Tour content detail ingest failed. contentId={}, message={}", contentId, exception.getMessage());
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
