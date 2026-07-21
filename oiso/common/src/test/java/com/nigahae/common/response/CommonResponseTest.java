package com.nigahae.common.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CommonResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void successResponseHasCommonFormat() {
        CommonResponse<Map<String, Object>> response = CommonResponse.success(Map.of(
                "contents", Map.of("sample", 1, "sample2", 2),
                "others", Map.of("sample", 1, "sample2", 2)
        ));

        JsonNode json = objectMapper.valueToTree(response);

        assertThat(json.get("code").asInt()).isZero();
        assertThat(json.get("message").asText()).isEqualTo("정상 처리 되었습니다.");
        assertThat(json.get("data").get("contents").get("sample").asInt()).isEqualTo(1);
        assertThat(json.get("data").get("others").get("sample2").asInt()).isEqualTo(2);
    }

    @Test
    void successWithoutDataContainsNullData() {
        JsonNode json = objectMapper.valueToTree(CommonResponse.success());

        assertThat(json.get("data").isNull()).isTrue();
    }
}
