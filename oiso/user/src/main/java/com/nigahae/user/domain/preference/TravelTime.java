package com.nigahae.user.domain.preference;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(description = """
        여행 일정(화면 선택값 → API enum → DB 코드)
        - 반나절 → HALF_DAY → 1
        - 하루종일 → ONE_DAY → 2
        - 1박 2일 → ONE_NIGHT_TWO_DAYS → 3
        - 2박 3일 → TWO_NIGHTS_THREE_DAYS → 4
        - 3박 4일 → THREE_NIGHTS_FOUR_DAYS → 5
        """)
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
