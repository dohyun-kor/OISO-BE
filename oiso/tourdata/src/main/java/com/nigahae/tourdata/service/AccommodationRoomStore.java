package com.nigahae.tourdata.service;

import com.nigahae.tourdata.domain.AccommodationRoom;
import com.nigahae.tourdata.repository.AccommodationRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccommodationRoomStore {

    private final AccommodationRoomRepository accommodationRoomRepository;

    @Transactional
    public void replace(Long spotId, List<AccommodationRoom> accommodationRooms) {
        accommodationRoomRepository.deleteBySpotId(spotId);
        if (!accommodationRooms.isEmpty()) {
            accommodationRoomRepository.saveAll(accommodationRooms);
        }
    }
}
