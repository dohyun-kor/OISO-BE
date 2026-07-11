package com.nigahae.user.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "\"User\"", uniqueConstraints =
        @UniqueConstraint(name = "uk_user_provider_subject", columnNames = {"provider", "provider_user_id"}))
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
    @SequenceGenerator(name = "user_id_seq", sequenceName = "user_id_seq", allocationSize = 1)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "has_travel_plan")
    private Boolean hasTravelPlan;

    @Column(name = "travel_time")
    private Long travelTime;

    @Column(name = "travel_companion")
    private Long travelCompanion;

    @Column(name = "preferred_travel_theme")
    private Long preferredTravelTheme;

    @Column(name = "transportation_mode")
    private Long transportationMode;

    @Column(name = "language")
    private Long language;

    @Column(nullable = false, length = 30)
    private String provider;

    @Column(name = "provider_user_id", nullable = false, length = 100)
    private String providerUserId;

    @Column(nullable = false, length = 100)
    private String nickname;

    @Column(length = 320)
    private String email;

    @Column(name = "profile_image_url", length = 2048)
    private String profileImageUrl;

    @Column(name = "\"Field\"", length = 255)
    private String field;

    @Column(name = "\"Field2\"", length = 255)
    private String field2;

    public void updateProfile(String nickname, String email, String profileImageUrl) {
        this.nickname = nickname;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
    }
}
