package com.nigahae.user.controller;

import com.nigahae.common.constant.ErrorCode;
import com.nigahae.common.exception.CustomException;
import com.nigahae.common.exception.GlobalExceptionHandler;
import com.nigahae.user.AuthenticatedUser;
import com.nigahae.user.controller.dto.MyPageResponse;
import com.nigahae.user.service.MyPageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MyPageControllerTest {

    private final MyPageService myPageService = mock(MyPageService.class);
    private final MyPageController controller = new MyPageController(myPageService);
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(
                        authenticatedUserResolver(new AuthenticatedUser(42L, "경주여행자"))
                )
                .build();
    }

    @Test
    void returnsMyPageSummaryInCommonResponse() throws Exception {
        MyPageResponse response = new MyPageResponse(
                new MyPageResponse.Profile(
                        "경주여행자",
                        "https://example.com/profile.jpg"
                ),
                new MyPageResponse.SavedSpotSummary(
                        13L,
                        List.of(
                                new MyPageResponse.SavedSpotItem(
                                        101L,
                                        "https://example.com/spot.jpg"
                                ),
                                new MyPageResponse.SavedSpotItem(
                                        102L,
                                        "https://example.com/spot-2.jpg"
                                )
                        )
                ),
                new MyPageResponse.SavedStorySummary(
                        8L,
                        List.of(
                                new MyPageResponse.SavedStoryItem(
                                        201L,
                                        "https://example.com/story-1.jpg"
                                ),
                                new MyPageResponse.SavedStoryItem(
                                        202L,
                                        "https://example.com/story-2.jpg"
                                ),
                                new MyPageResponse.SavedStoryItem(
                                        203L,
                                        "https://example.com/story-3.jpg"
                                )
                        )
                ),
                new MyPageResponse.MemoSummary(
                        5L,
                        List.of(
                                new MyPageResponse.MemoItem(
                                        301L,
                                        "야경이 아름다웠어요.",
                                        "첨성대 코스"
                                ),
                                new MyPageResponse.MemoItem(
                                        302L,
                                        "다시 방문하고 싶어요.",
                                        "대릉원 코스"
                                )
                        )
                )
        );
        when(myPageService.getMyPage(42L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/me/mypage"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.profile.nickname").value("경주여행자"))
                .andExpect(jsonPath("$.data.profile.profileImageUrl")
                        .value("https://example.com/profile.jpg"))
                .andExpect(jsonPath("$.data.savedSpots.totalCount").value(13))
                .andExpect(jsonPath("$.data.savedSpots.items.length()").value(2))
                .andExpect(jsonPath("$.data.savedSpots.items[0].spotId").value(101))
                .andExpect(jsonPath("$.data.savedStories.totalCount").value(8))
                .andExpect(jsonPath("$.data.savedStories.items.length()").value(3))
                .andExpect(jsonPath("$.data.memos.totalCount").value(5))
                .andExpect(jsonPath("$.data.memos.items.length()").value(2))
                .andExpect(jsonPath("$.data.memos.items[0].memoId").value(301))
                .andExpect(jsonPath("$.data.memos.items[0].content")
                        .value("야경이 아름다웠어요."))
                .andExpect(jsonPath("$.data.memos.items[0].courseTitle")
                        .value("첨성대 코스"));

        verify(myPageService).getMyPage(42L);
    }

    @Test
    void rejectsMissingAuthenticationPrincipal() {
        assertThatThrownBy(() -> controller.getMyPage(null))
                .isInstanceOfSatisfying(
                        CustomException.class,
                        exception -> assertThat(exception.getErrorCode())
                                .isEqualTo(ErrorCode.AUTH_ERROR)
                );

        verifyNoInteractions(myPageService);
    }

    private HandlerMethodArgumentResolver authenticatedUserResolver(AuthenticatedUser user) {
        return new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.hasParameterAnnotation(AuthenticationPrincipal.class)
                        && parameter.getParameterType().equals(AuthenticatedUser.class);
            }

            @Override
            public Object resolveArgument(
                    MethodParameter parameter,
                    ModelAndViewContainer mavContainer,
                    NativeWebRequest webRequest,
                    org.springframework.web.bind.support.WebDataBinderFactory binderFactory
            ) {
                return user;
            }
        };
    }
}
