package com.nigahae.tourspot.controller.docs;

import com.nigahae.common.response.CommonResponse;
import com.nigahae.tourspot.dto.DeleteSavedTourSpotResponse;
import com.nigahae.tourspot.dto.SaveTourSpotResponse;
import com.nigahae.tourspot.dto.SavedNearbyPlaceResponse;
import com.nigahae.tourspot.dto.PlaceListResponse;
import com.nigahae.tourspot.domain.PlaceCategory;
import com.nigahae.tourspot.dto.TourSpotDetailResponse;
import com.nigahae.tourspot.dto.PlaceListResponse;
import com.nigahae.tourspot.domain.PlaceCategory;
import com.nigahae.tourspot.domain.StampMissionType;
import com.nigahae.tourspot.dto.StampMissionClearResponse;
import com.nigahae.user.AuthenticatedUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Tag(name = "관광지", description = "관광지 조회 및 저장 API")
public interface TourSpotControllerDocs {

    @Operation(
            summary = "저장한 장소 목록 조회",
            description = """
                    로그인한 사용자가 저장한 관광지, 식당, 숙소를 최신 저장 순으로 조회합니다.

                    - `category`를 생략하면 모든 저장 장소를 조회합니다.
                    - `TOUR_SPOT`은 `spotId`, `RESTAURANT`와 `ACCOMMODATION`은 `nearbyPlaceId`를 반환합니다.
                    - 저장 장소가 없으면 빈 목록과 `totalCount=0`을 반환합니다.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "저장한 장소 목록 조회 성공",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                    {
                                      "code": 2000,
                                      "message": "저장한 장소 목록 조회에 성공했습니다.",
                                      "data": {
                                        "places": [
                                          {
                                            "category": "RESTAURANT",
                                            "spotId": null,
                                            "nearbyPlaceId": 101,
                                            "parentTourSpotId": 35,
                                            "name": "경주 맛집",
                                            "img": null,
                                            "overview": null,
                                            "address": "경상북도 경주시",
                                            "latitude": 35.8,
                                            "longitude": 129.2
                                          }
                                        ],
                                        "page": 0,
                                        "size": 20,
                                        "totalCount": 1
                                      }
                                    }
                                    """))),
                    @ApiResponse(responseCode = "400", description = "잘못된 카테고리 또는 페이지 값"),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
            }
    )
    CommonResponse<PlaceListResponse> getSavedPlaces(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser user,
            @Parameter(description = "장소 종류", example = "RESTAURANT") PlaceCategory category,
            @Parameter(description = "페이지 번호", example = "0")
            @Min(value = 0, message = "page") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @Min(value = 1, message = "size")
            @Max(value = 100, message = "size") int size
    );

    @Operation(
            summary = "숙소·식당 저장",
            description = "nearbyPlaceId로 숙소 또는 식당을 저장합니다. 이미 저장된 장소는 중복 저장하지 않습니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "숙소·식당 저장 성공"),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
                    @ApiResponse(responseCode = "404", description = "숙소·식당을 찾을 수 없음")
            }
    )
    CommonResponse<SavedNearbyPlaceResponse> saveNearbyPlace(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser user,
            @Parameter(description = "저장할 숙소·식당의 nearbyPlaceId", example = "101") Long nearbyPlaceId
    );

    @Operation(
            summary = "저장한 숙소·식당 삭제",
            description = "nearbyPlaceId에 해당하는 숙소 또는 식당을 저장 목록에서 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "저장한 숙소·식당 삭제 성공"),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
                    @ApiResponse(responseCode = "404", description = "숙소·식당을 찾을 수 없음")
            }
    )
    CommonResponse<SavedNearbyPlaceResponse> deleteSavedNearbyPlace(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser user,
            @Parameter(description = "삭제할 숙소·식당의 nearbyPlaceId", example = "101") Long nearbyPlaceId
    );

    @Operation(
            summary = "저장한 관광지 삭제",
            description = "인증된 사용자의 저장 목록에서 관광지를 삭제합니다. 이미 삭제된 관광지에도 동일하게 성공합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "삭제 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "code": 0,
                                                      "message": "저장한 관광지 삭제에 성공했습니다.",
                                                      "data": {
                                                        "spotId": 1,
                                                        "like": false
                                                      }
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 관광지 ID"),
                    @ApiResponse(responseCode = "401", description = "인증 필요"),
                    @ApiResponse(responseCode = "404", description = "관광지 없음")
            }
    )
    CommonResponse<DeleteSavedTourSpotResponse> deleteSavedTourSpot(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser user,
            @Parameter(
                    description = "저장 목록에서 삭제할 관광지 ID",
                    required = true,
                    example = "1",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            String spotId
    );

    @Operation(
            summary = "관광지 저장",
            description = "인증된 사용자의 저장 목록에 관광지를 추가합니다. 이미 저장한 관광지는 중복 저장하지 않습니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "저장 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "code": 0,
                                                      "message": "관광지 저장에 성공했습니다.",
                                                      "data": {
                                                        "spotId": 1,
                                                        "like": true
                                                      }
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 관광지 ID"),
                    @ApiResponse(responseCode = "401", description = "인증 필요"),
                    @ApiResponse(responseCode = "404", description = "관광지 없음")
            }
    )
    CommonResponse<SaveTourSpotResponse> saveTourSpot(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser user,
            @Parameter(
                    description = "저장할 관광지 ID",
                    required = true,
                    example = "1",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            String spotId
    );

    @Operation(
            summary = "관광지 상세 조회",
            description = "관광지 상세 정보를 조회합니다. JWT 쿠키가 있으면 사용자별 VISIT, QR_SCAN, STORY_CARD 완료 상태를 stampProgress로 반환하고, 비로그인 조회에서는 stampProgress가 null입니다.",
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
                                        },
                                        "stampProgress": {
                                          "clearedCount": 2,
                                          "totalCount": 3,
                                          "allCleared": false,
                                          "missions": [
                                            { "type": "VISIT", "cleared": true, "clearedAt": "2026-08-16T13:00:00" },
                                            { "type": "QR_SCAN", "cleared": true, "clearedAt": "2026-08-16T13:10:00" },
                                            { "type": "STORY_CARD", "cleared": false, "clearedAt": null }
                                          ]
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
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser user,
            @Parameter(
                    description = "조회할 관광지 ID",
                    required = true,
                    example = "1",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            String spotId
    );

    @Operation(
            summary = "스탬프 미션 완료",
            description = "인증 사용자의 관광지 미션을 완료 처리합니다. 동일 요청을 반복해도 기존 완료 상태를 반환하는 멱등 API입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "스탬프 미션 완료 성공",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                    {
                                      "code": 0,
                                      "message": "스탬프 미션을 완료했습니다.",
                                      "data": {
                                        "spotId": 1,
                                        "missionType": "QR_SCAN",
                                        "cleared": true,
                                        "clearedAt": "2026-08-16T13:10:00"
                                      }
                                    }
                                    """))),
                    @ApiResponse(responseCode = "400", description = "미션 완료 실패: 관광지 ID 또는 미션 유형 형식 오류"),
                    @ApiResponse(responseCode = "401", description = "미션 완료 실패: 인증되지 않은 사용자"),
                    @ApiResponse(responseCode = "404", description = "미션 완료 실패: 관광지를 찾을 수 없음")
            }
    )
    CommonResponse<StampMissionClearResponse> clearStampMission(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser user,
            @Parameter(description = "관광지 ID", example = "1") String spotId,
            @Parameter(description = "미션 유형", example = "QR_SCAN") StampMissionType missionType
    );

    @Operation(
            summary = "장소 통합 목록 조회",
            description = "관광지·식당·숙소를 category와 검색어로 조회합니다. category를 생략하면 전체 장소를 조회합니다.",
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
                                                      "message": "장소 목록 조회에 성공했습니다.",
                                                      "data": {
                                                        "places": [
                                                          {
                                                            "category": "TOUR_SPOT",
                                                            "spotId": 1,
                                                            "nearbyPlaceId": null,
                                                            "name": "관광지 이름 예시",
                                                            "img": "https://example.com/tour-spot.jpg",
                                                            "overview": "관광지 요약 예시",
                                                            "address": "관광지 주소 예시"
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
    CommonResponse<PlaceListResponse> getTourSpots(
            @Parameter(description = "장소 종류. 생략하면 전체 조회", example = "RESTAURANT")
            PlaceCategory category,
            @Parameter(description = "장소 이름, 주소, 요약 검색")
            String keyword,
            @Parameter(description = "페이지 번호", example = "0")
            @Min(value = 0, message = "page") int page,
            @Parameter(description = "페이지 크기(최대 100)", example = "20")
            @Min(value = 1, message = "size")
            @Max(value = 100, message = "size")
            int size
    );
}
