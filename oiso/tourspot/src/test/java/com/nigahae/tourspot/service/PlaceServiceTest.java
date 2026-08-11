package com.nigahae.tourspot.service;

import com.nigahae.tourspot.domain.PlaceCategory;
import com.nigahae.tourspot.dto.PlaceSummaryRow;
import com.nigahae.tourspot.repository.TourSpotQueryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {

    @Mock TourSpotQueryRepository repository;

    @Test
    void getsRestaurantsForCourseBuilder() {
        PlaceSummaryRow row = mock(PlaceSummaryRow.class);
        when(row.getCategory()).thenReturn("RESTAURANT");
        when(row.getNearbyPlaceId()).thenReturn(101L);
        when(row.getName()).thenReturn("경주 식당");
        when(repository.findPlaces(eq("RESTAURANT"), eq("한식"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(row), PageRequest.of(0, 20), 1));

        var response = new PlaceService(repository)
                .getPlaces(PlaceCategory.RESTAURANT, "  한식  ", 0, 20);

        assertThat(response.places()).hasSize(1);
        assertThat(response.places().get(0).category()).isEqualTo(PlaceCategory.RESTAURANT);
        assertThat(response.places().get(0).spotId()).isNull();
        assertThat(response.places().get(0).nearbyPlaceId()).isEqualTo(101L);
    }
}
