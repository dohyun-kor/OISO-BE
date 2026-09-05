package com.nigahae.tourspot.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record SavedStoryCardListResponse(
        List<Item> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public record Item(
            Long storyId,
            Long spotId,
            String tourSpotName,
            String title,
            String subTitle,
            String summary,
            String imageUrl,
            OffsetDateTime savedAt
    ) {
    }
}
