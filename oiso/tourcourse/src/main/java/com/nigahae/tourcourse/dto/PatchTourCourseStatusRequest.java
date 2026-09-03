package com.nigahae.tourcourse.dto;

import com.nigahae.tourcourse.domain.TourCourseStatus;

public record PatchTourCourseStatusRequest(
        TourCourseStatus status
) {
}
