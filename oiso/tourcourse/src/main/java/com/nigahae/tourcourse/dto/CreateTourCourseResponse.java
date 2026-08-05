package com.nigahae.tourcourse.dto;

import com.nigahae.tourcourse.domain.TourCourse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record CreateTourCourseResponse(
        @Schema(example = "1") Long tourCourseId,
        @Schema(example = "경주 역사 여행 코스") String title,
        @Schema(example = "false") boolean shareYn,
        @Schema(example = "3") int itemCount,
        @Schema(example = "2026-07-11T15:00:00") LocalDateTime createdAt
) {
    public static CreateTourCourseResponse from(TourCourse course) {
        return new CreateTourCourseResponse(
                course.getTourCourseId(),
                course.getTitle(),
                course.isShareYn(),
                course.getItems().size(),
                course.getCreatedAt()
        );
    }
}
