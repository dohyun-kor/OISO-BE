package com.nigahae.tourspot.service;

import com.nigahae.common.exception.CustomCommonException;
import com.nigahae.common.exception.ErrorCode;
import com.nigahae.tourspot.domain.TourSpot;
import com.nigahae.tourspot.dto.TourSpotListResponse;
import com.nigahae.tourspot.dto.TourSpotSummary;
import com.nigahae.tourspot.repository.TourSpotRepository;
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

    private final TourSpotRepository tourSpotRepository;

    public TourSpotListResponse getTourSpots(String keyword, int page, int size) {
        PageRequest pageRequest = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.ASC, "spotId")
        );
        String normalizedKeyword = keyword == null ? "" : keyword.trim();

        Page<TourSpot> result = normalizedKeyword.isEmpty()
                ? tourSpotRepository.findAll(pageRequest)
                : tourSpotRepository.searchByKeyword(normalizedKeyword, pageRequest);

        if (result.isEmpty()) {
            throw new CustomCommonException(ErrorCode.SEARCH_RESULT_NOT_FOUND);
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
