-- 식당·숙소 개인 메모 지원: 기존 메모 보존
ALTER TABLE public."Memo"
    ADD COLUMN IF NOT EXISTS nearby_place_id bigint
        REFERENCES public."TourSpotNearbyPlace" (tour_spot_nearby_course_id) ON DELETE CASCADE;
CREATE UNIQUE INDEX IF NOT EXISTS "UK_MEMO_USER_NEARBY_PLACE"
    ON public."Memo" (user_id, nearby_place_id)
    WHERE nearby_place_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS "IX_MEMO_NEARBY_PLACE_ID"
    ON public."Memo" (nearby_place_id);
