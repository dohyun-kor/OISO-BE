package com.nigahae.user.domain.preference;

import java.util.Arrays;

public enum PreferredTravelTheme {
    HISTORY_CULTURE(1L),
    NATURE_SCENERY(2L),
    FOOD(3L);

    private final long code;

    PreferredTravelTheme(long code) {
        this.code = code;
    }

    public long code() {
        return code;
    }

    public static PreferredTravelTheme fromCode(Long code) {
        if (code == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(value -> value.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown preferred travel theme code: " + code));
    }
}
