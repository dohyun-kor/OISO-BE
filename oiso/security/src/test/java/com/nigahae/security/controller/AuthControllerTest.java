package com.nigahae.security.controller;

import com.nigahae.common.exception.CustomException;
import com.nigahae.security.jwt.JwtTokenProvider;
import com.nigahae.security.util.CookieUtil;
import com.nigahae.user.AuthenticatedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {
    private final JwtTokenProvider jwt = mock(JwtTokenProvider.class);
    private final CookieUtil cookies = new CookieUtil();
    private AuthController controller;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(cookies, "secure", false);
        ReflectionTestUtils.setField(cookies, "sameSite", "Lax");
        ReflectionTestUtils.setField(cookies, "accessTokenMaxAge", 900);
        ReflectionTestUtils.setField(cookies, "refreshTokenMaxAge", 604800);
        controller = new AuthController(jwt, cookies);
    }

    @Test
    void rejectsAccessTokenInRefreshCookie() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new jakarta.servlet.http.Cookie(CookieUtil.REFRESH_TOKEN, "access.jwt"));
        when(jwt.validateToken("access.jwt", JwtTokenProvider.REFRESH)).thenReturn(false);
        assertThatThrownBy(() -> controller.refreshToken(request, new MockHttpServletResponse()))
                .isInstanceOf(CustomException.class);
    }

    @Test
    void refreshesAccessCookieWithValidRefreshToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new jakarta.servlet.http.Cookie(CookieUtil.REFRESH_TOKEN, "refresh.jwt"));
        when(jwt.validateToken("refresh.jwt", JwtTokenProvider.REFRESH)).thenReturn(true);
        when(jwt.getUserId("refresh.jwt")).thenReturn(3L);
        when(jwt.getNickname("refresh.jwt")).thenReturn("사용자");
        when(jwt.createAccessToken(3L, "사용자")).thenReturn("new.jwt");
        MockHttpServletResponse response = new MockHttpServletResponse();
        controller.refreshToken(request, response);
        assertThat(response.getHeader("Set-Cookie")).contains("accessToken=new.jwt", "HttpOnly");
    }

    @Test
    void returnsCurrentAuthenticatedUser() {
        var response = controller.me(new AuthenticatedUser(3L, "사용자"));

        assertThat(response.getBody()).isEqualTo(new AuthController.MeResponse(3L, "사용자"));
    }

    @Test
    void rejectsUnexpectedAuthenticationPrincipal() {
        assertThatThrownBy(() -> controller.me(null))
                .isInstanceOf(CustomException.class);
    }

    @Test
    void logoutExpiresBothCookies() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        controller.logout(response);
        assertThat(response.getHeaders("Set-Cookie")).hasSize(2)
                .allSatisfy(header -> assertThat(header).contains("Max-Age=0", "HttpOnly"));
    }
}
