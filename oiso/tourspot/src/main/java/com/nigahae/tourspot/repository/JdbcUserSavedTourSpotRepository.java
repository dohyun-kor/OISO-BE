package com.nigahae.tourspot.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcUserSavedTourSpotRepository implements UserSavedTourSpotRepository {

    private static final String SAVE_IF_ABSENT_SQL = """
            INSERT INTO public."UserSavedTourSpot" (user_id, spot_id)
            VALUES (?, ?)
            ON CONFLICT (user_id, spot_id) DO NOTHING
            """;
    private static final String DELETE_SQL = """
            DELETE FROM public."UserSavedTourSpot"
            WHERE user_id = ? AND spot_id = ?
            """;
    private static final String SAVE_NEARBY_PLACE_IF_ABSENT_SQL = """
            INSERT INTO public."UserSavedNearbyPlace" (user_id, tour_spot_nearby_course_id)
            VALUES (?, ?)
            ON CONFLICT (user_id, tour_spot_nearby_course_id) DO NOTHING
            """;
    private static final String DELETE_NEARBY_PLACE_SQL = """
            DELETE FROM public."UserSavedNearbyPlace"
            WHERE user_id = ? AND tour_spot_nearby_course_id = ?
            """;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void saveIfAbsent(Long userId, Long spotId) {
        jdbcTemplate.update(SAVE_IF_ABSENT_SQL, userId, spotId);
    }

    @Override
    public void delete(Long userId, Long spotId) {
        jdbcTemplate.update(DELETE_SQL, userId, spotId);
    }

    @Override
    public boolean exists(Long userId, Long spotId) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                "SELECT EXISTS (SELECT 1 FROM public.\"UserSavedTourSpot\" WHERE user_id = ? AND spot_id = ?)",
                Boolean.class, userId, spotId));
    }

    @Override
    public void saveNearbyPlaceIfAbsent(Long userId, Long nearbyPlaceId) {
        jdbcTemplate.update(SAVE_NEARBY_PLACE_IF_ABSENT_SQL, userId, nearbyPlaceId);
    }

    @Override
    public void deleteNearbyPlace(Long userId, Long nearbyPlaceId) {
        jdbcTemplate.update(DELETE_NEARBY_PLACE_SQL, userId, nearbyPlaceId);
    }
}
