-- Reviewed menu names present in restaurant_menu_seed.sql.
-- Run after schema.sql / english_content_migration.sql and restaurant_menu_seed.sql.
-- Only empty English fields with an exact Korean source match are filled.
-- Prices, URLs, user-authored text, and existing English translations are untouched.
BEGIN;

CREATE TEMP TABLE oiso_menu_names (
    source_ko text PRIMARY KEY,
    english text NOT NULL
) ON COMMIT DROP;

INSERT INTO oiso_menu_names (source_ko, english) VALUES
    ('가정식 백반', 'Home-Style Korean Set Meal'),
    ('간장 찜닭', 'Soy Sauce-Braised Chicken'),
    ('간장게장', 'Soy Sauce-Marinated Crab'),
    ('갈비찜', 'Braised Beef Short Ribs'),
    ('갈비탕', 'Beef Short Rib Soup'),
    ('갈치조림', 'Braised Hairtail Fish'),
    ('감자전', 'Potato Pancake'),
    ('경주식 생 갈빗살(한우 1++ 100g)', 'Gyeongju-Style Fresh Hanwoo Rib Meat (Grade 1++, 100 g)'),
    ('경주식 양념 갈빗살(한우 1++ 100g)', 'Gyeongju-Style Marinated Hanwoo Rib Meat (Grade 1++, 100 g)'),
    ('고등어 구이', 'Grilled Mackerel'),
    ('고등어구이', 'Grilled Mackerel'),
    ('고사리육개장', 'Spicy Beef Soup with Bracken'),
    ('곰탕', 'Beef Bone Soup'),
    ('국물닭볶음탕', 'Spicy Braised Chicken Stew'),
    ('김밥', 'Gimbap (Seaweed Rice Rolls)'),
    ('김치찌개', 'Kimchi Stew'),
    ('낙지 비빔칼국수', 'Spicy Mixed Knife-Cut Noodles with Octopus'),
    ('냉면', 'Cold Buckwheat Noodles'),
    ('뉴욕 치즈케이크', 'New York Cheesecake'),
    ('뉴욕치즈케이크', 'New York Cheesecake'),
    ('닭볶음탕', 'Spicy Braised Chicken'),
    ('닭불고기', 'Chicken Bulgogi'),
    ('대게장순두부', 'Soft Tofu Stew with Snow Crab Tomalley'),
    ('돈곰탕', 'Pork Bone Soup'),
    ('동죽칼국수', 'Knife-Cut Noodles with Surf Clams'),
    ('돼지갈비찜', 'Braised Pork Ribs'),
    ('돼지국밥', 'Pork and Rice Soup'),
    ('돼지두루치기', 'Spicy Stir-Fried Pork'),
    ('돼지불고기', 'Pork Bulgogi'),
    ('된장찌개', 'Soybean Paste Stew'),
    ('드립커피', 'Drip Coffee'),
    ('들깨순두부', 'Soft Tofu Stew with Perilla Seeds'),
    ('디저트', 'Dessert'),
    ('떡갈비', 'Grilled Short Rib Patties'),
    ('막국수', 'Buckwheat Noodles'),
    ('망고치즈빙수', 'Mango and Cheese Shaved Ice'),
    ('매운 등갈비찜', 'Spicy Braised Pork Back Ribs'),
    ('매운밀면', 'Spicy Wheat Noodles'),
    ('메밀소바', 'Buckwheat Soba Noodles'),
    ('명란 까르보나라', 'Pollock Roe Carbonara'),
    ('모둠회', 'Assorted Sashimi'),
    ('모듬해물장', 'Assorted Marinated Seafood'),
    ('몸국', 'Pork and Gulfweed Soup (Momguk)'),
    ('물막국수', 'Buckwheat Noodles in Chilled Broth'),
    ('물밀면', 'Wheat Noodles in Chilled Broth'),
    ('물회', 'Chilled Raw Fish Soup'),
    ('밀면', 'Wheat Noodles'),
    ('밀크티', 'Milk Tea'),
    ('바닐라 크림 라떼', 'Vanilla Cream Latte'),
    ('바지락칼국수', 'Knife-Cut Noodles with Manila Clams'),
    ('버터 스콘', 'Butter Scone'),
    ('보리밥정식', 'Barley Rice Set Meal'),
    ('볶음밥', 'Fried Rice'),
    ('부추전', 'Chive Pancake'),
    ('불고기뚝배기', 'Bulgogi in an Earthenware Pot'),
    ('브런치', 'Brunch'),
    ('비빔국수', 'Spicy Mixed Noodles'),
    ('비빔막국수', 'Spicy Mixed Buckwheat Noodles'),
    ('비빔밀면', 'Spicy Mixed Wheat Noodles'),
    ('산채비빔밥', 'Wild Vegetable Bibimbap'),
    ('삼계탕', 'Ginseng Chicken Soup'),
    ('생바지락칼국수', 'Knife-Cut Noodles with Fresh Manila Clams'),
    ('생삼겹살', 'Fresh Pork Belly'),
    ('송이순두부찌개', 'Soft Tofu Stew with Pine Mushrooms'),
    ('수제 돈까스', 'Homemade Pork Cutlet'),
    ('수제 양념 돼지갈비(220g)', 'House-Marinated Pork Ribs (220 g)'),
    ('수플레 팬케이크', 'Souffle Pancakes'),
    ('순두부', 'Soft Tofu'),
    ('순두부찌개', 'Soft Tofu Stew'),
    ('숯불구이', 'Charcoal-Grilled Meat'),
    ('쉬림프 오이스터 파스타', 'Shrimp Pasta with Oyster Sauce'),
    ('쌈밥', 'Rice with Leaf Wraps (Ssambap)'),
    ('쑥부쟁이정식', 'Wild Aster Set Meal'),
    ('아메리카노', 'Americano'),
    ('아이스 아메리카노', 'Iced Americano'),
    ('아이스 초코', 'Iced Chocolate'),
    ('안동간고등어', 'Andong Salted Mackerel'),
    ('애플망고빙수', 'Apple Mango Shaved Ice'),
    ('양꼬치', 'Lamb Skewers'),
    ('양념게장', 'Spicy Marinated Raw Crab'),
    ('연잎밥 정식', 'Lotus Leaf Rice Set Meal'),
    ('오징어불고기', 'Spicy Stir-Fried Squid'),
    ('우리밀 손칼국수', 'Hand-Cut Korean Wheat Noodles'),
    ('육전', 'Pan-Fried Battered Beef'),
    ('육회', 'Korean Beef Tartare'),
    ('육회물회', 'Chilled Raw Beef Soup'),
    ('육회비빔밥', 'Beef Tartare Bibimbap'),
    ('전복죽', 'Abalone Porridge'),
    ('전복해물뚝배기', 'Abalone and Seafood Soup in an Earthenware Pot'),
    ('전통차', 'Traditional Tea'),
    ('제주흑돼지 연탄불고기 대', 'Briquette-Grilled Jeju Black Pork Bulgogi (Large)'),
    ('제주흑돼지 연탄불고기 중', 'Briquette-Grilled Jeju Black Pork Bulgogi (Medium)'),
    ('중화비빔밥', 'Chinese-Style Bibimbap'),
    ('짜장면', 'Noodles in Black Bean Sauce (Jajangmyeon)'),
    ('짬뽕', 'Spicy Seafood Noodle Soup (Jjamppong)'),
    ('초코초코 브라우니', 'Choco Choco Brownie'),
    ('추억의 팥빙수', 'Old-Fashioned Red Bean Shaved Ice'),
    ('카페 아메리카노', 'Caffe Americano'),
    ('카페라떼', 'Caffe Latte'),
    ('카페라테', 'Caffe Latte'),
    ('칼국수', 'Knife-Cut Noodle Soup'),
    ('커피', 'Coffee'),
    ('커피·음료', 'Coffee and Beverages'),
    ('콩국수', 'Noodles in Chilled Soybean Broth'),
    ('콩나물밥', 'Soybean Sprout Rice'),
    ('탕수육', 'Sweet and Sour Pork'),
    ('티라미수', 'Tiramisu'),
    ('페일에일 생맥주', 'Pale Ale on Tap'),
    ('페퍼로니 피자', 'Pepperoni Pizza'),
    ('피자', 'Pizza'),
    ('한우 갈비살(100g)', 'Hanwoo Beef Rib Meat (100 g)'),
    ('한우 명인 사골곰탕', 'Master''s Hanwoo Beef Bone Soup'),
    ('한우 명인 얼큰 육개장', 'Master''s Spicy Hanwoo Beef Soup'),
    ('한우 물회', 'Chilled Raw Hanwoo Beef Soup'),
    ('한우 양념갈비살(100g)', 'Marinated Hanwoo Beef Rib Meat (100 g)'),
    ('한우 육회', 'Hanwoo Beef Tartare'),
    ('한우 육회 비빔밥', 'Hanwoo Beef Tartare Bibimbap'),
    ('한우구이', 'Grilled Hanwoo Beef'),
    ('한우곰탕', 'Hanwoo Beef Bone Soup'),
    ('한우불고기', 'Hanwoo Beef Bulgogi'),
    ('한우생갈비', 'Fresh Hanwoo Beef Ribs'),
    ('한우육회', 'Hanwoo Beef Tartare'),
    ('한정식', 'Korean Set Meal'),
    ('해물순두부', 'Seafood Soft Tofu Stew'),
    ('해물칼국수', 'Seafood Knife-Cut Noodle Soup'),
    ('해물파전', 'Seafood and Scallion Pancake'),
    ('황남빵', 'Hwangnam Bread'),
    ('회·해산물', 'Sashimi and Seafood');

UPDATE public."NearbyPlaceMenu" AS menu
SET menu_name_en = names.english
FROM oiso_menu_names AS names
WHERE menu.menu_name = names.source_ko
  AND NULLIF(btrim(menu.menu_name_en), '') IS NULL;

UPDATE public."NearbyPlaceDetail" AS detail
SET first_menu_en = names.english
FROM oiso_menu_names AS names
WHERE detail.first_menu = names.source_ko
  AND NULLIF(btrim(detail.first_menu_en), '') IS NULL;

UPDATE public."NearbyPlaceDetail" AS detail
SET treat_menu_en = names.english
FROM oiso_menu_names AS names
WHERE detail.treat_menu = names.source_ko
  AND NULLIF(btrim(detail.treat_menu_en), '') IS NULL;

COMMIT;
