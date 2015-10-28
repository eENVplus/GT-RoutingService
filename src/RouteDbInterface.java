import java.io.IOException;
import java.io.InputStream;
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

public class RouteDbInterface {
	
   private Connection mainconn = null;
   String lifeDatasetsLSIntervention = "Life:landslide_I_RL";
   String lifeDatasetsLSHazard = "Life:landslide_H_RL";

   public class Route
   {
	   double sourceX;
	   double sourceY;
	   double targetX;
	   double targetY; 
	   int modality;
	   //length of the path
	   double length =0;
	   //hypotetical time spent to cross the path
	   double time = 0;
	   int startNode=0;
	   int arrivalNode = 0;
	   String path = "";
	   Route(double sourcex,double sourcey,double targetx,double targety,int modality)
	   {
		   this.sourceX = sourcex;
		   this.sourceY = sourcey;
		   this.targetX = targetx;
		   this.targetY = targety;
		   this.modality = modality; 
	   }
	   public void setTime(double time)
	   {
		   this.time = time;
	   }
	   public double getTime()
	   {
		   return this.time;
	   }
	   
	   public void setLength(double length)
	   {
		   this.length = length;
	   }
	   public double getLength()
	   {
		   return this.length;
	   }
	   public int getArrivalNode()
	   {
		   return this.arrivalNode;
	   }
	   public void setArrivalNode(int arrivalNd)
	   {
		   this.arrivalNode= arrivalNd;
	   }
	   
	   public int getStartingNode()
	   {
		   return this.startNode;
	   }
	   public void setStartingNode(int startNd)
	   {
		   this.startNode= startNd;
	   }
   
   } 
   
   
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
   
   public RouteDbInterface()
   {
	   getConnection();
	   
	   
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
	   if (modality == 0)
		   geoJSONresult = geoJSONAvoidStoredLSfromNodes(nodeA,nodeB);
	   else
		   geoJSONresult = geoJSONAvoidStoredLSfromNodes(nodeA,nodeB,modality);
	   return geoJSONresult;
   }
   
   public String graphRouteAvoidLSFromAtoB(double Alat,double Along,double Blat,double Blong,int modality,int type) throws SQLException
   {
	   String geoJSONresult = null ;
	   int nodeA = this.getNearestNode(Alat,Along );
	   System.out.println(nodeA);
	   int nodeB = this.getNearestNode(Blat,Blong );
	   System.out.println(nodeB);
	   if (modality == 0)
		   geoJSONresult = geoJSONAvoidStoredLSfromNodes(nodeA,nodeB);
	   else if (type==0)
		   geoJSONresult = geoJSONAvoidStoredLSfromNodes(nodeA,nodeB,modality);
	   else
		   geoJSONresult = geoJSONAvoidStoredLSfromNodes(nodeA,nodeB,modality,type,0);
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
   
   public Route getRouteFromCoords(double Alat,double Along,double Blat,double Blong,int mod) throws SQLException
   {
	   Route rt = new Route(Alat,Along,Blat,Blong,mod);
	   rt.setArrivalNode(this.getTargetNearestNode(Blat, Blong));
	   rt.setStartingNode(this.getNearestNode(Alat,Along));
	   
	   int nodeA = rt.getStartingNode();
	   int nodeB = rt.getArrivalNode();
	   
	   String cost_mod =setCost(mod);
	   String sql;
	   if (mod==3)
		  sql = "SELECT seq, id1 AS node, id2 AS edge, route.cost, x1, y1, x2, y2, osm_name,km,km/7 as time FROM pgr_dijkstra('SELECT id AS id,source::integer,target::integer,km,rl_2po_4pgr.cost::double precision * c." + cost_mod + " AS cost FROM rl_2po_4pgr, classes c WHERE rl_2po_4pgr.clazz = c.clazz',?, ?, false, false) as route join rl_2po_4pgr on rl_2po_4pgr.id = route.id2;";
	   else if (mod==0)
		   // sql = "SELECT seq, id1 AS node, id2 AS edge, route.cost, x1, y1, x2, y2, osm_name  FROM pgr_dijkstra('SELECT id AS id,source::integer,target::integer,rl_2po_4pgr.cost::double precision * c." + cost_mod + " AS cost FROM rl_2po_4pgr, classes c WHERE rl_2po_4pgr.clazz = c.clazz',?, ?, true, true) as route join rl_2po_4pgr on rl_2po_4pgr.id = route.id2;";
		   sql = "SELECT seq, id1 AS node, id2 AS edge, route.cost, x1, y1, x2, y2, osm_name,km,km/kmh AS time  FROM pgr_dijkstra('SELECT id AS id,source::integer,target::integer,km,  rl_2po_4pgr.cost::double precision,rl_2po_4pgr.reverse_cost::double precision AS reverse_cost FROM rl_2po_4pgr, classes c WHERE rl_2po_4pgr.clazz = c.clazz',?, ?, true, true) as route join rl_2po_4pgr on rl_2po_4pgr.id = route.id2;";
	   else if (mod==4)
		   // sql = "SELECT seq, id1 AS node, id2 AS edge, route.cost, x1, y1, x2, y2, osm_name  FROM pgr_dijkstra('SELECT id AS id,source::integer,target::integer,rl_2po_4pgr.cost::double precision * c." + cost_mod + " AS cost FROM rl_2po_4pgr, classes c WHERE rl_2po_4pgr.clazz = c.clazz',?, ?, true, true) as route join rl_2po_4pgr on rl_2po_4pgr.id = route.id2;";
		   sql = "SELECT seq, id1 AS node, id2 AS edge, route.cost, x1, y1, x2, y2, osm_name,km,km/20 AS time  FROM pgr_dijkstra('SELECT id AS id,source::integer,target::integer,km,rl_2po_4pgr.cost::double precision * c." + cost_mod + " as cost,rl_2po_4pgr.cost::double precision * c." + cost_mod + " AS reverse_cost FROM rl_2po_4pgr, classes c WHERE rl_2po_4pgr.clazz = c.clazz',?, ?, true, true) as route join rl_2po_4pgr on rl_2po_4pgr.id = route.id2;";
	   else
		   sql = "SELECT seq, id1 AS node, id2 AS edge, route.cost, x1, y1, x2, y2, osm_name,km,km/kmh AS time  FROM pgr_dijkstra('SELECT id AS id,source::integer,target::integer,km,rl_2po_4pgr.cost::double precision * c." + cost_mod + " as cost ,rl_2po_4pgr.cost::double precision * c." + cost_mod + " AS reverse_cost FROM rl_2po_4pgr, classes c WHERE rl_2po_4pgr.clazz = c.clazz',?, ?, true, true) as route join rl_2po_4pgr on rl_2po_4pgr.id = route.id2;";
	   System.out.println(sql);
	   PreparedStatement ps = this.mainconn.prepareStatement(sql);
	   ps.setInt(1,nodeA);
	   ps.setInt(2,nodeB);
	  
	   ResultSet rs = ps.executeQuery();
	   Route route = this.readDataRecord(rs, rt);
	   //rt.setLength(this.totlengthFromRecords(rs));
	   //rt.path = this.createGeoJSONfromRecords(rs);
	   //rt.setTime(this.totTimeFromRecords(rs));
	   return route;
   }
   
   private Route readDataRecord(ResultSet rs,Route route) throws NumberFormatException, SQLException
   {
	   String geoJSON = "{ \"type\": \"GeometryCollection\",\"geometries\": [";
	   double tottime = 0;
	   double totlength = 0;
	   while ( rs.next() ) {
	          
	          String  node = rs.getString("node");
	          String  edge = rs.getString("edge");
	          tottime = tottime + rs.getDouble("time");
	          totlength = totlength + rs.getDouble("km");
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
	        	if (!rs.isFirst())
	        		geoJSON = geoJSON + ",";
	       	   	String JSONstreet = rsgeoJSON.getString("st_asgeojson");
	       	   	geoJSON = geoJSON + JSONstreet;
	       	   	
	          }
	          rsgeoJSON.close();
	          psgeoJSON.close();
	       }
	   geoJSON = geoJSON + " ] }";
	   route.path = geoJSON;
	   route.setLength(totlength);
	   route.setTime(tottime);
	   return route;
   }
   /*
   public Route getRouteAvoidLSFromCoords(double Alat,double Along,double Blat,double Blong,int mod,int type )
   {
	   
	   
	   
	   
   }
   
   public Route getRouteAvoidJSON(double Alat,double Along,double Blat,double Blong,int mod,String json)
   {
	   
	   
	   
	   
   }
   
   public Route getRouteAvoidGML(double Alat,double Along,double Blat,double Blong,int mod,String gml)
   {
	   
	   
   }
   */
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
	   String geoJSON ;
	   String sql = "SELECT seq, id1 AS node, id2 AS edge,cost FROM pgr_dijkstra('SELECT id AS id,source::integer,target::integer,cost::double precision AS cost FROM rl_2po_4pgr',?, ?, false, false) ;";
	   PreparedStatement ps = this.mainconn.prepareStatement(sql);
	   ps.setInt(1,nodeA);
	   ps.setInt(2,nodeB);
	   ResultSet rs = ps.executeQuery();
	   geoJSON = createGeoJSONfromRecords(rs);
	   ps.close();
	   rs.close();
	   return geoJSON;
	   
   }
   
   private String createGeoJSONfromRecords(ResultSet rs) throws NumberFormatException, SQLException
   {
	   String geoJSON = "{ \"type\": \"GeometryCollection\",\"geometries\": [";
	   while ( rs.next() ) {
	          
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
	        	if (!rs.isFirst())
	        		geoJSON = geoJSON + ",";
	       	   	String JSONstreet = rsgeoJSON.getString("st_asgeojson");
	       	   	geoJSON = geoJSON + JSONstreet;
	       	   	
	          }
	          rsgeoJSON.close();
	          psgeoJSON.close();
	       }
	   geoJSON = geoJSON + " ] }";
	   return geoJSON;
	   
	   
   }
   
   private double totlengthFromRecords(ResultSet rs) throws NumberFormatException, SQLException
   {
	   double totlength= 0;
	   while ( rs.next() ) { 
	         double  km = rs.getDouble("km");
	         System.out.println(totlength);
	         totlength = km +totlength;
	       }
	   return totlength;
	   
	   
   }
   
   private double totTimeFromRecords(ResultSet rs) throws NumberFormatException, SQLException
   {
	   double time = 0;
	   while ( rs.next() ) {
	          
	          double  timeforsegment = rs.getDouble("time");
	          System.out.println(timeforsegment);
	          time = time + timeforsegment; 
	       }
	   return time;
   }
   
   //TODO. try catch stuff
   private String geoJSONfromNodes(int nodeA,int nodeB,int modality) throws SQLException
   {
	   //String geoJSON = "{ \"type\": \"GeometryCollection\",\"geometries\": [";
	   
	   String cost_mod =setCost(modality);
	   
	   String sql;
	   
	   if (modality==3)
		  sql = "SELECT seq, id1 AS node, id2 AS edge, route.cost,km/10 AS time,km, x1, y1, x2, y2, osm_name  FROM pgr_dijkstra('SELECT id AS id,source::integer,target::integer,rl_2po_4pgr.cost::double precision * c." + cost_mod + " AS cost FROM rl_2po_4pgr, classes c WHERE rl_2po_4pgr.clazz = c.clazz',?, ?, false, false) as route join rl_2po_4pgr on rl_2po_4pgr.id = route.id2;";
	   else  if (modality==4)
		   // sql = "SELECT seq, id1 AS node, id2 AS edge, route.cost, x1, y1, x2, y2, osm_name  FROM pgr_dijkstra('SELECT id AS id,source::integer,target::integer,rl_2po_4pgr.cost::double precision * c." + cost_mod + " AS cost FROM rl_2po_4pgr, classes c WHERE rl_2po_4pgr.clazz = c.clazz',?, ?, true, true) as route join rl_2po_4pgr on rl_2po_4pgr.id = route.id2;";
		   sql = "SELECT seq, id1 AS node, id2 AS edge, route.cost,km,km/20 as time x1, y1, x2, y2, osm_name  FROM pgr_dijkstra('SELECT id AS id,source::integer,target::integer,rl_2po_4pgr.cost::double precision * c." + cost_mod + " AS cost FROM rl_2po_4pgr, classes c WHERE rl_2po_4pgr.clazz = c.clazz',?, ?, false, false) as route join rl_2po_4pgr on rl_2po_4pgr.id = route.id2;";
	   else
		   sql = "SELECT seq, id1 AS node, id2 AS edge, route.cost,km,km/kmh AS time x1, y1, x2, y2, osm_name  FROM pgr_dijkstra('SELECT id AS id,source::integer,target::integer,km,kmh,rl_2po_4pgr.cost::double precision * c." + cost_mod + " AS cost FROM rl_2po_4pgr, classes c WHERE rl_2po_4pgr.clazz = c.clazz',?, ?, false, false) as route join rl_2po_4pgr on rl_2po_4pgr.id = route.id2;";
	   System.out.println(sql);
	   PreparedStatement ps = this.mainconn.prepareStatement(sql);
	   ps.setInt(1,nodeA);
	   ps.setInt(2,nodeB);
	  
	   ResultSet rs = ps.executeQuery();
	   String geoJSON = createGeoJSONfromRecords(rs);
	    /*
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
	   */
	   ps.close();
	   rs.close();
	   return geoJSON;
	   
   }
   private String setCost(int modality)
   {
	   String sqlUpdate="";
	   switch(modality)
	   {
	   	case 0:
	   		break;
	   	//car not avoiding pedestrian or bicycle	
	   	case 1:	
	   		sqlUpdate = "cost";
	   		
	   		break;
	   	//Car avoiding pedestrian and bicycle
	   	case 2:
	   		sqlUpdate =  "cost_car";
	   		break;
	   	//Pedestrian
	   	case 3:
            sqlUpdate = "cost_pedestrian";
            break;
	   	//bicycle
	   	case 4:
            sqlUpdate = "cost_bicycle";
            break;
        //heavy goods vehicle
	   	case 5:
	   		sqlUpdate =  "cost_hgv";
            break;
        default:
        	sqlUpdate = "cost";
	   		
	   }
	   return sqlUpdate;
	   
   }
   
   private String geoJSONAvoidStoredLSfromNodes(int nodeA,int nodeB) throws SQLException
   {
	   
	   String geoJSON;
	   //String sql = "SELECT seq, id1 AS node, id2 AS edge, cost, x1, y1, x2, y2 FROM pgr_dijkstra('SELECT osm_id AS id,source::integer,target::integer,cost::double precision AS cost FROM rl_2po_4pgr as roads  left join frane as ls ',45254, 32937, true, false)FROM public."ways" as roads left join on public."BarrierPolygon" as barrier on ST_Intersects(roads.geom, barrier.geom_polygon)where barrier.id_0 is null'' , '|| source || ', '|| target || ' , false, false), '|| 'public."ways" WHERE id2 = id ORDER BY seq;SELECT DISTINCT dip.id,dip.s,dip.t FROM (SELECT ST_Intersects(rl_2po_4pgr.geom_way,frane.geom) as inters ,rl_2po_4pgr.id,rl_2po_4pgr.source as s,rl_2po_4pgr.target as t FROM rl_2po_4pgr,frane)AS dip WHERE dip.inters= 't' ;   ";
	   String sql = "SELECT seq, id1 AS node, id2 AS edge, cost FROM pgr_dijkstra('SELECT roads.id AS id,source::integer,target::integer,cost::double precision AS cost FROM rl_2po_4pgr as roads  left join frane as f ON ST_Intersects(roads.geom_way,f.geom) WHERE f.id IS NULL ',?,?, false, false); ";
	   PreparedStatement ps = this.mainconn.prepareStatement(sql);
	   ps.setInt(1,nodeA);
	   ps.setInt(2,nodeB);
	   ResultSet rs = ps.executeQuery();
	   geoJSON = createGeoJSONfromRecords(rs);
	   ps.close();
	   rs.close();
	   return geoJSON;
	   
   }
   
   private String geoJSONAvoidStoredLSfromNodes(int nodeA,int nodeB,int modality) throws SQLException
   {
	   String cost_mod = setCost(modality);
	   String geoJSON ;
	   //String sql = "SELECT seq, id1 AS node, id2 AS edge, cost, x1, y1, x2, y2 FROM pgr_dijkstra('SELECT osm_id AS id,source::integer,target::integer,cost::double precision AS cost FROM rl_2po_4pgr as roads  left join frane as ls ',45254, 32937, true, false)FROM public."ways" as roads left join on public."BarrierPolygon" as barrier on ST_Intersects(roads.geom, barrier.geom_polygon)where barrier.id_0 is null'' , '|| source || ', '|| target || ' , false, false), '|| 'public."ways" WHERE id2 = id ORDER BY seq;SELECT DISTINCT dip.id,dip.s,dip.t FROM (SELECT ST_Intersects(rl_2po_4pgr.geom_way,frane.geom) as inters ,rl_2po_4pgr.id,rl_2po_4pgr.source as s,rl_2po_4pgr.target as t FROM rl_2po_4pgr,frane)AS dip WHERE dip.inters= 't' ;   ";
	   String sql = "SELECT seq, id1 AS node, id2 AS edge, cost FROM pgr_dijkstra('SELECT roads.id AS id,source::integer,target::integer,cost::double precision * c." + cost_mod + " AS cost FROM rl_2po_4pgr as roads  left join frane as f ON ST_Intersects(roads.geom_way,f.geom) WHERE f.id IS NULL ',?,?, false, false); ";
	   //possible change of way in which stree are weighted
	   System.out.println(sql);
	   PreparedStatement ps = this.mainconn.prepareStatement(sql);
	   ps.setInt(1,nodeA);
	   ps.setInt(2,nodeB);
	   ResultSet rs = ps.executeQuery();
	   geoJSON = createGeoJSONfromRecords(rs);
	   ps.close();
	   rs.close();
	   return geoJSON;
	   
   }
   
   private String geoJSONAvoidStoredLSfromNodes(int nodeA,int nodeB,int modality,int type,int lsTab) throws SQLException
   {
	   String cost_mod = setCost(modality);
	   String geoJSON ;
	   PreparedStatement ps; 
	   if (lsTab==0)
	   {
		   //String sql = "SELECT seq, id1 AS node, id2 AS edge, cost, x1, y1, x2, y2 FROM pgr_dijkstra('SELECT osm_id AS id,source::integer,target::integer,cost::double precision AS cost FROM rl_2po_4pgr as roads  left join frane as ls ',45254, 32937, true, false)FROM public."ways" as roads left join on public."BarrierPolygon" as barrier on ST_Intersects(roads.geom, barrier.geom_polygon)where barrier.id_0 is null'' , '|| source || ', '|| target || ' , false, false), '|| 'public."ways" WHERE id2 = id ORDER BY seq;SELECT DISTINCT dip.id,dip.s,dip.t FROM (SELECT ST_Intersects(rl_2po_4pgr.geom_way,frane.geom) as inters ,rl_2po_4pgr.id,rl_2po_4pgr.source as s,rl_2po_4pgr.target as t FROM rl_2po_4pgr,frane)AS dip WHERE dip.inters= 't' ;   ";
		   String sql = "SELECT seq, id1 AS node, id2 AS edge, cost FROM pgr_dijkstra('SELECT roads.id AS id,source::integer,target::integer,cost::double precision" ;
		   String sqlSuffix = "AS cost FROM rl_2po_4pgr as roads  left join frane as f ON ST_Intersects(roads.geom_way,f.geom) WHERE f.id IS NULL ',?,?, false, false); ";
		   String sqlSuffixType =  "AS cost FROM rl_2po_4pgr as roads  left join (SELECT * from frane where tipo=?) as f ON ST_Intersects(roads.geom_way,f.geom) WHERE f.id IS NULL ',?,?, false, false); ";
		   String sqlfinal = sql; 		
		   if (type ==0)
		   		{
			   		if (modality == 0)
			   			sqlfinal.concat(sqlSuffix);
			   			//sqlfinal=sqlfinal + sqlSuffix;
			   		else
			   			sqlfinal.concat( " * c." + cost_mod + " "+sqlSuffix);
			   		ps = this.mainconn.prepareStatement(sqlfinal);
		   			ps.setInt(1,nodeA);
		   			ps.setInt(2,nodeB);
		   		}
		   		else
		   		{
		   			if (modality == 0)
			   			sqlfinal.concat(sqlSuffix);
		   			else
			   			sqlfinal.concat( " * c." + cost_mod + " "+ sqlSuffixType);
		   			ps = this.mainconn.prepareStatement(sqlfinal);
		   			ps.setInt(2,nodeA);
		   			ps.setInt(3,nodeB); 
		   			ps.setInt(1,type);
		   		}
	   	
	  }
	   else
	   {
		   String sql = "SELECT seq, id1 AS node, id2 AS edge, cost FROM pgr_dijkstra('SELECT roads.id AS id,source::integer,target::integer,cost::double precision" ;
		   String sqlSuffix ="AS cost FROM rl_2po_4pgr as roads  left join pericolo_frane as f ON ST_Intersects(roads.geom_way,f.geom) WHERE f.id IS NULL ',?,?, false, false); ";
		   String sqlSuffixType = "AS cost FROM rl_2po_4pgr as roads  left join (SELECT * from pericolo_frane where tipo=?) as f ON ST_Intersects(roads.geom_way,f.geom) WHERE f.id IS NULL ',?,?, false, false); ";
		   String sqlfinal = sql;
		   if (type ==0)
	   		{
			   if (modality == 0)
		   			sqlfinal.concat(sqlSuffix);
		   			//sqlfinal=sqlfinal + sqlSuffix;
		   		else
		   			sqlfinal.concat( " * c." + cost_mod + " "+sqlSuffix);
	   			ps = this.mainconn.prepareStatement(sqlfinal);
	   			ps.setInt(1,nodeA);
	   			ps.setInt(2,nodeB);
	   		}
	   		else
	   		{
	   			if (modality == 0)
		   			sqlfinal.concat(sqlSuffix);
	   			else
		   			sqlfinal.concat( " * c." + cost_mod + " "+ sqlSuffixType);
	   			ps = this.mainconn.prepareStatement(sqlfinal);
	   			ps.setInt(2,nodeA);
	   			ps.setInt(3,nodeB); 
	   			ps.setInt(1,type);
	   		}
	   }
	   ResultSet rs = ps.executeQuery();
	   geoJSON = createGeoJSONfromRecords(rs);
	   ps.close();
	   rs.close();
	   return geoJSON;
	   
   }
   
   
   
   private String geoJSONAvoidGeoJSONfromNodes(int nodeA,int nodeB,String geoJSONInput) throws SQLException
   {
	   
	   return geoJSONAvoidGeoJSONfromNodes(nodeA,nodeB,geoJSONInput,0);
	   
	   
   }
   
   private String geoJSONAvoidGeoJSONfromNodes(int nodeA,int nodeB,String geoJSONInput,int modality) throws SQLException
   {
	   
	   String cost_mod = setCost(modality);
	   String geoJSON ;
	   
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
	   geoJSON = createGeoJSONfromRecords(rs);
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
   
   public String getGeometryRoadsLanslided(RouteDbInterface.BoundingBox bb)
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
