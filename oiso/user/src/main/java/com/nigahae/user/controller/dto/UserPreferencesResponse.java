package com.nigahae.user.controller.dto;

import com.nigahae.user.domain.User;
import com.nigahae.user.domain.preference.PreferredTravelTheme;
import com.nigahae.user.domain.preference.TransportationMode;
import com.nigahae.user.domain.preference.TravelCompanion;
import com.nigahae.user.domain.preference.TravelTime;

import java.math.BigDecimal;

public record UserPreferencesResponse(
        Boolean hasTravelPlan,
        TravelTime travelTime,
        TravelCompanion travelCompanion,
        PreferredTravelTheme preferredTravelTheme,
        TransportationMode transportationMode,
        BigDecimal latitude,
        BigDecimal longitude
) {
    public static UserPreferencesResponse from(User user) {
        return new UserPreferencesResponse(
                user.getHasTravelPlan(),
                TravelTime.fromCode(user.getTravelTime()),
                TravelCompanion.fromCode(user.getTravelCompanion()),
                PreferredTravelTheme.fromCode(user.getPreferredTravelTheme()),
                TransportationMode.fromCode(user.getTransportationMode()),
                toBigDecimal(user.getLatitude()),
                toBigDecimal(user.getLongitude())
        );
    }

    private static BigDecimal toBigDecimal(String value) {
        return value == null ? null : new BigDecimal(value);
    }
}
