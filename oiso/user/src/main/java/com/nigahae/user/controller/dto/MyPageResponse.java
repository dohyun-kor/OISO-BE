package com.nigahae.user.controller.dto;

import java.util.List;

public record MyPageResponse(
        Profile profile,
        SavedSpotSummary savedSpots,
        SavedStorySummary savedStories,
        MemoSummary memos
) {
    public record Profile(
            String nickname,
            String profileImageUrl
    ) {
    }

    public record SavedSpotSummary(
            long totalCount,
            List<SavedSpotItem> items
    ) {
    }

    public record SavedSpotItem(
            Long spotId,
            String imageUrl
    ) {
    }

    public record SavedStorySummary(
            long totalCount,
            List<SavedStoryItem> items
    ) {
    }

    public record SavedStoryItem(
            Long storyId,
            String imageUrl
    ) {
    }

    public record MemoSummary(
            long totalCount,
            List<MemoItem> items
    ) {
    }

    public record MemoItem(
            Long memoId,
            Long spotId,
            String content,
            String spotName,
            String spotImage,
            java.time.OffsetDateTime updatedAt
    ) {
        public MemoItem(Long memoId, String content, String spotName) {
            this(memoId, null, content, spotName, null, null);
        }
    }
}
