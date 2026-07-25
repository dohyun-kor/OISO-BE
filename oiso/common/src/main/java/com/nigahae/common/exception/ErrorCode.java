package com.nigahae.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INVALID_REQUEST(4000, "잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
    SEARCH_RESULT_NOT_FOUND(4004, "검색 결과가 존재하지 않습니다.", HttpStatus.NOT_FOUND);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}
