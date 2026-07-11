package com.nigahae.security.oauth;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class KakaoUserInfoParser {
    public KakaoUserInfo parse(Map<String, Object> attributes) {
        Object id = attributes.get("id");
        if (id == null) {
            throw new OAuth2AuthenticationException(new OAuth2Error("invalid_user_info"),
                    "Kakao user response does not contain id");
        }
        Map<String, Object> account = map(attributes.get("kakao_account"));
        Map<String, Object> properties = map(attributes.get("properties"));
        Map<String, Object> profile = map(account.get("profile"));
        String nickname = firstString(properties.get("nickname"), profile.get("nickname"));
        if (nickname == null) nickname = "카카오 사용자";
        String image = firstString(properties.get("profile_image"), profile.get("profile_image_url"));
        return new KakaoUserInfo(String.valueOf(id), nickname, string(account.get("email")), image);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> map(Object value) {
        return value instanceof Map<?, ?> ? (Map<String, Object>) value : Map.of();
    }

    private String firstString(Object first, Object second) {
        String value = string(first);
        return value != null ? value : string(second);
    }

    private String string(Object value) {
        return value instanceof String string && !string.isBlank() ? string : null;
    }
}
