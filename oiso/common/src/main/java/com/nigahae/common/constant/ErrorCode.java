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
    USER_NOT_FOUND(
            ErrorCategory.RESOURCE,
            ErrorDomain.USER,
            HttpStatus.NOT_FOUND,
            "존재하지 않는 사용자입니다."
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
    INVALID_TOUR_COURSE_ID(
            4000,
            HttpStatus.BAD_REQUEST,
            "잘못된 요청입니다."
    ),
    INVALID_TOUR_COURSE_UPDATE(
            4000,
            HttpStatus.BAD_REQUEST,
            "관광 코스 수정 요청이 올바르지 않습니다."
    ),
    TOUR_COURSE_FORBIDDEN(
            4003,
            HttpStatus.FORBIDDEN,
            "관광 코스를 조회할 권한이 없습니다."
    ),
    TOUR_COURSE_UPDATE_FORBIDDEN(
            4003,
            HttpStatus.FORBIDDEN,
            "관광 코스를 수정할 권한이 없습니다."
    ),
    TOUR_COURSE_DELETE_FORBIDDEN(
            4003,
            HttpStatus.FORBIDDEN,
            "관광 코스를 삭제할 권한이 없습니다."
    ),
    TOUR_COURSE_NOT_FOUND(
            4004,
            HttpStatus.NOT_FOUND,
            "관광 코스를 찾을 수 없습니다."
    ),
    TOUR_SPOT_NOT_FOUND(
            4004,
            HttpStatus.NOT_FOUND,
            "관광지를 찾을 수 없습니다."
    ),
    TOUR_COURSE_ITEMS_REQUIRED(
            4000,
            HttpStatus.BAD_REQUEST,
            "관광 코스에는 최소 1개 이상의 관광지가 필요합니다."
    ),
    TOUR_COURSE_ITEM_NOT_BELONG(
            4000,
            HttpStatus.BAD_REQUEST,
            "해당 관광 코스에 포함되지 않은 아이템입니다."
    ),
    TOUR_COURSE_ITEM_ORDER_DUPLICATED(
            4000,
            HttpStatus.BAD_REQUEST,
            "관광지 순서가 중복되었습니다."
    ),
    TOUR_COURSE_ITEM_ORDER_NOT_CONSECUTIVE(
            4000,
            HttpStatus.BAD_REQUEST,
            "관광지 순서는 1부터 연속적으로 설정해야 합니다."
    ),
    TOUR_COURSE_FIRST_ITEM_MOVEMENT(
            4000,
            HttpStatus.BAD_REQUEST,
            "첫 번째 관광지는 이동 정보를 가질 수 없습니다."
    ),
    TOUR_COURSE_NEGATIVE_DISTANCE(
            4000,
            HttpStatus.BAD_REQUEST,
            "이동거리는 0 이상이어야 합니다."
    ),
    TOUR_COURSE_NEGATIVE_DURATION(
            4000,
            HttpStatus.BAD_REQUEST,
            "이동시간은 0 이상이어야 합니다."
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

    ErrorCode(int code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
