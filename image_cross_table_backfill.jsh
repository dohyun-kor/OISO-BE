import java.sql.*;
var db=DriverManager.getConnection(System.getenv("LOCAL_DATASOURCE_URL"),System.getenv("LOCAL_DATASOURCE_USERNAME"),System.getenv("LOCAL_DATASOURCE_PASSWORD"));db.setAutoCommit(false);
var rs=db.createStatement().executeQuery("""
SELECT ts.spot_id,ts.name,np.img FROM public.\"TourSpot\" ts
JOIN LATERAL (SELECT img FROM public.\"TourSpotNearbyPlace\" n WHERE n.name=ts.name AND n.img IS NOT NULL AND btrim(n.img)<>'' LIMIT 1) np ON true
WHERE ts.img IS NULL OR btrim(ts.img)=''
""");var us=db.prepareStatement("UPDATE public.\"TourSpot\" SET img=?,updated_at=now() WHERE spot_id=?");var ut=db.prepareStatement("UPDATE public.\"TourStory\" SET img=?,updated_at=now() WHERE spot_id=?");int n=0;while(rs.next()){us.setString(1,rs.getString(3));us.setLong(2,rs.getLong(1));us.executeUpdate();ut.setString(1,rs.getString(3));ut.setLong(2,rs.getLong(1));ut.executeUpdate();n++;System.out.println("COPIED "+rs.getLong(1));}db.commit();System.out.println("copied="+n);db.close();
/exit
