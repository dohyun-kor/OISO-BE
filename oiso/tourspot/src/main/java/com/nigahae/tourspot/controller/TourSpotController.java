package com.nigahae.tourspot.controller;

import com.nigahae.common.response.CommonResponse;
import com.nigahae.tourspot.dto.TourSpotListResponse;
import com.nigahae.tourspot.service.TourSpotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "관광지", description = "관광지 조회 API")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/tour-spots")
public class TourSpotController {

    private static final String SUCCESS_MESSAGE = "관광지 목록 조회에 성공했습니다.";

    private final TourSpotService tourSpotService;

    @Operation(
            summary = "관광지 목록 조회",
            description = "관광지 이름, 주소, 요약으로 검색하고 페이지 단위로 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "잘못된 페이지 요청",
                            content = @Content(examples = @ExampleObject(value = """
                                    {
                                      "code": 1001,
                                      "message": "요청 값이 올바르지 않습니다.",
                                      "data": null
                                    }
                                    """))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "검색 결과 없음",
                            content = @Content(examples = @ExampleObject(value = """
                                    {
                                      "code": 3001,
                                      "message": "요청한 리소스를 찾을 수 없습니다.",
                                      "data": null
                                    }
                                    """))
                    )
            }
    )
    @GetMapping
    public CommonResponse<TourSpotListResponse> getTourSpots(
            @Parameter(description = "관광지 이름, 주소, 요약 검색")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "page") int page,
            @Parameter(description = "페이지 크기(최대 100)", example = "20")
            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "size")
            @Max(value = 100, message = "size")
            int size
    ) {
        return new CommonResponse<>(
                CommonResponse.SUCCESS_CODE,
                SUCCESS_MESSAGE,
                tourSpotService.getTourSpots(keyword, page, size)
        );
    }
}
