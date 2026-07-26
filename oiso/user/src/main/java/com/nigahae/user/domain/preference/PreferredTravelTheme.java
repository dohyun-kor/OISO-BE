package com.nigahae.user.domain.preference;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(description = """
        선호 여행 테마(화면 선택값 → API enum → DB 코드)
        - 역사 및 문화탐방 → HISTORY_CULTURE → 1
        - 자연 및 경관 → NATURE_SCENERY → 2
        - 먹거리 탐방 → FOOD → 3
        """)
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
