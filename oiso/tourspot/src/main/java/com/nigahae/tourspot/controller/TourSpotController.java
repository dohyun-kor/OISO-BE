package com.nigahae.tourspot.controller;

import com.nigahae.common.response.CommonResponse;
import com.nigahae.tourspot.controller.docs.TourSpotControllerDocs;
import com.nigahae.tourspot.dto.TourSpotDetailResponse;
import com.nigahae.tourspot.dto.TourSpotListResponse;
import com.nigahae.tourspot.exception.InvalidTourSpotIdException;
import com.nigahae.tourspot.service.TourSpotService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/tour-spots")
public class TourSpotController implements TourSpotControllerDocs {

    private static final String SUCCESS_MESSAGE = "관광지 목록 조회에 성공했습니다.";
    private static final String DETAIL_SUCCESS_MESSAGE = "관광지 상세 조회에 성공했습니다.";

    private final TourSpotService tourSpotService;

    @GetMapping("/{spotId}")
    @Override
    public CommonResponse<TourSpotDetailResponse> getTourSpot(@PathVariable String spotId) {
        return new CommonResponse<>(
                CommonResponse.SUCCESS_CODE,
                DETAIL_SUCCESS_MESSAGE,
                tourSpotService.getTourSpot(parseSpotId(spotId))
        );
    }

    @GetMapping
    @Override
    public CommonResponse<TourSpotListResponse> getTourSpots(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20")
            int size
    ) {
        return new CommonResponse<>(
                CommonResponse.SUCCESS_CODE,
                SUCCESS_MESSAGE,
                tourSpotService.getTourSpots(keyword, page, size)
        );
    }

    private Long parseSpotId(String spotId) {
        try {
            long parsedSpotId = Long.parseLong(spotId);
            if (parsedSpotId <= 0) {
                throw new InvalidTourSpotIdException();
            }
            return parsedSpotId;
        } catch (NumberFormatException exception) {
            throw new InvalidTourSpotIdException();
        }
    }
}
