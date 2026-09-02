package com.nigahae.tourspot.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record TourSpotMemoListResponse(
        List<Item> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public record Item(
            Long memoId,
            Long spotId,
            String spotName,
            String spotImage,
            String content,
            String imageUrl,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
    }
}
