package com.nigahae.tourcourse.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class TourCourseConfig {

    @Bean
    public PasswordEncoder tourCoursePasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
