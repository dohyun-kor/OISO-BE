package com.nigahae.tourspot.dto;

import java.time.OffsetDateTime;

public record TourSpotMemoResponse(
        Long spotId,
        Memo memo
) {
    public record Memo(
            Long memoId,
            String content,
            String imageUrl,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        public Memo(Long memoId, String content, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
            this(memoId, content, null, createdAt, updatedAt);
        }
    }
}
