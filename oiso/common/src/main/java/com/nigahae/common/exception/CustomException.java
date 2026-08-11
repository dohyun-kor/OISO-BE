package com.nigahae.common.exception;

import com.nigahae.common.constant.ErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Object data;

    public CustomException(ErrorCode errorCode) {
        this(errorCode, errorCode.getMessage(), null);
    }

    public CustomException(ErrorCode errorCode, Object data) {
        this(errorCode, errorCode.getMessage(), data);
    }

    public CustomException(ErrorCode errorCode, String message, Object data) {
        super(message);
        this.errorCode = errorCode;
        this.data = data;
    }
}
