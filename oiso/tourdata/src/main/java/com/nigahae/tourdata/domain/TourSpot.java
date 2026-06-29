package com.nigahae.tourdata.domain;

import com.nigahae.tourdata.client.TourContentPayload;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "`TourSpot`")
public class TourSpot {

    @Id
    @Column(name = "spot_id", nullable = false)
    private Long spotId;

    @Column(name = "name")
    private String name;

    @Column(name = "img")
    private String img;

    @Column(name = "content")
    private String content;

    @Column(name = "address")
    private String address;

    @Column(name = "can_parking")
    private Boolean canParking;

    @Column(name = "can_pet")
    private Boolean canPet;

    @Column(name = "`like`")
    private Boolean like;

    @Column(name = "crowd_level")
    private Integer crowdLevel;

    @Column(name = "overview")
    private String overview;

    @Column(name = "homepage")
    private String homepage;

    @Column(name = "map_x")
    private Double mapX;

    @Column(name = "map_y")
    private Double mapY;

    @Column(name = "content_id")
    private Long contentId;

    @Column(name = "content_type_id")
    private Long contentTypeId;

    @Column(name = "tel")
    private String tel;

    protected TourSpot() {
    }

    public TourSpot(Long spotId) {
        this.spotId = spotId;
    }

    public void updateFrom(TourContentPayload payload) {
        this.contentId = payload.contentIdAsLong();
        this.contentTypeId = payload.contentTypeIdAsLong();
        this.name = payload.title();
        this.img = payload.firstImage();
        this.address = payload.address();
        this.mapX = payload.mapXAsDouble();
        this.mapY = payload.mapYAsDouble();
        this.tel = payload.tel();
        this.overview = payload.overview();
    }

    public Long getSpotId() {
        return spotId;
    }
}
