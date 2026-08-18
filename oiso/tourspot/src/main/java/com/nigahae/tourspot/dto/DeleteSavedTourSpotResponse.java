package com.nigahae.tourspot.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record DeleteSavedTourSpotResponse(
        @Schema(description = "저장 목록에서 삭제한 관광지 ID", example = "1")
        Long spotId,
        @Schema(description = "관광지 저장 여부", example = "false")
        boolean like
) {
}
