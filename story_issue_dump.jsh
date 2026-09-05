import java.sql.*;
var db=DriverManager.getConnection(System.getenv("LOCAL_DATASOURCE_URL"),System.getenv("LOCAL_DATASOURCE_USERNAME"),System.getenv("LOCAL_DATASOURCE_PASSWORD"));
String esc(String s){if(s==null)return "NULL";var b=new StringBuilder();for(char c:s.toCharArray()){if(c>127)b.append(String.format("\\u%04x",(int)c));else b.append(c=='\n'?"\\n":c);}return b.toString();}
var rs=db.createStatement().executeQuery("""
SELECT st.spot_id,ts.name,st.sub_title,st.summary,st.intro,st.story_title,st.story,st.tip,st.comment,st.did_you_know
FROM public.\"TourStory\" st JOIN public.\"TourSpot\" ts USING(spot_id)
WHERE concat_ws(' ',st.sub_title,st.summary,st.intro,st.story_title,st.story,st.tip,st.comment,st.did_you_know) ~* '(출처|자료제공|사진제공|https?://|www\\.)'
 OR length(btrim(st.sub_title))<5 OR length(btrim(st.summary))<15 OR length(btrim(st.intro))<30 OR length(btrim(st.story_title))<5 OR length(btrim(st.story))<50 OR length(btrim(st.tip))<20 OR length(btrim(st.did_you_know))<15
ORDER BY st.spot_id
""");
while(rs.next()){System.out.println("ROW "+rs.getLong(1)+" name="+esc(rs.getString(2)));String[] n={"sub_title","summary","intro","story_title","story","tip","comment","did_you_know"};for(int i=0;i<n.length;i++)System.out.println(n[i]+"="+esc(rs.getString(i+3)));}
db.close();
/exit
