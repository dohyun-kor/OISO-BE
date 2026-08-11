package com.nigahae.tourspot.controller.docs;

import com.nigahae.common.response.CommonResponse;
import com.nigahae.tourspot.dto.NearbyPlaceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "주변 장소", description = "식당·숙소 상세 조회 API")
public interface NearbyPlaceControllerDocs {

    @Operation(
            summary = "주변 장소 상세 조회",
            description = "전역 고유 nearbyPlaceId로 식당·숙소 상세 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "주변 장소 상세 조회 성공"),
                    @ApiResponse(responseCode = "404", description = "주변 장소를 찾을 수 없음")
            }
    )
    CommonResponse<NearbyPlaceResponse> getNearbyPlace(
            @Parameter(
                    description = "전역 고유 주변 장소 ID",
                    required = true,
                    example = "101",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            Long nearbyPlaceId
    );
}
