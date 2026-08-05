package com.nigahae.tourspot.dto;

import com.nigahae.tourspot.domain.TourSpot;
import io.swagger.v3.oas.annotations.media.Schema;

public record TourSpotDetail(
        @Schema(description = "관광지 ID", example = "1")
        Long spotId,
        @Schema(description = "관광지 이름", example = "불국사")
        String name,
        @Schema(description = "대표 이미지 URL")
        String img,
        @Schema(description = "관광지 상세 설명")
        String content,
        @Schema(description = "관광지 주소", example = "경상북도 경주시 불국로 385")
        String address,
        @Schema(description = "주차 가능 여부", example = "true")
        Boolean canParking,
        @Schema(description = "반려동물 동반 가능 여부", example = "false")
        Boolean canPet,
        @Schema(description = "즐겨찾기 여부. 즐겨찾기 기능 연동 전까지 false", example = "false")
        boolean like,
        @Schema(description = "관광지 혼잡도", example = "2")
        Integer crowdLevel,
        @Schema(description = "관광지 요약 설명")
        String overview,
        @Schema(description = "관광지 홈페이지 URL")
        String homepage,
        @Schema(description = "경도", example = "129.3319")
        Double mapX,
        @Schema(description = "위도", example = "35.7900")
        Double mapY,
        @Schema(description = "한국관광공사 콘텐츠 ID", example = "126508")
        Long contentId,
        @Schema(description = "한국관광공사 콘텐츠 유형 ID", example = "12")
        Integer contentTypeId,
        @Schema(description = "관광지 전화번호", example = "054-746-9913")
        String tel
) {
    public static TourSpotDetail from(TourSpot tourSpot) {
        return new TourSpotDetail(
                tourSpot.getSpotId(),
                tourSpot.getName(),
                tourSpot.getImg(),
                tourSpot.getContent(),
                tourSpot.getAddress(),
                tourSpot.getCanParking(),
                tourSpot.getCanPet(),
                false,
                tourSpot.getCrowdLevel(),
                tourSpot.getOverview(),
                tourSpot.getHomepage(),
                tourSpot.getMapX(),
                tourSpot.getMapY(),
                tourSpot.getContentId(),
                tourSpot.getContentTypeId() == null ? null : tourSpot.getContentTypeId().intValue(),
                tourSpot.getTel()
        );
    }
}
