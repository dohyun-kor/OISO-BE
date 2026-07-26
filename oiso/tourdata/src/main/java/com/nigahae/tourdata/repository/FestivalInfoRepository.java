package com.nigahae.tourdata.repository;

import com.nigahae.tourdata.domain.FestivalInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FestivalInfoRepository extends JpaRepository<FestivalInfo, Long> {

    Optional<FestivalInfo> findByContentId(Long contentId);
}
