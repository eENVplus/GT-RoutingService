

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.File;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Servlet implementation class route
 */
@WebServlet("/route")
public class route extends HttpServlet {
	private static final long serialVersionUID = 1L;
	RouteDbInterface rdbconn = new  RouteDbInterface(); 
    /**
     * @see HttpServlet#HttpServlet()
     */
    public route() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 response.setContentType("text/html");
		 
		 String fromLat="";String fromLong="";
		 String toLat ="";String toLong="";
	     String from = request.getParameter("from");
	     double ptAlat=0;
	     double ptAlong=0;
	     double ptBlat=0;
	     double ptBlong = 0;
	     if (!from.contains(";"))
	    	 return;
	     
	     if (from != null)
	     {
	    	 
	    		 fromLat = from.split(";")[0];
	    		 ptAlat = Double.parseDouble(fromLat);
	    		 fromLong = from.split(";")[1];
	    		 ptAlong = Double.parseDouble(fromLong);
	    	
	     }
	     String to = request.getParameter("to");
	     if (!to.contains(";"))
	    	 return;
	     if (to != null)
	     {
	    	 
	    		 toLat = to.split(";")[0];
	    		 ptBlat = Double.parseDouble(toLat);
	    		 toLong = to.split(";")[1];
	    		 ptBlong = Double.parseDouble(toLong);
	    	
	     }
	     String mod = request.getParameter("mod");
	     int modality = 0;
	     if ( mod != null)
	    	modality = Integer.parseInt(mod);
	     String avoid = request.getParameter("avoid");
	     int type = 0;
	     if (avoid !=null)
	     {
		     
		     if ( avoid.contains("_"))
		    {
		    		 String typeoflandlslide = avoid.split("_")[1];
		    		type = Integer.parseInt(typeoflandlslide);		 
		    }		 
	     }
	     // Actual logic goes here.
	     
	     response.setContentType("text/xml;charset=UTF-8");
	     PrintWriter writer = response.getWriter();
	     //writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	    
	     try {
			writer.append(this.getRouting(ptAlat,ptAlong,ptBlat,ptBlong));
		} catch (SQLException | ParserConfigurationException
				| TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     //Previous Version of the routing service that return a JSON file 
	     /*
	     PrintWriter out = response.getWriter();
	     double pointalat = 44.1382; //node 46837
		 double pointalong = 9.6943; //node 33834
		 double pointblat = 44.1145;
		 double pointblong = 9.8377;
	    
	     String geoJSON = " ";
		 try {
			if (avoid != null)
				geoJSON = queryRouteAvoidStoredLS(ptAlat,ptAlong,ptBlat,ptBlong,modality,type);
			else	
				geoJSON = queryRoute(ptAlat,ptAlong,ptBlat,ptBlong,modality);
		 } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		 }
	     out.println(geoJSON);
		*/
	}
	
	public String getRouting(double pointalat,double pointalong,double pointblat,double pointblong) throws SQLException, ParserConfigurationException, TransformerException
	{
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		
		
		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("routing");
		doc.appendChild(rootElement);
		// staff elements
				
		//queryROute for the different modalities
		for(int i=0;i<6;i++)
		{
			Element route = doc.createElement("route");
			rootElement.appendChild(route);
			Element modality = doc.createElement("modality");
			// set attribute to staff element	
			switch(i)
			{
				case 0:
				{	
					Attr attr = doc.createAttribute("mod");
					attr.setValue("nomod");
					route.setAttributeNode(attr);
					break;
				}
				case 1:
				{	
					Attr attr = doc.createAttribute("mod");
					attr.setValue("carfullaccess");
					route.setAttributeNode(attr);
					break;
				}	
				case 2:
				{	
					Attr attr = doc.createAttribute("mod");
					attr.setValue("car");
					route.setAttributeNode(attr);
					break;
				}
				case 3:
				{	
					Attr attr = doc.createAttribute("mod");
					attr.setValue("pedestrian");
					route.setAttributeNode(attr);
					break;
				}	
				case 4:
				{	
					Attr attr = doc.createAttribute("mod");
					attr.setValue("bike");
					route.setAttributeNode(attr);
					break;
				}	
				case 5:
				{	
					Attr attr = doc.createAttribute("mod");
					attr.setValue("hgv");
					route.setAttributeNode(attr);
					break;
				}	
			}
			//RouteDbInterface.Route route = new RouteDbInterface.Route(pointalat,pointalong,pointblat,pointblong,i);
			//String geojson = queryRoute(pointalat,pointalong,pointblat,pointblong,i);
			RouteDbInterface.Route rt = rdbconn.getRouteFromCoords(pointalat,pointalong,pointblat,pointblong,i);
			Element geometry = doc.createElement("geometry");
			geometry.appendChild(doc.createTextNode(rt.path));
			route.appendChild(geometry);
			Element distance = doc.createElement("distance");
			distance.appendChild(doc.createTextNode(Double.toString(rt.getLength())));
			System.out.println(rt.getLength());
			route.appendChild(distance);
			Element time = doc.createElement("timelength");
			time.appendChild(doc.createTextNode(Double.toString(rt.getTime())));
			route.appendChild(time);
			Element crossRoads = doc.createElement("crossroads");
			//crossRoads.appendChild(doc.createTextNode(rt.get()));
			//String distance = queryDistance(pointalat,pointalong,pointblat,pointblong,i);
			//String timelength = queryTime(pointalat,pointalong,pointblat,pointblong,i)
			//String travelRoads = queryRoadCross(pointalat,pointalong,pointblat,pointblong,i);
		}
		
		DOMSource source = new DOMSource(doc);
		StringWriter sw = new StringWriter();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        //transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        //transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        //transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        //transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        transformer.transform(new DOMSource(doc), new StreamResult(sw));
        return sw.toString();
	}
	
	public String queryRoute(double pointalat,double pointalong,double pointblat,double pointblong, int modality) throws SQLException
	{  
		System.out.println(pointalat);
		System.out.println(pointalong);
		System.out.println(pointblat);
		String geoJSONNoModal = "";
		if (modality == 0)
			geoJSONNoModal = rdbconn.graphRouteFromAtoB(pointalat,pointalong,pointblat,pointblong) ;
		else
			geoJSONNoModal = rdbconn.graphRouteModalFromAtoB(pointalat,pointalong,pointblat,pointblong, modality);
		return geoJSONNoModal;
		
	}
	
	public String queryRouteAvoidStoredLS(double pointalat,double pointalong,double pointblat,double pointblong, int modality, int type) throws SQLException
	{  
		
		System.out.println(pointalat);
		System.out.println(pointalong);
		System.out.println(pointblat);
		String geoJSON = "";
		if (modality == 0)
			geoJSON = rdbconn.graphRouteAvoidLSFromAtoB(pointalat,pointalong,pointblat,pointblong,0) ;
		else if (type == 0)
			geoJSON = rdbconn.graphRouteModalFromAtoB(pointalat,pointalong,pointblat,pointblong, modality);
		else
			geoJSON = rdbconn.graphRouteAvoidLSFromAtoB(pointalat,pointalong,pointblat,pointblong, modality,type);
		
		return geoJSON;
		
	}
	
	public String queryRouteAvoidJSON(double pointalat,double pointalong,double pointblat,double pointblong, int modality,String json) throws SQLException
	{  
		System.out.println(pointalat);
		System.out.println(pointalong);
		System.out.println(pointblat);
		String geoJSONNoModal = "";
		geoJSONNoModal = rdbconn.graphRouteAvoidGeoJSONFromAtoB(pointalat,pointalong,pointblat,pointblong,modality,json);
		
		return geoJSONNoModal;
		
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("Receive a POST");
		response.setContentType("text/html");
		InputStream inputStream = request.getInputStream();
		PrintWriter out = response.getWriter();
	    StringBuilder textBuilder = new StringBuilder();
	    try (Reader reader = new BufferedReader(new InputStreamReader
	      (inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
	        int c = 0;
	        while ((c = reader.read()) != -1) {
	            textBuilder.append((char) c);
	        }
	    }
	    String json = textBuilder.toString();
	    System.out.println(json);
	    String fromLat="";String fromLong="";
		 String toLat ="";String toLong="";
	     String from = request.getParameter("from");
	     double ptAlat=0;
	     double ptAlong=0;
	     double ptBlat=0;
	     double ptBlong = 0;
	     if (!from.contains(";"))
	    	 return;
	     
	     if (from != null)
	     {
	    		 fromLat = from.split(";")[0];
	    		 ptAlat = Double.parseDouble(fromLat);
	    		 fromLong = from.split(";")[1];
	    		 ptAlong = Double.parseDouble(fromLong);
	    	
	     }
	     String to = request.getParameter("to");
	     if (!to.contains(";"))
	    	 return;
	     if (to != null)
	     {
	    	 
	    		 toLat = to.split(";")[0];
	    		 ptBlat = Double.parseDouble(toLat);
	    		 toLong = to.split(";")[1];
	    		 ptBlong = Double.parseDouble(toLong);
	    	 
	     }
	     System.out.println(ptBlat);
	    
	     String mod = request.getParameter("mod");
	     int modality = 0;
	     if ( mod != null)
	    	modality = Integer.parseInt(mod);
	     String geoJSON = " ";
			try {
				geoJSON = queryRouteAvoidJSON(ptAlat,ptAlong,ptBlat,ptBlong,modality,json);
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		System.out.println(geoJSON);
		out.println(geoJSON);
		
	
		
		
		
		
	}

}
