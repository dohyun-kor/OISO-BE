-- Venue-specific menu labels are transliterated where the Korean name alone
-- does not establish the dish's ingredients or the venue's official English spelling.
-- Run after the two other english_menu_content_seed files.
BEGIN;

CREATE TEMP TABLE oiso_brand_menu_names (
    source_ko text PRIMARY KEY,
    english text NOT NULL
) ON COMMIT DROP;

INSERT INTO oiso_brand_menu_names (source_ko, english) VALUES
    ('긍정빵', 'Positive Bread'),
    ('낙지마실', 'Nakji Masil'),
    ('날이 좋아서 빵찌니', 'Bbangjjini'),
    ('노네임', 'No Name'),
    ('랑산밥상', 'Rangsan Set Meal'),
    ('맛자랑정식', 'Matjarang Set Meal'),
    ('석하정일품', 'Seokhajeong Special'),
    ('오륙돈 소육', 'Oryukdon Soyuk (Boiled Pork)'),
    ('오륙돈 지짐', 'Oryukdon Pan-Fried Pork'),
    ('정일품', 'Jeongilpum');

UPDATE public."NearbyPlaceMenu" AS menu
SET menu_name_en = names.english
FROM oiso_brand_menu_names AS names
WHERE menu.menu_name = names.source_ko
  AND NULLIF(btrim(menu.menu_name_en), '') IS NULL;

UPDATE public."NearbyPlaceDetail" AS detail
SET first_menu_en = names.english
FROM oiso_brand_menu_names AS names
WHERE detail.first_menu = names.source_ko
  AND NULLIF(btrim(detail.first_menu_en), '') IS NULL;

UPDATE public."NearbyPlaceDetail" AS detail
SET treat_menu_en = names.english
FROM oiso_brand_menu_names AS names
WHERE detail.treat_menu = names.source_ko
  AND NULLIF(btrim(detail.treat_menu_en), '') IS NULL;

COMMIT;
