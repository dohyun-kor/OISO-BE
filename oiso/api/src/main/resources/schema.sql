CREATE TABLE IF NOT EXISTS tour_ingest_execution (
    id BIGSERIAL PRIMARY KEY,
    status VARCHAR(20) NOT NULL,
    started_at TIMESTAMP NOT NULL,
    finished_at TIMESTAMP,
    processed_count INTEGER NOT NULL,
    failure_message TEXT
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_tour_spot_content_id
    ON "TourSpot" (content_id)
    WHERE content_id IS NOT NULL;

CREATE TABLE IF NOT EXISTS "AccommodationDetail" (
    temp_room_id BIGINT NOT NULL,
    spot_id BIGINT NOT NULL,
    room_code TEXT,
    room_title TEXT,
    room_size_pyeong TEXT,
    room_size_square_meter TEXT,
    room_count TEXT,
    base_count TEXT,
    max_count TEXT,
    offseason_weekday_min_fee TEXT,
    offseason_weekend_min_fee TEXT,
    peak_season_weekday_min_fee TEXT,
    peak_season_weekend_min_fee TEXT,
    room_intro TEXT,
    has_bath_facility TEXT,
    has_bath TEXT,
    has_home_theater TEXT,
    has_air_condition TEXT,
    has_tv TEXT,
    has_pc TEXT,
    has_cable TEXT,
    has_internet TEXT,
    has_refrigerator TEXT,
    has_toiletries TEXT,
    has_sofa TEXT,
    has_cook TEXT,
    has_table TEXT,
    has_hairdryer TEXT,
    CONSTRAINT "PK_ACCOMMODATIONDETAIL" PRIMARY KEY (temp_room_id, spot_id)
);
