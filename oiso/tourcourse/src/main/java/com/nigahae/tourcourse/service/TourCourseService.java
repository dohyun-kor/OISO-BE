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
import com.nigahae.tourcourse.dto.TourCourseDetailResponse;
import com.nigahae.tourcourse.dto.TourCourseItemDetailResponse;
import com.nigahae.tourcourse.dto.UpdateTourCourseItemRequest;
import com.nigahae.tourcourse.dto.UpdateTourCourseRequest;
import com.nigahae.tourcourse.domain.TourCourseItem;
import com.nigahae.tourcourse.domain.TourCourseItemCategory;
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
import java.util.HashMap;
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
    private static final int ITEM_ORDER_OFFSET = 1_000;
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
                item.category(), item.spotId(), item.nearbyPlaceId(),
                item.dayNumber(), item.itemOrder(),
                item.transportType(), null, null));

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

    @Transactional(readOnly = true)
    public TourCourseDetailResponse getTourCourseDetail(Long userId, Long tourCourseId) {
        validateCourseId(tourCourseId);
        TourCourse course = getOwnedCourse(userId, tourCourseId, ErrorCode.TOUR_COURSE_FORBIDDEN);
        return toDetailResponse(course, userId);
    }

    @Transactional
    public TourCourseDetailResponse updateTourCourse(
            Long userId, Long tourCourseId, UpdateTourCourseRequest request
    ) {
        validateCourseId(tourCourseId);
        TourCourse course = getOwnedCourse(
                userId, tourCourseId, ErrorCode.TOUR_COURSE_UPDATE_FORBIDDEN);

        validateUpdateRequest(request);
        validateUpdateItems(request.items());
        validateUpdatePlaces(request.items());

        Map<Long, TourCourseItem> existingItems = new HashMap<>();
        course.getItems().forEach(item -> existingItems.put(item.getItemId(), item));
        validateItemOwnership(request.items(), existingItems);

        String encodedPassword = request.shareYn()
                ? passwordEncoder.encode(request.sharedPassword())
                : null;
        course.updateBasicInfo(request.title().trim(), request.shareYn(), encodedPassword);

        Set<Long> retainedItemIds = request.items().stream()
                .map(UpdateTourCourseItemRequest::itemId)
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toSet());

        if (!existingItems.isEmpty()) {
            tourCourseRepository.offsetItemOrders(tourCourseId, ITEM_ORDER_OFFSET);
        }
        course.getItems().removeIf(item ->
                item.getItemId() != null && !retainedItemIds.contains(item.getItemId()));

        for (UpdateTourCourseItemRequest item : request.items()) {
            if (item.itemId() == null) {
                course.addItem(
                        item.category(), item.spotId(), item.nearbyPlaceId(),
                        item.dayNumber(), item.itemOrder(), item.transportType(),
                        item.distanceMeter(), item.durationSecond());
            } else {
                existingItems.get(item.itemId()).update(
                        item.category(), item.spotId(), item.nearbyPlaceId(),
                        item.dayNumber(), item.itemOrder() + ITEM_ORDER_OFFSET, item.transportType(),
                        item.distanceMeter(), item.durationSecond());
            }
        }

        int totalDistance = request.items().stream()
                .map(UpdateTourCourseItemRequest::distanceMeter)
                .filter(java.util.Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();
        int totalDuration = request.items().stream()
                .map(UpdateTourCourseItemRequest::durationSecond)
                .filter(java.util.Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();
        course.updateMovementTotals(totalDistance, totalDuration);

        tourCourseRepository.flush();
        if (!existingItems.isEmpty()) {
            tourCourseRepository.restoreItemOrders(tourCourseId, ITEM_ORDER_OFFSET);
        }
        return toDetailResponse(course, userId);
    }

    @Transactional
    public void deleteTourCourse(Long userId, Long tourCourseId) {
        validateCourseId(tourCourseId);
        TourCourse course = getOwnedCourse(
                userId, tourCourseId, ErrorCode.TOUR_COURSE_DELETE_FORBIDDEN);
        tourCourseRepository.delete(course);
    }

    private void validateCourseId(Long tourCourseId) {
        if (tourCourseId == null || tourCourseId < 1) {
            throw new CustomException(
                    ErrorCode.INVALID_TOUR_COURSE_ID, Map.of("field", "tourCourseId"));
        }
    }

    private TourCourse getOwnedCourse(Long userId, Long tourCourseId, ErrorCode forbiddenCode) {
        TourCourse course = tourCourseRepository.findById(tourCourseId)
                .orElseThrow(() -> new CustomException(ErrorCode.TOUR_COURSE_NOT_FOUND));
        if (!course.getUserId().equals(userId)) {
            throw new CustomException(forbiddenCode);
        }
        return course;
    }

    private TourCourseDetailResponse toDetailResponse(TourCourse course, Long userId) {
        List<TourCourseItemDetailResponse> items = tourCourseRepository
                .findDetailItems(course.getTourCourseId(), userId)
                .stream()
                .map(TourCourseItemDetailResponse::from)
                .toList();
        return TourCourseDetailResponse.from(course, items);
    }

    private void validateUpdateRequest(UpdateTourCourseRequest request) {
        if (request == null) {
            throw updateValidationException("request", "요청 본문이 필요합니다.");
        }
        if (request.title() == null || request.title().isBlank()) {
            throw updateValidationException("title", "관광 코스 제목은 필수입니다.");
        }
        if (request.title().trim().length() > MAX_TITLE_LENGTH) {
            throw updateValidationException(
                    "title", "관광 코스 제목은 " + MAX_TITLE_LENGTH + "자 이하여야 합니다.");
        }
        if (request.shareYn() == null) {
            throw updateValidationException("shareYn", "공유 여부는 필수입니다.");
        }
        validateUpdatePassword(request.shareYn(), request.sharedPassword());
        if (request.items() == null || request.items().isEmpty()) {
            throw new CustomException(
                    ErrorCode.TOUR_COURSE_ITEMS_REQUIRED, Map.of("field", "items"));
        }
        if (request.items().size() > MAX_ITEM_COUNT) {
            throw updateValidationException(
                    "items", "관광지는 최대 " + MAX_ITEM_COUNT + "개까지 설정할 수 있습니다.");
        }
    }

    private void validateUpdateItems(List<UpdateTourCourseItemRequest> items) {
        Map<Integer, Set<Integer>> ordersByDay = new HashMap<>();
        Set<Integer> days = new HashSet<>();
        Set<Long> itemIds = new HashSet<>();
        Set<String> placeKeys = new HashSet<>();
        for (int index = 0; index < items.size(); index++) {
            UpdateTourCourseItemRequest item = items.get(index);
            String itemPath = "items[" + index + "]";
            if (item == null) {
                throw updateValidationException(itemPath, "관광지 항목은 null일 수 없습니다.");
            }
            validatePlaceIdentifiers(
                    item.category(), item.spotId(), item.nearbyPlaceId(), itemPath);
            if (item.dayNumber() == null || item.dayNumber() < 1) {
                throw updateValidationException(
                        itemPath + ".dayNumber", "여행 일차는 1 이상의 값이어야 합니다.");
            }
            days.add(item.dayNumber());
            if (item.itemOrder() == null || item.itemOrder() < 1) {
                throw updateValidationException(
                        itemPath + ".itemOrder", "관광지 순서는 1 이상의 값이어야 합니다.");
            }
            Long placeId = item.category() == TourCourseItemCategory.TOUR_SPOT
                    ? item.spotId() : item.nearbyPlaceId();
            if (!placeKeys.add(item.category() + ":" + placeId)) {
                throw updateValidationException(itemPath, "동일한 장소를 중복해서 포함할 수 없습니다.");
            }
            Set<Integer> dayOrders = ordersByDay.computeIfAbsent(
                    item.dayNumber(), ignored -> new HashSet<>());
            if (!dayOrders.add(item.itemOrder())) {
                throw new CustomException(
                        ErrorCode.TOUR_COURSE_ITEM_ORDER_DUPLICATED,
                        Map.of("field", itemPath + ".itemOrder"));
            }
            if (item.itemId() != null && (item.itemId() < 1 || !itemIds.add(item.itemId()))) {
                throw new CustomException(
                        ErrorCode.TOUR_COURSE_ITEM_NOT_BELONG,
                        Map.of("field", itemPath + ".itemId", "itemId", item.itemId()));
            }
            if (item.distanceMeter() != null && item.distanceMeter() < 0) {
                throw new CustomException(
                        ErrorCode.TOUR_COURSE_NEGATIVE_DISTANCE,
                        Map.of("field", itemPath + ".distanceMeter"));
            }
            if (item.durationSecond() != null && item.durationSecond() < 0) {
                throw new CustomException(
                        ErrorCode.TOUR_COURSE_NEGATIVE_DURATION,
                        Map.of("field", itemPath + ".durationSecond"));
            }
            boolean hasAnyMovement = item.transportType() != null
                    || item.distanceMeter() != null || item.durationSecond() != null;
            boolean hasAllMovement = item.transportType() != null
                    && item.distanceMeter() != null && item.durationSecond() != null;
            if (item.itemOrder() == 1 && hasAnyMovement) {
                throw new CustomException(
                        ErrorCode.TOUR_COURSE_FIRST_ITEM_MOVEMENT,
                        Map.of("field", itemPath + ".transportType"));
            }
            if (item.itemOrder() > 1 && hasAnyMovement && !hasAllMovement) {
                throw updateValidationException(
                        itemPath,
                        "이동 기록은 transportType, distanceMeter, durationSecond를 모두 입력하거나 모두 null이어야 합니다.");
            }
        }
        for (int expectedDay = 1; expectedDay <= days.size(); expectedDay++) {
            if (!days.contains(expectedDay)) {
                throw updateValidationException("dayNumber", "여행 일차는 1부터 연속이어야 합니다.");
            }
        }
        for (Map.Entry<Integer, Set<Integer>> entry : ordersByDay.entrySet()) {
            for (int expected = 1; expected <= entry.getValue().size(); expected++) {
                if (!entry.getValue().contains(expected)) {
                throw new CustomException(
                        ErrorCode.TOUR_COURSE_ITEM_ORDER_NOT_CONSECUTIVE,
                            Map.of("field", "itemOrder", "dayNumber", entry.getKey()));
                }
            }
        }
    }

    private void validatePlaceIdentifiers(
            TourCourseItemCategory category, Long spotId, Long nearbyPlaceId, String itemPath
    ) {
        boolean valid = category != null && (
                category == TourCourseItemCategory.TOUR_SPOT
                        ? spotId != null && spotId > 0 && nearbyPlaceId == null
                        : nearbyPlaceId != null && nearbyPlaceId > 0 && spotId == null);
        if (!valid) {
            throw updateValidationException(
                    itemPath,
                    "TOUR_SPOT은 spotId만, RESTAURANT/ACCOMMODATION은 nearbyPlaceId만 입력해야 합니다.");
        }
    }

    private void validateUpdatePassword(boolean shareYn, String password) {
        if (shareYn && (password == null || password.isBlank())) {
            throw updateValidationException("sharedPassword", "공유 코스는 접근 비밀번호가 필요합니다.");
        }
        if (!shareYn && password != null) {
            throw updateValidationException(
                    "sharedPassword", "공유하지 않는 코스에는 접근 비밀번호를 설정할 수 없습니다.");
        }
        if (shareYn && !password.matches(PASSWORD_PATTERN)) {
            throw updateValidationException(
                    "sharedPassword", "공유 비밀번호는 최대 5자리 숫자여야 합니다.");
        }
    }

    private CustomException updateValidationException(String field, String message) {
        return new CustomException(
                ErrorCode.INVALID_TOUR_COURSE_UPDATE, message, Map.of("field", field));
    }

    private void validateUpdatePlaces(List<UpdateTourCourseItemRequest> items) {
        List<Long> spotIds = items.stream()
                .filter(item -> item.category() == TourCourseItemCategory.TOUR_SPOT)
                .map(UpdateTourCourseItemRequest::spotId)
                .toList();
        List<Long> nearbyPlaceIds = items.stream()
                .filter(item -> item.category() != TourCourseItemCategory.TOUR_SPOT)
                .map(UpdateTourCourseItemRequest::nearbyPlaceId)
                .toList();
        Set<Long> existingSpotIds = spotIds.isEmpty()
                ? Set.of() : tourSpotReferenceRepository.findExistingIds(spotIds);
        Set<Long> existingNearbyPlaceIds = nearbyPlaceIds.isEmpty()
                ? Set.of() : nearbyPlaceReferenceRepository.findExistingIds(nearbyPlaceIds);
        for (int index = 0; index < items.size(); index++) {
            UpdateTourCourseItemRequest item = items.get(index);
            if (item.category() == TourCourseItemCategory.TOUR_SPOT
                    && !existingSpotIds.contains(item.spotId())) {
                throw new CustomException(
                        ErrorCode.TOUR_SPOT_NOT_FOUND,
                        Map.of("field", "items[" + index + "].spotId", "spotId", item.spotId()));
            }
            if (item.category() != TourCourseItemCategory.TOUR_SPOT
                    && !existingNearbyPlaceIds.contains(item.nearbyPlaceId())) {
                throw new CustomException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        Map.of("field", "items[" + index + "].nearbyPlaceId",
                                "nearbyPlaceId", item.nearbyPlaceId()));
            }
        }
    }

    private void validateItemOwnership(
            List<UpdateTourCourseItemRequest> items,
            Map<Long, TourCourseItem> existingItems
    ) {
        for (int index = 0; index < items.size(); index++) {
            Long itemId = items.get(index).itemId();
            if (itemId != null && !existingItems.containsKey(itemId)) {
                throw new CustomException(
                        ErrorCode.TOUR_COURSE_ITEM_NOT_BELONG,
                        Map.of("field", "items[" + index + "].itemId", "itemId", itemId));
            }
        }
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
        Map<Integer, Set<Integer>> ordersByDay = new HashMap<>();
        Set<Integer> days = new HashSet<>();
        for (CreateTourCourseItemRequest item : items) {
            if (item == null) {
                throw validationException();
            }
            if (item.dayNumber() == null || item.dayNumber() < 1) {
                throw validationException();
            }
            days.add(item.dayNumber());
            if (item.itemOrder() == null || item.itemOrder() < 1) {
                throw validationException();
            }
            if (!ordersByDay.computeIfAbsent(item.dayNumber(), ignored -> new HashSet<>())
                    .add(item.itemOrder())) {
                throw validationException();
            }
        }
        for (int expectedDay = 1; expectedDay <= days.size(); expectedDay++) {
            if (!days.contains(expectedDay)) {
                throw validationException();
            }
        }
        for (Set<Integer> orders : ordersByDay.values()) {
            for (int expected = 1; expected <= orders.size(); expected++) {
                if (!orders.contains(expected)) {
                    throw validationException();
                }
            }
        }
    }

    private void validateTransport(List<CreateTourCourseItemRequest> items) {
        if (items.stream().anyMatch(item ->
                item.itemOrder() == 1 && item.transportType() != null)) {
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
