package com.nigahae.tourspot.dto;

import com.nigahae.tourspot.domain.NearbyPlaceCategory;
import io.swagger.v3.oas.annotations.media.Schema;

public record NearbyPlaceResponse(
        @Schema(description = "전체 주변 장소에서 유일한 ID", example = "101") Long nearbyPlaceId,
        @Schema(description = "주변 장소가 속한 관광지 ID", example = "35") Long spotId,
        NearbyPlaceCategory category,
        String name,
        String img,
        String overview,
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
        String treatMenu,
        @Schema(description = "현재 로그인 사용자의 저장 여부. 비로그인 시 false", example = "true")
        boolean like
) {
    /** Backward-compatible constructor for callers that do not provide like. */
    public NearbyPlaceResponse(
            Long nearbyPlaceId, Long spotId, NearbyPlaceCategory category, String name, String img,
            String overview, String mood, String address, String homepageUrl, Long contentId,
            Integer contentTypeId, Double mapX, Double mapY, String openTime, String restDate,
            String reservationInfo, String inquiryInfo, String parkingInfo, String petInfo,
            String firstMenu, String treatMenu) {
        this(nearbyPlaceId, spotId, category, name, img, overview, mood, address, homepageUrl,
                contentId, contentTypeId, mapX, mapY, openTime, restDate, reservationInfo,
                inquiryInfo, parkingInfo, petInfo, firstMenu, treatMenu, false);
    }

    public static NearbyPlaceResponse from(NearbyPlaceRow row) {
        return from(row, false);
    }

    public static NearbyPlaceResponse from(NearbyPlaceRow row, boolean like) {
        return new NearbyPlaceResponse(
                row.getNearbyPlaceId(), row.getSpotId(), NearbyPlaceCategory.valueOf(row.getCategory()),
                row.getName(), row.getImg(), row.getOverview(), row.getMood(), row.getAddress(), row.getHomepageUrl(),
                row.getContentId(), row.getContentTypeId(), row.getMapX(), row.getMapY(),
                row.getOpenTime(), row.getRestDate(), row.getReservationInfo(),
                row.getInquiryInfo(), row.getParkingInfo(), row.getPetInfo(),
                row.getFirstMenu(), row.getTreatMenu(), like);
    }
}
