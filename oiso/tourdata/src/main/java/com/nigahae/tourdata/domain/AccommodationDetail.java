package com.nigahae.tourdata.domain;

import com.nigahae.tourdata.client.AccommodationRoomPayload;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@IdClass(AccommodationDetailId.class)
@Table(name = "`AccommodationDetail`")
public class AccommodationDetail {

    @Id
    @Column(name = "temp_room_id", nullable = false)
    private Long tempRoomId;

    @Id
    @Column(name = "spot_id", nullable = false)
    private Long spotId;

    @Column(name = "room_code", columnDefinition = "TEXT")
    private String roomCode;

    @Column(name = "room_title", columnDefinition = "TEXT")
    private String roomTitle;

    @Column(name = "room_size_pyeong", columnDefinition = "TEXT")
    private String roomSizePyeong;

    @Column(name = "room_size_square_meter", columnDefinition = "TEXT")
    private String roomSizeSquareMeter;

    @Column(name = "room_count", columnDefinition = "TEXT")
    private String roomCount;

    @Column(name = "base_count", columnDefinition = "TEXT")
    private String baseCount;

    @Column(name = "max_count", columnDefinition = "TEXT")
    private String maxCount;

    @Column(name = "offseason_weekday_min_fee", columnDefinition = "TEXT")
    private String offseasonWeekdayMinFee;

    @Column(name = "offseason_weekend_min_fee", columnDefinition = "TEXT")
    private String offseasonWeekendMinFee;

    @Column(name = "peak_season_weekday_min_fee", columnDefinition = "TEXT")
    private String peakSeasonWeekdayMinFee;

    @Column(name = "peak_season_weekend_min_fee", columnDefinition = "TEXT")
    private String peakSeasonWeekendMinFee;

    @Column(name = "room_intro", columnDefinition = "TEXT")
    private String roomIntro;

    @Column(name = "has_bath_facility", columnDefinition = "TEXT")
    private String hasBathFacility;

    @Column(name = "has_bath", columnDefinition = "TEXT")
    private String hasBath;

    @Column(name = "has_home_theater", columnDefinition = "TEXT")
    private String hasHomeTheater;

    @Column(name = "has_air_condition", columnDefinition = "TEXT")
    private String hasAirCondition;

    @Column(name = "has_tv", columnDefinition = "TEXT")
    private String hasTv;

    @Column(name = "has_pc", columnDefinition = "TEXT")
    private String hasPc;

    @Column(name = "has_cable", columnDefinition = "TEXT")
    private String hasCable;

    @Column(name = "has_internet", columnDefinition = "TEXT")
    private String hasInternet;

    @Column(name = "has_refrigerator", columnDefinition = "TEXT")
    private String hasRefrigerator;

    @Column(name = "has_toiletries", columnDefinition = "TEXT")
    private String hasToiletries;

    @Column(name = "has_sofa", columnDefinition = "TEXT")
    private String hasSofa;

    @Column(name = "has_cook", columnDefinition = "TEXT")
    private String hasCook;

    @Column(name = "has_table", columnDefinition = "TEXT")
    private String hasTable;

    @Column(name = "has_hairdryer", columnDefinition = "TEXT")
    private String hasHairdryer;

    protected AccommodationDetail() {
    }

    private AccommodationDetail(Long tempRoomId, Long spotId, AccommodationRoomPayload payload) {
        this.tempRoomId = tempRoomId;
        this.spotId = spotId;
        this.roomCode = payload.roomCode();
        this.roomTitle = payload.roomTitle();
        this.roomSizePyeong = payload.roomSizePyeong();
        this.roomSizeSquareMeter = payload.roomSizeSquareMeter();
        this.roomCount = payload.roomCount();
        this.baseCount = payload.baseCount();
        this.maxCount = payload.maxCount();
        this.offseasonWeekdayMinFee = payload.offseasonWeekdayMinFee();
        this.offseasonWeekendMinFee = payload.offseasonWeekendMinFee();
        this.peakSeasonWeekdayMinFee = payload.peakSeasonWeekdayMinFee();
        this.peakSeasonWeekendMinFee = payload.peakSeasonWeekendMinFee();
        this.roomIntro = payload.roomIntro();
        this.hasBathFacility = payload.hasBathFacility();
        this.hasBath = payload.hasBath();
        this.hasHomeTheater = payload.hasHomeTheater();
        this.hasAirCondition = payload.hasAirCondition();
        this.hasTv = payload.hasTv();
        this.hasPc = payload.hasPc();
        this.hasCable = payload.hasCable();
        this.hasInternet = payload.hasInternet();
        this.hasRefrigerator = payload.hasRefrigerator();
        this.hasToiletries = payload.hasToiletries();
        this.hasSofa = payload.hasSofa();
        this.hasCook = payload.hasCook();
        this.hasTable = payload.hasTable();
        this.hasHairdryer = payload.hasHairdryer();
    }

    public static AccommodationDetail fromRoomPayload(Long tempRoomId, Long spotId, AccommodationRoomPayload payload) {
        return new AccommodationDetail(tempRoomId, spotId, payload);
    }

    public Long getTempRoomId() {
        return tempRoomId;
    }

    public Long getSpotId() {
        return spotId;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public String getRoomTitle() {
        return roomTitle;
    }
}
