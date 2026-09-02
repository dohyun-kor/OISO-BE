package com.nigahae.tourspot.repository;

import com.nigahae.tourspot.dto.TourSpotMemoResponse;

import java.util.Optional;

public interface TourSpotMemoRepository {
    TourSpotMemoResponse.Memo save(Long userId, Long spotId, String content);

    Optional<TourSpotMemoResponse.Memo> find(Long userId, Long spotId);

    boolean delete(Long userId, Long spotId);
}
