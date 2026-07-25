package com.nigahae.tourspot.repository;

import com.nigahae.tourspot.domain.TourSpot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TourSpotQueryRepository extends JpaRepository<TourSpot, Long> {

    @Query("""
            SELECT t
            FROM TourSpotReadModel t
            WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(COALESCE(t.address, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(COALESCE(t.overview, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    Page<TourSpot> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
