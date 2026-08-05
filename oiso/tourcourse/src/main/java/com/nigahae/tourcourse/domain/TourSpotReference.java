package com.nigahae.tourcourse.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TourCourseSpotReference")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "\"TourSpot\"")
public class TourSpotReference {

    @Id
    @Column(name = "spot_id")
    private Long spotId;

    @Column(name = "img", length = 2048)
    private String img;
}
