package com.nigahae.tourcourse.repository;

import com.nigahae.tourcourse.domain.NearbyPlaceReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Set;

public interface NearbyPlaceReferenceRepository extends JpaRepository<NearbyPlaceReference, Long> {

    @Query("select p.nearbyPlaceId from TourCourseNearbyPlaceReference p where p.nearbyPlaceId in :ids")
    Set<Long> findExistingIds(@Param("ids") Collection<Long> ids);
}
