package com.nigahae.tourspot.service;

import com.nigahae.common.constant.ErrorCode;
import com.nigahae.common.exception.CustomException;
import com.nigahae.tourspot.dto.SavedStoryCardListResponse;
import com.nigahae.tourspot.dto.StoryCardDetailResponse;
import com.nigahae.tourspot.repository.StoryCardRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StoryCardServiceTest {

    private final StoryCardRepository repository = mock(StoryCardRepository.class);
    private final StoryCardService service = new StoryCardService(repository);

    @Test
    void savesExistingStoryCardForUser() {
        when(repository.existsById(3L)).thenReturn(true);

        var response = service.save(7L, 3L);

        assertThat(response.storyId()).isEqualTo(3L);
        assertThat(response.saved()).isTrue();
        verify(repository).saveIfAbsent(7L, 3L);
    }

    @Test
    void rejectsUnknownStoryCard() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.save(7L, 99L))
                .isInstanceOfSatisfying(CustomException.class,
                        exception -> assertThat(exception.getErrorCode())
                                .isEqualTo(ErrorCode.RESOURCE_NOT_FOUND));
    }

    @Test
    void returnsSavedStoryCards() {
        SavedStoryCardListResponse expected =
                new SavedStoryCardListResponse(List.of(), 0, 20, 0, 0);
        when(repository.findSaved(7L, 0, 20)).thenReturn(expected);

        assertThat(service.getSaved(7L, 0, 20)).isEqualTo(expected);
    }

    @Test
    void returnsStoryCardDetailWithMySavedState() {
        StoryCardDetailResponse expected = new StoryCardDetailResponse(
                3L, 24L, "동궁과 월지", "달빛 이야기", "신라의 밤",
                "요약", "도입", "하늘을 읽는 신라인들", "본문", "image.jpg",
                "해 질 무렵 추천", "알고 있었나요 내용",
                List.of("야경", "신라"), true);
        when(repository.findBySpotId(7L, 24L)).thenReturn(Optional.of(expected));

        assertThat(service.getDetailBySpotId(7L, 24L)).isEqualTo(expected);
    }
}
