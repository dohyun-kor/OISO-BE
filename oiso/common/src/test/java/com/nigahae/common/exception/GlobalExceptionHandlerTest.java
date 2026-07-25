package com.nigahae.common.exception;

import com.nigahae.common.constant.ErrorCode;
import com.nigahae.common.error.CommonError;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void validationFailureReturnsFirstValidationMessage() {
        BindException exception = new BindException(new Object(), "request");
        exception.addError(new FieldError("request", "name", "이름은 필수입니다."));

        ResponseEntity<CommonError> response = handler.handleBindException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody())
                .extracting(CommonError::code, CommonError::message, CommonError::data)
                .containsExactly(1001, "이름은 필수입니다.", null);
    }

    @Test
    void customExceptionUsesDeclaredCodeAndHttpStatus() {
        CustomException exception = new CustomException(ErrorCode.NO_REFRESH_TOKEN);

        ResponseEntity<CommonError> response = handler.handleCustomException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody())
                .extracting(CommonError::code, CommonError::message, CommonError::data)
                .containsExactly(4002, "Refresh Token이 없습니다.", null);
    }

    @Test
    void unexpectedExceptionReturnsSafeServerError() {
        ResponseEntity<CommonError> response = handler.handleUnexpectedException(
                new IllegalStateException("노출하면 안 되는 내부 메시지")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody())
                .extracting(CommonError::code, CommonError::message, CommonError::data)
                .containsExactly(5001, "서버 내부 오류가 발생했습니다.", null);
    }
}
