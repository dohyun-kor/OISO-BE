package com.nigahae.tourcourse.dto;

import com.nigahae.tourcourse.domain.TransportType;
import com.nigahae.tourcourse.domain.TourCourseItemCategory;
import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateTourCourseItemRequest(
        @Schema(nullable = true, description = "관광 코스에 포함된 항목 자체의 ID. 기존 항목은 상세 조회의 itemId를 입력하고, 새로 추가하는 항목은 null로 입력합니다.", example = "11")
        Long itemId,
        @Schema(description = "항목 종류", example = "TOUR_SPOT", requiredMode = Schema.RequiredMode.REQUIRED)
        TourCourseItemCategory category,
        @Schema(nullable = true, description = "관광지 항목의 관광지 ID. TOUR_SPOT일 때만 입력합니다.", example = "35")
        Long spotId,
        @Schema(nullable = true, description = "식당·숙소 항목의 주변 장소 ID. 해당 category일 때만 입력합니다.", example = "101")
        Long nearbyPlaceId,
        @Schema(description = "여행 일차(1부터 연속)", example = "1") Integer dayNumber,
        @Schema(description = "해당 일차 내 방문 순서(1부터 연속)", example = "1")
        Integer itemOrder,
        @Schema(nullable = true) TransportType transportType
) {
    public UpdateTourCourseItemRequest(
            Long itemId, Long spotId, Integer itemOrder, TransportType transportType,
            Integer ignoredDistance, Integer ignoredDuration
    ) {
        this(itemId, spotId, itemOrder, transportType);
    }

    public UpdateTourCourseItemRequest(
            Long itemId, Long spotId, Integer itemOrder, TransportType transportType
    ) {
        this(itemId, TourCourseItemCategory.TOUR_SPOT, spotId, null, 1, itemOrder,
                transportType);
    }

    public UpdateTourCourseItemRequest(
            Long itemId, TourCourseItemCategory category, Long spotId, Long nearbyPlaceId,
            Integer itemOrder, TransportType transportType
    ) {
        this(itemId, category, spotId, nearbyPlaceId, 1, itemOrder,
                transportType);
    }
}
