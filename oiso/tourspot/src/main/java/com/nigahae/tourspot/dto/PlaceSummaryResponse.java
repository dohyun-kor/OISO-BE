package com.nigahae.tourspot.dto;

import com.nigahae.tourspot.domain.PlaceCategory;
import io.swagger.v3.oas.annotations.media.Schema;

public record PlaceSummaryResponse(
        @Schema(description = "장소 종류", example = "RESTAURANT") PlaceCategory category,
        @Schema(nullable = true, description = "관광지 ID. TOUR_SPOT일 때만 값이 있습니다.", example = "35") Long spotId,
        @Schema(nullable = true, description = "주변 장소 ID. RESTAURANT/ACCOMMODATION일 때만 값이 있습니다.", example = "101") Long nearbyPlaceId,
        @Schema(nullable = true, description = "주변 장소가 연결된 부모 관광지 ID", example = "35") Long parentTourSpotId,
        String name,
        @Schema(nullable = true) String img,
        @Schema(nullable = true) String overview,
        String address,
        Double latitude,
        Double longitude
) {
    public static PlaceSummaryResponse from(PlaceSummaryRow row) {
        return new PlaceSummaryResponse(
                PlaceCategory.valueOf(row.getCategory()), row.getSpotId(), row.getNearbyPlaceId(), row.getParentTourSpotId(),
                row.getName(), row.getImg(), row.getOverview(), row.getAddress(),
                row.getLatitude(), row.getLongitude());
    }
}
