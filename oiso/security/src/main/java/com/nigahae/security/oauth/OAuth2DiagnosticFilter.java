package com.nigahae.security.oauth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

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
        log.info("OAuth request start: requestId={}, path={}, sessionPresent={}, requestedSessionPresent={}, requestedSessionValid={}, sessionFromCookie={}, codePresent={}, statePresent={}, providerErrorPresent={}",
                requestId, request.getServletPath(), request.getSession(false) != null,
                request.getRequestedSessionId() != null, request.isRequestedSessionIdValid(),
                request.isRequestedSessionIdFromCookie(), request.getParameter("code") != null,
                request.getParameter("state") != null, request.getParameter("error") != null);
        try {
            chain.doFilter(request, response);
        } finally {
            log.info("OAuth request end: requestId={}, path={}, status={}, sessionPresent={}",
                    requestId, request.getServletPath(), response.getStatus(), request.getSession(false) != null);
        }
    }
}
