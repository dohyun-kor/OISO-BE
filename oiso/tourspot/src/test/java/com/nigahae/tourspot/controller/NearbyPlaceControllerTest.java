package com.nigahae.tourspot.controller;

import com.nigahae.tourspot.domain.NearbyPlaceCategory;
import com.nigahae.tourspot.dto.NearbyPlaceResponse;
import com.nigahae.tourspot.service.TourSpotService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NearbyPlaceControllerTest {

    @Test
    void getsNearbyPlaceDetailByGlobalId() {
        TourSpotService service = mock(TourSpotService.class);
        NearbyPlaceResponse response = new NearbyPlaceResponse(
                101L, 35L, NearbyPlaceCategory.RESTAURANT, "경주 식당",
                null, "경주시", null, null, 39,
                129.1, 35.1, null, null, null, null,
                null, null, null, null);
        when(service.getNearbyPlace(101L)).thenReturn(response);

        var result = new NearbyPlaceController(service).getNearbyPlace(101L);

        assertThat(result.data()).isEqualTo(response);
        verify(service).getNearbyPlace(101L);
    }
}
