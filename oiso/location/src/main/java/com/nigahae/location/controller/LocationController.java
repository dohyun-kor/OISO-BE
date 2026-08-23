package com.nigahae.location.controller;

import com.nigahae.common.response.CommonResponse;
import com.nigahae.location.controller.docs.LocationControllerDocs;
import com.nigahae.location.dto.*;
import com.nigahae.location.service.LocationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/v1")
public class LocationController implements LocationControllerDocs {
    private final LocationService locationService;
    public LocationController(LocationService locationService) { this.locationService = locationService; }

    @PostMapping("/routes/calculate")
    public CommonResponse<RouteCalculationResponse> calculateRoute(@Valid @RequestBody RouteCalculationRequest request) {
        return new CommonResponse<>(CommonResponse.SUCCESS_CODE, "장소 간 이동정보를 계산했습니다.",
                locationService.calculateRoute(request));
    }
    @GetMapping("/locations/geocode")
    public CommonResponse<GeocodingResponse> geocode(
            @RequestParam @NotBlank(message="검색할 주소를 입력해 주세요.") String address,
            @RequestParam(defaultValue="1") @Min(value=1, message="page는 1 이상이어야 합니다.")
            @Max(value=45, message="page는 45 이하여야 합니다.") int page,
            @RequestParam(defaultValue="10") @Min(value=1, message="size는 1 이상이어야 합니다.")
            @Max(value=30, message="size는 30 이하여야 합니다.") int size) {
        return new CommonResponse<>(CommonResponse.SUCCESS_CODE, "주소를 좌표로 변환했습니다.",
                locationService.geocode(address, page, size));
    }
    @GetMapping("/locations/reverse-geocode")
    public CommonResponse<ReverseGeocodingResponse> reverseGeocode(
            @RequestParam @DecimalMin(value="-180", message="경도(longitude, X)는 -180 이상이어야 합니다.")
            @DecimalMax(value="180", message="경도(longitude, X)는 180 이하여야 합니다.") double longitude,
            @RequestParam @DecimalMin(value="-90", message="위도(latitude, Y)는 -90 이상이어야 합니다. 경도와 순서를 확인해 주세요.")
            @DecimalMax(value="90", message="위도(latitude, Y)는 90 이하여야 합니다. 경도와 순서를 확인해 주세요.") double latitude) {
        return new CommonResponse<>(CommonResponse.SUCCESS_CODE, "좌표를 주소로 변환했습니다.",
                locationService.reverseGeocode(longitude, latitude));
    }
    @GetMapping("/locations/regions")
    public CommonResponse<RegionListResponse> regions(
            @RequestParam @DecimalMin(value="-180", message="경도(longitude, X)는 -180 이상이어야 합니다.")
            @DecimalMax(value="180", message="경도(longitude, X)는 180 이하여야 합니다.") double longitude,
            @RequestParam @DecimalMin(value="-90", message="위도(latitude, Y)는 -90 이상이어야 합니다. 경도와 순서를 확인해 주세요.")
            @DecimalMax(value="90", message="위도(latitude, Y)는 90 이하여야 합니다. 경도와 순서를 확인해 주세요.") double latitude) {
        return new CommonResponse<>(CommonResponse.SUCCESS_CODE, "좌표의 행정구역을 조회했습니다.",
                locationService.findRegions(longitude, latitude));
    }
    @PostMapping("/map-links")
    public CommonResponse<MapLinkResponse> mapLinks(@Valid @RequestBody MapLinkRequest request) {
        return new CommonResponse<>(CommonResponse.SUCCESS_CODE, "지도 링크를 생성했습니다.",
                locationService.createMapLinks(request));
    }
}
