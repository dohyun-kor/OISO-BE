package com.nigahae.tourcourse.repository;

import com.nigahae.tourcourse.domain.TourSpotReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Set;

public interface TourSpotReferenceRepository extends JpaRepository<TourSpotReference, Long> {

    @Query("select t.spotId from TourCourseSpotReference t where t.spotId in :spotIds")
    Set<Long> findExistingIds(@Param("spotIds") Collection<Long> spotIds);
}
