package com.nigahae.tourdata.controller.dto;

import com.nigahae.tourdata.domain.FestivalInfo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(name = "FestivalResponse", description = "축제 목록 항목")
public record FestivalResponse(
        @Schema(description = "축제 식별자", example = "300")
        Long spotId,

        @Schema(description = "축제 이름", example = "2026 봉황대 뮤직 스퀘어")
        String name,

        @Schema(description = "대표 이미지 URL", example = "https://example.com/festival.jpg")
        String img,

        @Schema(description = "축제 시작일", example = "2026-06-05")
        LocalDate eventStartDate,

        @Schema(description = "축제 종료일", example = "2026-08-28")
        LocalDate eventEndDate,

        @Schema(description = "축제 진행 상태", example = "ONGOING")
        FestivalStatus status
) {

    public static FestivalResponse from(FestivalInfo festival, LocalDate today) {
        FestivalStatus status = festival.getEventStartDate().isAfter(today)
                ? FestivalStatus.UPCOMING
                : FestivalStatus.ONGOING;

        return new FestivalResponse(
                festival.getSpotId(),
                festival.getName(),
                festival.getImg(),
                festival.getEventStartDate(),
                festival.getEventEndDate(),
                status
        );
    }
}
