package com.nigahae.tourcourse.dto;

import com.nigahae.tourcourse.domain.TourCourse;

import java.time.LocalDateTime;
import java.util.List;

public record TourCourseDetailResponse(
        Long tourCourseId,
        String title,
        boolean shareYn,
        List<TourCourseItemDetailResponse> items,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public TourCourseDetailResponse(
            Long tourCourseId, String title, boolean shareYn,
            List<TourCourseItemDetailResponse> items,
            int ignoredDistance, int ignoredDuration,
            LocalDateTime createdAt, LocalDateTime updatedAt
    ) {
        this(tourCourseId, title, shareYn, items, createdAt, updatedAt);
    }

    public static TourCourseDetailResponse from(
            TourCourse course,
            List<TourCourseItemDetailResponse> items
    ) {
        return new TourCourseDetailResponse(
                course.getTourCourseId(),
                course.getTitle(),
                course.isShareYn(),
                items,
                course.getCreatedAt(),
                course.getUpdatedAt()
        );
    }
}
