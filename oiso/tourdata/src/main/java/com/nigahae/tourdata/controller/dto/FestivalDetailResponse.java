package com.nigahae.tourdata.controller.dto;

import com.nigahae.tourdata.domain.FestivalInfo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(name = "FestivalDetailResponse", description = "축제 상세 정보")
public record FestivalDetailResponse(
        @Schema(description = "축제 식별자", example = "300")
        Long spotId,

        @Schema(description = "축제 이름", example = "2026 봉황대 뮤직 스퀘어")
        String name,

        @Schema(description = "대표 이미지 URL", example = "https://example.com/festival.jpg")
        String img,

        @Schema(description = "축제 주소", example = "경상북도 경주시 봉황로 30")
        String address,

        @Schema(description = "축제 상세 소개")
        String overview,

        @Schema(description = "축제 홈페이지")
        String homepage,

        @Schema(description = "지도 X 좌표(경도)", example = "129.2123")
        Double mapX,

        @Schema(description = "지도 Y 좌표(위도)", example = "35.8451")
        Double mapY,

        @Schema(description = "한국관광공사 콘텐츠 식별자", example = "300")
        Long contentId,

        @Schema(description = "한국관광공사 콘텐츠 유형 식별자", example = "15")
        Long contentTypeId,

        @Schema(description = "문의 전화번호", example = "054-000-0000")
        String tel,

        @Schema(description = "축제 시작일", example = "2026-06-05")
        LocalDate eventStartDate,

        @Schema(description = "축제 종료일", example = "2026-08-28")
        LocalDate eventEndDate
) {

    public static FestivalDetailResponse from(FestivalInfo festival) {
        return new FestivalDetailResponse(
                festival.getSpotId(),
                festival.getName(),
                festival.getImg(),
                festival.getAddress(),
                festival.getOverview(),
                festival.getHomepage(),
                festival.getMapX(),
                festival.getMapY(),
                festival.getContentId(),
                festival.getContentTypeId(),
                festival.getTel(),
                festival.getEventStartDate(),
                festival.getEventEndDate()
        );
    }
}
