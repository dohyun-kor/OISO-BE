package com.nigahae.tourcourse.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record TourCourseSummaryResponse(
        @Schema(example = "1") Long tourCourseId,
        @Schema(example = "경주 역사 여행 코스") String title,
        @Schema(example = "false") boolean shareYn,
        @Schema(example = "https://cdn.example.com/bulguksa.jpg", nullable = true) String thumbnailImg,
        @Schema(example = "3") int itemCount,
        @Schema(example = "13500") int totalDistanceMeter,
        @Schema(example = "2300") int totalDurationSecond,
        @Schema(example = "2026-07-11T15:00:00") LocalDateTime createdAt,
        @Schema(example = "2026-07-11T15:00:00") LocalDateTime updatedAt
) {
    public static TourCourseSummaryResponse from(TourCourseListRow row) {
        return new TourCourseSummaryResponse(
                row.tourCourseId(),
                row.title(),
                row.shareYn(),
                row.thumbnailImg(),
                Math.toIntExact(row.itemCount()),
                row.totalDistanceMeter(),
                row.totalDurationSecond(),
                row.createdAt(),
                row.updatedAt()
        );
    }
}
