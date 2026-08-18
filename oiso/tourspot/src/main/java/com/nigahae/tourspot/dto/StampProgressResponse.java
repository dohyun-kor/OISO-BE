package com.nigahae.tourspot.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record StampProgressResponse(
        @Schema(description = "완료한 미션 수", example = "2") int clearedCount,
        @Schema(description = "전체 미션 수", example = "3") int totalCount,
        @Schema(description = "전체 미션 완료 여부", example = "false") boolean allCleared,
        List<StampMissionResponse> missions
) {
}
