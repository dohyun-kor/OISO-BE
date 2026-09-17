package com.nigahae.tourcourse.repository;

import com.nigahae.tourcourse.domain.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.Assertions.assertThat;

class LocalizedCourseQueryTest {
    private SessionFactory factory;
    private Session session;
    private TourCourseRepository repository;

    @BeforeEach
    void setUp() {
        factory = new Configuration().addAnnotatedClass(TourCourse.class)
                .addAnnotatedClass(TourCourseItem.class).addAnnotatedClass(TourSpotReference.class)
                .addAnnotatedClass(NearbyPlaceReference.class)
                .setProperty("hibernate.connection.driver_class", "org.h2.Driver")
                .setProperty("hibernate.connection.url", "jdbc:h2:mem:localized_courses;MODE=PostgreSQL")
                .setProperty("hibernate.hbm2ddl.auto", "create-drop").buildSessionFactory();
        session = factory.openSession();
        session.beginTransaction();
        seed("Featured", "English title", true);
        seed("Fallback", " ", true);
        seed("Personal", "Do not use", false);
        session.flush();
        repository = new JpaRepositoryFactory(session).getRepository(TourCourseRepository.class);
    }

    private void seed(String title, String english, boolean featured) {
        var course = new TourCourse(7L, title, false, null);
        ReflectionTestUtils.setField(course, "titleEn", english);
        ReflectionTestUtils.setField(course, "featuredYn", featured);
        session.persist(course);
    }

    @AfterEach
    void close() {
        RequestContextHolder.resetRequestAttributes();
        if (session != null) { session.getTransaction().rollback(); session.close(); }
        if (factory != null) factory.close();
    }

    @Test
    void validatesRepositoryAndExecutesLocalizedListsWithFallback() {
        var page = PageRequest.of(0, 10);
        assertThat(repository.findMyTourCourses(7L, "", page).getContent())
                .extracting(row -> row.title()).containsExactlyInAnyOrder("Featured", "Fallback", "Personal");
        var request = new MockHttpServletRequest();
        request.addHeader("Accept-Language", "en");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        assertThat(repository.findMyTourCourses(7L, "", page).getContent())
                .extracting(row -> row.title()).containsExactlyInAnyOrder("English title", "Fallback", "Personal");
        assertThat(repository.findRandomFeaturedCourses(page))
                .extracting(row -> row.title()).containsExactlyInAnyOrder("English title", "Fallback");
        assertThat(repository.findMyTourCourses(7L, "", PageRequest.of(0, 1)).getTotalElements()).isEqualTo(3);
    }
}
