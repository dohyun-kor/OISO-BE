package com.nigahae.tourspot.controller;

import com.nigahae.common.constant.ErrorCode;
import com.nigahae.common.exception.CustomException;
import com.nigahae.tourspot.dto.TourSpotMemoRequest;
import com.nigahae.tourspot.dto.TourSpotMemoResponse;
import com.nigahae.tourspot.dto.TourSpotMemoListResponse;
import com.nigahae.tourspot.service.TourSpotService;
import com.nigahae.user.AuthenticatedUser;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MemoControllerTest {

    private final TourSpotService service = mock(TourSpotService.class);
    private final MemoController controller = new MemoController(service);

    @Test
    void savesMemoForAuthenticatedUser() {
        OffsetDateTime now = OffsetDateTime.now();
        TourSpotMemoResponse data = new TourSpotMemoResponse(
                24L, new TourSpotMemoResponse.Memo(3L, "해 질 무렵 방문", now, now));
        when(service.saveMemo(7L, 24L, "해 질 무렵 방문", "https://example.com/memo.jpg"))
                .thenReturn(data);

        var response = controller.saveMemo(
                new AuthenticatedUser(7L, "사용자"),
                "24",
                new TourSpotMemoRequest("해 질 무렵 방문", "https://example.com/memo.jpg")
        );

        assertThat(response.data()).isEqualTo(data);
        verify(service).saveMemo(
                7L, 24L, "해 질 무렵 방문", "https://example.com/memo.jpg");
    }

    @Test
    void getsMyMemoList() {
        TourSpotMemoListResponse data = new TourSpotMemoListResponse(
                List.of(), 0, 20, 0, 0);
        when(service.getMemos(7L, 0, 20)).thenReturn(data);

        var response = controller.getMemos(new AuthenticatedUser(7L, "사용자"), 0, 20);

        assertThat(response.data()).isEqualTo(data);
        verify(service).getMemos(7L, 0, 20);
    }

    @Test
    void rejectsUnauthenticatedMemoRequest() {
        assertThatThrownBy(() -> controller.getMemo(null, "24"))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.AUTH_ERROR);
    }
    @Test
    void rejectsUnauthenticatedNearbyMemoOperations() {
        assertThatThrownBy(() -> controller.saveNearbyMemo(null, 101L, new TourSpotMemoRequest("memo")))
                .isInstanceOf(CustomException.class);
        assertThatThrownBy(() -> controller.getNearbyMemo(null, 101L))
                .isInstanceOf(CustomException.class);
        assertThatThrownBy(() -> controller.deleteNearbyMemo(null, 101L))
                .isInstanceOf(CustomException.class);
    }
}
