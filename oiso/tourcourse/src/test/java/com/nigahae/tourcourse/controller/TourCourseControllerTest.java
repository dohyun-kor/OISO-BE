package com.nigahae.tourcourse.controller;

import com.nigahae.tourcourse.dto.CreateTourCourseRequest;
import com.nigahae.tourcourse.dto.CreateTourCourseResponse;
import com.nigahae.tourcourse.dto.TourCourseListResponse;
import com.nigahae.tourcourse.dto.TourCourseSummaryResponse;
import com.nigahae.tourcourse.dto.TourCourseDetailResponse;
import com.nigahae.tourcourse.dto.UpdateTourCourseRequest;
import com.nigahae.tourcourse.service.TourCourseService;
import com.nigahae.user.AuthenticatedUser;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

class TourCourseControllerTest {

    private final TourCourseService service = mock(TourCourseService.class);
    private final TourCourseController controller = new TourCourseController(service);

    @Test
    void returnsSpecifiedSuccessResponse() {
        CreateTourCourseRequest request = new CreateTourCourseRequest("코스", false, null, List.of());
        CreateTourCourseResponse data = new CreateTourCourseResponse(
                1L, "코스", false, 1, LocalDateTime.of(2026, 7, 11, 15, 0)
        );
        when(service.create(7L, request)).thenReturn(data);

        var response = controller.createTourCourse(new AuthenticatedUser(7L, "사용자"), request);

        assertThat(response.code()).isEqualTo(2001);
        assertThat(response.message()).isEqualTo("관광 코스 생성에 성공했습니다.");
        assertThat(response.data()).isEqualTo(data);
    }

    @Test
    void returnsSpecifiedListSuccessResponse() {
        LocalDateTime now = LocalDateTime.of(2026, 7, 11, 15, 0);
        TourCourseListResponse data = new TourCourseListResponse(
                List.of(new TourCourseSummaryResponse(
                        1L, "코스", false, "image.jpg", 3, 1000, 600, now, now
                )),
                0,
                10,
                1
        );
        when(service.getMyTourCourses(7L, 0, 10, "createdAt,desc", null)).thenReturn(data);

        var response = controller.getMyTourCourses(
                new AuthenticatedUser(7L, "사용자"), 0, 10, "createdAt,desc", null
        );

        assertThat(response.code()).isEqualTo(2000);
        assertThat(response.message()).isEqualTo("관광 코스 목록 조회에 성공했습니다.");
        assertThat(response.data()).isEqualTo(data);
    }

    @Test
    void returnsSpecifiedDetailSuccessResponse() {
        LocalDateTime now = LocalDateTime.of(2026, 7, 11, 15, 0);
        TourCourseDetailResponse data = new TourCourseDetailResponse(
                1L, "코스", false, List.of(), 0, 0, now, now
        );
        when(service.getTourCourseDetail(7L, 1L)).thenReturn(data);

        var response = controller.getTourCourseDetail(
                new AuthenticatedUser(7L, "사용자"), 1L
        );

        assertThat(response.code()).isEqualTo(2000);
        assertThat(response.message()).isEqualTo("관광 코스 상세 조회에 성공했습니다.");
        assertThat(response.data()).isEqualTo(data);
    }

    @Test
    void returnsSpecifiedUpdateSuccessResponse() {
        LocalDateTime now = LocalDateTime.of(2026, 7, 12, 17, 30);
        UpdateTourCourseRequest request = new UpdateTourCourseRequest(
                "수정 코스", false, null, List.of()
        );
        TourCourseDetailResponse data = new TourCourseDetailResponse(
                1L, "수정 코스", false, List.of(), 0, 0, now, now
        );
        when(service.updateTourCourse(7L, 1L, request)).thenReturn(data);

        var response = controller.updateTourCourse(
                new AuthenticatedUser(7L, "사용자"), 1L, request
        );

        assertThat(response.code()).isEqualTo(2000);
        assertThat(response.message()).isEqualTo("관광 코스 수정에 성공했습니다.");
        assertThat(response.data()).isEqualTo(data);
    }

    @Test
    void returnsSpecifiedDeleteSuccessResponse() {
        var response = controller.deleteTourCourse(
                new AuthenticatedUser(7L, "사용자"), 1L);

        assertThat(response.code()).isEqualTo(2000);
        assertThat(response.message()).isEqualTo("관광 코스 삭제에 성공했습니다.");
        assertThat(response.data()).isNull();
        verify(service).deleteTourCourse(7L, 1L);
    }
}
