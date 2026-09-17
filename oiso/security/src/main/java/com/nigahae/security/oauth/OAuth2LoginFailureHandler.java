package com.nigahae.security.oauth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;

@Slf4j
public class OAuth2LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    public OAuth2LoginFailureHandler() {
        super("/login?error");
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
        super.onAuthenticationFailure(request, response, exception);
    }
}
