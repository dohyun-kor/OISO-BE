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
import com.nigahae.tourspot.exception.TourSpotNotFoundException;
import com.nigahae.tourspot.repository.TourSpotQueryRepository;
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

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TourSpotService {

    private final TourSpotQueryRepository tourSpotQueryRepository;
    private final UserSavedTourSpotRepository userSavedTourSpotRepository;
    private final TourDetailRefresher tourDetailRefresher;

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
    public TourSpotDetailResponse getTourSpot(Long spotId) {
        TourDetailRefreshResult refreshResult = tourDetailRefresher.refresh(spotId);
        if (refreshResult.targetNotFound()) {
            throw new TourSpotNotFoundException(spotId);
        }
        if (refreshResult.failed()) {
            log.warn(
                    "Returning stored tour detail after refresh failure. spotId={}, message={}",
                    spotId,
                    refreshResult.failureMessage()
            );
        }

        TourSpot tourSpot = tourSpotQueryRepository.findById(spotId)
                .orElseThrow(() -> new TourSpotNotFoundException(spotId));

        return TourSpotDetailResponse.from(tourSpot);
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
