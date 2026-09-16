package com.nigahae.tourcourse.repository;

import com.nigahae.tourcourse.domain.TourCourse;
import com.nigahae.tourcourse.dto.TourCourseItemDetailRow;
import com.nigahae.tourcourse.dto.TourCourseListRow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TourCourseRepository extends JpaRepository<TourCourse, Long> {

    @Query("""
            select new com.nigahae.tourcourse.dto.TourCourseListRow(
                c.tourCourseId, c.title, c.shareYn, c.status,
                max(case when item.dayNumber = 1 and item.itemOrder = 1
                         then coalesce(spot.img, nearby.img) else null end),
                count(item.itemId), c.createdAt, c.updatedAt)
            from TourCourse c
            left join c.items item
            left join TourCourseSpotReference spot on spot.spotId = item.spotId
            left join TourCourseNearbyPlaceReference nearby on nearby.nearbyPlaceId = item.nearbyPlaceId
            where c.featuredYn = true
            group by c.tourCourseId, c.title, c.shareYn, c.status, c.createdAt, c.updatedAt
            order by function('random')
            """)
    List<TourCourseListRow> findRandomFeaturedCourses(org.springframework.data.domain.Pageable pageable);

    @Query(
            value = """
                    select new com.nigahae.tourcourse.dto.TourCourseListRow(
                        c.tourCourseId,
                        c.title,
                        c.shareYn,
                        c.status,
                        max(case when item.dayNumber = 1 and item.itemOrder = 1
                                 then coalesce(spot.img, nearby.img) else null end),
                        count(item.itemId),
                        c.createdAt,
                        c.updatedAt
                    )
                    from TourCourse c
                    left join c.items item
                    left join TourCourseSpotReference spot on spot.spotId = item.spotId
                    left join TourCourseNearbyPlaceReference nearby
                        on nearby.nearbyPlaceId = item.nearbyPlaceId
                    where c.userId = :userId
                      and (:keyword = '' or lower(c.title) like lower(concat('%', :keyword, '%')))
                    group by c.tourCourseId, c.title, c.shareYn, c.status, c.createdAt, c.updatedAt
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

    @Query(value = """
            select
                item.item_id as "itemId",
                item.day_number as "dayNumber",
                item.item_order as "itemOrder",
                case
                    when item.spot_id is not null then 'TOUR_SPOT'
                    when nearby.content_type_id = 39 or upper(nearby.category) in ('RESTAURANT', 'FOOD')
                        then 'RESTAURANT'
                    when nearby.content_type_id = 32 or upper(nearby.category) in ('ACCOMMODATION', 'LODGING')
                        then 'ACCOMMODATION'
                    else upper(nearby.category)
                end as "category",
                item.spot_id as "spotId",
                item.tour_spot_nearby_course_id as "nearbyPlaceId",
                nearby.spot_id as "parentTourSpotId",
                coalesce(spot.name, nearby.name) as "name",
                coalesce(spot.img, nearby.img) as "img",
                coalesce(spot.overview, nearby.overview) as "overview",
                coalesce(spot.address, nearby.address) as "address",
                coalesce(spot.map_y, nearby.map_y) as "latitude",
                coalesce(spot.map_x, nearby.map_x) as "longitude",
                case
                    when item.spot_id is null then false
                    else exists (
                        select 1
                        from "UserSavedTourSpot" saved
                        where saved.user_id = :userId
                          and saved.spot_id = item.spot_id
                    )
                end as "liked",
                item.transport_type as "transportType",
                item.status as "status"
            from "TourCourseItem" item
            left join "TourSpot" spot
                on spot.spot_id = item.spot_id
            left join "TourSpotNearbyPlace" nearby
                on nearby.tour_spot_nearby_course_id = item.tour_spot_nearby_course_id
            where item.tour_course_id = :tourCourseId
            order by item.day_number asc, item.item_order asc
            """, nativeQuery = true)
    List<TourCourseItemDetailRow> findDetailItems(
            @Param("tourCourseId") Long tourCourseId,
            @Param("userId") Long userId
    );

}
