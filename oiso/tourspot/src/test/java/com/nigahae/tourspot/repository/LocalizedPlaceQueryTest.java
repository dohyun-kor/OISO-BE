package com.nigahae.tourspot.repository;

import com.nigahae.tourspot.domain.TourSpot;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.Assertions.assertThat;

class LocalizedPlaceQueryTest {
    private SessionFactory factory;
    private Session session;
    private TourSpotQueryRepository repository;

    @BeforeEach void setUp() {
        factory = new Configuration().addAnnotatedClass(TourSpot.class)
                .setProperty("hibernate.connection.driver_class", "org.h2.Driver")
                .setProperty("hibernate.connection.url", "jdbc:h2:mem:localized_places;MODE=PostgreSQL")
                .setProperty("hibernate.hbm2ddl.auto", "create-drop").buildSessionFactory();
        session = factory.openSession();
        session.beginTransaction();
        session.persist(TourSpot.builder().name("불국사").nameEn("Bulguksa Temple")
                .address("경주").addressEn("Gyeongju").contentTypeId(12L).build());
        session.persist(TourSpot.builder().name("석굴암").nameEn(" ").contentTypeId(12L).build());
        session.flush();
        session.createNativeMutationQuery("""
                create table "TourSpotNearbyPlace" (
                    tour_spot_nearby_course_id bigint, spot_id bigint, content_id bigint, content_type_id integer,
                    category varchar, name varchar, name_en varchar, img varchar, overview varchar,
                    overview_en varchar, address varchar, address_en varchar, map_y double precision, map_x double precision)
                """).executeUpdate();
        repository = new JpaRepositoryFactory(session).getRepository(TourSpotQueryRepository.class);
    }

    @AfterEach void close() {
        RequestContextHolder.resetRequestAttributes();
        if (session != null) { session.getTransaction().rollback(); session.close(); }
        if (factory != null) factory.close();
    }

    @Test void nativePlaceListSearchesAndReturnsEnglishWithFallback() {
        english();
        var result = repository.findPlaces("", "Bulguksa", PageRequest.of(0, 1));
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Bulguksa Temple");
        assertThat(repository.findPlaces("", "석굴암", PageRequest.of(0, 10)).getContent().get(0).getName())
                .isEqualTo("석굴암");
        RequestContextHolder.resetRequestAttributes();
        assertThat(repository.findPlaces("", "불국사", PageRequest.of(0, 10)).getContent().get(0).getName())
                .isEqualTo("불국사");
    }

    @Test void jpqlSearchFindsEnglishNamesWithoutChangingStoredKorean() {
        english();
        var result = repository.searchByKeyword("Bulguksa", PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("불국사");
        assertThat(result.getContent().get(0).getLocalizedName()).isEqualTo("Bulguksa Temple");
    }

    private void english() {
        var request = new MockHttpServletRequest();
        request.addHeader("Accept-Language", "en");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
