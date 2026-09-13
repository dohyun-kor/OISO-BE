package com.nigahae.tourspot.controller;

import com.nigahae.common.response.CommonResponse;
import com.nigahae.tourspot.controller.docs.NearbyPlaceControllerDocs;
import com.nigahae.tourspot.dto.NearbyPlaceResponse;
import com.nigahae.tourspot.service.TourSpotService;
import lombok.RequiredArgsConstructor;
import com.nigahae.user.AuthenticatedUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/nearby-places")
public class NearbyPlaceController implements NearbyPlaceControllerDocs {

    private final TourSpotService tourSpotService;

    public CommonResponse<NearbyPlaceResponse> getNearbyPlace(@PathVariable Long nearbyPlaceId) {
        return getNearbyPlace(null, nearbyPlaceId);
    }

    @GetMapping("/{nearbyPlaceId}")
    @Override
    public CommonResponse<NearbyPlaceResponse> getNearbyPlace(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long nearbyPlaceId
    ) {
        return new CommonResponse<>(
                CommonResponse.SUCCESS_CODE,
                "주변 장소 상세 조회에 성공했습니다.",
                tourSpotService.getNearbyPlace(nearbyPlaceId, user == null ? null : user.userId()));
    }
}
