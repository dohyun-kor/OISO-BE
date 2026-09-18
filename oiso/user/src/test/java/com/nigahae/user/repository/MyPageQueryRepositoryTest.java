package com.nigahae.user.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

class MyPageQueryRepositoryTest {

    private MyPageQueryRepository repository;

    @BeforeEach
    void setUp() {
        var dataSource = new DriverManagerDataSource(
                "jdbc:h2:mem:mypage;MODE=PostgreSQL;DB_CLOSE_DELAY=-1", "sa", "");
        var jdbc = new JdbcTemplate(dataSource);
        jdbc.execute("DROP ALL OBJECTS");
        jdbc.execute("""
                CREATE TABLE public."TourSpot" (
                    spot_id BIGINT PRIMARY KEY, name VARCHAR(255), name_en VARCHAR(255), img VARCHAR(500)
                )
                """);
        jdbc.execute("""
                CREATE TABLE public."Memo" (
                    history_id BIGINT PRIMARY KEY, user_id BIGINT NOT NULL, spot_id BIGINT,
                    review VARCHAR(1000), img VARCHAR(500), updated_at TIMESTAMP WITH TIME ZONE
                )
                """);
        jdbc.update("INSERT INTO public.\"TourSpot\" VALUES (?, ?, ?, ?)",
                10L, "감천문화마을", "Gamcheon Culture Village", "spot.jpg");
        jdbc.update("INSERT INTO public.\"Memo\" VALUES (?, ?, ?, ?, ?, ?)",
                1L, 7L, 10L, "메모", "memo.jpg", OffsetDateTime.now());
        repository = new MyPageQueryRepository(new NamedParameterJdbcTemplate(dataSource));
    }

    @AfterEach
    void cleanUp() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void findMemosSuppliesLanguageParameter() {
        var request = new MockHttpServletRequest();
        request.addHeader("Accept-Language", "en");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        var memos = repository.findMemos(7L);

        assertThat(memos).singleElement()
                .extracting(MyPageQueryRepository.MemoItemRow::spotName)
                .isEqualTo("Gamcheon Culture Village");
    }
}
