package com.nigahae.tourcourse.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record TourCourseListResponse(
        List<TourCourseSummaryResponse> tourCourses,
        @Schema(example = "0") int page,
        @Schema(example = "10") int size,
        @Schema(example = "1") long totalCount
) {
}
