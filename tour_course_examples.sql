-- OISO 홈 화면용 공개 관광코스 예시 10개
-- 서버의 최신 schema.sql 적용 후 1회 실행하세요.
-- user_id=1 계정이 존재한다는 전제입니다. 운영 계정 ID가 다르면 아래 1을 변경하세요.
BEGIN;

WITH c AS (
  INSERT INTO public."TourCourse" (user_id, title, share_yn, shared_password, status)
  VALUES (1, '경주 핵심 유적 산책', true, 'OISO2026', 'PENDING') RETURNING tour_course_id
) INSERT INTO public."TourCourseItem" (tour_course_id, spot_id, day_number, item_order, transport_type, status)
SELECT c.tour_course_id, v.spot_id, 1, v.ord, CASE WHEN v.ord=1 THEN NULL ELSE 'WALK' END, 'PENDING' FROM c CROSS JOIN (VALUES (24,1),(32,2),(184,3),(167,4)) v(spot_id,ord);

WITH c AS (
  INSERT INTO public."TourCourse" (user_id, title, share_yn, shared_password, status)
  VALUES (1, '보문호 감성 하루', true, 'OISO2026', 'PENDING') RETURNING tour_course_id
) INSERT INTO public."TourCourseItem" (tour_course_id, spot_id, day_number, item_order, transport_type, status)
SELECT c.tour_course_id, v.spot_id, 1, v.ord, CASE WHEN v.ord=1 THEN NULL ELSE 'WALK' END, 'PENDING' FROM c CROSS JOIN (VALUES (32,1),(61,2),(54,3)) v(spot_id,ord);

WITH c AS (
  INSERT INTO public."TourCourse" (user_id, title, share_yn, shared_password, status)
  VALUES (1, '남산 불교 유적길', true, 'OISO2026', 'PENDING') RETURNING tour_course_id
) INSERT INTO public."TourCourseItem" (tour_course_id, spot_id, day_number, item_order, transport_type, status)
SELECT c.tour_course_id, v.spot_id, 1, v.ord, CASE WHEN v.ord=1 THEN NULL ELSE 'CAR' END, 'PENDING' FROM c CROSS JOIN (VALUES (131,1),(109,2),(140,3),(156,4)) v(spot_id,ord);

WITH c AS (
  INSERT INTO public."TourCourse" (user_id, title, share_yn, shared_password, status)
  VALUES (1, '왕릉 따라 걷는 길', true, 'OISO2026', 'PENDING') RETURNING tour_course_id
) INSERT INTO public."TourCourseItem" (tour_course_id, spot_id, day_number, item_order, transport_type, status)
SELECT c.tour_course_id, v.spot_id, 1, v.ord, CASE WHEN v.ord=1 THEN NULL ELSE 'CAR' END, 'PENDING' FROM c CROSS JOIN (VALUES (160,1),(265,2),(266,3),(290,4)) v(spot_id,ord);

WITH c AS (
  INSERT INTO public."TourCourse" (user_id, title, share_yn, shared_password, status)
  VALUES (1, '서라벌 야경 코스', true, 'OISO2026', 'PENDING') RETURNING tour_course_id
) INSERT INTO public."TourCourseItem" (tour_course_id, spot_id, day_number, item_order, transport_type, status)
SELECT c.tour_course_id, v.spot_id, 1, v.ord, CASE WHEN v.ord=1 THEN NULL ELSE 'CAR' END, 'PENDING' FROM c CROSS JOIN (VALUES (184,1),(167,2),(178,3),(160,4)) v(spot_id,ord);

WITH c AS (
  INSERT INTO public."TourCourse" (user_id, title, share_yn, shared_password, status)
  VALUES (1, '경주 바다 드라이브', true, 'OISO2026', 'PENDING') RETURNING tour_course_id
) INSERT INTO public."TourCourseItem" (tour_course_id, spot_id, day_number, item_order, transport_type, status)
SELECT c.tour_course_id, v.spot_id, 1, v.ord, CASE WHEN v.ord=1 THEN NULL ELSE 'CAR' END, 'PENDING' FROM c CROSS JOIN (VALUES (56,1),(54,2),(32,3)) v(spot_id,ord);

WITH c AS (
  INSERT INTO public."TourCourse" (user_id, title, share_yn, shared_password, status)
  VALUES (1, '아이와 함께 경주', true, 'OISO2026', 'PENDING') RETURNING tour_course_id
) INSERT INTO public."TourCourseItem" (tour_course_id, spot_id, day_number, item_order, transport_type, status)
SELECT c.tour_course_id, v.spot_id, 1, v.ord, CASE WHEN v.ord=1 THEN NULL ELSE 'CAR' END, 'PENDING' FROM c CROSS JOIN (VALUES (32,1),(61,2),(164,3),(195,4)) v(spot_id,ord);

WITH c AS (
  INSERT INTO public."TourCourse" (user_id, title, share_yn, shared_password, status)
  VALUES (1, '조용한 경주 힐링길', true, 'OISO2026', 'PENDING') RETURNING tour_course_id
) INSERT INTO public."TourCourseItem" (tour_course_id, spot_id, day_number, item_order, transport_type, status)
SELECT c.tour_course_id, v.spot_id, 1, v.ord, CASE WHEN v.ord=1 THEN NULL ELSE 'CAR' END, 'PENDING' FROM c CROSS JOIN (VALUES (195,1),(61,2),(140,3),(143,4)) v(spot_id,ord);

WITH c AS (
  INSERT INTO public."TourCourse" (user_id, title, share_yn, shared_password, status)
  VALUES (1, '신라 왕경 역사여행', true, 'OISO2026', 'PENDING') RETURNING tour_course_id
) INSERT INTO public."TourCourseItem" (tour_course_id, spot_id, day_number, item_order, transport_type, status)
SELECT c.tour_course_id, v.spot_id, 1, v.ord, CASE WHEN v.ord=1 THEN NULL ELSE 'CAR' END, 'PENDING' FROM c CROSS JOIN (VALUES (167,1),(178,2),(160,3),(265,4)) v(spot_id,ord);

WITH c AS (
  INSERT INTO public."TourCourse" (user_id, title, share_yn, shared_password, status)
  VALUES (1, '경주 사진 명소 투어', true, 'OISO2026', 'PENDING') RETURNING tour_course_id
) INSERT INTO public."TourCourseItem" (tour_course_id, spot_id, day_number, item_order, transport_type, status)
SELECT c.tour_course_id, v.spot_id, 1, v.ord, CASE WHEN v.ord=1 THEN NULL ELSE 'CAR' END, 'PENDING' FROM c CROSS JOIN (VALUES (32,1),(184,2),(131,3),(56,4)) v(spot_id,ord);

COMMIT;
