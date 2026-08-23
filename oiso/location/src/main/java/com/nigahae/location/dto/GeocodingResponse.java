package com.nigahae.location.dto;
import java.util.List;
public record GeocodingResponse(String query, int totalCount, List<AddressCandidateResponse> results) {}
