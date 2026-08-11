package com.nigahae.security.jwt;

import com.nigahae.security.util.CookieUtil;
import com.nigahae.user.AuthenticatedUser;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterTest {
    private final JwtTokenProvider jwt = mock(JwtTokenProvider.class);
    private final CookieUtil cookies = new CookieUtil();
    private final FilterChain chain = mock(FilterChain.class);

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void authenticatesValidAccessTokenCookie() throws Exception {
        ReflectionTestUtils.setField(cookies, "secure", false);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/auth/me");
        request.setCookies(new jakarta.servlet.http.Cookie(CookieUtil.ACCESS_TOKEN, "access.jwt"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(jwt.validateToken("access.jwt", JwtTokenProvider.ACCESS)).thenReturn(true);
        when(jwt.getUserId("access.jwt")).thenReturn(3L);
        when(jwt.getNickname("access.jwt")).thenReturn("사용자");

        new JwtAuthenticationFilter(jwt, cookies).doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .isEqualTo(new AuthenticatedUser(3L, "사용자"));
        verify(chain).doFilter(request, response);
    }

    @Test
    void authenticatesValidBearerAccessToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/tour-courses");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer access.jwt");
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(jwt.validateToken("access.jwt", JwtTokenProvider.ACCESS)).thenReturn(true);
        when(jwt.getUserId("access.jwt")).thenReturn(3L);
        when(jwt.getNickname("access.jwt")).thenReturn("사용자");

        new JwtAuthenticationFilter(jwt, cookies).doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .isEqualTo(new AuthenticatedUser(3L, "사용자"));
        verify(chain).doFilter(request, response);
    }

    @Test
    void replacesOAuthSessionAuthenticationWithJwtAuthentication() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/auth/me");
        request.setCookies(new jakarta.servlet.http.Cookie(CookieUtil.ACCESS_TOKEN, "access.jwt"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "oauth-session-user",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))));
        when(jwt.validateToken("access.jwt", JwtTokenProvider.ACCESS)).thenReturn(true);
        when(jwt.getUserId("access.jwt")).thenReturn(3L);
        when(jwt.getNickname("access.jwt")).thenReturn("사용자");

        new JwtAuthenticationFilter(jwt, cookies).doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .isEqualTo(new AuthenticatedUser(3L, "사용자"));
    }

    @Test
    void clearsOAuthSessionAuthenticationWhenApiRequestHasNoAccessToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/auth/me");
        MockHttpServletResponse response = new MockHttpServletResponse();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "oauth-session-user",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))));

        new JwtAuthenticationFilter(jwt, cookies).doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
