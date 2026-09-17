package com.nigahae.common.i18n;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

@Configuration
public class ApiLanguageConfig {
    @Bean
    public LocaleResolver localeResolver() {
        return new AcceptHeaderLocaleResolver() {
            @Override
            public Locale resolveLocale(HttpServletRequest request) {
                return Locale.forLanguageTag(ApiLanguage.resolve(request.getHeader("Accept-Language")));
            }
        };
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 10)
    public OncePerRequestFilter apiLanguageFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain) throws ServletException, IOException {
                var previous = RequestContextHolder.getRequestAttributes();
                var attributes = new ServletRequestAttributes(request, response);
                RequestContextHolder.setRequestAttributes(attributes);
                response.setHeader("Content-Language", ApiLanguage.resolve(request.getHeader("Accept-Language")));
                response.addHeader("Vary", "Accept-Language");
                try {
                    chain.doFilter(request, response);
                } finally {
                    RequestContextHolder.setRequestAttributes(previous);
                    attributes.requestCompleted();
                }
            }
        };
    }
}
