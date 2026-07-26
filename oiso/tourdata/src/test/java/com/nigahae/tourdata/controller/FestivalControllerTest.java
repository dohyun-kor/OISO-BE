package com.nigahae.tourdata.controller;

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
}
