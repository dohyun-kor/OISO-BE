package com.nigahae.common.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    //400
    NO_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "Refresh Token이 없습니다."),

    //401
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "만료되거나 유효하지 않은 Refresh Token입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
