package com.nigahae.tourcourse.dto;

import com.nigahae.tourcourse.domain.TransportType;
import io.swagger.v3.oas.annotations.media.Schema;

public record CreateTourCourseItemRequest(
        @Schema(description = "관광지 ID. nearbyPlaceId와 둘 중 하나만 입력", example = "1", nullable = true)
        Long spotId,
        @Schema(description = "주변 장소 ID. spotId와 둘 중 하나만 입력", example = "10", nullable = true)
        Long nearbyPlaceId,
        @Schema(description = "코스 내 순서(1부터 연속)", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer itemOrder,
        @Schema(description = "이전 항목에서 현재 항목까지의 이동수단. 첫 항목은 null", example = "CAR", nullable = true)
        TransportType transportType
) {
    public CreateTourCourseItemRequest(Long spotId, Integer itemOrder, TransportType transportType) {
        this(spotId, null, itemOrder, transportType);
    }
}
