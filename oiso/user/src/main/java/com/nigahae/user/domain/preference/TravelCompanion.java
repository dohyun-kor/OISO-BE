package com.nigahae.user.domain.preference;

import java.util.Arrays;

public enum TravelCompanion {
    ALONE(1L),
    PARTNER(2L),
    FRIENDS(3L),
    FAMILY(4L);

    private final long code;

    TravelCompanion(long code) {
        this.code = code;
    }

    public long code() {
        return code;
    }

    public static TravelCompanion fromCode(Long code) {
        if (code == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(value -> value.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown travel companion code: " + code));
    }
}
