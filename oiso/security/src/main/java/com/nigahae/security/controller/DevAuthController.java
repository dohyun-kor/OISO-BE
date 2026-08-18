package com.nigahae.security.controller;

import com.nigahae.security.jwt.JwtTokenProvider;
import com.nigahae.security.util.CookieUtil;
import com.nigahae.user.AuthenticatedUser;
import com.nigahae.user.UserAuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "개발용 인증", description = "개발 환경에서 사용하는 인증 API")
public class DevAuthController {
    private static final String DEV_PROVIDER = "dev";

    public record DevLoginResponse(
            Long userId,
            String nickname
    ) {
    }

    private final UserAuthenticationService userAuthenticationService;
    private final JwtTokenProvider jwtTokenProvider;
    private final CookieUtil cookieUtil;

    @Operation(
            summary = "개발용 로그인",
            description = "소셜 로그인 없이 개발 사용자를 생성하거나 조회하고 JWT 쿠키를 발급합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 및 access/refresh 쿠키 발급 성공")
    })
    @PostMapping("/dev-login")
    public ResponseEntity<DevLoginResponse> login(
            @Parameter(description = "반복 로그인에 사용할 개발 사용자 식별자", example = "swagger-user")
            @RequestParam(defaultValue = "swagger-user") String providerUserId,
            @Parameter(description = "개발 사용자 닉네임", example = "Swagger 개발자")
            @RequestParam(defaultValue = "Swagger 개발자") String nickname,
            HttpServletResponse response
    ) {
        AuthenticatedUser user = userAuthenticationService.loginOrRegister(
                DEV_PROVIDER, providerUserId, nickname, null, null);

        String accessToken = jwtTokenProvider.createAccessToken(user.userId(), user.nickname());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.userId(), user.nickname());

        cookieUtil.addCookie(response, CookieUtil.ACCESS_TOKEN, accessToken,
                cookieUtil.getAccessTokenMaxAge());
        cookieUtil.addCookie(response, CookieUtil.REFRESH_TOKEN, refreshToken,
                cookieUtil.getRefreshTokenMaxAge());

        return ResponseEntity.ok(new DevLoginResponse(user.userId(), user.nickname()));
    }
}
