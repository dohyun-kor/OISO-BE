package com.nigahae.user.domain.preference;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(description = """
        이동 수단(화면 선택값 → API enum → DB 코드)
        - 도보 및 대중교통 → WALK_PUBLIC_TRANSIT → 1
        - 자전거 → BICYCLE → 2
        - 자동차 → CAR → 3
        """)
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
