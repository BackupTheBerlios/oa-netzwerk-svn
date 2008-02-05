package de.dini.oanetzwerk.utils;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.jdom.Content;
import org.jdom.Element;
import org.jdom.Text;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;

import org.xml.sax.InputSource;

public class RestXmlCodec {
	
	static Logger logger = Logger.getLogger (RestXmlCodec.class);
	
	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    // bisher nur lokal bei mir, könnte aber auf Scope1 deponiert werden (rm)
	private static final String XML_ROOT = "<oanrest xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"N:\\EIGENE~1\\_Aufgaben\\OA-NETZWERK\\OANREST2.xsd\">\n";

	
	private static String encodeBase64(String data) {
		try {
			return new String (Base64.encodeBase64 (data.getBytes ("UTF-8")));
		} catch(Exception ex) {
			logger.error(ex);
			return null;
		}
	}
	
	private static String decodeBase64(String data) {
		try {
			return new String(Base64.decodeBase64(((String) data).getBytes()));
		} catch(Exception ex) {
			logger.error(ex);
			return null;
		}
	}
	
	/**
	 * encodes XML REST request body
	 * @param listEntrySet list of hashmaps storing key-value-sets
	 * @return XML encoded request body
	 */
	public static String encodeEntrySetRequestBody(List<HashMap<String, String>> listEntrySet, boolean useBase64keys, boolean useBase64values) {
		StringBuffer sbXML = new StringBuffer();
		sbXML.append(XML_HEADER);
		sbXML.append(XML_ROOT);
		sbXML.append("<request>\n");		
		sbXML.append(encodeEntrySet(listEntrySet, useBase64keys, useBase64values));
		sbXML.append("</request>\n");
		sbXML.append("</oanrest>\n");
		return sbXML.toString();
	}

	public static String encodeEntrySetRequestBody(List<HashMap<String, String>> listEntrySet) {
		return encodeEntrySetRequestBody(listEntrySet, true, true);
	}
	
	/**
	 * encodes XML REST response body
	 * @param listEntrySet list of hashmaps storing key-value-sets
	 * @param keyword a REST-keyword to deliver back by the server
	 * @return XML encoded request body
	 */	
	public static String encodeEntrySetResponseBody(List<HashMap<String, String>> listEntrySet, String keyword, boolean useBase64keys, boolean useBase64values) {
		StringBuffer sbXML = new StringBuffer();
		sbXML.append(XML_HEADER);
		sbXML.append(XML_ROOT);
		sbXML.append("<response keyword=\"");
		sbXML.append(keyword);
		sbXML.append("\">\n");		
		sbXML.append(encodeEntrySet(listEntrySet, useBase64keys, useBase64values));
		sbXML.append("</response>\n");
		sbXML.append("</oanrest>\n");
		return sbXML.toString();
	}	
	
	public static String encodeEntrySetResponseBody(List<HashMap<String, String>> listEntrySet, String keyword) {
		return encodeEntrySetResponseBody(listEntrySet, keyword, true, true);
	}
	
	/**
	 * encodes only the entryset (without header or footer)
	 * @param listEntrySet list of hashmaps storing key-value-sets
	 * @return XML encoded data
	 */
	@SuppressWarnings("unchecked")
	private static String encodeEntrySet(List<HashMap<String, String>> listEntrySet, boolean useBase64keys, boolean useBase64values) {
		StringBuffer sbXML = new StringBuffer();
		for(int i = 0; i < listEntrySet.size(); i++) {
			sbXML.append("<entryset>\n");
			HashMap<String,String> mapEntry = listEntrySet.get(i);
			Iterator it = mapEntry.keySet().iterator();
			while(it.hasNext()) {
				String key = (String)it.next();
				sbXML.append("<entry key=\"");
				if(useBase64keys) {
					sbXML.append(encodeBase64(key));	
				} else {
					sbXML.append(key);
				}				
				sbXML.append("\">");
				String value = mapEntry.get(key); 
				if(value == null) {
					sbXML.append("<NULL/>");
				} else {
					if(useBase64values) {
						sbXML.append(encodeBase64(value));
					} else {
						sbXML.append(value);
					}
				}
				sbXML.append("</entry>\n");
			}
			sbXML.append("</entryset>\n");
		}
		return sbXML.toString();
	}
	
	/*private static String encodeEntrySet(List<HashMap<String, String>> listEntrySet) {
		return encodeEntrySet(listEntrySet, true, true);
	}*/
	
	/**
	 * decodes XML String to entryset 
	 * @param strXML XML encoded data
	 * @return list of hashmaps storing key-value-sets
	 */
	@SuppressWarnings("unchecked")
	public static List<HashMap<String, String>> decodeEntrySet(String strXML, boolean useBase64keys, boolean useBase64values) {
		List<HashMap<String, String>> listEntrySet = new ArrayList<HashMap<String,String>>();
			
		org.jdom.Document doc;
		SAXBuilder builder = new SAXBuilder();
		try {		
			doc = builder.build(new InputSource (new StringReader(strXML)));
			
			logger.debug("** doc generated");
			
			ElementFilter filter = new ElementFilter("entryset");
			Iterator iterator = doc.getDescendants(filter);
			while (iterator.hasNext()) {
				logger.debug("** <entryset> found");
				Element elementEntrySet = (Element) iterator.next();
				HashMap<String,String> mapEntry = new HashMap<String,String>();
				filter = new ElementFilter("entry");
				Iterator iterator2 = elementEntrySet.getDescendants(filter);
				while (iterator2.hasNext()) {
					logger.debug("** <entry> found");					
					Element elementEntry = (Element) iterator2.next();
					String key = "";
					if(useBase64keys) {
						key = decodeBase64(elementEntry.getAttributeValue("key"));
					} else {
						key = elementEntry.getAttributeValue("key");
					}					
					logger.debug("** key == " + key);
					Content content = elementEntry.getContent(0);
					String value = "";
					if(content instanceof org.jdom.Text) {
						if(useBase64values) {
							value = decodeBase64(((Text)content).getValue());
						} else {
							value = ((Text)content).getValue();
						}						
					} else if(content instanceof org.jdom.Element) {
						if(((Element)content).getName().equals("NULL")) {
							value = null;
						}
					}			
					logger.debug("** value == " + value);					
					mapEntry.put(key,value);
				}
				listEntrySet.add(mapEntry);
			}
		} catch(Exception e) {
			logger.error("error while decoding XML String: " + e);
		}
		
		return listEntrySet;
	}
	
	public static List<HashMap<String, String>> decodeEntrySet(String strXML) {
		return decodeEntrySet(strXML, true, true);
	}
	
	//TODO: Fehlerkodierung als Hash ist ungeschickt, da Errors eine Liste ist

	
	/**
	 * encodes an error response
	 * @param listErrors list of hashmaps storing key-description-sets
	 * @return XML encoded errors
	 */
	@SuppressWarnings("unchecked")
	public static String encodeErrors(List<HashMap<String, String>> listErrors) {
		StringBuffer sbXML = new StringBuffer();
		sbXML.append(XML_HEADER);
		sbXML.append(XML_ROOT);
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
	@SuppressWarnings("unchecked")
	public static List<HashMap<String, String>> decodeErrors(String strXML) {
		List<HashMap<String, String>> listErrors = new ArrayList<HashMap<String,String>>();
			
		org.jdom.Document doc;
		SAXBuilder builder = new SAXBuilder();
		try {		
			doc = builder.build(new InputSource (new StringReader(strXML)));
			
			logger.debug("** doc generated");
			
			ElementFilter filter = new ElementFilter("errors");
			Iterator iterator = doc.getDescendants(filter);
			while (iterator.hasNext()) {
				logger.debug("** <errors> found");
				Element elementErrors = (Element) iterator.next();
				HashMap<String,String> mapError = new HashMap<String,String>();
				filter = new ElementFilter("error");
				Iterator iterator2 = elementErrors.getDescendants(filter);
				while (iterator2.hasNext()) {
					logger.debug("** <error> found");					
					Element elementError = (Element) iterator2.next();
					String key = elementError.getAttributeValue("key");
					logger.debug("** key == " + key);
					String description = null; 
					Element elementDescription = elementError.getChild("description");
					if(elementDescription != null) {
						description = ((Text)elementDescription.getContent(0)).getValue();
					}
					logger.debug("** description == " + description);					
					mapError.put(key,description);
				}
				listErrors.add(mapError);
			}
		} catch(Exception e) {
			logger.error("error while decoding XML String: " + e);
		}
		
		return listErrors;
	}
	
	/**
	 * parses the XML String for mutually exclusive tags determining the message type
	 * (used to check as to whether there was an error instead of a data response)
	 * @param strXML XML encoded data
	 * @return identifying String (errors|response|request)
	 */
	public static String fetchMessageType(String strXML) {
		String type = null; //unknown
		Element mainElement = null;
		
		org.jdom.Document doc;
		SAXBuilder builder = new SAXBuilder();
		try {		
			doc = builder.build(new InputSource (new StringReader(strXML)));
			mainElement = (Element)(doc.getRootElement().getChildren()).get(0);
			type = mainElement.getName();
		} catch(Exception e) {
			logger.error("couldn't fetch message type - cue element: " + 
					      mainElement + " exception : " + e);
		}
		
		return type;
	}
	
}
