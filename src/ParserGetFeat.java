import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.text.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

//import RouteDbConnection;


public class ParserGetFeat {
	
	 	//Retrieve the XML ofr a determined Bounding box
	 	//public XML getFeature(String request)
		InputStream xmlgetfeat;
		String type = "";
		boolean readed;
		
		ParserGetFeat(InputStream xml,String name)
		{
			xmlgetfeat = xml;
			type = name;
			readed = false;
			
		}
	
	   public String getGeometryRoadsLanslided(RouteDbConnection.BoundingBox bb)
	   {
		   
		   //Parse the xml to obtian the gml
		   DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		   DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		   Document doc = (Document) dBuilder.parse(xmlgetfeat);
		   //doc.getDefaultRootElement().normalize();
		   XPath xPath = XPathFactory.newInstance().newXPath();
		   
		   /*
		   //Foreach gml 
		   ArrayList<String> roadsid= this.getAffectedRoads(gml);
		   		//foreach roads 
		   
		   		String geoJSONroad = this.makeGeoJSONfromRoadID(road_id);
		   		//add the goeJSON part to the main
		   		geoJSON = geoJSON + geoJSONroad;
		   */
		   
		  
		   return geoJSON;
	   }
	   
	   public double GMLGetID(String gmlfeature)
	   {
		   double id = 0.0;
		   return id;
		   
		   
	   }
	   
	   public double GMLNextGetID()
	   {
		   double id = 0.0;
		   return id;
		   
		   
	   }
	   
	   public String GMLGetComune(String gmlfeature)
	   {
		   String comune="";
		   return comune;
		      
	   }
	  
	   public String GMLNextGetComune()
	   {
		   String comune="";
		   return comune;
		      
	   }
	   
	   
	   public int GMLGetIdFrana(String gmlfeature)
	   {
		   int id_frana = 0;
		   return id_frana;
		   
	   }
	   
	   public int GMLGetNextIdFrana()
	   {
		   int id_frana = 0;
		   return id_frana;
		   
	   }
	   
	   
	   public String GMLGetGeometry(String gmlfeature)
	   {
		   String geom="";
		   return geom;
	   }
	   
	   public String GMLGetNextGeometry()
	   {
		   String geom="";
		   return geom;
	   }
	   
	   
	   
	   public String GMLGetTipoFrana(String gmlfeature)
	   {
		   String tipofrana = "";
		   return tipofrana;
		   
	   }
	   
	   public String GMLGetNextTipoFrana()
	   {
		   String tipofrana = "";
		   return tipofrana;
		   
	   }
	   
	   public String GMLGetClass(String gmlfeature)
	   {
		   String classe = "";
		   return classe;
		   
	   }
	   
	   public String GMLGetNextClass()
	   {
		   String classe = "";
		   return classe;
		   
	   }
	   
	   public String GMLGetBacino(String GML)
	   {
		   String bacino ="";
		   return bacino;
		   
	   }

	   public String GMLGetNextBacino()
	   {
		   String bacino ="";
		   return bacino;
		   
	   }

	   
	   public String GMLGetDatadigit(String GML)
	   {
		   
		   String datadigit ="";
		   return datadigit;
		   
	   }
	   
	   public String GMLGetNextDatadigit()
	   {
		   
		   String datadigit ="";
		   return datadigit;
		   
	   }
	   
	   
	   
	   public int GMLGetIDSusce(String GML)
	   {
		   int idsusce=0 ;
		   return idsusce;	   
	   }
   
	   public int GMLGetNextIDSusce( )
	   {
		   int idsusce=0 ;
		   return idsusce;	   
	   }
	   
	   
	   
   
   }
