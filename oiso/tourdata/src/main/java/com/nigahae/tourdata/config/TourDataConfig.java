package com.nigahae.tourdata.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(TourDataProperties.class)
public class TourDataConfig {

    @Bean
    RestClient tourApiRestClient(RestClient.Builder builder, TourDataProperties properties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(properties.getApi().getConnectTimeout());
        requestFactory.setReadTimeout(properties.getApi().getReadTimeout());

        return builder
                .baseUrl(properties.getApi().getBaseUrl())
                .requestFactory(requestFactory)
                .build();
    }
}
