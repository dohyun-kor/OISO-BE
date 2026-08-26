package com.nigahae.tourcourse.dto;

public interface TourCourseItemDetailRow {
    Long getItemId();
    Integer getDayNumber();
    Integer getItemOrder();
    String getCategory();
    Long getSpotId();
    Long getNearbyPlaceId();
    Long getParentTourSpotId();
    String getName();
    String getImg();
    String getOverview();
    String getAddress();
    Double getLatitude();
    Double getLongitude();
    Boolean getLiked();
    String getTransportType();
    String getStatus();
}
