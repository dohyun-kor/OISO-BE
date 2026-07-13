package com.nigahae.common.constant;

public enum ErrorCategory {
    VALIDATION(1000),
    BUSINESS(2000),
    RESOURCE(3000),
    AUTH(4000),
    SERVER(5000);

    private final int baseCode;

    ErrorCategory(int baseCode) {
        this.baseCode = baseCode;
    }

    public int codeFor(ErrorDomain domain) {
        return baseCode + domain.getDetailCode();
    }
}
