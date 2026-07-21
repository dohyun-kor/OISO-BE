package com.nigahae.common.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    VALIDATION_ERROR(
            ErrorCategory.VALIDATION,
            ErrorDomain.COMMON,
            HttpStatus.BAD_REQUEST,
            "요청 값이 올바르지 않습니다."
    ),
    BUSINESS_ERROR(
            ErrorCategory.BUSINESS,
            ErrorDomain.COMMON,
            HttpStatus.UNPROCESSABLE_ENTITY,
            "요청한 작업을 처리할 수 없습니다."
    ),
    RESOURCE_NOT_FOUND(
            ErrorCategory.RESOURCE,
            ErrorDomain.COMMON,
            HttpStatus.NOT_FOUND,
            "요청한 리소스를 찾을 수 없습니다."
    ),
    AUTH_ERROR(
            ErrorCategory.AUTH,
            ErrorDomain.COMMON,
            HttpStatus.UNAUTHORIZED,
            "인증 또는 권한 확인이 필요합니다."
    ),
    NO_REFRESH_TOKEN(
            ErrorCategory.AUTH,
            ErrorDomain.SECURITY,
            HttpStatus.UNAUTHORIZED,
            "Refresh Token이 없습니다."
    ),
    INVALID_REFRESH_TOKEN(
            ErrorCategory.AUTH,
            ErrorDomain.SECURITY,
            HttpStatus.UNAUTHORIZED,
            "만료되거나 유효하지 않은 Refresh Token입니다."
    ),
    INTERNAL_SERVER_ERROR(
            ErrorCategory.SERVER,
            ErrorDomain.COMMON,
            HttpStatus.INTERNAL_SERVER_ERROR,
            "서버 내부 오류가 발생했습니다."
    );

    private final int code;
    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(ErrorCategory category, ErrorDomain domain, HttpStatus httpStatus, String message) {
        this.code = category.codeFor(domain);
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
