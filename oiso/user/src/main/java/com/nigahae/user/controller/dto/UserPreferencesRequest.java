package com.nigahae.user.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nigahae.user.domain.preference.PreferredTravelTheme;
import com.nigahae.user.domain.preference.TransportationMode;
import com.nigahae.user.domain.preference.TravelCompanion;
import com.nigahae.user.domain.preference.TravelTime;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UserPreferencesRequest(
        @NotNull(message = "여행 계획 여부는 필수입니다.")
        Boolean hasTravelPlan,

        @NotNull(message = "여행 일정은 필수입니다.")
        TravelTime travelTime,

        @NotNull(message = "동행 유형은 필수입니다.")
        TravelCompanion travelCompanion,

        PreferredTravelTheme preferredTravelTheme,

        @NotNull(message = "이동 수단은 필수입니다.")
        TransportationMode transportationMode,

        @DecimalMin(value = "-90.0", message = "위도는 -90 이상이어야 합니다.")
        @DecimalMax(value = "90.0", message = "위도는 90 이하여야 합니다.")
        BigDecimal latitude,

        @DecimalMin(value = "-180.0", message = "경도는 -180 이상이어야 합니다.")
        @DecimalMax(value = "180.0", message = "경도는 180 이하여야 합니다.")
        BigDecimal longitude
) {
    @JsonIgnore
    @AssertTrue(message = "위도와 경도는 함께 입력해야 합니다.")
    public boolean isCoordinatePairValid() {
        return (latitude == null) == (longitude == null);
    }
}
