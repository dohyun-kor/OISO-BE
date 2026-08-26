package com.nigahae.tourspot.dto;

import com.nigahae.tourspot.domain.NearbyPlaceCategory;
import io.swagger.v3.oas.annotations.media.Schema;

public record SavedNearbyPlaceResponse(
        @Schema(description = "저장하거나 삭제한 숙소·식당 ID", example = "101")
        Long nearbyPlaceId,
        @Schema(description = "장소 종류", example = "RESTAURANT")
        NearbyPlaceCategory category,
        @Schema(description = "저장 여부", example = "true")
        boolean like
) {
}
