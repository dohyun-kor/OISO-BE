package com.nigahae.tourdata.domain;

import com.nigahae.tourdata.client.TourContentPayload;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "`TourSpotNearbyPlace`")
public class TourSpotNearbyPlace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tour_spot_nearby_course_id")
    private Long nearbyPlaceId;

    @Column(name = "spot_id", nullable = false)
    private Long spotId;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "img", length = 2048)
    private String img;
    @Column(name = "overview", columnDefinition = "text")
    private String overview;
    @Column(name = "category", nullable = false, length = 30)
    private String category;
    @Column(name = "address", columnDefinition = "text")
    private String address;
    @Column(name = "homepage_url", length = 2048)
    private String homepageUrl;
    @Column(name = "content_id")
    private Long contentId;
    @Column(name = "content_type_id")
    private Long contentTypeId;
    @Column(name = "map_x")
    private Double mapX;
    @Column(name = "map_y")
    private Double mapY;

    protected TourSpotNearbyPlace() {}

    public static TourSpotNearbyPlace create(Long spotId, String category, TourContentPayload payload) {
        TourSpotNearbyPlace place = new TourSpotNearbyPlace();
        place.spotId = spotId;
        place.update(category, payload);
        return place;
    }

    public void update(String category, TourContentPayload payload) {
        this.category = category;
        this.contentId = payload.contentIdAsLong();
        this.contentTypeId = payload.contentTypeIdAsLong();
        if (hasText(payload.title())) this.name = payload.title();
        if (hasText(payload.firstImage())) this.img = payload.firstImage();
        if (hasText(payload.overview())) this.overview = payload.overview();
        if (hasText(payload.address())) this.address = payload.address();
        if (hasText(payload.homepage())) this.homepageUrl = payload.homepage();
        if (payload.mapXAsDouble() != null) this.mapX = payload.mapXAsDouble();
        if (payload.mapYAsDouble() != null) this.mapY = payload.mapYAsDouble();
    }

    private static boolean hasText(String value) { return value != null && !value.isBlank(); }
}
