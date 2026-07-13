package com.nigahae.common.error;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nigahae.common.constant.ErrorCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommonErrorTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void errorResponseHasCommonFormat() {
        CommonError response = CommonError.from(ErrorCode.RESOURCE_NOT_FOUND);

        JsonNode json = objectMapper.valueToTree(response);

        assertThat(json.get("code").asInt()).isEqualTo(3001);
        assertThat(json.get("message").asText()).isEqualTo("요청한 리소스를 찾을 수 없습니다.");
        assertThat(json.get("data").isNull()).isTrue();
        assertThat(json.size()).isEqualTo(3);
    }

    @Test
    void errorMessageCanBeOverriddenForValidationDetails() {
        CommonError response = CommonError.from(ErrorCode.VALIDATION_ERROR, "이름은 필수입니다.");

        assertThat(response.code()).isEqualTo(1001);
        assertThat(response.message()).isEqualTo("이름은 필수입니다.");
        assertThat(response.data()).isNull();
    }
}
