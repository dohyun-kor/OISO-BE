package com.nigahae.tourcourse.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record UpdateTourCourseRequest(
        String title,
        Boolean shareYn,
        @Schema(nullable = true) String sharedPassword,
        List<UpdateTourCourseItemRequest> items
) {
}
