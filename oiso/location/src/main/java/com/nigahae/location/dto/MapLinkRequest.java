package com.nigahae.location.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
public record MapLinkRequest(
 @NotNull(message="목적지(destination)는 필수입니다.") @Valid CoordinateRequest destination) {}
