package com.nigahae.tourdata.repository;

import com.nigahae.tourdata.domain.AccommodationRoom;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AccommodationRoomRepository extends JpaRepository<AccommodationRoom, Long> {

    @Transactional
    @Modifying
    @Query("delete from AccommodationRoom accommodationRoom where accommodationRoom.spotId = :spotId")
    void deleteBySpotId(Long spotId);
}
