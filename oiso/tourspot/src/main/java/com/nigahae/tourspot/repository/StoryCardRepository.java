package com.nigahae.tourspot.repository;

import com.nigahae.tourspot.dto.SavedStoryCardListResponse;
import com.nigahae.tourspot.dto.StoryCardDetailResponse;

import java.util.Optional;

public interface StoryCardRepository {
    boolean existsById(Long storyId);

    void saveIfAbsent(Long userId, Long storyId);

    void delete(Long userId, Long storyId);

    SavedStoryCardListResponse findSaved(Long userId, int page, int size);

    Optional<StoryCardDetailResponse> findBySpotId(Long userId, Long spotId);
}
