package com.nigahae.tourdata.scheduler;

import com.nigahae.tourdata.domain.TourDataJobType;
import com.nigahae.tourdata.service.TourDataResult;
import com.nigahae.tourdata.service.TourDataRunner;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TourIngestSchedulerTest {

    @Test
    void schedulesDelegateToIndependentJobs() {
        TourDataRunner runner = mock(TourDataRunner.class);
        for (TourDataJobType jobType : new TourDataJobType[]{
                TourDataJobType.TOUR_SPOTS,
                TourDataJobType.FESTIVALS,
                TourDataJobType.COMMON_DETAILS,
                TourDataJobType.ACCOMMODATIONS
        }) {
            when(runner.run(jobType, "schedule")).thenReturn(TourDataResult.success(1));
        }
        TourDataScheduler scheduler = new TourDataScheduler(runner);

        scheduler.refreshTourSpots();
        scheduler.refreshFestivals();
        scheduler.refreshCommonDetails();
        scheduler.refreshAccommodations();

        verify(runner).run(TourDataJobType.TOUR_SPOTS, "schedule");
        verify(runner).run(TourDataJobType.FESTIVALS, "schedule");
        verify(runner).run(TourDataJobType.COMMON_DETAILS, "schedule");
        verify(runner).run(TourDataJobType.ACCOMMODATIONS, "schedule");
    }
}
