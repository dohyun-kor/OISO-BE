package com.nigahae.tourspot.dto;

public interface NearbyPlaceRow {
    Long getNearbyPlaceId();
    Long getSpotId();
    String getCategory();
    String getName();
    String getImg();
    String getOverview();
    String getMood();
    String getAddress();
    String getHomepageUrl();
    Long getContentId();
    Integer getContentTypeId();
    Double getMapX();
    Double getMapY();
    String getOpenTime();
    String getRestDate();
    String getReservationInfo();
    String getInquiryInfo();
    String getParkingInfo();
    String getPetInfo();
    String getFirstMenu();
    String getTreatMenu();
}
