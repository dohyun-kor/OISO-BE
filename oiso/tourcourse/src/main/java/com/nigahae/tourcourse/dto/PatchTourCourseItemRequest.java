package com.nigahae.tourcourse.dto;

import com.nigahae.tourcourse.domain.TourCourseItemCategory;
import com.nigahae.tourcourse.domain.TourCourseItemStatus;
import com.nigahae.tourcourse.domain.TransportType;

public record PatchTourCourseItemRequest(
        TourCourseItemCategory category,
        Long spotId,
        Long nearbyPlaceId,
        Integer dayNumber,
        Integer itemOrder,
        TransportType transportType,
        Integer distanceMeter,
        Integer durationSecond,
        TourCourseItemStatus status
) {
}
