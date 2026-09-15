package com.nigahae.tourspot.repository;

import com.nigahae.tourspot.dto.TourSpotMemoResponse;
import com.nigahae.tourspot.dto.TourSpotMemoListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcTourSpotMemoRepository implements TourSpotMemoRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public TourSpotMemoResponse.Memo save(Long userId, Long spotId, String content, String imageUrl) {
        String sql = """
                INSERT INTO public."Memo" (user_id, spot_id, review, img)
                VALUES (:userId, :spotId, :content, :imageUrl)
                ON CONFLICT (user_id, spot_id) WHERE spot_id IS NOT NULL
                DO UPDATE SET review = EXCLUDED.review, img = EXCLUDED.img, updated_at = now()
                RETURNING history_id, review, img, created_at, updated_at
                """;
        return jdbcTemplate.queryForObject(sql, parameters(userId, spotId)
                        .addValue("content", content.trim())
                        .addValue("imageUrl", normalizeImageUrl(imageUrl)),
                (resultSet, rowNumber) -> new TourSpotMemoResponse.Memo(
                        resultSet.getLong("history_id"),
                        resultSet.getString("review"),
                        resultSet.getString("img"),
                        resultSet.getObject("created_at", java.time.OffsetDateTime.class),
                        resultSet.getObject("updated_at", java.time.OffsetDateTime.class)
                ));
    }

    @Override
    public Optional<TourSpotMemoResponse.Memo> find(Long userId, Long spotId) {
        String sql = """
                SELECT history_id, review, img, created_at, updated_at
                FROM public."Memo"
                WHERE user_id = :userId AND spot_id = :spotId
                """;
        return jdbcTemplate.query(sql, parameters(userId, spotId),
                        (resultSet, rowNumber) -> new TourSpotMemoResponse.Memo(
                                resultSet.getLong("history_id"),
                                resultSet.getString("review"),
                                resultSet.getString("img"),
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

    @Override
    public TourSpotMemoResponse.Memo saveNearby(Long userId, Long spotId, String content, String imageUrl) {
        String sql = """
                INSERT INTO public."Memo" (user_id, nearby_place_id, review, img)
                VALUES (:userId, :spotId, :content, :imageUrl)
                ON CONFLICT (user_id, nearby_place_id) WHERE nearby_place_id IS NOT NULL
                DO UPDATE SET review = EXCLUDED.review, img = EXCLUDED.img, updated_at = now()
                RETURNING history_id, review, img, created_at, updated_at
                """;
        return jdbcTemplate.queryForObject(sql, parameters(userId, spotId)
                        .addValue("content", content.trim())
                        .addValue("imageUrl", normalizeImageUrl(imageUrl)),
                (resultSet, rowNumber) -> new TourSpotMemoResponse.Memo(
                        resultSet.getLong("history_id"),
                        resultSet.getString("review"),
                        resultSet.getString("img"),
                        resultSet.getObject("created_at", java.time.OffsetDateTime.class),
                        resultSet.getObject("updated_at", java.time.OffsetDateTime.class)
                ));
    }

    @Override
    public Optional<TourSpotMemoResponse.Memo> findNearby(Long userId, Long spotId) {
        String sql = """
                SELECT history_id, review, img, created_at, updated_at
                FROM public."Memo"
                WHERE user_id = :userId AND nearby_place_id = :spotId
                """;
        return jdbcTemplate.query(sql, parameters(userId, spotId),
                        (resultSet, rowNumber) -> new TourSpotMemoResponse.Memo(
                                resultSet.getLong("history_id"),
                                resultSet.getString("review"),
                                resultSet.getString("img"),
                                resultSet.getObject("created_at", java.time.OffsetDateTime.class),
                                resultSet.getObject("updated_at", java.time.OffsetDateTime.class)
                        ))
                .stream().findFirst();
    }

    @Override
    public boolean deleteNearby(Long userId, Long spotId) {
        return jdbcTemplate.update("""
                DELETE FROM public."Memo"
                WHERE user_id = :userId AND nearby_place_id = :spotId
                """, parameters(userId, spotId)) > 0;
    }

    @Override
    public TourSpotMemoListResponse findAll(Long userId, int page, int size) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("limit", size)
                .addValue("offset", (long) page * size);
        String sql = """
                SELECT memo.history_id, memo.spot_id, memo.review, memo.img AS memo_img,
                       memo.created_at, memo.updated_at, memo.nearby_place_id,
                       CASE WHEN memo.spot_id IS NOT NULL THEN 'TOUR_SPOT'
                            ELSE upper(nearby.category) END AS category,
                       coalesce(spot.name, nearby.name) AS name,
                       coalesce(spot.img, nearby.img) AS spot_img
                FROM public."Memo" memo
                LEFT JOIN public."TourSpot" spot ON spot.spot_id = memo.spot_id
                LEFT JOIN public."TourSpotNearbyPlace" nearby
                    ON nearby.tour_spot_nearby_course_id = memo.nearby_place_id
                WHERE memo.user_id = :userId AND (spot.spot_id IS NOT NULL OR nearby.tour_spot_nearby_course_id IS NOT NULL)
                ORDER BY memo.updated_at DESC, memo.history_id DESC
                LIMIT :limit OFFSET :offset
                """;
        List<TourSpotMemoListResponse.Item> content = jdbcTemplate.query(sql, parameters,
                (resultSet, rowNumber) -> new TourSpotMemoListResponse.Item(
                        resultSet.getLong("history_id"),
                        resultSet.getObject("spot_id", Long.class),
                        resultSet.getObject("nearby_place_id", Long.class),
                        resultSet.getString("category"),
                        resultSet.getString("name"),
                        resultSet.getString("spot_img"),
                        resultSet.getString("review"),
                        resultSet.getString("memo_img"),
                        resultSet.getObject("created_at", java.time.OffsetDateTime.class),
                        resultSet.getObject("updated_at", java.time.OffsetDateTime.class)
                ));
        Long totalElements = jdbcTemplate.queryForObject("""
                SELECT COUNT(*) FROM public."Memo" memo
                LEFT JOIN public."TourSpot" spot ON spot.spot_id = memo.spot_id
                LEFT JOIN public."TourSpotNearbyPlace" nearby
                    ON nearby.tour_spot_nearby_course_id = memo.nearby_place_id
                WHERE memo.user_id = :userId AND (spot.spot_id IS NOT NULL OR nearby.tour_spot_nearby_course_id IS NOT NULL)
                """, new MapSqlParameterSource("userId", userId), Long.class);
        long total = totalElements == null ? 0 : totalElements;
        int totalPages = total == 0 ? 0 : (int) ((total + size - 1) / size);
        return new TourSpotMemoListResponse(content, page, size, total, totalPages);
    }

    private MapSqlParameterSource parameters(Long userId, Long spotId) {
        return new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("spotId", spotId);
    }

    private String normalizeImageUrl(String imageUrl) {
        return imageUrl == null || imageUrl.isBlank() ? null : imageUrl.trim();
    }
}
