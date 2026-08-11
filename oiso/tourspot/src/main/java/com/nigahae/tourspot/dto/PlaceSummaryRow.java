package com.nigahae.tourspot.dto;

public interface PlaceSummaryRow {
    String getCategory();
    Long getSpotId();
    Long getNearbyPlaceId();
    String getName();
    String getImg();
    String getOverview();
    String getAddress();
    Double getLatitude();
    Double getLongitude();
}
