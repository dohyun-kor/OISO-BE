package com.nigahae.location.dto;
import com.nigahae.location.domain.RouteTransportType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
public record RouteCalculationRequest(
 @NotNull(message="출발 장소(origin)는 필수입니다.") @Valid CoordinateRequest origin,
 @NotNull(message="도착 장소(destination)는 필수입니다.") @Valid CoordinateRequest destination,
 @NotNull(message="이동수단(transportType)은 필수입니다.") RouteTransportType transportType) {}
