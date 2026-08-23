package com.nigahae.tourspot.repository;

import com.nigahae.tourspot.domain.TourSpot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nigahae.tourspot.dto.NearbyPlaceRow;
import com.nigahae.tourspot.dto.PlaceSummaryRow;
import java.util.Optional;

public interface TourSpotQueryRepository extends JpaRepository<TourSpot, Long> {

    @Query("""
            SELECT t FROM TourSpotReadModel t
            WHERE t.contentTypeId IS NULL OR t.contentTypeId IN (12, 14, 15, 25, 28)
            """)
    Page<TourSpot> findAllTourSpots(Pageable pageable);

    @Query("""
            SELECT t
            FROM TourSpotReadModel t
            WHERE (t.contentTypeId IS NULL OR t.contentTypeId IN (12, 14, 15, 25, 28))
              AND (LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(COALESCE(t.address, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(COALESCE(t.overview, '')) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<TourSpot> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = """
            select nearby.tour_spot_nearby_course_id as "nearbyPlaceId",
                   nearby.spot_id as "spotId",
                   case
                     when nearby.content_type_id = 39 or upper(nearby.category) in ('RESTAURANT', 'FOOD') then 'RESTAURANT'
                     when nearby.content_type_id = 32 or upper(nearby.category) in ('ACCOMMODATION', 'LODGING') then 'ACCOMMODATION'
                   end as "category",
                   nearby.name as "name", nearby.mood as "mood", nearby.address as "address",
                   nearby.homepage_url as "homepageUrl", nearby.content_id as "contentId",
                   nearby.content_type_id as "contentTypeId", nearby.map_x as "mapX", nearby.map_y as "mapY",
                   detail.open_time as "openTime", detail.rest_date as "restDate",
                   detail.reservation_info as "reservationInfo", detail.inquiry_info as "inquiryInfo",
                   detail.parking_info as "parkingInfo", detail.pet_info as "petInfo",
                   detail.first_menu as "firstMenu", detail.treat_menu as "treatMenu"
            from "TourSpotNearbyPlace" nearby
            left join "NearbyPlaceDetail" detail
              on detail.tour_spot_nearby_course_id = nearby.tour_spot_nearby_course_id
             and detail.spot_id = nearby.spot_id
            where nearby.spot_id = :spotId
              and (nearby.content_type_id in (32, 39)
                   or upper(nearby.category) in ('RESTAURANT', 'FOOD', 'ACCOMMODATION', 'LODGING'))
              and (:category = '' or
                   case
                     when upper(nearby.category) = 'RESTAURANT' then 'RESTAURANT'
                     when upper(nearby.category) = 'ACCOMMODATION' then 'ACCOMMODATION'
                   end = :category)
            """,
            countQuery = """
            select count(*) from "TourSpotNearbyPlace" nearby
            where nearby.spot_id = :spotId
              and (nearby.content_type_id in (32, 39)
                   or upper(nearby.category) in ('RESTAURANT', 'FOOD', 'ACCOMMODATION', 'LODGING'))
              and (:category = '' or
                   case
                     when nearby.content_type_id = 39 or upper(nearby.category) in ('RESTAURANT', 'FOOD') then 'RESTAURANT'
                     when nearby.content_type_id = 32 or upper(nearby.category) in ('ACCOMMODATION', 'LODGING') then 'ACCOMMODATION'
                   end = :category)
            """, nativeQuery = true)
    Page<NearbyPlaceRow> findNearbyPlaces(
            @Param("spotId") Long spotId,
            @Param("category") String category,
            Pageable pageable
    );

    @Query(value = """
            select nearby.tour_spot_nearby_course_id as "nearbyPlaceId",
                   nearby.spot_id as "spotId",
                   case
                     when nearby.content_type_id = 39 or upper(nearby.category) in ('RESTAURANT', 'FOOD') then 'RESTAURANT'
                     when nearby.content_type_id = 32 or upper(nearby.category) in ('ACCOMMODATION', 'LODGING') then 'ACCOMMODATION'
                   end as "category",
                   nearby.name as "name", nearby.mood as "mood", nearby.address as "address",
                   nearby.homepage_url as "homepageUrl", nearby.content_id as "contentId",
                   nearby.content_type_id as "contentTypeId", nearby.map_x as "mapX", nearby.map_y as "mapY",
                   detail.open_time as "openTime", detail.rest_date as "restDate",
                   detail.reservation_info as "reservationInfo", detail.inquiry_info as "inquiryInfo",
                   detail.parking_info as "parkingInfo", detail.pet_info as "petInfo",
                   detail.first_menu as "firstMenu", detail.treat_menu as "treatMenu"
            from "TourSpotNearbyPlace" nearby
            left join "NearbyPlaceDetail" detail
              on detail.tour_spot_nearby_course_id = nearby.tour_spot_nearby_course_id
             and detail.spot_id = nearby.spot_id
            where nearby.tour_spot_nearby_course_id = :nearbyPlaceId
              and upper(nearby.category) in ('RESTAURANT', 'ACCOMMODATION')
            """, nativeQuery = true)
    Optional<NearbyPlaceRow> findNearbyPlace(
            @Param("nearbyPlaceId") Long nearbyPlaceId
    );

    @Query(value = """
            select places.category as "category", places.spot_id as "spotId",
                   places.nearby_place_id as "nearbyPlaceId", places.name as "name",
                   places.img as "img", places.overview as "overview", places.address as "address",
                   places.latitude as "latitude", places.longitude as "longitude"
            from (
                select 'TOUR_SPOT' as category, spot.spot_id as spot_id,
                       cast(null as bigint) as nearby_place_id, spot.name as name,
                       spot.img as img, spot.overview as overview, spot.address as address,
                       spot.map_y as latitude, spot.map_x as longitude
                from "TourSpot" spot
                where spot.content_type_id is null or spot.content_type_id in (12, 14, 15, 25, 28)
                union all
                select case
                         when nearby.content_type_id = 39 or upper(nearby.category) in ('RESTAURANT', 'FOOD') then 'RESTAURANT'
                         when nearby.content_type_id = 32 or upper(nearby.category) in ('ACCOMMODATION', 'LODGING') then 'ACCOMMODATION'
                       end as category,
                       cast(null as bigint) as spot_id,
                       nearby.tour_spot_nearby_course_id as nearby_place_id,
                       nearby.name as name, cast(null as varchar) as img,
                       cast(null as varchar) as overview, nearby.address as address,
                       nearby.map_y as latitude, nearby.map_x as longitude
                from "TourSpotNearbyPlace" nearby
                where nearby.content_type_id in (32, 39)
                   or upper(nearby.category) in ('RESTAURANT', 'FOOD', 'ACCOMMODATION', 'LODGING')
            ) places
            where (:category = '' or places.category = :category)
              and (:keyword = '' or lower(places.name) like lower(concat('%', :keyword, '%'))
                   or lower(coalesce(places.address, '')) like lower(concat('%', :keyword, '%'))
                   or lower(coalesce(places.overview, '')) like lower(concat('%', :keyword, '%')))
            order by places.category, places.name,
                     coalesce(places.spot_id, places.nearby_place_id)
            """,
            countQuery = """
            select count(*)
            from (
                select 'TOUR_SPOT' as category, spot.name, spot.address, spot.overview
                from "TourSpot" spot
                where spot.content_type_id is null or spot.content_type_id in (12, 14, 15, 25, 28)
                union all
                select case
                         when nearby.content_type_id = 39 or upper(nearby.category) in ('RESTAURANT', 'FOOD') then 'RESTAURANT'
                         when nearby.content_type_id = 32 or upper(nearby.category) in ('ACCOMMODATION', 'LODGING') then 'ACCOMMODATION'
                       end as category,
                       nearby.name, nearby.address, cast(null as varchar) as overview
                from "TourSpotNearbyPlace" nearby
                where nearby.content_type_id in (32, 39)
                   or upper(nearby.category) in ('RESTAURANT', 'FOOD', 'ACCOMMODATION', 'LODGING')
            ) places
            where (:category = '' or places.category = :category)
              and (:keyword = '' or lower(places.name) like lower(concat('%', :keyword, '%'))
                   or lower(coalesce(places.address, '')) like lower(concat('%', :keyword, '%'))
                   or lower(coalesce(places.overview, '')) like lower(concat('%', :keyword, '%')))
            """, nativeQuery = true)
    Page<PlaceSummaryRow> findPlaces(
            @Param("category") String category,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
