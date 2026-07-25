package com.nigahae.security.controller;

import com.nigahae.common.constant.ErrorCode;
import com.nigahae.common.exception.CustomException;
import com.nigahae.security.jwt.JwtTokenProvider;
import com.nigahae.security.util.CookieUtil;
import com.nigahae.user.AuthenticatedUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    public record MeResponse(Long userId, String nickname) {
    }

    private final JwtTokenProvider jwtTokenProvider;
    private final CookieUtil cookieUtil;

    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieUtil.getCookie(request, CookieUtil.REFRESH_TOKEN)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_REFRESH_TOKEN));
        if (!jwtTokenProvider.validateToken(refreshToken, JwtTokenProvider.REFRESH)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        Long userId = jwtTokenProvider.getUserId(refreshToken);
        String nickname = jwtTokenProvider.getNickname(refreshToken);
        cookieUtil.addCookie(response, CookieUtil.ACCESS_TOKEN,
                jwtTokenProvider.createAccessToken(userId, nickname), cookieUtil.getAccessTokenMaxAge());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(@AuthenticationPrincipal AuthenticatedUser user) {
        if (user == null) {
            throw new CustomException(ErrorCode.AUTH_ERROR);
        }
        return ResponseEntity.ok(new MeResponse(user.userId(), user.nickname()));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        cookieUtil.deleteCookie(response, CookieUtil.ACCESS_TOKEN);
        cookieUtil.deleteCookie(response, CookieUtil.REFRESH_TOKEN);
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }
}
