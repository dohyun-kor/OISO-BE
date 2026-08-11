package com.nigahae.tourspot.dto;

import java.util.List;

public record PlaceListResponse(
        List<PlaceSummaryResponse> places,
        int page,
        int size,
        long totalCount
) {
}
