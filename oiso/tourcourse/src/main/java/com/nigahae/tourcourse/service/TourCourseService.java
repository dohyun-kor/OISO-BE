package com.nigahae.tourcourse.service;

import com.nigahae.tourcourse.domain.TourCourse;
import com.nigahae.common.constant.ErrorCode;
import com.nigahae.common.exception.CustomException;
import com.nigahae.tourcourse.dto.CreateTourCourseItemRequest;
import com.nigahae.tourcourse.dto.CreateTourCourseRequest;
import com.nigahae.tourcourse.dto.CreateTourCourseResponse;
import com.nigahae.tourcourse.dto.TourCourseListResponse;
import com.nigahae.tourcourse.dto.TourCourseListRow;
import com.nigahae.tourcourse.dto.TourCourseSummaryResponse;
import com.nigahae.tourcourse.repository.TourCourseRepository;
import com.nigahae.tourcourse.repository.TourSpotReferenceRepository;
import com.nigahae.tourcourse.repository.NearbyPlaceReferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TourCourseService {

    private static final int MAX_TITLE_LENGTH = 20;
    private static final int MAX_ITEM_COUNT = 10;
    private static final String PASSWORD_PATTERN = "\\d{1,5}";
    private static final int MAX_PAGE_SIZE = 100;
    private static final Map<String, String> SORT_PROPERTIES = Map.of(
            "createdAt", "createdAt",
            "updatedAt", "updatedAt",
            "title", "title"
    );

    private final TourCourseRepository tourCourseRepository;
    private final TourSpotReferenceRepository tourSpotReferenceRepository;
    private final NearbyPlaceReferenceRepository nearbyPlaceReferenceRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public CreateTourCourseResponse create(Long userId, CreateTourCourseRequest request) {
        validateRequest(request);

        List<CreateTourCourseItemRequest> items = request.items();
        validateOrders(items);
        validateTransport(items);
        TargetIds targetIds = validateAndGetTargetIds(items);
        validateExistingSpots(targetIds.spotIds());
        validateExistingNearbyPlaces(targetIds.nearbyPlaceIds());

        String encodedPassword = request.shareYn()
                ? passwordEncoder.encode(request.sharedPassword())
                : null;
        TourCourse course = new TourCourse(userId, request.title().trim(), request.shareYn(), encodedPassword);
        items.forEach(item -> course.addItem(
                item.spotId(),
                item.nearbyPlaceId(),
                item.itemOrder(),
                item.transportType()
        ));

        return CreateTourCourseResponse.from(tourCourseRepository.save(course));
    }

    @Transactional(readOnly = true)
    public TourCourseListResponse getMyTourCourses(
            Long userId,
            int page,
            int size,
            String sort,
            String keyword
    ) {
        validatePage(page, size);
        Sort parsedSort = parseSort(sort);
        String normalizedKeyword = keyword == null ? "" : keyword.trim();

        Page<TourCourseListRow> result = tourCourseRepository.findMyTourCourses(
                userId,
                normalizedKeyword,
                PageRequest.of(page, size, parsedSort)
        );
        if (result.isEmpty()) {
            throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        List<TourCourseSummaryResponse> courses = result.getContent().stream()
                .map(TourCourseSummaryResponse::from)
                .toList();
        return new TourCourseListResponse(courses, result.getNumber(), result.getSize(), result.getTotalElements());
    }

    private void validatePage(int page, int size) {
        if (page < 0 || size < 1 || size > MAX_PAGE_SIZE) {
            throw validationException();
        }
    }

    private Sort parseSort(String sortValue) {
        if (sortValue == null) {
            throw validationException();
        }
        String[] parts = sortValue.split(",", -1);
        if (parts.length != 2) {
            throw validationException();
        }
        String property = SORT_PROPERTIES.get(parts[0].trim());
        if (property == null) {
            throw validationException();
        }
        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(parts[1].trim());
        } catch (IllegalArgumentException exception) {
            throw validationException();
        }
        return Sort.by(direction, property);
    }

    private void validateRequest(CreateTourCourseRequest request) {
        if (request == null) {
            throw validationException();
        }
        if (request.title() == null || request.title().isBlank()) {
            throw validationException();
        }
        if (request.title().trim().length() > MAX_TITLE_LENGTH) {
            throw validationException();
        }
        if (request.shareYn() == null) {
            throw validationException();
        }
        validatePassword(request.shareYn(), request.sharedPassword());
        if (request.items() == null || request.items().isEmpty()) {
            throw validationException();
        }
        if (request.items().size() > MAX_ITEM_COUNT) {
            throw validationException();
        }
    }

    private void validatePassword(boolean shareYn, String password) {
        if (shareYn && (password == null || password.isBlank())) {
            throw validationException();
        }
        if (!shareYn && password != null) {
            throw validationException();
        }
        if (shareYn && !password.matches(PASSWORD_PATTERN)) {
            throw validationException();
        }
    }

    private void validateOrders(List<CreateTourCourseItemRequest> items) {
        Set<Integer> orders = new HashSet<>();
        for (CreateTourCourseItemRequest item : items) {
            if (item == null) {
                throw validationException();
            }
            if (item.itemOrder() == null || item.itemOrder() < 1) {
                throw validationException();
            }
            if (!orders.add(item.itemOrder())) {
                throw validationException();
            }
        }
        for (int expected = 1; expected <= items.size(); expected++) {
            if (!orders.contains(expected)) {
                throw validationException();
            }
        }
    }

    private void validateTransport(List<CreateTourCourseItemRequest> items) {
        CreateTourCourseItemRequest first = items.stream()
                .filter(item -> item.itemOrder() == 1)
                .findFirst()
                .orElseThrow(this::validationException);
        if (first.transportType() != null) {
            throw validationException();
        }
        if (items.stream().anyMatch(item -> item.itemOrder() > 1 && item.transportType() == null)) {
            throw validationException();
        }
    }

    private TargetIds validateAndGetTargetIds(List<CreateTourCourseItemRequest> items) {
        Set<Long> uniqueSpotIds = new HashSet<>();
        Set<Long> uniqueNearbyPlaceIds = new HashSet<>();
        for (CreateTourCourseItemRequest item : items) {
            boolean hasSpot = item.spotId() != null;
            boolean hasNearbyPlace = item.nearbyPlaceId() != null;
            if (hasSpot == hasNearbyPlace) {
                throw validationException();
            }
            if (hasSpot && (item.spotId() < 1 || !uniqueSpotIds.add(item.spotId()))) {
                throw validationException();
            }
            if (hasNearbyPlace && (item.nearbyPlaceId() < 1
                    || !uniqueNearbyPlaceIds.add(item.nearbyPlaceId()))) {
                throw validationException();
            }
        }
        return new TargetIds(
                items.stream().map(CreateTourCourseItemRequest::spotId).filter(java.util.Objects::nonNull).toList(),
                items.stream().map(CreateTourCourseItemRequest::nearbyPlaceId).filter(java.util.Objects::nonNull).toList()
        );
    }

    private void validateExistingSpots(List<Long> spotIds) {
        if (spotIds.isEmpty()) {
            return;
        }
        Set<Long> existingIds = tourSpotReferenceRepository.findExistingIds(spotIds);
        spotIds.stream()
                .filter(spotId -> !existingIds.contains(spotId))
                .findFirst()
                .ifPresent(spotId -> {
                    throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND);
                });
    }

    private void validateExistingNearbyPlaces(List<Long> nearbyPlaceIds) {
        if (nearbyPlaceIds.isEmpty()) {
            return;
        }
        Set<Long> existingIds = nearbyPlaceReferenceRepository.findExistingIds(nearbyPlaceIds);
        nearbyPlaceIds.stream()
                .filter(id -> !existingIds.contains(id))
                .findFirst()
                .ifPresent(id -> {
                    throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND);
                });
    }

    private CustomException validationException() {
        return new CustomException(ErrorCode.VALIDATION_ERROR);
    }

    private record TargetIds(List<Long> spotIds, List<Long> nearbyPlaceIds) {
    }
}
