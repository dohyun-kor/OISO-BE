package com.nigahae.tourcourse.controller.docs;

import com.nigahae.common.response.CommonResponse;
import com.nigahae.tourcourse.dto.CreateTourCourseRequest;
import com.nigahae.tourcourse.dto.CreateTourCourseResponse;
import com.nigahae.tourcourse.dto.TourCourseDetailResponse;
import com.nigahae.tourcourse.dto.TourCourseListResponse;
import com.nigahae.tourcourse.dto.UpdateTourCourseRequest;
import com.nigahae.user.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "관광 코스", description = "사용자 관광 코스 API")
public interface TourCourseControllerDocs {

    String AUTH_FAILURE = """
            {
              "code": 4001,
              "message": "인증 또는 권한 확인이 필요합니다.",
              "data": null
            }
            """;
    String COURSE_NOT_FOUND = """
            {
              "code": 4004,
              "message": "관광 코스를 찾을 수 없습니다.",
              "data": null
            }
            """;
    String INVALID_COURSE_ID = """
            {
              "code": 4000,
              "message": "잘못된 요청입니다.",
              "data": { "field": "tourCourseId" }
            }
            """;
    String COMMON_VALIDATION_FAILURE = """
            {
              "code": 1001,
              "message": "요청 값이 올바르지 않습니다.",
              "data": null
            }
            """;

    @Operation(
            summary = "관광 코스 상세 조회",
            description = "소유자가 관광 코스의 기본 정보, 장소 목록과 실제 이동 기록을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "관광 코스 상세 조회 성공",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "상세 조회 실패: 잘못된 관광 코스 ID",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = INVALID_COURSE_ID))),
                    @ApiResponse(responseCode = "401", description = "상세 조회 실패: 인증되지 않은 사용자",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = AUTH_FAILURE))),
                    @ApiResponse(responseCode = "403", description = "상세 조회 실패: 조회 권한 없음",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                    {
                                      "code": 4003,
                                      "message": "관광 코스를 조회할 권한이 없습니다.",
                                      "data": null
                                    }
                                    """))),
                    @ApiResponse(responseCode = "404", description = "상세 조회 실패: 관광 코스 없음",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = COURSE_NOT_FOUND)))
            }
    )
    CommonResponse<TourCourseDetailResponse> getTourCourseDetail(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser user,
            @Parameter(description = "조회할 관광 코스 ID", required = true, example = "1") Long tourCourseId
    );

    @Operation(
            summary = "관광 코스 기본 정보 수정",
            description = "요청의 최종 상태를 기준으로 코스 정보, 관광지 목록과 이동 기록을 추가·수정·삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "관광 코스 수정 성공",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "수정 실패: 요청 값 검증 실패",
                            content = @Content(mediaType = "application/json", examples = {
                                    @ExampleObject(name = "잘못된 제목", value = """
                                            {
                                              "code": 4000,
                                              "message": "관광 코스 제목은 필수입니다.",
                                              "data": { "field": "title" }
                                            }
                                            """),
                                    @ExampleObject(name = "관광지 순서 중복", value = """
                                            {
                                              "code": 4000,
                                              "message": "관광지 순서가 중복되었습니다.",
                                              "data": { "field": "items[1].itemOrder" }
                                            }
                                            """),
                                    @ExampleObject(name = "음수 이동거리", value = """
                                            {
                                              "code": 4000,
                                              "message": "이동거리는 0 이상이어야 합니다.",
                                              "data": { "field": "items[1].distanceMeter" }
                                            }
                                            """),
                                    @ExampleObject(name = "다른 코스 아이템", value = """
                                            {
                                              "code": 4000,
                                              "message": "해당 관광 코스에 포함되지 않은 아이템입니다.",
                                              "data": { "field": "items[0].itemId", "itemId": 99 }
                                            }
                                            """)
                            })),
                    @ApiResponse(responseCode = "401", description = "수정 실패: 인증되지 않은 사용자",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = AUTH_FAILURE))),
                    @ApiResponse(responseCode = "403", description = "수정 실패: 수정 권한 없음",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                    {
                                      "code": 4003,
                                      "message": "관광 코스를 수정할 권한이 없습니다.",
                                      "data": null
                                    }
                                    """))),
                    @ApiResponse(responseCode = "404", description = "수정 실패: 관광 코스 또는 관광지 없음",
                            content = @Content(mediaType = "application/json", examples = {
                                    @ExampleObject(name = "관광 코스 없음", value = COURSE_NOT_FOUND),
                                    @ExampleObject(name = "관광지 없음", value = """
                                            {
                                              "code": 4004,
                                              "message": "관광지를 찾을 수 없습니다.",
                                              "data": { "field": "items[2].spotId", "spotId": 999 }
                                            }
                                            """)
                            }))
            }
    )
    CommonResponse<TourCourseDetailResponse> updateTourCourse(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser user,
            @Parameter(description = "수정할 관광 코스 ID", required = true, example = "1") Long tourCourseId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = """
                            수정 후 관광 코스의 최종 상태입니다.

                            - `itemId`: 코스에 포함된 항목 자체의 ID입니다. 기존 항목은 상세 조회의 `itemId`를 입력하고, 새 항목은 `null`로 입력합니다.
                            - `category=TOUR_SPOT`: `spotId`만 입력하고 `nearbyPlaceId`는 null로 입력합니다.
                            - `category=RESTAURANT/ACCOMMODATION`: `nearbyPlaceId`만 입력하고 `spotId`는 null로 입력합니다.
                            """)
            @RequestBody UpdateTourCourseRequest request
    );

    @Operation(
            summary = "관광 코스 삭제",
            description = "로그인한 사용자가 소유한 관광 코스와 포함된 코스 항목을 함께 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "관광 코스 삭제 성공"),
                    @ApiResponse(responseCode = "400", description = "삭제 실패: 잘못된 관광 코스 ID",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = INVALID_COURSE_ID))),
                    @ApiResponse(responseCode = "401", description = "삭제 실패: 인증되지 않은 사용자",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = AUTH_FAILURE))),
                    @ApiResponse(responseCode = "403", description = "삭제 실패: 삭제 권한 없음"),
                    @ApiResponse(responseCode = "404", description = "삭제 실패: 관광 코스 없음",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = COURSE_NOT_FOUND)))
            }
    )
    CommonResponse<Void> deleteTourCourse(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser user,
            @Parameter(description = "삭제할 관광 코스 ID", required = true, example = "1") Long tourCourseId
    );

    @Operation(
            summary = "내 관광 코스 목록 조회",
            description = "로그인한 사용자가 생성한 관광 코스를 페이징하여 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "관광 코스 목록 조회 성공",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "목록 조회 실패: 페이지 또는 정렬 값 오류",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = COMMON_VALIDATION_FAILURE))),
                    @ApiResponse(responseCode = "401", description = "목록 조회 실패: 인증되지 않은 사용자",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = AUTH_FAILURE))),
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
    CommonResponse<TourCourseListResponse> getMyTourCourses(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser user,
            @Parameter(description = "현재 페이지 번호(0 이상)", example = "0") @RequestParam int page,
            @Parameter(description = "페이지 크기(1~100)", example = "10") @RequestParam int size,
            @Parameter(description = "정렬 조건", example = "createdAt,desc") @RequestParam String sort,
            @Parameter(description = "관광 코스 제목 검색어", example = "경주") @RequestParam String keyword
    );

    @Operation(
            summary = "관광 코스 생성",
            description = "인증된 사용자가 관광지와 주변 식당·숙소를 조합해 1~10개 항목의 관광 코스를 생성합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "관광 코스 생성 성공",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "생성 실패: 요청 값 검증 실패",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = COMMON_VALIDATION_FAILURE))),
                    @ApiResponse(responseCode = "401", description = "생성 실패: 인증되지 않은 사용자",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = AUTH_FAILURE))),
                    @ApiResponse(responseCode = "404", description = "생성 실패: 관광지 없음",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                    {
                                      "code": 3001,
                                      "message": "요청한 리소스를 찾을 수 없습니다.",
                                      "data": null
                                    }
                                    """)))
            }
    )
    CommonResponse<CreateTourCourseResponse> createTourCourse(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser user,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "TOUR_SPOT은 spotId만, RESTAURANT/ACCOMMODATION은 nearbyPlaceId만 입력합니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "title": "경주 역사 여행 코스",
                              "shareYn": false,
                              "sharedPassword": null,
                              "items": [
                                { "category": "TOUR_SPOT", "spotId": 1, "nearbyPlaceId": null, "itemOrder": 1, "transportType": null },
                                { "category": "RESTAURANT", "spotId": null, "nearbyPlaceId": 101, "itemOrder": 2, "transportType": "CAR" }
                              ]
                            }
                            """)))
            @RequestBody CreateTourCourseRequest request
    );
}
