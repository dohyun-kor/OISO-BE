package com.nigahae.security.controller;

import com.nigahae.common.constant.ErrorCode;
import com.nigahae.common.exception.CustomException;
import com.nigahae.security.jwt.JwtTokenProvider;
import com.nigahae.security.util.CookieUtil;
import com.nigahae.user.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증", description = "인증 토큰 갱신, 사용자 확인 및 로그아웃 API")
public class AuthController {
    public static final String OAUTH2_REDIRECT_URI_SESSION_ATTRIBUTE =
            "oauth2_redirect_uri";
    @Value("${app.oauth2.allowed-redirect-uris}")
    private String allowedRedirectUrisProperty;
    public record MeResponse(Long userId, String nickname) {
    }

    private final JwtTokenProvider jwtTokenProvider;
    private final CookieUtil cookieUtil;

    @PostMapping("/refresh")
    @Operation(summary = "액세스 토큰 갱신", description = "리프레시 토큰 쿠키를 검증하고 새로운 액세스 토큰 쿠키를 발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "액세스 토큰 갱신 성공"),
            @ApiResponse(responseCode = "401", description = "리프레시 토큰이 없거나 유효하지 않음")
    })
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

    @GetMapping("/kakao")
    @Operation(
            summary = "카카오 로그인",
            description = "로그인 완료 후 이동할 프론트 주소를 저장하고 카카오 OAuth2 로그인을 시작합니다."
    )
    public void kakaoLogin(
            @RequestParam String redirectUri,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        Set<String> allowedRedirectUris =
                Arrays.stream(allowedRedirectUrisProperty.split(","))
                        .map(String::trim)
                        .collect(Collectors.toSet());

        if (!allowedRedirectUris.contains(redirectUri)) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "허용되지 않은 redirectUri입니다."
            );
            return;
        }

        request.getSession().setAttribute(
                OAUTH2_REDIRECT_URI_SESSION_ATTRIBUTE,
                redirectUri
        );

        response.sendRedirect("/oauth2/authorization/kakao");
    }

    @GetMapping("/me")
    @Operation(summary = "현재 사용자 조회", description = "현재 인증된 사용자의 식별자와 닉네임을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "현재 사용자 조회 성공",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            { "userId": 7, "nickname": "경주 여행자" }
                            """))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    public ResponseEntity<MeResponse> me(@AuthenticationPrincipal AuthenticatedUser user) {
        if (user == null) {
            throw new CustomException(ErrorCode.AUTH_ERROR);
        }
        return ResponseEntity.ok(new MeResponse(user.userId(), user.nickname()));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "액세스 토큰과 리프레시 토큰 쿠키를 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공",
            content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "로그아웃 되었습니다.")))
    public ResponseEntity<String> logout(HttpServletResponse response) {
        cookieUtil.deleteCookie(response, CookieUtil.ACCESS_TOKEN);
        cookieUtil.deleteCookie(response, CookieUtil.REFRESH_TOKEN);
        return ResponseEntity.ok(com.nigahae.common.i18n.ApiMessages.translate("로그아웃 되었습니다."));
    }
}
