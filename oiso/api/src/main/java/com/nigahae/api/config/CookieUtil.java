package com.nigahae.api.config;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

public class CookieUtil {

    /**
     * 쿠키 생성 및 Response 추가
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge, boolean isHttpOnly) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setSecure(true);                  // HTTPS 환경 강제
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(isHttpOnly);          // 💡 매개변수로 받은 값 설정
        // cookie.setAttribute("SameSite", "Lax"); // 필요한 경우 활성화

        response.addCookie(cookie);
    }

    /**
     * 이름으로 쿠키 조회
     */
    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return Optional.of(cookie);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * 쿠키 삭제 (만료시간을 0으로 설정)
     */
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }
}
