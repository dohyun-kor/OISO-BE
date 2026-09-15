-- 기존 로컬 DB에 NearbyPlaceDetail이 이미 존재하는 경우 누락 컬럼을 보완한다.
CREATE TABLE IF NOT EXISTS public."NearbyPlaceDetail" (
    tour_spot_nearby_course_id int8 PRIMARY KEY
);

ALTER TABLE public."NearbyPlaceDetail"
    ADD COLUMN IF NOT EXISTS open_time text,
    ADD COLUMN IF NOT EXISTS rest_date text,
    ADD COLUMN IF NOT EXISTS reservation_info text,
    ADD COLUMN IF NOT EXISTS inquiry_info text,
    ADD COLUMN IF NOT EXISTS parking_info text,
    ADD COLUMN IF NOT EXISTS pet_info text,
    ADD COLUMN IF NOT EXISTS first_menu text,
    ADD COLUMN IF NOT EXISTS treat_menu text,
    ADD COLUMN IF NOT EXISTS first_menu_price int8,
    ADD COLUMN IF NOT EXISTS treat_menu_price int8,s
    ADD COLUMN IF NOT EXISTS menu_source text,
    ADD COLUMN IF NOT EXISTS menu_updated_at timestamptz;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'FK_NEARBYPLACEDETAIL_NEARBYPLACE') THEN
        ALTER TABLE public."NearbyPlaceDetail"
            ADD CONSTRAINT "FK_NEARBYPLACEDETAIL_NEARBYPLACE"
            FOREIGN KEY (tour_spot_nearby_course_id)
            REFERENCES public."TourSpotNearbyPlace" (tour_spot_nearby_course_id)
            ON DELETE CASCADE;
    END IF;
END $$;
