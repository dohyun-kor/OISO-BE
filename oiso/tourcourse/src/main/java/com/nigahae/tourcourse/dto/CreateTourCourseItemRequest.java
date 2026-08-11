package com.nigahae.tourcourse.dto;

import com.nigahae.tourcourse.domain.TransportType;
import com.nigahae.tourcourse.domain.TourCourseItemCategory;
import io.swagger.v3.oas.annotations.media.Schema;

public record CreateTourCourseItemRequest(
        @Schema(description = "항목 종류", example = "TOUR_SPOT", requiredMode = Schema.RequiredMode.REQUIRED)
        TourCourseItemCategory category,
        @Schema(nullable = true, description = "관광지 항목의 관광지 ID. TOUR_SPOT일 때만 입력합니다.", example = "1")
        Long spotId,
        @Schema(nullable = true, description = "식당·숙소 항목의 주변 장소 ID. RESTAURANT 또는 ACCOMMODATION일 때만 입력합니다.", example = "101")
        Long nearbyPlaceId,
        @Schema(description = "관광지 순서(1부터 연속)", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer itemOrder,
        @Schema(description = "이전 관광지에서 현재 관광지까지의 이동수단. 첫 관광지는 null", example = "CAR", nullable = true)
        TransportType transportType
) {
    public CreateTourCourseItemRequest(Long spotId, Integer itemOrder, TransportType transportType) {
        this(TourCourseItemCategory.TOUR_SPOT, spotId, null, itemOrder, transportType);
    }
}
