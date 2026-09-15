package com.nigahae.tourspot.service;

import com.nigahae.common.constant.ErrorCode;
import com.nigahae.common.exception.CustomException;
import com.nigahae.tourspot.dto.SavedStoryCardListResponse;
import com.nigahae.tourspot.dto.SavedStoryCardResponse;
import com.nigahae.tourspot.dto.StoryCardDetailResponse;
import com.nigahae.tourspot.dto.StoryCardSummaryResponse;
import java.util.List;
import com.nigahae.tourspot.repository.StoryCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoryCardService {

    private final StoryCardRepository storyCardRepository;

    public List<StoryCardSummaryResponse> getRandomCards(int size) {
        if (size < 1 || size > 20) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, Map.of("field", "size"));
        }
        return storyCardRepository.findRandom(size);
    }

    @Transactional
    public SavedStoryCardResponse save(Long userId, Long storyId) {
        validateStoryCard(storyId);
        storyCardRepository.saveIfAbsent(userId, storyId);
        return new SavedStoryCardResponse(storyId, true);
    }

    @Transactional
    public SavedStoryCardResponse delete(Long userId, Long storyId) {
        validateStoryCard(storyId);
        storyCardRepository.delete(userId, storyId);
        return new SavedStoryCardResponse(storyId, false);
    }

    public SavedStoryCardListResponse getSaved(Long userId, int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR);
        }
        return storyCardRepository.findSaved(userId, page, size);
    }

    public StoryCardDetailResponse getDetailBySpotId(Long userId, Long spotId) {
        if (spotId == null || spotId < 1) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, Map.of("field", "spotId"));
        }
        return storyCardRepository.findBySpotId(userId, spotId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.RESOURCE_NOT_FOUND, Map.of("spotId", spotId)));
    }

    private void validateStoryCard(Long storyId) {
        if (storyId == null || storyId < 1) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, Map.of("field", "storyId"));
        }
        if (!storyCardRepository.existsById(storyId)) {
            throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND, Map.of("storyId", storyId));
        }
    }
}
