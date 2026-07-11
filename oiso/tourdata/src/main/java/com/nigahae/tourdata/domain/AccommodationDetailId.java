package com.nigahae.tourdata.domain;

import java.io.Serializable;
import java.util.Objects;

public class AccommodationDetailId implements Serializable {

    private Long tempRoomId;
    private Long spotId;

    protected AccommodationDetailId() {
    }

    public AccommodationDetailId(Long tempRoomId, Long spotId) {
        this.tempRoomId = tempRoomId;
        this.spotId = spotId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof AccommodationDetailId that)) {
            return false;
        }
        return Objects.equals(tempRoomId, that.tempRoomId)
                && Objects.equals(spotId, that.spotId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tempRoomId, spotId);
    }
}
