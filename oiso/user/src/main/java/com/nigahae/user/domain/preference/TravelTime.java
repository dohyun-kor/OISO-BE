package com.nigahae.user.domain.preference;

import java.util.Arrays;

public enum TravelTime {
    HALF_DAY(1L),
    ONE_DAY(2L),
    ONE_NIGHT_TWO_DAYS(3L),
    TWO_NIGHTS_THREE_DAYS(4L),
    THREE_NIGHTS_FOUR_DAYS(5L);

    private final long code;

    TravelTime(long code) {
        this.code = code;
    }

    public long code() {
        return code;
    }

    public static TravelTime fromCode(Long code) {
        if (code == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(value -> value.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown travel time code: " + code));
    }
}
