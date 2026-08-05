package com.nigahae.user.domain.preference;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(description = """
        동행 유형(화면 선택값 → API enum → DB 코드)
        - 혼자 → ALONE → 1
        - 연인과 함께 → PARTNER → 2
        - 친구와 함께 → FRIENDS → 3
        - 가족과 함께 → FAMILY → 4
        """)
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
