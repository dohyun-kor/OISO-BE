package com.nigahae.tourdata.domain;

import com.nigahae.tourdata.client.FestivalPayload;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import org.springframework.data.domain.Persistable;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "`FestivalInfo`")
public class FestivalInfo implements Persistable<Long> {

    @Id
    @Column(name = "spot_id", nullable = false)
    private Long spotId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "spot_id", nullable = false, insertable = false, updatable = false)
    private TourSpot tourSpot;

    @Transient
    private boolean isNew = true;

    @Column(name = "event_start_date")
    private LocalDate eventStartDate;

    @Column(name = "event_end_date")
    private LocalDate eventEndDate;

    protected FestivalInfo() {
    }

    private FestivalInfo(TourSpot tourSpot) {
        this.tourSpot = tourSpot;
        this.spotId = tourSpot.getSpotId();
    }

    public static FestivalInfo forSpot(TourSpot tourSpot) {
        return new FestivalInfo(tourSpot);
    }

    public void updateDates(FestivalPayload payload) {
        this.eventStartDate = payload.eventStartDate();
        this.eventEndDate = payload.eventEndDate();
    }

    public Long getSpotId() {
        return spotId;
    }

    @Override
    public Long getId() {
        return spotId;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PostLoad
    @PostPersist
    private void markNotNew() {
        isNew = false;
    }

    public String getName() {
        return tourSpot.getLocalizedName();
    }

    public String getImg() {
        return tourSpot.getImg();
    }

    public String getAddress() {
        return tourSpot.getLocalizedAddress();
    }

    public String getOverview() {
        return tourSpot.getLocalizedOverview();
    }

    public String getHomepage() {
        return tourSpot.getHomepage();
    }

    public Double getMapX() {
        return tourSpot.getMapX();
    }

    public Double getMapY() {
        return tourSpot.getMapY();
    }

    public Long getContentId() {
        return tourSpot.getContentId();
    }

    public Long getContentTypeId() {
        return tourSpot.getContentTypeId();
    }

    public String getTel() {
        return tourSpot.getTel();
    }
}
