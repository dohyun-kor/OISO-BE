package com.nigahae.common.error;

import com.nigahae.common.constant.ErrorCode;

/**
 * API 오류 응답의 공통 형식입니다.
 *
 * @param code 애플리케이션 오류 코드
 * @param message 오류 메시지
 * @param data 오류 응답 데이터. 항상 {@code null}입니다.
 */
public record CommonError(int code, String message, Void data) {

    public static CommonError from(ErrorCode errorCode) {
        return from(errorCode, errorCode.getMessage());
    }

    public static CommonError from(ErrorCode errorCode, String message) {
        return new CommonError(errorCode.getCode(), message, null);
    }
}
