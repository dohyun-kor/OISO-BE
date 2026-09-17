package com.nigahae.common.response;

/**
 * API 성공 응답의 공통 형식입니다.
 *
 * @param code 응답 코드
 * @param message 응답 메시지
 * @param data 응답 데이터
 * @param <T> 응답 데이터 타입
 */
public record CommonResponse<T>(int code, String message, T data) {

    public CommonResponse {
        message = com.nigahae.common.i18n.ApiMessages.translate(message);
    }

    public static final int SUCCESS_CODE = 0;
    public static final String SUCCESS_MESSAGE = "정상 처리 되었습니다.";

    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(SUCCESS_CODE, SUCCESS_MESSAGE, data);
    }

    public static CommonResponse<Void> success() {
        return success(null);
    }
}
