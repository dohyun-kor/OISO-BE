package com.nigahae.tourdata.controller;

import com.nigahae.tourdata.service.TourDataResult;
import com.nigahae.tourdata.service.TourDataRunner;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Tour Ingest", description = "Development APIs for refreshing tour data")
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/tour-ingest")
public class TourDataController {

    private final TourDataRunner tourIngestRunner;

    @Operation(summary = "Refresh tour data now", description = "Calls the public tour OpenAPI and upserts results into TourSpot.")
    @PostMapping("/refresh")
    public ResponseEntity<TourDataResult> refresh() {
        TourDataResult result = tourIngestRunner.run("manual-api");
        if (result.skipped()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        }
        if (!result.success()) {
            return ResponseEntity.internalServerError().body(result);
        }
        return ResponseEntity.ok(result);
    }
}
