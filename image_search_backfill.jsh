import java.sql.*;
import java.net.*;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.regex.*;
var db=DriverManager.getConnection(System.getenv("LOCAL_DATASOURCE_URL"),System.getenv("LOCAL_DATASOURCE_USERNAME"),System.getenv("LOCAL_DATASOURCE_PASSWORD"));db.setAutoCommit(false);
var select=db.prepareStatement("SELECT spot_id,name FROM public.\"TourSpot\" WHERE img IS NULL OR btrim(img)='' ORDER BY spot_id");
var us=db.prepareStatement("UPDATE public.\"TourSpot\" SET img=?,updated_at=now() WHERE spot_id=?");
var ut=db.prepareStatement("UPDATE public.\"TourStory\" SET img=?,updated_at=now() WHERE spot_id=?");
var client=HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).followRedirects(HttpClient.Redirect.NORMAL).build();
var pat=Pattern.compile("\\\"murl\\\":\\\"(https?://[^\\\"]+)\\\"");int done=0,failed=0;
var rs=select.executeQuery();while(rs.next()){long id=rs.getLong(1);String name=rs.getString(2);String query="\""+name+"\" 경주 사진";var uri=URI.create("https://www.bing.com/images/search?q="+URLEncoder.encode(query,StandardCharsets.UTF_8)+"&form=HDRSC2");String chosen=null;
 try{var body=client.send(HttpRequest.newBuilder(uri).header("User-Agent","Mozilla/5.0").timeout(Duration.ofSeconds(25)).GET().build(),HttpResponse.BodyHandlers.ofString()).body().replace("&quot;","\\\"").replace("\\u002f","/");var m=pat.matcher(body);while(m.find()){String u=m.group(1).replace("\\/","/").replace("&amp;","&");String l=u.toLowerCase();if(l.contains("logo")||l.contains("icon")||l.contains("favicon")||l.contains("youtube")||l.contains("instagram"))continue;try{var check=client.send(HttpRequest.newBuilder(URI.create(u)).header("User-Agent","Mozilla/5.0").timeout(Duration.ofSeconds(12)).GET().build(),HttpResponse.BodyHandlers.discarding());String ct=check.headers().firstValue("content-type").orElse("");if(check.statusCode()<400&&ct.toLowerCase().startsWith("image/")){chosen=u;break;}}catch(Exception ignored){}}
 }catch(Exception ignored){}
 if(chosen!=null){us.setString(1,chosen);us.setLong(2,id);us.executeUpdate();ut.setString(1,chosen);ut.setLong(2,id);ut.executeUpdate();done++;System.out.println("RECOVERED "+id+" "+name+" "+URI.create(chosen).getHost());}else{failed++;System.out.println("UNRESOLVED "+id+" "+name);}
}
db.commit();System.out.printf("searchRecovered=%d unresolved=%d%n",done,failed);db.close();
/exit
