package com.nigahae.location.dto;
import java.util.List;
public record RegionListResponse(double longitude, double latitude, List<RegionResponse> regions) {}
