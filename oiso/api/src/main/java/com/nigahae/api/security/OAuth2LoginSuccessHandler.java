package com.nigahae.api.security;

import com.nigahae.api.config.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;

        String provider = authToken.getAuthorizedClientRegistrationId(); // google 또는 kakao
        String email = "";
        String nickname = "";
        String profileImageUrl = ""; // 프로필 이미지 URL을 담을 변수 추가

        if ("kakao".equals(provider)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) oAuth2User.getAttribute("kakao_account");
            Map<String, Object> properties = (Map<String, Object>) oAuth2User.getAttribute("properties");

            email = (String) kakaoAccount.get("email");
            nickname = (String) properties.get("nickname");

            // 💡 카카오 프로필 이미지 추출 (properties 구조 내부 혹은 kakao_account.profile 내부에 존재)
            if (properties != null && properties.containsKey("profile_image")) {
                profileImageUrl = (String) properties.get("profile_image");
            } else if (kakaoAccount != null && kakaoAccount.containsKey("profile")) {
                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                profileImageUrl = (String) profile.get("profile_image_url");
            }

        } else if ("google".equals(provider)) {
            email = oAuth2User.getAttribute("email");
            nickname = oAuth2User.getAttribute("name");

            // 💡 구글 프로필 이미지 추출 (구글은 'picture'라는 키값으로 제공합니다)
            profileImageUrl = oAuth2User.getAttribute("picture");
        }

        log.info("{} {} {} {}", provider, email, nickname, profileImageUrl);

        // 💡 추출한 회원 정보를 바탕으로 회원가입 또는 정보 업데이트 처리를 진행합니다.
        // userModuleService.loginOrRegister(email, nickname, profileImageUrl);

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(email, nickname);
        String refreshToken = jwtTokenProvider.createRefreshToken(email);

        // 💡 CookieUtil을 사용한 초간단 쿠키 생성
        CookieUtil.addCookie(response, "accessToken", accessToken, 1800, false);
        CookieUtil.addCookie(response, "refreshToken", refreshToken, 1209600, true);

        response.sendRedirect("http://localhost:3000/");
    }
}
