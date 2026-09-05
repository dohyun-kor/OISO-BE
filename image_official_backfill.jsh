import java.sql.*;
import java.net.*;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.*;
var db=DriverManager.getConnection(System.getenv("LOCAL_DATASOURCE_URL"),System.getenv("LOCAL_DATASOURCE_USERNAME"),System.getenv("LOCAL_DATASOURCE_PASSWORD"));
db.setAutoCommit(false);
var select=db.prepareStatement("SELECT spot_id,name,content_id FROM public.\"TourSpot\" WHERE (img IS NULL OR btrim(img)='') AND content_id IS NOT NULL ORDER BY spot_id");
var updateSpot=db.prepareStatement("UPDATE public.\"TourSpot\" SET img=?,updated_at=now() WHERE spot_id=?");
var updateStory=db.prepareStatement("UPDATE public.\"TourStory\" SET img=?,updated_at=now() WHERE spot_id=?");
var client=HttpClient.newBuilder().connectTimeout(java.time.Duration.ofSeconds(15)).build();
var key=System.getenv("LOCAL_TOUR_API_SERVICE_KEY");
var encodedKey=key.contains("%")?key:URLEncoder.encode(key,StandardCharsets.UTF_8);
var imagePattern=Pattern.compile("\\\"originimgurl\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"");
var rows=select.executeQuery();int recovered=0,unresolved=0;
while(rows.next()){
 long spotId=rows.getLong(1),contentId=rows.getLong(3);String name=rows.getString(2);
 var uri=URI.create("https://apis.data.go.kr/B551011/KorService2/detailImage2?serviceKey="+encodedKey+"&MobileOS=ETC&MobileApp=Oiso&_type=json&contentId="+contentId+"&imageYN=Y&subImageYN=Y&numOfRows=10&pageNo=1");
 try{
  var response=client.send(HttpRequest.newBuilder(uri).timeout(java.time.Duration.ofSeconds(30)).GET().build(),HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
  var matcher=imagePattern.matcher(response.body());String image=matcher.find()?matcher.group(1).replace("\\/","/"):"";
  if(response.statusCode()==200&&!image.isBlank()){
   updateSpot.setString(1,image);updateSpot.setLong(2,spotId);updateSpot.executeUpdate();
   updateStory.setString(1,image);updateStory.setLong(2,spotId);updateStory.executeUpdate();
   recovered++;System.out.println("RECOVERED "+spotId+" "+name);
  }else{unresolved++;System.out.println("UNRESOLVED "+spotId+" "+name+" HTTP="+response.statusCode());}
 }catch(Exception e){unresolved++;System.out.println("FAILED "+spotId+" "+name+" "+e.getClass().getSimpleName());}
}
db.commit();System.out.printf("officialRecovered=%d unresolved=%d%n",recovered,unresolved);db.close();
/exit
