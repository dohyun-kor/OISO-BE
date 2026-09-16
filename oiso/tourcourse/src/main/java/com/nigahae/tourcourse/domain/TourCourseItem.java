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

    @Column(name = "spot_id")
    private Long spotId;

    @Column(name = "tour_spot_nearby_course_id")
    private Long nearbyPlaceId;

    @Column(name = "item_order", nullable = false)
    private int itemOrder;

    @Column(name = "day_number", nullable = false)
    private int dayNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "transport_type", length = 10)
    private TransportType transportType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TourCourseItemStatus status = TourCourseItemStatus.PENDING;

    TourCourseItem(TourCourse tourCourse, Long spotId, int itemOrder, TransportType transportType) {
        this.tourCourse = tourCourse;
        this.spotId = spotId;
        this.itemOrder = itemOrder;
        this.transportType = transportType;
    }

    TourCourseItem(
            TourCourse tourCourse, TourCourseItemCategory category, Long spotId, Long nearbyPlaceId,
            int dayNumber, int itemOrder, TransportType transportType
    ) {
        this.tourCourse = tourCourse;
        setPlace(category, spotId, nearbyPlaceId);
        this.dayNumber = dayNumber;
        this.itemOrder = itemOrder;
        this.transportType = transportType;
    }

    public void update(
            Long spotId, int itemOrder, TransportType transportType
    ) {
        this.spotId = spotId;
        this.nearbyPlaceId = null;
        this.itemOrder = itemOrder;
        this.transportType = transportType;
    }

    public void update(
            TourCourseItemCategory category, Long spotId, Long nearbyPlaceId,
            int dayNumber, int itemOrder, TransportType transportType
    ) {
        setPlace(category, spotId, nearbyPlaceId);
        this.dayNumber = dayNumber;
        this.itemOrder = itemOrder;
        this.transportType = transportType;
    }

    public void updateOrder(int itemOrder) {
        this.itemOrder = itemOrder;
    }

    public void updateStatus(TourCourseItemStatus status) {
        this.status = status;
    }

    public TourCourseItemCategory getCategory() {
        return spotId != null
                ? TourCourseItemCategory.TOUR_SPOT
                : TourCourseItemCategory.RESTAURANT;
    }

    private void setPlace(TourCourseItemCategory category, Long spotId, Long nearbyPlaceId) {
        if (category == TourCourseItemCategory.TOUR_SPOT) {
            this.spotId = spotId;
            this.nearbyPlaceId = null;
        } else {
            this.spotId = null;
            this.nearbyPlaceId = nearbyPlaceId;
        }
    }
}
