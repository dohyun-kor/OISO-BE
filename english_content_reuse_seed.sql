-- Reuse English only when a database-only Korean field exactly matches a
-- provider-authored text already translated in a TourStory record.
-- Run after all english_story_* seed files. Ambiguous English variants are skipped.
-- No user-authored Memo or user course text is read or changed.
BEGIN;

CREATE TEMP TABLE oiso_unique_story_names ON COMMIT DROP AS
SELECT source_ko, min(english) AS english
FROM (
    SELECT title AS source_ko, title_en AS english
    FROM public."TourStory"
    WHERE NULLIF(btrim(title_en), '') IS NOT NULL
) AS translated
WHERE NULLIF(btrim(source_ko), '') IS NOT NULL
GROUP BY source_ko
HAVING count(DISTINCT english) = 1;

CREATE UNIQUE INDEX ON oiso_unique_story_names (source_ko);

UPDATE public."TourSpot" AS spot
SET name_en = translated.english
FROM oiso_unique_story_names AS translated
WHERE spot.name = translated.source_ko
  AND NULLIF(btrim(spot.name_en), '') IS NULL;

UPDATE public."TourSpotNearbyPlace" AS nearby
SET name_en = translated.english
FROM oiso_unique_story_names AS translated
WHERE nearby.name = translated.source_ko
  AND NULLIF(btrim(nearby.name_en), '') IS NULL;

CREATE TEMP TABLE oiso_unique_story_copy ON COMMIT DROP AS
SELECT source_ko, min(english) AS english
FROM (
    SELECT summary AS source_ko, summary_en AS english FROM public."TourStory"
    UNION ALL
    SELECT intro, intro_en FROM public."TourStory"
    UNION ALL
    SELECT story, story_en FROM public."TourStory"
    UNION ALL
    SELECT tip, tip_en FROM public."TourStory"
    UNION ALL
    SELECT did_you_know, did_you_know_en FROM public."TourStory"
) AS translated
WHERE NULLIF(btrim(source_ko), '') IS NOT NULL
  AND NULLIF(btrim(english), '') IS NOT NULL
GROUP BY source_ko
HAVING count(DISTINCT english) = 1;

CREATE UNIQUE INDEX ON oiso_unique_story_copy (source_ko);

UPDATE public."TourSpot" AS spot
SET content_en = translated.english
FROM oiso_unique_story_copy AS translated
WHERE spot.content = translated.source_ko
  AND NULLIF(btrim(spot.content_en), '') IS NULL;

UPDATE public."TourSpot" AS spot
SET overview_en = translated.english
FROM oiso_unique_story_copy AS translated
WHERE spot.overview = translated.source_ko
  AND NULLIF(btrim(spot.overview_en), '') IS NULL;

UPDATE public."TourSpotNearbyPlace" AS nearby
SET overview_en = translated.english
FROM oiso_unique_story_copy AS translated
WHERE nearby.overview = translated.source_ko
  AND NULLIF(btrim(nearby.overview_en), '') IS NULL;

COMMIT;
