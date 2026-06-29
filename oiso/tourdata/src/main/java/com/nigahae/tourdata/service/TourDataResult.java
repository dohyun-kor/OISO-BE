package com.nigahae.tourdata.service;

public record TourDataResult(boolean success, boolean skipped, int processedCount, String failureMessage) {

    public static TourDataResult success(int processedCount) {
        return new TourDataResult(true, false, processedCount, null);
    }

    public static TourDataResult failure(String failureMessage) {
        return new TourDataResult(false, false, 0, failureMessage);
    }

    public static TourDataResult skipped(String failureMessage) {
        return new TourDataResult(false, true, 0, failureMessage);
    }
}
