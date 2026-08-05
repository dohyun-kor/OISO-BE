package com.nigahae.tourspot.exception;

public class TourSpotNotFoundException extends RuntimeException {

    public TourSpotNotFoundException(Long spotId) {
        super("관광지를 찾을 수 없습니다. spotId=" + spotId);
    }
}
