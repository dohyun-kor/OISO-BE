package com.nigahae.tourspot.repository;

import com.nigahae.tourspot.dto.TourSpotMemoResponse;
import com.nigahae.tourspot.dto.TourSpotMemoListResponse;

import java.util.Optional;

public interface TourSpotMemoRepository {
    TourSpotMemoResponse.Memo save(Long userId, Long spotId, String content, String imageUrl);

    Optional<TourSpotMemoResponse.Memo> find(Long userId, Long spotId);

    boolean delete(Long userId, Long spotId);

    TourSpotMemoResponse.Memo saveNearby(Long userId, Long nearbyPlaceId, String content, String imageUrl);
    Optional<TourSpotMemoResponse.Memo> findNearby(Long userId, Long nearbyPlaceId);
    boolean deleteNearby(Long userId, Long nearbyPlaceId);

    TourSpotMemoListResponse findAll(Long userId, int page, int size);
}
