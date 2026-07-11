package com.nigahae.security.oauth;

import com.nigahae.security.jwt.JwtTokenProvider;
import com.nigahae.security.util.CookieUtil;
import com.nigahae.user.AuthenticatedUser;
import com.nigahae.user.UserAuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class OAuth2LoginSuccessHandlerTest {
    @Test
    void registersUserAndRedirectsWithHttpOnlyCookies() throws Exception {
        UserAuthenticationService users = mock(UserAuthenticationService.class);
        JwtTokenProvider jwt = mock(JwtTokenProvider.class);
        CookieUtil cookies = new CookieUtil();
        ReflectionTestUtils.setField(cookies, "secure", false);
        ReflectionTestUtils.setField(cookies, "sameSite", "Lax");
        ReflectionTestUtils.setField(cookies, "accessTokenMaxAge", 900);
        ReflectionTestUtils.setField(cookies, "refreshTokenMaxAge", 604800);
        when(users.loginOrRegister("kakao", "123", "닉네임", null, null))
                .thenReturn(new AuthenticatedUser(9L, "닉네임"));
        when(jwt.createAccessToken(9L, "닉네임")).thenReturn("access.jwt");
        when(jwt.createRefreshToken(9L, "닉네임")).thenReturn("refresh.jwt");
        OAuth2LoginSuccessHandler handler = new OAuth2LoginSuccessHandler(
                new KakaoUserInfoParser(), users, jwt, cookies);
        ReflectionTestUtils.setField(handler, "redirectUri", "http://localhost:3000/");
        var principal = new DefaultOAuth2User(List.of(new SimpleGrantedAuthority("ROLE_USER")),
                Map.of("id", 123L, "properties", Map.of("nickname", "닉네임")), "id");
        var authentication = new OAuth2AuthenticationToken(principal, principal.getAuthorities(), "kakao");
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(new MockHttpServletRequest(), response, authentication);

        assertThat(response.getRedirectedUrl()).isEqualTo("http://localhost:3000/");
        assertThat(response.getHeaders("Set-Cookie")).hasSize(2)
                .allSatisfy(header -> assertThat(header).contains("HttpOnly", "SameSite=Lax"));
    }
}
