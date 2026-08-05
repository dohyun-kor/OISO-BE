package com.nigahae.tourcourse.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CreateTourCourseRequest(
        @Schema(description = "관광 코스 제목(최대 20자)", example = "경주 역사 여행 코스", requiredMode = Schema.RequiredMode.REQUIRED)
        String title,
        @Schema(description = "관광 코스 공유 여부", example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
        Boolean shareYn,
        @Schema(description = "공유 코스 접근 비밀번호(최대 5자리 숫자)", example = "12345", nullable = true)
        String sharedPassword,
        @Schema(description = "관광 코스에 포함될 관광지 목록(1~10개)", requiredMode = Schema.RequiredMode.REQUIRED)
        List<CreateTourCourseItemRequest> items
) {
}
