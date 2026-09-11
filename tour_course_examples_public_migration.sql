-- 기존에 생성된 OISO 예시 코스를 비밀번호 없는 홈 추천 코스로 전환
-- 운영/로컬 DB에서 1회 실행
BEGIN;

ALTER TABLE public."TourCourse"
    ADD COLUMN IF NOT EXISTS featured_yn boolean NOT NULL DEFAULT false;

UPDATE public."TourCourse"
SET share_yn = false,
    shared_password = NULL,
    featured_yn = true,
    updated_at = now()
WHERE title IN (
    '경주 핵심 유적 산책', '보문호 감성 하루', '남산 불교 유적길', '왕릉 따라 걷는 길',
    '서라벌 야경 코스', '경주 바다 드라이브', '아이와 함께 경주', '조용한 경주 힐링길',
    '신라 왕경 역사여행', '경주 사진 명소 투어', '경주 봄꽃 산책', '경주 역사 야간여행',
    '경주 왕릉 역사길', '경주 남산 하루코스', '경주 조용한 사찰길', '보문호와 왕경 코스',
    '경주 자연 힐링코스', '경주 전통마을 여행', '경주 문화유산 사진길', '경주 주말 드라이브'
  );

COMMIT;
