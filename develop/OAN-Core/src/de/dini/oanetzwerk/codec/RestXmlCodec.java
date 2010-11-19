package de.dini.oanetzwerk.codec;

import java.io.StringReader;
import java.util.ArrayList;
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
	
	final static boolean DEFAULT_FLAG_BASE64KEYS = false;
	final static boolean DEFAULT_FLAG_BASE64VALUES = true;
	
	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    // bisher nur lokal bei mir, könnte aber auf Scope1 deponiert werden (rm)
	private static final String XML_ROOT = "<oanrest xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://oanet.cms.hu-berlin.de/xsd/OANRESTMessage.xsd\">\n";

	
	private static String encodeBase64(String data) {
		try {
			return new String (Base64.encodeBase64 (data.getBytes()));
		} catch(Exception ex) {
			logger.error(ex.getLocalizedMessage ( ), ex);
			return null;
		}
	}
	
	private static String decodeBase64(String data) {
		try {
			return new String(Base64.decodeBase64(data.getBytes()));
		} catch(Exception ex) {
			logger.error(ex.getLocalizedMessage ( ), ex);
			return null;
		}
	}
	
	public static String encodeRestMessage(RestMessage msg) {
		return encodeRestMessage(msg, DEFAULT_FLAG_BASE64KEYS, DEFAULT_FLAG_BASE64VALUES);
	}
	
	public static String encodeRestMessage(RestMessage msg, boolean useBase64keys, boolean useBase64values) {
		StringBuffer sbXML = new StringBuffer();
		
		sbXML.append(XML_HEADER);
		sbXML.append(XML_ROOT);
		
		sbXML.append("  <keyword value=\"");
		sbXML.append(msg.getKeyword().toString());
		sbXML.append("\"/>\n");
		
		sbXML.append("  <resturl value=\"");
		sbXML.append(msg.getRestURL());
		sbXML.append("\"/>\n");
		
		sbXML.append("  <status value=\"");
		sbXML.append(msg.getStatus().toString());
		sbXML.append("\">\n");
		sbXML.append("    <description>");
		sbXML.append(msg.getStatusDescription());
		sbXML.append("</description>\n");
		sbXML.append("  </status>\n");
		
		if(msg.getListEntrySets() != null) {
			for(int i = 0; i < msg.getListEntrySets().size(); i++) {
				sbXML.append("  <entryset>\n");
				RestEntrySet restDataEntrySet = msg.getListEntrySets().get(i);
//				Iterator it = restDataEntrySet.getEntryHashMap().keySet().iterator();
				Iterator <String> it = restDataEntrySet.getKeyIterator();
				while(it.hasNext()) {
					String key = (String)it.next();
					sbXML.append("    <entry key=\"");
					if(useBase64keys) {
						sbXML.append(encodeBase64(key));						
					} else {
						sbXML.append(key);
					}				
					sbXML.append("\">");
					String value = restDataEntrySet.getEntryHashMap().get(key); 
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
				sbXML.append("  </entryset>\n");
			}
		} else {
			msg.setStatus(RestStatusEnum.REST_XML_ENCODING_ERROR);
			msg.setStatusDescription("list of entrySets is NULL");
		}
		
		sbXML.append("</oanrest>\n");
		
		return sbXML.toString();
	}
	
	public static RestMessage decodeRestMessage(String strXML) {
		return decodeRestMessage(strXML, DEFAULT_FLAG_BASE64KEYS, DEFAULT_FLAG_BASE64VALUES);
	}
	
	public static RestMessage decodeRestMessage(String strXML, boolean useBase64keys, boolean useBase64values) {
		RestMessage msg = new RestMessage();		
					
		org.jdom.Document doc;
		ElementFilter filter;
		Iterator <?> iterator;
		SAXBuilder builder = new SAXBuilder();
		
		if (strXML == null)
			return null;
		
		try {
			doc = builder.build(new InputSource (new StringReader(strXML)));
			
			logger.debug("** doc generated");

			filter = new ElementFilter("keyword");
			iterator = doc.getDescendants(filter);
			while (iterator.hasNext()) {				
				Element keywordElement = (Element) iterator.next();
				msg.setKeyword(RestKeyword.valueOf(keywordElement.getAttributeValue("value")));
			}

			filter = new ElementFilter("resturl");
			iterator = doc.getDescendants(filter);
			while (iterator.hasNext()) {				
				Element resturlElement = (Element) iterator.next();
				msg.setRestURL(resturlElement.getAttributeValue("value"));
			}
			
			filter = new ElementFilter("status");
			iterator = doc.getDescendants(filter);
			while (iterator.hasNext()) {
				
				Element statusElement = (Element) iterator.next();
				msg.setStatus(RestStatusEnum.valueOf(statusElement.getAttributeValue("value")));
				
				Element statusDescriptionElement = statusElement.getChild("description");
				if(statusDescriptionElement.getContentSize() > 0) {
					msg.setStatusDescription(((Text)statusDescriptionElement.getContent(0)).getValue());
				}
			}
			
			List<RestEntrySet> listEntrySets = new ArrayList<RestEntrySet>();
			
			filter = new ElementFilter("entryset");
			iterator = doc.getDescendants(filter);
			while (iterator.hasNext()) {
				
				logger.debug("** <entryset> found");
				
				Element elementEntrySet = (Element) iterator.next();
				RestEntrySet entrySet = new RestEntrySet();			
				filter = new ElementFilter("entry");
				Iterator <?> iterator2 = elementEntrySet.getDescendants(filter);
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
					
					String value = "";
					
					
					if(elementEntry.getContent() != null && elementEntry.getContent().size() > 0) {
						Content content = elementEntry.getContent(0);

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
					}
					
					logger.debug("** value == " + value);	
					
					entrySet.addEntry(key,value);
				}
				listEntrySets.add(entrySet);
								
			}
			
			msg.setListEntrySets(listEntrySets);
			
		} catch(Exception e) {
			logger.error("error while decoding XML String: " + e, e);
			
			msg.setStatus(RestStatusEnum.REST_XML_DECODING_ERROR);
			msg.setStatusDescription("error while decoding XML String: " + e + e.getCause());
		}
		
		return msg;
	}
	
}
