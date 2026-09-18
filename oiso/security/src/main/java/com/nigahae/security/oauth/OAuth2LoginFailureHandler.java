package com.nigahae.security.oauth;

import com.nigahae.security.controller.AuthController;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;

@Slf4j
@Component
public class OAuth2LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private final String defaultRedirectUri;
    private final String localRedirectUri;

    public OAuth2LoginFailureHandler(@Value("${app.oauth2.success-redirect-uri}") String defaultRedirectUri,
                                     @Value("${app.oauth2.local-success-redirect-uri}") String localRedirectUri) {
        this.defaultRedirectUri = defaultRedirectUri;
        this.localRedirectUri = localRedirectUri;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String errorCode = exception instanceof OAuth2AuthenticationException oauth
                ? oauth.getError().getErrorCode() : "authentication_failure";
        // Provider descriptions and exception messages may contain credentials or response bodies.
        String safeErrorCode = errorCode != null && errorCode.matches("[a-zA-Z0-9_.-]{1,80}")
                ? errorCode : "unrecognized_error";
        log.warn("OAuth login failed: errorCode={}, exceptionType={}, causeType={}, sessionPresent={}, requestedSessionPresent={}, requestedSessionValid={}, sessionFromCookie={}",
                safeErrorCode, exception.getClass().getSimpleName(),
                exception.getCause() == null ? "none" : exception.getCause().getClass().getSimpleName(),
                request.getSession(false) != null, request.getRequestedSessionId() != null,
                request.isRequestedSessionIdValid(), request.isRequestedSessionIdFromCookie());
        HttpSession session = request.getSession(false);
        Object requestedRedirectUri = session == null ? null
                : session.getAttribute(AuthController.OAUTH2_REDIRECT_URI_SESSION_ATTRIBUTE);
        if (session != null) {
            session.removeAttribute(AuthController.OAUTH2_REDIRECT_URI_SESSION_ATTRIBUTE);
        }
        boolean localLogin = request.getRequestURI().equals(
                request.getContextPath() + "/login/oauth2/code/kakao-local");
        String redirectUri;
        if (localLogin) {
            redirectUri = localRedirectUri;
        } else if (requestedRedirectUri instanceof String uri) {
            redirectUri = uri;
        } else {
            redirectUri = defaultRedirectUri;
        }
        String loginUri = URI.create(redirectUri).resolve("/login?error").toString();
        getRedirectStrategy().sendRedirect(request, response, loginUri);
    }
}
