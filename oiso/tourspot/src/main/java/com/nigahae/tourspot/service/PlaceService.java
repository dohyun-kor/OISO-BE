package com.nigahae.tourspot.service;

import com.nigahae.common.constant.ErrorCode;
import com.nigahae.common.exception.CustomException;
import com.nigahae.tourspot.domain.PlaceCategory;
import com.nigahae.tourspot.dto.PlaceListResponse;
import com.nigahae.tourspot.dto.PlaceSummaryResponse;
import com.nigahae.tourspot.repository.TourSpotQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {

    private final TourSpotQueryRepository tourSpotQueryRepository;

    public PlaceListResponse getPlaces(
            PlaceCategory category, String keyword, int page, int size
    ) {
        String normalizedKeyword = keyword == null ? "" : keyword.trim();
        Page<com.nigahae.tourspot.dto.PlaceSummaryRow> result =
                tourSpotQueryRepository.findPlaces(
                        category == null ? "" : category.name(),
                        normalizedKeyword,
                        PageRequest.of(page, size));
        if (result.isEmpty()) {
            throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        List<PlaceSummaryResponse> places = result.getContent().stream()
                .map(PlaceSummaryResponse::from)
                .toList();
        return new PlaceListResponse(
                places, result.getNumber(), result.getSize(), result.getTotalElements());
    }

    public PlaceListResponse getSavedPlaces(
            Long userId, PlaceCategory category, int page, int size
    ) {
        Page<com.nigahae.tourspot.dto.PlaceSummaryRow> result =
                tourSpotQueryRepository.findSavedPlaces(
                        userId,
                        category == null ? "" : category.name(),
                        PageRequest.of(page, size));
        List<PlaceSummaryResponse> places = result.getContent().stream()
                .map(PlaceSummaryResponse::from)
                .toList();
        return new PlaceListResponse(
                places, result.getNumber(), result.getSize(), result.getTotalElements());
    }
}
