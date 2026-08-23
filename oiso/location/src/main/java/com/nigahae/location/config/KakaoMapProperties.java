package com.nigahae.location.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.time.Duration;
@ConfigurationProperties(prefix = "kakao.map")
public class KakaoMapProperties {
    private String baseUrl = "https://dapi.kakao.com";
    private String mobilityBaseUrl = "https://apis-navi.kakaomobility.com";
    private String restApiKey = "";
    private Duration connectTimeout = Duration.ofSeconds(3);
    private Duration readTimeout = Duration.ofSeconds(10);
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String value) { baseUrl = value; }
    public String getMobilityBaseUrl() { return mobilityBaseUrl; }
    public void setMobilityBaseUrl(String value) { mobilityBaseUrl = value; }
    public String getRestApiKey() { return restApiKey; }
    public void setRestApiKey(String value) { restApiKey = value; }
    public Duration getConnectTimeout() { return connectTimeout; }
    public void setConnectTimeout(Duration value) { connectTimeout = value; }
    public Duration getReadTimeout() { return readTimeout; }
    public void setReadTimeout(Duration value) { readTimeout = value; }
}
