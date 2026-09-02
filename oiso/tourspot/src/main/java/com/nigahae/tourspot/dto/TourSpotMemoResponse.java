package com.nigahae.tourspot.dto;

import java.time.OffsetDateTime;

public record TourSpotMemoResponse(
        Long spotId,
        Memo memo
) {
    public record Memo(
            Long memoId,
            String content,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
    }
}
