package com.nigahae.tourdata.client;

import java.util.List;

public interface PublicTourDataClient {

    List<TourContentPayload> fetchAllTourContents();

    List<AccommodationRoomPayload> fetchAccommodationRooms(Long contentId, Long contentTypeId);
}
