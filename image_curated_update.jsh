import java.sql.*;
var db=DriverManager.getConnection(System.getenv("LOCAL_DATASOURCE_URL"),System.getenv("LOCAL_DATASOURCE_USERNAME"),System.getenv("LOCAL_DATASOURCE_PASSWORD"));db.setAutoCommit(false);
var p1=db.prepareStatement("UPDATE public.\"TourSpot\" SET img=?,updated_at=now() WHERE spot_id=?");var p2=db.prepareStatement("UPDATE public.\"TourStory\" SET img=?,updated_at=now() WHERE spot_id=?");
var imgs=new Object[][]{
{26L,"https://www.gocamping.or.kr/upload/camp/7236/thumb/thumb_1000_45018h3D8WWEc8YQKrDbRNpV.jpg"},
{31L,"https://image.withstatic.com/272/100/202/0268b05d78a24678b5537b5728fbf58f.jpg?format=webp&height=1024&width=1280"},
{66L,"https://media.triple.guide/triple-cms/c_limit%2Cf_auto%2Ch_1024%2Cw_1024/08a1e072-609d-4797-9ac9-fc5cfd4cf32e.jpeg"},
{109L,"https://cdn.ibulgyo.com/news/photo/202212/221948_248114_5454.jpg"},
{136L,"https://tong.visitkorea.or.kr/cms/resource/56/2708756_image2_1.jpg"},
{139L,"https://m.imaeil.com/photos/2023/12/20/2023122011572053521_l.jpg"},
{142L,"https://static.kimcaddie.com/media/upload/cc/672%EA%B2%BD%EC%A3%BCcc/1613444387.765798.jpg"},
{144L,"https://minio.nculture.org/amsweb-opt/multimedia_assets/112/97738/113851/c/%EA%B2%BD%EC%A3%BC-%EC%9A%A9%EC%82%B0%EC%84%9C%EC%9B%90_%EC%9A%A9%EC%82%B0%EC%84%9C%EC%9B%90-%EC%A0%84%EA%B2%BD%282015%EB%85%84%29_%EB%AC%B8%ED%99%94%EC%9E%AC%EC%B2%AD_%EC%A0%9C1%EC%9C%A0%ED%98%95-medium-size.jpg"},
{145L,"https://tong.visitkorea.or.kr/cms/resource/68/1200968_image2_1.jpg"},
{148L,"https://world.kbs.co.kr/special/unesco/common/images/gallery/gallery_06_09.jpg"},
{157L,"https://cdn.tourtoctoc.com/news/photo/202412/7410_43073_939.jpg"},
{159L,"https://www.balpumnews.com/wp-content/uploads/2025/10/Ungok-Seowon-1024x576.jpg"},
{187L,"https://www.koya-culture.com/data/photos/20170312/art_14901864180667_a0008b.jpg"},
{270L,"https://tong.visitkorea.or.kr/cms/resource/07/3029207_image2_1.jpg"},
{276L,"https://cdn.visitkorea.or.kr/img/call?cmd=VIEW&id=6cedcd71-bc54-4eca-9e5b-59387cfa5896"},
{286L,"https://www.kolonhotel.co.kr/storage/golf/1/1/%5B%ED%81%AC%EA%B8%B0%EB%B3%80%ED%99%98%5D_JY_8337.jpg"},
{269L,"https://commons.wikimedia.org/wiki/Special:Redirect/file/Yangdong%208496.jpg"},
{283L,"https://img.trippose.com/thum/1048953/600/450"},
{287L,"https://image.xn--ok0b236bp0a.com/content_travel/2021061415301716236522171906.jpg"}
,{158L,"https://devin.aks.ac.kr/image/00e2d742-5c30-4744-aeb2-08260e629f32?preset=orig"}
};
for(var x:imgs){long id=(Long)x[0];String u=(String)x[1];p1.setString(1,u);p1.setLong(2,id);p1.executeUpdate();p2.setString(1,u);p2.setLong(2,id);p2.executeUpdate();}db.commit();System.out.println("curated="+imgs.length);db.close();
/exit
