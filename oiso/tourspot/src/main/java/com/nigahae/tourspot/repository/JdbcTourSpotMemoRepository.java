package com.nigahae.tourspot.repository;

import com.nigahae.tourspot.dto.TourSpotMemoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcTourSpotMemoRepository implements TourSpotMemoRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public TourSpotMemoResponse.Memo save(Long userId, Long spotId, String content) {
        String sql = """
                INSERT INTO public."Memo" (user_id, spot_id, review)
                VALUES (:userId, :spotId, :content)
                ON CONFLICT (user_id, spot_id) WHERE spot_id IS NOT NULL
                DO UPDATE SET review = EXCLUDED.review, updated_at = now()
                RETURNING history_id, review, created_at, updated_at
                """;
        return jdbcTemplate.queryForObject(sql, parameters(userId, spotId)
                        .addValue("content", content.trim()),
                (resultSet, rowNumber) -> new TourSpotMemoResponse.Memo(
                        resultSet.getLong("history_id"),
                        resultSet.getString("review"),
                        resultSet.getObject("created_at", java.time.OffsetDateTime.class),
                        resultSet.getObject("updated_at", java.time.OffsetDateTime.class)
                ));
    }

    @Override
    public Optional<TourSpotMemoResponse.Memo> find(Long userId, Long spotId) {
        String sql = """
                SELECT history_id, review, created_at, updated_at
                FROM public."Memo"
                WHERE user_id = :userId AND spot_id = :spotId
                """;
        return jdbcTemplate.query(sql, parameters(userId, spotId),
                        (resultSet, rowNumber) -> new TourSpotMemoResponse.Memo(
                                resultSet.getLong("history_id"),
                                resultSet.getString("review"),
                                resultSet.getObject("created_at", java.time.OffsetDateTime.class),
                                resultSet.getObject("updated_at", java.time.OffsetDateTime.class)
                        ))
                .stream().findFirst();
    }

    @Override
    public boolean delete(Long userId, Long spotId) {
        return jdbcTemplate.update("""
                DELETE FROM public."Memo"
                WHERE user_id = :userId AND spot_id = :spotId
                """, parameters(userId, spotId)) > 0;
    }

    private MapSqlParameterSource parameters(Long userId, Long spotId) {
        return new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("spotId", spotId);
    }
}
