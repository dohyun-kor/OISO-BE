package com.nigahae.tourspot.controller;

import com.nigahae.common.constant.ErrorCode;
import com.nigahae.common.exception.CustomException;
import com.nigahae.common.response.CommonResponse;
import com.nigahae.tourspot.controller.docs.TourSpotControllerDocs;
import com.nigahae.tourspot.dto.DeleteSavedTourSpotResponse;
import com.nigahae.tourspot.dto.SaveTourSpotResponse;
import com.nigahae.tourspot.dto.TourSpotDetailResponse;
import com.nigahae.tourspot.dto.TourSpotListResponse;
import com.nigahae.tourspot.exception.InvalidTourSpotIdException;
import com.nigahae.tourspot.service.TourSpotService;
import com.nigahae.user.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tour-spots")
public class TourSpotController implements TourSpotControllerDocs {

    private static final String SUCCESS_MESSAGE = "관광지 목록 조회에 성공했습니다.";
    private static final String DETAIL_SUCCESS_MESSAGE = "관광지 상세 조회에 성공했습니다.";
    private static final String SAVE_SUCCESS_MESSAGE = "관광지 저장에 성공했습니다.";
    private static final String DELETE_SAVED_SUCCESS_MESSAGE = "저장한 관광지 삭제에 성공했습니다.";

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

    @PostMapping("/{spotId}/save")
    @Override
    public CommonResponse<SaveTourSpotResponse> saveTourSpot(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable String spotId
    ) {
        if (user == null) {
            throw new CustomException(ErrorCode.AUTH_ERROR);
        }

        return new CommonResponse<>(
                CommonResponse.SUCCESS_CODE,
                SAVE_SUCCESS_MESSAGE,
                tourSpotService.saveTourSpot(user.userId(), parseSpotId(spotId))
        );
    }

    @DeleteMapping("/{spotId}/save")
    @Override
    public CommonResponse<DeleteSavedTourSpotResponse> deleteSavedTourSpot(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable String spotId
    ) {
        if (user == null) {
            throw new CustomException(ErrorCode.AUTH_ERROR);
        }

        return new CommonResponse<>(
                CommonResponse.SUCCESS_CODE,
                DELETE_SAVED_SUCCESS_MESSAGE,
                tourSpotService.deleteSavedTourSpot(user.userId(), parseSpotId(spotId))
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
