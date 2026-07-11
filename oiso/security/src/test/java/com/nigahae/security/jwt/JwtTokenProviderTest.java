package com.nigahae.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {
    private final JwtTokenProvider provider = new JwtTokenProvider();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(provider, "secretKey", "0123456789012345678901234567890123456789");
        ReflectionTestUtils.setField(provider, "accessTokenValidity", 60_000L);
        ReflectionTestUtils.setField(provider, "refreshTokenValidity", 120_000L);
    }

    @Test
    void tokenUsesInternalUserIdAndExpectedType() {
        String access = provider.createAccessToken(42L, "닉네임");
        String refresh = provider.createRefreshToken(42L, "닉네임");
        assertThat(provider.getUserId(access)).isEqualTo(42L);
        assertThat(provider.getNickname(access)).isEqualTo("닉네임");
        assertThat(provider.validateToken(access, JwtTokenProvider.ACCESS)).isTrue();
        assertThat(provider.validateToken(access, JwtTokenProvider.REFRESH)).isFalse();
        assertThat(provider.validateToken(refresh, JwtTokenProvider.REFRESH)).isTrue();
    }

    @Test
    void rejectsTamperedToken() {
        String token = provider.createAccessToken(1L, "user") + "x";
        assertThat(provider.validateToken(token, JwtTokenProvider.ACCESS)).isFalse();
    }
}
