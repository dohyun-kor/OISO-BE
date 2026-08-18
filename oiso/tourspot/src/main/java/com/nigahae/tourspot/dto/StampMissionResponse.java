package com.nigahae.tourspot.dto;

import com.nigahae.tourspot.domain.StampMissionType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record StampMissionResponse(
        @Schema(description = "스탬프 미션 유형", example = "QR_SCAN") StampMissionType type,
        @Schema(description = "미션 완료 여부", example = "true") boolean cleared,
        @Schema(description = "미션 완료 시각. 미완료 시 null", nullable = true)
        LocalDateTime clearedAt
) {
}
