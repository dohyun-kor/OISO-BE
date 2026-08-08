package com.nigahae.tourspot.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record SaveTourSpotResponse(
        @Schema(description = "저장한 관광지 ID", example = "1")
        Long spotId,
        @Schema(description = "관광지 저장 여부", example = "true")
        boolean like
) {
}
