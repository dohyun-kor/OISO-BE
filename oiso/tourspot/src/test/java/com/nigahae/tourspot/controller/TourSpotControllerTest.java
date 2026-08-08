package com.nigahae.tourspot.controller;

import com.nigahae.common.constant.ErrorCode;
import com.nigahae.common.exception.CustomException;
import com.nigahae.tourspot.dto.SaveTourSpotResponse;
import com.nigahae.tourspot.service.TourSpotService;
import com.nigahae.user.AuthenticatedUser;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TourSpotControllerTest {

    private final TourSpotService service = mock(TourSpotService.class);
    private final TourSpotController controller = new TourSpotController(service);

    @Test
    void savesTourSpotForAuthenticatedUser() {
        SaveTourSpotResponse data = new SaveTourSpotResponse(1L, true);
        when(service.saveTourSpot(7L, 1L)).thenReturn(data);

        var response = controller.saveTourSpot(new AuthenticatedUser(7L, "사용자"), "1");

        assertThat(response.code()).isZero();
        assertThat(response.message()).isEqualTo("관광지 저장에 성공했습니다.");
        assertThat(response.data()).isEqualTo(data);
    }

    @Test
    void rejectsUnauthenticatedUser() {
        assertThatThrownBy(() -> controller.saveTourSpot(null, "1"))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.AUTH_ERROR);
    }
}
