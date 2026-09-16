package com.nigahae.common.exception;

import com.nigahae.common.constant.ErrorCode;
import com.nigahae.common.error.CommonError;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    private org.springframework.test.web.servlet.MockMvc mvc() {
        return org.springframework.test.web.servlet.setup.MockMvcBuilders
                .standaloneSetup(new RequestController()).setControllerAdvice(handler).build();
    }

    @Test
    void invalidEnumReturnsNestedFieldAndAllowedValues() throws Exception {
        mvc().perform(put("/test").contentType("application/json")
                        .content("""
                                {"items":[{"transportType":"FLY"}]}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(1001))
                .andExpect(jsonPath("$.data.field").value("items[0].transportType"))
                .andExpect(jsonPath("$.data.allowedValues[0]").value("CAR"))
                .andExpect(jsonPath("$.message").value("items[0].transportType: 허용된 값은 CAR, WALK, BIKE입니다."));
    }

    @Test
    void invalidNumberExplainsExpectedTypeWithoutEchoingInput() throws Exception {
        mvc().perform(put("/test").contentType("application/json")
                        .content("""
                                {"items":[{"itemId":"secret-value"}]}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.field").value("items[0].itemId"))
                .andExpect(jsonPath("$.message").value("items[0].itemId: 정수 형식으로 입력해야 합니다."))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("secret-value"))));
    }

    @Test
    void malformedJsonExplainsBodySyntaxFailure() throws Exception {
        mvc().perform(put("/test").contentType("application/json").content("{broken"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.field").value("request"))
                .andExpect(jsonPath("$.message").value("request: 요청 본문이 없거나 JSON 문법이 올바르지 않습니다."));
    }

    @Test
    void missingBodyAndIncorrectArrayHaveActionableErrors() throws Exception {
        mvc().perform(put("/test").contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.field").value("request"));
        mvc().perform(put("/test").contentType("application/json").content("{\"items\":{}}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("items: 배열 형식으로 입력해야 합니다."));
    }

    @org.springframework.web.bind.annotation.RestController
    static class RequestController {
        @org.springframework.web.bind.annotation.PutMapping("/test")
        public void update(@org.springframework.web.bind.annotation.RequestBody Payload payload) { }
    }

    record Payload(java.util.List<Item> items) { }
    record Item(Long itemId, Movement transportType) { }
    enum Movement { CAR, WALK, BIKE }

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
