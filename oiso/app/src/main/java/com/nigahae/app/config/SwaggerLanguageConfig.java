package com.nigahae.app.config;

import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.media.StringSchema;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerLanguageConfig {

    @Bean
    public OpenApiCustomizer acceptLanguageHeader() {
        return openApi -> {
            if (openApi.getPaths() == null) return;
            openApi.getPaths().forEach((path, pathItem) -> {
                if (!supportsEnglish(path) || pathItem.getGet() == null) return;
                pathItem.getGet().addParametersItem(new Parameter()
                        .in("header")
                        .name("Accept-Language")
                        .required(false)
                        .description("응답 언어: en(영어), ko(한국어). 생략하면 한국어이며 영문 값이 없는 필드는 한국어로 반환됩니다.")
                        .schema(new StringSchema().example("en")));
            });
        };
    }

    private static boolean supportsEnglish(String path) {
        return path.startsWith("/api/v1/tour-spots")
                || path.startsWith("/api/v1/nearby-places")
                || path.startsWith("/api/v1/story-cards")
                || path.startsWith("/api/v1/tour-courses");
    }
}
