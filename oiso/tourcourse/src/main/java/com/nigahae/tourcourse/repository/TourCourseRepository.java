package com.nigahae.tourcourse.repository;

import com.nigahae.tourcourse.domain.TourCourse;
import com.nigahae.tourcourse.dto.TourCourseListRow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TourCourseRepository extends JpaRepository<TourCourse, Long> {

    @Query(
            value = """
                    select new com.nigahae.tourcourse.dto.TourCourseListRow(
                        c.tourCourseId,
                        c.title,
                        c.shareYn,
                        max(case when item.itemOrder = 1 then spot.img else null end),
                        count(item.itemId),
                        c.totalDistanceMeter,
                        c.totalDurationSecond,
                        c.createdAt,
                        c.updatedAt
                    )
                    from TourCourse c
                    left join c.items item
                    left join TourCourseSpotReference spot on spot.spotId = item.spotId
                    where c.userId = :userId
                      and (:keyword = '' or lower(c.title) like lower(concat('%', :keyword, '%')))
                    group by c.tourCourseId, c.title, c.shareYn,
                             c.totalDistanceMeter, c.totalDurationSecond, c.createdAt, c.updatedAt
                    """,
            countQuery = """
                    select count(c.tourCourseId)
                    from TourCourse c
                    where c.userId = :userId
                      and (:keyword = '' or lower(c.title) like lower(concat('%', :keyword, '%')))
                    """
    )
    Page<TourCourseListRow> findMyTourCourses(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
