package com.nigahae.tourcourse.controller.docs;

import com.nigahae.common.response.CommonResponse;
import com.nigahae.tourcourse.dto.CreateTourCourseRequest;
import com.nigahae.tourcourse.dto.CreateTourCourseResponse;
import com.nigahae.tourcourse.dto.TourCourseDetailResponse;
import com.nigahae.tourcourse.dto.TourCourseListResponse;
import com.nigahae.tourcourse.dto.UpdateTourCourseRequest;
import com.nigahae.tourcourse.dto.PatchTourCourseItemRequest;
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
    String DETAIL_SUCCESS = """
            {
              "code": 2000,
              "message": "관광 코스 상세 조회에 성공했습니다.",
              "data": {
                "tourCourseId": 1,
                "title": "경주 2일 역사 여행 코스",
                "shareYn": false,
                "items": [
                  {
                    "itemId": 11,
                    "dayNumber": 1,
                    "itemOrder": 1,
                    "category": "TOUR_SPOT",
                    "spotId": 35,
                    "nearbyPlaceId": null,
                    "parentTourSpotId": null,
                    "name": "불국사",
                    "img": "https://cdn.example.com/bulguksa.jpg",
                    "overview": "신라 불교문화를 대표하는 사찰입니다.",
                    "address": "경상북도 경주시 불국로 385",
                    "latitude": 35.7901,
                    "longitude": 129.332,
                    "like": true,
                    "transportType": null
                  },
                  {
                    "itemId": 12,
                    "dayNumber": 2,
                    "itemOrder": 1,
                    "category": "RESTAURANT",
                    "spotId": null,
                    "nearbyPlaceId": 101,
                    "parentTourSpotId": 35,
                    "name": "경주 한식당",
                    "img": null,
                    "overview": null,
                    "address": "경상북도 경주시",
                    "latitude": 35.791,
                    "longitude": 129.333,
                    "like": false,
                    "transportType": null
                  }
                ],
                "createdAt": "2026-08-18T10:00:00",
                "updatedAt": "2026-08-18T10:30:00"
              }
            }
            """;
    String UPDATE_SUCCESS = """
            {
              "code": 2000,
              "message": "관광 코스 수정에 성공했습니다.",
              "data": {
                "tourCourseId": 1,
                "title": "수정된 경주 2일 코스",
                "shareYn": true,
                "items": [
                  {
                    "itemId": 11,
                    "dayNumber": 1,
                    "itemOrder": 1,
                    "category": "TOUR_SPOT",
                    "spotId": 35,
                    "nearbyPlaceId": null,
                    "parentTourSpotId": null,
                    "name": "불국사",
                    "img": "https://cdn.example.com/bulguksa.jpg",
                    "overview": "신라 불교문화를 대표하는 사찰입니다.",
                    "address": "경상북도 경주시 불국로 385",
                    "latitude": 35.7901,
                    "longitude": 129.332,
                    "like": true,
                    "transportType": null
                  },
                  {
                    "itemId": 15,
                    "dayNumber": 1,
                    "itemOrder": 2,
                    "category": "TOUR_SPOT",
                    "spotId": 36,
                    "nearbyPlaceId": null,
                    "parentTourSpotId": null,
                    "name": "석굴암",
                    "img": "https://cdn.example.com/seokguram.jpg",
                    "overview": "통일신라 시대의 석굴 사원입니다.",
                    "address": "경상북도 경주시 석굴로 238",
                    "latitude": 35.7948,
                    "longitude": 129.3492,
                    "like": false,
                    "transportType": "CAR"
                  }
                ],
                "createdAt": "2026-08-18T10:00:00",
                "updatedAt": "2026-08-18T11:00:00"
              }
            }
            """;

    @Operation(
            summary = "관광 코스 상세 조회",
            description = "소유자가 관광 코스의 기본 정보, 장소 목록과 실제 이동 기록을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "관광 코스 상세 조회 성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = DETAIL_SUCCESS))),
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
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = UPDATE_SUCCESS))),
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
                                              "data": { "field": "items[0].transportType" }
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
                            - `dayNumber`: 여행 일차입니다. 1부터 연속이어야 합니다.
                            - `itemOrder`: 해당 일차 안의 방문 순서입니다. 각 일차마다 1부터 연속이어야 합니다.
                            - `category=TOUR_SPOT`: `spotId`만 입력하고 `nearbyPlaceId`는 null로 입력합니다.
                            - `category=RESTAURANT/ACCOMMODATION`: `nearbyPlaceId`만 입력하고 `spotId`는 null로 입력합니다.
                            """)
            @RequestBody UpdateTourCourseRequest request
    );

    @Operation(
            summary = "관광 코스 아이템 진행 상태 변경",
            description = """
                    로그인한 사용자가 소유한 관광 코스의 특정 아이템 진행 상태만 변경합니다.

                    - `tourCourseId`와 `itemId`가 모두 일치하는 아이템만 변경됩니다.
                    - `status`는 `PENDING`, `IN_PROGRESS`, `COMPLETED` 중 하나입니다.
                    - 장소, 일차, 순서, 이동 수단 등 다른 정보는 변경하지 않습니다.
                    - 수정 후 코스 전체 상세 정보를 반환합니다.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "코스 아이템 진행 상태 변경 성공",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                    {
                                      "code": 2000,
                                      "message": "관광 코스 수정에 성공했습니다.",
                                      "data": {
                                        "tourCourseId": 1,
                                        "title": "벚꽃이 흩날리는 경주 산책 코스",
                                        "shareYn": false,
                                        "items": [
                                          {
                                            "itemId": 18,
                                            "dayNumber": 1,
                                            "itemOrder": 1,
                                            "category": "TOUR_SPOT",
                                            "spotId": 32,
                                            "nearbyPlaceId": null,
                                            "parentTourSpotId": null,
                                            "name": "불국사",
                                            "img": null,
                                            "overview": null,
                                            "address": "경상북도 경주시",
                                            "latitude": 35.7900,
                                            "longitude": 129.3320,
                                            "like": false,
                                            "transportType": null,
                                            "status": "IN_PROGRESS"
                                          }
                                        ],
                                        "createdAt": "2026-08-26T10:00:00",
                                        "updatedAt": "2026-08-26T15:30:00"
                                      }
                                    }
                                    """))),
                    @ApiResponse(responseCode = "400", description = "잘못된 아이템 수정 요청",
                            content = @Content(mediaType = "application/json", examples = {
                                    @ExampleObject(name = "잘못된 상태", value = """
                                            {
                                              "code": 1001,
                                              "message": "요청 값이 올바르지 않습니다.",
                                              "data": null
                                            }
                                            """),
                                    @ExampleObject(name = "코스에 속하지 않은 아이템", value = """
                                            {
                                              "code": 4000,
                                              "message": "해당 관광 코스에 포함되지 않은 아이템입니다.",
                                              "data": { "field": "itemId", "itemId": 99 }
                                            }
                                            """)
                            })),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = AUTH_FAILURE))),
                    @ApiResponse(responseCode = "403", description = "코스 수정 권한 없음"),
                    @ApiResponse(responseCode = "404", description = "관광 코스를 찾을 수 없음",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = COURSE_NOT_FOUND)))
            }
    )
    CommonResponse<TourCourseDetailResponse> patchTourCourseItem(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser user,
            @Parameter(description = "수정할 관광 코스 ID", required = true, example = "1") Long tourCourseId,
            @Parameter(description = "수정할 코스 아이템 ID", required = true, example = "18") Long itemId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "변경할 진행 상태입니다. PENDING(진행 전), IN_PROGRESS(진행 중), COMPLETED(완료)를 사용할 수 있습니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "status": "IN_PROGRESS"
                            }
                            """)))
            @RequestBody PatchTourCourseItemRequest request
    );

    @Operation(
            summary = "관광 코스 삭제",
            description = "로그인한 사용자가 소유한 관광 코스와 포함된 코스 항목을 함께 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "관광 코스 삭제 성공",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                    {
                                      "code": 2000,
                                      "message": "관광 코스 삭제에 성공했습니다.",
                                      "data": null
                                    }
                                    """))),
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
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                    {
                                      "code": 2000,
                                      "message": "관광 코스 목록 조회에 성공했습니다.",
                                      "data": {
                                        "tourCourses": [
                                          {
                                            "tourCourseId": 1,
                                            "title": "경주 2일 역사 여행 코스",
                                            "shareYn": false,
                                            "thumbnailImg": "https://cdn.example.com/bulguksa.jpg",
                                            "itemCount": 3,
                                            "createdAt": "2026-08-18T10:00:00",
                                            "updatedAt": "2026-08-18T10:30:00"
                                          }
                                        ],
                                        "page": 0,
                                        "size": 10,
                                        "totalCount": 1
                                      }
                                    }
                                    """))),
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
            description = "인증된 사용자가 관광지와 주변 식당·숙소를 조합해 1~10개 항목의 다일 관광 코스를 생성합니다. dayNumber와 각 일차의 itemOrder는 1부터 연속이어야 합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "관광 코스 생성 성공",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                    {
                                      "code": 2001,
                                      "message": "관광 코스 생성에 성공했습니다.",
                                      "data": {
                                        "tourCourseId": 1,
                                        "title": "경주 역사 여행 코스",
                                        "shareYn": false,
                                        "itemCount": 3,
                                        "createdAt": "2026-08-18T10:00:00"
                                      }
                                    }
                                    """))),
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
                                { "category": "TOUR_SPOT", "spotId": 1, "nearbyPlaceId": null, "dayNumber": 1, "itemOrder": 1, "transportType": null },
                                { "category": "RESTAURANT", "spotId": null, "nearbyPlaceId": 101, "dayNumber": 1, "itemOrder": 2, "transportType": "CAR" },
                                { "category": "TOUR_SPOT", "spotId": 2, "nearbyPlaceId": null, "dayNumber": 2, "itemOrder": 1, "transportType": null }
                              ]
                            }
                            """)))
            @RequestBody CreateTourCourseRequest request
    );
}
