package com.nigahae.tourspot.service;

import com.nigahae.common.exception.CustomCommonException;
import com.nigahae.common.exception.ErrorCode;
import com.nigahae.tourspot.domain.TourSpot;
import com.nigahae.tourspot.dto.TourSpotListResponse;
import com.nigahae.tourspot.repository.TourSpotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TourSpotServiceTest {

    @Mock
    private TourSpotRepository tourSpotRepository;

    private TourSpotService tourSpotService;

    @BeforeEach
    void setUp() {
        tourSpotService = new TourSpotService(tourSpotRepository);
    }

    @Test
    @DisplayName("키워드가 없으면 전체 관광지를 페이지 단위로 조회한다")
    void getTourSpotsWithoutKeyword() {
        TourSpot tourSpot = tourSpot(1L, "불국사");
        when(tourSpotRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(tourSpot), PageRequest.of(0, 20), 1));

        TourSpotListResponse response = tourSpotService.getTourSpots(null, 0, 20);

        assertThat(response.tourSpots()).hasSize(1);
        assertThat(response.tourSpots().get(0).name()).isEqualTo("불국사");
        assertThat(response.tourSpots().get(0).like()).isFalse();
        assertThat(response.page()).isZero();
        assertThat(response.size()).isEqualTo(20);
        assertThat(response.totalCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("앞뒤 공백을 제거한 키워드로 관광지를 검색한다")
    void searchTourSpotsByNormalizedKeyword() {
        TourSpot tourSpot = tourSpot(1L, "불국사");
        when(tourSpotRepository.searchByKeyword(eq("불국사"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(tourSpot), PageRequest.of(0, 20), 1));

        tourSpotService.getTourSpots("  불국사  ", 0, 20);

        verify(tourSpotRepository).searchByKeyword(eq("불국사"), any(Pageable.class));
    }

    @Test
    @DisplayName("조회된 관광지가 없으면 검색 결과 없음 예외를 던진다")
    void throwExceptionWhenResultIsEmpty() {
        when(tourSpotRepository.findAll(any(Pageable.class)))
                .thenReturn(Page.empty());

        assertThatThrownBy(() -> tourSpotService.getTourSpots(null, 0, 20))
                .isInstanceOf(CustomCommonException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.SEARCH_RESULT_NOT_FOUND);
    }

    private TourSpot tourSpot(Long id, String name) {
        return TourSpot.builder()
                .spotId(id)
                .name(name)
                .overview("대표 문화유산")
                .address("경상북도 경주시")
                .build();
    }
}
