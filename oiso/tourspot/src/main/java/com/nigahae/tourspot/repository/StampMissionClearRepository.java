package com.nigahae.tourspot.repository;

import com.nigahae.tourspot.domain.StampMissionClear;
import com.nigahae.tourspot.domain.StampMissionClearId;
import com.nigahae.tourspot.domain.StampMissionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StampMissionClearRepository
        extends JpaRepository<StampMissionClear, StampMissionClearId> {

    List<StampMissionClear> findAllByIdUserIdAndIdSpotId(Long userId, Long spotId);

    @Modifying
    @Query(value = """
            insert into "UserTourSpotMissionClear" (user_id, spot_id, mission_type, cleared_at)
            values (:userId, :spotId, :missionType, CURRENT_TIMESTAMP)
            on conflict (user_id, spot_id, mission_type) do nothing
            """, nativeQuery = true)
    int saveIfAbsent(
            @Param("userId") Long userId,
            @Param("spotId") Long spotId,
            @Param("missionType") String missionType
    );
}
