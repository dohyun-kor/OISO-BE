package com.nigahae.tourspot.controller.docs;

import com.nigahae.common.response.CommonResponse;
import com.nigahae.tourspot.dto.TourSpotDetailResponse;
import com.nigahae.tourspot.dto.PlaceListResponse;
import com.nigahae.tourspot.domain.PlaceCategory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Tag(name = "관광지", description = "관광지 조회 API")
public interface TourSpotControllerDocs {

    @Operation(
            summary = "관광지 상세 조회",
            description = "관광지 ID로 관광지 상세 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "관광지 상세 조회 성공",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                    {
                                      "code": 0,
                                      "message": "관광지 상세 조회에 성공했습니다.",
                                      "data": {
                                        "tourSpot": {
                                          "spotId": 1,
                                          "name": "불국사",
                                          "img": "https://cdn.example.com/bulguksa.jpg",
                                          "overview": "신라 불교문화를 대표하는 사찰입니다.",
                                          "address": "경상북도 경주시 불국로 385",
                                          "mapX": 129.332,
                                          "mapY": 35.7901
                                        }
                                      }
                                    }
                                    """))),
                    @ApiResponse(responseCode = "400", description = "상세 조회 실패: 관광지 ID 형식 오류",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                    {
                                      "code": 4000,
                                      "message": "잘못된 요청입니다.",
                                      "data": { "field": "spotId" }
                                    }
                                    """))),
                    @ApiResponse(responseCode = "404", description = "상세 조회 실패: 관광지 없음",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                    {
                                      "code": 4004,
                                      "message": "관광지를 찾을 수 없습니다.",
                                      "data": null
                                    }
                                    """)))
            }
    )
    CommonResponse<TourSpotDetailResponse> getTourSpot(
            @Parameter(
                    description = "조회할 관광지 ID",
                    required = true,
                    example = "1",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            String spotId
    );

    @Operation(
            summary = "장소 통합 목록 조회",
            description = "관광지·식당·숙소를 category와 검색어로 조회합니다. category를 생략하면 전체 장소를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "장소 목록 조회 성공",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                    {
                                      "code": 0,
                                      "message": "장소 목록 조회에 성공했습니다.",
                                      "data": {
                                        "places": [
                                          {
                                            "category": "TOUR_SPOT",
                                            "spotId": 1,
                                            "nearbyPlaceId": null,
                                            "name": "불국사",
                                            "img": "https://cdn.example.com/bulguksa.jpg",
                                            "overview": "신라 불교문화를 대표하는 사찰입니다.",
                                            "address": "경상북도 경주시 불국로 385"
                                          }
                                        ],
                                        "page": 0,
                                        "size": 20,
                                        "totalCount": 1
                                      }
                                    }
                                    """))),
                    @ApiResponse(responseCode = "400", description = "목록 조회 실패: 페이지 값 오류",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                    {
                                      "code": 1001,
                                      "message": "요청 값이 올바르지 않습니다.",
                                      "data": null
                                    }
                                    """))),
                    @ApiResponse(responseCode = "404", description = "목록 조회 실패: 검색 결과 없음",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                    {
                                      "code": 3001,
                                      "message": "요청한 리소스를 찾을 수 없습니다.",
                                      "data": null
                                    }
                                    """)))
            }
    )
    CommonResponse<PlaceListResponse> getTourSpots(
            @Parameter(description = "장소 종류. 생략하면 전체 조회", example = "RESTAURANT") PlaceCategory category,
            @Parameter(description = "장소 이름, 주소, 요약 검색어") String keyword,
            @Parameter(description = "페이지 번호", example = "0")
            @Min(value = 0, message = "page") int page,
            @Parameter(description = "페이지 크기(최대 100)", example = "20")
            @Min(value = 1, message = "size")
            @Max(value = 100, message = "size") int size
    );
}
