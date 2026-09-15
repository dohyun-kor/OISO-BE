package com.nigahae.tourspot.controller;

import com.nigahae.common.constant.ErrorCode;
import com.nigahae.common.exception.CustomException;
import com.nigahae.common.response.CommonResponse;
import com.nigahae.tourspot.dto.SavedStoryCardListResponse;
import com.nigahae.tourspot.dto.SavedStoryCardResponse;
import com.nigahae.tourspot.dto.StoryCardDetailResponse;
import com.nigahae.tourspot.dto.StoryCardSummaryResponse;
import java.util.List;
import com.nigahae.tourspot.service.StoryCardService;
import com.nigahae.user.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/story-cards")
@Tag(name = "스토리카드", description = "스토리카드 저장 및 내 저장 목록 API")
public class StoryCardController {

    private final StoryCardService storyCardService;

    @GetMapping("/featured")
    @Operation(summary = "홈 화면 스토리카드 랜덤 조회",
            description = "로그인 없이 전체 스토리카드 중 중복 없이 랜덤 조회합니다. 기본 5개, 최대 20개이며 데이터가 부족하면 존재하는 개수만 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "랜덤 스토리카드 조회 성공"),
            @ApiResponse(responseCode = "400", description = "size는 1~20 사이여야 합니다.")
    })
    public CommonResponse<List<StoryCardSummaryResponse>> getFeatured(
            @RequestParam(defaultValue = "5") @Min(1) @Max(20) int size
    ) {
        return new CommonResponse<>(CommonResponse.SUCCESS_CODE, "추천 스토리카드 조회에 성공했습니다.",
                storyCardService.getRandomCards(size));
    }

    @GetMapping("/tour-spots/{spotId}")
    @Operation(
            summary = "스토리카드 상세 조회",
            description = "스토리카드 본문과 관광지, 해시태그, 현재 사용자의 저장 여부를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "스토리카드 상세 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 관광지 ID"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "해당 관광지의 스토리카드를 찾을 수 없음")
    })
    public CommonResponse<StoryCardDetailResponse> getDetail(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Parameter(description = "관광지 ID", example = "24") @PathVariable Long spotId
    ) {
        validateAuthenticatedUser(user);
        return new CommonResponse<>(CommonResponse.SUCCESS_CODE,
                "스토리카드 상세 조회에 성공했습니다.",
                storyCardService.getDetailBySpotId(user.userId(), spotId));
    }

    @PostMapping("/{storyId}/save")
    @Operation(summary = "스토리카드 저장", description = "스토리카드를 내 저장 목록에 추가합니다. 중복 요청도 성공합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "스토리카드 저장 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 스토리카드 ID"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "스토리카드를 찾을 수 없음")
    })
    public CommonResponse<SavedStoryCardResponse> save(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Parameter(description = "스토리카드 ID", example = "1") @PathVariable Long storyId
    ) {
        validateAuthenticatedUser(user);
        return new CommonResponse<>(CommonResponse.SUCCESS_CODE, "스토리카드가 저장되었습니다.",
                storyCardService.save(user.userId(), storyId));
    }

    @DeleteMapping("/{storyId}/save")
    @Operation(summary = "스토리카드 저장 취소", description = "스토리카드를 내 저장 목록에서 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "스토리카드 저장 취소 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 스토리카드 ID"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "스토리카드를 찾을 수 없음")
    })
    public CommonResponse<SavedStoryCardResponse> delete(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Parameter(description = "스토리카드 ID", example = "1") @PathVariable Long storyId
    ) {
        validateAuthenticatedUser(user);
        return new CommonResponse<>(CommonResponse.SUCCESS_CODE, "스토리카드 저장이 취소되었습니다.",
                storyCardService.delete(user.userId(), storyId));
    }

    @GetMapping("/saved")
    @Operation(summary = "내가 저장한 스토리카드 목록 조회", description = "내가 저장한 스토리카드를 최근 저장 순으로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "저장한 스토리카드 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 페이지 값"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    public CommonResponse<SavedStoryCardListResponse> getSaved(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        validateAuthenticatedUser(user);
        return new CommonResponse<>(CommonResponse.SUCCESS_CODE,
                "저장한 스토리카드 목록 조회에 성공했습니다.",
                storyCardService.getSaved(user.userId(), page, size));
    }

    private void validateAuthenticatedUser(AuthenticatedUser user) {
        if (user == null) {
            throw new CustomException(ErrorCode.AUTH_ERROR);
        }
    }
}
