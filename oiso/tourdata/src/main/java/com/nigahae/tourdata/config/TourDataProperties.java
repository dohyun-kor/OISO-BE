package com.nigahae.tourdata.config;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "tour-data")
public class TourDataProperties {

    private final Api api = new Api();
    private final Schedule schedule = new Schedule();

    @Setter
    @Getter
    public static class Api {
        private String baseUrl = "http://apis.data.go.kr";
        private String operationPath = "/B551011/KorService2/areaBasedList2";
        private String serviceKey = "";
        private int pageSize = 1000;
        private String mobileOs = "ETC";
        private String mobileApp = "AppTest";
        private String arrange = "C";
        private String lDongRegnCd = "47";
        private String lDongSignguCd = "130";
        private List<String> contentTypeIds = new ArrayList<>(List.of(""));
        private Duration connectTimeout = Duration.ofSeconds(5);
        private Duration readTimeout = Duration.ofSeconds(20);

    }

    @Getter
    @Setter
    public static class Schedule {
        private String cron = "0 0 3 * * *";

    }
}
