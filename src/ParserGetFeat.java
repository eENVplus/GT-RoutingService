import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

//import RouteDbConnection;


public class ParserGetFeat {
	
	 	//Retrieve the XML ofr a determined Bounding box
	 	//public XML getFeature(String request)
		InputStream xmlgetfeat;
		String type = "";
		boolean readed;
		int features=0;
		private int iteratorposition;
		private NodeList nList = null;
		private Node tempNode=null; 
		
		
		ParserGetFeat(InputStream xml,String name) throws ParserConfigurationException, SAXException, IOException
		{
			xmlgetfeat = xml;
			type = name;
			readed = false;
			iteratorposition = 0;
			
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlgetfeat);
			
			NodeList nlist = doc.getElementsByTagName("gml:featureMember");
			nList = nlist;
			tempNode = nList.item(iteratorposition);
			features = nList.getLength();
			//((Node) doc.getDefaultRootElement()).normalize();

			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			
		}
	
	   public void test() throws TransformerFactoryConfigurationError, TransformerException
	   {
		   Element eElement = (Element) tempNode;
		   
		   System.out.println("ID :" + eElement.getElementsByTagName("Life:ID").item(0).getTextContent());
		   System.out.println(Double.toString(this.GMLGetNextID()));
		   System.out.println(this.GMLGetNextGeometry());
		   System.out.println(this.GMLGetNextIdFrana());
		   System.out.println(this.GMLNextGetComune());
		   System.out.println(this.GMLGetNextTipoFrana());
		   System.out.println(this.GMLNextGetOrigin());
		   
		   
	   }
	   
	   public void goForward()
	   {
		   
		   if (iteratorposition+1==nList.getLength())
			   this.readed=true;
		   else
		   {
			   iteratorposition++;
			   tempNode = nList.item(iteratorposition);
		   }	   
	   }
	   
	   
	   public String getGeometryRoadsLanslided(RouteDbConnection.BoundingBox bb) throws ParserConfigurationException, SAXException, IOException
	   {
		   String geoJSON="";
		   //Parse the xml to obtian the gml
		   DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		   DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		   Document doc = dBuilder.parse(xmlgetfeat);
		   //doc.getDefaultRootElement().normalize();
		 
		   
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
	   
	   public double GMLGetNextID()
	   {
		   double id = 0.0;
		   Element eElement = (Element) tempNode;
		   
		   String idString = eElement.getElementsByTagName("Life:ID").item(0).getTextContent();
		   id = Double.parseDouble(idString);
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
		   Element eElement = (Element) tempNode;
		   comune = eElement.getElementsByTagName("Life:comune").item(0).getTextContent();
		   return comune;
		      
	   }
	   
	   public String GMLNextGetOrigin()
	   {
		   String _class ="";
		   Element eElement = (Element) tempNode;
		   _class = eElement.getElementsByTagName("Life:origine").item(0).getTextContent();
		   return _class;
		      
	   }
	   
	   
	   public int GMLGetIdFrana(String gmlfeature)
	   {
		   int id_frana = 0;
		   return id_frana;
		   
	   }
	   
	   public int GMLGetNextIdFrana()
	   {
		   int id_frana = 0;
		   Element eElement = (Element) tempNode;
		   
		   String idString = eElement.getElementsByTagName("Life:ID_frana").item(0).getTextContent();
		   id_frana = Integer.parseInt(idString);
		   return id_frana;
		   
	   }
	   
	   
	   public String GMLGetGeometry(String gmlfeature)
	   {
		   String geom="";
		   return geom;
	   }
	   
	   public String GMLGetNextGeometry() throws TransformerFactoryConfigurationError, TransformerException
	   {
		   String geom="";
		   Element eElement = (Element) tempNode;
		     
		   Transformer xform = TransformerFactory.newInstance().newTransformer();
		   xform.setOutputProperty("indent", "yes");
		   StringWriter sw = new StringWriter();
	       StreamResult result = new StreamResult(sw);
	       DOMSource source = null;
	       source = new DOMSource(eElement);
	       xform.transform(source, result);
	       String sp= sw.toString().split("<Life:the_geom>")[1];
	       
	       geom = sp.split("</Life:the_geom>")[0];
		   //geom = eElement.getElementsByTagName("Life:the_geom").item(0).getTextContent();
		   return geom;
	   }
	   
	   
	   
	   public String GMLGetTipoFrana(String gmlfeature)
	   {
		   String tipofrana = "";
		   return tipofrana;
		   
	   }
	   
	   public int GMLGetNextTipoFrana()
	   {
		   int tipofrana = 99999;
		   Element eElement = (Element) tempNode;  
		   String franaString = eElement.getElementsByTagName("Life:tipo").item(0).getTextContent();
		   tipofrana = Integer.parseInt(franaString);
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
		   Element eElement = (Element) tempNode;
		   classe = eElement.getElementsByTagName("Life:CLASSE").item(0).getTextContent();
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
		   Element eElement = (Element) tempNode;
		   bacino = eElement.getElementsByTagName("Life:BACINO").item(0).getTextContent();
		   return bacino;
		   
	   }
	   
	   public String GMLGetNextFornitore()
	   {
		   String fornitore ="";
		   Element eElement = (Element) tempNode;
		   fornitore = eElement.getElementsByTagName("Life:BACINO").item(0).getTextContent();
		   return fornitore;
		   
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
		   Element eElement = (Element) tempNode;
		   String idString = eElement.getElementsByTagName("Life:ID_SUSCE").item(0).getTextContent();
		   idsusce = Integer.parseInt(idString);
		   return idsusce;	   
	   }
	   
	   public int GMLGetNextMapID( )
	   {
		   int idsusce=0 ;
		   Element eElement = (Element) tempNode;
		   String idString = eElement.getElementsByTagName("Life:MAPID").item(0).getTextContent();
		   idsusce = Integer.parseInt(idString);
		   return idsusce;	   
	   }
	   
	   public int GMLGetNextIDSorgente( )
	   {
		   int idsusce=0 ;
		   Element eElement = (Element) tempNode;
		   String idString = eElement.getElementsByTagName("Life:ID_SORGENT").item(0).getTextContent();
		   idsusce = Integer.parseInt(idString);
		   return idsusce;	   
	   }
   
   }
