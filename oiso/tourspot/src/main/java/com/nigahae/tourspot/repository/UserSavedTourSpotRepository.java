package com.nigahae.tourspot.repository;

public interface UserSavedTourSpotRepository {

    void saveIfAbsent(Long userId, Long spotId);

    void delete(Long userId, Long spotId);

    boolean exists(Long userId, Long spotId);

    void saveNearbyPlaceIfAbsent(Long userId, Long nearbyPlaceId);

    void deleteNearbyPlace(Long userId, Long nearbyPlaceId);
}
