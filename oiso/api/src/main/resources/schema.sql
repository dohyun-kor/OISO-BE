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
