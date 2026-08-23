package com.nigahae.location.controller.docs;

import com.nigahae.common.response.CommonResponse;
import com.nigahae.location.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@Tag(name="장소·이동", description="장소 간 이동정보, 주소·좌표 변환, 지도 링크 API")
public interface LocationControllerDocs {
    String INVALID_COORDINATE = "{\"code\":1001,\"message\":\"위도(latitude, Y)는 90 이하여야 합니다. 경도와 순서를 확인해 주세요.\",\"data\":null}";
    String INVALID_REQUEST = "{\"code\":1001,\"message\":\"요청 값이 올바르지 않습니다.\",\"data\":null}";
    String PROVIDER_ERROR = "{\"code\":5020,\"message\":\"Kakao Map API 호출에 실패했습니다.\",\"data\":null}";

    @Operation(summary="장소 간 이동 거리·소요 시간 계산",
      description="좌표로 지정한 두 장소 사이의 이동 거리와 예상 소요 시간을 계산합니다. "
       + "longitude는 X(경도, -180~180), latitude는 Y(위도, -90~90)입니다. "
       + "이동수단은 WALK, BICYCLE, PUBLIC_TRANSIT, CAR를 지원합니다. CAR는 추천 자동차 경로를 계산합니다.")
    @ApiResponses({
      @ApiResponse(responseCode="200", description="계산 성공",
       content=@Content(schema=@Schema(implementation=RouteCalculationResponse.class))),
      @ApiResponse(responseCode="400", description="좌표 범위 오류, 필수값 누락 또는 지원하지 않는 이동수단",
       content=@Content(mediaType="application/json", examples={
        @ExampleObject(name="위도·경도 순서 오류", value=INVALID_COORDINATE),
        @ExampleObject(name="잘못된 이동수단", value=INVALID_REQUEST)})),
      @ApiResponse(responseCode="502", description="Kakao 경로 API 설정 오류·통신 실패·경로 없음",
       content=@Content(mediaType="application/json", examples=@ExampleObject(value=PROVIDER_ERROR)))
    })
    CommonResponse<RouteCalculationResponse> calculateRoute(@Valid RouteCalculationRequest request);

    @Operation(summary="주소로 좌표 검색", description="지번 또는 도로명 주소를 검색해 경도(X)와 위도(Y) 후보 목록을 반환합니다. 검색 결과가 없으면 200과 빈 목록을 반환합니다.")
    @ApiResponses({
      @ApiResponse(responseCode="200", description="검색 성공"),
      @ApiResponse(responseCode="400", description="주소 누락, page(1~45) 또는 size(1~30) 범위 오류",
       content=@Content(mediaType="application/json", examples=@ExampleObject(
        value="{\"code\":1001,\"message\":\"검색할 주소를 입력해 주세요.\",\"data\":null}"))),
      @ApiResponse(responseCode="502", description="Kakao 주소 검색 API 설정 또는 통신 오류",
       content=@Content(mediaType="application/json", examples=@ExampleObject(value=PROVIDER_ERROR)))
    })
    CommonResponse<GeocodingResponse> geocode(
      @NotBlank(message="검색할 주소를 입력해 주세요.") String address,
      @Min(value=1, message="page는 1 이상이어야 합니다.") @Max(value=45, message="page는 45 이하여야 합니다.") int page,
      @Min(value=1, message="size는 1 이상이어야 합니다.") @Max(value=30, message="size는 30 이하여야 합니다.") int size);

    @Operation(summary="좌표로 주소 조회", description="WGS84 좌표의 주소를 반환합니다. longitude는 X(경도), latitude는 Y(위도)입니다.")
    @ApiResponses({
      @ApiResponse(responseCode="200", description="조회 성공"),
      @ApiResponse(responseCode="400", description="경도(-180~180) 또는 위도(-90~90) 범위 오류",
       content=@Content(mediaType="application/json", examples=@ExampleObject(value=INVALID_COORDINATE))),
      @ApiResponse(responseCode="502", description="Kakao API 설정·통신 오류 또는 주소 없음",
       content=@Content(mediaType="application/json", examples=@ExampleObject(value=PROVIDER_ERROR)))
    })
    CommonResponse<ReverseGeocodingResponse> reverseGeocode(
      @DecimalMin(value="-180", message="경도(longitude, X)는 -180 이상이어야 합니다.")
      @DecimalMax(value="180", message="경도(longitude, X)는 180 이하여야 합니다.") double longitude,
      @DecimalMin(value="-90", message="위도(latitude, Y)는 -90 이상이어야 합니다. 경도와 순서를 확인해 주세요.")
      @DecimalMax(value="90", message="위도(latitude, Y)는 90 이하여야 합니다. 경도와 순서를 확인해 주세요.") double latitude);

    @Operation(summary="좌표로 행정구역 조회", description="WGS84 좌표에 해당하는 행정동(H)과 법정동(B)을 반환합니다. longitude는 X(경도), latitude는 Y(위도)입니다.")
    @ApiResponses({
      @ApiResponse(responseCode="200", description="조회 성공"),
      @ApiResponse(responseCode="400", description="경도(-180~180) 또는 위도(-90~90) 범위 오류",
       content=@Content(mediaType="application/json", examples=@ExampleObject(value=INVALID_COORDINATE))),
      @ApiResponse(responseCode="502", description="Kakao 행정구역 API 설정 또는 통신 오류",
       content=@Content(mediaType="application/json", examples=@ExampleObject(value=PROVIDER_ERROR)))
    })
    CommonResponse<RegionListResponse> regions(
      @DecimalMin(value="-180", message="경도(longitude, X)는 -180 이상이어야 합니다.")
      @DecimalMax(value="180", message="경도(longitude, X)는 180 이하여야 합니다.") double longitude,
      @DecimalMin(value="-90", message="위도(latitude, Y)는 -90 이상이어야 합니다. 경도와 순서를 확인해 주세요.")
      @DecimalMax(value="90", message="위도(latitude, Y)는 90 이하여야 합니다. 경도와 순서를 확인해 주세요.") double latitude);

    @Operation(summary="카카오맵 링크 생성", description="목적지 지도 표시 URL과 목적지 길찾기 URL을 생성합니다. 외부 API를 호출하지 않습니다.")
    @ApiResponses({
      @ApiResponse(responseCode="200", description="링크 생성 성공"),
      @ApiResponse(responseCode="400", description="목적지 누락 또는 좌표 범위 오류",
       content=@Content(mediaType="application/json", examples=@ExampleObject(value=INVALID_COORDINATE)))
    })
    CommonResponse<MapLinkResponse> mapLinks(@Valid MapLinkRequest request);
}
