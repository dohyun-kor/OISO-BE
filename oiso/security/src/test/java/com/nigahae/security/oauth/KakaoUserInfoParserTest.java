package com.nigahae.security.oauth;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class KakaoUserInfoParserTest {
    private final KakaoUserInfoParser parser = new KakaoUserInfoParser();

    @Test
    void parsesKakaoUserWithOptionalEmail() {
        KakaoUserInfo info = parser.parse(Map.of(
                "id", 12345L,
                "properties", Map.of("nickname", "오이소", "profile_image", "https://image"),
                "kakao_account", Map.of("email", "user@example.com")));
        assertThat(info).isEqualTo(new KakaoUserInfo("12345", "오이소", "user@example.com", "https://image"));
    }

    @Test
    void acceptsMissingEmailAndImage() {
        KakaoUserInfo info = parser.parse(Map.of("id", "abc", "properties", Map.of("nickname", "사용자")));
        assertThat(info.email()).isNull();
        assertThat(info.profileImageUrl()).isNull();
    }

    @Test
    void rejectsResponseWithoutId() {
        assertThatThrownBy(() -> parser.parse(Map.of())).isInstanceOf(OAuth2AuthenticationException.class);
    }
}
