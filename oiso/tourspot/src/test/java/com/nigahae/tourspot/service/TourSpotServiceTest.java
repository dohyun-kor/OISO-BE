package com.nigahae.tourspot.service;

import com.nigahae.common.constant.ErrorCode;
import com.nigahae.common.exception.CustomException;
import com.nigahae.tourdata.TourDetailRefresher;
import com.nigahae.tourdata.TourDetailRefreshResult;
import com.nigahae.tourspot.domain.TourSpot;
import com.nigahae.tourspot.dto.TourSpotDetailResponse;
import com.nigahae.tourspot.dto.TourSpotListResponse;
import com.nigahae.tourspot.dto.NearbyPlaceRow;
import com.nigahae.tourspot.domain.NearbyPlaceCategory;
import com.nigahae.tourspot.exception.TourSpotNotFoundException;
import com.nigahae.tourspot.repository.TourSpotQueryRepository;
import com.nigahae.tourspot.repository.UserSavedTourSpotRepository;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class TourSpotServiceTest {

    @Mock
    private TourSpotQueryRepository tourSpotQueryRepository;
    @Mock
    private UserSavedTourSpotRepository userSavedTourSpotRepository;
    @Mock
    private TourDetailRefresher tourDetailRefresher;

    private TourSpotService tourSpotService;

    @BeforeEach
    void setUp() {
        tourSpotService = new TourSpotService(
                tourSpotQueryRepository,
                userSavedTourSpotRepository,
                tourDetailRefresher
        );
    }

    @Test
    @DisplayName("관광지를 사용자 저장 목록에 추가한다")
    void saveTourSpot() {
        when(tourSpotQueryRepository.existsById(1L)).thenReturn(true);

        var response = tourSpotService.saveTourSpot(7L, 1L);

        assertThat(response.spotId()).isEqualTo(1L);
        assertThat(response.like()).isTrue();
        verify(userSavedTourSpotRepository).saveIfAbsent(7L, 1L);
    }

    @Test
    @DisplayName("존재하지 않는 관광지는 저장하지 않는다")
    void doesNotSaveMissingTourSpot() {
        when(tourSpotQueryRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> tourSpotService.saveTourSpot(7L, 999L))
                .isInstanceOf(TourSpotNotFoundException.class);

        verify(userSavedTourSpotRepository, org.mockito.Mockito.never())
                .saveIfAbsent(7L, 999L);
    }

    @Test
    @DisplayName("관광지를 사용자 저장 목록에서 삭제한다")
    void deleteSavedTourSpot() {
        when(tourSpotQueryRepository.existsById(1L)).thenReturn(true);

        var response = tourSpotService.deleteSavedTourSpot(7L, 1L);

        assertThat(response.spotId()).isEqualTo(1L);
        assertThat(response.like()).isFalse();
        verify(userSavedTourSpotRepository).delete(7L, 1L);
    }

    @Test
    @DisplayName("존재하지 않는 관광지는 저장 목록에서 삭제하지 않는다")
    void doesNotDeleteMissingTourSpot() {
        when(tourSpotQueryRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> tourSpotService.deleteSavedTourSpot(7L, 999L))
                .isInstanceOf(TourSpotNotFoundException.class);

        verify(userSavedTourSpotRepository, org.mockito.Mockito.never())
                .delete(7L, 999L);
    }

    @Test
    @DisplayName("관광지 ID로 상세 정보를 조회한다")
    void getTourSpot() {
        TourSpot tourSpot = tourSpot(1L, "불국사");
        when(tourDetailRefresher.refresh(1L)).thenReturn(TourDetailRefreshResult.updated());
        when(tourSpotQueryRepository.findById(1L)).thenReturn(Optional.of(tourSpot));

        TourSpotDetailResponse response = tourSpotService.getTourSpot(1L);

        assertThat(response.tourSpot().spotId()).isEqualTo(1L);
        assertThat(response.tourSpot().name()).isEqualTo("불국사");
        assertThat(response.tourSpot().like()).isFalse();
    }

    @Test
    void getTourSpotRefreshesOnEveryRequest() {
        TourSpot tourSpot = tourSpot(1L, "Repeated place");
        when(tourDetailRefresher.refresh(1L)).thenReturn(TourDetailRefreshResult.updated());
        when(tourSpotQueryRepository.findById(1L)).thenReturn(Optional.of(tourSpot));

        tourSpotService.getTourSpot(1L);
        tourSpotService.getTourSpot(1L);

        verify(tourDetailRefresher, times(2)).refresh(1L);
        verify(tourSpotQueryRepository, times(2)).findById(1L);
    }

    @Test
    void getTourSpotReturnsStoredDataWhenRefreshFails() {
        TourSpot tourSpot = tourSpot(1L, "Stored place");
        when(tourDetailRefresher.refresh(1L))
                .thenReturn(TourDetailRefreshResult.failed("Read timed out"));
        when(tourSpotQueryRepository.findById(1L)).thenReturn(Optional.of(tourSpot));

        TourSpotDetailResponse response = tourSpotService.getTourSpot(1L);

        assertThat(response.tourSpot().name()).isEqualTo("Stored place");
    }

    @Test
    @DisplayName("관광지가 존재하지 않으면 상세 조회 예외를 던진다")
    void throwExceptionWhenTourSpotDoesNotExist() {
        when(tourDetailRefresher.refresh(999L)).thenReturn(TourDetailRefreshResult.notFound());

        assertThatThrownBy(() -> tourSpotService.getTourSpot(999L))
                .isInstanceOf(TourSpotNotFoundException.class);
    }

    @Test
    @DisplayName("키워드가 없으면 전체 관광지를 페이지 단위로 조회한다")
    void getTourSpotsWithoutKeyword() {
        TourSpot tourSpot = tourSpot(1L, "불국사");
        when(tourSpotQueryRepository.findAll(any(Pageable.class)))
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
        when(tourSpotQueryRepository.searchByKeyword(eq("불국사"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(tourSpot), PageRequest.of(0, 20), 1));

        tourSpotService.getTourSpots("  불국사  ", 0, 20);

        verify(tourSpotQueryRepository).searchByKeyword(eq("불국사"), any(Pageable.class));
    }

    @Test
    @DisplayName("조회된 관광지가 없으면 검색 결과 없음 예외를 던진다")
    void throwExceptionWhenResultIsEmpty() {
        when(tourSpotQueryRepository.findAll(any(Pageable.class)))
                .thenReturn(Page.empty());

        assertThatThrownBy(() -> tourSpotService.getTourSpots(null, 0, 20))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
    }

    @Test
    void getsRestaurantNearbyPlaces() {
        NearbyPlaceRow row = mock(NearbyPlaceRow.class);
        when(row.getNearbyPlaceId()).thenReturn(101L);
        when(row.getSpotId()).thenReturn(1L);
        when(row.getCategory()).thenReturn("RESTAURANT");
        when(row.getName()).thenReturn("경주 식당");
        when(tourSpotQueryRepository.existsById(1L)).thenReturn(true);
        when(tourSpotQueryRepository.findNearbyPlaces(
                eq(1L), eq("RESTAURANT"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(row), PageRequest.of(0, 20), 1));

        var response = tourSpotService.getNearbyPlaces(
                1L, NearbyPlaceCategory.RESTAURANT, 0, 20);

        assertThat(response.nearbyPlaces()).hasSize(1);
        assertThat(response.nearbyPlaces().get(0).nearbyPlaceId()).isEqualTo(101L);
        assertThat(response.nearbyPlaces().get(0).category())
                .isEqualTo(NearbyPlaceCategory.RESTAURANT);
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
