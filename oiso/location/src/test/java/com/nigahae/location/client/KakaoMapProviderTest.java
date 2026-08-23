package com.nigahae.location.client;

import com.nigahae.location.config.KakaoMapProperties;
import com.nigahae.location.domain.RouteTransportType;
import com.nigahae.location.dto.CoordinateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class KakaoMapProviderTest {

    @Test
    void calculatesCarRouteFromKakaoMobilitySummary() {
        RestClient mapClient = RestClient.builder().baseUrl("https://dapi.kakao.com").build();
        RestClient.Builder mobilityBuilder = RestClient.builder()
                .baseUrl("https://apis-navi.kakaomobility.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK test-key");
        MockRestServiceServer server = MockRestServiceServer.bindTo(mobilityBuilder).build();
        RestClient mobilityClient = mobilityBuilder.build();
        KakaoMapProperties properties = new KakaoMapProperties();
        properties.setRestApiKey("test-key");
        KakaoMapProvider provider = new KakaoMapProvider(mapClient, mobilityClient, properties);

        server.expect(requestTo(containsString("/v1/directions")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "KakaoAK test-key"))
                .andRespond(withSuccess("""
                        {
                          "routes": [{
                            "result_code": 0,
                            "result_msg": "길찾기 성공",
                            "summary": {"distance": 18432, "duration": 1560}
                          }]
                        }
                        """, MediaType.APPLICATION_JSON));

        RouteProviderResult result = provider.calculateRoute(
                new CoordinateRequest("불국사", 129.3319, 35.7900),
                new CoordinateRequest("동궁과 월지", 129.2273, 35.8349),
                RouteTransportType.CAR);

        assertThat(result.distanceMeters()).isEqualTo(18432);
        assertThat(result.durationSeconds()).isEqualTo(1560);
        assertThat(result.navigationUrl()).contains("map.kakao.com/link/to/");
        server.verify();
    }
}
