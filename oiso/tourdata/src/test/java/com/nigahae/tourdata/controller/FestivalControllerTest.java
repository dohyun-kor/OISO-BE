package com.nigahae.tourdata.controller;

import com.nigahae.common.constant.ErrorCode;
import com.nigahae.common.exception.CustomException;
import com.nigahae.common.exception.GlobalExceptionHandler;
import com.nigahae.tourdata.controller.dto.FestivalDetailResponse;
import com.nigahae.tourdata.controller.dto.FestivalResponse;
import com.nigahae.tourdata.controller.dto.FestivalStatus;
import com.nigahae.tourdata.service.FestivalQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FestivalControllerTest {

    private final FestivalQueryService festivalQueryService = mock(FestivalQueryService.class);
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new FestivalController(festivalQueryService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void returnsFestivalListInCommonResponse() throws Exception {
        FestivalResponse response = new FestivalResponse(
                300L,
                "2026 봉황대 뮤직 스퀘어",
                "https://example.com/festival.jpg",
                LocalDate.of(2026, 6, 5),
                LocalDate.of(2026, 8, 28),
                FestivalStatus.ONGOING
        );
        when(festivalQueryService.getAvailableFestivals()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/event"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.data[0].spotId").value(300))
                .andExpect(jsonPath("$.data[0].name").value("2026 봉황대 뮤직 스퀘어"))
                .andExpect(jsonPath("$.data[0].img").value("https://example.com/festival.jpg"))
                .andExpect(jsonPath("$.data[0].eventStartDate").value("2026-06-05"))
                .andExpect(jsonPath("$.data[0].eventEndDate").value("2026-08-28"))
                .andExpect(jsonPath("$.data[0].status").value("ONGOING"));

        verify(festivalQueryService).getAvailableFestivals();
    }

    @Test
    void returnsEmptyDataArrayWhenNoFestivalIsAvailable() throws Exception {
        when(festivalQueryService.getAvailableFestivals()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/event"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void returnsFestivalDetailBySpotId() throws Exception {
        FestivalDetailResponse response = new FestivalDetailResponse(
                300L,
                "2026 Festival",
                "https://example.com/festival.jpg",
                "Gyeongju detail address",
                "Festival overview",
                "https://festival.example.com",
                129.2123,
                35.8451,
                300L,
                15L,
                "054-000-0000",
                LocalDate.of(2026, 6, 5),
                LocalDate.of(2026, 8, 28)
        );
        when(festivalQueryService.getFestivalDetail(300L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/event/300"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.spotId").value(300))
                .andExpect(jsonPath("$.data.name").value("2026 Festival"))
                .andExpect(jsonPath("$.data.img").value("https://example.com/festival.jpg"))
                .andExpect(jsonPath("$.data.address").value("Gyeongju detail address"))
                .andExpect(jsonPath("$.data.overview").value("Festival overview"))
                .andExpect(jsonPath("$.data.homepage").value("https://festival.example.com"))
                .andExpect(jsonPath("$.data.mapX").value(129.2123))
                .andExpect(jsonPath("$.data.mapY").value(35.8451))
                .andExpect(jsonPath("$.data.contentId").value(300))
                .andExpect(jsonPath("$.data.contentTypeId").value(15))
                .andExpect(jsonPath("$.data.tel").value("054-000-0000"))
                .andExpect(jsonPath("$.data.eventStartDate").value("2026-06-05"))
                .andExpect(jsonPath("$.data.eventEndDate").value("2026-08-28"));

        verify(festivalQueryService).getFestivalDetail(300L);
    }

    @Test
    void returnsNotFoundWhenFestivalDoesNotExist() throws Exception {
        when(festivalQueryService.getFestivalDetail(999L))
                .thenThrow(new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        mockMvc.perform(get("/api/v1/event/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.RESOURCE_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}
