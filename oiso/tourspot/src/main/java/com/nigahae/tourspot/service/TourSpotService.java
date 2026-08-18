package com.nigahae.tourspot.service;

import com.nigahae.common.constant.ErrorCode;
import com.nigahae.common.exception.CustomException;
import com.nigahae.tourdata.TourDetailRefresher;
import com.nigahae.tourdata.TourDetailRefreshResult;
import com.nigahae.tourspot.domain.TourSpot;
import com.nigahae.tourspot.domain.NearbyPlaceCategory;
import com.nigahae.tourspot.dto.DeleteSavedTourSpotResponse;
import com.nigahae.tourspot.dto.NearbyPlaceListResponse;
import com.nigahae.tourspot.dto.NearbyPlaceResponse;
import com.nigahae.tourspot.dto.SaveTourSpotResponse;
import com.nigahae.tourspot.dto.TourSpotDetailResponse;
import com.nigahae.tourspot.dto.TourSpotListResponse;
import com.nigahae.tourspot.dto.TourSpotSummary;
import com.nigahae.tourspot.domain.StampMissionClear;
import com.nigahae.tourspot.domain.StampMissionType;
import com.nigahae.tourspot.dto.StampMissionClearResponse;
import com.nigahae.tourspot.dto.StampMissionResponse;
import com.nigahae.tourspot.dto.StampProgressResponse;
import com.nigahae.tourspot.exception.TourSpotNotFoundException;
import com.nigahae.tourspot.repository.TourSpotQueryRepository;
import com.nigahae.tourspot.repository.StampMissionClearRepository;
import com.nigahae.tourspot.repository.UserSavedTourSpotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TourSpotService {

    private final TourSpotQueryRepository tourSpotQueryRepository;
    private final UserSavedTourSpotRepository userSavedTourSpotRepository;
    private final TourDetailRefresher tourDetailRefresher;
    private final StampMissionClearRepository stampMissionClearRepository;

    @Transactional
    public SaveTourSpotResponse saveTourSpot(Long userId, Long spotId) {
        validateTourSpotExists(spotId);
        userSavedTourSpotRepository.saveIfAbsent(userId, spotId);
        return new SaveTourSpotResponse(spotId, true);
    }

    @Transactional
    public DeleteSavedTourSpotResponse deleteSavedTourSpot(Long userId, Long spotId) {
        validateTourSpotExists(spotId);
        userSavedTourSpotRepository.delete(userId, spotId);
        return new DeleteSavedTourSpotResponse(spotId, false);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public TourSpotDetailResponse getTourSpot(Long spotId, Long userId) {
        TourDetailRefreshResult refreshResult = tourDetailRefresher.refresh(spotId);
        if (refreshResult.targetNotFound()) {
            throw new TourSpotNotFoundException(spotId);
        }
        if (refreshResult.failed()) {
            log.warn("Returning stored tour detail after refresh failure. spotId={}, message={}",
                    spotId, refreshResult.failureMessage());
        }
        TourSpot tourSpot = tourSpotQueryRepository.findById(spotId)
                .orElseThrow(() -> new TourSpotNotFoundException(spotId));

        StampProgressResponse progress = userId == null ? null : getStampProgress(userId, spotId);
        return TourSpotDetailResponse.from(tourSpot, progress);
    }

    public TourSpotDetailResponse getTourSpot(Long spotId) {
        return getTourSpot(spotId, null);
    }

    @Transactional
    public StampMissionClearResponse clearStampMission(
            Long userId, Long spotId, StampMissionType missionType
    ) {
        if (!tourSpotQueryRepository.existsById(spotId)) {
            throw new TourSpotNotFoundException(spotId);
        }
        stampMissionClearRepository.saveIfAbsent(userId, spotId, missionType.name());
        StampMissionClear clear = stampMissionClearRepository.findById(
                        new com.nigahae.tourspot.domain.StampMissionClearId(userId, spotId, missionType))
                .orElseThrow(() -> new CustomException(ErrorCode.INTERNAL_SERVER_ERROR));
        return new StampMissionClearResponse(spotId, missionType, true, clear.getClearedAt());
    }

    private StampProgressResponse getStampProgress(Long userId, Long spotId) {
        Map<StampMissionType, StampMissionClear> clears = stampMissionClearRepository
                .findAllByIdUserIdAndIdSpotId(userId, spotId).stream()
                .collect(Collectors.toMap(clear -> clear.getId().getMissionType(), Function.identity()));
        List<StampMissionResponse> missions = java.util.Arrays.stream(StampMissionType.values())
                .map(type -> new StampMissionResponse(
                        type, clears.containsKey(type),
                        clears.containsKey(type) ? clears.get(type).getClearedAt() : null))
                .toList();
        int clearedCount = clears.size();
        return new StampProgressResponse(
                clearedCount, missions.size(), clearedCount == missions.size(), missions);
    }

    public TourSpotListResponse getTourSpots(String keyword, int page, int size) {
        PageRequest pageRequest = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.ASC, "spotId")
        );
        String normalizedKeyword = keyword == null ? "" : keyword.trim();

        Page<TourSpot> result = normalizedKeyword.isEmpty()
                ? tourSpotQueryRepository.findAll(pageRequest)
                : tourSpotQueryRepository.searchByKeyword(normalizedKeyword, pageRequest);

        if (result.isEmpty()) {
            throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        List<TourSpotSummary> tourSpots = result.getContent().stream()
                .map(TourSpotSummary::from)
                .toList();

        return new TourSpotListResponse(
                tourSpots,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements()
        );
    }

    public NearbyPlaceResponse getNearbyPlace(Long nearbyPlaceId) {
        return tourSpotQueryRepository.findNearbyPlace(nearbyPlaceId)
                .map(NearbyPlaceResponse::from)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    public NearbyPlaceListResponse getNearbyPlaces(
            Long spotId, NearbyPlaceCategory category, int page, int size
    ) {
        if (!tourSpotQueryRepository.existsById(spotId)) {
            throw new TourSpotNotFoundException(spotId);
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<com.nigahae.tourspot.dto.NearbyPlaceRow> result =
                tourSpotQueryRepository.findNearbyPlaces(
                        spotId, category == null ? "" : category.name(), pageRequest);
        List<NearbyPlaceResponse> places = result.getContent().stream()
                .map(NearbyPlaceResponse::from)
                .toList();
        return new NearbyPlaceListResponse(
                places, result.getNumber(), result.getSize(), result.getTotalElements());
    }

    private void validateTourSpotExists(Long spotId) {
        if (!tourSpotQueryRepository.existsById(spotId)) {
            throw new TourSpotNotFoundException(spotId);
        }
    }
}
