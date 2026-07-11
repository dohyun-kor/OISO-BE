package com.nigahae.security.oauth;

import com.nigahae.security.jwt.JwtTokenProvider;
import com.nigahae.security.util.CookieUtil;
import com.nigahae.user.AuthenticatedUser;
import com.nigahae.user.UserAuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Value("${app.oauth2.success-redirect-uri}")
    private String redirectUri;

    private final KakaoUserInfoParser kakaoUserInfoParser;
    private final UserAuthenticationService userAuthenticationService;
    private final JwtTokenProvider jwtTokenProvider;
    private final CookieUtil cookieUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        if (!"kakao".equals(oauthToken.getAuthorizedClientRegistrationId())) {
            throw new OAuth2AuthenticationException(new OAuth2Error("unsupported_provider"),
                    "Only Kakao login is currently supported");
        }
        OAuth2User principal = (OAuth2User) authentication.getPrincipal();
        KakaoUserInfo info = kakaoUserInfoParser.parse(principal.getAttributes());
        AuthenticatedUser user = userAuthenticationService.loginOrRegister(
                "kakao", info.providerUserId(), info.nickname(), info.email(), info.profileImageUrl());

        cookieUtil.addCookie(response, CookieUtil.ACCESS_TOKEN,
                jwtTokenProvider.createAccessToken(user.userId(), user.nickname()), cookieUtil.getAccessTokenMaxAge());
        cookieUtil.addCookie(response, CookieUtil.REFRESH_TOKEN,
                jwtTokenProvider.createRefreshToken(user.userId(), user.nickname()), cookieUtil.getRefreshTokenMaxAge());
        response.sendRedirect(redirectUri);
    }
}
