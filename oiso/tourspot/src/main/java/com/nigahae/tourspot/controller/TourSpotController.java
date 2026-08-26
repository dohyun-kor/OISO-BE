package com.nigahae.tourspot.controller;

import com.nigahae.common.constant.ErrorCode;
import com.nigahae.common.exception.CustomException;
import com.nigahae.common.response.CommonResponse;
import com.nigahae.tourspot.controller.docs.TourSpotControllerDocs;
import com.nigahae.tourspot.dto.DeleteSavedTourSpotResponse;
import com.nigahae.tourspot.dto.NearbyPlaceListResponse;
import com.nigahae.tourspot.dto.PlaceListResponse;
import com.nigahae.tourspot.dto.SaveTourSpotResponse;
import com.nigahae.tourspot.dto.SavedNearbyPlaceResponse;
import com.nigahae.tourspot.dto.TourSpotDetailResponse;
import com.nigahae.tourspot.domain.NearbyPlaceCategory;
import com.nigahae.tourspot.domain.PlaceCategory;
import com.nigahae.tourspot.domain.StampMissionType;
import com.nigahae.tourspot.dto.PlaceListResponse;
import com.nigahae.tourspot.dto.StampMissionClearResponse;
import com.nigahae.user.AuthenticatedUser;
import com.nigahae.tourspot.exception.InvalidTourSpotIdException;
import com.nigahae.tourspot.service.PlaceService;
import com.nigahae.tourspot.service.TourSpotService;
import com.nigahae.user.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.nigahae.common.constant.ErrorCode;
import com.nigahae.common.exception.CustomException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tour-spots")
public class TourSpotController implements TourSpotControllerDocs {

    private static final String SUCCESS_MESSAGE = "장소 목록 조회에 성공했습니다.";
    private static final String DETAIL_SUCCESS_MESSAGE = "관광지 상세 조회에 성공했습니다.";
    private static final String SAVE_SUCCESS_MESSAGE = "관광지 저장에 성공했습니다.";
    private static final String DELETE_SAVED_SUCCESS_MESSAGE = "저장한 관광지 삭제에 성공했습니다.";

    private final TourSpotService tourSpotService;
    private final PlaceService placeService;

    @GetMapping("/{spotId}")
    @Override
    public CommonResponse<TourSpotDetailResponse> getTourSpot(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable String spotId
    ) {
        return new CommonResponse<>(
                CommonResponse.SUCCESS_CODE,
                DETAIL_SUCCESS_MESSAGE,
                tourSpotService.getTourSpot(parseSpotId(spotId), user == null ? null : user.userId())
        );
    }

    @PutMapping("/{spotId}/stamp-missions/{missionType}/clear")
    @Override
    public CommonResponse<StampMissionClearResponse> clearStampMission(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable String spotId,
            @PathVariable StampMissionType missionType
    ) {
        if (user == null) {
            throw new CustomException(ErrorCode.AUTH_ERROR);
        }
        return new CommonResponse<>(CommonResponse.SUCCESS_CODE, "스탬프 미션을 완료했습니다.",
                tourSpotService.clearStampMission(user.userId(), parseSpotId(spotId), missionType));
    }

    @GetMapping
    @Override
    public CommonResponse<PlaceListResponse> getTourSpots(
            @RequestParam(required = false) PlaceCategory category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20")
            int size
    ) {
        return new CommonResponse<>(
                CommonResponse.SUCCESS_CODE,
                SUCCESS_MESSAGE,
                placeService.getPlaces(category, keyword, page, size)
        );
    }

    @Operation(
            summary = "주변 장소 목록 조회",
            description = "관광지 주변의 식당·숙소를 조회합니다. category로 필터링할 수 있습니다."
    )
    @GetMapping("/{spotId}/nearby-places")
    public CommonResponse<NearbyPlaceListResponse> getNearbyPlaces(
            @PathVariable Long spotId,
            @RequestParam(required = false) NearbyPlaceCategory category,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        return new CommonResponse<>(
                CommonResponse.SUCCESS_CODE,
                "주변 장소 목록 조회에 성공했습니다.",
                tourSpotService.getNearbyPlaces(spotId, category, page, size)
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

    @GetMapping("/saved")
    @Override
    public CommonResponse<PlaceListResponse> getSavedPlaces(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(required = false) PlaceCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        if (user == null) {
            throw new CustomException(ErrorCode.AUTH_ERROR);
        }
        return new CommonResponse<>(CommonResponse.SUCCESS_CODE, "저장한 장소 목록 조회에 성공했습니다.",
                placeService.getSavedPlaces(user.userId(), category, page, size));
    }

    @PostMapping("/nearby-places/{nearbyPlaceId}/save")
    @Override
    public CommonResponse<SavedNearbyPlaceResponse> saveNearbyPlace(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long nearbyPlaceId
    ) {
        if (user == null) {
            throw new CustomException(ErrorCode.AUTH_ERROR);
        }
        return new CommonResponse<>(CommonResponse.SUCCESS_CODE, "숙소·식당 저장에 성공했습니다.",
                tourSpotService.saveNearbyPlace(user.userId(), nearbyPlaceId));
    }

    @DeleteMapping("/nearby-places/{nearbyPlaceId}/save")
    @Override
    public CommonResponse<SavedNearbyPlaceResponse> deleteSavedNearbyPlace(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long nearbyPlaceId
    ) {
        if (user == null) {
            throw new CustomException(ErrorCode.AUTH_ERROR);
        }
        return new CommonResponse<>(CommonResponse.SUCCESS_CODE, "저장한 숙소·식당 삭제에 성공했습니다.",
                tourSpotService.deleteSavedNearbyPlace(user.userId(), nearbyPlaceId));
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
