package com.nigahae.tourspot.dto;

import com.nigahae.tourspot.domain.TourSpot;
import io.swagger.v3.oas.annotations.media.Schema;

public record TourSpotDetailResponse(
        @Schema(description = "관광지 상세 정보")
        TourSpotDetail tourSpot,
        @Schema(description = "로그인 사용자의 스탬프 진행 상태. 비로그인 조회 시 null", nullable = true)
        StampProgressResponse stampProgress
) {
    public static TourSpotDetailResponse from(TourSpot tourSpot) {
        return from(tourSpot, null);
    }

    public static TourSpotDetailResponse from(TourSpot tourSpot, StampProgressResponse stampProgress) {
        return from(tourSpot, stampProgress, false);
    }

    public static TourSpotDetailResponse from(TourSpot tourSpot, StampProgressResponse stampProgress, boolean like) {
        return new TourSpotDetailResponse(TourSpotDetail.from(tourSpot, like), stampProgress);
    }
}
