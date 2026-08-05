package com.nigahae.tourcourse.controller.docs;

import com.nigahae.common.response.CommonResponse;
import com.nigahae.tourcourse.dto.CreateTourCourseRequest;
import com.nigahae.tourcourse.dto.CreateTourCourseResponse;
import com.nigahae.tourcourse.dto.TourCourseListResponse;
import com.nigahae.user.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "관광 코스", description = "사용자 관광 코스 API")
public interface TourCourseControllerDocs {

    @Operation(
            summary = "내 관광 코스 목록 조회",
            description = "로그인한 사용자가 생성한 관광 코스를 페이징하여 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "관광 코스 목록 조회 성공",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                    {
                                      "code": 2000,
                                      "message": "관광 코스 목록 조회에 성공했습니다.",
                                      "data": {
                                        "tourCourses": [
                                          {
                                            "tourCourseId": 1,
                                            "title": "경주 역사 여행 코스",
                                            "shareYn": false,
                                            "thumbnailImg": "https://cdn.example.com/bulguksa.jpg",
                                            "itemCount": 3,
                                            "totalDistanceMeter": 13500,
                                            "totalDurationSecond": 2300,
                                            "createdAt": "2026-07-11T15:00:00",
                                            "updatedAt": "2026-07-11T15:00:00"
                                          }
                                        ],
                                        "page": 0,
                                        "size": 10,
                                        "totalCount": 1
                                      }
                                    }
                                    """))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 페이지 또는 정렬 요청",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                    {
                                      "code": 1001,
                                      "message": "요청 값이 올바르지 않습니다.",
                                      "data": null
                                    }
                                    """))
                    ),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "검색 결과 없음",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                    {
                                      "code": 3001,
                                      "message": "요청한 리소스를 찾을 수 없습니다.",
                                      "data": null
                                    }
                                    """))
                    )
            }
    )
    CommonResponse<TourCourseListResponse> getMyTourCourses(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser user,
            @Parameter(description = "현재 페이지 번호(0 이상)", example = "0")
            @RequestParam int page,
            @Parameter(description = "페이지 크기(1~100)", example = "10")
            @RequestParam int size,
            @Parameter(description = "정렬 조건", example = "createdAt,desc")
            @RequestParam String sort,
            @Parameter(description = "관광 코스 제목 검색어", example = "경주")
            @RequestParam String keyword
    );

    @Operation(
            summary = "관광 코스 생성",
            description = "인증된 사용자가 1~10개의 관광지로 새로운 관광 코스를 생성합니다.",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = @Parameter(
                    name = "Authorization",
                    description = "Bearer AccessToken",
                    required = true,
                    in = ParameterIn.HEADER,
                    example = "Bearer eyJhbGciOiJIUzI1NiJ9..."
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "관광 코스 생성 성공",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                    {
                                      "code": 2001,
                                      "message": "관광 코스 생성에 성공했습니다.",
                                      "data": {
                                        "tourCourseId": 1,
                                        "title": "경주 역사 여행 코스",
                                        "shareYn": false,
                                        "itemCount": 3,
                                        "createdAt": "2026-07-11T15:00:00"
                                      }
                                    }
                                    """))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "요청 값 검증 실패",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                    {
                                      "code": 1001,
                                      "message": "요청 값이 올바르지 않습니다.",
                                      "data": null
                                    }
                                    """))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 실패",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "관광지를 찾을 수 없음",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                    {
                                      "code": 3001,
                                      "message": "요청한 리소스를 찾을 수 없습니다.",
                                      "data": null
                                    }
                                    """))
                    )
            }
    )
    CommonResponse<CreateTourCourseResponse> createTourCourse(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser user,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "생성할 관광 코스 정보",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "title": "경주 역사 여행 코스",
                              "shareYn": false,
                              "sharedPassword": null,
                              "items": [
                                { "spotId": 1, "itemOrder": 1, "transportType": null },
                                { "spotId": 2, "itemOrder": 2, "transportType": "CAR" },
                                { "spotId": 3, "itemOrder": 3, "transportType": "WALK" }
                              ]
                            }
                            """))
            )
            @RequestBody CreateTourCourseRequest request
    );
}
