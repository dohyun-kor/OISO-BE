package com.nigahae.tourdata.service;

public record TourDataResult(
        boolean success,
        boolean skipped,
        int processedCount,
        int failedCount,
        String failureMessage
) {

    public static TourDataResult success(int processedCount) {
        return new TourDataResult(true, false, processedCount, 0, null);
    }

    public static TourDataResult partial(int processedCount, int failedCount, String failureMessage) {
        return new TourDataResult(false, false, processedCount, failedCount, failureMessage);
    }

    public static TourDataResult failure(int failedCount, String failureMessage) {
        return new TourDataResult(false, false, 0, failedCount, failureMessage);
    }

    public static TourDataResult skipped(String failureMessage) {
        return new TourDataResult(false, true, 0, 0, failureMessage);
    }

    public boolean partial() {
        return !success && !skipped && processedCount > 0 && failedCount > 0;
    }
}
