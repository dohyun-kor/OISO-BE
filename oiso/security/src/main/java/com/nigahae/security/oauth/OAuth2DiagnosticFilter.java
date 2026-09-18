package com.nigahae.security.oauth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/** Logs only presence flags, never OAuth parameters, cookies, or session IDs. */
@Slf4j
public class OAuth2DiagnosticFilter extends OncePerRequestFilter {
    private static final Set<String> PATHS = Set.of(
            "/api/auth/kakao", "/oauth2/authorization/kakao", "/login/oauth2/code/kakao");

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !PATHS.contains(request.getServletPath());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String requestId = UUID.randomUUID().toString();
        log.info("OAuth request start: requestId={}, path={}, host={}, forwardedHost={}, sessionPresent={}, sessionFingerprint={}, requestedSessionPresent={}, requestedSessionFingerprint={}, requestedSessionValid={}, sessionFromCookie={}, codePresent={}, statePresent={}, providerErrorPresent={}",
                requestId, request.getServletPath(), request.getHeader("Host"),
                request.getHeader("X-Forwarded-Host"), request.getSession(false) != null,
                fingerprint(request.getSession(false) == null ? null : request.getSession(false).getId()),
                request.getRequestedSessionId() != null, fingerprint(request.getRequestedSessionId()),
                request.isRequestedSessionIdValid(),
                request.isRequestedSessionIdFromCookie(), request.getParameter("code") != null,
                request.getParameter("state") != null, request.getParameter("error") != null);
        try {
            chain.doFilter(request, response);
        } finally {
            log.info("OAuth request end: requestId={}, path={}, status={}, sessionPresent={}, sessionFingerprint={}, setCookieNames={}",
                    requestId, request.getServletPath(), response.getStatus(), request.getSession(false) != null,
                    fingerprint(request.getSession(false) == null ? null : request.getSession(false).getId()),
                    response.getHeaders("Set-Cookie").stream()
                            .map(OAuth2DiagnosticFilter::cookieName)
                            .collect(Collectors.toSet()));
        }
    }

    private static String fingerprint(String value) {
        if (value == null) return "none";
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest, 0, 6);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

    private static String cookieName(String setCookie) {
        if (setCookie == null) return "unknown";
        int separator = setCookie.indexOf('=');
        return separator > 0 ? setCookie.substring(0, separator) : "unknown";
    }
}
