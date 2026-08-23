package com.nigahae.tourdata.repository;

import com.nigahae.tourdata.domain.TourSpotNearbyPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TourSpotNearbyPlaceRepository extends JpaRepository<TourSpotNearbyPlace, Long> {
    Optional<TourSpotNearbyPlace> findBySpotIdAndContentId(Long spotId, Long contentId);
}
