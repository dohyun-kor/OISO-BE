package com.nigahae.tourspot.controller.docs;

import com.nigahae.common.response.CommonResponse;
import com.nigahae.tourspot.dto.TourSpotDetailResponse;
import com.nigahae.tourspot.dto.TourSpotListResponse;
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
                    @ApiResponse(
                            responseCode = "200",
                            description = "Example Response - 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "Example Response",
                                            summary = "조회 성공",
                                            value = """
                                                    {
                                                      "code": 0,
                                                      "message": "관광지 상세 조회에 성공했습니다.",
                                                      "data": {
                                                        "tourSpot": {
                                                          "spotId": 1,
                                                          "name": "불국사",
                                                          "img": "https://oiso.s3.ap-northeast-2.amazonaws.com/tourspot/bulguksa.jpg",
                                                          "content": "불국사는 신라 시대에 창건된 대표적인 사찰로, 신라 불교문화와 건축 기술을 보여주는 문화유산입니다.",
                                                          "address": "경상북도 경주시 불국로 385",
                                                          "canParking": true,
                                                          "canPet": false,
                                                          "like": false,
                                                          "crowdLevel": 2,
                                                          "overview": "신라 불교문화의 정수를 보여주는 대표 문화유산",
                                                          "homepage": "https://www.bulguksa.or.kr",
                                                          "mapX": 129.3319,
                                                          "mapY": 35.7900,
                                                          "contentId": 126508,
                                                          "contentTypeId": 12,
                                                          "tel": "054-746-9913"
                                                        }
                                                      }
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Example Response - 잘못된 관광지 ID",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "Example Response",
                                            summary = "잘못된 관광지 ID",
                                            value = """
                                                    {
                                                      "code": 4000,
                                                      "message": "잘못된 요청입니다.",
                                                      "data": {
                                                        "field": "spotId"
                                                      }
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Example Response - 관광지 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "Example Response",
                                            summary = "관광지 없음",
                                            value = """
                                                    {
                                                      "code": 4004,
                                                      "message": "관광지를 찾을 수 없습니다.",
                                                      "data": null
                                                    }
                                                    """
                                    )
                            )
                    )
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
            summary = "관광지 목록 조회",
            description = "관광지 이름, 주소, 요약으로 검색하고 페이지 단위로 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Example Response - 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "Example Response",
                                            summary = "조회 성공",
                                            value = """
                                                    {
                                                      "code": 0,
                                                      "message": "관광지 목록 조회에 성공했습니다.",
                                                      "data": {
                                                        "tourSpots": [
                                                          {
                                                            "spotId": 1,
                                                            "name": "관광지 이름 예시",
                                                            "img": "https://example.com/tour-spot.jpg",
                                                            "overview": "관광지 요약 예시",
                                                            "address": "관광지 주소 예시",
                                                            "like": false
                                                          }
                                                        ],
                                                        "page": 0,
                                                        "size": 20,
                                                        "totalCount": 1
                                                      }
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Example Response - 잘못된 페이지 요청",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "Example Response",
                                            summary = "잘못된 페이지 요청",
                                            value = """
                                                    {
                                                      "code": 1001,
                                                      "message": "요청 값이 올바르지 않습니다.",
                                                      "data": null
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Example Response - 검색 결과 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "Example Response",
                                            summary = "검색 결과 없음",
                                            value = """
                                                    {
                                                      "code": 3001,
                                                      "message": "요청한 리소스를 찾을 수 없습니다.",
                                                      "data": null
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    CommonResponse<TourSpotListResponse> getTourSpots(
            @Parameter(description = "관광지 이름, 주소, 요약 검색")
            String keyword,
            @Parameter(description = "페이지 번호", example = "0")
            @Min(value = 0, message = "page") int page,
            @Parameter(description = "페이지 크기(최대 100)", example = "20")
            @Min(value = 1, message = "size")
            @Max(value = 100, message = "size")
            int size
    );
}
