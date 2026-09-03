package com.nigahae.tourspot.controller;

import com.nigahae.common.constant.ErrorCode;
import com.nigahae.common.exception.CustomException;
import com.nigahae.common.response.CommonResponse;
import com.nigahae.tourspot.dto.TourSpotMemoRequest;
import com.nigahae.tourspot.dto.TourSpotMemoResponse;
import com.nigahae.tourspot.dto.TourSpotMemoListResponse;
import com.nigahae.tourspot.exception.InvalidTourSpotIdException;
import com.nigahae.tourspot.service.TourSpotService;
import com.nigahae.user.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/memos")
@Tag(name = "메모", description = "로그인한 사용자만 볼 수 있는 관광지별 개인 메모 API")
public class MemoController {

    private final TourSpotService tourSpotService;

    @GetMapping
    @Operation(summary = "내 메모 목록 조회", description = "현재 로그인한 사용자가 작성한 관광지 메모를 최근 수정 순으로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메모 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 페이지 값"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    public CommonResponse<TourSpotMemoListResponse> getMemos(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        validateAuthenticatedUser(user);
        return new CommonResponse<>(CommonResponse.SUCCESS_CODE, "내 메모 목록 조회에 성공했습니다.",
                tourSpotService.getMemos(user.userId(), page, size));
    }

    @PutMapping("/tour-spots/{spotId}")
    @Operation(summary = "관광지 메모 저장", description = "내 관광지 메모를 새로 작성하거나 기존 메모를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메모 저장 성공"),
            @ApiResponse(responseCode = "400", description = "관광지 ID 또는 메모 내용 오류"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "관광지를 찾을 수 없음")
    })
    public CommonResponse<TourSpotMemoResponse> saveMemo(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable String spotId,
            @Valid @RequestBody TourSpotMemoRequest request
    ) {
        validateAuthenticatedUser(user);
        return new CommonResponse<>(CommonResponse.SUCCESS_CODE, "관광지 메모가 저장되었습니다.",
                tourSpotService.saveMemo(
                        user.userId(), parseSpotId(spotId), request.content(), request.imageUrl()));
    }

    @GetMapping("/tour-spots/{spotId}")
    @Operation(summary = "관광지 메모 조회", description = "현재 로그인한 사용자가 작성한 관광지 메모만 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메모 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 관광지 ID"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "관광지를 찾을 수 없음")
    })
    public CommonResponse<TourSpotMemoResponse> getMemo(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable String spotId
    ) {
        validateAuthenticatedUser(user);
        return new CommonResponse<>(CommonResponse.SUCCESS_CODE, "관광지 메모 조회에 성공했습니다.",
                tourSpotService.getMemo(user.userId(), parseSpotId(spotId)));
    }

    @DeleteMapping("/tour-spots/{spotId}")
    @Operation(summary = "관광지 메모 삭제", description = "현재 로그인한 사용자가 작성한 관광지 메모만 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메모 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 관광지 ID"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "관광지를 찾을 수 없음")
    })
    public CommonResponse<TourSpotMemoResponse> deleteMemo(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable String spotId
    ) {
        validateAuthenticatedUser(user);
        return new CommonResponse<>(CommonResponse.SUCCESS_CODE, "관광지 메모가 삭제되었습니다.",
                tourSpotService.deleteMemo(user.userId(), parseSpotId(spotId)));
    }

    private void validateAuthenticatedUser(AuthenticatedUser user) {
        if (user == null) {
            throw new CustomException(ErrorCode.AUTH_ERROR);
        }
    }

    private Long parseSpotId(String spotId) {
        try {
            long parsedSpotId = Long.parseLong(spotId);
            if (parsedSpotId <= 0) {
                throw new InvalidTourSpotIdException();
            }
            return parsedSpotId;
        } catch (NumberFormatException exception) {
            throw new InvalidTourSpotIdException();
        }
    }
}
