package com.nigahae.tourcourse.controller;

import com.nigahae.common.response.CommonResponse;
import com.nigahae.common.constant.ErrorCode;
import com.nigahae.common.exception.CustomException;
import com.nigahae.tourcourse.controller.docs.TourCourseControllerDocs;
import com.nigahae.tourcourse.dto.CreateTourCourseRequest;
import com.nigahae.tourcourse.dto.CreateTourCourseResponse;
import com.nigahae.tourcourse.dto.TourCourseListResponse;
import com.nigahae.tourcourse.dto.TourCourseDetailResponse;
import com.nigahae.tourcourse.dto.UpdateTourCourseRequest;
import com.nigahae.tourcourse.dto.PatchTourCourseItemRequest;
import com.nigahae.tourcourse.dto.PatchTourCourseStatusRequest;
import com.nigahae.tourcourse.service.TourCourseService;
import com.nigahae.user.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tour-courses")
public class TourCourseController implements TourCourseControllerDocs {

    private static final int SUCCESS_CODE = 2001;
    private static final String SUCCESS_MESSAGE = "관광 코스 생성에 성공했습니다.";
    private static final int LIST_SUCCESS_CODE = 2000;
    private static final String LIST_SUCCESS_MESSAGE = "관광 코스 목록 조회에 성공했습니다.";

    private static final String DETAIL_SUCCESS_MESSAGE = "관광 코스 상세 조회에 성공했습니다.";
    private static final String UPDATE_SUCCESS_MESSAGE = "관광 코스 수정에 성공했습니다.";
    private static final String DELETE_SUCCESS_MESSAGE = "관광 코스 삭제에 성공했습니다.";

    private final TourCourseService tourCourseService;

    @PostMapping
    @Override
    public CommonResponse<CreateTourCourseResponse> createTourCourse(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestBody(required = false) CreateTourCourseRequest request
    ) {
        if (user == null) {
            throw new CustomException(ErrorCode.AUTH_ERROR);
        }
        return new CommonResponse<>(
                SUCCESS_CODE,
                SUCCESS_MESSAGE,
                tourCourseService.create(user.userId(), request)
        );
    }

    @GetMapping
    @Override
    public CommonResponse<TourCourseListResponse> getMyTourCourses(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @RequestParam(required = false) String keyword
    ) {
        if (user == null) {
            throw new CustomException(ErrorCode.AUTH_ERROR);
        }
        return new CommonResponse<>(
                LIST_SUCCESS_CODE,
                LIST_SUCCESS_MESSAGE,
                tourCourseService.getMyTourCourses(user.userId(), page, size, sort, keyword)
        );
    }

    @GetMapping("/{tourCourseId}")
    @Override
    public CommonResponse<TourCourseDetailResponse> getTourCourseDetail(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long tourCourseId
    ) {
        if (user == null) {
            throw new CustomException(ErrorCode.AUTH_ERROR);
        }
        return new CommonResponse<>(
                LIST_SUCCESS_CODE,
                DETAIL_SUCCESS_MESSAGE,
                tourCourseService.getTourCourseDetail(user.userId(), tourCourseId)
        );
    }

    @PutMapping("/{tourCourseId}")
    @Override
    public CommonResponse<TourCourseDetailResponse> updateTourCourse(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long tourCourseId,
            @RequestBody(required = false) UpdateTourCourseRequest request
    ) {
        if (user == null) {
            throw new CustomException(ErrorCode.AUTH_ERROR);
        }
        return new CommonResponse<>(
                LIST_SUCCESS_CODE,
                UPDATE_SUCCESS_MESSAGE,
                tourCourseService.updateTourCourse(user.userId(), tourCourseId, request)
        );
    }

    @PatchMapping("/{tourCourseId}/items/{itemId}")
    @Override
    public CommonResponse<TourCourseDetailResponse> patchTourCourseItem(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long tourCourseId,
            @PathVariable Long itemId,
            @RequestBody(required = false) PatchTourCourseItemRequest request
    ) {
        if (user == null) {
            throw new CustomException(ErrorCode.AUTH_ERROR);
        }
        return new CommonResponse<>(
                LIST_SUCCESS_CODE,
                UPDATE_SUCCESS_MESSAGE,
                tourCourseService.patchTourCourseItem(user.userId(), tourCourseId, itemId, request)
        );
    }

    @PatchMapping("/{tourCourseId}/status")
    @Override
    public CommonResponse<TourCourseDetailResponse> patchTourCourseStatus(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long tourCourseId,
            @RequestBody(required = false) PatchTourCourseStatusRequest request
    ) {
        if (user == null) {
            throw new CustomException(ErrorCode.AUTH_ERROR);
        }
        return new CommonResponse<>(
                LIST_SUCCESS_CODE,
                UPDATE_SUCCESS_MESSAGE,
                tourCourseService.patchTourCourseStatus(user.userId(), tourCourseId, request)
        );
    }

    @DeleteMapping("/{tourCourseId}")
    @Override
    public CommonResponse<Void> deleteTourCourse(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long tourCourseId
    ) {
        if (user == null) {
            throw new CustomException(ErrorCode.AUTH_ERROR);
        }
        tourCourseService.deleteTourCourse(user.userId(), tourCourseId);
        return new CommonResponse<>(LIST_SUCCESS_CODE, DELETE_SUCCESS_MESSAGE, null);
    }
}
