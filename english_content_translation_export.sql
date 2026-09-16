-- Run after the migration. Read-only; excludes user-authored content.
SELECT 'TourSpot' AS table_name, t.spot_id AS row_id, 'name' AS field_name,
       t.name AS source_ko, t.name_en AS translation_en
FROM public."TourSpot" t
WHERE NULLIF(btrim(t.name), '') IS NOT NULL
  AND NULLIF(btrim(t.name_en), '') IS NULL
UNION ALL
SELECT 'TourSpot' AS table_name, t.spot_id AS row_id, 'content' AS field_name,
       t.content AS source_ko, t.content_en AS translation_en
FROM public."TourSpot" t
WHERE NULLIF(btrim(t.content), '') IS NOT NULL
  AND NULLIF(btrim(t.content_en), '') IS NULL
UNION ALL
SELECT 'TourSpot' AS table_name, t.spot_id AS row_id, 'address' AS field_name,
       t.address AS source_ko, t.address_en AS translation_en
FROM public."TourSpot" t
WHERE NULLIF(btrim(t.address), '') IS NOT NULL
  AND NULLIF(btrim(t.address_en), '') IS NULL
UNION ALL
SELECT 'TourSpot' AS table_name, t.spot_id AS row_id, 'overview' AS field_name,
       t.overview AS source_ko, t.overview_en AS translation_en
FROM public."TourSpot" t
WHERE NULLIF(btrim(t.overview), '') IS NOT NULL
  AND NULLIF(btrim(t.overview_en), '') IS NULL
UNION ALL
SELECT 'TourSpotNearbyPlace' AS table_name, t.tour_spot_nearby_course_id AS row_id, 'name' AS field_name,
       t.name AS source_ko, t.name_en AS translation_en
FROM public."TourSpotNearbyPlace" t
WHERE NULLIF(btrim(t.name), '') IS NOT NULL
  AND NULLIF(btrim(t.name_en), '') IS NULL
UNION ALL
SELECT 'TourSpotNearbyPlace' AS table_name, t.tour_spot_nearby_course_id AS row_id, 'overview' AS field_name,
       t.overview AS source_ko, t.overview_en AS translation_en
FROM public."TourSpotNearbyPlace" t
WHERE NULLIF(btrim(t.overview), '') IS NOT NULL
  AND NULLIF(btrim(t.overview_en), '') IS NULL
UNION ALL
SELECT 'TourSpotNearbyPlace' AS table_name, t.tour_spot_nearby_course_id AS row_id, 'mood' AS field_name,
       t.mood AS source_ko, t.mood_en AS translation_en
FROM public."TourSpotNearbyPlace" t
WHERE NULLIF(btrim(t.mood), '') IS NOT NULL
  AND NULLIF(btrim(t.mood_en), '') IS NULL
UNION ALL
SELECT 'TourSpotNearbyPlace' AS table_name, t.tour_spot_nearby_course_id AS row_id, 'address' AS field_name,
       t.address AS source_ko, t.address_en AS translation_en
FROM public."TourSpotNearbyPlace" t
WHERE NULLIF(btrim(t.address), '') IS NOT NULL
  AND NULLIF(btrim(t.address_en), '') IS NULL
UNION ALL
SELECT 'TourStory' AS table_name, t.story_id AS row_id, 'title' AS field_name,
       t.title AS source_ko, t.title_en AS translation_en
FROM public."TourStory" t
WHERE NULLIF(btrim(t.title), '') IS NOT NULL
  AND NULLIF(btrim(t.title_en), '') IS NULL
UNION ALL
SELECT 'TourStory' AS table_name, t.story_id AS row_id, 'sub_title' AS field_name,
       t.sub_title AS source_ko, t.sub_title_en AS translation_en
FROM public."TourStory" t
WHERE NULLIF(btrim(t.sub_title), '') IS NOT NULL
  AND NULLIF(btrim(t.sub_title_en), '') IS NULL
UNION ALL
SELECT 'TourStory' AS table_name, t.story_id AS row_id, 'summary' AS field_name,
       t.summary AS source_ko, t.summary_en AS translation_en
FROM public."TourStory" t
WHERE NULLIF(btrim(t.summary), '') IS NOT NULL
  AND NULLIF(btrim(t.summary_en), '') IS NULL
UNION ALL
SELECT 'TourStory' AS table_name, t.story_id AS row_id, 'intro' AS field_name,
       t.intro AS source_ko, t.intro_en AS translation_en
FROM public."TourStory" t
WHERE NULLIF(btrim(t.intro), '') IS NOT NULL
  AND NULLIF(btrim(t.intro_en), '') IS NULL
UNION ALL
SELECT 'TourStory' AS table_name, t.story_id AS row_id, 'story_title' AS field_name,
       t.story_title AS source_ko, t.story_title_en AS translation_en
FROM public."TourStory" t
WHERE NULLIF(btrim(t.story_title), '') IS NOT NULL
  AND NULLIF(btrim(t.story_title_en), '') IS NULL
UNION ALL
SELECT 'TourStory' AS table_name, t.story_id AS row_id, 'story' AS field_name,
       t.story AS source_ko, t.story_en AS translation_en
FROM public."TourStory" t
WHERE NULLIF(btrim(t.story), '') IS NOT NULL
  AND NULLIF(btrim(t.story_en), '') IS NULL
UNION ALL
SELECT 'TourStory' AS table_name, t.story_id AS row_id, 'tip' AS field_name,
       t.tip AS source_ko, t.tip_en AS translation_en
FROM public."TourStory" t
WHERE NULLIF(btrim(t.tip), '') IS NOT NULL
  AND NULLIF(btrim(t.tip_en), '') IS NULL
UNION ALL
SELECT 'TourStory' AS table_name, t.story_id AS row_id, 'did_you_know' AS field_name,
       t.did_you_know AS source_ko, t.did_you_know_en AS translation_en
FROM public."TourStory" t
WHERE NULLIF(btrim(t.did_you_know), '') IS NOT NULL
  AND NULLIF(btrim(t.did_you_know_en), '') IS NULL
UNION ALL
SELECT 'HashTag' AS table_name, t.hashtag_id AS row_id, 'name' AS field_name,
       t.name AS source_ko, t.name_en AS translation_en
FROM public."HashTag" t
WHERE NULLIF(btrim(t.name), '') IS NOT NULL
  AND NULLIF(btrim(t.name_en), '') IS NULL
UNION ALL
SELECT 'NearbyPlaceMenu' AS table_name, t.nearby_place_menu_id AS row_id, 'menu_name' AS field_name,
       t.menu_name AS source_ko, t.menu_name_en AS translation_en
FROM public."NearbyPlaceMenu" t
WHERE NULLIF(btrim(t.menu_name), '') IS NOT NULL
  AND NULLIF(btrim(t.menu_name_en), '') IS NULL
UNION ALL
SELECT 'TourCourse' AS table_name, t.tour_course_id AS row_id, 'title' AS field_name,
       t.title AS source_ko, t.title_en AS translation_en
FROM public."TourCourse" t
WHERE NULLIF(btrim(t.title), '') IS NOT NULL
  AND NULLIF(btrim(t.title_en), '') IS NULL
  AND t.featured_yn = true
UNION ALL
SELECT 'NearbyPlaceDetail' AS table_name, t.tour_spot_nearby_course_id AS row_id, 'fee_info' AS field_name,
       t.fee_info AS source_ko, t.fee_info_en AS translation_en
FROM public."NearbyPlaceDetail" t
WHERE NULLIF(btrim(t.fee_info), '') IS NOT NULL
  AND NULLIF(btrim(t.fee_info_en), '') IS NULL
UNION ALL
SELECT 'NearbyPlaceDetail' AS table_name, t.tour_spot_nearby_course_id AS row_id, 'discount_info' AS field_name,
       t.discount_info AS source_ko, t.discount_info_en AS translation_en
FROM public."NearbyPlaceDetail" t
WHERE NULLIF(btrim(t.discount_info), '') IS NOT NULL
  AND NULLIF(btrim(t.discount_info_en), '') IS NULL
UNION ALL
SELECT 'NearbyPlaceDetail' AS table_name, t.tour_spot_nearby_course_id AS row_id, 'credit_card_info' AS field_name,
       t.credit_card_info AS source_ko, t.credit_card_info_en AS translation_en
FROM public."NearbyPlaceDetail" t
WHERE NULLIF(btrim(t.credit_card_info), '') IS NOT NULL
  AND NULLIF(btrim(t.credit_card_info_en), '') IS NULL
UNION ALL
SELECT 'NearbyPlaceDetail' AS table_name, t.tour_spot_nearby_course_id AS row_id, 'open_time' AS field_name,
       t.open_time AS source_ko, t.open_time_en AS translation_en
FROM public."NearbyPlaceDetail" t
WHERE NULLIF(btrim(t.open_time), '') IS NOT NULL
  AND NULLIF(btrim(t.open_time_en), '') IS NULL
UNION ALL
SELECT 'NearbyPlaceDetail' AS table_name, t.tour_spot_nearby_course_id AS row_id, 'rest_date' AS field_name,
       t.rest_date AS source_ko, t.rest_date_en AS translation_en
FROM public."NearbyPlaceDetail" t
WHERE NULLIF(btrim(t.rest_date), '') IS NOT NULL
  AND NULLIF(btrim(t.rest_date_en), '') IS NULL
UNION ALL
SELECT 'NearbyPlaceDetail' AS table_name, t.tour_spot_nearby_course_id AS row_id, 'reservation_info' AS field_name,
       t.reservation_info AS source_ko, t.reservation_info_en AS translation_en
FROM public."NearbyPlaceDetail" t
WHERE NULLIF(btrim(t.reservation_info), '') IS NOT NULL
  AND NULLIF(btrim(t.reservation_info_en), '') IS NULL
UNION ALL
SELECT 'NearbyPlaceDetail' AS table_name, t.tour_spot_nearby_course_id AS row_id, 'inquiry_info' AS field_name,
       t.inquiry_info AS source_ko, t.inquiry_info_en AS translation_en
FROM public."NearbyPlaceDetail" t
WHERE NULLIF(btrim(t.inquiry_info), '') IS NOT NULL
  AND NULLIF(btrim(t.inquiry_info_en), '') IS NULL
UNION ALL
SELECT 'NearbyPlaceDetail' AS table_name, t.tour_spot_nearby_course_id AS row_id, 'parking_info' AS field_name,
       t.parking_info AS source_ko, t.parking_info_en AS translation_en
FROM public."NearbyPlaceDetail" t
WHERE NULLIF(btrim(t.parking_info), '') IS NOT NULL
  AND NULLIF(btrim(t.parking_info_en), '') IS NULL
UNION ALL
SELECT 'NearbyPlaceDetail' AS table_name, t.tour_spot_nearby_course_id AS row_id, 'restroom_info' AS field_name,
       t.restroom_info AS source_ko, t.restroom_info_en AS translation_en
FROM public."NearbyPlaceDetail" t
WHERE NULLIF(btrim(t.restroom_info), '') IS NOT NULL
  AND NULLIF(btrim(t.restroom_info_en), '') IS NULL
UNION ALL
SELECT 'NearbyPlaceDetail' AS table_name, t.tour_spot_nearby_course_id AS row_id, 'baby_carriage_info' AS field_name,
       t.baby_carriage_info AS source_ko, t.baby_carriage_info_en AS translation_en
FROM public."NearbyPlaceDetail" t
WHERE NULLIF(btrim(t.baby_carriage_info), '') IS NOT NULL
  AND NULLIF(btrim(t.baby_carriage_info_en), '') IS NULL
UNION ALL
SELECT 'NearbyPlaceDetail' AS table_name, t.tour_spot_nearby_course_id AS row_id, 'pet_info' AS field_name,
       t.pet_info AS source_ko, t.pet_info_en AS translation_en
FROM public."NearbyPlaceDetail" t
WHERE NULLIF(btrim(t.pet_info), '') IS NOT NULL
  AND NULLIF(btrim(t.pet_info_en), '') IS NULL
UNION ALL
SELECT 'NearbyPlaceDetail' AS table_name, t.tour_spot_nearby_course_id AS row_id, 'smoking_info' AS field_name,
       t.smoking_info AS source_ko, t.smoking_info_en AS translation_en
FROM public."NearbyPlaceDetail" t
WHERE NULLIF(btrim(t.smoking_info), '') IS NOT NULL
  AND NULLIF(btrim(t.smoking_info_en), '') IS NULL
UNION ALL
SELECT 'NearbyPlaceDetail' AS table_name, t.tour_spot_nearby_course_id AS row_id, 'first_menu' AS field_name,
       t.first_menu AS source_ko, t.first_menu_en AS translation_en
FROM public."NearbyPlaceDetail" t
WHERE NULLIF(btrim(t.first_menu), '') IS NOT NULL
  AND NULLIF(btrim(t.first_menu_en), '') IS NULL
UNION ALL
SELECT 'NearbyPlaceDetail' AS table_name, t.tour_spot_nearby_course_id AS row_id, 'treat_menu' AS field_name,
       t.treat_menu AS source_ko, t.treat_menu_en AS translation_en
FROM public."NearbyPlaceDetail" t
WHERE NULLIF(btrim(t.treat_menu), '') IS NOT NULL
  AND NULLIF(btrim(t.treat_menu_en), '') IS NULL
UNION ALL
SELECT 'NearbyPlaceDetail' AS table_name, t.tour_spot_nearby_course_id AS row_id, 'packing_info' AS field_name,
       t.packing_info AS source_ko, t.packing_info_en AS translation_en
FROM public."NearbyPlaceDetail" t
WHERE NULLIF(btrim(t.packing_info), '') IS NOT NULL
  AND NULLIF(btrim(t.packing_info_en), '') IS NULL
UNION ALL
SELECT 'NearbyPlaceDetail' AS table_name, t.tour_spot_nearby_course_id AS row_id, 'kids_facility_info' AS field_name,
       t.kids_facility_info AS source_ko, t.kids_facility_info_en AS translation_en
FROM public."NearbyPlaceDetail" t
WHERE NULLIF(btrim(t.kids_facility_info), '') IS NOT NULL
  AND NULLIF(btrim(t.kids_facility_info_en), '') IS NULL
UNION ALL
SELECT 'NearbyPlaceDetail' AS table_name, t.tour_spot_nearby_course_id AS row_id, 'experience_info' AS field_name,
       t.experience_info AS source_ko, t.experience_info_en AS translation_en
FROM public."NearbyPlaceDetail" t
WHERE NULLIF(btrim(t.experience_info), '') IS NOT NULL
  AND NULLIF(btrim(t.experience_info_en), '') IS NULL
UNION ALL
SELECT 'NearbyPlaceDetail' AS table_name, t.tour_spot_nearby_course_id AS row_id, 'age_limit_info' AS field_name,
       t.age_limit_info AS source_ko, t.age_limit_info_en AS translation_en
FROM public."NearbyPlaceDetail" t
WHERE NULLIF(btrim(t.age_limit_info), '') IS NOT NULL
  AND NULLIF(btrim(t.age_limit_info_en), '') IS NULL
UNION ALL
SELECT 'NearbyPlaceDetail' AS table_name, t.tour_spot_nearby_course_id AS row_id, 'scale_info' AS field_name,
       t.scale_info AS source_ko, t.scale_info_en AS translation_en
FROM public."NearbyPlaceDetail" t
WHERE NULLIF(btrim(t.scale_info), '') IS NOT NULL
  AND NULLIF(btrim(t.scale_info_en), '') IS NULL
UNION ALL
SELECT 'NearbyPlaceDetail' AS table_name, t.tour_spot_nearby_course_id AS row_id, 'use_season_info' AS field_name,
       t.use_season_info AS source_ko, t.use_season_info_en AS translation_en
FROM public."NearbyPlaceDetail" t
WHERE NULLIF(btrim(t.use_season_info), '') IS NOT NULL
  AND NULLIF(btrim(t.use_season_info_en), '') IS NULL
UNION ALL
SELECT 'NearbyPlaceDetail' AS table_name, t.tour_spot_nearby_course_id AS row_id, 'spend_time_info' AS field_name,
       t.spend_time_info AS source_ko, t.spend_time_info_en AS translation_en
FROM public."NearbyPlaceDetail" t
WHERE NULLIF(btrim(t.spend_time_info), '') IS NOT NULL
  AND NULLIF(btrim(t.spend_time_info_en), '') IS NULL
UNION ALL
SELECT 'AccommodationRoom' AS table_name, t.room_id AS row_id, 'room_title' AS field_name,
       t.room_title AS source_ko, t.room_title_en AS translation_en
FROM public."AccommodationRoom" t
WHERE NULLIF(btrim(t.room_title), '') IS NOT NULL
  AND NULLIF(btrim(t.room_title_en), '') IS NULL
UNION ALL
SELECT 'AccommodationRoom' AS table_name, t.room_id AS row_id, 'room_size_pyeong' AS field_name,
       t.room_size_pyeong AS source_ko, t.room_size_pyeong_en AS translation_en
FROM public."AccommodationRoom" t
WHERE NULLIF(btrim(t.room_size_pyeong), '') IS NOT NULL
  AND NULLIF(btrim(t.room_size_pyeong_en), '') IS NULL
UNION ALL
SELECT 'AccommodationRoom' AS table_name, t.room_id AS row_id, 'room_size_square_meter' AS field_name,
       t.room_size_square_meter AS source_ko, t.room_size_square_meter_en AS translation_en
FROM public."AccommodationRoom" t
WHERE NULLIF(btrim(t.room_size_square_meter), '') IS NOT NULL
  AND NULLIF(btrim(t.room_size_square_meter_en), '') IS NULL
UNION ALL
SELECT 'AccommodationRoom' AS table_name, t.room_id AS row_id, 'room_count' AS field_name,
       t.room_count AS source_ko, t.room_count_en AS translation_en
FROM public."AccommodationRoom" t
WHERE NULLIF(btrim(t.room_count), '') IS NOT NULL
  AND NULLIF(btrim(t.room_count_en), '') IS NULL
UNION ALL
SELECT 'AccommodationRoom' AS table_name, t.room_id AS row_id, 'base_count' AS field_name,
       t.base_count AS source_ko, t.base_count_en AS translation_en
FROM public."AccommodationRoom" t
WHERE NULLIF(btrim(t.base_count), '') IS NOT NULL
  AND NULLIF(btrim(t.base_count_en), '') IS NULL
UNION ALL
SELECT 'AccommodationRoom' AS table_name, t.room_id AS row_id, 'max_count' AS field_name,
       t.max_count AS source_ko, t.max_count_en AS translation_en
FROM public."AccommodationRoom" t
WHERE NULLIF(btrim(t.max_count), '') IS NOT NULL
  AND NULLIF(btrim(t.max_count_en), '') IS NULL
UNION ALL
SELECT 'AccommodationRoom' AS table_name, t.room_id AS row_id, 'offseason_weekday_min_fee' AS field_name,
       t.offseason_weekday_min_fee AS source_ko, t.offseason_weekday_min_fee_en AS translation_en
FROM public."AccommodationRoom" t
WHERE NULLIF(btrim(t.offseason_weekday_min_fee), '') IS NOT NULL
  AND NULLIF(btrim(t.offseason_weekday_min_fee_en), '') IS NULL
UNION ALL
SELECT 'AccommodationRoom' AS table_name, t.room_id AS row_id, 'offseason_weekend_min_fee' AS field_name,
       t.offseason_weekend_min_fee AS source_ko, t.offseason_weekend_min_fee_en AS translation_en
FROM public."AccommodationRoom" t
WHERE NULLIF(btrim(t.offseason_weekend_min_fee), '') IS NOT NULL
  AND NULLIF(btrim(t.offseason_weekend_min_fee_en), '') IS NULL
UNION ALL
SELECT 'AccommodationRoom' AS table_name, t.room_id AS row_id, 'peak_season_weekday_min_fee' AS field_name,
       t.peak_season_weekday_min_fee AS source_ko, t.peak_season_weekday_min_fee_en AS translation_en
FROM public."AccommodationRoom" t
WHERE NULLIF(btrim(t.peak_season_weekday_min_fee), '') IS NOT NULL
  AND NULLIF(btrim(t.peak_season_weekday_min_fee_en), '') IS NULL
UNION ALL
SELECT 'AccommodationRoom' AS table_name, t.room_id AS row_id, 'peak_season_weekend_min_fee' AS field_name,
       t.peak_season_weekend_min_fee AS source_ko, t.peak_season_weekend_min_fee_en AS translation_en
FROM public."AccommodationRoom" t
WHERE NULLIF(btrim(t.peak_season_weekend_min_fee), '') IS NOT NULL
  AND NULLIF(btrim(t.peak_season_weekend_min_fee_en), '') IS NULL
UNION ALL
SELECT 'AccommodationRoom' AS table_name, t.room_id AS row_id, 'room_intro' AS field_name,
       t.room_intro AS source_ko, t.room_intro_en AS translation_en
FROM public."AccommodationRoom" t
WHERE NULLIF(btrim(t.room_intro), '') IS NOT NULL
  AND NULLIF(btrim(t.room_intro_en), '') IS NULL
UNION ALL
SELECT 'AccommodationRoom' AS table_name, t.room_id AS row_id, 'has_bath_facility' AS field_name,
       t.has_bath_facility AS source_ko, t.has_bath_facility_en AS translation_en
FROM public."AccommodationRoom" t
WHERE NULLIF(btrim(t.has_bath_facility), '') IS NOT NULL
  AND NULLIF(btrim(t.has_bath_facility_en), '') IS NULL
UNION ALL
SELECT 'AccommodationRoom' AS table_name, t.room_id AS row_id, 'has_bath' AS field_name,
       t.has_bath AS source_ko, t.has_bath_en AS translation_en
FROM public."AccommodationRoom" t
WHERE NULLIF(btrim(t.has_bath), '') IS NOT NULL
  AND NULLIF(btrim(t.has_bath_en), '') IS NULL
UNION ALL
SELECT 'AccommodationRoom' AS table_name, t.room_id AS row_id, 'has_home_theater' AS field_name,
       t.has_home_theater AS source_ko, t.has_home_theater_en AS translation_en
FROM public."AccommodationRoom" t
WHERE NULLIF(btrim(t.has_home_theater), '') IS NOT NULL
  AND NULLIF(btrim(t.has_home_theater_en), '') IS NULL
UNION ALL
SELECT 'AccommodationRoom' AS table_name, t.room_id AS row_id, 'has_air_condition' AS field_name,
       t.has_air_condition AS source_ko, t.has_air_condition_en AS translation_en
FROM public."AccommodationRoom" t
WHERE NULLIF(btrim(t.has_air_condition), '') IS NOT NULL
  AND NULLIF(btrim(t.has_air_condition_en), '') IS NULL
UNION ALL
SELECT 'AccommodationRoom' AS table_name, t.room_id AS row_id, 'has_tv' AS field_name,
       t.has_tv AS source_ko, t.has_tv_en AS translation_en
FROM public."AccommodationRoom" t
WHERE NULLIF(btrim(t.has_tv), '') IS NOT NULL
  AND NULLIF(btrim(t.has_tv_en), '') IS NULL
UNION ALL
SELECT 'AccommodationRoom' AS table_name, t.room_id AS row_id, 'has_pc' AS field_name,
       t.has_pc AS source_ko, t.has_pc_en AS translation_en
FROM public."AccommodationRoom" t
WHERE NULLIF(btrim(t.has_pc), '') IS NOT NULL
  AND NULLIF(btrim(t.has_pc_en), '') IS NULL
UNION ALL
SELECT 'AccommodationRoom' AS table_name, t.room_id AS row_id, 'has_cable' AS field_name,
       t.has_cable AS source_ko, t.has_cable_en AS translation_en
FROM public."AccommodationRoom" t
WHERE NULLIF(btrim(t.has_cable), '') IS NOT NULL
  AND NULLIF(btrim(t.has_cable_en), '') IS NULL
UNION ALL
SELECT 'AccommodationRoom' AS table_name, t.room_id AS row_id, 'has_internet' AS field_name,
       t.has_internet AS source_ko, t.has_internet_en AS translation_en
FROM public."AccommodationRoom" t
WHERE NULLIF(btrim(t.has_internet), '') IS NOT NULL
  AND NULLIF(btrim(t.has_internet_en), '') IS NULL
UNION ALL
SELECT 'AccommodationRoom' AS table_name, t.room_id AS row_id, 'has_refrigerator' AS field_name,
       t.has_refrigerator AS source_ko, t.has_refrigerator_en AS translation_en
FROM public."AccommodationRoom" t
WHERE NULLIF(btrim(t.has_refrigerator), '') IS NOT NULL
  AND NULLIF(btrim(t.has_refrigerator_en), '') IS NULL
UNION ALL
SELECT 'AccommodationRoom' AS table_name, t.room_id AS row_id, 'has_toiletries' AS field_name,
       t.has_toiletries AS source_ko, t.has_toiletries_en AS translation_en
FROM public."AccommodationRoom" t
WHERE NULLIF(btrim(t.has_toiletries), '') IS NOT NULL
  AND NULLIF(btrim(t.has_toiletries_en), '') IS NULL
UNION ALL
SELECT 'AccommodationRoom' AS table_name, t.room_id AS row_id, 'has_sofa' AS field_name,
       t.has_sofa AS source_ko, t.has_sofa_en AS translation_en
FROM public."AccommodationRoom" t
WHERE NULLIF(btrim(t.has_sofa), '') IS NOT NULL
  AND NULLIF(btrim(t.has_sofa_en), '') IS NULL
UNION ALL
SELECT 'AccommodationRoom' AS table_name, t.room_id AS row_id, 'has_cook' AS field_name,
       t.has_cook AS source_ko, t.has_cook_en AS translation_en
FROM public."AccommodationRoom" t
WHERE NULLIF(btrim(t.has_cook), '') IS NOT NULL
  AND NULLIF(btrim(t.has_cook_en), '') IS NULL
UNION ALL
SELECT 'AccommodationRoom' AS table_name, t.room_id AS row_id, 'has_table' AS field_name,
       t.has_table AS source_ko, t.has_table_en AS translation_en
FROM public."AccommodationRoom" t
WHERE NULLIF(btrim(t.has_table), '') IS NOT NULL
  AND NULLIF(btrim(t.has_table_en), '') IS NULL
UNION ALL
SELECT 'AccommodationRoom' AS table_name, t.room_id AS row_id, 'has_hairdryer' AS field_name,
       t.has_hairdryer AS source_ko, t.has_hairdryer_en AS translation_en
FROM public."AccommodationRoom" t
WHERE NULLIF(btrim(t.has_hairdryer), '') IS NOT NULL
  AND NULLIF(btrim(t.has_hairdryer_en), '') IS NULL
ORDER BY table_name, row_id, field_name;
