package com.nigahae.tourcourse.dto;

import com.nigahae.tourcourse.domain.TourCourseItemStatus;

public record PatchTourCourseItemRequest(
        TourCourseItemStatus status
) {
}
