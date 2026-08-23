package com.nigahae.location.dto;
import com.nigahae.location.domain.RouteTransportType;
import io.swagger.v3.oas.annotations.media.Schema;
public record RouteCalculationResponse(CoordinateResponse origin, CoordinateResponse destination,
 RouteTransportType transportType, @Schema(example="4025") int distanceMeters,
 @Schema(example="3914") int durationSeconds, String distanceText, String durationText, String navigationUrl) {}
