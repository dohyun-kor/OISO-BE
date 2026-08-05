package com.nigahae.tourcourse.dto;

import com.nigahae.tourcourse.domain.TransportType;
import io.swagger.v3.oas.annotations.media.Schema;

public record CreateTourCourseItemRequest(
        @Schema(description = "관광지 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long spotId,
        @Schema(description = "관광지 순서(1부터 연속)", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer itemOrder,
        @Schema(description = "이전 관광지에서 현재 관광지까지의 이동수단. 첫 관광지는 null", example = "CAR", nullable = true)
        TransportType transportType
) {
}
