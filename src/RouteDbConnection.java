import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.postgis.*; 

public class RouteDbConnection {
	
   private Connection mainconn = null;
	
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
   
   
   public RouteDbConnection()
   {
	   getConnection();
	   
	   
   }
   
   
   public static void main(String args[]) throws SQLException, FileNotFoundException, UnsupportedEncodingException {
	  double pointblat = 44.398125;
	  double pointblong = 8.9349;
	  
	  double pointalat = 44.4253;
	  double pointalong = 8.9359769;
	  RouteDbConnection rdbconn = new  RouteDbConnection();
	  String route = rdbconn.routeFromAtoB(pointalat,pointalong,pointblat,pointblong);
	  //System.out.println(route);
	  String geoJSON = 	rdbconn.graphRouteFromAtoB(pointalat,pointalong,pointblat,pointblong) ;
	  //String geoJSON = rdbconn.geoJSONfromNodes(10953, 2112);
	  System.out.println(geoJSON);
	  PrintWriter writer = new PrintWriter("route.json", "UTF-8");
	  writer.println(geoJSON);
	  writer.close();
	   /*
      Connection c = null;
      try {
         Class.forName("org.postgresql.Driver");
         c = DriverManager
            .getConnection("jdbc:postgresql://localhost:5432/trentocitydb",
            "postgres", "passwd");
         System.out.println("Opened database successfully");
         Statement stmt = null;
         stmt = c.createStatement();
         ResultSet rs = stmt.executeQuery( "SELECT seq, id1 AS node, id2 AS edge, cost FROM pgr_dijkstra('SELECT osm_id AS id,source::integer,target::integer,cost::double precision AS cost FROM hh_2po_4pgr',20, 40, false, false);" );
         while ( rs.next() ) {
            int id = rs.getInt("seq");
            String  node = rs.getString("node");
            float cost  = rs.getFloat("cost");
            String  edge = rs.getString("edge");
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
         c.close();
      
      } catch (Exception e) {
         e.printStackTrace();
         System.err.println(e.getClass().getName()+": "+e.getMessage());
         System.exit(0);
      }
      System.out.println("Opened database successfully");
      */
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
   
   /*NOT CORRECT!!!!
   private String geoJSONfromNodes(int nodeA,int nodeB) throws SQLException
   {
	   String geoJSON = null;
	   String sql = " SELECT ST_AsGeoJSON (ST_UNION( ARRAY(SELECT geom_way FROM pgr_dijkstra('SELECT osm_id AS id,source::integer,target::integer,cost::double precision AS cost FROM hh_2po_4pgr',?, ?, false, false) as route join hh_2po_4pgr on hh_2po_4pgr.osm_id = route.id2)) ) AS geom_union;" ;
	   PreparedStatement ps = this.mainconn.prepareStatement(sql);
	   ps.setInt(1,144);//nodeA);
	   ps.setInt(2,121);//nodeB);
       ResultSet rsUnion = ps.executeQuery();
       while (rsUnion.next())
       {
    	   geoJSON = rsUnion.getString("geom_union");
       }
      
	   return geoJSON;
	   
   }
   */
   
   private String geoJSONfromNodes(int nodeA,int nodeB) throws SQLException
   {
	   String geoJSON = "{ \"type\": \"GeometryCollection\",\"geometries\": [";
	   String sql = "SELECT seq, id1 AS node, id2 AS edge,cost FROM pgr_dijkstra('SELECT osm_id AS id,source::integer,target::integer,cost::double precision AS cost FROM rl_2po_4pgr',?, ?, false, false) ;";
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
	          String sqlgeoJSON = "SELECT ST_AsGeoJSON(geom_way) FROM rl_2po_4pgr WHERE source = ? AND osm_id = ? UNION SELECT ST_AsGeoJSON(ST_Reverse(geom_way)) FROM rl_2po_4pgr WHERE target = ? AND osm_id = ?;";
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
	          
	       }
	   geoJSON = geoJSON + " ] }";
	   
	   return geoJSON;
	   
   }
   
   private String geoJSONfromNodes(int nodeA,int nodeB,int modality) throws SQLException
   {
	   String geoJSON = "{ \"type\": \"GeometryCollection\",\"geometries\": [";
	   String sqlUpdate = "";
	   switch(modality)
	   {
	   	case 0:
	   		geoJSON = geoJSONfromNodes(nodeA,nodeB);
	   		break;
	   	//car not avoiding pedestrian or bicycle	
	   	case 1:	
	   		sqlUpdate = sqlUpdate + "UPDATE classes SET cost=1 ;UPDATE classes SET cost=2.0 WHERE name IN ('pedestrian','steps','footway');UPDATE classes SET cost=1.5 WHERE name IN ('cicleway','living_street','path');UPDATE classes SET cost=0.8 WHERE name IN ('secondary','tertiary');UPDATE classes SET cost=0.6 WHERE name IN ('primary','primary_link');UPDATE classes SET cost=0.4 WHERE name IN ('trunk','trunk_link');UPDATE classes SET cost=0.3 WHERE name IN ('motorway','motorway_junction','motorway_link');";
	   		break;
	   	//Car avoiding pedestrian and bicycle
	   	case 2:
	   		sqlUpdate = sqlUpdate + "UPDATE classes SET cost=1 ;UPDATE classes SET cost=100000 WHERE name IN ('pedestrian','steps','footway');UPDATE classes SET cost=100000 WHERE name IN ('cicleway','living_street','path');UPDATE classes SET cost=0.6 WHERE name IN ('secondary','tertiary');UPDATE classes SET cost=0.4 WHERE name IN ('primary','primary_link');UPDATE classes SET cost=0.3 WHERE name IN ('trunk','trunk_link');UPDATE classes SET cost=0.2 WHERE name IN ('motorway','motorway_junction','motorway_link');";
	   		break;
	   	//Pedestrian
	   	case 3:
            sqlUpdate = sqlUpdate + "UPDATE classes SET cost=1 ;"
            		+ "UPDATE classes SET cost=0.1 WHERE name IN ('pedestrian','steps','footway');"
            		+ "UPDATE classes SET cost=0.2 WHERE name IN ('cicleway','living_street','path');"
            		+ "UPDATE classes SET cost=0.3 WHERE name IN ('tertiary');"
            		+ "UPDATE classes SET cost=0.5 WHERE name IN ('secondary');"
            		+ "UPDATE classes SET cost=2.0 WHERE name IN ('primary','primary_link')"
            		+ "UPDATE classes SET cost=5 WHERE name IN ('trunk','trunk_link');"
            		+ "UPDATE classes SET cost=100000 WHERE name IN ('motorway','motorway_junction','motorway_link');";
            break;
	   	//bicycle
	   	case 4:
            sqlUpdate = sqlUpdate + "UPDATE classes SET cost=1 ;"
            		+ "UPDATE classes SET cost=0.3 WHERE name IN ('pedestrian','steps','footway');"
            		+ "UPDATE classes SET cost=0.1 WHERE name IN ('cicleway','living_street','path');"
            		+ "UPDATE classes SET cost=0.3 WHERE name IN ('tertiary');"
            		+ "UPDATE classes SET cost=0.5 WHERE name IN ('secondary');"
            		+ "UPDATE classes SET cost=1.5 WHERE name IN ('primary','primary_link')"
            		+ "UPDATE classes SET cost=5 WHERE name IN ('trunk','trunk_link');"
            		+ "UPDATE classes SET cost=100000 WHERE name IN ('motorway','motorway_junction','motorway_link');";
            break;
        //heavy goods vehicle
	   	case 5:
	   		sqlUpdate = sqlUpdate + "UPDATE classes SET cost=1 ;"
            		+ "UPDATE classes SET cost=100000 WHERE name IN ('pedestrian','steps','footway');"
            		+ "UPDATE classes SET cost=100000 WHERE name IN ('cicleway','living_street','path');"
            		+ "UPDATE classes SET cost=4 WHERE name IN ('tertiary');"
            		+ "UPDATE classes SET cost=1.5 WHERE name IN ('secondary');"
            		+ "UPDATE classes SET cost=1.0 WHERE name IN ('primary','primary_link')"
            		+ "UPDATE classes SET cost=0.6 WHERE name IN ('trunk','trunk_link');"
            		+ "UPDATE classes SET cost=0.2 WHERE name IN ('motorway','motorway_junction','motorway_link');";
            break;
	   		
	   }
	   this.mainconn.prepareStatement(sqlUpdate).executeQuery();
	  
	   String sql = "SELECT seq, id1 AS node, id2 AS edge, route.cost, x1, y1, x2, y2, osm_name  FROM pgr_dijkstra('SELECT osm_id AS id,source::integer,target::integer,cost::double precision * c.cost AS cost FROM hh_2po_4pgr, classes c WHERE clazz = c.id',?, ?, false, false) as route join hh_2po_4pgr on hh_2po_4pgr.osm_id = route.id2;";
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
	          String sqlgeoJSON = "SELECT ST_AsGeoJSON(geom_way) FROM rl_2po_4pgr WHERE source = ? AND osm_id = ? UNION SELECT ST_AsGeoJSON(geom_way) FROM rl_2po_4pgr WHERE target = ? AND osm_id = ?;";
	          PreparedStatement psgeoJSON = this.mainconn.prepareStatement(sqlgeoJSON);
	          psgeoJSON.setInt(1,node_num);
	          psgeoJSON.setInt(2,edge_num);
	          ResultSet rsgeoJSON = psgeoJSON.executeQuery();
	          while (rsgeoJSON.next())
	          {
	       	   	String JSONstreet = rsgeoJSON.getString("st_asgeojson");
	       	   	geoJSON = geoJSON + JSONstreet;
	          }
	          //float salary = rs.getFloat("salary");
	          
	       }
	   geoJSON = geoJSON + " ] }";
	   
	   return geoJSON;
	   
   }
   
   
   private String makeGeoJSONfromRoadID( String road_id )
   {
	   String geoJSON ="";
	   //query for geometry starting from the road id
	   
	   //create the geoJSON part form the geometry
	   return geoJSON;
   }
   
   
   private ArrayList<String> getAffectedRoads(String gml) 
   {	
	   ArrayList<String> roadids = new ArrayList<String>();
	   //Query to obtain the roads affected with ST_Crosses
	   	
	   	//foreach gml of area GML 
	   	//insert id roads obtained in the list
	   	
	   return roadids;
	   
   }
    
   
   //Retrieve the XML ofr a determined Bounding box
   //public XML getFeature(String request)
   /*
   public String getGeometryRoadsLanslided(BoundingBox bb)
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
	   DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	   DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	   Document doc = (Document) dBuilder.parse(xml);
	   //doc.getDefaultRootElement().normalize();
	   XPath xPath = XPathFactory.newInstance().newXPath();
	   
	   //Foreach gml 
	   ArrayList<String> roadsid= this.getAffectedRoads(gml);
	   		//foreach roads 
	   
	   		String geoJSONroad = this.makeGeoJSONfromRoadID(road_id);
	   		//add the goeJSON part to the main
	   		geoJSON = geoJSON + geoJSONroad;
	   
	   //return a geoJSON comprehensive of all the geometries
	   }catch(MalformedURLException ex) {
	        ex.printStackTrace();
	   } catch(IOException ioex) {
	           ioex.printStackTrace();
	   }
	   return geoJSON;
   }
   */
   public ArrayList<StreetBranch> routeFromNodes(String nodeA, String nodeB) throws SQLException
   {
	   int nodea = Integer.parseInt(nodeA);
       int nodeb = Integer.parseInt(nodeB);
			   
	   ArrayList<StreetBranch> route = routeFromNodes(nodea,nodeb);
	   
	   return route;
   }
   
   
   
   private ArrayList<StreetBranch> routeFromNodes(int nodeA,int nodeB) throws SQLException
   {
	   ArrayList<StreetBranch> route = new ArrayList<StreetBranch>();
	   
	   // 
	   int source = nodeA;
	   int target = nodeB;
	   
	   Statement stmt = null;
	   stmt = this.mainconn.createStatement();
	   String sql = "SELECT seq, id1 AS node, id2 AS edge, route.cost, x1, y1, x2, y2, osm_name  FROM pgr_dijkstra('SELECT osm_id AS id,source::integer,target::integer,cost::double precision AS cost FROM rl_2po_4pgr',?, ?, false, false) as route join rl_2po_4pgr on rl_2po_4pgr.osm_id = route.id2;";
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
   
   
   
   
}
