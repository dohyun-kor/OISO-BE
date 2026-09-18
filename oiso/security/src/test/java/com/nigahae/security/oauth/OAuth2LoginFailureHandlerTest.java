package com.nigahae.security.oauth;

import com.nigahae.security.controller.AuthController;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

import static org.assertj.core.api.Assertions.assertThat;

class OAuth2LoginFailureHandlerTest {
    private final OAuth2LoginFailureHandler handler =
            new OAuth2LoginFailureHandler("https://oiso-fe.vercel.app/login-success");
    private final OAuth2AuthenticationException failure =
            new OAuth2AuthenticationException(new OAuth2Error("invalid_state"));

    @Test
    void redirectsToFrontendLoginWhenThereIsNoSession() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationFailure(new MockHttpServletRequest(), response, failure);

        assertThat(response.getRedirectedUrl()).isEqualTo("https://oiso-fe.vercel.app/login?error");
    }

    @Test
    void redirectsToRequestedFrontendAndClearsSessionValue() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute(AuthController.OAUTH2_REDIRECT_URI_SESSION_ATTRIBUTE,
                "http://localhost:5173/login-success");
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationFailure(request, response, failure);

        assertThat(response.getRedirectedUrl()).isEqualTo("http://localhost:5173/login?error");
        assertThat(request.getSession().getAttribute(AuthController.OAUTH2_REDIRECT_URI_SESSION_ATTRIBUTE))
                .isNull();
    }
}
