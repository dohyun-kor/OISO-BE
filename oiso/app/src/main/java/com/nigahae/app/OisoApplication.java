package com.nigahae.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = "com.nigahae") // 서비스, 컨트롤러 스캔
@EnableJpaRepositories(basePackages = "com.nigahae") // 레포지토리 스캔
@EntityScan(basePackages = "com.nigahae") // @Entity 위치 지정
@EnableScheduling
public class OisoApplication {

    public static void main(String[] args) {
        SpringApplication.run(OisoApplication.class, args);
    }

}
