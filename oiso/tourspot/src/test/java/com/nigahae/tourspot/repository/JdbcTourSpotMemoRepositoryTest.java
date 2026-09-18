package com.nigahae.tourspot.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

class JdbcTourSpotMemoRepositoryTest {

    private JdbcTemplate jdbcTemplate;
    private JdbcTourSpotMemoRepository repository;

    @BeforeEach
    void setUp() {
        var dataSource = new DriverManagerDataSource(
                "jdbc:h2:mem:memo;MODE=PostgreSQL;DB_CLOSE_DELAY=-1", "sa", "");
        jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("DROP ALL OBJECTS");
        jdbcTemplate.execute("""
                CREATE TABLE public."TourSpot" (
                    spot_id BIGINT PRIMARY KEY, name VARCHAR(255), name_en VARCHAR(255), img VARCHAR(500)
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE public."TourSpotNearbyPlace" (
                    tour_spot_nearby_course_id BIGINT PRIMARY KEY,
                    category VARCHAR(50), name VARCHAR(255), img VARCHAR(500)
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE public."Memo" (
                    history_id BIGINT PRIMARY KEY, user_id BIGINT NOT NULL,
                    spot_id BIGINT, nearby_place_id BIGINT, review VARCHAR(1000), img VARCHAR(500),
                    created_at TIMESTAMP WITH TIME ZONE, updated_at TIMESTAMP WITH TIME ZONE
                )
                """);
        repository = new JdbcTourSpotMemoRepository(new NamedParameterJdbcTemplate(dataSource));
    }

    @Test
    void findAllExecutesNameFallbackQuery() {
        var now = OffsetDateTime.now();
        jdbcTemplate.update("INSERT INTO public.\"TourSpot\" VALUES (?, ?, ?, ?)",
                10L, "감천문화마을", "Gamcheon Culture Village", "spot.jpg");
        jdbcTemplate.update("INSERT INTO public.\"TourSpotNearbyPlace\" VALUES (?, ?, ?, ?)",
                20L, "restaurant", "근처 식당", "nearby.jpg");
        jdbcTemplate.update("INSERT INTO public.\"Memo\" VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                1L, 7L, 10L, null, "관광지 메모", null, now, now);
        jdbcTemplate.update("INSERT INTO public.\"Memo\" VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                2L, 7L, null, 20L, "식당 메모", null, now.minusMinutes(1), now.minusMinutes(1));

        var result = repository.findAll(7L, 0, 10);

        assertThat(result.totalElements()).isEqualTo(2);
        assertThat(result.content()).extracting(item -> item.spotName())
                .containsExactly("감천문화마을", "근처 식당");
    }
}
