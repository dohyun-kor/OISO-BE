package com.nigahae.tourspot.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TourSpotReadModel")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "\"TourSpot\"")
public class TourSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spot_id")
    private Long spotId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "img", length = 2048)
    private String img;

    @Column(name = "content", columnDefinition = "text")
    private String content;

    @Column(name = "can_parking")
    private Boolean canParking;

    @Column(name = "can_pet")
    private Boolean canPet;

    @Column(name = "crowd_level")
    private Integer crowdLevel;

    @Column(name = "overview", columnDefinition = "text")
    private String overview;

    @Column(name = "address", columnDefinition = "text")
    private String address;

    @Column(name = "homepage", columnDefinition = "text")
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
}
