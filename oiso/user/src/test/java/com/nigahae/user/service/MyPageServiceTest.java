package com.nigahae.user.service;

import com.nigahae.common.constant.ErrorCode;
import com.nigahae.common.exception.CustomException;
import com.nigahae.user.controller.dto.MyPageResponse;
import com.nigahae.user.domain.User;
import com.nigahae.user.repository.MyPageQueryRepository;
import com.nigahae.user.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class MyPageServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final MyPageQueryRepository myPageQueryRepository = mock(MyPageQueryRepository.class);
    private final MyPageService service = new MyPageService(userRepository, myPageQueryRepository);

    @Test
    void returnsOnlyMyPageSummaryData() {
        User user = User.builder()
                .userId(42L)
                .nickname("경주여행자")
                .profileImageUrl("https://example.com/profile.jpg")
                .build();
        when(userRepository.findById(42L)).thenReturn(Optional.of(user));
        when(myPageQueryRepository.findCounts(42L))
                .thenReturn(new MyPageQueryRepository.Counts(13L, 8L, 5L));
        when(myPageQueryRepository.findSavedSpots(42L))
                .thenReturn(List.of(
                        new MyPageQueryRepository.SavedSpotItemRow(
                                101L,
                                "https://example.com/spot.jpg"
                        ),
                        new MyPageQueryRepository.SavedSpotItemRow(
                                102L,
                                "https://example.com/spot-2.jpg"
                        )
                ));
        when(myPageQueryRepository.findSavedStories(42L))
                .thenReturn(List.of(
                        new MyPageQueryRepository.SavedStoryItemRow(
                                201L,
                                "https://example.com/story-1.jpg"
                        ),
                        new MyPageQueryRepository.SavedStoryItemRow(
                                202L,
                                "https://example.com/story-2.jpg"
                        ),
                        new MyPageQueryRepository.SavedStoryItemRow(
                                203L,
                                "https://example.com/story-3.jpg"
                        )
                ));
        when(myPageQueryRepository.findMemos(42L))
                .thenReturn(List.of(
                        new MyPageQueryRepository.MemoItemRow(
                                301L,
                                "야경이 아름다웠어요.",
                                "첨성대 코스"
                        ),
                        new MyPageQueryRepository.MemoItemRow(
                                302L,
                                "다시 방문하고 싶어요.",
                                "대릉원 코스"
                        ),
                        new MyPageQueryRepository.MemoItemRow(
                                303L,
                                "산책하기 좋았어요.",
                                "황리단길 코스"
                        )
                ));

        MyPageResponse response = service.getMyPage(42L);

        assertThat(response.profile().nickname()).isEqualTo("경주여행자");
        assertThat(response.profile().profileImageUrl())
                .isEqualTo("https://example.com/profile.jpg");
        assertThat(response.savedSpots().totalCount()).isEqualTo(13L);
        assertThat(response.savedSpots().items()).containsExactly(
                new MyPageResponse.SavedSpotItem(
                        101L,
                        "https://example.com/spot.jpg"
                ),
                new MyPageResponse.SavedSpotItem(
                        102L,
                        "https://example.com/spot-2.jpg"
                )
        );
        assertThat(response.savedStories().totalCount()).isEqualTo(8L);
        assertThat(response.savedStories().items()).hasSize(3);
        assertThat(response.memos().totalCount()).isEqualTo(5L);
        assertThat(response.memos().items()).hasSize(3);
        assertThat(response.memos().items().get(0))
                .isEqualTo(new MyPageResponse.MemoItem(
                        301L,
                        "야경이 아름다웠어요.",
                        "첨성대 코스"
                ));
    }

    @Test
    void rejectsUnknownUserBeforeQueryingMyPageTables() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getMyPage(99L))
                .isInstanceOfSatisfying(
                        CustomException.class,
                        exception -> assertThat(exception.getErrorCode())
                                .isEqualTo(ErrorCode.USER_NOT_FOUND)
                );

        verifyNoInteractions(myPageQueryRepository);
    }
}
