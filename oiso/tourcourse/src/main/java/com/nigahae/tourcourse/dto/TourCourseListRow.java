package com.nigahae.tourcourse.dto;

import java.time.LocalDateTime;

public record TourCourseListRow(
        Long tourCourseId,
        String title,
        boolean shareYn,
        String thumbnailImg,
        long itemCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public TourCourseListRow(
            Long tourCourseId, String title, boolean shareYn, String thumbnailImg,
            long itemCount, int ignoredDistance, int ignoredDuration,
            LocalDateTime createdAt, LocalDateTime updatedAt
    ) {
        this(tourCourseId, title, shareYn, thumbnailImg, itemCount, createdAt, updatedAt);
    }
}
