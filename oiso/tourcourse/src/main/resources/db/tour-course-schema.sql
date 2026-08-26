-- Apply this migration to the application database before using the tour-course API.
ALTER TABLE public."TourCourse"
    ADD COLUMN IF NOT EXISTS share_yn boolean NOT NULL DEFAULT false,
    ADD COLUMN IF NOT EXISTS shared_password varchar(100),
    ADD COLUMN IF NOT EXISTS created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN IF NOT EXISTS updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN IF NOT EXISTS total_distance_meter integer NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS total_duration_second integer NOT NULL DEFAULT 0;

ALTER TABLE public."TourCourseItem"
    ADD COLUMN IF NOT EXISTS day_number integer NOT NULL DEFAULT 1,
    ADD COLUMN IF NOT EXISTS status varchar(20) NOT NULL DEFAULT 'PENDING',
    ADD COLUMN IF NOT EXISTS tour_spot_nearby_course_id bigint,
    ADD COLUMN IF NOT EXISTS transport_type varchar(10),
    ADD COLUMN IF NOT EXISTS distance_meter integer,
    ADD COLUMN IF NOT EXISTS duration_second integer;

ALTER TABLE public."TourCourseItem"
    DROP CONSTRAINT IF EXISTS "UK_TOURCOURSEITEM_ORDER";

CREATE UNIQUE INDEX IF NOT EXISTS ux_tour_course_item_day_order
    ON public."TourCourseItem" (tour_course_id, day_number, item_order)
    WHERE tour_course_id IS NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'CK_TOURCOURSEITEM_TRANSPORT_TYPE'
    ) THEN
        ALTER TABLE public."TourCourseItem"
            ADD CONSTRAINT "CK_TOURCOURSEITEM_TRANSPORT_TYPE"
                CHECK (transport_type IS NULL OR transport_type IN ('CAR', 'WALK', 'BIKE'));
    END IF;
END $$;
