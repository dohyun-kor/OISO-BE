package com.nigahae.tourdata.repository;

import com.nigahae.tourdata.domain.FestivalInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FestivalInfoRepository extends JpaRepository<FestivalInfo, Long> {

    Optional<FestivalInfo> findByContentId(Long contentId);

    List<FestivalInfo> findAllByEventEndDateGreaterThanEqual(LocalDate eventEndDate);
}
