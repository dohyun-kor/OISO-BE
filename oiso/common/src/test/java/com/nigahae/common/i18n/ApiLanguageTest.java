package com.nigahae.common.i18n;

import com.nigahae.common.error.CommonError;
import com.nigahae.common.response.CommonResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

class ApiLanguageTest {
    @AfterEach void cleanUp() { RequestContextHolder.resetRequestAttributes(); }

    @ParameterizedTest
    @CsvSource(value={"en|en", "en-US,ko;q=0.8|en", "ko-KR,en;q=0.5|ko",
            "fr,en;q=0.8|en", "en;q=0,ko;q=0.5|ko", "fr|ko", "bad;q=invalid|ko", "*|ko"}, delimiter='|')
    void negotiatesSupportedLanguage(String header, String expected) {
        assertThat(ApiLanguage.resolve(header)).isEqualTo(expected);
    }

    @Test void koreanIsDefaultAndMissingEnglishFallsBack() {
        assertThat(ApiLanguage.isEnglish()).isFalse();
        assertThat(ApiLanguage.choose("불국사", "Bulguksa Temple")).isEqualTo("불국사");
        english();
        assertThat(ApiLanguage.choose("불국사", "Bulguksa Temple")).isEqualTo("Bulguksa Temple");
        assertThat(ApiLanguage.choose("불국사", "  ")).isEqualTo("불국사");
    }

    @Test void translatesMessagesButPreservesUserContentAndIds() {
        english();
        var data = Map.of("nickname", "경주 여행자", "memo", "정상 처리 되었습니다.", "itemId", 297);
        var response = CommonResponse.success(data);
        assertThat(response.message()).isEqualTo("The request was processed successfully.");
        assertThat(response.data()).isSameAs(data);
        var error = new CommonError(4000, "items[1].itemId: 동일한 기존 항목 ID를 중복해서 입력할 수 없습니다.",
                Map.of("field", "items[1].itemId"));
        assertThat(error.message()).isEqualTo("items[1].itemId: The same existing item ID cannot appear more than once.");
    }

    @Test void translatesDynamicValidationMessages() {
        english();
        assertThat(ApiMessages.translate("title: 관광 코스 제목은 20자 이하여야 합니다."))
                .isEqualTo("title: Maximum tour course title length: 20 characters.");
        assertThat(ApiMessages.translate("items[1].transportType: 허용된 값은 CAR, WALK, BIKE입니다."))
                .isEqualTo("items[1].transportType: Allowed values: CAR, WALK, BIKE.");
    }

    @Test void filterLocalizesSecurityResponsesAndClearsContextEvenOnFailure() throws Exception {
        var request = new MockHttpServletRequest();
        request.addHeader("Accept-Language", "en-US");
        var response = new MockHttpServletResponse();
        new ApiLanguageConfig().apiLanguageFilter().doFilter(request, response, (req, res) -> {
            assertThat(ApiLanguage.isEnglish()).isTrue();
            assertThat(new CommonError(4001,"인증 또는 권한 확인이 필요합니다.",null).message())
                    .isEqualTo("Authentication or authorization is required.");
        });
        assertThat(response.getHeader("Content-Language")).isEqualTo("en");
        assertThat(response.getHeaders("Vary")).contains("Accept-Language");
        assertThat(RequestContextHolder.getRequestAttributes()).isNull();
    }

    private void english() {
        var request = new MockHttpServletRequest();
        request.addHeader("Accept-Language", "en");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
