package com.nigahae.tourspot.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record TourSpotListResponse(
        @Schema(description = "관광지 목록")
        List<TourSpotSummary> tourSpots,
        @Schema(description = "현재 페이지 번호", example = "0")
        int page,
        @Schema(description = "페이지 크기", example = "20")
        int size,
        @Schema(description = "검색 조건에 해당하는 전체 관광지 개수", example = "152")
        long totalCount
) {
}
