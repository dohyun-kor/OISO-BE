package com.nigahae.tourcourse.dto;

import java.time.LocalDateTime;

public record TourCourseListRow(
        Long tourCourseId,
        String title,
        boolean shareYn,
        String thumbnailImg,
        long itemCount,
        int totalDistanceMeter,
        int totalDurationSecond,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
