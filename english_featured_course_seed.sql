-- Draft translations of repository-provided featured courses.
-- Run after english_content_migration.sql and the featured course seed.
-- Existing translations and non-featured user courses are preserved.
WITH translations(title_ko, title_en) AS (
    VALUES
        ('경주 핵심 유적 산책', 'Gyeongju Historic Highlights Walk'),
        ('보문호 감성 하루', 'A Relaxing Day at Bomun Lake'),
        ('남산 불교 유적길', 'Namsan Buddhist Heritage Trail'),
        ('왕릉 따라 걷는 길', 'Royal Tombs Walking Trail'),
        ('서라벌 야경 코스', 'Seorabeol Night Views'),
        ('경주 바다 드라이브', 'Gyeongju Coastal Drive'),
        ('아이와 함께 경주', 'Gyeongju with Kids'),
        ('조용한 경주 힐링길', 'A Peaceful Walk in Gyeongju'),
        ('신라 왕경 역사여행', 'Journey through the Silla Royal Capital'),
        ('경주 사진 명소 투어', 'Gyeongju Photo Spots Tour'),
        ('경주 봄꽃 산책', 'Gyeongju Spring Blossom Walk'),
        ('경주 역사 야간여행', 'Gyeongju Heritage at Night'),
        ('경주 왕릉 역사길', 'Gyeongju Royal Tombs Heritage Trail'),
        ('경주 남산 하루코스', 'A Day on Gyeongju Namsan'),
        ('경주 조용한 사찰길', 'Gyeongju Quiet Temple Trail'),
        ('보문호와 왕경 코스', 'Bomun Lake and the Royal Capital'),
        ('경주 자연 힐링코스', 'Gyeongju Nature Retreat'),
        ('경주 전통마을 여행', 'Gyeongju Traditional Villages Tour'),
        ('경주 문화유산 사진길', 'Gyeongju Heritage Photography Trail'),
        ('경주 주말 드라이브', 'Gyeongju Weekend Drive')
)
UPDATE public."TourCourse" course
SET title_en = translations.title_en
FROM translations
WHERE course.featured_yn = true
  AND course.title = translations.title_ko
  AND NULLIF(btrim(course.title_en), '') IS NULL;
