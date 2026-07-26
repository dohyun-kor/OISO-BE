package com.nigahae.user.domain.preference;

import java.util.Arrays;

public enum TransportationMode {
    WALK_PUBLIC_TRANSIT(1L),
    BICYCLE(2L),
    CAR(3L);

    private final long code;

    TransportationMode(long code) {
        this.code = code;
    }

    public long code() {
        return code;
    }

    public static TransportationMode fromCode(Long code) {
        if (code == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(value -> value.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown transportation mode code: " + code));
    }
}
