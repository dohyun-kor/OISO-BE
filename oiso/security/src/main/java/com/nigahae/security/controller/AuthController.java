package com.nigahae.security.controller;

import com.nigahae.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.nigahae.security.util.CookieUtil.addCookie;
import static com.nigahae.security.util.CookieUtil.getCookie;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getCookie(request, "refreshToken")
                .map(Cookie::getValue)
                .orElseThrow(() -> new RuntimeException("Refresh Token이 없습니다."));

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("만료되거나 유효하지 않은 Refresh Token입니다.");
        }

        String email = jwtTokenProvider.getEmail(refreshToken);
        String nickname = jwtTokenProvider.getNickname(refreshToken);

        String newAccessToken = jwtTokenProvider.createAccessToken(email, nickname);

        addCookie(response, "accessToken", newAccessToken, 1800, false);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        addCookie(response, "accessToken", "", 0, false);
        addCookie(response, "refreshToken", "", 0, false);

        return ResponseEntity.ok().body("로그아웃 되었습니다.");
    }
}