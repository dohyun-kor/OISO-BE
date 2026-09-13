package com.nigahae.app.recommendation;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.util.List;

public final class RecommendationDtos {
    private RecommendationDtos() {}

    public record RecommendationRequest(
            Object travelTime, Object travelCompanion, Object preferredTravelTheme,
            Object transportationMode, Double latitude, Double longitude,
            Boolean withPet) {}

    public record RecommendationResponse(
            String concept,
            Integer totalSpotCount,
            Integer totalDurationMinute,
            List<RecommendationItem> items) {}

    public record RecommendationItem(
            Integer itemOrder, Integer dayNumber, String category, Long spotId,
            Long nearbyPlaceId, Double latitude, Double longitude,
            String transportType) {}
}
