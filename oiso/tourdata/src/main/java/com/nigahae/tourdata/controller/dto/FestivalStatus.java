package com.nigahae.tourdata.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "축제 진행 상태")
public enum FestivalStatus {
    ONGOING,
    UPCOMING
}
