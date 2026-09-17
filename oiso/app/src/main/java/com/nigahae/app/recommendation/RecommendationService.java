package com.nigahae.app.recommendation;

import com.nigahae.app.recommendation.RecommendationDtos.RecommendationRequest;
import com.nigahae.app.recommendation.RecommendationDtos.RecommendationResponse;
import com.nigahae.common.constant.ErrorCode;
import com.nigahae.common.exception.CustomException;
import com.nigahae.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationService {
    private final RestClient.Builder restClientBuilder;

    @Value("${recommendation.api.base-url:http://localhost:8000}")
    private String baseUrl;

    public RecommendationResponse recommend(RecommendationRequest request) {
        try {
            log.info("Calling recommendation server: baseUrl={}", baseUrl);
            CommonResponse<RecommendationResponse> response = restClientBuilder
                    .requestFactory(new SimpleClientHttpRequestFactory())
                    .baseUrl(baseUrl).build()
                    .post().uri("/api/v1/recommendations/courses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Accept-Language", com.nigahae.common.i18n.ApiLanguage.isEnglish() ? "en" : "ko")
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, res) -> {
                        String body;
                        try {
                            body = new String(res.getBody().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                        } catch (Exception ignored) {
                            body = "<unable to read response body>";
                        }
                        log.error("Recommendation server returned error: status={}, body={}",
                                res.getStatusCode(), body);
                        throw new CustomException(res.getStatusCode().is5xxServerError()
                                ? ErrorCode.RECOMMENDATION_UNAVAILABLE : ErrorCode.RECOMMENDATION_UNAVAILABLE);
                    })
                    .body(new ParameterizedTypeReference<CommonResponse<RecommendationResponse>>() {});
            if (response == null || response.data() == null) {
                throw new CustomException(ErrorCode.RECOMMENDATION_UNAVAILABLE);
            }
            return response.data();
        } catch (ResourceAccessException e) {
            log.error("Unable to connect to recommendation server: baseUrl={}", baseUrl, e);
            throw new CustomException(ErrorCode.RECOMMENDATION_TIMEOUT);
        } catch (RuntimeException e) {
            if (e instanceof CustomException custom) throw custom;
            log.error("Unexpected recommendation integration error: baseUrl={}", baseUrl, e);
            throw new CustomException(ErrorCode.RECOMMENDATION_UNAVAILABLE);
        }
    }
}
