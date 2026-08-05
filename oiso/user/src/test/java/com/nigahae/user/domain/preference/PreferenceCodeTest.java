package com.nigahae.user.domain.preference;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PreferenceCodeTest {

    @Test
    void travelTimeCodesAreStable() {
        assertThat(TravelTime.HALF_DAY.code()).isEqualTo(1L);
        assertThat(TravelTime.ONE_DAY.code()).isEqualTo(2L);
        assertThat(TravelTime.ONE_NIGHT_TWO_DAYS.code()).isEqualTo(3L);
        assertThat(TravelTime.TWO_NIGHTS_THREE_DAYS.code()).isEqualTo(4L);
        assertThat(TravelTime.THREE_NIGHTS_FOUR_DAYS.code()).isEqualTo(5L);

        for (TravelTime value : TravelTime.values()) {
            assertThat(TravelTime.fromCode(value.code())).isEqualTo(value);
        }
    }

    @Test
    void travelCompanionCodesAreStable() {
        assertThat(TravelCompanion.ALONE.code()).isEqualTo(1L);
        assertThat(TravelCompanion.PARTNER.code()).isEqualTo(2L);
        assertThat(TravelCompanion.FRIENDS.code()).isEqualTo(3L);
        assertThat(TravelCompanion.FAMILY.code()).isEqualTo(4L);

        for (TravelCompanion value : TravelCompanion.values()) {
            assertThat(TravelCompanion.fromCode(value.code())).isEqualTo(value);
        }
    }

    @Test
    void preferredTravelThemeCodesAreStable() {
        assertThat(PreferredTravelTheme.HISTORY_CULTURE.code()).isEqualTo(1L);
        assertThat(PreferredTravelTheme.NATURE_SCENERY.code()).isEqualTo(2L);
        assertThat(PreferredTravelTheme.FOOD.code()).isEqualTo(3L);

        for (PreferredTravelTheme value : PreferredTravelTheme.values()) {
            assertThat(PreferredTravelTheme.fromCode(value.code())).isEqualTo(value);
        }
    }

    @Test
    void transportationModeCodesAreStable() {
        assertThat(TransportationMode.WALK_PUBLIC_TRANSIT.code()).isEqualTo(1L);
        assertThat(TransportationMode.BICYCLE.code()).isEqualTo(2L);
        assertThat(TransportationMode.CAR.code()).isEqualTo(3L);

        for (TransportationMode value : TransportationMode.values()) {
            assertThat(TransportationMode.fromCode(value.code())).isEqualTo(value);
        }
    }
}
