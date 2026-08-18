package com.nigahae.tourdata.repository;

import com.nigahae.tourdata.domain.FestivalInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface FestivalInfoRepository extends JpaRepository<FestivalInfo, Long> {

    List<FestivalInfo> findAllByEventEndDateGreaterThanEqual(LocalDate eventEndDate);
}
