package com.nigahae.location.config;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
@Configuration
@EnableConfigurationProperties(KakaoMapProperties.class)
public class KakaoMapConfig {
    @Bean RestClient kakaoMapRestClient(RestClient.Builder builder, KakaoMapProperties properties) {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.getConnectTimeout());
        factory.setReadTimeout(properties.getReadTimeout());
        return builder.baseUrl(properties.getBaseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + properties.getRestApiKey())
                .requestFactory(factory).build();
    }

    @Bean RestClient kakaoMobilityRestClient(RestClient.Builder builder, KakaoMapProperties properties) {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.getConnectTimeout());
        factory.setReadTimeout(properties.getReadTimeout());
        return builder.baseUrl(properties.getMobilityBaseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + properties.getRestApiKey())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .requestFactory(factory).build();
    }
}
