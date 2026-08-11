package com.nigahae.tourcourse.dto;

import com.nigahae.tourcourse.domain.TourCourse;

import java.time.LocalDateTime;
import java.util.List;

public record TourCourseDetailResponse(
        Long tourCourseId,
        String title,
        boolean shareYn,
        List<TourCourseItemDetailResponse> items,
        int totalDistanceMeter,
        int totalDurationSecond,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TourCourseDetailResponse from(
            TourCourse course,
            List<TourCourseItemDetailResponse> items
    ) {
        int totalDistanceMeter = items.stream()
                .map(TourCourseItemDetailResponse::distanceMeter)
                .filter(value -> value != null)
                .mapToInt(Integer::intValue)
                .sum();
        int totalDurationSecond = items.stream()
                .map(TourCourseItemDetailResponse::durationSecond)
                .filter(value -> value != null)
                .mapToInt(Integer::intValue)
                .sum();
        return new TourCourseDetailResponse(
                course.getTourCourseId(),
                course.getTitle(),
                course.isShareYn(),
                items,
                totalDistanceMeter,
                totalDurationSecond,
                course.getCreatedAt(),
                course.getUpdatedAt()
        );
    }
}
