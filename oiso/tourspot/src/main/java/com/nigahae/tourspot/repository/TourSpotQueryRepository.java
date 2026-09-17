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

    @Query(value = """
            select saved.category as "category", saved.spot_id as "spotId",
                   saved.nearby_place_id as "nearbyPlaceId",
                   saved.parent_tour_spot_id as "parentTourSpotId",
                   saved.name as "name", saved.img as "img",
                   saved.overview as "overview", saved.address as "address",
                   saved.latitude as "latitude", saved.longitude as "longitude"
            from (
                select 'TOUR_SPOT' as category, spot.spot_id as spot_id,
                       cast(null as bigint) as nearby_place_id,
                       cast(null as bigint) as parent_tour_spot_id,
                       (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(spot.name_en), ''), spot.name) else spot.name end) as name, spot.img, (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(spot.overview_en), ''), spot.overview) else spot.overview end) as overview, (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(spot.address_en), ''), spot.address) else spot.address end) as address,
                       spot.map_y as latitude, spot.map_x as longitude,
                       favorite.saved_at
                from "UserSavedTourSpot" favorite
                join "TourSpot" spot on spot.spot_id = favorite.spot_id
                where favorite.user_id = :userId
                union all
                select case
                         when nearby.content_type_id = 39 or upper(nearby.category) in ('RESTAURANT', 'FOOD')
                           then 'RESTAURANT'
                         when nearby.content_type_id = 32 or upper(nearby.category) in ('ACCOMMODATION', 'LODGING')
                           then 'ACCOMMODATION'
                       end as category,
                       cast(null as bigint) as spot_id,
                       nearby.tour_spot_nearby_course_id as nearby_place_id,
                       nearby.spot_id as parent_tour_spot_id,
                       (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(nearby.name_en), ''), nearby.name) else nearby.name end) as name, nearby.img, (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(nearby.overview_en), ''), nearby.overview) else nearby.overview end) as overview, (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(nearby.address_en), ''), nearby.address) else nearby.address end) as address,
                       nearby.map_y as latitude, nearby.map_x as longitude,
                       favorite.saved_at
                from "UserSavedNearbyPlace" favorite
                join "TourSpotNearbyPlace" nearby
                  on nearby.tour_spot_nearby_course_id = favorite.tour_spot_nearby_course_id
                where favorite.user_id = :userId
            ) saved
            where (:category = '' or saved.category = :category)
            order by saved.saved_at desc,
                     coalesce(saved.spot_id, saved.nearby_place_id) desc
            """,
            countQuery = """
            select count(*)
            from (
                select 'TOUR_SPOT' as category
                from "UserSavedTourSpot" favorite
                where favorite.user_id = :userId
                union all
                select case
                         when nearby.content_type_id = 39 or upper(nearby.category) in ('RESTAURANT', 'FOOD')
                           then 'RESTAURANT'
                         when nearby.content_type_id = 32 or upper(nearby.category) in ('ACCOMMODATION', 'LODGING')
                           then 'ACCOMMODATION'
                       end as category
                from "UserSavedNearbyPlace" favorite
                join "TourSpotNearbyPlace" nearby
                  on nearby.tour_spot_nearby_course_id = favorite.tour_spot_nearby_course_id
                where favorite.user_id = :userId
            ) saved
            where (:category = '' or saved.category = :category)
            """, nativeQuery = true)
    Page<PlaceSummaryRow> findSavedPlaces(
            @Param("userId") Long userId,
            @Param("category") String category,
            Pageable pageable
    );

    @Query("""
            SELECT t FROM TourSpotReadModel t
            WHERE t.contentTypeId IS NULL OR t.contentTypeId IN (12, 14, 15, 25, 28)
            """)
    Page<TourSpot> findAllTourSpots(Pageable pageable);

    @Query("""
            SELECT t
            FROM TourSpotReadModel t
            WHERE (t.contentTypeId IS NULL OR t.contentTypeId IN (12, 14, 15, 25, 28))
              AND (LOWER((case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(t.nameEn), ''), t.name) else t.name end)) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(COALESCE((case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(t.addressEn), ''), t.address) else t.address end), '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(COALESCE((case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(t.overviewEn), ''), t.overview) else t.overview end), '')) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<TourSpot> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = """
            select nearby.tour_spot_nearby_course_id as "nearbyPlaceId",
                   nearby.spot_id as "spotId",
                   case
                     when nearby.content_type_id = 39 or upper(nearby.category) in ('RESTAURANT', 'FOOD') then 'RESTAURANT'
                     when nearby.content_type_id = 32 or upper(nearby.category) in ('ACCOMMODATION', 'LODGING') then 'ACCOMMODATION'
                   end as "category",
                   (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(nearby.name_en), ''), nearby.name) else nearby.name end) as "name", nearby.img as "img", (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(nearby.overview_en), ''), nearby.overview) else nearby.overview end) as "overview",
                   (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(nearby.mood_en), ''), nearby.mood) else nearby.mood end) as "mood", (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(nearby.address_en), ''), nearby.address) else nearby.address end) as "address",
                   nearby.homepage_url as "homepageUrl", nearby.content_id as "contentId",
                   nearby.content_type_id as "contentTypeId", nearby.map_x as "mapX", nearby.map_y as "mapY",
                   (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(detail.open_time_en), ''), detail.open_time) else detail.open_time end) as "openTime", (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(detail.rest_date_en), ''), detail.rest_date) else detail.rest_date end) as "restDate",
                   (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(detail.reservation_info_en), ''), detail.reservation_info) else detail.reservation_info end) as "reservationInfo", (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(detail.inquiry_info_en), ''), detail.inquiry_info) else detail.inquiry_info end) as "inquiryInfo",
                   (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(detail.parking_info_en), ''), detail.parking_info) else detail.parking_info end) as "parkingInfo", (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(detail.pet_info_en), ''), detail.pet_info) else detail.pet_info end) as "petInfo",
                   (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(detail.first_menu_en), ''), detail.first_menu) else detail.first_menu end) as "firstMenu", (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(detail.treat_menu_en), ''), detail.treat_menu) else detail.treat_menu end) as "treatMenu"
            from "TourSpotNearbyPlace" nearby
            left join "NearbyPlaceDetail" detail
              on detail.tour_spot_nearby_course_id = nearby.tour_spot_nearby_course_id
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
                   (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(nearby.name_en), ''), nearby.name) else nearby.name end) as "name", nearby.img as "img", (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(nearby.overview_en), ''), nearby.overview) else nearby.overview end) as "overview",
                   (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(nearby.mood_en), ''), nearby.mood) else nearby.mood end) as "mood", (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(nearby.address_en), ''), nearby.address) else nearby.address end) as "address",
                   nearby.homepage_url as "homepageUrl", nearby.content_id as "contentId",
                   nearby.content_type_id as "contentTypeId", nearby.map_x as "mapX", nearby.map_y as "mapY",
                   (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(detail.open_time_en), ''), detail.open_time) else detail.open_time end) as "openTime", (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(detail.rest_date_en), ''), detail.rest_date) else detail.rest_date end) as "restDate",
                   (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(detail.reservation_info_en), ''), detail.reservation_info) else detail.reservation_info end) as "reservationInfo", (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(detail.inquiry_info_en), ''), detail.inquiry_info) else detail.inquiry_info end) as "inquiryInfo",
                   (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(detail.parking_info_en), ''), detail.parking_info) else detail.parking_info end) as "parkingInfo", (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(detail.pet_info_en), ''), detail.pet_info) else detail.pet_info end) as "petInfo",
                   (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(detail.first_menu_en), ''), detail.first_menu) else detail.first_menu end) as "firstMenu", (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(detail.treat_menu_en), ''), detail.treat_menu) else detail.treat_menu end) as "treatMenu",
                   case when favorite.user_id is not null then true else false end as "like"
            from "TourSpotNearbyPlace" nearby
            left join "NearbyPlaceDetail" detail
              on detail.tour_spot_nearby_course_id = nearby.tour_spot_nearby_course_id
            left join "UserSavedNearbyPlace" favorite
              on favorite.tour_spot_nearby_course_id = nearby.tour_spot_nearby_course_id
             and (:userId is not null and favorite.user_id = :userId)
            where nearby.tour_spot_nearby_course_id = :nearbyPlaceId
              and upper(nearby.category) in ('RESTAURANT', 'ACCOMMODATION')
            """, nativeQuery = true)
    Optional<NearbyPlaceRow> findNearbyPlace(
            @Param("nearbyPlaceId") Long nearbyPlaceId,
            @Param("userId") Long userId
    );

    @Query(value = """
            select places.category as "category", places.spot_id as "spotId",
                   places.nearby_place_id as "nearbyPlaceId",
                   places.parent_tour_spot_id as "parentTourSpotId", places.name as "name",
                   places.img as "img", places.overview as "overview", places.address as "address",
                   places.latitude as "latitude", places.longitude as "longitude"
            from (
                select 'TOUR_SPOT' as category, spot.spot_id as spot_id,
                       cast(null as bigint) as nearby_place_id,
                       cast(null as bigint) as parent_tour_spot_id, (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(spot.name_en), ''), spot.name) else spot.name end) as name,
                       spot.img as img, (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(spot.overview_en), ''), spot.overview) else spot.overview end) as overview, (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(spot.address_en), ''), spot.address) else spot.address end) as address,
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
                       nearby.spot_id as parent_tour_spot_id,
                       (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(nearby.name_en), ''), nearby.name) else nearby.name end) as name, nearby.img as img,
                       (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(nearby.overview_en), ''), nearby.overview) else nearby.overview end) as overview, (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(nearby.address_en), ''), nearby.address) else nearby.address end) as address,
                       nearby.map_y as latitude, nearby.map_x as longitude
                from (
                    select distinct on (source.content_id) source.*
                    from "TourSpotNearbyPlace" source
                    where source.content_type_id in (32, 39)
                       or upper(source.category) in ('RESTAURANT', 'FOOD', 'ACCOMMODATION', 'LODGING')
                    order by source.content_id, source.tour_spot_nearby_course_id
                ) nearby
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
                select 'TOUR_SPOT' as category, (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(spot.name_en), ''), spot.name) else spot.name end) as name, (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(spot.address_en), ''), spot.address) else spot.address end) as address, (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(spot.overview_en), ''), spot.overview) else spot.overview end) as overview
                from "TourSpot" spot
                where spot.content_type_id is null or spot.content_type_id in (12, 14, 15, 25, 28)
                union all
                select case
                         when nearby.content_type_id = 39 or upper(nearby.category) in ('RESTAURANT', 'FOOD') then 'RESTAURANT'
                         when nearby.content_type_id = 32 or upper(nearby.category) in ('ACCOMMODATION', 'LODGING') then 'ACCOMMODATION'
                       end as category,
                       (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(nearby.name_en), ''), nearby.name) else nearby.name end) as name, (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(nearby.address_en), ''), nearby.address) else nearby.address end) as address, (case when :#{T(com.nigahae.common.i18n.ApiLanguage).isEnglish()} then coalesce(nullif(trim(nearby.overview_en), ''), nearby.overview) else nearby.overview end) as overview
                from (
                    select distinct on (source.content_id) source.*
                    from "TourSpotNearbyPlace" source
                    where source.content_type_id in (32, 39)
                       or upper(source.category) in ('RESTAURANT', 'FOOD', 'ACCOMMODATION', 'LODGING')
                    order by source.content_id, source.tour_spot_nearby_course_id
                ) nearby
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
