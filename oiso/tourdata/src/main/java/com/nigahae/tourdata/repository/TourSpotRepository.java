package com.nigahae.tourdata.repository;

import com.nigahae.tourdata.domain.TourSpot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TourSpotRepository extends JpaRepository<TourSpot, Long> {

    Optional<TourSpot> findByContentId(Long contentId);
}
