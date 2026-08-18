package com.nigahae.tourspot.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "\"UserTourSpotMissionClear\"")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StampMissionClear {
    @EmbeddedId
    private StampMissionClearId id;

    @Column(name = "cleared_at", nullable = false, updatable = false)
    private LocalDateTime clearedAt;
}
