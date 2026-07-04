package com.nigahae.tourdata.repository;

import com.nigahae.tourdata.domain.AccommodationDetail;
import com.nigahae.tourdata.domain.AccommodationDetailId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AccommodationDetailRepository extends JpaRepository<AccommodationDetail, AccommodationDetailId> {

    @Transactional
    @Modifying
    @Query("delete from AccommodationDetail accommodationDetail where accommodationDetail.spotId = :spotId")
    void deleteBySpotId(Long spotId);
}
