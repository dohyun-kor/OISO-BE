package com.nigahae.security.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

@Component
public class CookieUtil {
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";

    @Value("${app.cookie.secure}") private boolean secure;
    @Value("${app.cookie.same-site:Lax}") private String sameSite;
    @Value("${app.cookie.access-token-max-age}") private int accessTokenMaxAge;
    @Value("${app.cookie.refresh-token-max-age}") private int refreshTokenMaxAge;

    public void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value).httpOnly(true).secure(secure)
                .sameSite(sameSite).path("/").maxAge(Duration.ofSeconds(maxAge)).build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void deleteCookie(HttpServletResponse response, String name) { addCookie(response, name, "", 0); }

    public Optional<String> getCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return Optional.empty();
        return Arrays.stream(request.getCookies()).filter(cookie -> name.equals(cookie.getName()))
                .map(Cookie::getValue).findFirst();
    }

    public int getAccessTokenMaxAge() { return accessTokenMaxAge; }
    public int getRefreshTokenMaxAge() { return refreshTokenMaxAge; }
}
