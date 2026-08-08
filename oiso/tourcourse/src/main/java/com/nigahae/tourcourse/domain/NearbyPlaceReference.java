package com.nigahae.tourcourse.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TourCourseNearbyPlaceReference")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "\"TourSpotNearbyPlace\"")
public class NearbyPlaceReference {

    @Id
    @Column(name = "tour_spot_nearby_course_id")
    private Long nearbyPlaceId;
}
