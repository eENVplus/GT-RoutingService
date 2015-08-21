import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.text.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.postgis.*; 
import org.xml.sax.SAXException;

public class RouteDbConnection {
	
   private Connection mainconn = null;
   String lifeDatasetsLSIntervention = "Life:landslide_I_RL";
   String lifeDatasetsLSHazard = "Life:landslide_H_RL";
	
   class StreetBranch
   {
	   double sourceX;
	   double sourceY;
	   double targetX;
	   double targetY;
	   String streetName;
	   
	   StreetBranch(double sourcex,double sourcey,double targetx,double targety, String sn)
	   {
		   this.sourceX = sourcex;
		   this.sourceY = sourcey;
		   this.targetX = targetx;
		   this.targetY = targety;
		   this.streetName = sn;
	   }
	   
	   public String toString()
	   {
		   String output = "";
		   output = output + Double.toString(this.sourceX) + ", " + Double.toString(this.sourceY) + ", " + Double.toString(this.targetX) + ", " + Double.toString(this.targetY) + this.streetName;
		   return output;
	   }
	   
	   public void print()
	   {
		   System.out.println(this.sourceX + " " + " " + this.sourceY + " " + this.targetX + " " + this.targetY + " " + this.streetName );
	   }
	   
   }
   
   class BoundingBox
   {
	   double right;
	   double left;
	   double upper;
	   double lower;
	   
	   BoundingBox(double left,double lower,double right,double upper)
	   {
		   //TODO add check if the bbx is correct
		   this.left = left;
		   this.lower = lower;
		   this.right = right;
		   this.upper = upper;
	   }
	   public String toString()
	   {
		   String output="";
		   return output;
		   
	   }
	   
	   public void print()
	   {
		   
		   
	   }
	   
	   
   }
   
   
   //TODO: Input the database table name
   
   public RouteDbConnection()
   {
	   getConnection();
	   
	   
   }
   
   
   public static void main(String args[]) throws SQLException, IOException, ParserConfigurationException, SAXException, TransformerFactoryConfigurationError, TransformerException {
	  
	  
	  String getFeatURL = "http://lifeimagine.graphitech-projects.com/geoserver/Life/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=";
	  RouteDbConnection rdbconn = new  RouteDbConnection();
	  
	  //add record retrieved from GML wfs to table frane e pericolofrane 
	  //rdbconn.recordLandSlideonDB(rdbconn.requestGetFeatureXML(getFeatURL+ rdbconn.lifeDatasetsLSIntervention));
	  //rdbconn.recordLandSlideonDB(rdbconn.requestGetFeatureXML(getFeatURL+ rdbconn.lifeDatasetsLSHazard));
	  
	  //double pointalat = 44.1472;
	  //double pointalong = 9.6862;
	   
	   
	  double pointalat = 44.1382; //node 46837
	  double pointalong = 9.6943; //node 33834
	  double pointblat = 44.1145;
	  double pointblong = 9.8377;
	  
	  //FileInputStream fis = new FileInputStream("/home/giulio/Dropbox/Public/liferouting/avoidthis.json");
	  //InputStreamReader in = new InputStreamReader(fis, "UTF-8"); 
	  byte[] encoded = Files.readAllBytes(Paths.get("/home/giulio/Dropbox/Public/liferouting/map.json"));
	  String geoJsonAvoid = new String(encoded, "UTF-8");
	  
	  byte[] encodedPos = Files.readAllBytes(Paths.get("/home/giulio/Downloads/zaklCfHV"));
	  String jsonPos = new String(encodedPos, "UTF-8");
	  
	  String splitSRC = jsonPos.split(";")[0];
	  String source =splitSRC.split("source:")[1];
	  pointalat = Double.parseDouble(source.split(",")[0]);
	  pointalong = Double.parseDouble(source.split(",")[1]);
	  
	  String splitTRGT = jsonPos.split(";")[1];
	  String target = splitTRGT.split("target: ")[1];
	  pointblat = Double.parseDouble(target.split(",")[0]);
	  pointblong = Double.parseDouble(target.split(",")[1]);
	  
	  
	  String route = rdbconn.routeFromAtoB(pointalat,pointalong,pointblat,pointblong);
	  //System.out.println(route);
	  String geoJSONNoModal = 	rdbconn.graphRouteFromAtoB(pointalat,pointalong,pointblat,pointblong) ;
	  System.out.println("routing pedestrian");
	  String geoJSONPedestrian = 	rdbconn.graphRouteModalFromAtoB(pointalat,pointalong,pointblat,pointblong,3) ;
	  System.out.println("hgv routing");
	  String geoJSONHGV = 	rdbconn.graphRouteModalFromAtoB(pointalat,pointalong,pointblat,pointblong,5) ;
	  //String geoJSON = rdbconn.geoJSONfromNodes(10953, 2112);
	  
	  String geoJSONAvoid = rdbconn.graphRouteAvoidLSFromAtoB(pointalat, pointalong, pointblat, pointblong, 0);
	  String geoJSONAvoidJSON = rdbconn.graphRouteAvoidGeoJSONFromAtoB(pointalat, pointalong, pointblat, pointblong, 0,geoJsonAvoid);
	  
	  
	  PrintWriter writer_nomod = new PrintWriter("/home/giulio/Dropbox/Public/liferouting/routenomod.json", "UTF-8");
	  writer_nomod.println(geoJSONNoModal);
	  writer_nomod.close();
	  
	  PrintWriter writer_ped = new PrintWriter("/home/giulio/Dropbox/Public/liferouting/routeped.json", "UTF-8");
	  writer_ped.println(geoJSONPedestrian);
	  writer_ped.close();
	  
	  PrintWriter writer_hgv = new PrintWriter("/home/giulio/Dropbox/Public/liferouting/routehgv.json", "UTF-8");
	  writer_hgv.println(geoJSONHGV);
	  writer_hgv.close();
	  
	  PrintWriter writer_avoid = new PrintWriter("/home/giulio/Dropbox/Public/liferouting/routeavoid.json", "UTF-8");
	  writer_avoid.println(geoJSONAvoid);
	  writer_avoid.close();
	  
	  PrintWriter writer_avoidjson = new PrintWriter("/home/giulio/Dropbox/Public/liferouting/routeavoidjson.json", "UTF-8");
	  writer_avoidjson.println(geoJSONAvoidJSON);
	  writer_avoidjson.close();
	
      
   }
   
   
   public void testConnection()
   {
	   
	   
   } 
   
   //TODO: different typology of routing algorithm
   //TODO: Routing for different types of vehicle 
   
   
   public String routeFromAtoB(String A_lat,String A_long,String B_lat,String B_long) throws SQLException 
   {
	   String result = "";
	   double Alat = Double.parseDouble(A_lat);
	   double Along = Double.parseDouble(A_long) ;
	   double Blat = Double.parseDouble(B_lat);
	   double Blong = Double.parseDouble(B_long);
	   result = routeFromAtoB(Along,Alat,Blong,Blat);
	   return result;
	   
   }
   
   public String routeFromAtoB(double Alat,double Along,double Blat,double Blong) throws SQLException 
   {
	   String result = null;
	   int nodeA = this.getNearestNode(Alat,Along );
	   int nodeB = this.getTargetNearestNode(Blat,Blong );
	   ArrayList<StreetBranch> sbes = this.routeFromNodes(nodeA, nodeB);
	   result = sbes.toString();
	   return result;
	   
   }
   
   
   public String graphRouteFromAtoB(String A_lat,String A_long,String B_lat,String B_long) throws SQLException 
   {
	   
	  String geoJSONresult = null ;
	  double Alat = Double.parseDouble(A_lat);
	  double Along = Double.parseDouble(A_long) ;
	  double Blat = Double.parseDouble(B_lat);
	  double Blong = Double.parseDouble(B_long);
	  geoJSONresult = graphRouteFromAtoB(Alat,Along,Blat,Blong);
	  
	  return geoJSONresult;
	   
   }
   
   public String graphRouteFromAtoB(double Alat,double Along,double Blat,double Blong) throws SQLException
   {
	   String geoJSONresult = null ;
	   int nodeA = this.getNearestNode(Alat,Along );
	   System.out.println(nodeA);
	   int nodeB = this.getNearestNode(Blat,Blong );
	   System.out.println(nodeB);
	   geoJSONresult = geoJSONfromNodes(nodeA,nodeB);
	   return geoJSONresult;
   }
   
   public String graphRouteModalFromAtoB(double Alat,double Along,double Blat,double Blong,int modality) throws SQLException
   {
	   String geoJSONresult = null ;
	   int nodeA = this.getNearestNode(Alat,Along );
	   System.out.println(nodeA);
	   int nodeB = this.getNearestNode(Blat,Blong );
	   System.out.println(nodeB);
	   geoJSONresult = geoJSONfromNodes(nodeA,nodeB,modality);
	   return geoJSONresult;
   }
   
   public String graphRouteAvoidLSFromAtoB(double Alat,double Along,double Blat,double Blong,int modality) throws SQLException
   {
	   String geoJSONresult = null ;
	   int nodeA = this.getNearestNode(Alat,Along );
	   System.out.println(nodeA);
	   int nodeB = this.getNearestNode(Blat,Blong );
	   System.out.println(nodeB);
	   geoJSONresult = geoJSONAvoidStoredLSfromNodes(nodeA,nodeB);
	   return geoJSONresult;
   }
   
   public String graphRouteAvoidGeoJSONFromAtoB(double Alat,double Along,double Blat,double Blong,int modality,String geoJSON) throws SQLException
   {
	   String geoJSONtoAvoid = geoJSON;
	   String geoJSONresult = null ;
	   int nodeA = this.getNearestNode(Alat,Along );
	   System.out.println(nodeA);
	   int nodeB = this.getNearestNode(Blat,Blong );
	   System.out.println(nodeB);
	   geoJSONresult = geoJSONAvoidGeoJSONfromNodes(nodeA,nodeB,geoJSONtoAvoid,0);
	   return geoJSONresult;
   }
   
   
   private Connection getConnection()
   {
	   if (this.mainconn == null)
	   {
		  try 
	      {
	         Class.forName("org.postgresql.Driver");
	         this.mainconn = DriverManager
	            .getConnection("jdbc:postgresql://localhost:5432/lifeplusdb",
	            "postgres", "passwd");
	         System.out.println("Opened database successfully");
	      }
	      catch (Exception e) 
	      {
	          e.printStackTrace();
	          System.err.println(e.getClass().getName()+": "+e.getMessage());
	          System.exit(0);
	      }
	   }
	   return this.mainconn;
   }
   
   //TODO. try catch stuff
   private int getNearestNode(double latitude ,double longitude) throws SQLException
   {
	   int node=-1;
	   String sql = "SELECT osm_id::integer,source FROM rl_2po_4pgr ORDER BY geom_way <-> ST_GeometryFromText('POINT(' || ? || ' ' || ? || ')',4326) LIMIT 1;";
	   //String sql = "select id from hh_2po_4pgr order by st_distance(geom_way, st_setsrid(st_makepoint(?, ?), 4326)) limit 1;";
	   PreparedStatement ps = this.mainconn.prepareStatement(sql);
	   ps.setDouble(2,latitude);
	   ps.setDouble(1,longitude);
	   ResultSet rs = ps.executeQuery();
	   while ( rs.next() ) {
	          node = rs.getInt("source");
	          System.out.println( "ID = " + node );
	       }
	   
	   return node;
	   
   }
   
   private int getTargetNearestNode(double latitude ,double longitude) throws SQLException
   {
	   int node=-1;
	   String sql = "SELECT osm_id::integer,source,target FROM rl_2po_4pgr ORDER BY geom_way <-> ST_GeometryFromText('POINT(' || ? || ' ' || ? || ')',4326) LIMIT 1;";
	   //String sql = "select id from hh_2po_4pgr order by st_distance(geom_way, st_setsrid(st_makepoint(?, ?), 4326)) limit 1;";
	   PreparedStatement ps = this.mainconn.prepareStatement(sql);
	   ps.setDouble(2,latitude);
	   ps.setDouble(1,longitude);
	   ResultSet rs = ps.executeQuery();
	   while ( rs.next() ) {
	          node = rs.getInt("target");
	          System.out.println( "ID = " + node );
	       }
	   
	   return node;
	   
   }
   
   
   public String geoJSONfromNodes(String nodeA,String nodeB) throws SQLException
   {
	   String geoJSON = null;
	   int nodea = Integer.parseInt(nodeA);
       int nodeb = Integer.parseInt(nodeB);
	   geoJSON = geoJSONfromNodes(nodea,nodeb);
	   return geoJSON;
	   
   }
   
   
   //TODO. try catch stuff
   private String geoJSONfromNodes(int nodeA,int nodeB) throws SQLException
   {
	   String geoJSON = "{ \"type\": \"GeometryCollection\",\"geometries\": [";
	   String sql = "SELECT seq, id1 AS node, id2 AS edge,cost FROM pgr_dijkstra('SELECT id AS id,source::integer,target::integer,cost::double precision AS cost FROM rl_2po_4pgr',?, ?, false, false) ;";
	   PreparedStatement ps = this.mainconn.prepareStatement(sql);
	   ps.setInt(1,nodeA);
	   ps.setInt(2,nodeB);
	   ResultSet rs = ps.executeQuery();
	   
	   while ( rs.next() ) {
	          int id = rs.getInt("seq");
	          String  node = rs.getString("node");
	          String  edge = rs.getString("edge");
	          int node_num = Integer.parseInt(node);
	          int edge_num = Integer.parseInt(edge);
	          String sqlgeoJSON = "SELECT ST_AsGeoJSON(geom_way) FROM rl_2po_4pgr WHERE source = ? AND id = ? UNION SELECT ST_AsGeoJSON(ST_Reverse(geom_way)) FROM rl_2po_4pgr WHERE target = ? AND id = ?;";
	          PreparedStatement psgeoJSON = this.mainconn.prepareStatement(sqlgeoJSON);
	          psgeoJSON.setInt(1,node_num);
	          psgeoJSON.setInt(2,edge_num);
	          psgeoJSON.setInt(3,node_num);
	          psgeoJSON.setInt(4,edge_num);
	          ResultSet rsgeoJSON = psgeoJSON.executeQuery();
	          
	          while (rsgeoJSON.next())
	          {
	       	   	String JSONstreet = rsgeoJSON.getString("st_asgeojson");
	       	   	if (rs.isLast())
	       	   		geoJSON = geoJSON + JSONstreet;
	       	   	else
	       	   		geoJSON = geoJSON + JSONstreet +",";
	          }
	          //float salary = rs.getFloat("salary");
	          rsgeoJSON.close();
	          psgeoJSON.close();
	       }
	   geoJSON = geoJSON + " ] }";
	   ps.close();
	   rs.close();
	   return geoJSON;
	   
   }
   //TODO. try catch stuff
   private String geoJSONfromNodes(int nodeA,int nodeB,int modality) throws SQLException
   {
	   String geoJSON = "{ \"type\": \"GeometryCollection\",\"geometries\": [";
	   
	   updateCost(modality);
	   
	   
	   String sql;
	   if (modality==3)
		  sql = "SELECT seq, id1 AS node, id2 AS edge, route.cost, x1, y1, x2, y2, osm_name  FROM pgr_dijkstra('SELECT id AS id,source::integer,target::integer,rl_2po_4pgr.cost::double precision * c.cost AS cost FROM rl_2po_4pgr, classes c WHERE rl_2po_4pgr.clazz = c.clazz',?, ?, false, false) as route join rl_2po_4pgr on rl_2po_4pgr.id = route.id2;";
	   else
		   sql = "SELECT seq, id1 AS node, id2 AS edge, route.cost, x1, y1, x2, y2, osm_name  FROM pgr_dijkstra('SELECT id AS id,source::integer,target::integer,rl_2po_4pgr.cost::double precision * c.cost AS cost FROM rl_2po_4pgr, classes c WHERE rl_2po_4pgr.clazz = c.clazz',?, ?, true, false) as route join rl_2po_4pgr on rl_2po_4pgr.id = route.id2;";
	   PreparedStatement ps = this.mainconn.prepareStatement(sql);
	   ps.setInt(1,nodeA);
	   ps.setInt(2,nodeB);
	   ResultSet rs = ps.executeQuery();
	   
	   while ( rs.next() ) {
	          int id = rs.getInt("seq");
	          String  node = rs.getString("node");
	          String  edge = rs.getString("edge");
	          int node_num = Integer.parseInt(node);
	          int edge_num = Integer.parseInt(edge);
	          String sqlgeoJSON = "SELECT ST_AsGeoJSON(geom_way) FROM rl_2po_4pgr WHERE source = ? AND id = ? UNION SELECT ST_AsGeoJSON(ST_Reverse(geom_way)) FROM rl_2po_4pgr WHERE target = ? AND id = ?;";
	          PreparedStatement psgeoJSON = this.mainconn.prepareStatement(sqlgeoJSON);
	          psgeoJSON.setInt(1,node_num);
	          psgeoJSON.setInt(2,edge_num);
	          psgeoJSON.setInt(3,node_num);
	          psgeoJSON.setInt(4,edge_num);
	          ResultSet rsgeoJSON = psgeoJSON.executeQuery();
	          while (rsgeoJSON.next())
	          {
	       	   	String JSONstreet = rsgeoJSON.getString("st_asgeojson");
	       	   	if (rs.isLast())
	       	   		geoJSON = geoJSON + JSONstreet;
	       	   	else
	       	   		geoJSON = geoJSON + JSONstreet +",";
	          }
	          rsgeoJSON.close();
	          psgeoJSON.close();
	       }
	   geoJSON = geoJSON + " ] }";
	   ps.close();
	   rs.close();
	   return geoJSON;
	   
   }
   private void updateCost(int modality) throws SQLException
   {
	   String sqlUpdate="";
	   switch(modality)
	   {
	   	case 0:
	   		break;
	   	//car not avoiding pedestrian or bicycle	
	   	case 1:	
	   		sqlUpdate = "UPDATE classes SET cost=1 ;UPDATE classes SET cost=2.0 WHERE name IN ('pedestrian','steps','footway');UPDATE classes SET cost=1.5 WHERE name IN ('cicleway','living_street','path');UPDATE classes SET cost=0.8 WHERE name IN ('secondary','tertiary');UPDATE classes SET cost=0.6 WHERE name IN ('primary','primary_link');UPDATE classes SET cost=0.4 WHERE name IN ('trunk','trunk_link');UPDATE classes SET cost=0.3 WHERE name IN ('motorway','motorway_junction','motorway_link');";
	   		
	   		break;
	   	//Car avoiding pedestrian and bicycle
	   	case 2:
	   		sqlUpdate =  "UPDATE classes SET cost=1 ;UPDATE classes SET cost=100000 WHERE name IN ('pedestrian','steps','footway');UPDATE classes SET cost=100000 WHERE name IN ('cicleway','living_street','path');UPDATE classes SET cost=0.6 WHERE name IN ('secondary','tertiary');UPDATE classes SET cost=0.4 WHERE name IN ('primary','primary_link');UPDATE classes SET cost=0.3 WHERE name IN ('trunk','trunk_link');UPDATE classes SET cost=0.2 WHERE name IN ('motorway','motorway_junction','motorway_link');";
	   		break;
	   	//Pedestrian
	   	case 3:
            sqlUpdate = "UPDATE classes SET cost=1 ;"
            		+ "UPDATE classes SET cost=0.1 WHERE name IN ('pedestrian','steps','footway');"
            		+ "UPDATE classes SET cost=0.2 WHERE name IN ('cicleway','living_street','path');"
            		+ "UPDATE classes SET cost=0.3 WHERE name IN ('tertiary');"
            		+ "UPDATE classes SET cost=0.5 WHERE name IN ('secondary');"
            		+ "UPDATE classes SET cost=2.0 WHERE name IN ('primary','primary_link');"
            		+ "UPDATE classes SET cost=5 WHERE name IN ('trunk','trunk_link');"
            		+ "UPDATE classes SET cost=100000 WHERE name IN ('motorway','motorway_junction','motorway_link');";
            break;
	   	//bicycle
	   	case 4:
            sqlUpdate = "UPDATE classes SET cost=1 ;"
            		+ "UPDATE classes SET cost=0.3 WHERE name IN ('pedestrian','steps','footway');"
            		+ "UPDATE classes SET cost=0.1 WHERE name IN ('cicleway','living_street','path');"
            		+ "UPDATE classes SET cost=0.3 WHERE name IN ('tertiary');"
            		+ "UPDATE classes SET cost=0.5 WHERE name IN ('secondary');"
            		+ "UPDATE classes SET cost=1.5 WHERE name IN ('primary','primary_link');"
            		+ "UPDATE classes SET cost=5 WHERE name IN ('trunk','trunk_link');"
            		+ "UPDATE classes SET cost=100000 WHERE name IN ('motorway','motorway_junction','motorway_link');";
            break;
        //heavy goods vehicle
	   	case 5:
	   		sqlUpdate =  "UPDATE classes SET cost=1 ;"
            		+ "UPDATE classes SET cost=100000 WHERE name IN ('pedestrian','steps','footway');"
            		+ "UPDATE classes SET cost=100000 WHERE name IN ('cicleway','living_street','path');"
            		+ "UPDATE classes SET cost=4 WHERE name IN ('tertiary');"
            		+ "UPDATE classes SET cost=1.5 WHERE name IN ('secondary');"
            		+ "UPDATE classes SET cost=1.0 WHERE name IN ('primary','primary_link');"
            		+ "UPDATE classes SET cost=0.6 WHERE name IN ('trunk','trunk_link');"
            		+ "UPDATE classes SET cost=0.2 WHERE name IN ('motorway','motorway_junction','motorway_link');";
            break;
	   		
	   }
	   PreparedStatement psupd = this.mainconn.prepareStatement(sqlUpdate);
	   psupd.executeUpdate();
	   psupd.close();
	   
   }
   
   private String geoJSONAvoidStoredLSfromNodes(int nodeA,int nodeB) throws SQLException
   {
	   
	   String geoJSON = "{ \"type\": \"GeometryCollection\",\"geometries\": [";
	   //String sql = "SELECT seq, id1 AS node, id2 AS edge, cost, x1, y1, x2, y2 FROM pgr_dijkstra('SELECT osm_id AS id,source::integer,target::integer,cost::double precision AS cost FROM rl_2po_4pgr as roads  left join frane as ls ',45254, 32937, true, false)FROM public."ways" as roads left join on public."BarrierPolygon" as barrier on ST_Intersects(roads.geom, barrier.geom_polygon)where barrier.id_0 is null'' , '|| source || ', '|| target || ' , false, false), '|| 'public."ways" WHERE id2 = id ORDER BY seq;SELECT DISTINCT dip.id,dip.s,dip.t FROM (SELECT ST_Intersects(rl_2po_4pgr.geom_way,frane.geom) as inters ,rl_2po_4pgr.id,rl_2po_4pgr.source as s,rl_2po_4pgr.target as t FROM rl_2po_4pgr,frane)AS dip WHERE dip.inters= 't' ;   ";
	   String sql = "SELECT seq, id1 AS node, id2 AS edge, cost FROM pgr_dijkstra('SELECT roads.id AS id,source::integer,target::integer,cost::double precision AS cost FROM rl_2po_4pgr as roads  left join frane as f ON ST_Intersects(roads.geom_way,f.geom) WHERE f.id IS NULL ',?,?, false, false); ";
	   PreparedStatement ps = this.mainconn.prepareStatement(sql);
	   ps.setInt(1,nodeA);
	   ps.setInt(2,nodeB);
	   ResultSet rs = ps.executeQuery();
	   
	   while ( rs.next() ) {
	          int id = rs.getInt("seq");
	          String  node = rs.getString("node");
	          String  edge = rs.getString("edge");
	          int node_num = Integer.parseInt(node);
	          int edge_num = Integer.parseInt(edge);
	          String sqlgeoJSON = "SELECT ST_AsGeoJSON(geom_way) FROM rl_2po_4pgr WHERE source = ? AND id = ? UNION SELECT ST_AsGeoJSON(ST_Reverse(geom_way)) FROM rl_2po_4pgr WHERE target = ? AND id = ?;";
	          PreparedStatement psgeoJSON = this.mainconn.prepareStatement(sqlgeoJSON);
	          psgeoJSON.setInt(1,node_num);
	          psgeoJSON.setInt(2,edge_num);
	          psgeoJSON.setInt(3,node_num);
	          psgeoJSON.setInt(4,edge_num);
	          ResultSet rsgeoJSON = psgeoJSON.executeQuery();
	          while (rsgeoJSON.next())
	          {
	       	   	String JSONstreet = rsgeoJSON.getString("st_asgeojson");
	       	   	if (rs.isLast())
	       	   		geoJSON = geoJSON + JSONstreet;
	       	   	else
	       	   		geoJSON = geoJSON + JSONstreet +",";
	          }
	          //float salary = rs.getFloat("salary");
	          rsgeoJSON.close();
	          psgeoJSON.close();
	       }
	   geoJSON = geoJSON + " ] }";
	   ps.close();
	   rs.close();
	   return geoJSON;
	   
   }
   
   private String geoJSONAvoidStoredLSfromNodes(int nodeA,int nodeB,int modality) throws SQLException
   {
	   updateCost(modality);
	   String geoJSON = "{ \"type\": \"GeometryCollection\",\"geometries\": [";
	   //String sql = "SELECT seq, id1 AS node, id2 AS edge, cost, x1, y1, x2, y2 FROM pgr_dijkstra('SELECT osm_id AS id,source::integer,target::integer,cost::double precision AS cost FROM rl_2po_4pgr as roads  left join frane as ls ',45254, 32937, true, false)FROM public."ways" as roads left join on public."BarrierPolygon" as barrier on ST_Intersects(roads.geom, barrier.geom_polygon)where barrier.id_0 is null'' , '|| source || ', '|| target || ' , false, false), '|| 'public."ways" WHERE id2 = id ORDER BY seq;SELECT DISTINCT dip.id,dip.s,dip.t FROM (SELECT ST_Intersects(rl_2po_4pgr.geom_way,frane.geom) as inters ,rl_2po_4pgr.id,rl_2po_4pgr.source as s,rl_2po_4pgr.target as t FROM rl_2po_4pgr,frane)AS dip WHERE dip.inters= 't' ;   ";
	   String sql = "SELECT seq, id1 AS node, id2 AS edge, cost FROM pgr_dijkstra('SELECT roads.id AS id,source::integer,target::integer,cost::double precision AS cost FROM rl_2po_4pgr as roads  left join frane as f ON ST_Intersects(roads.geom_way,f.geom) WHERE f.id IS NULL ',?,?, false, false); ";
	   PreparedStatement ps = this.mainconn.prepareStatement(sql);
	   ps.setInt(1,nodeA);
	   ps.setInt(2,nodeB);
	   ResultSet rs = ps.executeQuery();
	   
	   while ( rs.next() ) {
	          int id = rs.getInt("seq");
	          String  node = rs.getString("node");
	          String  edge = rs.getString("edge");
	          int node_num = Integer.parseInt(node);
	          int edge_num = Integer.parseInt(edge);
	          String sqlgeoJSON = "SELECT ST_AsGeoJSON(geom_way) FROM rl_2po_4pgr WHERE source = ? AND id = ? UNION SELECT ST_AsGeoJSON(ST_Reverse(geom_way)) FROM rl_2po_4pgr WHERE target = ? AND id = ?;";
	          PreparedStatement psgeoJSON = this.mainconn.prepareStatement(sqlgeoJSON);
	          psgeoJSON.setInt(1,node_num);
	          psgeoJSON.setInt(2,edge_num);
	          psgeoJSON.setInt(3,node_num);
	          psgeoJSON.setInt(4,edge_num);
	          ResultSet rsgeoJSON = psgeoJSON.executeQuery();
	          while (rsgeoJSON.next())
	          {
	       	   	String JSONstreet = rsgeoJSON.getString("st_asgeojson");
	       	   	if (rs.isLast())
	       	   		geoJSON = geoJSON + JSONstreet;
	       	   	else
	       	   		geoJSON = geoJSON + JSONstreet +",";
	          }
	          //float salary = rs.getFloat("salary");
	          rsgeoJSON.close();
	          psgeoJSON.close();
	       }
	   geoJSON = geoJSON + " ] }";
	   ps.close();
	   rs.close();
	   return geoJSON;
	   
   }
   
   
   private String geoJSONAvoidGeoJSONfromNodes(int nodeA,int nodeB,String geoJSONInput) throws SQLException
   {
	   
	   String geoJSON = "{ \"type\": \"GeometryCollection\",\"geometries\": [";
	   
	    String jsonAvoidArea = geoJSONInput;
	   /*String sql = "WITH data AS (SELECT '?'::json AS fc)" +
			 "SELECT DISTINCT dip.id FROM (SELECT ST_Intersects(ST_SetSRid(areas.geom,4326),rl_2po_4pgr.geom_way) as inters ,rl_2po_4pgr.id  FROM rl_2po_4pgr," +
			 "(SELECT row_number() OVER () AS gid,ST_GeomFromGeoJSON(feat->>'geometry') AS geom" +
			 "FROM (SELECT json_array_elements(fc->'features') AS feat FROM data) AS f )AS areas)AS dip WHERE dip.inters= 't'";
	   */
	   String sql = "SELECT seq, id1 AS node, id2 AS edge, cost FROM pgr_dijkstra"+
	   "('WITH data AS (SELECT ''?''::json AS fc)"+
	   "SELECT roads.id AS id,source::integer,target::integer,cost::double precision AS cost,geom_way FROM rl_2po_4pgr as roads"+  
	   "left join (SELECT row_number() OVER () AS gid,"+
	   "ST_GeomFromGeoJSON(feat->>''geometry'') AS geom FROM (SELECT json_array_elements(fc->''features'') AS feat FROM data) AS f )AS areas"+
	   "ON ST_Intersects(roads.geom_way,ST_SetSRid(areas.geom,4326)) WHERE areas.gid IS NULL',1034,1235, false, false); "; 
	    
	    
	   PreparedStatement ps = this.mainconn.prepareStatement(sql);
	   ps.setString(1, jsonAvoidArea);
	   ps.setInt(1,nodeA);
	   ps.setInt(2,nodeB);
	   
	   ResultSet rs = ps.executeQuery();
	   
	   while ( rs.next() ) {
	          int id = rs.getInt("seq");
	          String  node = rs.getString("node");
	          String  edge = rs.getString("edge");
	          int node_num = Integer.parseInt(node);
	          int edge_num = Integer.parseInt(edge);
	          String sqlgeoJSON = "SELECT ST_AsGeoJSON(geom_way) FROM rl_2po_4pgr WHERE source = ? AND id = ? UNION SELECT ST_AsGeoJSON(ST_Reverse(geom_way)) FROM rl_2po_4pgr WHERE target = ? AND id = ?;";
	          PreparedStatement psgeoJSON = this.mainconn.prepareStatement(sqlgeoJSON);
	          psgeoJSON.setInt(1,node_num);
	          psgeoJSON.setInt(2,edge_num);
	          psgeoJSON.setInt(3,node_num);
	          psgeoJSON.setInt(4,edge_num);
	          ResultSet rsgeoJSON = psgeoJSON.executeQuery();
	          while (rsgeoJSON.next())
	          {
	       	   	String JSONstreet = rsgeoJSON.getString("st_asgeojson");
	       	   	if (rs.isLast())
	       	   		geoJSON = geoJSON + JSONstreet;
	       	   	else
	       	   		geoJSON = geoJSON + JSONstreet +",";
	          }
	          //float salary = rs.getFloat("salary");
	          rsgeoJSON.close();
	          psgeoJSON.close();
	       }
	   rs.close();
       ps.close();
	   geoJSON = geoJSON + " ] }";
	   
	   return geoJSON;
	   
	   
   }
   
   private String geoJSONAvoidGeoJSONfromNodes(int nodeA,int nodeB,String geoJSONInput,int modality) throws SQLException
   {
	   
	   updateCost(modality);
	   String geoJSON = "{ \"type\": \"GeometryCollection\",\"geometries\": [";
	   
	    String jsonAvoidArea = "''" + geoJSONInput + "''";
	   /*String sql = "WITH data AS (SELECT '?'::json AS fc)" +
			 "SELECT DISTINCT dip.id FROM (SELECT ST_Intersects(ST_SetSRid(areas.geom,4326),rl_2po_4pgr.geom_way) as inters ,rl_2po_4pgr.id  FROM rl_2po_4pgr," +
			 "(SELECT row_number() OVER () AS gid,ST_GeomFromGeoJSON(feat->>'geometry') AS geom" +
			 "FROM (SELECT json_array_elements(fc->'features') AS feat FROM data) AS f )AS areas)AS dip WHERE dip.inters= 't'";
	   */
	   String sql = "SELECT seq, id1 AS node, id2 AS edge, cost FROM pgr_dijkstra"+
	   "('WITH data AS (SELECT " + jsonAvoidArea + "::json AS fc) "+
	   "SELECT roads.id AS id,source::integer,target::integer,cost::double precision AS cost,geom_way FROM rl_2po_4pgr as roads "+  
	   "left join (SELECT row_number() OVER () AS gid,"+
	   "ST_GeomFromGeoJSON(feat->>''geometry'') AS geom FROM (SELECT json_array_elements(fc->''features'') AS feat FROM data) AS f )AS areas "+
	   "ON ST_Intersects(roads.geom_way,ST_SetSRid(areas.geom,4326)) WHERE areas.gid IS NULL',?,?, false, false); "; 
	    
	   PreparedStatement ps = this.mainconn.prepareStatement(sql);
	   ps.setInt(1,nodeA);
	   ps.setInt(2,nodeB);
	   
	   ResultSet rs = ps.executeQuery();
	   
	   while ( rs.next() ) {
	          int id = rs.getInt("seq");
	          String  node = rs.getString("node");
	          String  edge = rs.getString("edge");
	          int node_num = Integer.parseInt(node);
	          int edge_num = Integer.parseInt(edge);
	          String sqlgeoJSON = "SELECT ST_AsGeoJSON(geom_way) FROM rl_2po_4pgr WHERE source = ? AND id = ? UNION SELECT ST_AsGeoJSON(ST_Reverse(geom_way)) FROM rl_2po_4pgr WHERE target = ? AND id = ?;";
	          PreparedStatement psgeoJSON = this.mainconn.prepareStatement(sqlgeoJSON);
	          psgeoJSON.setInt(1,node_num);
	          psgeoJSON.setInt(2,edge_num);
	          psgeoJSON.setInt(3,node_num);
	          psgeoJSON.setInt(4,edge_num);
	          ResultSet rsgeoJSON = psgeoJSON.executeQuery();
	          while (rsgeoJSON.next())
	          {
	       	   	String JSONstreet = rsgeoJSON.getString("st_asgeojson");
	       	   	if (rs.isLast())
	       	   		geoJSON = geoJSON + JSONstreet;
	       	   	else
	       	   		geoJSON = geoJSON + JSONstreet +",";
	          }
	          rsgeoJSON.close();
	          psgeoJSON.close();
	       }
	   geoJSON = geoJSON + " ] }";
	   ps.close();
	   rs.close();
	   
	   return geoJSON;
	   
	   
   }
   
   
   
   
   //TODO String makeGeoJSONfromRoadID( String road_id )
   private String makeGeoJSONfromRoadID( String road_id )
   {
	   String geoJSON ="";
	   //query for geometry starting from the road id
	   
	   //create the geoJSON part form the geometry
	   return geoJSON;
   }
   
   
   private ArrayList<String> getAffectedRoads(String gml) throws SQLException 
   {	
	   ArrayList<String> roadids = null;
	   //ArrayList<String> roadids = new ArrayList<String>();
	   //Query to obtain the roads affected with ST_Crosses
	   String sql = "SELECT DISTINCT dip.id FROM (SELECT ST_Intersects(ST_geomfromgml(?),rl_2po_4pgr.geom_way) as inters ,rl_2po_4pgr.id  FROM rl_2po_4pgr,frane)AS dip WHERE dip.inters= 't' ;";   
	   	//foreach gml of area GML 
	   	//insert id roads obtained in the list
	   PreparedStatement ps = this.mainconn.prepareStatement(sql);
       ps.setString(1,gml);
       ResultSet rs = ps.executeQuery();
	   while ( rs.next() )
	   {
		   
		   
		   
	   }
	   return roadids;
	   
   }
    
   
  
   
   public ArrayList<StreetBranch> routeFromNodes(String nodeA, String nodeB) throws SQLException
   {
	   int nodea = Integer.parseInt(nodeA);
       int nodeb = Integer.parseInt(nodeB);
			   
	   ArrayList<StreetBranch> route = routeFromNodes(nodea,nodeb);
	   
	   return route;
   }
   

   //TODO. try catch stuff
   private ArrayList<StreetBranch> routeFromNodes(int nodeA,int nodeB) throws SQLException
   {
	   ArrayList<StreetBranch> route = new ArrayList<StreetBranch>();
	   
	   // 
	   int source = nodeA;
	   int target = nodeB;
	   
	   Statement stmt = null;
	   stmt = this.mainconn.createStatement();
	   String sql = "SELECT seq, id1 AS node, id2 AS edge, route.cost, x1, y1, x2, y2, osm_name  FROM pgr_dijkstra('SELECT id AS id,source::integer,target::integer,cost::double precision AS cost FROM rl_2po_4pgr',?, ?, false, false) as route join rl_2po_4pgr on rl_2po_4pgr.id = route.id2;";
	   PreparedStatement ps = this.mainconn.prepareStatement(sql);
	   ps.setInt(1,source);
	   ps.setInt(2,target);
       ResultSet rs = ps.executeQuery();//stmt.executeQuery( "SELECT seq, id1 AS node, id2 AS edge, cost, x1, y1, x2, y2 FROM pgr_dijkstra('SELECT osm_id AS id,source::integer,target::integer,cost::double precision AS cost FROM hh_2po_4pgr',20, 40, false, false);" );
       while ( rs.next() ) {
          int id = rs.getInt("seq");
          String  node = rs.getString("node");
          float cost  = rs.getFloat("cost");
          String  edge = rs.getString("edge");
          double sourceLat = rs.getDouble("x1");
          double sourceLong = rs.getDouble("x2");
          double targetLat = rs.getDouble("y1");
          double targetLong = rs.getDouble("y2");
          String stname = rs.getString("osm_name");
          StreetBranch sb = new StreetBranch(sourceLat,sourceLong,targetLat,targetLong,stname);
          route.add(sb);
          //float salary = rs.getFloat("salary");
          System.out.println( "ID = " + id );
          System.out.println( "NODE = " + node );
          System.out.println( "EDGE = " + edge );
          System.out.println( "COST = " + cost );
          //System.out.println( "SALARY = " + salary );
          System.out.println();
       }
       
       rs.close();
       stmt.close();
       
       return route;
   }
   
   public void recordLandSlideonDB(InputStream getfeaureXML) throws ParserConfigurationException, SAXException, IOException, TransformerFactoryConfigurationError, TransformerException, SQLException
   {
	   
	   InputStream getfeatureXML = getfeaureXML;
	   ParserGetFeat pgf = new ParserGetFeat(getfeatureXML,lifeDatasetsLSIntervention);
	   //pgf.test();
	   
	   //foreach gmlfeature
	   if (pgf.type.equals("Life:landslide_H_RL"))
	   {
		   String addRecPericoloFrane = "INSERT INTO pericolo_frane VALUES('?','?','?','?');";
		   while (!pgf.readed)
		   {
			   PreparedStatement ps = this.mainconn.prepareStatement(addRecPericoloFrane);
			   ps.setInt(1, pgf.GMLGetNextIDSusce());
			   ps.setString(2, pgf.GMLGetNextClass());
			   ps.setString(3, pgf.GMLGetNextBacino());
			   ps.setString(4,pgf.GMLGetNextGeometry());
			   ps.executeUpdate();
			   
		   }
	   
	   }
	   else
	   {
		   String addRecFrane = "INSERT INTO frane VALUES(?,ST_geomFromGML(?),?,?,?,?);";
		   while (!pgf.readed)
		   {
			   PreparedStatement ps = this.mainconn.prepareStatement(addRecFrane);
			   ps.setDouble(3, pgf.GMLGetNextID());
			   ps.setString(5, pgf.GMLNextGetComune());
			   ps.setInt(1, pgf.GMLGetNextIdFrana());
			   ps.setString(2,pgf.GMLGetNextGeometry());
			   ps.setInt(4,pgf.GMLGetNextTipoFrana());
			   ps.setString(6,pgf.GMLNextGetOrigin());
			   ps.executeUpdate();
			   pgf.goForward();
		   }
		   
		  
	   }
	   
   }
   
   
   public InputStream requestGetFeatureXML(String request)
   {
	   InputStream xml = null;
	   String getfeaturexml = "";
	   try{
		   URL url = new URL(request);
		   HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		   connection.setRequestMethod("GET");
		   connection.setRequestProperty("Accept", "application/xml");
		   
		   xml = connection.getInputStream();
	   }catch(MalformedURLException ex) {
	        ex.printStackTrace();
	   } catch(IOException ioex) {
	           ioex.printStackTrace();
	   }
	   
	   return xml;
   }
   
   public String getGeometryRoadsLanslided(RouteDbConnection.BoundingBox bb)
   {
	   String request = "http://lifeimagine.graphitech-projects.com/geoserver/Life/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=Life:landslide_I_RL&CQL_FILTER=BBOX(the_geom,"+ bb.left + "," + bb.lower + "," + bb.right +"," + bb.upper +")";
	   String geoJSON = "";
	   //Obtain the XML
	   try{
	   URL url = new URL(request);
	   HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	   connection.setRequestMethod("GET");
	   connection.setRequestProperty("Accept", "application/xml");
	   
	   InputStream xml = connection.getInputStream();
	   //Parse the xml to obtian the gml
	  
	   
	   /*
	   //Foreach gml 
	   ArrayList<String> roadsid= this.getAffectedRoads(gml);
	   		//foreach roads 
	   
	   		String geoJSONroad = this.makeGeoJSONfromRoadID(road_id);
	   		//add the goeJSON part to the main
	   		geoJSON = geoJSON + geoJSONroad;
	   */
	   
	   //return a geoJSON comprehensive of all the geometries
	   }catch(MalformedURLException ex) {
	        ex.printStackTrace();
	   } catch(IOException ioex) {
	           ioex.printStackTrace();
	   }
	   return geoJSON;
   }
   
   
}
