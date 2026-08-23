package com.nigahae.tourdata.repository;

import com.nigahae.tourdata.domain.TourSpot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TourSpotRepository extends JpaRepository<TourSpot, Long> {

    Optional<TourSpot> findByContentId(Long contentId);

    List<TourSpot> findAllByContentTypeId(Long contentTypeId);

    List<TourSpot> findAllByContentTypeIdIn(List<Long> contentTypeIds);
}
