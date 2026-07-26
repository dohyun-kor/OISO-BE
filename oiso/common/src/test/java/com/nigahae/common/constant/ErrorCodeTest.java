package com.nigahae.common.constant;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorCodeTest {

    @Test
    void commonErrorCodesUseCategoryPrefixAndCommonDomainNumber() {
        assertThat(ErrorCode.VALIDATION_ERROR.getCode()).isEqualTo(1001);
        assertThat(ErrorCode.BUSINESS_ERROR.getCode()).isEqualTo(2001);
        assertThat(ErrorCode.RESOURCE_NOT_FOUND.getCode()).isEqualTo(3001);
        assertThat(ErrorCode.AUTH_ERROR.getCode()).isEqualTo(4001);
        assertThat(ErrorCode.INTERNAL_SERVER_ERROR.getCode()).isEqualTo(5001);
    }

    @Test
    void securityAuthErrorsUseSecurityDomainNumber() {
        assertThat(ErrorCode.NO_REFRESH_TOKEN.getCode()).isEqualTo(4002);
        assertThat(ErrorCode.INVALID_REFRESH_TOKEN.getCode()).isEqualTo(4002);
        assertThat(ErrorCode.NO_REFRESH_TOKEN.getHttpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(ErrorCode.INVALID_REFRESH_TOKEN.getHttpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void userNotFoundUsesUserDomainNumber() {
        assertThat(ErrorCode.USER_NOT_FOUND.getCode()).isEqualTo(3003);
        assertThat(ErrorCode.USER_NOT_FOUND.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void categoryBuildsCodeFromDomainNumber() {
        assertThat(ErrorCategory.VALIDATION.codeFor(ErrorDomain.COMMON)).isEqualTo(1001);
        assertThat(ErrorCategory.AUTH.codeFor(ErrorDomain.SECURITY)).isEqualTo(4002);
        assertThat(ErrorCategory.RESOURCE.codeFor(ErrorDomain.USER)).isEqualTo(3003);
        assertThat(ErrorCategory.SERVER.codeFor(ErrorDomain.COMMON)).isEqualTo(5001);
    }
}
