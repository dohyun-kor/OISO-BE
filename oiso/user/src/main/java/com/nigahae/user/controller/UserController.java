package com.nigahae.user.controller;

import com.nigahae.user.domain.User;
import com.nigahae.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "사용자", description = "사용자 조회 API")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "전체 사용자 조회", description = "등록된 전체 사용자 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "사용자 목록 조회 성공")
    public List<User> getUsers() {
        return userService.getUsers();
    }

}
