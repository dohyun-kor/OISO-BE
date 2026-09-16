-- English content storage. Existing Korean text and user content are preserved.
-- Idempotent: safe to execute again. NULL means not translated yet.

ALTER TABLE public."TourSpot"
    ADD COLUMN IF NOT EXISTS name_en text,
    ADD COLUMN IF NOT EXISTS content_en text,
    ADD COLUMN IF NOT EXISTS address_en text,
    ADD COLUMN IF NOT EXISTS overview_en text;

ALTER TABLE public."TourSpotNearbyPlace"
    ADD COLUMN IF NOT EXISTS name_en text,
    ADD COLUMN IF NOT EXISTS overview_en text,
    ADD COLUMN IF NOT EXISTS mood_en text,
    ADD COLUMN IF NOT EXISTS address_en text;

ALTER TABLE public."TourStory"
    ADD COLUMN IF NOT EXISTS title_en text,
    ADD COLUMN IF NOT EXISTS sub_title_en text,
    ADD COLUMN IF NOT EXISTS summary_en text,
    ADD COLUMN IF NOT EXISTS intro_en text,
    ADD COLUMN IF NOT EXISTS story_title_en text,
    ADD COLUMN IF NOT EXISTS story_en text,
    ADD COLUMN IF NOT EXISTS tip_en text,
    ADD COLUMN IF NOT EXISTS did_you_know_en text;

ALTER TABLE public."HashTag"
    ADD COLUMN IF NOT EXISTS name_en text;

ALTER TABLE public."NearbyPlaceMenu"
    ADD COLUMN IF NOT EXISTS menu_name_en text;

ALTER TABLE public."TourCourse"
    ADD COLUMN IF NOT EXISTS title_en text;

ALTER TABLE public."NearbyPlaceDetail"
    ADD COLUMN IF NOT EXISTS fee_info_en text,
    ADD COLUMN IF NOT EXISTS discount_info_en text,
    ADD COLUMN IF NOT EXISTS credit_card_info_en text,
    ADD COLUMN IF NOT EXISTS open_time_en text,
    ADD COLUMN IF NOT EXISTS rest_date_en text,
    ADD COLUMN IF NOT EXISTS reservation_info_en text,
    ADD COLUMN IF NOT EXISTS inquiry_info_en text,
    ADD COLUMN IF NOT EXISTS parking_info_en text,
    ADD COLUMN IF NOT EXISTS restroom_info_en text,
    ADD COLUMN IF NOT EXISTS baby_carriage_info_en text,
    ADD COLUMN IF NOT EXISTS pet_info_en text,
    ADD COLUMN IF NOT EXISTS smoking_info_en text,
    ADD COLUMN IF NOT EXISTS first_menu_en text,
    ADD COLUMN IF NOT EXISTS treat_menu_en text,
    ADD COLUMN IF NOT EXISTS packing_info_en text,
    ADD COLUMN IF NOT EXISTS kids_facility_info_en text,
    ADD COLUMN IF NOT EXISTS experience_info_en text,
    ADD COLUMN IF NOT EXISTS age_limit_info_en text,
    ADD COLUMN IF NOT EXISTS scale_info_en text,
    ADD COLUMN IF NOT EXISTS use_season_info_en text,
    ADD COLUMN IF NOT EXISTS spend_time_info_en text;

ALTER TABLE public."AccommodationRoom"
    ADD COLUMN IF NOT EXISTS room_title_en text,
    ADD COLUMN IF NOT EXISTS room_size_pyeong_en text,
    ADD COLUMN IF NOT EXISTS room_size_square_meter_en text,
    ADD COLUMN IF NOT EXISTS room_count_en text,
    ADD COLUMN IF NOT EXISTS base_count_en text,
    ADD COLUMN IF NOT EXISTS max_count_en text,
    ADD COLUMN IF NOT EXISTS offseason_weekday_min_fee_en text,
    ADD COLUMN IF NOT EXISTS offseason_weekend_min_fee_en text,
    ADD COLUMN IF NOT EXISTS peak_season_weekday_min_fee_en text,
    ADD COLUMN IF NOT EXISTS peak_season_weekend_min_fee_en text,
    ADD COLUMN IF NOT EXISTS room_intro_en text,
    ADD COLUMN IF NOT EXISTS has_bath_facility_en text,
    ADD COLUMN IF NOT EXISTS has_bath_en text,
    ADD COLUMN IF NOT EXISTS has_home_theater_en text,
    ADD COLUMN IF NOT EXISTS has_air_condition_en text,
    ADD COLUMN IF NOT EXISTS has_tv_en text,
    ADD COLUMN IF NOT EXISTS has_pc_en text,
    ADD COLUMN IF NOT EXISTS has_cable_en text,
    ADD COLUMN IF NOT EXISTS has_internet_en text,
    ADD COLUMN IF NOT EXISTS has_refrigerator_en text,
    ADD COLUMN IF NOT EXISTS has_toiletries_en text,
    ADD COLUMN IF NOT EXISTS has_sofa_en text,
    ADD COLUMN IF NOT EXISTS has_cook_en text,
    ADD COLUMN IF NOT EXISTS has_table_en text,
    ADD COLUMN IF NOT EXISTS has_hairdryer_en text;
