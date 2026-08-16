package com.nigahae.tourdata.controller;

import com.nigahae.tourdata.domain.TourDataJobType;
import com.nigahae.tourdata.service.TourDataResult;
import com.nigahae.tourdata.service.TourDataRunner;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "관광 데이터 동기화", description = "관광 데이터 수동 동기화 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/tour-data")
public class TourDataController {

    private final TourDataRunner tourDataRunner;

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
            summary = "공통 상세정보 동기화",
            description = "공통 상세정보 조회 결과를 관광지와 축제 정보에 반영합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "공통 상세정보 동기화 성공"),
            @ApiResponse(responseCode = "207", description = "일부 데이터 동기화 성공"),
            @ApiResponse(responseCode = "409", description = "동일 작업이 실행 중이어서 요청 생략"),
            @ApiResponse(responseCode = "500", description = "공통 상세정보 동기화 실패")
    })
    @PostMapping("/common-details/refresh")
    public ResponseEntity<TourDataResult> refreshCommonDetails() {
        return response(tourDataRunner.run(TourDataJobType.COMMON_DETAILS, "manual-api"));
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
