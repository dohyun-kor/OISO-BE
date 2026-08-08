package com.nigahae.tourspot.repository;

public interface UserSavedTourSpotRepository {

    void saveIfAbsent(Long userId, Long spotId);
}
