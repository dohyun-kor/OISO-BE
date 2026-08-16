package com.nigahae.user.controller;

import com.nigahae.common.constant.ErrorCode;
import com.nigahae.common.exception.CustomException;
import com.nigahae.common.response.CommonResponse;
import com.nigahae.user.AuthenticatedUser;
import com.nigahae.user.controller.dto.MyPageResponse;
import com.nigahae.user.service.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/me/mypage")
@Tag(name = "마이페이지", description = "인증 사용자의 마이페이지 조회 API")
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping
    @Operation(
            summary = "마이페이지 요약 조회",
            description = "프로필과 저장 장소·스토리카드·메모의 개수 및 전체 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "마이페이지 조회 성공"),
            @ApiResponse(responseCode = "401", description = "마이페이지 조회 실패: 인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "마이페이지 조회 실패: 사용자를 찾을 수 없음")
    })
    public CommonResponse<MyPageResponse> getMyPage(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        if (authenticatedUser == null) {
            throw new CustomException(ErrorCode.AUTH_ERROR);
        }

        return CommonResponse.success(
                myPageService.getMyPage(authenticatedUser.userId())
        );
    }
}
