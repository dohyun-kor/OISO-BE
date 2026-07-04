package com.nigahae.tourdata.client;

import java.util.List;

public interface PublicTourDataClient {

    List<TourContentPayload> fetchAllTourContents();

    TourContentPayload fetchTourContentDetail(Long contentId);

    List<AccommodationRoomPayload> fetchAccommodationRooms(Long contentId, Long contentTypeId);
}
