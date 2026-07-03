package com.nigahae.common.exception;

import com.nigahae.common.constant.ErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // 부모 RuntimeException에 메시지 전달
        this.errorCode = errorCode;
    }
}