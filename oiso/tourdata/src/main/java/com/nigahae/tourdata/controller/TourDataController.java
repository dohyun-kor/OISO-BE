package com.nigahae.tourdata.controller;

import com.nigahae.tourdata.TourDetailRefresher;
import com.nigahae.tourdata.TourDetailRefreshResult;
import com.nigahae.tourdata.domain.TourDataJobType;
import com.nigahae.tourdata.service.TourDataResult;
import com.nigahae.tourdata.service.TourDataRunner;
import com.nigahae.tourdata.service.TourDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "관광 데이터 동기화", description = "관광 데이터 수동 동기화 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/tour-data")
public class TourDataController {

    private final TourDataRunner tourDataRunner;
    private final TourDetailRefresher tourDetailRefresher;
    private final TourDataService tourDataService;

    @Operation(summary = "관광지 데이터 동기화", description = "지역 기반 관광지 조회 결과를 관광지 데이터에 반영합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "관광지 데이터 동기화 성공"),
            @ApiResponse(responseCode = "207", description = "일부 데이터 동기화 성공"),
            @ApiResponse(responseCode = "409", description = "동일 작업이 실행 중이어서 요청 생략"),
            @ApiResponse(responseCode = "500", description = "관광지 데이터 동기화 실패")
    })
    @PostMapping("/tour-spots/refresh")
    public ResponseEntity<TourDataResult> refreshTourSpots() {
        return response(tourDataRunner.run(TourDataJobType.TOUR_SPOTS, "manual-api"));
    }

    @Operation(summary = "주변 식당·숙소 상세정보 수집", description = "한국관광공사 목록과 상세 API를 다시 호출해 주변 식당·숙소 및 상세정보를 저장합니다.")
    @PostMapping("/nearby/refresh")
    public ResponseEntity<TourDataResult> refreshNearbyPlaces() {
        return response(tourDataService.refreshNearbyPlaceDetails());
    }

    @Operation(summary = "축제 데이터 동기화", description = "축제 조회 결과를 축제 정보 데이터에 반영합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "축제 데이터 동기화 성공"),
            @ApiResponse(responseCode = "207", description = "일부 데이터 동기화 성공"),
            @ApiResponse(responseCode = "409", description = "동일 작업이 실행 중이어서 요청 생략"),
            @ApiResponse(responseCode = "500", description = "축제 데이터 동기화 실패")
    })
    @PostMapping("/festivals/refresh")
    public ResponseEntity<TourDataResult> refreshFestivals() {
        return response(tourDataRunner.run(TourDataJobType.FESTIVALS, "manual-api"));
    }

    @Operation(
            summary = "관광지 공통 상세정보 단건 동기화",
            description = "관광지 ID에 해당하는 공통 상세정보를 외부 관광 API에서 다시 조회해 반영합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "관광지 공통 상세정보 동기화 성공"),
            @ApiResponse(responseCode = "404", description = "관광지를 찾을 수 없음"),
            @ApiResponse(responseCode = "502", description = "외부 관광 API 호출 실패")
    })
    @PostMapping("/common-details/{spotId}/refresh")
    public ResponseEntity<TourDataResult> refreshCommonDetail(@PathVariable Long spotId) {
        TourDetailRefreshResult result = tourDetailRefresher.refresh(spotId);
        if (result.updatedSuccessfully()) {
            return ResponseEntity.ok(TourDataResult.success(1));
        }
        if (result.targetNotFound()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(TourDataResult.failure(1, "Tour spot not found. spotId=" + spotId));
        }
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(TourDataResult.failure(1, result.failureMessage()));
    }

    @Operation(
            summary = "숙박 상세정보 동기화",
            description = "숙박 상세정보 조회 결과로 객실 정보를 갱신합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "숙박 상세정보 동기화 성공"),
            @ApiResponse(responseCode = "207", description = "일부 데이터 동기화 성공"),
            @ApiResponse(responseCode = "409", description = "동일 작업이 실행 중이어서 요청 생략"),
            @ApiResponse(responseCode = "500", description = "숙박 상세정보 동기화 실패")
    })
    @PostMapping("/accommodations/refresh")
    public ResponseEntity<TourDataResult> refreshAccommodations() {
        return response(tourDataRunner.run(TourDataJobType.ACCOMMODATIONS, "manual-api"));
    }

    private ResponseEntity<TourDataResult> response(TourDataResult result) {
        if (result.skipped()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        }
        if (result.success()) {
            return ResponseEntity.ok(result);
        }
        if (result.partial()) {
            return ResponseEntity.status(HttpStatus.MULTI_STATUS).body(result);
        }
        return ResponseEntity.internalServerError().body(result);
    }
}
