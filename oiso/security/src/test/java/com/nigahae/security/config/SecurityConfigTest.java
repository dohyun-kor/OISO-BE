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

    @Test
    void festivalReadMatcherAllowsOnlyGetOnCollectionAndSingleResourcePaths() {
        MockHttpServletRequest getFestival = new MockHttpServletRequest("GET", "/api/v1/event");
        MockHttpServletRequest getFestivalDetail = new MockHttpServletRequest("GET", "/api/v1/event/300");
        MockHttpServletRequest postFestival = new MockHttpServletRequest("POST", "/api/v1/event");
        MockHttpServletRequest postFestivalDetail = new MockHttpServletRequest("POST", "/api/v1/event/300");
        MockHttpServletRequest getNestedPath = new MockHttpServletRequest("GET", "/api/v1/event/300/other");

        assertThat(SecurityConfig.festivalReadRequestMatcher().matches(getFestival)).isTrue();
        assertThat(SecurityConfig.festivalReadRequestMatcher().matches(getFestivalDetail)).isTrue();
        assertThat(SecurityConfig.festivalReadRequestMatcher().matches(postFestival)).isFalse();
        assertThat(SecurityConfig.festivalReadRequestMatcher().matches(postFestivalDetail)).isFalse();
        assertThat(SecurityConfig.festivalReadRequestMatcher().matches(getNestedPath)).isFalse();
    }
}
