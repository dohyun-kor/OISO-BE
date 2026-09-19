-- Restore images for the 30 TourSpot rows in data-1789830645345.csv.
-- URLs are the existing place-matched images in story_image_migration.sql.
-- Existing nonblank images are preserved.
WITH images (spot_id, content_id, image_url) AS (
    VALUES
        (66, 130030, 'https://media.triple.guide/triple-cms/c_limit%2Cf_auto%2Ch_1024%2Cw_1024/08a1e072-609d-4797-9ac9-fc5cfd4cf32e.jpeg'),
        (139, 1621637, 'https://m.imaeil.com/photos/2023/12/20/2023122011572053521_l.jpg'),
        (287, 479934, 'https://image.xn--ok0b236bp0a.com/content_travel/2021061415301716236522171906.jpg'),
        (290, 2736388, 'https://cdn.visitkorea.or.kr/img/call?cmd=VIEW&id=678235c9-0489-4c91-9ad4-a234c8eec10a'),
        (160, 1621751, 'https://devin.aks.ac.kr/image/968e36e4-ec25-481c-9e41-7df8b3fb03dc?preset=orig'),
        (109, 250261, 'https://cdn.ibulgyo.com/news/photo/202212/221948_248114_5454.jpg'),
        (140, 482074, 'https://devin.aks.ac.kr/image/75eb8ca5-be65-4a7b-abc0-9bec3f0f03be?preset=orig'),
        (1, 2910042, 'https://tong.visitkorea.or.kr/cms/resource/35/2910035_image2_1.jpg'),
        (286, 131558, 'https://www.kolonhotel.co.kr/storage/golf/1/1/%5B%ED%81%AC%EA%B8%B0%EB%B3%80%ED%99%98%5D_JY_8337.jpg'),
        (26, 2730791, 'https://www.gocamping.or.kr/upload/camp/7236/thumb/thumb_1000_45018h3D8WWEc8YQKrDbRNpV.jpg'),
        (31, 2738810, 'https://image.withstatic.com/272/100/202/0268b05d78a24678b5537b5728fbf58f.jpg?format=webp&height=1024&width=1280'),
        (136, 125405, 'https://tong.visitkorea.or.kr/cms/resource/56/2708756_image2_1.jpg'),
        (145, 126229, 'https://tong.visitkorea.or.kr/cms/resource/68/1200968_image2_1.jpg'),
        (187, 231927, 'https://www.koya-culture.com/data/photos/20170312/art_14901864180667_a0008b.jpg'),
        (195, 2941029, 'https://tripgend.co.kr/wp-content/uploads/2025/08/batch_%EA%B5%AD%EB%AF%BC%ED%9E%90%EB%A7%81%ED%8C%8C%ED%81%AC-%EC%B9%B4%EB%88%84-%EC%B6%9C%EC%B2%98-%EA%B2%BD%EC%A3%BC%EC%8B%9C-5-1024x576.jpg'),
        (265, 1621597, 'https://devin.aks.ac.kr/image/c7e230b7-99f7-4c6e-8aba-090f4db165fa?preset=orig'),
        (269, 231933, 'https://commons.wikimedia.org/wiki/Special:Redirect/file/Yangdong%208496.jpg'),
        (283, 130858, 'https://img.trippose.com/thum/1048953/600/450'),
        (61, 2909960, 'https://cdn.visitkorea.or.kr/img/call?cmd=VIEW&id=bcb857aa-390f-4dc9-884e-b1d2bf42d706'),
        (144, 1621680, 'https://minio.nculture.org/amsweb-opt/multimedia_assets/112/97738/113851/c/%EA%B2%BD%EC%A3%BC-%EC%9A%A9%EC%82%B0%EC%84%9C%EC%9B%90_%EC%9A%A9%EC%82%B0%EC%84%9C%EC%9B%90-%EC%A0%84%EA%B2%BD%282015%EB%85%84%29_%EB%AC%B8%ED%99%94%EC%9E%AC%EC%B2%AD_%EC%A0%9C1%EC%9C%A0%ED%98%95-medium-size.jpg'),
        (142, 131557, 'https://static.kimcaddie.com/media/upload/cc/672%EA%B2%BD%EC%A3%BCcc/1613444387.765798.jpg'),
        (143, 1621764, 'https://devin.aks.ac.kr/image/0d992b92-2854-4f5e-8fdb-30c436162790?preset=orig'),
        (148, 250264, 'https://world.kbs.co.kr/special/unesco/common/images/gallery/gallery_06_09.jpg'),
        (158, 1621397, 'https://devin.aks.ac.kr/image/00e2d742-5c30-4744-aeb2-08260e629f32?preset=orig'),
        (159, 1959067, 'https://www.balpumnews.com/wp-content/uploads/2025/10/Ungok-Seowon-1024x576.jpg'),
        (266, 319725, 'https://devin.aks.ac.kr/image/a074d738-dcda-4057-8bd8-d4d84dcd117a?preset=orig'),
        (30, 2730792, 'https://tong.visitkorea.or.kr/cms/resource/30/2731530_image2_1.jpg'),
        (157, 250260, 'https://cdn.tourtoctoc.com/news/photo/202412/7410_43073_939.jpg'),
        (276, 3442528, 'https://cdn.visitkorea.or.kr/img/call?cmd=VIEW&id=6cedcd71-bc54-4eca-9e5b-59387cfa5896'),
        (270, 1621759, 'https://tong.visitkorea.or.kr/cms/resource/07/3029207_image2_1.jpg')
)
UPDATE public."TourSpot" AS spot
SET img = images.image_url,
    updated_at = now()
FROM images
WHERE spot.spot_id = images.spot_id
  AND spot.content_id = images.content_id
  AND (spot.img IS NULL OR btrim(spot.img) = '');
