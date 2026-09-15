-- 전체 식당 메뉴 시드
-- 한국관광공사 detailInfo2로 이미 수집된 메뉴명·가격만 사용한다.
-- 메뉴 사진은 사용하지 않으며 image_url은 항상 NULL이다.

INSERT INTO public."NearbyPlaceMenu"
    AS target
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT
    p.tour_spot_nearby_course_id,
    d.first_menu,
    d.first_menu_price,
    NULL,
    COALESCE(NULLIF(d.menu_source, ''),
             'https://map.naver.com/p/search/' ||
             replace(replace(p.name, ' ', '%20'), '/', '%2F'))
FROM public."TourSpotNearbyPlace" p
JOIN public."NearbyPlaceDetail" d
  ON d.tour_spot_nearby_course_id = p.tour_spot_nearby_course_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
  AND d.first_menu IS NOT NULL
  AND btrim(d.first_menu) <> ''
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (batch 40)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('2816890', '블랙라떼', 6000::bigint, 'https://heecnic.bizw.kr/'),
        ('2816890', '히크닉 팬케이크', 13000::bigint, 'https://heecnic.bizw.kr/'),
        ('2816890', '컵케익', 3800::bigint, 'https://heecnic.bizw.kr/'),
        ('2816890', '점보쿠키', 3800::bigint, 'https://heecnic.bizw.kr/'),
        ('2816890', '아메리카노', 4500::bigint, 'https://heecnic.bizw.kr/'),
        ('2818721', '한식 코스', NULL::bigint, 'https://www.ktourmap.com/spotDetails.jsp?contentId=2818721'),
        ('2819789', '가정식 백반', NULL::bigint, 'https://www.ktourmap.com/spotDetails.jsp?contentId=2819789'),
        ('2820735', '아메리카노', NULL::bigint, 'https://m.tripinfo.co.kr/info.html?content_id=2820735&content_type_id=39'),
        ('2820735', '카페라떼', NULL::bigint, 'https://m.tripinfo.co.kr/info.html?content_id=2820735&content_type_id=39')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (batch 39)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('2807685', '국수', 5000::bigint, 'https://autoreserve.com/ko/kr/gyeongju-tomb-of-kim-yu-sin/korean-noodle'),
        ('2807685', '비빔국수', NULL::bigint, 'https://autoreserve.com/ko/kr/gyeongju-tomb-of-kim-yu-sin/korean-noodle'),
        ('2807805', '국수', 5000::bigint, 'https://autoreserve.com/ko/kr/gyeongju-tomb-of-kim-yu-sin/korean-noodle'),
        ('2807805', '비빔국수', NULL::bigint, 'https://autoreserve.com/ko/kr/gyeongju-tomb-of-kim-yu-sin/korean-noodle'),
        ('2807929', '물회', 18000::bigint, 'https://www.diningcode.com/profile.php?rid=oFvvu8qlLwGR'),
        ('2807929', '횟밥', 18000::bigint, 'https://www.diningcode.com/profile.php?rid=oFvvu8qlLwGR'),
        ('2807929', '전복죽', 20000::bigint, 'https://www.diningcode.com/profile.php?rid=oFvvu8qlLwGR'),
        ('2810867', '메밀소바', NULL::bigint, 'https://www.ktourmap.com/spotDetails.jsp?contentId=2810867'),
        ('2810867', '튀김', NULL::bigint, 'https://www.ktourmap.com/spotDetails.jsp?contentId=2810867')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (batch 38)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('2773625', '칼국수', NULL::bigint, 'https://ontrip.kr/travel-guides/details/2773625'),
        ('2773625', '부추전', NULL::bigint, 'https://ontrip.kr/travel-guides/details/2773625'),
        ('2778485', '석쇠구이', NULL::bigint, 'https://www.gyeongju.go.kr/tour'),
        ('2778485', '된장찌개', NULL::bigint, 'https://www.gyeongju.go.kr/tour'),
        ('2805190', '한우 갈비', NULL::bigint, 'https://paran2020.tistory.com/m/7109'),
        ('2805190', '갈비살', NULL::bigint, 'https://paran2020.tistory.com/m/7109'),
        ('2805190', '된장찌개', NULL::bigint, 'https://paran2020.tistory.com/m/7109'),
        ('2807684', '한식 정식', NULL::bigint, 'https://www.ktourmap.com/spotDetails.jsp?contentId=2807684')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (batch 37)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('2766530', '해물순두부', NULL::bigint, 'https://zvwhj2141.tistory.com/2309'),
        ('2766530', '두부스테이크', 9000::bigint, 'https://zvwhj2141.tistory.com/2309'),
        ('2766530', '들깨순두부', NULL::bigint, 'https://zvwhj2141.tistory.com/2309'),
        ('2766530', '한방보쌈', NULL::bigint, 'https://zvwhj2141.tistory.com/2309'),
        ('2766532', '물막국수', 9000::bigint, 'https://www.foodbus.co.kr/r/%EA%B8%B0%EC%99%80%EB%A9%94%EB%B0%80%EB%A7%89%EA%B5%AD%EC%88%98/'),
        ('2766532', '비빔막국수', 9000::bigint, 'https://www.foodbus.co.kr/r/%EA%B8%B0%EC%99%80%EB%A9%94%EB%B0%80%EB%A7%89%EA%B5%AD%EC%88%98/'),
        ('2766532', '곰국시', 10000::bigint, 'https://www.foodbus.co.kr/r/%EA%B8%B0%EC%99%80%EB%A9%94%EB%B0%80%EB%A7%89%EA%B5%AD%EC%88%98/'),
        ('2766532', '수육 소', 18000::bigint, 'https://www.foodbus.co.kr/r/%EA%B8%B0%EC%99%80%EB%A9%94%EB%B0%80%EB%A7%89%EA%B5%AD%EC%88%98/'),
        ('2766532', '수육 중', 27000::bigint, 'https://www.foodbus.co.kr/r/%EA%B8%B0%EC%99%80%EB%A9%94%EB%B0%80%EB%A7%89%EA%B5%AD%EC%88%98/'),
        ('2766532', '수육 대', 36000::bigint, 'https://www.foodbus.co.kr/r/%EA%B8%B0%EC%99%80%EB%A9%94%EB%B0%80%EB%A7%89%EA%B5%AD%EC%88%98/'),
        ('2766548', '맛자랑정식', NULL::bigint, 'https://www.tabling.co.kr/restaurant/6310'),
        ('2766548', '찰보리 한우떡갈비정식', NULL::bigint, 'https://www.tabling.co.kr/restaurant/6310'),
        ('2773381', '짬뽕', NULL::bigint, 'https://heimishjm.tistory.com/318'),
        ('2773381', '돼지낙지볶음', NULL::bigint, 'https://heimishjm.tistory.com/318')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (batch 36)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('2762727', '한정식', NULL::bigint, 'https://www.ktourmap.com/spotDetails.jsp?contentId=2762727'),
        ('2762727', '떡갈비', NULL::bigint, 'https://www.ktourmap.com/spotDetails.jsp?contentId=2762727'),
        ('2762860', '교동쌈밥 A (한우)', 19000::bigint, 'https://badaon.or.kr/seantour_map/travel/destination/detail.do?destId=DEST000677'),
        ('2762860', '교동쌈밥 B (오리)', 16000::bigint, 'https://badaon.or.kr/seantour_map/travel/destination/detail.do?destId=DEST000677'),
        ('2762860', '교동쌈밥 C (돼지)', 15000::bigint, 'https://badaon.or.kr/seantour_map/travel/destination/detail.do?destId=DEST000677'),
        ('2762860', '곤달비 비빔밥', 11000::bigint, 'https://badaon.or.kr/seantour_map/travel/destination/detail.do?destId=DEST000677'),
        ('2765063', '황남빵 30개입', 42000::bigint, 'https://hwangnam.com/product/황남빵-2호/16/'),
        ('2765375', '아메리카노', 4500::bigint, 'https://kimdj1989.kr/308'),
        ('2765375', '카페라떼', 5000::bigint, 'https://kimdj1989.kr/308'),
        ('2765375', '카푸치노', 5000::bigint, 'https://kimdj1989.kr/308'),
        ('2765375', '바닐라라떼', 5500::bigint, 'https://kimdj1989.kr/308')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (batch 35)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('2736715', '바지락칼국수', NULL::bigint, 'https://www.ktourmap.com/spotDetails.jsp?contentId=2736715'),
        ('2736715', '해물파전', NULL::bigint, 'https://www.ktourmap.com/spotDetails.jsp?contentId=2736715'),
        ('2736719', '명품감포물회(일반)', 17000::bigint, 'https://www.tabling.co.kr/place/677cccb266de5f06987f95b1'),
        ('2736719', '명품감포물회(스페셜)', 22000::bigint, 'https://www.tabling.co.kr/place/677cccb266de5f06987f95b1'),
        ('2736719', '포항식비빔물회', 17000::bigint, 'https://www.tabling.co.kr/place/677cccb266de5f06987f95b1'),
        ('2736719', '전복물회', 25000::bigint, 'https://www.tabling.co.kr/place/677cccb266de5f06987f95b1'),
        ('2736719', '회덮밥', 17000::bigint, 'https://www.tabling.co.kr/place/677cccb266de5f06987f95b1'),
        ('2736721', '매콤 소갈비찜', 29000::bigint, 'https://megojigo.com/foodmap/14705'),
        ('2736721', '돼지갈비찜', 20000::bigint, 'https://megojigo.com/foodmap/14705'),
        ('2736721', '매운 돼지갈비찜', 22000::bigint, 'https://megojigo.com/foodmap/14705'),
        ('2736721', '한우육회비빔밥', 14000::bigint, 'https://megojigo.com/foodmap/14705'),
        ('2736721', '갈비탕', 15000::bigint, 'https://megojigo.com/foodmap/14705'),
        ('2736721', '한우물회', 14000::bigint, 'https://megojigo.com/foodmap/14705'),
        ('2736721', '한우수제 떡갈비', 15000::bigint, 'https://megojigo.com/foodmap/14705'),
        ('2736721', '순두부', 12000::bigint, 'https://megojigo.com/foodmap/14705'),
        ('2736721', '한우 육회', 27000::bigint, 'https://megojigo.com/foodmap/14705'),
        ('2738038', '고추장불고기', NULL::bigint, 'https://www.diningcode.com/profile.php?rid=w6nvuF0Ca3AM'),
        ('2738038', '청국순두부', NULL::bigint, 'https://www.diningcode.com/profile.php?rid=w6nvuF0Ca3AM'),
        ('2738038', '비지찌개', NULL::bigint, 'https://www.diningcode.com/profile.php?rid=w6nvuF0Ca3AM'),
        ('2738038', '고등어구이', NULL::bigint, 'https://www.diningcode.com/profile.php?rid=w6nvuF0Ca3AM')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (batch 34)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('2736677', '떡갈비', NULL::bigint, 'https://www.ktourmap.com/spotDetails.jsp?contentId=2736677'),
        ('2736679', '가정식 백반', NULL::bigint, 'https://www.ktourmap.com/spotDetails.jsp?contentId=2736679'),
        ('2736687', '정식', 13000::bigint, 'https://www.injangsa.com/place/143'),
        ('2736687', '닭볶음탕', NULL::bigint, 'https://www.injangsa.com/place/143'),
        ('2736687', '김치찌개', NULL::bigint, 'https://www.injangsa.com/place/143'),
        ('2736691', '한우갈비살양념구이', 22000::bigint, 'https://m.menupan.com/restaurant/onepage.asp?acode=H702338'),
        ('2736691', '한우갈비살소금구이', 22000::bigint, 'https://m.menupan.com/restaurant/onepage.asp?acode=H702338'),
        ('2736691', '한우치마살양념구이', 25000::bigint, 'https://m.menupan.com/restaurant/onepage.asp?acode=H702338'),
        ('2736691', '육회', 15000::bigint, 'https://m.menupan.com/restaurant/onepage.asp?acode=H702338'),
        ('2736691', '냉면', 6000::bigint, 'https://m.menupan.com/restaurant/onepage.asp?acode=H702338')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (batch 33)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('2736664', '보리밥정식', 10000::bigint, 'https://m.menupan.com/restaurant/onepage.asp?acode=H702335'),
        ('2736664', '갈비찜정식', 17000::bigint, 'https://m.menupan.com/restaurant/onepage.asp?acode=H702335'),
        ('2736664', '청국장', 10000::bigint, 'https://m.menupan.com/restaurant/onepage.asp?acode=H702335'),
        ('2736667', '정수가성 한정식', 38000::bigint, 'https://visitgyeongju.or.kr/kr/cuisine/view/535f4040060409460947024645514a444a'),
        ('2736667', '국내산 암꽃게 간장게장', 29000::bigint, 'https://visitgyeongju.or.kr/kr/cuisine/view/535f4040060409460947024645514a444a'),
        ('2736667', '소갈비찜', 29000::bigint, 'https://visitgyeongju.or.kr/kr/cuisine/view/535f4040060409460947024645514a444a'),
        ('2736670', '소갈비찜', 14900::bigint, 'https://pandarank.net/contents/693a7c58a37959408776cb7c'),
        ('2736670', '감태주먹밥', 5500::bigint, 'https://pandarank.net/contents/693a7c58a37959408776cb7c'),
        ('2736673', '경주육회물회', 15900::bigint, 'https://kfoodpicks.com/gyeongbuk/gyeongju-hyanghwajeong-cockle-bibimbap-review/'),
        ('2736673', '경주육회비빔밥', 15900::bigint, 'https://kfoodpicks.com/gyeongbuk/gyeongju-hyanghwajeong-cockle-bibimbap-review/'),
        ('2736673', '해물파전', 16000::bigint, 'https://kfoodpicks.com/gyeongbuk/gyeongju-hyanghwajeong-cockle-bibimbap-review/')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (batch 32)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('2014746', '연잎 한정식', 13000::bigint, 'https://www.daily.co.kr/life3128162222'),
        ('2014746', '원효반상', 28000::bigint, 'https://www.daily.co.kr/life3128162222'),
        ('2014746', '선덕반상', 28000::bigint, 'https://www.daily.co.kr/life3128162222'),
        ('2736660', '한우 육회비빔밥', NULL::bigint, 'https://info.koreacharts.com/tour/2736660/contents.html'),
        ('2736660', '한우 물회', NULL::bigint, 'https://info.koreacharts.com/tour/2736660/contents.html'),
        ('2736660', '한우 떡갈비 정식', NULL::bigint, 'https://info.koreacharts.com/tour/2736660/contents.html'),
        ('2736660', '명품 한우 갈비탕', NULL::bigint, 'https://info.koreacharts.com/tour/2736660/contents.html')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (batch 31)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('1827807', '낙지삼겹살', 7000::bigint, 'https://onpc.kr/3314628/?PageSpeed=noscript'),
        ('1878721', '랑산밥상', 13000::bigint, 'https://3.violet097.com/entry/%ED%95%9C%EA%B5%AD%EA%B8%B0%ED%96%89-%EA%B2%BD%EC%A3%BC-%EB%91%90%EB%B6%80%EC%82%BC%ED%95%A9'),
        ('1878721', '고두반밥상', 18000::bigint, 'https://3.violet097.com/entry/%ED%95%9C%EA%B5%AD%EA%B8%B0%ED%96%89-%EA%B2%BD%EC%A3%BC-%EB%91%90%EB%B6%80%EC%82%BC%ED%95%A9'),
        ('1878721', '두부삼합', 25000::bigint, 'https://www.yeongnam.com/web/view.php?key=20120510.010080731220001'),
        ('1878721', '고두반수라상', 38000::bigint, 'https://3.violet097.com/entry/%ED%95%9C%EA%B5%AD%EA%B8%B0%ED%96%89-%EA%B2%BD%EC%A3%BC-%EB%91%90%EB%B6%80%EC%82%BC%ED%95%A9')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- 상세 메뉴가 비어 있는 식당도 최소 대표 메뉴 행을 보장한다.
-- 실제 가격을 확인하지 못한 경우에는 NULL을 유지한다.
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id,
       COALESCE(NULLIF(btrim(d.first_menu), ''),
                NULLIF(btrim(d.treat_menu), '')),
       COALESCE(d.first_menu_price, d.treat_menu_price),
       NULL,
       COALESCE(NULLIF(d.menu_source, ''),
                'https://map.naver.com/p/search/' ||
                replace(replace(p.name, ' ', '%20'), '/', '%2F'))
FROM public."TourSpotNearbyPlace" p
LEFT JOIN public."NearbyPlaceDetail" d
  ON d.tour_spot_nearby_course_id = p.tour_spot_nearby_course_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
  AND NOT EXISTS (
      SELECT 1 FROM public."NearbyPlaceMenu" m
      WHERE m.tour_spot_nearby_course_id = p.tour_spot_nearby_course_id
  )
  AND (NULLIF(btrim(d.first_menu), '') IS NOT NULL
       OR NULLIF(btrim(d.treat_menu), '') IS NOT NULL)
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO NOTHING;

-- Verified restaurant menu seed (batch 30)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('3456352', '르씨엘 블렌드 핸드드립', NULL::bigint, 'https://ontrip.kr/travel-guides/details/3456352'),
        ('3456352', '아몬드 크림 모카', NULL::bigint, 'https://ontrip.kr/travel-guides/details/3456352'),
        ('3456352', '말차 샷 라떼', NULL::bigint, 'https://ontrip.kr/travel-guides/details/3456352'),
        ('3458332', '아메리카노', 5500::bigint, 'https://polle.com/place/4PGNME/%EC%9D%B8%ED%84%B0%ED%8F%B4%20%EC%BB%A4%ED%94%BC'),
        ('3458332', '드립커피', 8000::bigint, 'https://polle.com/place/4PGNME/%EC%9D%B8%ED%84%B0%ED%8F%B4%20%EC%BB%A4%ED%94%BC'),
        ('3458332', '카페라떼', 6500::bigint, 'https://polle.com/place/4PGNME/%EC%9D%B8%ED%84%B0%ED%8F%B4%20%EC%BB%A4%ED%94%BC'),
        ('3458332', '추억의 팥빙수', 15000::bigint, 'https://polle.com/place/4PGNME/%EC%9D%B8%ED%84%B0%ED%8F%B4%20%EC%BB%A4%ED%94%BC'),
        ('4063506', '경주식 생 갈빗살(한우 1++ 100g)', 25000::bigint, 'https://bomungalbi.com/home'),
        ('4063506', '경주식 양념 갈빗살(한우 1++ 100g)', 25000::bigint, 'https://bomungalbi.com/home'),
        ('4063506', '수제 양념 돼지갈비(220g)', 22000::bigint, 'https://bomungalbi.com/home'),
        ('4063506', '한우물회냉면', 14000::bigint, 'https://bomungalbi.com/home')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (batch 29)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('2989036', '대릉빵', NULL::bigint, 'https://www.ktourmap.com/spotDetails.jsp?contentId=2989036'),
        ('3006833', '아메리카노', NULL::bigint, 'https://www.ktourmap.com/spotDetails.jsp?contentId=3006833'),
        ('3006833', '카페라테', NULL::bigint, 'https://www.ktourmap.com/spotDetails.jsp?contentId=3006833'),
        ('3101705', '드립커피', NULL::bigint, 'https://www.ktriptips.com/kor/food/3101705'),
        ('3101705', '디저트', NULL::bigint, 'https://www.ktriptips.com/kor/food/3101705'),
        ('3396283', '시즌 티라미수', NULL::bigint, 'https://www.ktriptips.com/kor/food/3396283'),
        ('3396283', '베리 티라미수', NULL::bigint, 'https://www.ktriptips.com/kor/food/3396283'),
        ('3396283', '아몬드 티라미수', NULL::bigint, 'https://www.ktriptips.com/kor/food/3396283'),
        ('3444113', '데니시', NULL::bigint, 'https://www.ktriptips.com/kor/food/3444113'),
        ('3444113', '노네임', NULL::bigint, 'https://www.ktriptips.com/kor/food/3444113'),
        ('3444113', '풀드포크 그릴드샌드위치', NULL::bigint, 'https://www.ktriptips.com/kor/food/3444113'),
        ('3444369', '커피', NULL::bigint, 'https://www.ktriptips.com/kor/food/3444369')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- 한국관광공사에서 수집된 대표 취급메뉴도 보조 메뉴로 등록
INSERT INTO public."NearbyPlaceMenu"
    AS target
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT
    p.tour_spot_nearby_course_id,
    d.treat_menu,
    d.treat_menu_price,
    NULL,
    COALESCE(NULLIF(d.menu_source, ''),
             'https://map.naver.com/p/search/' ||
             replace(replace(p.name, ' ', '%20'), '/', '%2F'))
FROM public."TourSpotNearbyPlace" p
JOIN public."NearbyPlaceDetail" d
  ON d.tour_spot_nearby_course_id = p.tour_spot_nearby_course_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
  AND d.treat_menu IS NOT NULL
  AND btrim(d.treat_menu) <> ''
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = COALESCE(EXCLUDED.price, target.price),
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (batch 28)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('2907353', '간장 찜닭', 25000::bigint, 'https://www.siksinhot.com/theme/liking/459'),
        ('2907353', '고등어 구이', 6000::bigint, 'https://www.siksinhot.com/theme/liking/459'),
        ('2907353', '국물닭볶음탕', 27000::bigint, 'https://www.siksinhot.com/theme/liking/459'),
        ('2907353', '돼지두루치기', 25000::bigint, 'https://www.siksinhot.com/theme/liking/459'),
        ('2907353', '한우물회', 12000::bigint, 'https://www.siksinhot.com/theme/liking/459'),
        ('2932676', '돈곰탕', NULL::bigint, 'https://info.koreacharts.com/tour/2932676/contents.html'),
        ('2932676', '오륙돈 지짐', NULL::bigint, 'https://info.koreacharts.com/tour/2932676/contents.html'),
        ('2932676', '오륙돈 소육', NULL::bigint, 'https://info.koreacharts.com/tour/2932676/contents.html')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (batch 27)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('2907183', '오징어불고기', 14000::bigint, 'https://prolivingtips.com/jeon-hyun-moo-plan-season-3-11th-gyeongju-squid-bulgogi/'),
        ('2907183', '닭불고기', 14000::bigint, 'https://prolivingtips.com/jeon-hyun-moo-plan-season-3-11th-gyeongju-squid-bulgogi/'),
        ('2907183', '돼지불고기', 14000::bigint, 'https://prolivingtips.com/jeon-hyun-moo-plan-season-3-11th-gyeongju-squid-bulgogi/'),
        ('2907183', '돼지국밥', 9000::bigint, 'https://prolivingtips.com/jeon-hyun-moo-plan-season-3-11th-gyeongju-squid-bulgogi/'),
        ('2907327', '칼국수', NULL::bigint, 'https://tikntok.tistory.com/1981'),
        ('2907327', '김밥', NULL::bigint, 'https://ontrip.kr/travel-guides/details/2907327'),
        ('2907327', '부추전', NULL::bigint, 'https://ontrip.kr/travel-guides/details/2907327'),
        ('2907343', '곰탕', 13000::bigint, 'https://www.mugoong.com/ko/restaurants/classic-korean/gyeongju-neul-gomtang-timeless-warmth-deep-slow-simmered-traditional-korean-beef'),
        ('2907343', '한우 육회 비빔밥', 13000::bigint, 'https://www.mugoong.com/ko/restaurants/classic-korean/gyeongju-neul-gomtang-timeless-warmth-deep-slow-simmered-traditional-korean-beef')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (batch 26)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('2906673', '아메리카노', NULL::bigint, 'https://www.ktourmap.com/spotDetails.jsp?contentId=2906673'),
        ('2906690', '한식 정식', NULL::bigint, 'https://www.ktourmap.com/spotDetails.jsp?contentId=2906690'),
        ('2906715', '한우꽃갈비살', 25000::bigint, 'https://www.menupan.com/Restaurant/Onepage.asp?acode=H702345'),
        ('2906715', '수제떡갈비순두부찌개', 12000::bigint, 'https://www.menupan.com/Restaurant/Onepage.asp?acode=H702345'),
        ('2906715', '한우갈비살', 23000::bigint, 'https://www.menupan.com/Restaurant/Onepage.asp?acode=H702345'),
        ('2906745', '카페 아메리카노', 5700::bigint, 'https://www.diningcode.com/profile.php?rid=LCNkFvE1yvDD'),
        ('2906745', '카페라테', 5900::bigint, 'https://www.diningcode.com/profile.php?rid=LCNkFvE1yvDD'),
        ('2906745', '갸또 쇼콜라', 6900::bigint, 'https://www.diningcode.com/profile.php?rid=LCNkFvE1yvDD'),
        ('2906745', '뉴욕치즈케이크', 6500::bigint, 'https://www.diningcode.com/profile.php?rid=LCNkFvE1yvDD')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (batch 25)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('2905411', '티라미수', NULL::bigint, 'https://www.ktourmap.com/spotDetails.jsp?contentId=2905411'),
        ('2905411', '아메리카노', NULL::bigint, 'https://www.ktourmap.com/spotDetails.jsp?contentId=2905411'),
        ('2905411', '카페라테', NULL::bigint, 'https://www.ktourmap.com/spotDetails.jsp?contentId=2905411'),
        ('2905427', '모둠회', NULL::bigint, 'https://onpc.kr/6452352/'),
        ('2906650', '해물칼국수', NULL::bigint, 'https://www.ktourmap.com/spotDetails.jsp?contentId=2906650'),
        ('2906658', '모둠회', NULL::bigint, 'https://www.ktourmap.com/spotDetails.jsp?contentId=2906658')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (batch 24)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('2904285', '순두부찌개', NULL::bigint, 'https://koreatriptips.com/dining/2904285.html'),
        ('2904334', '아메리카노', 5000::bigint, 'https://www.diningcode.com/profile.php?rid=gIVoQc4wWacd'),
        ('2904334', '카페라떼', 5500::bigint, 'https://www.diningcode.com/profile.php?rid=gIVoQc4wWacd'),
        ('2904334', '망고치즈빙수', 18000::bigint, 'https://www.diningcode.com/profile.php?rid=gIVoQc4wWacd'),
        ('2904334', '바닐라 크림 라떼', 6800::bigint, 'https://www.diningcode.com/profile.php?rid=gIVoQc4wWacd'),
        ('2904353', '명란 까르보나라', 13900::bigint, 'https://realestate-k.tistory.com/405'),
        ('2904353', '쉬림프 오이스터 파스타', 13900::bigint, 'https://realestate-k.tistory.com/405'),
        ('2904373', '한우 주먹시(100g)', 30000::bigint, 'https://gjrang.kr/ko/food/hiconear.do?idx=623&pageIndex=1&serChk=food'),
        ('2904373', '한우 갈비살(100g)', 23000::bigint, 'https://gjrang.kr/ko/food/hiconear.do?idx=623&pageIndex=1&serChk=food'),
        ('2904373', '한우 양념갈비살(100g)', 23000::bigint, 'https://gjrang.kr/ko/food/hiconear.do?idx=623&pageIndex=1&serChk=food'),
        ('2904373', '육회', 30000::bigint, 'https://gjrang.kr/ko/food/hiconear.do?idx=623&pageIndex=1&serChk=food')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (batch 23)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('2904171', '고디탕', NULL::bigint, 'https://place.udanax.org/p/127564/%EC%95%88%EA%B0%95%ED%95%A0%EB%A7%88%EA%B3%A0%EB%94%94%ED%83%95'),
        ('2904191', '콩나물밥', NULL::bigint, 'https://momi.tistory.com/10?category=1005848'),
        ('2904216', '애플망고빙수', NULL::bigint, 'https://toronglife.tistory.com/498'),
        ('2904241', '육전', NULL::bigint, 'https://megojigo.com/foodmap/7118'),
        ('2904241', '육회비빔밥', NULL::bigint, 'https://megojigo.com/foodmap/7118')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (batch 22)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('2904007', '쌈밥', NULL::bigint, 'https://map.naver.com/p/search/%EA%B2%BD%EC%A3%BC%20%EC%8B%9C%EA%B3%A8%EC%8C%88%EB%B0%A5'),
        ('2904048', '낙지 비빔칼국수', 12000::bigint, 'https://www.biroso.kr/924?category=343498'),
        ('2904048', '생바지락칼국수', NULL::bigint, 'https://month.foodbank.co.kr/m/section/section_view.php?back=S&page=3&secIndex=6858&section=002014&section_list=restaurant.php'),
        ('2904048', '감자전', NULL::bigint, 'https://month.foodbank.co.kr/m/section/section_view.php?back=S&page=3&secIndex=6858&section=002014&section_list=restaurant.php'),
        ('2904076', '선덕반상', 25000::bigint, 'https://rubygarden.tistory.com/1517'),
        ('2904076', '쑥부쟁이정식', 20000::bigint, 'https://rubygarden.tistory.com/1517'),
        ('2904076', '구절초정식', 15000::bigint, 'https://rubygarden.tistory.com/1517'),
        ('2904076', '연잎밥 정식', 15000::bigint, 'https://rubygarden.tistory.com/1517'),
        ('2904103', '양꼬치', NULL::bigint, 'https://map.naver.com/p/search/%EA%B2%BD%EC%A3%BC%20%EC%95%84%EB%A6%AC%EB%9E%91')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (batch 21)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('2902567', '수플레 팬케이크', NULL::bigint, 'https://3jjo.tistory.com/405'),
        ('2902777', '피자', NULL::bigint, 'https://autoreserve.com/en/restaurants/aKUD95HWLv3wbvxMWyBk'),
        ('2902799', '1894 돌체라떼', NULL::bigint, 'https://www.gyeongju.go.kr/tour/page.do?cmd=2&con_uid=30&listType=list&mnu_uid=2501&pageNo=1&sortKwd=name&srchKwd='),
        ('2902799', '1894 아인슈페너', NULL::bigint, 'https://www.gyeongju.go.kr/tour/page.do?cmd=2&con_uid=30&listType=list&mnu_uid=2501&pageNo=1&sortKwd=name&srchKwd='),
        ('2902837', '송이순두부찌개', 16000::bigint, 'https://www.diningcode.com/profile.php?rid=TEtmNq9VkwyI'),
        ('2902837', '기본 순두부찌개', 13000::bigint, 'https://www.diningcode.com/profile.php?rid=TEtmNq9VkwyI')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (batch 20)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('2902305', '중화비빔밥', NULL::bigint, 'https://okayer.tistory.com/192'),
        ('2902305', '탕수육', NULL::bigint, 'https://okayer.tistory.com/192'),
        ('2902470', '안심카츠', NULL::bigint, 'https://map.naver.com/p/search/%EA%B2%BD%EC%A3%BC%20%ED%98%B8%EB%9E%91%EC%9D%B4%EC%B9%B4%EC%B8%A0'),
        ('2902488', '물밀면', 8000::bigint, 'https://doldoles.tistory.com/720'),
        ('2902488', '비빔밀면', 8000::bigint, 'https://doldoles.tistory.com/720'),
        ('2902488', '매운밀면', 9000::bigint, 'https://doldoles.tistory.com/720'),
        ('2902488', '제주흑돼지 연탄불고기 중', 13000::bigint, 'https://doldoles.tistory.com/720'),
        ('2902488', '제주흑돼지 연탄불고기 대', 20000::bigint, 'https://doldoles.tistory.com/720')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (batch 19)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('2902130', '몸국', NULL::bigint, 'https://megojigo.com/foodmap/6498'),
        ('2902130', '고사리육개장', NULL::bigint, 'https://megojigo.com/foodmap/6498'),
        ('2902130', '안동간고등어', NULL::bigint, 'https://megojigo.com/foodmap/6498'),
        ('2902200', '한우구이', NULL::bigint, 'https://map.naver.com/p/search/%EA%B2%BD%EC%A3%BC%20%ED%95%9C%EC%9A%B0114%EC%88%AF%EB%B6%88'),
        ('2902223', '회·해산물', NULL::bigint, 'https://map.naver.com/p/search/%EA%B2%BD%EC%A3%BC%20%ED%95%B4%EB%A7%9E%EC%9D%B4%ED%9A%8C%ED%83%80%EC%9A%B4'),
        ('2902264', '모듬해물장', NULL::bigint, 'https://sale.mimint.co.kr/bbs/view/news/S1N20/5199102')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (batch 18)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('2902092', '페퍼로니 피자', 20000::bigint, 'https://abalonestarmoon.tistory.com/33'),
        ('2902092', '페일에일 생맥주', 6000::bigint, 'https://enolja.com/r/116414'),
        ('2902110', '한우 육회 비빔밥', 14000::bigint, 'https://www.diningcode.com/profile.php?rid=uI1dFkbFj6ho'),
        ('2902110', '한우 물회', 14000::bigint, 'https://www.diningcode.com/profile.php?rid=uI1dFkbFj6ho'),
        ('2902110', '한우 명인 얼큰 육개장', 12000::bigint, 'https://www.diningcode.com/profile.php?rid=uI1dFkbFj6ho'),
        ('2902110', '한우 명인 사골곰탕', 11000::bigint, 'https://www.diningcode.com/profile.php?rid=uI1dFkbFj6ho'),
        ('2902110', '한우 궁중 떡갈비', 15000::bigint, 'https://www.diningcode.com/profile.php?rid=uI1dFkbFj6ho')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (batch 17)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('2876533', '짬뽕', NULL::bigint, 'https://emmaru.com/matzip/matzip.do?code=M150409122729035398I'),
        ('2876559', '우리밀 손칼국수', NULL::bigint, 'https://auri-story3.tistory.com/50'),
        ('2876577', '해물순두부', NULL::bigint, 'https://www.koreatriptips.com/dining/2876577.html'),
        ('2876577', '간장게장', NULL::bigint, 'https://www.koreatriptips.com/dining/2876577.html'),
        ('2876577', '양념게장', NULL::bigint, 'https://www.koreatriptips.com/dining/2876577.html'),
        ('2876611', '짬뽕', NULL::bigint, 'https://www.ktriptips.com/kor/food/2876611')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (batch 16)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('2876431', '짬뽕', NULL::bigint, 'https://map.naver.com/p/search/%EA%B2%BD%EC%A3%BC%20%EB%8F%99%EA%B6%81%EB%B0%98%EC%A0%90'),
        ('2876431', '짜장면', NULL::bigint, 'https://map.naver.com/p/search/%EA%B2%BD%EC%A3%BC%20%EB%8F%99%EA%B6%81%EB%B0%98%EC%A0%90'),
        ('2876449', '한우생갈비', 24000::bigint, 'https://epicure.co.kr/detail?shopId=3867'),
        ('2876449', '한우양념갈비', 24000::bigint, 'https://epicure.co.kr/detail?shopId=3867'),
        ('2876449', '한우갈빗살 소금구이', 24000::bigint, 'https://epicure.co.kr/detail?shopId=3867'),
        ('2876449', '한우갈빗살 양념구이', 24000::bigint, 'https://epicure.co.kr/detail?shopId=3867'),
        ('2876501', '어탕 손수제비', NULL::bigint, 'https://info.koreacharts.com/tour/2876501/contents.html')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (batch 15)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('2876409', '대게장순두부', NULL::bigint, 'https://m.tripinfo.co.kr/info.html?content_id=2876409&content_type_id=39'),
        ('2876409', '전복해물뚝배기', NULL::bigint, 'https://come-out-play.tistory.com/126'),
        ('2876409', '불고기뚝배기', NULL::bigint, 'https://come-out-play.tistory.com/126'),
        ('2876473', '아메리카노', 5800::bigint, 'https://adeukes.tistory.com/1040'),
        ('2876473', '밀크티', 6500::bigint, 'https://adeukes.tistory.com/1040'),
        ('2876473', '날이 좋아서 빵찌니', 8000::bigint, 'https://adeukes.tistory.com/1040'),
        ('2876473', '뉴욕 치즈케이크', 7500::bigint, 'https://adeukes.tistory.com/1040'),
        ('2876473', '초코초코 브라우니', 4500::bigint, 'https://adeukes.tistory.com/1040')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (batch 14)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('2876236', '따뜻한 콩국', 6000::bigint, 'https://funch-mom.tistory.com/148'),
        ('2876236', '생콩우거지탕', 9000::bigint, 'https://funch-mom.tistory.com/148'),
        ('2876236', '콩국수', 7000::bigint, 'https://theqoo.net/square/2128993033?category=24784'),
        ('2876287', '짬뽕', NULL::bigint, 'https://www.ktourmap.com/spotDetails.jsp?contentId=2876287'),
        ('2876287', '짜장면', NULL::bigint, 'https://www.ktourmap.com/spotDetails.jsp?contentId=2876287'),
        ('2876311', '전통차', NULL::bigint, 'https://injangsa.com/place/161'),
        ('2876348', '매운 등갈비찜', 18000::bigint, 'https://pujjell.tistory.com/86'),
        ('2876348', '볶음밥', 4000::bigint, 'https://pujjell.tistory.com/86')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (batch 13)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('2840919', '육회꼬막비빔밥', 29000::bigint, 'https://kr.trip.com/moments/detail/gyeongju-431-11423718/'),
        ('2840919', '육회물회', 13000::bigint, 'https://kr.trip.com/moments/detail/gyeongju-431-11423718/'),
        ('2840841', '브런치', NULL::bigint, 'https://www.diningcode.com/profile.php?rid=Cl3I4KUCWSug'),
        ('2840804', '커피·음료', NULL::bigint, 'https://map.naver.com/p/search/%EA%B2%BD%EC%A3%BC%20%EC%B9%B4%ED%8E%98%EC%86%94'),
        ('2840855', '숯불구이', NULL::bigint, 'https://map.naver.com/p/search/%EA%B2%BD%EC%A3%BC%20%ED%99%94%EC%82%B0%EC%88%AF%EB%B6%88%EC%86%90%EB%91%90%EB%B6%80')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (batch 12)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('2840767', '오션라떼', 7500::bigint, 'https://lovelykimsso.tistory.com/1277'),
        ('2840767', '엘더플라워에이드', 7500::bigint, 'https://lovelykimsso.tistory.com/1277'),
        ('2840767', '리얼딸기라떼', 7500::bigint, 'https://lovelykimsso.tistory.com/1277'),
        ('2840787', '버터 스콘', 4000::bigint, 'https://wanderlog.com/ko/place/details/3896648/%EC%B9%B4%ED%8E%98-%EB%A7%88%EB%A5%B4%EC%89%90'),
        ('2840787', '아메리카노', NULL::bigint, 'https://www.diningcode.com/profile.php?rid=tndbiNy8qOcD'),
        ('2840787', '얼그레이 파이', NULL::bigint, 'https://wanderlog.com/ko/place/details/3896648/%EC%B9%B4%ED%8E%98-%EB%A7%88%EB%A5%B4%EC%89%90')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (batch 11)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('2840513', '떡갈비', NULL::bigint, 'https://hagood1127.tistory.com/27'),
        ('2840523', '한우', NULL::bigint, 'https://www.kaa-edu.or.kr/online2019/down/2019KAA%26AAAs%20%20Conference-01.pdf'),
        ('2840523', '한우육회', NULL::bigint, 'https://m.tripinfo.co.kr/info.html?content_id=2840523&content_type_id=39'),
        ('2840523', '한우불고기전골', NULL::bigint, 'https://m.tripinfo.co.kr/info.html?content_id=2840523&content_type_id=39'),
        ('2840523', '한우육회비빔밥', NULL::bigint, 'https://m.tripinfo.co.kr/info.html?content_id=2840523&content_type_id=39'),
        ('2840523', '한우곰탕', NULL::bigint, 'https://m.tripinfo.co.kr/info.html?content_id=2840523&content_type_id=39'),
        ('2840523', '냉면정식', NULL::bigint, 'https://m.tripinfo.co.kr/info.html?content_id=2840523&content_type_id=39')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (batch 10)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('2840458', '아메리카노', NULL::bigint, 'https://www.oceang.kr/ko/village/experience/?i=206'),
        ('2840458', '맷돌흑임자라떼', NULL::bigint, 'https://www.oceang.kr/ko/village/experience/?i=206'),
        ('2840458', '수제레몬에이드', NULL::bigint, 'https://www.oceang.kr/ko/village/experience/?i=206'),
        ('2840471', '피자', NULL::bigint, 'https://www.diningcode.com/profile.php?rid=kOsRWE6iWpMB'),
        ('2840471', '파스타', NULL::bigint, 'https://hotplacehunter.co.kr/local/article/53325/amp/'),
        ('2840488', '커피', NULL::bigint, 'https://sanus.tistory.com/181'),
        ('2840506', '산드레 정식', 15000::bigint, 'https://visitgyeongju.or.kr/kr/cuisine/view/535f404006040946094603404d5144434f')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (batch 9)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('2840432', '아이스 아메리카노', 4800::bigint, 'https://review-review-review.tistory.com/340'),
        ('2840432', '필터커피', NULL::bigint, 'https://www.diningcode.com/profile.php?rid=XUQDPxmCPyg3'),
        ('2840439', '비프 웰링턴', NULL::bigint, 'https://www.diningcode.com/profile.php?rid=oZlSiXdChuYg'),
        ('2840439', '베이컨 크림뇨끼', NULL::bigint, 'https://www.diningcode.com/profile.php?rid=oZlSiXdChuYg'),
        ('2840447', '참가자미회 쌈밥', NULL::bigint, 'https://gloomy2.tistory.com/1052')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (batch 8)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('2839014', '소바', NULL::bigint, 'https://triple.guide/regions/8a836f07-82c6-48ca-be6a-7cb9741b6f6d/restaurants/2ad049e7-324a-4c36-8da3-e18198d7c33a'),
        ('2839014', '덮밥', NULL::bigint, 'https://triple.guide/regions/8a836f07-82c6-48ca-be6a-7cb9741b6f6d/restaurants/2ad049e7-324a-4c36-8da3-e18198d7c33a'),
        ('2839014', '후토마키', NULL::bigint, 'https://triple.guide/regions/8a836f07-82c6-48ca-be6a-7cb9741b6f6d/restaurants/2ad049e7-324a-4c36-8da3-e18198d7c33a'),
        ('2839062', '료코 도시락', 15000::bigint, 'https://k-sein.tistory.com/32?category=727311'),
        ('2839075', '한우구이', NULL::bigint, 'https://dasima4874.tistory.com/238'),
        ('2839097', '밀면', NULL::bigint, 'https://map.naver.com/p/search/%EA%B2%BD%EC%A3%BC%20%EB%B0%80%EB%A9%B4%EC%8B%9D%EB%8B%B9'),
        ('2839118', '커피·음료', NULL::bigint, 'https://map.naver.com/p/search/%EA%B2%BD%EC%A3%BC%20%EB%B0%94%EC%8B%A4%EB%9D%BC')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (batch 7)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('2824393', '고등어구이', NULL::bigint, 'https://hi-jsb.blog/food/ko/gyeongju-bulguksa-review'),
        ('2824393', '꼬막비빔밥', NULL::bigint, 'https://hi-jsb.blog/food/ko/gyeongju-bulguksa-review'),
        ('2831747', '수제 바닐라빈 라떼', 7000::bigint, 'https://monkeymasil.tistory.com/171'),
        ('2831747', '아이스 아메리카노', 5500::bigint, 'https://monkeymasil.tistory.com/171'),
        ('2831747', '아이스 초코', 7000::bigint, 'https://monkeymasil.tistory.com/171'),
        ('2831874', '긍정빵', NULL::bigint, 'https://the-kawai.tistory.com/entry/%EC%95%84%EB%8D%B4-%EA%B2%BD%EC%A3%BC-%EB%B3%B4%EB%AC%B8%ED%98%B8%EA%B0%80-%EB%B3%B4%EC%9D%B4%EB%8A%94-%EB%B3%B4%EB%AC%B8%EA%B4%80%EA%B4%91%EB%8B%A8%EC%A7%80%EC%9D%98-%EB%8C%80%ED%98%95-%ED%95%9C%EC%98%A5-%EC%B9%B4%ED%8E%98'),
        ('2831874', '달빛빵', NULL::bigint, 'https://the-kawai.tistory.com/entry/%EC%95%84%EB%8D%B4-%EA%B2%BD%EC%A3%BC-%EB%B3%B4%EB%AC%B8%ED%98%B8%EA%B0%80-%EB%B3%B4%EC%9D%B4%EB%8A%94-%EB%B3%B4%EB%AC%B8%EA%B4%80%EA%B4%91%EB%8B%A8%EC%A7%80%EC%9D%98-%EB%8C%80%ED%98%95-%ED%95%9C%EC%98%A5-%EC%B9%B4%ED%8E%98'),
        ('2831874', '경주커피', NULL::bigint, 'https://the-kawai.tistory.com/entry/%EC%95%84%EB%8D%B4-%EA%B2%BD%EC%A3%BC-%EB%B3%B4%EB%AC%B8%ED%98%B8%EA%B0%80-%EB%B3%B4%EC%9D%B4%EB%8A%94-%EB%B3%B4%EB%AC%B8%EA%B4%80%EA%B4%91%EB%8B%A8%EC%A7%80%EC%9D%98-%EB%8C%80%ED%98%95-%ED%95%9C%EC%98%A5-%EC%B9%B4%ED%8E%98'),
        ('2838676', '한우감태말이와 샐러드', 12500::bigint, 'https://www.tel-co.net/cafe/spot/11011')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (batch 6)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('2762860', '교동쌈밥 A (한우)', 19000::bigint, 'https://badaon.or.kr/seantour_map/travel/destination/detail.do?destId=DEST000677'),
        ('2762860', '교동쌈밥 B (오리)', 16000::bigint, 'https://badaon.or.kr/seantour_map/travel/destination/detail.do?destId=DEST000677'),
        ('2762860', '교동쌈밥 C (돼지)', 15000::bigint, 'https://badaon.or.kr/seantour_map/travel/destination/detail.do?destId=DEST000677'),
        ('2762860', '곤달비 비빔밥', 11000::bigint, 'https://badaon.or.kr/seantour_map/travel/destination/detail.do?destId=DEST000677'),
        ('2762860', '육부촌 육개장', 11000::bigint, 'https://badaon.or.kr/seantour_map/travel/destination/detail.do?destId=DEST000677'),
        ('2765375', '아메리카노', 4500::bigint, 'https://kimdj1989.kr/308'),
        ('2765375', '카페라떼', 5000::bigint, 'https://kimdj1989.kr/308'),
        ('2765375', '카푸치노', 5000::bigint, 'https://kimdj1989.kr/308'),
        ('2765375', '바닐라라떼', 5500::bigint, 'https://kimdj1989.kr/308'),
        ('2766532', '막국수', NULL::bigint, 'https://map.naver.com/p/search/%EA%B2%BD%EC%A3%BC%20%EA%B8%B0%EC%99%80%EB%A9%94%EB%B0%80%EB%A7%89%EA%B5%AD%EC%88%98'),
        ('2765063', '황남빵', NULL::bigint, 'https://hwangnam.com/product/%ED%99%A9%EB%82%A8%EB%B9%B5-2%ED%98%B8/16/')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (batch 5)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('2555977', '동죽칼국수', NULL::bigint, 'https://hyuntori83.tistory.com/1244?category=1059159'),
        ('2555977', '수제 돈까스', NULL::bigint, 'https://hyuntori83.tistory.com/1244?category=1059159'),
        ('2555977', '고구마 치즈 돈까스', NULL::bigint, 'https://hyuntori83.tistory.com/1244?category=1059159'),
        ('2717548', '아메리카노', NULL::bigint, 'https://m.tripinfo.co.kr/info.html?content_id=2717548&content_type_id=39'),
        ('2717548', '카페라떼', NULL::bigint, 'https://www.diningcode.com/profile.php?rid=Id1YztMnVJay'),
        ('2717548', '헤이즐넛라떼', NULL::bigint, 'https://www.diningcode.com/profile.php?rid=Id1YztMnVJay'),
        ('2717548', '허니자몽블랙티', NULL::bigint, 'https://m.tripinfo.co.kr/info.html?content_id=2717548&content_type_id=39'),
        ('2717548', '밤라떼', NULL::bigint, 'https://m.tripinfo.co.kr/info.html?content_id=2717548&content_type_id=39'),
        ('2730210', '조돌칼국수', NULL::bigint, 'https://map.naver.com/p/search/%EA%B2%BD%EC%A3%BC%20%EC%A1%B0%EB%8F%8C%EC%B9%BC%EA%B5%AD%EC%88%98'),
        ('2736657', '불국사밀면', NULL::bigint, 'https://map.naver.com/p/search/%EA%B2%BD%EC%A3%BC%20%EB%B6%88%EA%B5%AD%EC%82%AC%EB%B0%80%EB%A9%B4'),
        ('2736662', '낙지마실', NULL::bigint, 'https://map.naver.com/p/search/%EA%B2%BD%EC%A3%BC%20%EB%82%99%EC%A7%80%EB%A7%88%EC%8B%A4')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (batch 4)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('403845', '돼지갈비찜', NULL::bigint, 'https://jindduck-bubu.tistory.com/2'),
        ('403847', '맷돌순두부', 12000::bigint, 'https://www.siksinhot.com/theme/magazine/7823'),
        ('403847', '순두부찌개', 12000::bigint, 'https://www.siksinhot.com/theme/magazine/7823'),
        ('403847', '해물파전', 13000::bigint, 'https://www.siksinhot.com/theme/magazine/7823'),
        ('403847', '반모두부', 7000::bigint, 'https://www.siksinhot.com/theme/magazine/7823'),
        ('725787', '우렁이쌈밥', NULL::bigint, 'https://map.naver.com/p/search/%EA%B2%BD%EC%A3%BC%20%EC%9B%B0%EB%B9%99%ED%99%A9%ED%86%A0%EC%9A%B0%EB%A0%81%EC%9D%B4%EC%8C%88%EB%B0%A5'),
        ('855074', '한우불고기', NULL::bigint, 'http://www.munbok.co.kr/'),
        ('855074', '닭백숙', NULL::bigint, 'http://www.munbok.co.kr/')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT
    p.tour_spot_nearby_course_id,
    d.treat_menu,
    d.treat_menu_price,
    NULL,
    COALESCE(NULLIF(d.menu_source, ''),
             'https://map.naver.com/p/search/' ||
             replace(replace(p.name, ' ', '%20'), '/', '%2F'))
FROM public."TourSpotNearbyPlace" p
JOIN public."NearbyPlaceDetail" d
  ON d.tour_spot_nearby_course_id = p.tour_spot_nearby_course_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
  AND d.treat_menu IS NOT NULL
  AND btrim(d.treat_menu) <> ''
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- 기존에 잘못 저장된 식당 메뉴 이미지도 모두 제거한다.
UPDATE public."NearbyPlaceMenu" m
SET image_url = NULL,
    updated_at = now()
FROM public."TourSpotNearbyPlace" p
WHERE p.tour_spot_nearby_course_id = m.tour_spot_nearby_course_id
  AND upper(p.category) IN ('RESTAURANT', 'FOOD');

-- 수동 확인 메뉴·가격 보정 (사진 없음)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
VALUES
    (2, '랑산밥상', 10000, NULL, 'https://map.naver.com/p/search/%EA%B2%BD%EC%A3%BC%20%EA%B3%A0%EB%91%90%EB%B0%98'),
    (2, '고두반밥상', 13000, NULL, 'https://map.naver.com/p/search/%EA%B2%BD%EC%A3%BC%20%EA%B3%A0%EB%91%90%EB%B0%98'),
    (2, '두부삼합', 25000, NULL, 'https://map.naver.com/p/search/%EA%B2%BD%EC%A3%BC%20%EA%B3%A0%EB%91%90%EB%B0%98'),
    (4, '경주식 양념 갈빗살 100g', 25000, NULL, 'https://www.bomungalbi.com/faq'),
    (4, '생 갈빗살 100g', 25000, NULL, 'https://www.bomungalbi.com/faq'),
    (4, '수제 양념 돼지갈비 220g', 22000, NULL, 'https://www.bomungalbi.com/faq'),
    (4, '한우물회냉면', 14000, NULL, 'https://www.bomungalbi.com/faq'),
    (9, '천연벌꿀 탕수육', 18000, NULL, 'https://map.naver.com/p/search/%EA%B2%BD%EC%A3%BC%20%EC%B2%A8%EB%AF%B8'),
    (9, '육미 간짜장', 7000, NULL, 'https://map.naver.com/p/search/%EA%B2%BD%EC%A3%BC%20%EC%B2%A8%EB%AF%B8'),
    (9, '해물 짬뽕', 8500, NULL, 'https://map.naver.com/p/search/%EA%B2%BD%EC%A3%BC%20%EC%B2%A8%EB%AF%B8'),
    (14, '닭갈비', 12500, NULL, 'https://map.naver.com/p/search/%EA%B2%BD%EC%A3%BC%20%EC%82%B0%EA%B0%88%EB%9E%98%EB%8B%AD%EA%B0%88%EB%B9%84'),
    (14, '매운 닭갈비', 12500, NULL, 'https://map.naver.com/p/search/%EA%B2%BD%EC%A3%BC%20%EC%82%B0%EA%B0%88%EB%9E%98%EB%8B%AD%EA%B0%88%EB%B9%84'),
    (14, '치즈 추가', 4000, NULL, 'https://map.naver.com/p/search/%EA%B2%BD%EC%A3%BC%20%EC% 산갈래닭갈비'),
    (14, '볶음밥', 3000, NULL, 'https://map.naver.com/p/search/%EA%B2%BD%EC%A3%BC%20%EC% 산갈래닭갈비'),
    (18, '한우 갈비살 양념구이 110g', 27000, NULL, 'https://map.naver.com/p/search/%EA%B2%BD%EC%A3%BC%20%EC%98%81%EC%96%91%EC%88%AF%EB%B6%88%EA%B0%88%EB%B9%84'),
    (18, '한우 갈비살 소금구이 110g', 27000, NULL, 'https://map.naver.com/p/search/%EA%B2%BD%EC%A3%BC%20%EC%98%81%EC%96%91%EC%88%AF%EB%B6%88%EA%B0%88%EB%B9%84'),
    (18, '한우 치마살 양념구이 110g', 30000, NULL, 'https://map.naver.com/p/search/%EA%B2%BD%EC%A3%BC%20%EC%98%81%EC%96%91%EC%숯불%EA%B0%88%EB%B9%84'),
    (18, '한우 치마살 소금구이 110g', 30000, NULL, 'https://map.naver.com/p/search/%EA%B2%BD%EC%A3%BC%20%EC%98%81%EC%96%91%EC%88%AF%EB%B6%88%EA%B0%88%EB%B9%84')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price, image_url = NULL, source = EXCLUDED.source, updated_at = now();

UPDATE public."NearbyPlaceMenu"
SET source = 'https://map.naver.com/p/search/%EA%B2%BD%EC%A3%BC%20%EC%82%B0%EA%B0%88%EB%9E%98%EB%8B%AD%EA%B0%88%EB%B9%84'
WHERE tour_spot_nearby_course_id = 14;

-- Verified restaurant menu seed (batch 2)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('133964', '계절 한정식', NULL::bigint, 'https://www.siksinhot.com/P/255918'),
        ('133967', '한정식', NULL::bigint, 'https://www.tel-co.net/food/spot/5870'),
        ('133970', '석하정일품', 48000::bigint, 'https://fmr.purpleo.co.kr/view/23700'),
        ('133970', '정일품', 63000::bigint, 'https://fmr.purpleo.co.kr/view/23700'),
        ('134159', '구로쌈밥', NULL::bigint, 'https://autoreserve.com/ko/restaurants/edcfb3sGVdCMXXepA2oD')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (content_id based, prices remain NULL when not verified)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('132984', '삼계탕', NULL::bigint, 'https://www.busannavi.net/ko/8879'),
        ('133956', '차돌박이 된장찌개 정식', NULL::bigint, 'https://www.diningcode.com/profile.php?rid=xAU1CRoljWuB'),
        ('133956', '산채비빔밥', NULL::bigint, 'https://www.diningcode.com/profile.php?rid=xAU1CRoljWuB'),
        ('133958', '갈비찜', NULL::bigint, 'https://m.menupan.com/restaurant/onepage.asp?acode=H702394'),
        ('133958', '떡갈비', NULL::bigint, 'https://m.menupan.com/restaurant/onepage.asp?acode=H702394'),
        ('133962', '생삼겹살', 14000::bigint, 'https://m.kofood.kr/m_review.php?id=7986'),
        ('133962', '곱창전골', 25000::bigint, 'https://m.kofood.kr/m_review.php?id=7986'),
        ('133962', '청국장', 12000::bigint, 'https://m.kofood.kr/m_review.php?id=7986'),
        ('133962', '된장찌개', 12000::bigint, 'https://m.kofood.kr/m_review.php?id=7986')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();

-- Verified restaurant menu seed (batch 3)
WITH menu_data(content_id, menu_name, price, source) AS (
    VALUES
        ('134773', '갈치조림', NULL::bigint, 'https://gyeongju-si.com/bbs/board.php?bo_table=gyeongju_food&wr_id=10'),
        ('134964', '한우구이', NULL::bigint, 'https://megojigo.com/foodmap/1950'),
        ('135911', '석쇠 돼지불고기 쌈밥', NULL::bigint, 'https://www.ilyo.co.kr/?ac=article_view&entry_id=135191'),
        ('135911', '소불고기 쌈밥', NULL::bigint, 'https://www.ilyo.co.kr/?ac=article_view&entry_id=135191'),
        ('135911', '영양불고기 돌솥밥', NULL::bigint, 'https://www.ilyo.co.kr/?ac=article_view&entry_id=135191'),
        ('135911', '메밀전병', NULL::bigint, 'https://www.ilyo.co.kr/?ac=article_view&entry_id=135191'),
        ('135914', '순두부찌개', NULL::bigint, 'https://eatpick.kr/detail?id=82'),
        ('135914', '두부전골', NULL::bigint, 'https://eatpick.kr/detail?id=82')
)
INSERT INTO public."NearbyPlaceMenu"
    (tour_spot_nearby_course_id, menu_name, price, image_url, source)
SELECT p.tour_spot_nearby_course_id, m.menu_name, m.price, NULL, m.source
FROM public."TourSpotNearbyPlace" p
JOIN menu_data m ON p.content_id::text = m.content_id
WHERE upper(p.category) IN ('RESTAURANT', 'FOOD')
ON CONFLICT (tour_spot_nearby_course_id, menu_name) DO UPDATE
SET price = EXCLUDED.price,
    image_url = NULL,
    source = EXCLUDED.source,
    updated_at = now();
