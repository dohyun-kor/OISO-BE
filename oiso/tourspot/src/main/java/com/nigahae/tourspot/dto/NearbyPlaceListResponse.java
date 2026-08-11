package com.nigahae.tourspot.dto;

import java.util.List;

public record NearbyPlaceListResponse(
        List<NearbyPlaceResponse> nearbyPlaces,
        int page,
        int size,
        long totalCount
) {
}
