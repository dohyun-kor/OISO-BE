package com.nigahae.common.exception;

public class CustomCommonException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomCommonException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

}
