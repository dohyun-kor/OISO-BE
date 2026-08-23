package com.nigahae.location.controller;

import com.nigahae.common.response.CommonResponse;
import com.nigahae.location.controller.docs.LocationControllerDocs;
import com.nigahae.location.dto.*;
import com.nigahae.location.service.LocationService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/v1")
public class LocationController implements LocationControllerDocs {
    private final LocationService locationService;
    public LocationController(LocationService locationService) { this.locationService = locationService; }

    @PostMapping("/routes/calculate")
    public CommonResponse<RouteCalculationResponse> calculateRoute(@RequestBody RouteCalculationRequest request) {
        return new CommonResponse<>(CommonResponse.SUCCESS_CODE, "장소 간 이동정보를 계산했습니다.",
                locationService.calculateRoute(request));
    }
    @GetMapping("/locations/geocode")
    public CommonResponse<GeocodingResponse> geocode(
            @RequestParam String address,
            @RequestParam(defaultValue="1") int page,
            @RequestParam(defaultValue="10") int size) {
        return new CommonResponse<>(CommonResponse.SUCCESS_CODE, "주소를 좌표로 변환했습니다.",
                locationService.geocode(address, page, size));
    }
    @GetMapping("/locations/reverse-geocode")
    public CommonResponse<ReverseGeocodingResponse> reverseGeocode(
            @RequestParam double longitude,
            @RequestParam double latitude) {
        return new CommonResponse<>(CommonResponse.SUCCESS_CODE, "좌표를 주소로 변환했습니다.",
                locationService.reverseGeocode(longitude, latitude));
    }
    @GetMapping("/locations/regions")
    public CommonResponse<RegionListResponse> regions(
            @RequestParam double longitude,
            @RequestParam double latitude) {
        return new CommonResponse<>(CommonResponse.SUCCESS_CODE, "좌표의 행정구역을 조회했습니다.",
                locationService.findRegions(longitude, latitude));
    }
    @PostMapping("/map-links")
    public CommonResponse<MapLinkResponse> mapLinks(@RequestBody MapLinkRequest request) {
        return new CommonResponse<>(CommonResponse.SUCCESS_CODE, "지도 링크를 생성했습니다.",
                locationService.createMapLinks(request));
    }
}
