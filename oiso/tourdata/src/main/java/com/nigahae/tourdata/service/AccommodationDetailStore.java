package com.nigahae.tourdata.service;

import com.nigahae.tourdata.domain.AccommodationDetail;
import com.nigahae.tourdata.repository.AccommodationDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccommodationDetailStore {

    private final AccommodationDetailRepository accommodationDetailRepository;

    @Transactional
    public void replace(Long spotId, List<AccommodationDetail> accommodationDetails) {
        accommodationDetailRepository.deleteBySpotId(spotId);
        if (!accommodationDetails.isEmpty()) {
            accommodationDetailRepository.saveAll(accommodationDetails);
        }
    }
}
