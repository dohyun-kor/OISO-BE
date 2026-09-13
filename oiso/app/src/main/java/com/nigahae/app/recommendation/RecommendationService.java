package com.nigahae.app.recommendation;

import com.nigahae.app.recommendation.RecommendationDtos.RecommendationRequest;
import com.nigahae.app.recommendation.RecommendationDtos.RecommendationResponse;
import com.nigahae.common.constant.ErrorCode;
import com.nigahae.common.exception.CustomException;
import com.nigahae.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RestClient.Builder restClientBuilder;

    @Value("${recommendation.api.base-url:http://localhost:8000}")
    private String baseUrl;

    public RecommendationResponse recommend(RecommendationRequest request) {
        try {
            CommonResponse<RecommendationResponse> response = restClientBuilder.baseUrl(baseUrl).build()
                    .post().uri("/api/v1/recommendations/courses")
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, res) -> {
                        throw new CustomException(res.getStatusCode().is5xxServerError()
                                ? ErrorCode.RECOMMENDATION_UNAVAILABLE : ErrorCode.RECOMMENDATION_UNAVAILABLE);
                    })
                    .body(new ParameterizedTypeReference<CommonResponse<RecommendationResponse>>() {});
            if (response == null || response.data() == null) {
                throw new CustomException(ErrorCode.RECOMMENDATION_UNAVAILABLE);
            }
            return response.data();
        } catch (ResourceAccessException e) {
            throw new CustomException(ErrorCode.RECOMMENDATION_TIMEOUT);
        } catch (RuntimeException e) {
            if (e instanceof CustomException custom) throw custom;
            throw new CustomException(ErrorCode.RECOMMENDATION_UNAVAILABLE);
        }
    }
}
