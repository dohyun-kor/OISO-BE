package com.nigahae.location.dto;
public record ReverseGeocodingResponse(double longitude, double latitude, String address, String roadAddress,
 String region1, String region2, String region3) {}
