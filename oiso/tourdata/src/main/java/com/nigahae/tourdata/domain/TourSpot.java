package com.nigahae.tourdata.domain;

import com.nigahae.tourdata.client.TourContentPayload;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "`TourSpot`")
public class TourSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spot_id", nullable = false)
    private Long spotId;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "img", length = 2048)
    private String img;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "can_parking")
    private Boolean canParking;

    @Column(name = "can_pet")
    private Boolean canPet;

    @Column(name = "crowd_level")
    private Integer crowdLevel;

    @Column(name = "overview", columnDefinition = "TEXT")
    private String overview;

    @Column(name = "homepage", columnDefinition = "TEXT")
    private String homepage;

    @Column(name = "map_x")
    private Double mapX;

    @Column(name = "map_y")
    private Double mapY;

    @Column(name = "content_id")
    private Long contentId;

    @Column(name = "content_type_id")
    private Long contentTypeId;

    @Column(name = "tel", length = 100)
    private String tel;

    protected TourSpot() {
    }

    public static TourSpot create() {
        return new TourSpot();
    }

    public static TourSpot reference(Long spotId) {
        TourSpot tourSpot = new TourSpot();
        tourSpot.spotId = spotId;
        return tourSpot;
    }

    public void updateFromList(TourContentPayload payload) {
        this.contentId = payload.contentIdAsLong();
        this.contentTypeId = payload.contentTypeIdAsLong();
        this.name = payload.title();
        this.img = payload.firstImage();
        this.address = payload.address();
        this.mapX = payload.mapXAsDouble();
        this.mapY = payload.mapYAsDouble();
        this.tel = payload.tel();
    }

    public void updateFromDetail(TourContentPayload payload) {
        updateFromList(payload);
        this.overview = payload.overview();
        this.homepage = payload.homepage();
    }

}
