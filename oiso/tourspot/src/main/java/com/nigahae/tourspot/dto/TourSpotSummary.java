package com.nigahae.tourspot.dto;

import com.nigahae.tourspot.domain.TourSpot;
import io.swagger.v3.oas.annotations.media.Schema;

public record TourSpotSummary(
        @Schema(description = "관광지 ID", example = "1")
        Long spotId,
        @Schema(description = "관광지 이름", example = "불국사")
        String name,
        @Schema(description = "대표 이미지 URL", example = "https://oiso.s3.ap-northeast-2.amazonaws.com/tourspot/bulguksa.jpg")
        String img,
        @Schema(description = "관광지 요약 설명", example = "신라 불교문화의 정수를 보여주는 대표 문화유산")
        String overview,
        @Schema(description = "관광지 주소", example = "경상북도 경주시 불국로 385")
        String address,
        @Schema(description = "즐겨찾기 여부. 즐겨찾기 기능 연동 전까지 false", example = "false")
        boolean like
) {
    public static TourSpotSummary from(TourSpot tourSpot) {
        return new TourSpotSummary(
                tourSpot.getSpotId(),
                tourSpot.getLocalizedName(),
                tourSpot.getImg(),
                tourSpot.getLocalizedOverview(),
                tourSpot.getLocalizedAddress(),
                false
        );
    }
}
