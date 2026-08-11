package com.nigahae.tourspot.dto;

import com.nigahae.tourspot.domain.NearbyPlaceCategory;
import io.swagger.v3.oas.annotations.media.Schema;

public record NearbyPlaceResponse(
        @Schema(description = "전체 주변 장소에서 유일한 ID", example = "101") Long nearbyPlaceId,
        @Schema(description = "주변 장소가 속한 관광지 ID", example = "35") Long spotId,
        NearbyPlaceCategory category,
        String name,
        String mood,
        String address,
        String homepageUrl,
        Long contentId,
        Integer contentTypeId,
        Double mapX,
        Double mapY,
        String openTime,
        String restDate,
        String reservationInfo,
        String inquiryInfo,
        String parkingInfo,
        String petInfo,
        String firstMenu,
        String treatMenu
) {
    public static NearbyPlaceResponse from(NearbyPlaceRow row) {
        return new NearbyPlaceResponse(
                row.getNearbyPlaceId(), row.getSpotId(), NearbyPlaceCategory.valueOf(row.getCategory()),
                row.getName(), row.getMood(), row.getAddress(), row.getHomepageUrl(),
                row.getContentId(), row.getContentTypeId(), row.getMapX(), row.getMapY(),
                row.getOpenTime(), row.getRestDate(), row.getReservationInfo(),
                row.getInquiryInfo(), row.getParkingInfo(), row.getPetInfo(),
                row.getFirstMenu(), row.getTreatMenu());
    }
}
