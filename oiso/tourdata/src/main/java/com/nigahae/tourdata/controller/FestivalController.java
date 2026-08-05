package com.nigahae.tourdata.controller;

import com.nigahae.common.response.CommonResponse;
import com.nigahae.tourdata.controller.dto.FestivalDetailResponse;
import com.nigahae.tourdata.controller.dto.FestivalResponse;
import com.nigahae.tourdata.service.FestivalQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/event")
@Tag(name = "Festival", description = "축제 조회 API")
public class FestivalController {

    private final FestivalQueryService festivalQueryService;

    @GetMapping
    @Operation(
            summary = "전체 축제 목록 조회",
            description = """
                    Asia/Seoul 기준으로 진행 중이거나 진행 예정인 축제를 조회합니다.
                    진행 중 축제를 먼저 반환하고, 각 그룹에서는 시작일과 축제 식별자 오름차순으로 정렬합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "축제 목록 조회 성공")
    })
    public CommonResponse<List<FestivalResponse>> getFestivals() {
        return CommonResponse.success(festivalQueryService.getAvailableFestivals());
    }

    @GetMapping("/{spotId}")
    @Operation(
            summary = "단일 축제 상세 조회",
            description = "경로 변수로 전달한 spotId에 해당하는 축제 상세 정보를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "축제 상세 조회 성공"),
            @ApiResponse(responseCode = "400", description = "spotId 형식 오류"),
            @ApiResponse(responseCode = "404", description = "해당 spotId의 축제가 존재하지 않음")
    })
    public CommonResponse<FestivalDetailResponse> getFestivalDetail(
            @Parameter(description = "축제 식별자", example = "300", required = true)
            @PathVariable Long spotId
    ) {
        return CommonResponse.success(festivalQueryService.getFestivalDetail(spotId));
    }
}
