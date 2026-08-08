package com.nigahae.tourspot.service;

import com.nigahae.common.constant.ErrorCode;
import com.nigahae.common.exception.CustomException;
import com.nigahae.tourspot.domain.TourSpot;
import com.nigahae.tourspot.dto.SaveTourSpotResponse;
import com.nigahae.tourspot.dto.TourSpotDetailResponse;
import com.nigahae.tourspot.dto.TourSpotListResponse;
import com.nigahae.tourspot.dto.TourSpotSummary;
import com.nigahae.tourspot.exception.TourSpotNotFoundException;
import com.nigahae.tourspot.repository.TourSpotQueryRepository;
import com.nigahae.tourspot.repository.UserSavedTourSpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TourSpotService {

    private final TourSpotQueryRepository tourSpotQueryRepository;
    private final UserSavedTourSpotRepository userSavedTourSpotRepository;

    @Transactional
    public SaveTourSpotResponse saveTourSpot(Long userId, Long spotId) {
        if (!tourSpotQueryRepository.existsById(spotId)) {
            throw new TourSpotNotFoundException(spotId);
        }

        userSavedTourSpotRepository.saveIfAbsent(userId, spotId);
        return new SaveTourSpotResponse(spotId, true);
    }

    public TourSpotDetailResponse getTourSpot(Long spotId) {
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
}
