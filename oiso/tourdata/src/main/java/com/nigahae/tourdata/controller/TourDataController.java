package com.nigahae.tourdata.controller;

import com.nigahae.tourdata.TourDetailRefresher;
import com.nigahae.tourdata.TourDetailRefreshResult;
import com.nigahae.tourdata.domain.TourDataJobType;
import com.nigahae.tourdata.service.TourDataResult;
import com.nigahae.tourdata.service.TourDataRunner;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Tour Ingest", description = "Development APIs for refreshing tour data by job")
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/tour-data")
public class TourDataController {

    private final TourDataRunner tourDataRunner;
    private final TourDetailRefresher tourDetailRefresher;

    @Operation(summary = "Refresh tour spots", description = "Upserts areaBasedList2 results into TourSpot.")
    @PostMapping("/tour-spots/refresh")
    public ResponseEntity<TourDataResult> refreshTourSpots() {
        return response(tourDataRunner.run(TourDataJobType.TOUR_SPOTS, "manual-api"));
    }

    @Operation(summary = "Refresh festivals", description = "Upserts searchFestival2 results into FestivalInfo.")
    @PostMapping("/festivals/refresh")
    public ResponseEntity<TourDataResult> refreshFestivals() {
        return response(tourDataRunner.run(TourDataJobType.FESTIVALS, "manual-api"));
    }

    @Operation(
            summary = "Refresh one common detail",
            description = "Updates one TourSpot with the detailCommon2 result."
    )
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
            summary = "Refresh accommodations",
            description = "Replaces accommodation room details with detailInfo2 results."
    )
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
