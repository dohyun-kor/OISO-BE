package com.nigahae.tourspot.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@EqualsAndHashCode
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class StampMissionClearId implements Serializable {
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "spot_id", nullable = false)
    private Long spotId;

    @Enumerated(EnumType.STRING)
    @Column(name = "mission_type", nullable = false, length = 30)
    private StampMissionType missionType;
}
