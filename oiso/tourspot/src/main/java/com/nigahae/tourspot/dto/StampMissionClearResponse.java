package com.nigahae.tourspot.dto;

import com.nigahae.tourspot.domain.StampMissionType;

import java.time.LocalDateTime;

public record StampMissionClearResponse(
        Long spotId,
        StampMissionType missionType,
        boolean cleared,
        LocalDateTime clearedAt
) {
}
