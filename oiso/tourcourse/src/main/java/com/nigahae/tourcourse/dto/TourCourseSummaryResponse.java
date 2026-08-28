package com.nigahae.tourcourse.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.nigahae.tourcourse.domain.TourCourseStatus;

import java.time.LocalDateTime;

public record TourCourseSummaryResponse(
        @Schema(example = "1") Long tourCourseId,
        @Schema(example = "경주 역사 여행 코스") String title,
        @Schema(example = "false") boolean shareYn,
        @Schema(example = "IN_PROGRESS") TourCourseStatus status,
        @Schema(example = "https://cdn.example.com/bulguksa.jpg", nullable = true) String thumbnailImg,
        @Schema(example = "3") int itemCount,
        @Schema(example = "2026-07-11T15:00:00") LocalDateTime createdAt,
        @Schema(example = "2026-07-11T15:00:00") LocalDateTime updatedAt
) {
    public TourCourseSummaryResponse(
            Long tourCourseId, String title, boolean shareYn, String thumbnailImg,
            int itemCount, int ignoredDistance, int ignoredDuration,
            LocalDateTime createdAt, LocalDateTime updatedAt
    ) {
        this(tourCourseId, title, shareYn, TourCourseStatus.PENDING,
                thumbnailImg, itemCount, createdAt, updatedAt);
    }

    public TourCourseSummaryResponse(
            Long tourCourseId, String title, boolean shareYn, String thumbnailImg,
            int itemCount, LocalDateTime createdAt, LocalDateTime updatedAt
    ) {
        this(tourCourseId, title, shareYn, TourCourseStatus.PENDING,
                thumbnailImg, itemCount, createdAt, updatedAt);
    }

    public static TourCourseSummaryResponse from(TourCourseListRow row) {
        return new TourCourseSummaryResponse(
                row.tourCourseId(),
                row.title(),
                row.shareYn(),
                row.status(),
                row.thumbnailImg(),
                Math.toIntExact(row.itemCount()),
                row.createdAt(),
                row.updatedAt()
        );
    }
}
