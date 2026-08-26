package com.nigahae.tourcourse.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "\"TourCourse\"")
public class TourCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tour_course_id")
    private Long tourCourseId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "title", nullable = false, length = 20)
    private String title;

    @Column(name = "share_yn", nullable = false)
    private boolean shareYn;

    @Column(name = "shared_password", length = 100)
    private String sharedPassword;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "tourCourse", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<TourCourseItem> items = new ArrayList<>();

    public TourCourse(Long userId, String title, boolean shareYn, String sharedPassword) {
        this.userId = userId;
        this.title = title;
        this.shareYn = shareYn;
        this.sharedPassword = sharedPassword;
    }

    public void addItem(
            TourCourseItemCategory category,
            Long spotId,
            Long nearbyPlaceId,
            int itemOrder,
            TransportType transportType
    ) {
        items.add(new TourCourseItem(
                this, category, spotId, nearbyPlaceId, 1, itemOrder,
                transportType));
    }

    public void addItem(
            Long spotId,
            int itemOrder,
            TransportType transportType
    ) {
        items.add(new TourCourseItem(
                this, spotId, itemOrder, transportType));
    }

    public void addItem(
            TourCourseItemCategory category, Long spotId, Long nearbyPlaceId,
            int dayNumber, int itemOrder, TransportType transportType
    ) {
        items.add(new TourCourseItem(
                this, category, spotId, nearbyPlaceId, dayNumber, itemOrder,
                transportType));
    }

    public void updateBasicInfo(String title, boolean shareYn, String sharedPassword) {
        this.title = title;
        this.shareYn = shareYn;
        this.sharedPassword = sharedPassword;
    }

    @PrePersist
    private void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    private void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
