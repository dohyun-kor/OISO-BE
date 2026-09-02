package com.nigahae.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MyPageQueryRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public Counts findCounts(Long userId) {
        String sql = """
                SELECT
                    (SELECT COUNT(DISTINCT saved_spot.spot_id)
                     FROM public."UserSavedTourSpot" saved_spot
                     JOIN public."TourSpot" spot
                       ON spot.spot_id = saved_spot.spot_id
                     WHERE saved_spot.user_id = :userId) AS saved_spot_count,
                    (SELECT COUNT(DISTINCT saved_story.story_id)
                     FROM public."UserSavedTourStory" saved_story
                     JOIN public."TourStory" story
                       ON story.story_id = saved_story.story_id
                     WHERE saved_story.user_id = :userId) AS saved_story_count,
                    (SELECT COUNT(*)
                     FROM public."Memo" memo
                     WHERE memo.user_id = :userId
                       AND memo.spot_id IS NOT NULL) AS memo_count
                """;

        return jdbcTemplate.queryForObject(
                sql,
                userParameters(userId),
                (resultSet, rowNumber) -> new Counts(
                        resultSet.getLong("saved_spot_count"),
                        resultSet.getLong("saved_story_count"),
                        resultSet.getLong("memo_count")
                )
        );
    }

    public List<SavedSpotItemRow> findSavedSpots(Long userId) {
        String sql = """
                SELECT saved.spot_id, saved.img
                FROM (
                    SELECT DISTINCT ON (saved_spot.spot_id)
                           saved_spot.spot_id,
                           spot.img,
                           saved_spot.saved_at
                    FROM public."UserSavedTourSpot" saved_spot
                    JOIN public."TourSpot" spot
                      ON spot.spot_id = saved_spot.spot_id
                    WHERE saved_spot.user_id = :userId
                    ORDER BY saved_spot.spot_id, saved_spot.saved_at DESC
                ) saved
                ORDER BY saved.saved_at DESC, saved.spot_id
                """;

        return jdbcTemplate.query(
                sql,
                userParameters(userId),
                (resultSet, rowNumber) -> new SavedSpotItemRow(
                        resultSet.getLong("spot_id"),
                        resultSet.getString("img")
                )
        );
    }

    public List<SavedStoryItemRow> findSavedStories(Long userId) {
        String sql = """
                SELECT saved.story_id, saved.img
                FROM (
                    SELECT DISTINCT ON (saved_story.story_id)
                           saved_story.story_id,
                           story.img,
                           saved_story.saved_at
                    FROM public."UserSavedTourStory" saved_story
                    JOIN public."TourStory" story
                      ON story.story_id = saved_story.story_id
                    WHERE saved_story.user_id = :userId
                    ORDER BY saved_story.story_id, saved_story.saved_at DESC, story.spot_id
                ) saved
                ORDER BY saved.saved_at DESC, saved.story_id
                """;

        return jdbcTemplate.query(
                sql,
                userParameters(userId),
                (resultSet, rowNumber) -> new SavedStoryItemRow(
                        resultSet.getLong("story_id"),
                        resultSet.getString("img")
                )
        );
    }

    public List<MemoItemRow> findMemos(Long userId) {
        String sql = """
                SELECT memo.history_id,
                       memo.spot_id,
                       memo.review,
                       memo.updated_at,
                       spot.name,
                       spot.img
                FROM public."Memo" memo
                JOIN public."TourSpot" spot
                  ON spot.spot_id = memo.spot_id
                WHERE memo.user_id = :userId
                ORDER BY memo.updated_at DESC, memo.history_id DESC
                """;

        return jdbcTemplate.query(
                sql,
                userParameters(userId),
                (resultSet, rowNumber) -> new MemoItemRow(
                        resultSet.getLong("history_id"),
                        resultSet.getLong("spot_id"),
                        resultSet.getString("review"),
                        resultSet.getString("name"),
                        resultSet.getString("img"),
                        resultSet.getObject("updated_at", java.time.OffsetDateTime.class)
                )
        );
    }

    private MapSqlParameterSource userParameters(Long userId) {
        return new MapSqlParameterSource("userId", userId);
    }

    public record Counts(
            long savedSpotCount,
            long savedStoryCount,
            long memoCount
    ) {
    }

    public record SavedSpotItemRow(
            Long spotId,
            String imageUrl
    ) {
    }

    public record SavedStoryItemRow(
            Long storyId,
            String imageUrl
    ) {
    }

    public record MemoItemRow(
            Long memoId,
            Long spotId,
            String content,
            String spotName,
            String spotImage,
            java.time.OffsetDateTime updatedAt
    ) {
        public MemoItemRow(Long memoId, String content, String spotName) {
            this(memoId, null, content, spotName, null, null);
        }
    }
}
