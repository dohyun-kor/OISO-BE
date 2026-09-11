import java.sql.*;
var c=DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres","postgres","rnclf2351@!");
var r=c.createStatement().executeQuery("select spot_id,name,content_id from public.\"TourSpot\" where img is null or btrim(img)='' order by spot_id");
int n=0; while(r.next()){System.out.println(r.getLong(1)+"|"+r.getString(2)+"|"+r.getObject(3)); n++;} System.out.println("COUNT="+n); c.close();
