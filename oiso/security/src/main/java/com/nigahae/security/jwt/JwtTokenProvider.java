package com.nigahae.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    public static final String ACCESS = "access";
    public static final String REFRESH = "refresh";
    private static final String TOKEN_TYPE = "tokenType";
    private static final String NICKNAME = "nickname";

    @Value("${jwt.secret-key}") private String secretKey;
    @Value("${jwt.access-token-validity}") private long accessTokenValidity;
    @Value("${jwt.refresh-token-validity}") private long refreshTokenValidity;

    public String createAccessToken(Long userId, String nickname) {
        return createToken(userId, nickname, ACCESS, accessTokenValidity);
    }

    public String createRefreshToken(Long userId, String nickname) {
        return createToken(userId, nickname, REFRESH, refreshTokenValidity);
    }

    private String createToken(Long userId, String nickname, String type, long validity) {
        Date now = new Date();
        return Jwts.builder().setSubject(String.valueOf(userId)).setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + validity))
                .claim(NICKNAME, nickname).claim(TOKEN_TYPE, type)
                .signWith(signingKey(), SignatureAlgorithm.HS256).compact();
    }

    public boolean validateToken(String token, String expectedType) {
        try {
            return expectedType.equals(claims(token).get(TOKEN_TYPE, String.class));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Long getUserId(String token) { return Long.valueOf(claims(token).getSubject()); }
    public String getNickname(String token) { return claims(token).get(NICKNAME, String.class); }

    private Claims claims(String token) {
        return Jwts.parserBuilder().setSigningKey(signingKey()).build().parseClaimsJws(token).getBody();
    }

    private Key signingKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
}
