package com.nigahae.tourcourse.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TourCourseStatusTest {

    @Test
    void recalculatesStatusFromItemStatuses() {
        TourCourse course = new TourCourse(1L, "경주 코스", false, null);
        course.addItem(1L, 1, null);
        course.addItem(2L, 2, TransportType.WALK);

        course.recalculateStatus();
        assertThat(course.getStatus()).isEqualTo(TourCourseStatus.PENDING);

        course.getItems().get(0).updateStatus(TourCourseItemStatus.COMPLETED);
        course.recalculateStatus();
        assertThat(course.getStatus()).isEqualTo(TourCourseStatus.IN_PROGRESS);

        course.getItems().get(1).updateStatus(TourCourseItemStatus.COMPLETED);
        course.recalculateStatus();
        assertThat(course.getStatus()).isEqualTo(TourCourseStatus.COMPLETED);

        course.getItems().get(0).updateStatus(TourCourseItemStatus.IN_PROGRESS);
        course.recalculateStatus();
        assertThat(course.getStatus()).isEqualTo(TourCourseStatus.IN_PROGRESS);
    }
}
