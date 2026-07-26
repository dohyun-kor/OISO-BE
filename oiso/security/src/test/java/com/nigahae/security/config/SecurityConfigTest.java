package com.nigahae.security.config;

import com.nigahae.security.jwt.JwtAuthenticationFilter;
import com.nigahae.security.oauth.OAuth2LoginSuccessHandler;
import com.nigahae.security.oauth.OAuth2UserService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {

    @Test
    void corsAllowsPatchRequests() {
        SecurityConfig securityConfig = new SecurityConfig(
                mock(OAuth2UserService.class),
                mock(OAuth2LoginSuccessHandler.class),
                mock(JwtAuthenticationFilter.class)
        );
        ReflectionTestUtils.setField(securityConfig, "allowedOrigin", "http://localhost:3000");

        CorsConfiguration configuration = ((UrlBasedCorsConfigurationSource)
                securityConfig.corsConfigurationSource())
                .getCorsConfiguration(new MockHttpServletRequest());

        assertThat(configuration).isNotNull();
        assertThat(configuration.getAllowedMethods()).contains("PATCH");
    }
}
