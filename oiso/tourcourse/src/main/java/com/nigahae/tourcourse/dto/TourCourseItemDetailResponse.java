package com.nigahae.tourcourse.dto;

import com.nigahae.tourcourse.domain.TourCourseItemCategory;
import com.nigahae.tourcourse.domain.TransportType;
import io.swagger.v3.oas.annotations.media.Schema;

public record TourCourseItemDetailResponse(
        @Schema(description = "관광 코스에 포함된 항목 자체의 ID. 코스 수정 시 기존 항목을 식별하는 itemId로 사용합니다.", example = "11")
        Long itemId,
        Integer itemOrder,
        TourCourseItemCategory category,
        @Schema(nullable = true, description = "항목이 가리키는 실제 관광지 ID. 코스 수정 요청의 spotId로 사용합니다. 주변 장소 항목이면 null입니다.", example = "35")
        Long spotId,
        @Schema(nullable = true) Long nearbyPlaceId,
        @Schema(nullable = true) Long parentTourSpotId,
        String name,
        @Schema(nullable = true) String img,
        @Schema(nullable = true) String overview,
        String address,
        Double latitude,
        Double longitude,
        boolean like,
        @Schema(nullable = true) TransportType transportType,
        @Schema(nullable = true) Integer distanceMeter,
        @Schema(nullable = true) Integer durationSecond
) {
    public static TourCourseItemDetailResponse from(TourCourseItemDetailRow row) {
        boolean firstItem = row.getItemOrder() != null && row.getItemOrder() == 1;
        return new TourCourseItemDetailResponse(
                row.getItemId(),
                row.getItemOrder(),
                TourCourseItemCategory.valueOf(row.getCategory()),
                row.getSpotId(),
                row.getNearbyPlaceId(),
                row.getParentTourSpotId(),
                row.getName(),
                row.getImg(),
                row.getOverview(),
                row.getAddress(),
                row.getLatitude(),
                row.getLongitude(),
                Boolean.TRUE.equals(row.getLiked()),
                firstItem || row.getTransportType() == null
                        ? null : TransportType.valueOf(row.getTransportType()),
                firstItem ? null : row.getDistanceMeter(),
                firstItem ? null : row.getDurationSecond()
        );
    }
}
