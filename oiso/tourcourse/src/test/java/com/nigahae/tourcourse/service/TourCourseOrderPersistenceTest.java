package com.nigahae.tourcourse.service;

import com.nigahae.security.jwt.JwtTokenProvider;
import com.nigahae.tourcourse.domain.*;
import com.nigahae.tourcourse.dto.UpdateTourCourseItemRequest;
import com.nigahae.tourcourse.dto.UpdateTourCourseRequest;
import com.nigahae.tourcourse.repository.NearbyPlaceReferenceRepository;
import com.nigahae.tourcourse.repository.TourCourseRepository;
import com.nigahae.tourcourse.repository.TourSpotReferenceRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Exercises real Hibernate flushes against an immediate database uniqueness constraint. */
class TourCourseOrderPersistenceTest {
    private static SessionFactory sessionFactory;
    private Session session;
    private TourCourseService service;
    private TourCourse course;
    private List<Long> itemIds;

    @BeforeAll
    static void createDatabase() {
        sessionFactory = new Configuration()
                .addAnnotatedClass(TourCourse.class)
                .addAnnotatedClass(TourCourseItem.class)
                .setProperty("hibernate.connection.driver_class", "org.h2.Driver")
                .setProperty("hibernate.connection.url", "jdbc:h2:mem:course_order;MODE=PostgreSQL;DB_CLOSE_DELAY=-1")
                .setProperty("hibernate.hbm2ddl.auto", "create-drop")
                .buildSessionFactory();
        try (Session schemaSession = sessionFactory.openSession()) {
            schemaSession.beginTransaction();
            schemaSession.createNativeMutationQuery("""
                    create unique index ux_tour_course_item_day_order
                    on "TourCourseItem" (tour_course_id, day_number, item_order)
                    """).executeUpdate();
            schemaSession.getTransaction().commit();
        }
    }

    @AfterAll
    static void closeDatabase() {
        sessionFactory.close();
    }

    @BeforeEach
    void setUp() {
        session = sessionFactory.openSession();
        session.beginTransaction();
        course = new TourCourse(7L, "왕릉 따라 걷는 길", false, null);
        course.updateStatus(TourCourseStatus.IN_PROGRESS);
        long[] spotIds = {160L, 265L, 266L, 290L};
        for (int index = 0; index < spotIds.length; index++) {
            course.addItem(TourCourseItemCategory.TOUR_SPOT, spotIds[index], null,
                    1, index + 1, index == 0 ? null : TransportType.CAR);
        }
        course.getItems().get(0).updateStatus(TourCourseItemStatus.COMPLETED);
        session.persist(course);
        session.flush();
        itemIds = course.getItems().stream().map(TourCourseItem::getItemId).toList();

        TourCourseRepository repository = mock(TourCourseRepository.class);
        when(repository.findById(course.getTourCourseId())).thenReturn(Optional.of(course));
        doAnswer(invocation -> { session.flush(); return null; }).when(repository).flush();
        TourSpotReferenceRepository spots = mock(TourSpotReferenceRepository.class);
        when(spots.findExistingIds(any())).thenReturn(Set.of(160L, 265L, 266L, 290L));
        NearbyPlaceReferenceRepository nearby = mock(NearbyPlaceReferenceRepository.class);
        when(nearby.findExistingIds(any())).thenReturn(Set.of(3318L));
        service = new TourCourseService(repository, spots, nearby,
                mock(PasswordEncoder.class), mock(JwtTokenProvider.class));
    }

    @AfterEach
    void tearDown() {
        session.getTransaction().rollback();
        session.close();
    }

    @Test
    void insertsRestaurantBetweenExistingItemsAndPreservesProgress() {
        update(List.of(existing(0, 1, 1), restaurant(2), existing(1, 1, 3),
                existing(2, 1, 4), existing(3, 1, 5)));

        TourCourse saved = reload();
        assertThat(saved.getStatus()).isEqualTo(TourCourseStatus.IN_PROGRESS);
        List<TourCourseItem> ordered = ordered(saved);
        assertThat(ordered).extracting(TourCourseItem::getItemOrder).containsExactly(1, 2, 3, 4, 5);
        assertThat(ordered).extracting(TourCourseItem::getSpotId).containsExactly(160L, null, 265L, 266L, 290L);
        assertThat(ordered.get(1).getNearbyPlaceId()).isEqualTo(3318L);
        assertThat(ordered.get(1).getStatus()).isEqualTo(TourCourseItemStatus.PENDING);
        assertThat(ordered.get(0).getStatus()).isEqualTo(TourCourseItemStatus.COMPLETED);
        assertThat(ordered.get(0).getItemId()).isEqualTo(itemIds.get(0));
    }

    @Test
    void swapsExistingItems() {
        update(List.of(existing(1, 1, 1), existing(0, 1, 2), existing(2, 1, 3), existing(3, 1, 4)));
        assertThat(ordered(reload())).extracting(TourCourseItem::getItemId)
                .containsExactly(itemIds.get(1), itemIds.get(0), itemIds.get(2), itemIds.get(3));
    }

    @Test
    void restoresUnchangedOrdersEvenWhenNoOtherFieldsChange() {
        update(List.of(existing(0, 1, 1), existing(1, 1, 2), existing(2, 1, 3), existing(3, 1, 4)));
        assertThat(ordered(reload())).extracting(TourCourseItem::getItemOrder).containsExactly(1, 2, 3, 4);
    }

    @Test
    void deletesAnItemAndUsesItsOrderForNewPlace() {
        update(List.of(existing(0, 1, 1), restaurant(2), existing(2, 1, 3), existing(3, 1, 4)));
        List<TourCourseItem> ordered = ordered(reload());
        assertThat(ordered).extracting(TourCourseItem::getItemOrder).containsExactly(1, 2, 3, 4);
        assertThat(ordered).extracting(TourCourseItem::getItemId).doesNotContain(itemIds.get(1));
        assertThat(ordered.get(1).getNearbyPlaceId()).isEqualTo(3318L);
    }

    @Test
    void movesItemsAcrossDaysWithOverlappingOrders() {
        update(List.of(existing(0, 1, 1), existing(1, 1, 2), existing(2, 2, 1), existing(3, 2, 2)));
        update(List.of(existing(2, 1, 1), existing(3, 1, 2), existing(0, 2, 1), existing(1, 2, 2)));
        assertThat(ordered(reload())).extracting(TourCourseItem::getItemId)
                .containsExactly(itemIds.get(2), itemIds.get(3), itemIds.get(0), itemIds.get(1));
    }

    private UpdateTourCourseItemRequest existing(int index, int day, int order) {
        long[] spotIds = {160L, 265L, 266L, 290L};
        return new UpdateTourCourseItemRequest(itemIds.get(index), TourCourseItemCategory.TOUR_SPOT,
                spotIds[index], null, day, order, order == 1 ? null : TransportType.CAR);
    }

    private UpdateTourCourseItemRequest restaurant(int order) {
        return new UpdateTourCourseItemRequest(null, TourCourseItemCategory.RESTAURANT,
                null, 3318L, 1, order, TransportType.WALK);
    }

    private void update(List<UpdateTourCourseItemRequest> items) {
        service.updateTourCourse(7L, course.getTourCourseId(),
                new UpdateTourCourseRequest(course.getTitle(), false, null, items));
    }

    private TourCourse reload() {
        Long id = course.getTourCourseId();
        session.clear();
        return session.find(TourCourse.class, id);
    }

    private List<TourCourseItem> ordered(TourCourse saved) {
        return saved.getItems().stream().sorted(java.util.Comparator
                .comparingInt(TourCourseItem::getDayNumber)
                .thenComparingInt(TourCourseItem::getItemOrder)).toList();
    }
}
