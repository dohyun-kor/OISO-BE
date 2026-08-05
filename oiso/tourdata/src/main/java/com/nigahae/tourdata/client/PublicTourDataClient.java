package com.nigahae.tourdata.client;

import java.time.LocalDate;
import java.util.List;

public interface PublicTourDataClient {

    List<TourContentPayload> fetchAllTourContents();

    List<FestivalPayload> fetchAllFestivals(LocalDate eventStartDate);

    TourContentPayload fetchTourContentDetail(Long contentId);

    List<AccommodationRoomPayload> fetchAccommodationRooms(Long contentId, Long contentTypeId);
}
