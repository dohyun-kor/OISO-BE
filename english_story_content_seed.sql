-- Curated English content available from story_image_migration.sql.
-- Run after oiso/app/src/main/resources/schema.sql (or english_content_migration.sql).
-- This file is NOT run automatically by Spring. It changes only empty _en fields.
-- The source text is checked before every update; changed Korean copy is skipped.
-- No user-authored Memo or TourCourse text is translated here.
BEGIN;

CREATE TEMP TABLE oiso_story_names (
    source_ko text PRIMARY KEY,
    english text NOT NULL
) ON COMMIT DROP;

INSERT INTO oiso_story_names (source_ko, english) VALUES
    ('경주 첨성대', 'Cheomseongdae Observatory'),
    ('국립경주박물관', 'Gyeongju National Museum'),
    ('경주역사유적지구 [유네스코 세계유산]', 'Gyeongju Historic Areas (UNESCO World Heritage)'),
    ('경주 교촌마을', 'Gyeongju Gyochon Traditional Village'),
    ('경주 계림', 'Gyerim Forest'),
    ('경주 월성', 'Gyeongju Wolseong Palace Site'),
    ('경주 석굴암 [유네스코 세계유산]', 'Seokguram Grotto (UNESCO World Heritage)'),
    ('보문호', 'Bomun Lake'),
    ('경주 양동마을 [유네스코 세계유산]', 'Gyeongju Yangdong Village (UNESCO World Heritage)'),
    ('경주 불국사 [유네스코 세계유산]', 'Bulguksa Temple (UNESCO World Heritage)'),
    ('경주 대릉원 일원', 'Gyeongju Daereungwon Ancient Tomb Complex'),
    ('월정교', 'Woljeonggyo Bridge'),
    ('경북천년숲정원', 'Gyeongbuk Millennium Forest Garden'),
    ('경주 오릉', 'Five Royal Tombs of Gyeongju'),
    ('경주 동궁원', 'Gyeongju Donggungwon'),
    ('황룡사역사문화관', 'Hwangnyongsa Temple History and Culture Hall'),
    ('경주 양남 주상절리 전망대', 'Yangnam Columnar Joints Observatory'),
    ('경주 감은사지', 'Gameunsa Temple Site'),
    ('경주국립공원', 'Gyeongju National Park'),
    ('경주 양남 주상절리군', 'Yangnam Columnar Joints'),
    ('경주세계문화엑스포 기념관', 'Gyeongju World Culture Expo Memorial Hall');

UPDATE public."TourStory" AS story
SET title_en = names.english
FROM oiso_story_names AS names
WHERE story.title = names.source_ko
  AND NULLIF(btrim(story.title_en), '') IS NULL;

UPDATE public."TourSpot" AS spot
SET name_en = names.english
FROM oiso_story_names AS names
WHERE spot.name = names.source_ko
  AND NULLIF(btrim(spot.name_en), '') IS NULL;

CREATE TEMP TABLE oiso_story_heading (
    source_ko text PRIMARY KEY,
    english text NOT NULL
) ON COMMIT DROP;

INSERT INTO oiso_story_heading (source_ko, english) VALUES
    ('경주의 밤을 머무는 방법', 'How to Spend the Night in Gyeongju'),
    ('기록이 다시 들려주는 경주', 'Gyeongju Retold Through Records'),
    ('달빛 아래 되살아나는 신라 왕궁', 'A Silla Palace Reawakened by Moonlight'),
    ('돌과 길에 남은 믿음의 시간', 'Faith Preserved in Stone and Paths'),
    ('마을 언덕에 이어지는 작은 기도처', 'A Small Place of Prayer on the Village Hill'),
    ('머무름으로 완성되는 여행', 'A Journey Made Complete by Staying'),
    ('바다가 빚어낸 경주의 한 장면', 'A Gyeongju Scene Shaped by the Sea'),
    ('바위 품에 앉은 할매부처', 'The Grandma Buddha Seated Among the Rocks'),
    ('봉분 아래 이어지는 신라의 기억', 'Memories of Silla Beneath the Burial Mounds'),
    ('빛으로 다시 피어난 신라의 정원', 'Silla''s Garden Brought Back to Light'),
    ('신라의 정원이 오늘에 이어지는 곳', 'Where Silla''s Garden Lives On'),
    ('오늘의 경주가 축제가 되는 순간', 'When Gyeongju Becomes a Celebration'),
    ('왕릉 뒤 구릉에 겹쳐진 신라의 시간', 'Layers of Silla History Behind the Royal Tomb'),
    ('이곳에서 시작되는 경주의 이야기', 'Where the Story of Gyeongju Begins'),
    ('자연이 들려주는 느린 이야기', 'Nature''s Unhurried Stories'),
    ('직접 경험하며 기억하는 하루', 'A Day to Experience and Remember'),
    ('청소년의 오늘을 응원하는 공간', 'A Place That Supports Young People Today'),
    ('한 사람을 위한 기도가 사찰이 되다', 'A Prayer for One Person Becomes a Temple'),
    ('흔적을 따라 복원하는 시간', 'Tracing the Past, Restoring Its Stories');

UPDATE public."TourStory" AS story
SET story_title_en = heading.english
FROM oiso_story_heading AS heading
WHERE story.story_title = heading.source_ko
  AND NULLIF(btrim(story.story_title_en), '') IS NULL;

-- The source card sub-title is usually "<Korean title>, <shared phrase>".
-- Build English only when both the exact source pattern and reviewed title_en exist.
CREATE TEMP TABLE oiso_story_subtitle_suffix (
    source_ko text PRIMARY KEY,
    english text NOT NULL
) ON COMMIT DROP;

INSERT INTO oiso_story_subtitle_suffix (source_ko, english) VALUES
    ('경주 여행에 새로운 장면을 더하는 곳', 'A New Side of Gyeongju to Discover'),
    ('계절의 빛과 바람을 가까이 만나는 곳', 'Where the Seasons'' Light and Breeze Feel Close'),
    ('고요한 풍경에 깃든 오래된 믿음', 'Ancient Faith in a Peaceful Landscape'),
    ('땅 위의 흔적으로 되살아나는 옛 경주', 'Old Gyeongju Reborn in the Traces Left Behind'),
    ('부드러운 능선 아래 잠든 신라의 시간', 'Silla''s Past Beneath Gentle Hills'),
    ('사람과 문화가 어우러지는 경주의 하루', 'A Day Where Gyeongju''s People and Culture Meet'),
    ('여행의 여운을 편안하게 이어가는 곳', 'A Place to Linger After the Journey'),
    ('움직임과 즐거움으로 채우는 경주 여행', 'A Gyeongju Trip Full of Energy and Fun'),
    ('자연 가까이 머무는 경주의 하룻밤', 'A Night Close to Nature in Gyeongju'),
    ('전시와 기록으로 만나는 경주의 이야기', 'Gyeongju''s Story Through Exhibits and Records'),
    ('파도와 바람이 펼쳐 놓은 경주의 해안', 'Gyeongju''s Coast of Waves and Wind');

UPDATE public."TourStory" AS story
SET sub_title_en = story.title_en || ': ' || suffix.english
FROM oiso_story_subtitle_suffix AS suffix
WHERE story.sub_title = story.title || ', ' || suffix.source_ko
  AND NULLIF(btrim(story.title_en), '') IS NOT NULL
  AND NULLIF(btrim(story.sub_title_en), '') IS NULL;

-- Fully reviewed long-form copy for six major sites in the repository seed.
-- Each English value is written only if that particular Korean field is unchanged.
CREATE TEMP TABLE oiso_story_copy (
    spot_id bigint NOT NULL,
    field_name text NOT NULL CHECK (field_name IN ('summary', 'intro', 'story', 'tip', 'did_you_know')),
    source_ko text NOT NULL,
    english text NOT NULL,
    PRIMARY KEY (spot_id, field_name)
) ON COMMIT DROP;

INSERT INTO oiso_story_copy (spot_id, field_name, source_ko, english) VALUES
    (5, 'summary',
     $$첨성대는 신라 제27대 선덕여왕 때 건립된 것으로 추정되며 경주를 상징하는 랜드마크 중 하나이다.$$
     , $$Cheomseongdae is thought to have been built during the reign of Queen Seondeok, Silla's 27th ruler, and is one of Gyeongju's best-known landmarks.$$),
    (5, 'intro',
     $$첨성대는 신라 제27대 선덕여왕 때 건립된 것으로 추정되며 경주를 상징하는 랜드마크 중 하나이다. 받침대 역할을 하는 기단부(基壇部) 위에 술병 모양의 원통부(圓筒部)를 올리고 맨 위에 정(井) 자형의 정상부(頂上部)를 얹은 모습으로 높이는 약 9m이다.$$
     , $$Cheomseongdae is thought to have been built during the reign of Queen Seondeok, Silla's 27th ruler, and is one of Gyeongju's best-known landmarks. About nine meters tall, it has a bottle-shaped cylindrical body on a base and a top formed by long stones arranged in a square lattice.$$),
    (5, 'story',
     $$원통부는 부채꼴 모양의 돌로 27단을 쌓아 올렸다.
남동쪽으로 난 창을 중심으로 아래쪽은 막돌로 채워져 있고 위쪽은 정상까지 뚫려서 속이 비어 있다.
동쪽 절반이 판도라로 막혀있는 정상부는 정(井) 자 모양으로 맞물린 기다란 석재의 끝이 바깥까지 뚫고 나와있다.$$
     , $$The cylindrical body is built in 27 layers of fan-shaped stones. Below the southeast-facing window, the structure is filled with rough stones; above it, the interior is hollow all the way to the top. At the summit, long stones interlock in a square lattice, with their ends projecting beyond the outer wall.$$),
    (5, 'tip',
     $$해 질 무렵 첨성대에 도착해 밝을 때의 석조 구조를 먼저 살펴본 뒤 조명이 켜지는 모습을 기다려 보세요. 대릉원에서 첨성대를 거쳐 동궁과 월지로 이어 걸으면 낮 풍경에서 야경으로 자연스럽게 연결돼요.$$
     , $$Arrive near sunset to see Cheomseongdae's stonework in daylight, then stay for the lights to come on. A walk from Daereungwon through Cheomseongdae to Donggung Palace and Wolji Pond takes you naturally from daytime views into Gyeongju's night scenery.$$),
    (5, 'did_you_know',
     $$첨성대는 신라 제27대 선덕여왕 때 건립된 것으로 추정되며 경주를 상징하는 랜드마크 중 하나이에요.$$
     , $$Cheomseongdae is thought to date from the reign of Queen Seondeok, Silla's 27th ruler.$$),

    (7, 'summary',
     $$신라의 문화유산을 한눈에 살펴볼 수 있는 한국의 대표적인 박물관이다.$$
     , $$A leading Korean museum where visitors can explore Silla's cultural heritage in one place.$$),
    (7, 'intro',
     $$국립경주박물관은 신라의 문화유산을 한눈에 살펴볼 수 있는 한국의 대표적인 박물관이다. 이곳에서는 기원전 57년부터 기원후 935년까지 이어진 천년왕국 신라의 압축된 역사를 살펴볼 수 있다.$$
     , $$The Gyeongju National Museum is one of Korea's leading museums for Silla heritage. Its collections trace the kingdom's nearly thousand-year history, from 57 BCE to 935 CE.$$),
    (7, 'story',
     $$경주 여행 시 가장 먼저 방문해 사전 지식을 쌓은 뒤 유적지를 둘러보면 더욱 알찬 여행이 된다.
박물관은 신라역사관, 신라미술관, 월지관, 신라천년보고, 옥외전시 등 5개의 상설전시관과 특별전시관으로 구성된다.
신라역사관에서는 신라의 역사적 흐름을, 신라미술관에서는 찬란한 미술문화와 유물을 볼 수 있다.$$
     , $$Visit the museum early in your Gyeongju trip to gain context before exploring the historic sites. It has five permanent exhibition areas—the Silla History Gallery, Silla Art Gallery, Wolji Gallery, Silla Millennium Storehouse, and outdoor exhibits—as well as special exhibitions. The history gallery follows Silla's development, while the art gallery presents its art and artifacts.$$),
    (7, 'tip',
     $$국립경주박물관을 방문하기 전 최신 운영 정보와 이동 방법을 확인하세요. 현장 안내판을 먼저 읽고 주변까지 천천히 걸으면 이 장소만의 특징을 더 쉽게 발견할 수 있어요.$$
     , $$Check current opening information and directions before visiting the Gyeongju National Museum. Reading the on-site signs and exploring at an easy pace will help you notice more of what makes it special.$$),
    (7, 'did_you_know',
     $$이곳에서는 기원전 57년부터 기원후 935년까지 이어진 천년왕국 신라의 압축된 역사를 살펴볼 수 있어요.$$
     , $$The museum offers an overview of Silla's history, from 57 BCE to 935 CE.$$),

    (29, 'summary',
     $$석굴암(국보)은 신라 경덕왕 10년에 당시의 재상인 김대성에 의해 창건되었다고 전해진다.$$
     , $$Seokguram Grotto, a National Treasure, is said to have been founded by the Silla official Kim Dae-seong in the tenth year of King Gyeongdeok's reign.$$),
    (29, 'intro',
     $$석굴암(국보)은 신라 경덕왕 10년에 당시의 재상인 김대성에 의해 창건되었다고 전해진다. 석굴암의 구조는 방형과 원형, 직선과 곡선, 평면과 구면이 조화를 이루며, 특히 벽면을 따라 조각된 38구의 불상과 보살상은 어느 것 하나 걸작이 아닌 것이 없고 전체적인 조화를 통해 고도의 철학성과 과학적인 면모를 나타내고 있다.$$
     , $$Seokguram Grotto, a National Treasure, is said to have been founded by the Silla official Kim Dae-seong in the tenth year of King Gyeongdeok's reign. Its design brings together square and circular forms, straight lines and curves, and flat and rounded surfaces. Thirty-eight figures of Buddhas and bodhisattvas carved along the walls contribute to a harmonious work of art that reflects both philosophical and technical sophistication.$$),
    (29, 'story',
     $$중앙에는 백색 화강암으로 된 본존불인 여래좌상이 동해를 굽어보고 있으며, 여래좌상의 정면은 문무왕 수중릉인 봉길리 앞 대왕암을 향하고 있다.
불국사와 석굴암은 1995년 12월 6일 유네스코 세계문화유산으로 등재되어 보호되고 있다.
석굴암에서 바라보는 수평선 너머의 붉은 해와 바다가 어우러진 풍경은 장관을 이룬다.$$
     , $$At the center sits a white-granite Buddha facing the East Sea. The statue looks toward Daewangam Rock off Bonggil-ri, traditionally associated with the underwater tomb of King Munmu. Seokguram and Bulguksa Temple were inscribed on the UNESCO World Heritage List on December 6, 1995. From the grotto, the red sun rising over the sea creates a striking view.$$),
    (29, 'tip',
     $$경주 석굴암 [유네스코 세계유산]을 방문하기 전 최신 운영 정보와 이동 방법을 확인하세요. 현장 안내판을 먼저 읽고 주변까지 천천히 걸으면 이 장소만의 특징을 더 쉽게 발견할 수 있어요.$$
     , $$Check current opening information and directions before visiting Seokguram Grotto. Read the on-site signs and take your time exploring to appreciate the site more fully.$$),
    (29, 'did_you_know',
     $$석굴암(국보)은 신라 경덕왕 10년에 당시의 재상인 김대성에 의해 창건되었다고 전해져요.$$
     , $$Seokguram Grotto is said to have been founded by Kim Dae-seong in the tenth year of King Gyeongdeok's reign.$$),

    (35, 'summary',
     $$양동 민속마을은 조선시대 전통문화와 자연을 고스란히 간직하고 있는 한국 최대 규모의 집성촌으로 월성 손 씨와 여강 이 씨에 의해 형성되었다.$$
     , $$Yangdong Village, formed by the Wolseong Son and Yeogang Yi clans, is Korea's largest traditional clan village and preserves Joseon-era culture and its natural setting.$$),
    (35, 'intro',
     $$양동 민속마을은 조선시대 전통문화와 자연을 고스란히 간직하고 있는 한국 최대 규모의 집성촌으로 월성 손 씨와 여강 이 씨에 의해 형성되었다. 국보, 보물, 민속자료 등 많은 문화유산을 간직하고 있어 마을 전체가 국가민속문화유산으로 지정되었다.$$
     , $$Yangdong Village was formed by the Wolseong Son and Yeogang Yi clans. As Korea's largest traditional clan village, it preserves Joseon-era culture amid its natural landscape. Its many cultural treasures have led to the entire village being designated a nationally protected folk heritage site.$$),
    (35, 'story',
     $$마을의 규모 및 보존 상태, 문화유산의 수와 전통성, 그리고 아름다운 자연환경과 때 묻지 않은 향토성 등 볼거리가 많아 1993년 영국의 찰스 황태자도 이곳을 방문한 바 있다.
또한 옛 명문대가의 영광스러운 자취와 선조들의 삶이 배어있는 200년 이상 된 고가 54호가 보존되어 있어 조선 중기 이후의 다양하고 특색 있는 우리나라 전통가옥 구조를 한눈에 볼 수 있다.
사전에 마을의 배치와 코스를 파악하고 문화유산의 소재를 확인한 후 답사하는 것이 좋다.$$
     , $$The village's scale, preservation, cultural heritage, and natural setting have long drawn visitors; Prince Charles visited in 1993. Fifty-four houses more than 200 years old remain, offering a close look at traditional Korean architecture from the mid-Joseon period onward. Check the village layout and the locations of its heritage sites before you explore.$$),
    (35, 'tip',
     $$경주 양동마을 [유네스코 세계유산]을 방문하기 전 최신 운영 정보와 이동 방법을 확인하세요. 현장 안내판을 먼저 읽고 주변까지 천천히 걸으면 이 장소만의 특징을 더 쉽게 발견할 수 있어요.$$
     , $$Check current visitor information and directions before visiting Yangdong Village. Reading the signs and walking through the village at an easy pace will help you appreciate its character.$$),
    (35, 'did_you_know',
     $$국보, 보물, 민속자료 등 많은 문화유산을 간직하고 있어 마을 전체가 국가민속문화유산으로 지정되었다고 해요.$$
     , $$The entire village is nationally protected for its wealth of cultural heritage, including National Treasures and traditional folk materials.$$),

    (36, 'summary',
     $$불국사는 신라 법흥왕 15년에 어머니의 뜻에 따라 국가의 안녕과 백성의 평안을 기원하며 창건되었다.$$
     , $$Bulguksa Temple was founded in the fifteenth year of King Beopheung's reign, in accordance with his mother's wishes, to pray for the kingdom and its people.$$),
    (36, 'intro',
     $$불국사는 신라 법흥왕 15년에 어머니의 뜻에 따라 국가의 안녕과 백성의 평안을 기원하며 창건되었다. 경덕왕 10년인 751년에는 재상 김대성이 절을 다시 지어 현재의 규모로 정비하였다.$$
     , $$Bulguksa Temple was founded in the fifteenth year of King Beopheung's reign, in accordance with his mother's wishes, to pray for the kingdom and its people. In 751, the tenth year of King Gyeongdeok's reign, the official Kim Dae-seong rebuilt and expanded the temple.$$),
    (36, 'story',
     $$그러나 임진왜란을 거치며 건물과 문화유산 대부분이 소실되거나 약탈당하는 아픔을 겪었다.
이후 1920년대 전까지는 일부 건물과 탑만 황량하게 남아 있었으나, 지속적인 원형 복구와 보수 작업을 거쳐 현재의 모습을 되찾게 되었다.
현재는 국보 7점을 포함한 문화유산을 보유한 대사찰로 자리 잡고 있다.$$
     , $$Much of Bulguksa's architecture and cultural heritage was destroyed or looted during the Japanese invasions of Korea in the late sixteenth century. For centuries, only a few buildings and pagodas remained. Restoration and repairs have since brought the temple to its present form. Today, it is a major temple with numerous cultural treasures, including seven National Treasures.$$),
    (36, 'tip',
     $$경주 불국사 [유네스코 세계유산]을 방문하기 전 최신 운영 정보와 이동 방법을 확인하세요. 현장 안내판을 먼저 읽고 주변까지 천천히 걸으면 이 장소만의 특징을 더 쉽게 발견할 수 있어요.$$
     , $$Check current opening information and directions before visiting Bulguksa Temple. Read the on-site signs and take your time exploring its grounds.$$),
    (36, 'did_you_know',
     $$불국사는 신라 법흥왕 15년에 어머니의 뜻에 따라 국가의 안녕과 백성의 평안을 기원하며 창건되었다고 해요.$$
     , $$Bulguksa Temple is said to have been founded in the fifteenth year of King Beopheung's reign to pray for the kingdom's peace and prosperity.$$),

    (41, 'summary',
     $$란 이름은 「삼국사기」에 통일신라 경덕왕 19년 “궁궐 남쪽 문천에 월정교, 춘양교 두 다리를 놓았다.”는 기록을 통해 알려졌다.$$
     , $$The name Woljeonggyo appears in the Samguk Sagi, which records the construction of two bridges south of the palace in the nineteenth year of King Gyeongdeok's reign.$$),
    (41, 'intro',
     $$월정교란 이름은 「삼국사기」에 통일신라 경덕왕 19년 “궁궐 남쪽 문천에 월정교, 춘양교 두 다리를 놓았다.”는 기록을 통해 알려졌다. 조선시대에 유실되어 없어진 것을 10여 년간의 조사 및 고증과 복원을 진행해 2018년 4월 모든 복원을 완료했다.$$
     , $$The Samguk Sagi records that Woljeonggyo and Chunyanggyo bridges were built across the Muncheon Stream south of the palace in the nineteenth year of King Gyeongdeok's reign. Woljeonggyo was lost during the Joseon period. After more than a decade of research and reconstruction, its restoration was completed in April 2018.$$),
    (41, 'story',
     $$2013년 교량복원을 먼저 마치고, 이후 다리 양쪽의 문루(門樓)를 마저 지었다.
문루 2층에는 교량의 복원과정을 담은 영상물과 출토 유물을 볼 수 있는 전시관이 있다.
낮에는 월정교의 자태를 오롯이 볼 수 있어 좋고, 밤의 월정교는 또 다른 매력으로 경주의 야경 명소로 손꼽힌다.$$
     , $$The bridge itself was restored in 2013, followed by the gate pavilions at either end. An exhibition on the second floor of a pavilion shows a video of the reconstruction and artifacts found at the site. Visit by day to take in the bridge's form, or return after dark to see one of Gyeongju's best-known night views.$$),
    (41, 'tip',
     $$월정교을 방문하기 전 최신 운영 정보와 이동 방법을 확인하세요. 현장 안내판을 먼저 읽고 주변까지 천천히 걸으면 이 장소만의 특징을 더 쉽게 발견할 수 있어요.$$
     , $$Check current access information and directions before visiting Woljeonggyo Bridge. Read the on-site signs and take time to explore the surrounding area.$$),
    (41, 'did_you_know',
     $$월정교란 이름은 「삼국사기」에 통일신라 경덕왕 19년 “궁궐 남쪽 문천에 월정교, 춘양교 두 다리를 놓았다.”는 기록을 통해 알려졌다고 해요.$$
     , $$Woljeonggyo is named in the Samguk Sagi, which records the building of two bridges south of the palace during King Gyeongdeok's reign.$$);

UPDATE public."TourStory" AS story
SET summary_en = translation.english
FROM oiso_story_copy AS translation
WHERE translation.field_name = 'summary'
  AND story.spot_id = translation.spot_id
  AND story.summary = translation.source_ko
  AND NULLIF(btrim(story.summary_en), '') IS NULL;

UPDATE public."TourStory" AS story
SET intro_en = translation.english
FROM oiso_story_copy AS translation
WHERE translation.field_name = 'intro'
  AND story.spot_id = translation.spot_id
  AND story.intro = translation.source_ko
  AND NULLIF(btrim(story.intro_en), '') IS NULL;

UPDATE public."TourStory" AS story
SET story_en = translation.english
FROM oiso_story_copy AS translation
WHERE translation.field_name = 'story'
  AND story.spot_id = translation.spot_id
  AND replace(story.story, E'\r\n', E'\n') = replace(translation.source_ko, E'\r\n', E'\n')
  AND NULLIF(btrim(story.story_en), '') IS NULL;

UPDATE public."TourStory" AS story
SET tip_en = translation.english
FROM oiso_story_copy AS translation
WHERE translation.field_name = 'tip'
  AND story.spot_id = translation.spot_id
  AND story.tip = translation.source_ko
  AND NULLIF(btrim(story.tip_en), '') IS NULL;

UPDATE public."TourStory" AS story
SET did_you_know_en = translation.english
FROM oiso_story_copy AS translation
WHERE translation.field_name = 'did_you_know'
  AND story.spot_id = translation.spot_id
  AND story.did_you_know = translation.source_ko
  AND NULLIF(btrim(story.did_you_know_en), '') IS NULL;

COMMIT;
