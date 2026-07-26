package com.nigahae.tourdata.domain;

import com.nigahae.tourdata.client.FestivalPayload;
import com.nigahae.tourdata.client.TourContentPayload;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "`FestivalInfo`")
public class FestivalInfo {

    @Id
    @Column(name = "spot_id", nullable = false)
    private Long spotId;

    @Column(name = "name")
    private String name;

    @Column(name = "img")
    private String img;

    @Column(name = "address")
    private String address;

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

    @Column(name = "tel")
    private String tel;

    @Column(name = "event_start_date")
    private LocalDate eventStartDate;

    @Column(name = "event_end_date")
    private LocalDate eventEndDate;

    protected FestivalInfo() {
    }

    public FestivalInfo(Long spotId) {
        this.spotId = spotId;
    }

    public void updateFromList(FestivalPayload payload) {
        updateListFields(payload.content());
        this.eventStartDate = payload.eventStartDate();
        this.eventEndDate = payload.eventEndDate();
    }

    public void updateFromDetail(TourContentPayload payload) {
        updateListFields(payload);
        this.overview = payload.overview();
        this.homepage = payload.homepage();
    }

    private void updateListFields(TourContentPayload payload) {
        this.contentId = payload.contentIdAsLong();
        this.contentTypeId = payload.contentTypeIdAsLong();
        this.name = payload.title();
        this.img = payload.firstImage();
        this.address = payload.address();
        this.mapX = payload.mapXAsDouble();
        this.mapY = payload.mapYAsDouble();
        this.tel = payload.tel();
    }
}
