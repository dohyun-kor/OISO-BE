package com.nigahae.tourspot.dto;

import com.nigahae.tourspot.domain.TourSpot;
import io.swagger.v3.oas.annotations.media.Schema;

public record TourSpotDetailResponse(
        @Schema(description = "관광지 상세 정보")
        TourSpotDetail tourSpot
) {
    public static TourSpotDetailResponse from(TourSpot tourSpot) {
        return new TourSpotDetailResponse(TourSpotDetail.from(tourSpot));
    }
}
