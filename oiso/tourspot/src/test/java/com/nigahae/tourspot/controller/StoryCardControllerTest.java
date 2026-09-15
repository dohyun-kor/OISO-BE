package com.nigahae.tourspot.controller;

import com.nigahae.common.constant.ErrorCode;
import com.nigahae.common.exception.CustomException;
import com.nigahae.tourspot.dto.SavedStoryCardListResponse;
import com.nigahae.tourspot.dto.SavedStoryCardResponse;
import com.nigahae.tourspot.dto.StoryCardDetailResponse;
import com.nigahae.tourspot.service.StoryCardService;
import com.nigahae.user.AuthenticatedUser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StoryCardControllerTest {

    private final StoryCardService service = mock(StoryCardService.class);
    private final StoryCardController controller = new StoryCardController(service);

    @Test
    void savesStoryCardForAuthenticatedUser() {
        SavedStoryCardResponse data = new SavedStoryCardResponse(3L, true);
        when(service.save(7L, 3L)).thenReturn(data);

        var response = controller.save(new AuthenticatedUser(7L, "사용자"), 3L);

        assertThat(response.data()).isEqualTo(data);
        verify(service).save(7L, 3L);
    }

    @Test
    void returnsMySavedStoryCards() {
        SavedStoryCardListResponse data =
                new SavedStoryCardListResponse(List.of(), 0, 20, 0, 0);
        when(service.getSaved(7L, 0, 20)).thenReturn(data);

        var response = controller.getSaved(new AuthenticatedUser(7L, "사용자"), 0, 20);

        assertThat(response.data()).isEqualTo(data);
        verify(service).getSaved(7L, 0, 20);
    }

    @Test
    void rejectsUnauthenticatedUser() {
        assertThatThrownBy(() -> controller.getSaved(null, 0, 20))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.AUTH_ERROR);
    }

    @Test
    void returnsStoryCardDetail() {
        StoryCardDetailResponse data = mock(StoryCardDetailResponse.class);
        when(service.getDetailBySpotId(7L, 24L)).thenReturn(data);

        var response = controller.getDetail(new AuthenticatedUser(7L, "사용자"), 24L);

        assertThat(response.data()).isEqualTo(data);
        verify(service).getDetailBySpotId(7L, 24L);
    }
    @Test
    void featuredEndpointWorksWithoutAuthenticationAndUsesDefaultSize() throws Exception {
        when(service.getRandomCards(5)).thenReturn(List.of(
                new com.nigahae.tourspot.dto.StoryCardSummaryResponse(
                        3L, 24L, "spot", "title", "subtitle", "summary", "image.jpg")));
        var mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders
                .standaloneSetup(controller).build();
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .get("/api/v1/story-cards/featured"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.data[0].storyId").value(3))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.data[0].imageUrl").value("image.jpg"));
        verify(service).getRandomCards(5);
    }
}
