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
        ReflectionTestUtils.setField(securityConfig, "allowedOrigins", "http://localhost:3000");

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

    @Test
    void tourSpotReadMatcherAllowsOnlyGetOnCollectionAndSingleResourcePaths() {
        MockHttpServletRequest getTourSpots = new MockHttpServletRequest("GET", "/api/v1/tour-spots");
        MockHttpServletRequest getTourSpotDetail = new MockHttpServletRequest("GET", "/api/v1/tour-spots/300");
        MockHttpServletRequest postTourSpot = new MockHttpServletRequest("POST", "/api/v1/tour-spots");
        MockHttpServletRequest postTourSpotDetail = new MockHttpServletRequest("POST", "/api/v1/tour-spots/300");
        MockHttpServletRequest getNestedPath = new MockHttpServletRequest("GET", "/api/v1/tour-spots/300/other");
        MockHttpServletRequest getSavedPlaces = new MockHttpServletRequest("GET", "/api/v1/tour-spots/saved");
        MockHttpServletRequest getNonNumericDetail =
                new MockHttpServletRequest("GET", "/api/v1/tour-spots/not-a-number");

        assertThat(SecurityConfig.tourSpotReadRequestMatcher().matches(getTourSpots)).isTrue();
        assertThat(SecurityConfig.tourSpotReadRequestMatcher().matches(getTourSpotDetail)).isTrue();
        assertThat(SecurityConfig.tourSpotReadRequestMatcher().matches(postTourSpot)).isFalse();
        assertThat(SecurityConfig.tourSpotReadRequestMatcher().matches(postTourSpotDetail)).isFalse();
        assertThat(SecurityConfig.tourSpotReadRequestMatcher().matches(getNestedPath)).isFalse();
        assertThat(SecurityConfig.tourSpotReadRequestMatcher().matches(getSavedPlaces)).isFalse();
        assertThat(SecurityConfig.tourSpotReadRequestMatcher().matches(getNonNumericDetail)).isFalse();
    }

    @Test
    void nearbyPlaceReadMatchersAllowGetOnly() {
        MockHttpServletRequest getGlobalDetail =
                new MockHttpServletRequest("GET", "/api/v1/nearby-places/101");
        MockHttpServletRequest getNearby =
                new MockHttpServletRequest("GET", "/api/v1/tour-spots/1/nearby-places");
        MockHttpServletRequest getNearbyDetail =
                new MockHttpServletRequest("GET", "/api/v1/tour-spots/1/nearby-places/101");
        MockHttpServletRequest getNearbyWithNonNumericSpotId =
                new MockHttpServletRequest("GET", "/api/v1/tour-spots/saved/nearby-places");
        MockHttpServletRequest getGlobalDetailWithNonNumericId =
                new MockHttpServletRequest("GET", "/api/v1/nearby-places/not-a-number");

        assertThat(SecurityConfig.nearbyPlaceDetailReadRequestMatcher().matches(getGlobalDetail)).isTrue();
        assertThat(SecurityConfig.nearbyPlaceReadRequestMatcher().matches(getNearby)).isTrue();
        assertThat(SecurityConfig.nearbyPlaceReadRequestMatcher().matches(getNearbyDetail)).isFalse();
        assertThat(SecurityConfig.nearbyPlaceReadRequestMatcher().matches(getNearbyWithNonNumericSpotId)).isFalse();
        assertThat(SecurityConfig.nearbyPlaceDetailReadRequestMatcher().matches(getGlobalDetailWithNonNumericId)).isFalse();
    }
    @Test
    void featuredStoryCardsPermitOnlyPublicGetPath() {
        var matcher = SecurityConfig.storyCardFeaturedReadRequestMatcher();
        assertThat(matcher.matches(new MockHttpServletRequest("GET", "/api/v1/story-cards/featured"))).isTrue();
        assertThat(matcher.matches(new MockHttpServletRequest("POST", "/api/v1/story-cards/featured"))).isFalse();
        assertThat(matcher.matches(new MockHttpServletRequest("GET", "/api/v1/story-cards/saved"))).isFalse();
        assertThat(matcher.matches(new MockHttpServletRequest("GET", "/api/v1/story-cards/3/save"))).isFalse();
    }
}
