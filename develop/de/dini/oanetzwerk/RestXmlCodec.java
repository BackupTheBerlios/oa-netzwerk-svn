package de.dini.oanetzwerk;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom.Content;
import org.jdom.Element;
import org.jdom.Text;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;

import org.xml.sax.InputSource;



public class RestXmlCodec {

	/**
	 * encodes XML REST request body
	 * @param listEntrySet list of hashmaps storing key-value-sets
	 * @return XML encoded request body
	 */
	public static String encodeEntrySetRequestBody(List<HashMap<String, String>> listEntrySet) {
		StringBuffer sbXML = new StringBuffer();
		sbXML.append("<oanrest>\n");
		sbXML.append("<request>\n");		
		sbXML.append(encodeEntrySet(listEntrySet));
		sbXML.append("</request>\n");
		sbXML.append("</oanrest>\n");
		return sbXML.toString();
	}

	/**
	 * encodes XML REST response body
	 * @param listEntrySet list of hashmaps storing key-value-sets
	 * @param keyword a REST-keyword to deliver back by the server
	 * @return XML encoded request body
	 */	
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
	
	/**
	 * encodes only the entryset (without header or footer)
	 * @param listEntrySet list of hashmaps storing key-value-sets
	 * @return XML encoded data
	 */
	private static String encodeEntrySet(List<HashMap<String, String>> listEntrySet) {
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
	
	/**
	 * decodes XML String to entryset 
	 * @param strXML XML encoded data
	 * @return list of hashmaps storing key-value-sets
	 */
	public static List<HashMap<String, String>> decodeEntrySet(String strXML) {
		List<HashMap<String, String>> listEntrySet = new ArrayList<HashMap<String,String>>();
			
		org.jdom.Document doc;
		SAXBuilder builder = new SAXBuilder();
		try {		
			doc = builder.build(new InputSource (new StringReader(strXML)));
			
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
	
	//TODO: Fehlerkodierung als Hash ist ungeschickt, da Errors eine Liste ist

	
	/**
	 * encodes an error response
	 * @param listErrors list of hashmaps storing key-description-sets
	 * @return XML encoded errors
	 */
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

	
	/**
	 * decodes XML String to error list 
	 * @param strXML XML encoded data
	 * @return list of hashmaps storing key-description-sets
	 */
	public static List<HashMap<String, String>> decodeErrors(String strXML) {
		List<HashMap<String, String>> listErrors = new ArrayList<HashMap<String,String>>();
			
		org.jdom.Document doc;
		SAXBuilder builder = new SAXBuilder();
		try {		
			doc = builder.build(new InputSource (new StringReader(strXML)));
			
			System.out.println("** doc generated");
			
			ElementFilter filter = new ElementFilter("errors");
			Iterator iterator = doc.getDescendants(filter);
			while (iterator.hasNext()) {
				System.out.println("** <errors> found");
				Element elementErrors = (Element) iterator.next();
				HashMap<String,String> mapError = new HashMap<String,String>();
				filter = new ElementFilter("error");
				Iterator iterator2 = elementErrors.getDescendants(filter);
				while (iterator2.hasNext()) {
					System.out.println("** <error> found");					
					Element elementError = (Element) iterator2.next();
					String key = elementError.getAttributeValue("key");
					System.out.println("** key == " + key);
					String description = null; 
					Element elementDescription = elementError.getChild("description");
					if(elementDescription != null) {
						description = ((Text)elementDescription.getContent(0)).getValue();
					}
					System.out.println("** description == " + description);					
					mapError.put(key,description);
				}
				listErrors.add(mapError);
			}
		} catch(Exception e) {
			System.out.println(e);
		}
		
		return listErrors;
	}
	
	public static String fetchMessageType(String strXML) {
		String type = null; //unknown
		
		org.jdom.Document doc;
		SAXBuilder builder = new SAXBuilder();
		try {		
			doc = builder.build(new InputSource (new StringReader(strXML)));
			Element mainElement = (Element)(doc.getRootElement().getChildren()).get(0);
			type = mainElement.getName();
		} catch(Exception e) {
			System.out.println(e);
		}
		
		return type;
	}
	
	/**
	 * Testroutine
	 * @param args (none)
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
		
		String strXMLRequest = encodeEntrySetRequestBody(listTestEntrySet);
		System.out.println("MessageType == " + fetchMessageType(strXMLRequest));
		System.out.println(strXMLRequest);
		
		String strXMLResponse = encodeEntrySetResponseBody(listTestEntrySet,"TestKeyword");
		System.out.println("MessageType == " + fetchMessageType(strXMLResponse));
		System.out.println(strXMLResponse);
	
		List<HashMap<String,String>> listTestEntrySet2 = decodeEntrySet(strXMLRequest);
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
		
		System.out.println("");
		
		List<HashMap<String,String>> listTestErrors = new ArrayList<HashMap<String,String>>();
		HashMap<String,String> mapTestError = new HashMap<String,String>();
		mapTestError.put("ServerError", "SQLException... ");
		mapTestError.put("ServerError", "IOException..");
		listTestErrors.add(mapTestError);
		mapTestError = new HashMap<String,String>();
		mapTestError.put("ServerError", "OutOfIdeasException... ");
		mapTestError.put("NOObject", "Object referenced by EXT_OID=1234 and REP_ID=0815 does not exist in database.");
		listTestErrors.add(mapTestError);
		
		String strXMLErrors = encodeErrors(listTestErrors);	
		System.out.println("MessageType == " + fetchMessageType(strXMLErrors));
		System.out.println(strXMLErrors);
		
		List<HashMap<String,String>> listTestErrors2 = decodeErrors(strXMLErrors);
		System.out.println("");
		System.out.println("ERRORS");
		for(int i = 0; i < listTestErrors2.size(); i++) {
			HashMap<String,String> entry = listTestErrors2.get(i);
			Iterator it = entry.keySet().iterator();
			while(it.hasNext()) {
				String key = (String)it.next();
				System.out.println("key == \"" + key + "\"");
				String value = entry.get(key);
				System.out.println("description == " + (value==null ? "<NULL>" : ("\"" + value + "\"")));
			}
		}
		
	}

}
