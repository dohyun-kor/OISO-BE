package com.nigahae.tourspot.repository;

import com.nigahae.tourspot.dto.SavedStoryCardListResponse;
import com.nigahae.tourspot.dto.StoryCardDetailResponse;
import com.nigahae.tourspot.dto.StoryCardSummaryResponse;
import java.util.List;

import java.util.Optional;

public interface StoryCardRepository {
    List<StoryCardSummaryResponse> findRandom(int size);

    boolean existsById(Long storyId);

    void saveIfAbsent(Long userId, Long storyId);

    void delete(Long userId, Long storyId);

    SavedStoryCardListResponse findSaved(Long userId, int page, int size);

    Optional<StoryCardDetailResponse> findBySpotId(Long userId, Long spotId);
}
