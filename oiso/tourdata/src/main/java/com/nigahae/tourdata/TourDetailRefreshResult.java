package com.nigahae.tourdata;

import java.util.Objects;

public record TourDetailRefreshResult(
        Status status,
        String failureMessage
) {

    public TourDetailRefreshResult {
        Objects.requireNonNull(status, "status is required");
    }

    public static TourDetailRefreshResult updated() {
        return new TourDetailRefreshResult(Status.UPDATED, null);
    }

    public static TourDetailRefreshResult notFound() {
        return new TourDetailRefreshResult(Status.NOT_FOUND, null);
    }

    public static TourDetailRefreshResult failed(String failureMessage) {
        return new TourDetailRefreshResult(Status.FAILED, failureMessage);
    }

    public boolean updatedSuccessfully() {
        return status == Status.UPDATED;
    }

    public boolean targetNotFound() {
        return status == Status.NOT_FOUND;
    }

    public boolean failed() {
        return status == Status.FAILED;
    }

    public enum Status {
        UPDATED,
        NOT_FOUND,
        FAILED
    }
}
