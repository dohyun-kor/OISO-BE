package com.nigahae.tourspot.dto;

public record StoryCardSummaryResponse(
        Long storyId,
        Long spotId,
        String tourSpotName,
        String title,
        String subTitle,
        String summary,
        String imageUrl
) {}
