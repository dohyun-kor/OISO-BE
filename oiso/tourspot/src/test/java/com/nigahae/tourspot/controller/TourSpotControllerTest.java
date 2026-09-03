package com.nigahae.tourspot.controller;

import com.nigahae.common.constant.ErrorCode;
import com.nigahae.common.exception.CustomException;
import com.nigahae.tourspot.dto.DeleteSavedTourSpotResponse;
import com.nigahae.tourspot.dto.NearbyPlaceListResponse;
import com.nigahae.tourspot.dto.PlaceListResponse;
import com.nigahae.tourspot.dto.SaveTourSpotResponse;
import com.nigahae.tourspot.domain.NearbyPlaceCategory;
import com.nigahae.tourspot.domain.PlaceCategory;
import com.nigahae.tourspot.service.TourSpotService;
import com.nigahae.tourspot.service.PlaceService;
import com.nigahae.user.AuthenticatedUser;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TourSpotControllerTest {

    private final TourSpotService service = mock(TourSpotService.class);
    private final PlaceService placeService = mock(PlaceService.class);
    private final TourSpotController controller = new TourSpotController(service, placeService);

    @Test
    void getsPlacesByCategory() {
        PlaceListResponse data = mock(PlaceListResponse.class);
        when(placeService.getPlaces(PlaceCategory.RESTAURANT, "한식", 0, 20))
                .thenReturn(data);

        var response = controller.getTourSpots(PlaceCategory.RESTAURANT, "한식", 0, 20);

        assertThat(response.data()).isEqualTo(data);
        verify(placeService).getPlaces(PlaceCategory.RESTAURANT, "한식", 0, 20);
    }

    @Test
    void getsNearbyPlacesByCategory() {
        NearbyPlaceListResponse data = mock(NearbyPlaceListResponse.class);
        when(service.getNearbyPlaces(1L, NearbyPlaceCategory.ACCOMMODATION, 0, 20))
                .thenReturn(data);

        var response = controller.getNearbyPlaces(
                1L, NearbyPlaceCategory.ACCOMMODATION, 0, 20);

        assertThat(response.data()).isEqualTo(data);
        verify(service).getNearbyPlaces(1L, NearbyPlaceCategory.ACCOMMODATION, 0, 20);
    }

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

    @Test
    void deletesSavedTourSpotForAuthenticatedUser() {
        DeleteSavedTourSpotResponse data = new DeleteSavedTourSpotResponse(1L, false);
        when(service.deleteSavedTourSpot(7L, 1L)).thenReturn(data);

        var response = controller.deleteSavedTourSpot(new AuthenticatedUser(7L, "사용자"), "1");

        assertThat(response.code()).isZero();
        assertThat(response.message()).isEqualTo("저장한 관광지 삭제에 성공했습니다.");
        assertThat(response.data()).isEqualTo(data);
    }

    @Test
    void rejectsUnauthenticatedDeleteRequest() {
        assertThatThrownBy(() -> controller.deleteSavedTourSpot(null, "1"))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.AUTH_ERROR);
    }

}
