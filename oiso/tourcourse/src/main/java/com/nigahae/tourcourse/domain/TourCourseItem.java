package com.nigahae.tourcourse.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "\"TourCourseItem\"")
public class TourCourseItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tour_course_id", nullable = false)
    private TourCourse tourCourse;

    @Column(name = "spot_id", nullable = false)
    private Long spotId;

    @Column(name = "item_order", nullable = false)
    private int itemOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "transport_type", length = 10)
    private TransportType transportType;

    TourCourseItem(TourCourse tourCourse, Long spotId, int itemOrder, TransportType transportType) {
        this.tourCourse = tourCourse;
        this.spotId = spotId;
        this.itemOrder = itemOrder;
        this.transportType = transportType;
    }
}
