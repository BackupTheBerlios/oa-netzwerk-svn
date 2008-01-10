package de.dini.oanetzwerk;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jdom.Content;
import org.jdom.Element;
import org.jdom.Text;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class RestXmlCodec {

	public static String encodeEntrySetRequestBody(List<HashMap<String, String>> listEntrySet) {
		StringBuffer sbXML = new StringBuffer();
		sbXML.append("<oanrest>\n");
		sbXML.append("<request>\n");		
		sbXML.append(encodeEntrySet(listEntrySet));
		sbXML.append("</request>\n");
		sbXML.append("</oanrest>\n");
		return sbXML.toString();
	}

	public static String encodeEntrySetResponseBody(List<HashMap<String, String>> listEntrySet, String keyword) {
		StringBuffer sbXML = new StringBuffer();
		sbXML.append("<oanrest>\n");
		sbXML.append("<response keyword=\"");
		sbXML.append(keyword);
		sbXML.append("\">\n");		
		sbXML.append(encodeEntrySet(listEntrySet));
		sbXML.append("</response>\n");
		sbXML.append("</oanrest>\n");
		return sbXML.toString();
	}	
	
	public static String encodeEntrySet(List<HashMap<String, String>> listEntrySet) {
		StringBuffer sbXML = new StringBuffer();
		for(int i = 0; i < listEntrySet.size(); i++) {
			sbXML.append("<entryset>\n");
			HashMap<String,String> mapEntry = listEntrySet.get(i);
			Iterator it = mapEntry.keySet().iterator();
			while(it.hasNext()) {
				String key = (String)it.next();
				sbXML.append("<entry key=\"");
				sbXML.append(key);
				sbXML.append("\">");
				String value = mapEntry.get(key); 
				if(value == null) {
					sbXML.append("<NULL/>");
				} else {
					sbXML.append(value);
				}
				sbXML.append("</entry>\n");
			}
			sbXML.append("</entryset>\n");
		}
		return sbXML.toString();
	}
	
	public static String encodeErrors(List<HashMap<String, String>> listErrors) {
		StringBuffer sbXML = new StringBuffer();
		sbXML.append("<oanrest>\n");
		sbXML.append("<errors>\n");		
		for(int i = 0; i < listErrors.size(); i++) {
			HashMap mapError = listErrors.get(i);
			Iterator it = mapError.keySet().iterator();
			while(it.hasNext()) {
				String key = (String)it.next();
				sbXML.append("<error key=\"");
				sbXML.append(key);
				sbXML.append("\">\n");
				sbXML.append("<description>");
				sbXML.append(mapError.get(key));
				sbXML.append("</description>\n");
				sbXML.append("</error>\n");
			}			
		}
		sbXML.append("</errors>\n");
		sbXML.append("</oanrest>\n");
		return sbXML.toString();
	}	
	
	public static List<HashMap<String, String>> decodeEntrySet(String strXML) {
		List<HashMap<String, String>> listEntrySet = new ArrayList<HashMap<String,String>>();
		
		/*
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance ( );
			DocumentBuilder builder = factory.newDocumentBuilder ( );
			Document document = builder.parse (new InputSource (new StringReader(strXML)));
			
			NodeList nlEntrySets = document.getElementsByTagName("entryset");
			for(int i = 0; i < nlEntrySets.getLength(); i++) {
				NodeList nlEntries = nlEntrySets.item(i).getChildNodes();
				for(int j = 0; j < nlEntries.getLength(); j++) {
					Node entry = nlEntries.item(j);
					String key = entry.getAttributes().getNamedItem("key").getNodeName();
				}
			}			
		} catch (SAXException ex) {		
			ex.printStackTrace ( );
		} catch (ParserConfigurationException ex) {
			ex.printStackTrace ( );
		} catch (IOException ex) {
			ex.printStackTrace ( );
		} catch (DOMException ex) {
			ex.printStackTrace();
		} 
		*/
		
		org.jdom.Document doc;
		Element root;	
		SAXBuilder builder = new SAXBuilder();
		try {		
			doc = builder.build(new InputSource (new StringReader(strXML)));
			root = doc.getRootElement();
			
			System.out.println("** doc generated");
			
			ElementFilter filter = new ElementFilter("entryset");
			Iterator iterator = doc.getDescendants(filter);
			while (iterator.hasNext()) {
				System.out.println("** <entryset> found");
				Element elementEntrySet = (Element) iterator.next();
				HashMap<String,String> mapEntry = new HashMap<String,String>();
				filter = new ElementFilter("entry");
				Iterator iterator2 = elementEntrySet.getDescendants(filter);
				while (iterator2.hasNext()) {
					System.out.println("** <entry> found");					
					Element elementEntry = (Element) iterator2.next();
					String key = elementEntry.getAttributeValue("key");
					System.out.println("** key == " + key);
					Content content = elementEntry.getContent(0);
					String value = "";
					if(content instanceof org.jdom.Text) {
						value = ((Text)content).getValue();
					} else if(content instanceof org.jdom.Element) {
						if(((Element)content).getName().equals("NULL")) {
							value = null;
						}
					}			
					System.out.println("** value == " + value);					
					mapEntry.put(key,value);
				}
				listEntrySet.add(mapEntry);
			}
		} catch(Exception e) {
			System.out.println(e);
		}
		
		return listEntrySet;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		List<HashMap<String,String>> listTestEntrySet = new ArrayList<HashMap<String,String>>();
		HashMap<String,String> mapTestEntry = new HashMap<String,String>();
		mapTestEntry.put("foo", "bar");
		mapTestEntry.put("baz", "1234");
		listTestEntrySet.add(mapTestEntry);
		mapTestEntry = new HashMap<String,String>();
		mapTestEntry.put("non-existent", null);
		mapTestEntry.put("existent", "null");
		listTestEntrySet.add(mapTestEntry);
		
		String strTestXML = encodeEntrySetRequestBody(listTestEntrySet);	
		System.out.println(strTestXML);
		
		String strTestXML2 = encodeEntrySetResponseBody(listTestEntrySet,"TestKeyword");	
		System.out.println(strTestXML2);

		String strTestXML3 = encodeErrors(listTestEntrySet);	
		System.out.println(strTestXML3);
		
		List<HashMap<String,String>> listTestEntrySet2 = decodeEntrySet(strTestXML);
		System.out.println("");
		for(int i = 0; i < listTestEntrySet2.size(); i++) {
			System.out.println("EINTRAG " + i);
			HashMap<String,String> entry = listTestEntrySet2.get(i);
			Iterator it = entry.keySet().iterator();
			while(it.hasNext()) {
				String key = (String)it.next();
				System.out.println("key == \"" + key + "\"");
				String value = entry.get(key);
				System.out.println("value == " + (value==null ? "<NULL>" : ("\"" + value + "\"")));
			}
		}
		
	}

}
