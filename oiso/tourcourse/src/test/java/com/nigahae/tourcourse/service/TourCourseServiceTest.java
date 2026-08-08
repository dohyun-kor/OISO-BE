package com.nigahae.tourcourse.service;

import com.nigahae.tourcourse.domain.TourCourse;
import com.nigahae.common.constant.ErrorCode;
import com.nigahae.common.exception.CustomException;
import com.nigahae.tourcourse.domain.TransportType;
import com.nigahae.tourcourse.dto.CreateTourCourseItemRequest;
import com.nigahae.tourcourse.dto.CreateTourCourseRequest;
import com.nigahae.tourcourse.dto.TourCourseListRow;
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

import java.util.List;
import java.util.Set;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
