package com.nigahae.tourspot.repository;

import com.nigahae.tourspot.dto.SavedStoryCardListResponse;
import com.nigahae.tourspot.dto.StoryCardDetailResponse;
import com.nigahae.tourspot.dto.StoryCardSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcStoryCardRepository implements StoryCardRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public List<StoryCardSummaryResponse> findRandom(int size) {
        return jdbcTemplate.query("""
                SELECT story.story_id, story.spot_id, spot.name AS tour_spot_name,
                       story.title, story.sub_title, story.summary, story.img
                FROM public."TourStory" story
                JOIN public."TourSpot" spot ON spot.spot_id = story.spot_id
                ORDER BY random()
                LIMIT :limit
                """, new MapSqlParameterSource("limit", size), (rs, rowNumber) ->
                new StoryCardSummaryResponse(rs.getLong("story_id"), rs.getLong("spot_id"),
                        rs.getString("tour_spot_name"), rs.getString("title"),
                        rs.getString("sub_title"), rs.getString("summary"), rs.getString("img")));
    }

    @Override
    public boolean existsById(Long storyId) {
        Boolean exists = jdbcTemplate.queryForObject("""
                SELECT EXISTS (
                    SELECT 1 FROM public."TourStory" WHERE story_id = :storyId
                )
                """, new MapSqlParameterSource("storyId", storyId), Boolean.class);
        return Boolean.TRUE.equals(exists);
    }

    @Override
    public void saveIfAbsent(Long userId, Long storyId) {
        jdbcTemplate.update("""
                INSERT INTO public."UserSavedTourStory" (user_id, story_id)
                VALUES (:userId, :storyId)
                ON CONFLICT (user_id, story_id) DO NOTHING
                """, ids(userId, storyId));
    }

    @Override
    public void delete(Long userId, Long storyId) {
        jdbcTemplate.update("""
                DELETE FROM public."UserSavedTourStory"
                WHERE user_id = :userId AND story_id = :storyId
                """, ids(userId, storyId));
    }

    @Override
    public SavedStoryCardListResponse findSaved(Long userId, int page, int size) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("limit", size)
                .addValue("offset", (long) page * size);
        List<SavedStoryCardListResponse.Item> content = jdbcTemplate.query("""
                SELECT story.story_id, story.spot_id, spot.name AS tour_spot_name,
                       story.title, story.sub_title, story.summary, story.img,
                       saved.saved_at
                FROM public."UserSavedTourStory" saved
                JOIN public."TourStory" story ON story.story_id = saved.story_id
                JOIN public."TourSpot" spot ON spot.spot_id = story.spot_id
                WHERE saved.user_id = :userId
                ORDER BY saved.saved_at DESC, story.story_id DESC
                LIMIT :limit OFFSET :offset
                """, parameters, (resultSet, rowNumber) -> new SavedStoryCardListResponse.Item(
                resultSet.getLong("story_id"),
                resultSet.getLong("spot_id"),
                resultSet.getString("tour_spot_name"),
                resultSet.getString("title"),
                resultSet.getString("sub_title"),
                resultSet.getString("summary"),
                resultSet.getString("img"),
                resultSet.getObject("saved_at", java.time.OffsetDateTime.class)
        ));
        Long totalElements = jdbcTemplate.queryForObject("""
                SELECT COUNT(*) FROM public."UserSavedTourStory"
                WHERE user_id = :userId
                """, new MapSqlParameterSource("userId", userId), Long.class);
        long total = totalElements == null ? 0 : totalElements;
        int totalPages = total == 0 ? 0 : (int) ((total + size - 1) / size);
        return new SavedStoryCardListResponse(content, page, size, total, totalPages);
    }

    @Override
    public Optional<StoryCardDetailResponse> findBySpotId(Long userId, Long spotId) {
        List<StoryCardRow> rows = jdbcTemplate.query("""
                SELECT story.story_id, story.spot_id, spot.name AS tour_spot_name,
                       story.title, story.sub_title, story.summary, story.intro,
                       story.story_title, story.story, story.img, story.tip,
                       story.did_you_know,
                       EXISTS (
                           SELECT 1 FROM public."UserSavedTourStory" saved
                           WHERE saved.user_id = :userId AND saved.story_id = story.story_id
                       ) AS saved
                FROM public."TourStory" story
                JOIN public."TourSpot" spot ON spot.spot_id = story.spot_id
                WHERE story.spot_id = :spotId
                """, new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("spotId", spotId), (resultSet, rowNumber) -> new StoryCardRow(
                resultSet.getLong("story_id"),
                resultSet.getLong("spot_id"),
                resultSet.getString("tour_spot_name"),
                resultSet.getString("title"),
                resultSet.getString("sub_title"),
                resultSet.getString("summary"),
                resultSet.getString("intro"),
                resultSet.getString("story_title"),
                resultSet.getString("story"),
                resultSet.getString("img"),
                resultSet.getString("tip"),
                resultSet.getString("did_you_know"),
                resultSet.getBoolean("saved")
        ));
        if (rows.isEmpty()) {
            return Optional.empty();
        }
        StoryCardRow row = rows.get(0);
        List<String> hashtags = jdbcTemplate.queryForList("""
                SELECT hashtag.name
                FROM public."StoryCard_HashTag" mapping
                JOIN public."HashTag" hashtag ON hashtag.hashtag_id = mapping.hashtag_id
                WHERE mapping.story_id = :storyId
                ORDER BY hashtag.name
                """, new MapSqlParameterSource("storyId", row.storyId()), String.class);
        return Optional.of(new StoryCardDetailResponse(
                row.storyId(), row.spotId(), row.tourSpotName(), row.title(), row.subTitle(),
                row.summary(), row.intro(), row.storyTitle(), row.story(), row.imageUrl(),
                row.tip(), row.didYouKnow(),
                hashtags, row.saved()));
    }

    private MapSqlParameterSource ids(Long userId, Long storyId) {
        return new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("storyId", storyId);
    }

    private record StoryCardRow(
            Long storyId,
            Long spotId,
            String tourSpotName,
            String title,
            String subTitle,
            String summary,
            String intro,
            String storyTitle,
            String story,
            String imageUrl,
            String tip,
            String didYouKnow,
            boolean saved
    ) {
    }
}
