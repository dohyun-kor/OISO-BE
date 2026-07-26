package com.nigahae.user.controller;

import com.nigahae.common.constant.ErrorCode;
import com.nigahae.common.exception.CustomException;
import com.nigahae.common.response.CommonResponse;
import com.nigahae.user.AuthenticatedUser;
import com.nigahae.user.controller.dto.UserPreferencesRequest;
import com.nigahae.user.controller.dto.UserPreferencesResponse;
import com.nigahae.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/me/preferences")
public class UserPreferencesController {

    private final UserService userService;

    @PatchMapping
    public CommonResponse<UserPreferencesResponse> updatePreferences(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @RequestBody UserPreferencesRequest request
    ) {
        if (authenticatedUser == null) {
            throw new CustomException(ErrorCode.AUTH_ERROR);
        }

        return CommonResponse.success(
                userService.updatePreferences(authenticatedUser.userId(), request)
        );
    }
}
