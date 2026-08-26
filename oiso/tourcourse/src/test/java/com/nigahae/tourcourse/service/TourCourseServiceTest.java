package com.nigahae.tourcourse.service;

import com.nigahae.tourcourse.domain.TourCourse;
import com.nigahae.tourcourse.domain.TourCourseItem;
import com.nigahae.common.constant.ErrorCode;
import com.nigahae.common.exception.CustomException;
import com.nigahae.tourcourse.domain.TransportType;
import com.nigahae.tourcourse.domain.TourCourseItemCategory;
import com.nigahae.tourcourse.dto.CreateTourCourseItemRequest;
import com.nigahae.tourcourse.dto.CreateTourCourseRequest;
import com.nigahae.tourcourse.dto.TourCourseListRow;
import com.nigahae.tourcourse.dto.TourCourseItemDetailRow;
import com.nigahae.tourcourse.dto.UpdateTourCourseItemRequest;
import com.nigahae.tourcourse.dto.UpdateTourCourseRequest;
import com.nigahae.tourcourse.repository.TourCourseRepository;
import com.nigahae.tourcourse.repository.TourSpotReferenceRepository;
import com.nigahae.tourcourse.repository.NearbyPlaceReferenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Set;
import java.util.Optional;
import java.util.Map;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class TourCourseServiceTest {

    @Mock TourCourseRepository tourCourseRepository;
    @Mock TourSpotReferenceRepository tourSpotReferenceRepository;
    @Mock NearbyPlaceReferenceRepository nearbyPlaceReferenceRepository;
    @Mock PasswordEncoder passwordEncoder;

    private TourCourseService tourCourseService;

    @BeforeEach
    void setUp() {
        tourCourseService = new TourCourseService(
                tourCourseRepository,
                tourSpotReferenceRepository,
                nearbyPlaceReferenceRepository,
                passwordEncoder
        );
    }

    @Test
    void createsTourCourse() {
        CreateTourCourseRequest request = validRequest();
        when(tourSpotReferenceRepository.findExistingIds(List.of(1L, 2L, 3L)))
                .thenReturn(Set.of(1L, 2L, 3L));
        when(tourCourseRepository.save(any(TourCourse.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = tourCourseService.create(7L, request);

        assertThat(response.title()).isEqualTo("경주 역사 여행 코스");
        assertThat(response.itemCount()).isEqualTo(3);
        assertThat(response.shareYn()).isFalse();
        verify(tourCourseRepository).save(any(TourCourse.class));
    }

    @Test
    void rejectsEmptyItems() {
        CreateTourCourseRequest request = new CreateTourCourseRequest("코스", false, null, List.of());

        assertValidationError(request);
    }

    @Test
    void rejectsTransportOnFirstItem() {
        CreateTourCourseRequest request = new CreateTourCourseRequest(
                "코스", false, null,
                List.of(new CreateTourCourseItemRequest(1L, 1, TransportType.CAR))
        );

        assertValidationError(request);
    }

    @Test
    void rejectsDuplicateOrders() {
        CreateTourCourseRequest request = new CreateTourCourseRequest(
                "코스", false, null,
                List.of(
                        new CreateTourCourseItemRequest(1L, 1, null),
                        new CreateTourCourseItemRequest(2L, 1, TransportType.WALK)
                )
        );

        assertValidationError(request);
    }

    @Test
    void rejectsNonConsecutiveOrders() {
        CreateTourCourseRequest request = new CreateTourCourseRequest(
                "코스", false, null,
                List.of(
                        new CreateTourCourseItemRequest(1L, 1, null),
                        new CreateTourCourseItemRequest(2L, 3, TransportType.WALK)
                )
        );

        assertValidationError(request);
    }

    @Test
    void createsItemsWithOrdersRestartingForEachDay() {
        CreateTourCourseRequest request = new CreateTourCourseRequest(
                "2일 코스", false, null,
                List.of(
                        new CreateTourCourseItemRequest(
                                TourCourseItemCategory.TOUR_SPOT, 1L, null, 1, 1, null),
                        new CreateTourCourseItemRequest(
                                TourCourseItemCategory.TOUR_SPOT, 2L, null, 2, 1, null)
                ));
        when(tourSpotReferenceRepository.findExistingIds(List.of(1L, 2L)))
                .thenReturn(Set.of(1L, 2L));
        when(tourCourseRepository.save(any(TourCourse.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        tourCourseService.create(7L, request);

        TourCourse saved = org.mockito.Mockito.mockingDetails(tourCourseRepository).getInvocations()
                .stream().filter(invocation -> invocation.getMethod().getName().equals("save"))
                .map(invocation -> (TourCourse) invocation.getArgument(0)).findFirst().orElseThrow();
        assertThat(saved.getItems()).extracting(TourCourseItem::getDayNumber)
                .containsExactly(1, 2);
        assertThat(saved.getItems()).extracting(TourCourseItem::getItemOrder)
                .containsExactly(1, 1);
    }

    @Test
    void rejectsDuplicateSpots() {
        CreateTourCourseRequest request = new CreateTourCourseRequest(
                "코스", false, null,
                List.of(
                        new CreateTourCourseItemRequest(1L, 1, null),
                        new CreateTourCourseItemRequest(1L, 2, TransportType.BIKE)
                )
        );

        assertValidationError(request);
    }

    @Test
    void createsCourseContainingNearbyPlace() {
        CreateTourCourseRequest request = new CreateTourCourseRequest(
                "코스", false, null,
                List.of(
                        new CreateTourCourseItemRequest(1L, null, 1, null),
                        new CreateTourCourseItemRequest(null, 10L, 2, TransportType.WALK)
                )
        );
        when(tourSpotReferenceRepository.findExistingIds(List.of(1L))).thenReturn(Set.of(1L));
        when(nearbyPlaceReferenceRepository.findExistingIds(List.of(10L))).thenReturn(Set.of(10L));
        when(tourCourseRepository.save(any(TourCourse.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertThat(tourCourseService.create(7L, request).itemCount()).isEqualTo(2);
    }

    @Test
    void rejectsItemWithBothTargetTypes() {
        CreateTourCourseRequest request = new CreateTourCourseRequest(
                "코스", false, null,
                List.of(new CreateTourCourseItemRequest(1L, 10L, 1, null))
        );

        assertValidationError(request);
    }

    @Test
    void requiresNumericSharedPasswordUpToFiveDigits() {
        CreateTourCourseRequest missing = new CreateTourCourseRequest(
                "코스", true, null,
                List.of(new CreateTourCourseItemRequest(1L, 1, null))
        );
        CreateTourCourseRequest invalid = new CreateTourCourseRequest(
                "코스", true, "123456",
                List.of(new CreateTourCourseItemRequest(1L, 1, null))
        );

        assertValidationError(missing);
        assertValidationError(invalid);
    }

    @Test
    void rejectsMissingSpot() {
        CreateTourCourseRequest request = validRequest();
        when(tourSpotReferenceRepository.findExistingIds(List.of(1L, 2L, 3L)))
                .thenReturn(Set.of(1L, 3L));

        assertThatThrownBy(() -> tourCourseService.create(7L, request))
                .isInstanceOfSatisfying(CustomException.class, exception -> {
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
                });
    }

    @Test
    void getsAuthenticatedUsersTourCourses() {
        LocalDateTime now = LocalDateTime.of(2026, 7, 11, 15, 0);
        TourCourseListRow row = new TourCourseListRow(
                1L, "경주 역사 여행 코스", false, "image.jpg", 3L,
                13_500, 2_300, now, now
        );
        when(tourCourseRepository.findMyTourCourses(eq(7L), eq("경주"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(row)));

        var response = tourCourseService.getMyTourCourses(7L, 0, 10, "createdAt,desc", " 경주 ");

        assertThat(response.tourCourses()).hasSize(1);
        assertThat(response.tourCourses().get(0).thumbnailImg()).isEqualTo("image.jpg");
        assertThat(response.tourCourses().get(0).itemCount()).isEqualTo(3);
        assertThat(response.totalCount()).isEqualTo(1);
        verify(tourCourseRepository).findMyTourCourses(eq(7L), eq("경주"), any(Pageable.class));
    }

    @Test
    void rejectsInvalidPageAndSize() {
        assertThatThrownBy(() -> tourCourseService.getMyTourCourses(7L, -1, 10, "createdAt,desc", null))
                .isInstanceOfSatisfying(CustomException.class, exception ->
                        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.VALIDATION_ERROR));
        assertThatThrownBy(() -> tourCourseService.getMyTourCourses(7L, 0, 101, "createdAt,desc", null))
                .isInstanceOfSatisfying(CustomException.class, exception ->
                        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.VALIDATION_ERROR));
    }

    @Test
    void rejectsInvalidSort() {
        assertThatThrownBy(() -> tourCourseService.getMyTourCourses(7L, 0, 10, "unknown,desc", null))
                .isInstanceOfSatisfying(CustomException.class, exception ->
                        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.VALIDATION_ERROR));
        assertThatThrownBy(() -> tourCourseService.getMyTourCourses(7L, 0, 10, "createdAt,wrong", null))
                .isInstanceOfSatisfying(CustomException.class, exception ->
                        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.VALIDATION_ERROR));
    }

    @Test
    void rejectsEmptyCourseResult() {
        when(tourCourseRepository.findMyTourCourses(eq(7L), eq(""), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        assertThatThrownBy(() -> tourCourseService.getMyTourCourses(7L, 0, 10, "createdAt,desc", null))
                .isInstanceOfSatisfying(CustomException.class, exception ->
                        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND));
    }

    @Test
    void deletesOwnedTourCourse() {
        TourCourse course = new TourCourse(7L, "삭제할 코스", false, null);
        when(tourCourseRepository.findById(1L)).thenReturn(Optional.of(course));

        tourCourseService.deleteTourCourse(7L, 1L);

        verify(tourCourseRepository).delete(course);
    }

    @Test
    void rejectsDeletingAnotherUsersTourCourse() {
        TourCourse course = new TourCourse(8L, "다른 사용자 코스", false, null);
        when(tourCourseRepository.findById(1L)).thenReturn(Optional.of(course));

        assertThatThrownBy(() -> tourCourseService.deleteTourCourse(7L, 1L))
                .isInstanceOfSatisfying(CustomException.class, exception ->
                        assertThat(exception.getErrorCode())
                                .isEqualTo(ErrorCode.TOUR_COURSE_DELETE_FORBIDDEN));
    }

    @Test
    void createsTourCourseWithNearbyPlace() {
        CreateTourCourseRequest request = new CreateTourCourseRequest(
                "맛집 코스", false, null,
                List.of(
                        new CreateTourCourseItemRequest(
                                TourCourseItemCategory.TOUR_SPOT, 1L, null, 1, null),
                        new CreateTourCourseItemRequest(
                                TourCourseItemCategory.RESTAURANT, null, 101L, 2, TransportType.WALK)
                ));
        when(tourSpotReferenceRepository.findExistingIds(List.of(1L))).thenReturn(Set.of(1L));
        when(nearbyPlaceReferenceRepository.findExistingIds(List.of(101L)))
                .thenReturn(Set.of(101L));
        when(tourCourseRepository.save(any(TourCourse.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        tourCourseService.create(7L, request);

        TourCourse saved = org.mockito.Mockito.mockingDetails(tourCourseRepository).getInvocations()
                .stream()
                .filter(invocation -> invocation.getMethod().getName().equals("save"))
                .map(invocation -> (TourCourse) invocation.getArgument(0))
                .findFirst()
                .orElseThrow();
        assertThat(saved.getItems().get(1).getSpotId()).isNull();
        assertThat(saved.getItems().get(1).getNearbyPlaceId()).isEqualTo(101L);
    }

    @Test
    void getsOwnersTourCourseDetailAndSumsRecordedMovement() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 7, 11, 15, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2026, 7, 12, 10, 30);
        TourCourse course = new TourCourse(7L, "경주 역사 여행 코스", false, null);
        ReflectionTestUtils.setField(course, "tourCourseId", 1L);
        ReflectionTestUtils.setField(course, "createdAt", createdAt);
        ReflectionTestUtils.setField(course, "updatedAt", updatedAt);

        TourCourseItemDetailRow first = detailRow(11L, 1, "TOUR_SPOT", null, 999, 99);
        TourCourseItemDetailRow second = detailRow(12L, 2, "RESTAURANT", "CAR", 12_000, 1_200);
        when(tourCourseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(tourCourseRepository.findDetailItems(1L, 7L)).thenReturn(List.of(first, second));

        var response = tourCourseService.getTourCourseDetail(7L, 1L);

        assertThat(response.tourCourseId()).isEqualTo(1L);
        assertThat(response.items()).hasSize(2);
        assertThat(response.items().get(0).transportType()).isNull();
        assertThat(response.items().get(1).nearbyPlaceId()).isEqualTo(101L);
        assertThat(response.items().get(1).parentTourSpotId()).isEqualTo(1L);
        assertThat(response.items().get(1).like()).isFalse();
    }

    @Test
    void rejectsInvalidMissingAndOtherUsersCourseDetail() {
        assertThatThrownBy(() -> tourCourseService.getTourCourseDetail(7L, 0L))
                .isInstanceOfSatisfying(CustomException.class, exception ->
                        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_TOUR_COURSE_ID));

        when(tourCourseRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> tourCourseService.getTourCourseDetail(7L, 99L))
                .isInstanceOfSatisfying(CustomException.class, exception ->
                        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.TOUR_COURSE_NOT_FOUND));

        TourCourse otherUsersCourse = new TourCourse(8L, "다른 사용자 코스", true, "encoded");
        when(tourCourseRepository.findById(1L)).thenReturn(Optional.of(otherUsersCourse));
        assertThatThrownBy(() -> tourCourseService.getTourCourseDetail(7L, 1L))
                .isInstanceOfSatisfying(CustomException.class, exception ->
                        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.TOUR_COURSE_FORBIDDEN));
    }

    @Test
    void synchronizesTourCourseToRequestedFinalState() {
        TourCourse course = new TourCourse(7L, "기존 코스", false, null);
        course.addItem(1L, 1, null);
        course.addItem(2L, 2, TransportType.CAR);
        ReflectionTestUtils.setField(course, "tourCourseId", 1L);
        ReflectionTestUtils.setField(course, "createdAt", LocalDateTime.of(2026, 7, 11, 15, 0));
        ReflectionTestUtils.setField(course, "updatedAt", LocalDateTime.of(2026, 7, 11, 15, 0));
        ReflectionTestUtils.setField(course.getItems().get(0), "itemId", 11L);
        ReflectionTestUtils.setField(course.getItems().get(1), "itemId", 12L);

        UpdateTourCourseRequest request = new UpdateTourCourseRequest(
                "수정된 코스",
                false,
                null,
                List.of(
                        new UpdateTourCourseItemRequest(null, 8L, 1, null, null, null),
                        new UpdateTourCourseItemRequest(11L, 2L, 2, TransportType.CAR, 12_000, 1_200)
                )
        );
        TourCourseItemDetailRow updatedFirst =
                detailRow(15L, 1, "TOUR_SPOT", null, null, null);
        TourCourseItemDetailRow updatedSecond =
                detailRow(11L, 2, "TOUR_SPOT", "CAR", 12_000, 1_200);
        when(tourCourseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(tourSpotReferenceRepository.findExistingIds(List.of(8L, 2L))).thenReturn(Set.of(8L, 2L));
        when(tourCourseRepository.findDetailItems(1L, 7L))
                .thenReturn(List.of(updatedFirst, updatedSecond));

        var response = tourCourseService.updateTourCourse(7L, 1L, request);

        assertThat(course.getTitle()).isEqualTo("수정된 코스");
        assertThat(course.getItems()).hasSize(2);
        assertThat(course.getItems()).noneMatch(item -> Long.valueOf(12L).equals(item.getItemId()));
        verify(tourCourseRepository).offsetItemOrders(1L, 1_000);
        verify(tourCourseRepository).restoreItemOrders(1L, 1_000);
    }

    @Test
    void rejectsInvalidUpdateMovementAndForeignItem() {
        TourCourse course = new TourCourse(7L, "코스", false, null);
        course.addItem(1L, 1, null);
        ReflectionTestUtils.setField(course.getItems().get(0), "itemId", 11L);
        when(tourCourseRepository.findById(1L)).thenReturn(Optional.of(course));

        UpdateTourCourseRequest firstMovement = new UpdateTourCourseRequest(
                "코스", false, null,
                List.of(new UpdateTourCourseItemRequest(11L, 1L, 1, TransportType.CAR, 1, 1))
        );
        assertThatThrownBy(() -> tourCourseService.updateTourCourse(7L, 1L, firstMovement))
                .isInstanceOfSatisfying(CustomException.class, exception -> {
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.TOUR_COURSE_FIRST_ITEM_MOVEMENT);
                    assertThat(exception.getData()).isEqualTo(Map.of(
                            "field", "items[0].transportType"));
                });

        UpdateTourCourseRequest foreignItem = new UpdateTourCourseRequest(
                "코스", false, null,
                List.of(new UpdateTourCourseItemRequest(99L, 1L, 1, null, null, null))
        );
        when(tourSpotReferenceRepository.findExistingIds(List.of(1L))).thenReturn(Set.of(1L));
        assertThatThrownBy(() -> tourCourseService.updateTourCourse(7L, 1L, foreignItem))
                .isInstanceOfSatisfying(CustomException.class, exception -> {
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.TOUR_COURSE_ITEM_NOT_BELONG);
                    assertThat(exception.getData()).isEqualTo(Map.of(
                            "field", "items[0].itemId", "itemId", 99L));
                });
    }

    @Test
    void identifiesInvalidUpdateFieldAndReason() {
        TourCourse course = new TourCourse(7L, "코스", false, null);
        when(tourCourseRepository.findById(1L)).thenReturn(Optional.of(course));
        UpdateTourCourseRequest request = new UpdateTourCourseRequest(
                "   ", false, null,
                List.of(new UpdateTourCourseItemRequest(null, 1L, 1, null, null, null))
        );

        assertThatThrownBy(() -> tourCourseService.updateTourCourse(7L, 1L, request))
                .isInstanceOfSatisfying(CustomException.class, exception -> {
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_TOUR_COURSE_UPDATE);
                    assertThat(exception.getMessage()).isEqualTo("관광 코스 제목은 필수입니다.");
                    assertThat(exception.getData()).isEqualTo(Map.of("field", "title"));
                });
    }

    private TourCourseItemDetailRow detailRow(
            Long itemId,
            int itemOrder,
            String category,
            String transportType,
            Integer distanceMeter,
            Integer durationSecond
    ) {
        TourCourseItemDetailRow row = mock(TourCourseItemDetailRow.class);
        when(row.getItemId()).thenReturn(itemId);
        when(row.getDayNumber()).thenReturn(1);
        when(row.getItemOrder()).thenReturn(itemOrder);
        when(row.getCategory()).thenReturn(category);
        when(row.getSpotId()).thenReturn("TOUR_SPOT".equals(category) ? 1L : null);
        when(row.getNearbyPlaceId()).thenReturn("TOUR_SPOT".equals(category) ? null : 101L);
        when(row.getParentTourSpotId()).thenReturn("TOUR_SPOT".equals(category) ? null : 1L);
        when(row.getName()).thenReturn("장소");
        when(row.getLiked()).thenReturn("TOUR_SPOT".equals(category));
        if (itemOrder != 1) {
            when(row.getTransportType()).thenReturn(transportType);
        }
        return row;
    }

    private CreateTourCourseRequest validRequest() {
        return new CreateTourCourseRequest(
                "경주 역사 여행 코스",
                false,
                null,
                List.of(
                        new CreateTourCourseItemRequest(1L, 1, null),
                        new CreateTourCourseItemRequest(2L, 2, TransportType.CAR),
                        new CreateTourCourseItemRequest(3L, 3, TransportType.WALK)
                )
        );
    }

    private void assertValidationError(CreateTourCourseRequest request) {
        assertThatThrownBy(() -> tourCourseService.create(7L, request))
                .isInstanceOfSatisfying(CustomException.class, exception -> {
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.VALIDATION_ERROR);
                });
    }
}
