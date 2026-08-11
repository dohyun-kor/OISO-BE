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

    @Query(value = """
            select exists (
                select 1 from "TourSpotNearbyPlace" nearby
                where nearby.tour_spot_nearby_course_id = :nearbyPlaceId
                  and case
                      when nearby.content_type_id = 39 or upper(nearby.category) in ('RESTAURANT', 'FOOD') then 'RESTAURANT'
                      when nearby.content_type_id = 32 or upper(nearby.category) in ('ACCOMMODATION', 'LODGING') then 'ACCOMMODATION'
                      else upper(nearby.category)
                  end = :category
            )
            """, nativeQuery = true)
    boolean existsNearbyPlace(
            @Param("nearbyPlaceId") Long nearbyPlaceId,
            @Param("category") String category
    );
}
