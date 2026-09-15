-- 식당 메뉴 가격 및 출처 보강용 마이그레이션
ALTER TABLE public."NearbyPlaceDetail"
    ADD COLUMN IF NOT EXISTS first_menu_price int8,
    ADD COLUMN IF NOT EXISTS treat_menu_price int8,
    ADD COLUMN IF NOT EXISTS menu_source text,
    ADD COLUMN IF NOT EXISTS menu_updated_at timestamptz;

COMMENT ON COLUMN public."NearbyPlaceDetail".first_menu_price IS '대표 메뉴 가격(원). 확인되지 않으면 NULL';
COMMENT ON COLUMN public."NearbyPlaceDetail".treat_menu_price IS '추천 메뉴 가격(원). 확인되지 않으면 NULL';
COMMENT ON COLUMN public."NearbyPlaceDetail".menu_source IS '메뉴·가격 정보 출처 URL 또는 기관명';
COMMENT ON COLUMN public."NearbyPlaceDetail".menu_updated_at IS '메뉴·가격 정보 확인 시각';

CREATE TABLE IF NOT EXISTS public."NearbyPlaceMenu"
(
    nearby_place_menu_id       bigserial PRIMARY KEY,
    tour_spot_nearby_course_id int8 NOT NULL,
    menu_name                  text NOT NULL,
    price                      int8 NULL,
    image_url                  text NULL,
    source                     text NULL,
    updated_at                 timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT "FK_NEARBYPACEMENU_PLACE" FOREIGN KEY (tour_spot_nearby_course_id)
        REFERENCES public."TourSpotNearbyPlace" (tour_spot_nearby_course_id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS "IDX_NEARBYPACEMENU_PLACE"
    ON public."NearbyPlaceMenu" (tour_spot_nearby_course_id);
CREATE UNIQUE INDEX IF NOT EXISTS "UK_NEARBYPACEMENU_PLACE_NAME"
    ON public."NearbyPlaceMenu" (tour_spot_nearby_course_id, menu_name);
