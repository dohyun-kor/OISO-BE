import java.sql.*;
var db=DriverManager.getConnection(System.getenv("LOCAL_DATASOURCE_URL"),System.getenv("LOCAL_DATASOURCE_USERNAME"),System.getenv("LOCAL_DATASOURCE_PASSWORD"));
var q=db.createStatement();
var rs=q.executeQuery("""
SELECT count(*) total,
 count(*) FILTER(WHERE ts.img IS NULL OR btrim(ts.img)='') spot_img_null,
 count(*) FILTER(WHERE st.img IS NULL OR btrim(st.img)='') story_img_null,
 count(*) FILTER(WHERE concat_ws(' ',st.sub_title,st.summary,st.intro,st.story_title,st.story,st.tip,st.comment,st.did_you_know) ~* '(출처|자료제공|사진제공|https?://|www\\.)') source_artifact,
 count(*) FILTER(WHERE concat_ws(' ',st.sub_title,st.summary,st.intro,st.story_title,st.story,st.tip,st.comment,st.did_you_know) ~ '<[^>]+>') html_artifact,
 count(*) FILTER(WHERE length(btrim(st.sub_title))<5 OR length(btrim(st.summary))<15 OR length(btrim(st.intro))<30 OR length(btrim(st.story_title))<5 OR length(btrim(st.story))<50 OR length(btrim(st.tip))<20 OR length(btrim(st.did_you_know))<15) too_short,
 count(*) FILTER(WHERE st.summary=st.intro OR st.summary=st.story OR st.intro=st.story OR st.tip=st.did_you_know) cross_duplicate
FROM public.\"TourSpot\" ts JOIN public.\"TourStory\" st USING(spot_id)
""");rs.next();System.out.printf("TOTAL=%d SPOT_IMG_NULL=%d STORY_IMG_NULL=%d SOURCE=%d HTML=%d SHORT=%d CROSS_DUP=%d%n",rs.getInt(1),rs.getInt(2),rs.getInt(3),rs.getInt(4),rs.getInt(5),rs.getInt(6),rs.getInt(7));
rs=q.executeQuery("""
SELECT st.spot_id,ts.name,
 concat_ws(',',CASE WHEN st.sub_title~*'(출처|자료제공|사진제공|https?://|www\\.)' THEN 'sub_title' END,CASE WHEN st.summary~*'(출처|자료제공|사진제공|https?://|www\\.)' THEN 'summary' END,CASE WHEN st.intro~*'(출처|자료제공|사진제공|https?://|www\\.)' THEN 'intro' END,CASE WHEN st.story_title~*'(출처|자료제공|사진제공|https?://|www\\.)' THEN 'story_title' END,CASE WHEN st.story~*'(출처|자료제공|사진제공|https?://|www\\.)' THEN 'story' END,CASE WHEN st.tip~*'(출처|자료제공|사진제공|https?://|www\\.)' THEN 'tip' END,CASE WHEN st.comment~*'(출처|자료제공|사진제공|https?://|www\\.)' THEN 'comment' END,CASE WHEN st.did_you_know~*'(출처|자료제공|사진제공|https?://|www\\.)' THEN 'did_you_know' END) fields
FROM public.\"TourStory\" st JOIN public.\"TourSpot\" ts USING(spot_id)
WHERE concat_ws(' ',st.sub_title,st.summary,st.intro,st.story_title,st.story,st.tip,st.comment,st.did_you_know) ~* '(출처|자료제공|사진제공|https?://|www\\.)' ORDER BY st.spot_id
""");while(rs.next())System.out.printf("ARTIFACT %d %s [%s]%n",rs.getLong(1),rs.getString(2),rs.getString(3));
rs=q.executeQuery("SELECT spot_id,name FROM public.\"TourSpot\" WHERE img IS NULL OR btrim(img)='' ORDER BY spot_id");while(rs.next())System.out.printf("NOIMG %d %s%n",rs.getLong(1),rs.getString(2));
db.close();
/exit
