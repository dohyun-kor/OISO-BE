package com.nigahae.common.constant;

public enum ErrorDomain {
    COMMON(1),
    SECURITY(2),
    USER(3);

    private final int detailCode;

    ErrorDomain(int detailCode) {
        this.detailCode = detailCode;
    }

    public int getDetailCode() {
        return detailCode;
    }
}
