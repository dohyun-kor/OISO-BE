package com.nigahae.security.controller;

import com.nigahae.security.jwt.JwtTokenProvider;
import com.nigahae.security.util.CookieUtil;
import com.nigahae.user.AuthenticatedUser;
import com.nigahae.user.UserAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DevAuthControllerTest {
    private final UserAuthenticationService users = mock(UserAuthenticationService.class);
    private final JwtTokenProvider jwt = mock(JwtTokenProvider.class);
    private final CookieUtil cookies = new CookieUtil();
    private DevAuthController controller;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(cookies, "secure", false);
        ReflectionTestUtils.setField(cookies, "sameSite", "Lax");
        ReflectionTestUtils.setField(cookies, "accessTokenMaxAge", 900);
        ReflectionTestUtils.setField(cookies, "refreshTokenMaxAge", 604800);
        controller = new DevAuthController(users, jwt, cookies);
    }

    @Test
    void logsInDevelopmentUserAndIssuesJwtCookies() {
        when(users.loginOrRegister("dev", "swagger-user", "Swagger 개발자", null, null))
                .thenReturn(new AuthenticatedUser(7L, "Swagger 개발자"));
        when(jwt.createAccessToken(7L, "Swagger 개발자")).thenReturn("access.jwt");
        when(jwt.createRefreshToken(7L, "Swagger 개발자")).thenReturn("refresh.jwt");
        MockHttpServletResponse response = new MockHttpServletResponse();

        var result = controller.login("swagger-user", "Swagger 개발자", response);

        assertThat(result.getBody())
                .isEqualTo(new DevAuthController.DevLoginResponse(7L, "Swagger 개발자"));
        assertThat(response.getHeaders("Set-Cookie")).hasSize(2)
                .anySatisfy(header -> assertThat(header)
                        .contains("accessToken=access.jwt", "HttpOnly", "Path=/", "SameSite=Lax"))
                .anySatisfy(header -> assertThat(header)
                        .contains("refreshToken=refresh.jwt", "HttpOnly", "Path=/", "SameSite=Lax"));
        verify(users).loginOrRegister("dev", "swagger-user", "Swagger 개발자", null, null);
    }
}
