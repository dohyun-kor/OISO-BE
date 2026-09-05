package com.nigahae.tourspot.dto;

import java.util.List;

public record StoryCardDetailResponse(
        Long storyId,
        Long spotId,
        String tourSpotName,
        String title,
        String subTitle,
        String summary,
        String intro,
        String storyTitle,
        String story,
        String imageUrl,
        String tip,
        String didYouKnow,
        List<String> hashtags,
        boolean saved
) {
}
