package de.dini.oanetzwerk.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.servicemodule.RestClient;
import de.dini.oanetzwerk.utils.imf.Author;
import de.dini.oanetzwerk.utils.imf.Classification;
import de.dini.oanetzwerk.utils.imf.DateValue;
import de.dini.oanetzwerk.utils.imf.Description;
import de.dini.oanetzwerk.utils.imf.Format;
import de.dini.oanetzwerk.utils.imf.Identifier;
import de.dini.oanetzwerk.utils.imf.InternalMetadata;
import de.dini.oanetzwerk.utils.imf.InternalMetadataJAXBMarshaller;
import de.dini.oanetzwerk.utils.imf.Keyword;
import de.dini.oanetzwerk.utils.imf.Language;
import de.dini.oanetzwerk.utils.imf.Publisher;
import de.dini.oanetzwerk.utils.imf.Title;
import de.dini.oanetzwerk.utils.imf.TypeValue;

/**
 * Der Inspector ist eine kleine Webanwendung um Funktionen der REST-Schnittstelle, inklusive des Codecs
 * und des Marshallings zu testen, und die Inhalte der Datenbank zu Kontrollzwecken abzufragen. 
 * 
 * @author malitzro
 *
 */
@SuppressWarnings("serial")
public class InspectorServlet extends HttpServlet {

	public final static String COLOR_TABLE_CONTENT = "LightGoldenrodYellow";
	public final static String COLOR_TABLE_LEFT = "Gold";
	public final static String COLOR_TABLE_TOP = "Goldenrod";
	public final static String COLOR_XML_ATTRVAL = "#990000";
	public final static String COLOR_XML_TAG = "#660000";
	
	private enum InspectorModeEnum {
		ObjectEntry,
		RawRecordData,
		InternalMetadataEntry,
		WorkflowDB,
		ServiceOrder,
		Services,
		ObjectEntryID,
		AllOIDs,
		DuplicatePossibility,
		TC_WrongKeyword,
		TC_WrongParams,
		TC_WrongUser,
		TC_WrongPW		
	}	
	
	private PrintWriter out;
	
	private String sParamOID;
	private String sSelfURL;
	private String sParamMarkedAs;
	private String sParamShowRestData;
	private String sParamShowCodecData;
	private InspectorModeEnum enumParamMode;
	
	private String sExtURLRestServer;
	private String sExtHarvUser;
	private String sExtHarvPass;
	private String sExtAggrUser;
	private String sExtAggrPass;
	
	private StringBuffer sbErrors;
	
	/**
	 * Diese Methode muss in jedem Servlet vorhanden sein. In diesem Fall initialisiert sie
	 * auch Parameter aus dem Request sowie dem Context aus der web.xml.
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		out = res.getWriter();
		
		sParamOID = StringUtils.defaultString(req.getParameter("OID"),"635");
		sParamMarkedAs = StringUtils.defaultString(req.getParameter("markedAs"),"");
		sParamShowRestData = StringUtils.defaultString(req.getParameter("ShowRestData"),"true");
		sParamShowCodecData = StringUtils.defaultString(req.getParameter("ShowCodecData"),"true");
		
		enumParamMode = InspectorModeEnum.valueOf(StringUtils.defaultString(req.getParameter("Mode"),"RawRecordData"));
		
//		sSelfURL = "http://lolita.cms.hu-berlin.de:8080/inspector/";
		sSelfURL = getServletContext().getInitParameter("SelfURL");		
				
		sExtURLRestServer = getServletContext().getInitParameter("URLRestServer");
		sExtHarvUser = getServletContext().getInitParameter("HarvUser");
		sExtHarvPass = getServletContext().getInitParameter("HarvPass");
		sExtAggrUser = getServletContext().getInitParameter("AggrUser");
		sExtAggrPass = getServletContext().getInitParameter("AggrPass");		
		
		sbErrors = new StringBuffer();
		
		out.write(renderContent(enumParamMode));
	}
	
	public void addErrorMsg(String msg) {
		sbErrors.append("<ul><li><font color=\"#FF0000\"><b>\n");
		sbErrors.append(StringEscapeUtils.escapeHtml(msg).replaceAll("\n", "<br/>\n"));
		sbErrors.append("\n</li></ul></b></font><br/>\n");
	}
	
	/**
	 * rendert den HTML-Response in Abhängigkeit des Zustands der Anwendung (d.h. der Requestparameter)
	 *
	 * auftretende Fehler werden im HTML-Format für eine spätere Ausgabe in den Response gesammelt
	 * 
	 * @param mode
	 * @return
	 */
	private String renderContent(InspectorModeEnum mode) {
		StringBuffer sb = new StringBuffer();

		InternalMetadataJAXBMarshaller imMarshaller = InternalMetadataJAXBMarshaller.getInstance();
		
		sb.append(renderHTMLHeader());
		sb.append(renderContentHeader());		
		
		if(mode != null) {

			sb.append("<br/>\n");
			sb.append("<table width=\"100%\" border=\"0\"><tr valign=\"top\"><td>\n");
			sb.append(renderModeForm());		
			sb.append("</td><td>\n");
			sb.append(renderParameterForm(mode));
			sb.append("</td></tr></table>\n");
			sb.append("<br/>\n");

			//sb.append("<h3>Parameter der WebApplikation</h3>\n");
			//sb.append("<b>OID: '" + sParamOID + "'</b><br/>\n");
			sb.append("<h3>Parsing</h3>\n");	
			
			String response = fetchRESTResponse(mode);
			
			if(mode == InspectorModeEnum.RawRecordData) {
																
				if(response != null) {
					sb.append(renderRESTResponse(response));

					String data = decodeRESTResponse(RestXmlCodec.decodeRestMessage(response), InspectorModeEnum.RawRecordData);
					sb.append(renderDecodedData(data));
				}

			} else if(mode == InspectorModeEnum.InternalMetadataEntry) {
			
				InternalMetadata myIM = null;
				
				if(response != null) {
					sb.append(renderRESTResponse(response));

					String data = decodeRESTResponse(RestXmlCodec.decodeRestMessage(response), InspectorModeEnum.InternalMetadataEntry);
					
					if(data == null) {
						data = imMarshaller.marshall(InternalMetadata.createDummy());
						addErrorMsg("Fehler beim Beziehen des gemarshallten IM\nmarshalle (korrekte) Musterinstanz des IM als Daten für das Unmarshalling");
					}
					
					sb.append(renderDecodedData(data));
					
					myIM = imMarshaller.unmarshall(data);
					
				}

				if(myIM == null) {
					myIM = InternalMetadata.createDummy();
					addErrorMsg("Fehler beim Unmarshalling des IM\nlege Musterinstanz des IM an");
				}
					
				sb.append("<h3>Webdarstellung eines InternalMetadata-Objektes</h3>\n");			
				
				sb.append(renderIM(myIM));
				
			} else if(mode == InspectorModeEnum.ObjectEntry) {
							
				if(response != null) {
					sb.append(renderRESTResponse(response));
					
					String data = decodeRESTResponse(RestXmlCodec.decodeRestMessage(response), InspectorModeEnum.ObjectEntry);

					sb.append(renderDecodedData(data));
				}
				 
			} else if (mode == InspectorModeEnum.WorkflowDB) {
				
				if(response != null) {
					sb.append(renderRESTResponse(response));

					String data = decodeRESTResponse(RestXmlCodec.decodeRestMessage(response), InspectorModeEnum.WorkflowDB);
					sb.append(renderDecodedData(data));
				}
				
			} else if(mode == InspectorModeEnum.TC_WrongUser ||
					  mode == InspectorModeEnum.TC_WrongPW ||
					  mode == InspectorModeEnum.TC_WrongKeyword ||
					  mode == InspectorModeEnum.TC_WrongParams) {
				
				if(response != null) {
					sb.append(renderRESTResponse(response));
					String data = decodeRESTResponse(RestXmlCodec.decodeRestMessage(response), mode);
					sb.append(renderDecodedData(data));
					
				}
					
			} else {
				
				if(response != null) {
					sb.append(renderRESTResponse(response));

					String data = decodeRESTResponse(RestXmlCodec.decodeRestMessage(response), mode);
					sb.append(renderDecodedData(data));
				}
				
			}

		}
				
		sb.append(sbErrors.toString());
		
		sb.append(renderHTMLFooter());
		
		return sb.toString();
	}
	
	/**
	 * main header für ein gültiges HTML Dokument
	 * 
	 * @return
	 */
	private String renderHTMLHeader() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("<html>\n");
		sb.append("<head>\n");
		sb.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">\n");
		sb.append("<title>OA-Netzwerk Inspector</title>\n");
		sb.append("</head>\n");
		sb.append("<body bgcolor=\"#FFFFFF\">\n");
		
		return sb.toString();
	}
	
	/**
	 * schlißende Tags für gültiges HTML Dokument
	 * 
	 * @return
	 */
	private String renderHTMLFooter() {
		StringBuffer sb = new StringBuffer();
		sb.append("</body>\n");
		sb.append("</html>\n");
		return sb.toString();
	}
	
	/**
	 * Titel der Webapplikation mit CI des Projektes
	 * 
	 * @return
	 */
	private String renderContentHeader() {
		StringBuffer sb = new StringBuffer();
		sb.append("<table border=\"0\">\n<tr>\n");
		sb.append("<td><img src=\"" + sSelfURL + "img/Logo_oan_rgb_micro.PNG\"/></td>\n");
		sb.append("<td widht=\"20\">&nbsp;</td>\n");
		sb.append("<td valign=\"center\"><font face=\"Helvetica,Arial\" size=\"+8\"><b>INSPECTOR</b></font><small> (Entwicklungswerkzeug zum &Uuml;berwachen der Infrastruktur)</small></td>\n");
		sb.append("</tr>\n</table>\n");
		return sb.toString();
	}
	
	/**
	 * modusabhängige Initialisierung eines internen RESTClients, welcher die externe REST-Schnittstelle
	 * über HTTP auf bestimmte Art und Weise abfragt
	 * 
	 * auftretende Fehler werden im HTML-Format für eine spätere Ausgabe in den Response gesammelt
	 * 
	 * @param mode
	 * @return
	 */
	private String fetchRESTResponse(InspectorModeEnum mode) {
		
		RestClient restclient = null;
		String ressource = null;
		
		if(mode != null) {		
			switch (mode) {
			case ObjectEntry:
				ressource = "ObjectEntry/" + sParamOID;
				restclient = RestClient.createRestClient(sExtURLRestServer, ressource, sExtHarvUser, sExtHarvPass);	
				break;			
			case RawRecordData:
				ressource = "RawRecordData/" + sParamOID;
				restclient = RestClient.createRestClient(sExtURLRestServer, ressource, sExtHarvUser, sExtHarvPass);	
				break;
			case InternalMetadataEntry:
				ressource = "InternalMetadataEntry/" + sParamOID;
				restclient = RestClient.createRestClient(sExtURLRestServer, ressource, sExtAggrUser, sExtAggrPass);
				break;
			case WorkflowDB: 
				//TODO: use an own servlet parameter!
				ressource =  "WorkflowDB/2/2"; //"WorkflowDB/" + sParamOID + "/" + sParamOID;
				restclient = RestClient.createRestClient(sExtURLRestServer, ressource, sExtAggrUser, sExtAggrPass);				
				break;
			case ObjectEntryID:
				ressource =  "ObjectEntryID/";
				restclient = RestClient.createRestClient(sExtURLRestServer, ressource, sExtAggrUser, sExtAggrPass);								
				break;
			case ServiceOrder:
				ressource =  "ServiceOrder/";
				restclient = RestClient.createRestClient(sExtURLRestServer, ressource, sExtAggrUser, sExtAggrPass);								
				break;
			case Services:
				ressource =  "Services/";
				restclient = RestClient.createRestClient(sExtURLRestServer, ressource, sExtAggrUser, sExtAggrPass);								
				break;
			case AllOIDs:
				ressource = "AllOIDs/";
				if(sParamMarkedAs.length() > 0) {
					ressource = "AllOIDs/markedAs/" + sParamMarkedAs;
				} 
				restclient = RestClient.createRestClient(sExtURLRestServer, ressource, sExtAggrUser, sExtAggrPass);								
				break;
			case TC_WrongUser:
				ressource = "RawRecordData/" + sParamOID;
				restclient = RestClient.createRestClient(sExtURLRestServer, ressource, "lalelu", sExtHarvPass);
				break;
			case TC_WrongPW:
				ressource = "RawRecordData/" + sParamOID;
				restclient = RestClient.createRestClient(sExtURLRestServer, ressource, sExtHarvUser, "lalelu");
				break;
			case TC_WrongKeyword:
				ressource = "NoSuchKeyword/" + sParamOID;
				restclient = RestClient.createRestClient(sExtURLRestServer, ressource, sExtHarvUser, sExtHarvPass);
				break;
			case TC_WrongParams:
				ressource = "RawRecordData/";
				restclient = RestClient.createRestClient(sExtURLRestServer, ressource, sExtHarvUser, sExtHarvPass);
				break;
			}
		}
			
		if(restclient == null) {
			addErrorMsg("Fehler beim Beziehen der Daten über die Rest-Schnittstelle\nREST-Client konnte nicht initialisiert werden");
			return null;
		}
		
		String result = restclient.GetData();		
		if(result == null) {
			addErrorMsg("Fehler beim Beziehen der Daten über die Rest-Schnittstelle\nREST-Response ist null");
		}
		
		return result;
	}
	
	/**
	 * erzeugt ein HTML-Fragment des Response
	 * unterscheidet HTML und XML und eskapiert beide entsprechend aus
	 * fügt Links zum Verbergen ein
	 * 
	 * @param response
	 * @return
	 */
	private String renderRESTResponse(String response) {
		StringBuffer sb = new StringBuffer();
		sb.append("<h4>von der Rest-Schnittstelle &uuml;bermittelte Daten:</h4>\n");
		if(sParamShowRestData.equals("true")) {
			sb.append("<a href=\"");
			//Todo: neue Parameter einbinden wie markedAs
			sb.append(encodeParameterIntoURL(sSelfURL, sParamOID, "false", sParamShowCodecData, enumParamMode.toString()));
			sb.append("\"><small> (verbergen)</small></a><br/>\n");
			if(response.indexOf("<html>") != -1) {
				sb.append(frameHTMLString(response));
			} else {
				sb.append(frameXMLString(response,true));
			}
		} else {
			sb.append("<a href=\"");
			sb.append(encodeParameterIntoURL(sSelfURL, sParamOID, "true", sParamShowCodecData, enumParamMode.toString()));
			sb.append("\"><small> (anzeigen)</small></a><br/>\n");
		}
		return sb.toString();
	}
	
	/**
	 * extrahiert modusabhägig mittels RestXMLCodec die Daten aus einem response 
	 * 
	 * auftretende Fehler werden im HTML-Format für eine spätere Ausgabe in den Response gesammelt
	 *
	 * @param response
	 * @param mode
	 * @return
	 */
	private String decodeRESTResponse(RestMessage responseMsg, InspectorModeEnum mode) {
		StringBuffer result = new StringBuffer();		
		
		switch(responseMsg.getStatus()) {
			case OK:
				// alles ok!
				break;
			default:
				addErrorMsg("Fehler beim Beziehen der Daten über die Rest-Schnittstelle\n" +
						    responseMsg.getStatus() + "\n" + responseMsg.getStatusDescription() + "\n");
				return null;				
		}
			
		List<RestEntrySet> listEntrySet = responseMsg.getListEntrySets();
		
		if (listEntrySet != null && listEntrySet.size() > 0) {
						
			String key = "";
			String value = "";
			Iterator<String> it;
			RestEntrySet entrySet;
			
			switch(mode) {						
			case RawRecordData:
				entrySet = listEntrySet.get(0);
				it = entrySet.getKeyIterator();
				while (it.hasNext()) {
					key = it.next();
					if (key.equalsIgnoreCase("data")) {
						value = entrySet.getValue(key);					
						value = decodeBase64(value);
						result.append(value);
						return result.toString();
					}
				}
				break;		
			case InternalMetadataEntry: 
				entrySet = listEntrySet.get(0);
				it = entrySet.getKeyIterator();
				while (it.hasNext()) {
					key = it.next();
					if (key.equalsIgnoreCase("internalmetadata")) {
						value = entrySet.getValue(key);							
						result.append(value);						
						return result.toString();
					}
				}
				break;
			default:
				for(RestEntrySet entrySet2: listEntrySet) {			
					it = entrySet2.getKeyIterator();
					result.append("Eintrag:\n");
					while (it.hasNext()) {
						key = it.next();
						value = entrySet2.getValue(key);
						result.append("  Schluessel = ");
						result.append(key + "\n");
						result.append("  Wert = ");
						result.append(value + "\n");
					}				
				}
				break;
			}
			
		} else {
			addErrorMsg("Fehler beim Beziehen der Daten über die Rest-Schnittstelle\nkeine Daten im REST-Response");
			return null;
		}
		
		return result.toString();
	}
		
	/**
 	 * erzeugt ein HTML-Fragment aus den Codec-extrahierten Daten
	 * fügt Links zum Verbergen ein
	 * 
	 */
	private String renderDecodedData(String data) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("<h4>vom RestXMLCodec extrahierte Daten:</h4>\n");
		if(sParamShowCodecData.equals("true")) {
			sb.append("<a href=\"");
			sb.append(encodeParameterIntoURL(sSelfURL, sParamOID, sParamShowRestData, "false", enumParamMode.toString()));
			sb.append("\"><small> (verbergen)</small></a><br/>\n");			
			sb.append(frameXMLString(data,true));
		} else {
			sb.append("<a href=\"");
			sb.append(encodeParameterIntoURL(sSelfURL, sParamOID, sParamShowRestData, "true", enumParamMode.toString()));
			sb.append("\"><small> (anzeigen)</small></a><br/>\n");
		}

		return sb.toString();
	}
	
	/**
	 * Hilfmethode um die Requestparamter in eine URL auf die Webapplikation zu mappen
	 * 
	 * @param baseURL
	 * @param OID
	 * @param ShowRestData
	 * @param ShowCodecData
	 * @param Mode
	 * @return
	 */
	private String encodeParameterIntoURL(String baseURL, String OID, String ShowRestData, String ShowCodecData, String Mode) {
		StringBuffer sb = new StringBuffer();
		sb.append(sSelfURL);
		sb.append("index.html");
		sb.append("?OID=");
		sb.append(OID);
		sb.append("&ShowRestData=");
		sb.append(ShowRestData);
		sb.append("&ShowCodecData=");
		sb.append(ShowCodecData);	
		sb.append("&Mode=");
		sb.append(Mode);
		return sb.toString();
	}
	
	/**
	 * rendert XML zu HTML-Fragment
	 * 
	 * @param content
	 * @param xmlSyntaxHighlight
	 * @return
	 */
	private String frameXMLString(String content, boolean xmlSyntaxHighlight) {
		StringBuffer sb = new StringBuffer();

		if(content == null) {
			content = "<b>(NULL)</b>";
		} else {
		
			content = StringEscapeUtils.escapeHtml(content).replaceAll("\n", "<br/>\n");
			if(xmlSyntaxHighlight) {
				content = content.replaceAll(" ", "&nbsp;");
				content = content.replaceAll("&lt;", "<b>&lt;<FONT COLOR=\""+COLOR_XML_TAG+"\">");
				content = content.replaceAll("&gt;", "</FONT>&gt;</b>");
				content = content.replaceAll("&quot;", "&quot;</FONT>");
				content = content.replaceAll("=&quot;</FONT>", "=<FONT COLOR=\""+COLOR_XML_ATTRVAL+"\">&quot;");
			}
				
		}
		
		sb.append("<table width=\"100%\" border=\"1\" cellspacing=\"1\" cellpadding=\"10\">\n");
		sb.append("<tr>\n");
		sb.append("<td bgcolor=\"" + COLOR_TABLE_CONTENT + "\" ><FONT FACE=\"monospace\">\n");
		sb.append(content);
		sb.append("</FONT></td>\n");
		sb.append("</tr>\n");		
		sb.append("</table>\n");
		
		return sb.toString();
	}
	
	/**
	 * rendert HTML zu HTML-Fragment (zur Einbettung in weiteres HTML)
	 * 
	 * @param content
	 * @return
	 */
	private String frameHTMLString(String content) {
		StringBuffer sb = new StringBuffer();

		if(content == null) return "(null)";
		
		content = content.substring(content.indexOf("<body>")+6, content.indexOf("</body>"));
				
		sb.append("<table width=\"100%\" border=\"1\" cellspacing=\"1\" cellpadding=\"10\">\n");
		sb.append("<tr>\n");
		sb.append("<td bgcolor=\"" + COLOR_TABLE_CONTENT + "\" >\n");
		sb.append(content);
		sb.append("</td>\n");
		sb.append("</tr>\n");		
		sb.append("</table>\n");
		
		return sb.toString();
	}
	
	/**
	 * Hilfsmethode zum Dekodierenvon Base64
	 * 
	 * @param data
	 * @return
	 */
	private String decodeBase64(String data) {
		try {
			return new String(Base64.decodeBase64(((String) data).getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * rendert das Formular zum Angeben einer anderen OID als HTML-Fragment
	 * 
	 * @return
	 */
	private String renderParameterForm(InspectorModeEnum mode) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("<small>Parameterauswahl: </small><br/>");
		sb.append("<form action=\"" + sSelfURL + "index.html\" method=\"get\">\n");
		sb.append("<fieldset>\n");
		switch(mode) {
		  case AllOIDs:
				sb.append("<label for=\"markedAs\">Markierung: </label>");
				sb.append("<input id=\"markedAs\" name=\"markedAs\" type=\"text\" size=\"30\" maxlength=\"30\" value=\"" + sParamMarkedAs + "\"/>\n");
				sb.append("<input type=\"submit\" value=\" Anfrage auf diese Markierung \">");
				sb.append("<input type=\"hidden\" name=\"ShowRestData\" value=\"" + sParamShowRestData + "\"/>");
				sb.append("<input type=\"hidden\" name=\"ShowCodecData\" value=\"" + sParamShowCodecData + "\"/>");
				sb.append("<input type=\"hidden\" name=\"Mode\" value=\"" + enumParamMode + "\"/>");	
				break;
		  default:
//				sb.append("<legend>Welches Objekt soll abgerufen werden?</legend>\n");
				sb.append("<label for=\"oid_input\">OID: </label>");
				sb.append("<input id=\"oid_input\" name=\"OID\" type=\"text\" size=\"30\" maxlength=\"30\" value=\"" + sParamOID + "\"/>\n");
				sb.append("<input type=\"submit\" value=\" Anfrage auf diese OID \">");
				sb.append("<input type=\"hidden\" name=\"ShowRestData\" value=\"" + sParamShowRestData + "\"/>");
				sb.append("<input type=\"hidden\" name=\"ShowCodecData\" value=\"" + sParamShowCodecData + "\"/>");
				sb.append("<input type=\"hidden\" name=\"Mode\" value=\"" + enumParamMode + "\"/>");			  			 
		}
		sb.append("</fieldset>\n");
		sb.append("</form>\n");
		
		return sb.toString();
	}
	
	/**
	 * render die Liste der verfügbaren Modi als Links auf die Webapplikation mit entsprechenden
	 * Parametern als HTML-Fragment
	 * 
	 * @return
	 */
	private String renderModeForm() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("<small>Testvarianten: </small><br/>");
		
		sb.append("<table cellspacing=\"1\" cellpadding=\"5\" width=\"100%\" border=\"1\"><tr>");
		
		int count = 1;
		int size = InspectorModeEnum.values().length;
		int colsPerRow = 3;
		
		for(InspectorModeEnum mode : InspectorModeEnum.values()) {
			String colspan="";
			//if(count==size) {
			//	colspan=" colspan=\"" + (colsPerRow - count % colsPerRow + 1) + "\"";
			//}
			if(mode == enumParamMode) {
				sb.append("<td width=\"150\""+colspan+" bgcolor=\"Goldenrod\"><center>[");
				sb.append(mode.toString());
				sb.append("]</center></td> ");
			} else {
				sb.append("<td width=\"150\""+colspan+" bgcolor=\"#EEEEEE\"><center><a href=\"");
				sb.append(encodeParameterIntoURL(sSelfURL, sParamOID, sParamShowRestData, sParamShowCodecData, mode.toString()));
				sb.append("\">[");
				sb.append(mode.toString());
				sb.append("]</a></center></td> ");
			}
			if(count % colsPerRow == 0 && count != size) {
				//sb.append("<br/>");
				sb.append("<tr/><tr>");
			}
			count++;
		}

		for(int i = count % colsPerRow; i > 0; i--) {
			sb.append("<td width=\"150\" bgcolor=\"#EEEEEE\">&nbsp;</td>");
		}
		
		sb.append("</tr></table>");

		
		return sb.toString();
	}
	
	
// -------- Webdarstellung IMF------------------------------------------------------------------------------------------------------------------
	
	private static final String ENTRY_HTML_HEADER = "<table width=\"100%\" border=\"1\" cellspacing=\"0\" cellpadding=\"10\"><tr><td>\n<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n";   
	private static final String ENTRY_HTML_FOOTER = "</table>\n</td></tr></table>\n";
	
	private String renderEntryContent(String label, String content) {
		StringBuffer sb = new StringBuffer();
		sb.append("<tr>\n<td width=\"100\">\n<b>");
		sb.append(label);
		sb.append(":</b></td>\n<td>\n");
		sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"1\"><tr><td bgcolor=\"white\">\n");
		sb.append(StringEscapeUtils.escapeHtml(content).replaceAll("\n", "<br/>\n").replaceAll(" ", "&nbsp;"));
		sb.append("\n</td></tr></table>\n");
		sb.append("</td>\n</tr>\n");
		return sb.toString();
	}
	
	private String renderTitles(List<Title> titles) {
		StringBuffer sb = new StringBuffer();
		for(Title title : titles) {
			sb.append(ENTRY_HTML_HEADER);			
			sb.append(renderEntryContent("Nummer",""+title.getNumber()));
			sb.append(renderEntryContent("Sprache",""+title.getLang()));
			sb.append(renderEntryContent("Qualifier",""+title.getQualifier()));
			sb.append(renderEntryContent("Titel",""+title.getTitle()));
			sb.append(ENTRY_HTML_FOOTER);	
		}			
		return sb.toString();
	}
	
	private String renderAuthors(List<Author> authors) {
		StringBuffer sb = new StringBuffer();	
		for(Author author : authors) {
			sb.append(ENTRY_HTML_HEADER);			
			sb.append(renderEntryContent("Nummer",""+author.getNumber()));
			sb.append(renderEntryContent("Vorname",""+author.getFirstname()));
			sb.append(renderEntryContent("Nachname",""+author.getLastname()));
			sb.append(ENTRY_HTML_FOOTER);				
		}			
		return sb.toString();
	}
	
	private String renderDateValues(List<DateValue> dvs) {
		StringBuffer sb = new StringBuffer();	
		for(DateValue dv : dvs) {
			sb.append(ENTRY_HTML_HEADER);			
			sb.append(renderEntryContent("Nummer",""+dv.getNumber()));
			sb.append(renderEntryContent("Datum",""+dv.getDateValue()));
			sb.append(ENTRY_HTML_FOOTER);				
		}			
		return sb.toString();
	}

	private String renderDescriptions(List<Description> descs) {
		StringBuffer sb = new StringBuffer();	
		for(Description desc : descs) {
			sb.append(ENTRY_HTML_HEADER);			
			sb.append(renderEntryContent("Nummer",""+desc.getNumber()));
			sb.append(renderEntryContent("Sprache",""+desc.getLanguage()));
			sb.append(renderEntryContent("Beschreibung",""+desc.getDescription()));
			sb.append(ENTRY_HTML_FOOTER);				
		}			
		return sb.toString();
	}
	
	private String renderKeywords(List<Keyword> keywords) {
		StringBuffer sb = new StringBuffer();	
		for(Keyword keyword : keywords) {
			sb.append(ENTRY_HTML_HEADER);			
			sb.append(renderEntryContent("Sprache",""+keyword.getLanguage()));
			sb.append(renderEntryContent("Schlagwort",""+keyword.getKeyword()));
			sb.append(ENTRY_HTML_FOOTER);				
		}			
		return sb.toString();
	}

	private String renderFormats(List<Format> formats) {
		StringBuffer sb = new StringBuffer();	
		for(Format format : formats) {
			sb.append(ENTRY_HTML_HEADER);			
			sb.append(renderEntryContent("Nummer",""+format.getNumber()));
			sb.append(renderEntryContent("Format",""+format.getSchema_f()));
			sb.append(ENTRY_HTML_FOOTER);				
		}			
		return sb.toString();
	}
	
	private String renderPublishers(List<Publisher> publishers) {
		StringBuffer sb = new StringBuffer();	
		for(Publisher publisher : publishers) {
			sb.append(ENTRY_HTML_HEADER);			
			sb.append(renderEntryContent("Nummer",""+publisher.getNumber()));
			sb.append(renderEntryContent("Name",""+publisher.getName()));
			sb.append(ENTRY_HTML_FOOTER);				
		}			
		return sb.toString();
	}
	
	private String renderLanguages(List<Language> langs) {
		StringBuffer sb = new StringBuffer();	
		for(Language lang : langs) {
			sb.append(ENTRY_HTML_HEADER);			
			sb.append(renderEntryContent("Nummer",""+lang.getNumber()));
			sb.append(renderEntryContent("Sprache",""+lang.getLanguage()));
			sb.append(ENTRY_HTML_FOOTER);				
		}			
		return sb.toString();
	}
	
	private String renderTypes(List<TypeValue> tvs) {
		StringBuffer sb = new StringBuffer();	
		for(TypeValue tv : tvs) {
			sb.append(ENTRY_HTML_HEADER);			
			sb.append(renderEntryContent("Nummer",""+tv.getNumber()));
			sb.append(renderEntryContent("Typus",""+tv.getTypeValue()));
			sb.append(ENTRY_HTML_FOOTER);				
		}			
		return sb.toString();
	}
	
	private String renderIdentifiers(List<Identifier> idents) {
		StringBuffer sb = new StringBuffer();	
		for(Identifier ident : idents) {
			sb.append(ENTRY_HTML_HEADER);			
			sb.append(renderEntryContent("Nummer",""+ident.getNumber()));
			sb.append(renderEntryContent("Identifier",""+ident.getIdentifier()));
			sb.append(ENTRY_HTML_FOOTER);				
		}			
		return sb.toString();
	}
	
	private String renderClassifications(List<Classification> classis) {
		StringBuffer sb = new StringBuffer();	
		for(Classification classi : classis) {
			sb.append(ENTRY_HTML_HEADER);			
			sb.append(renderEntryContent("Klasse",""+classi.getClass()));
			sb.append(renderEntryContent("Wert",""+classi.getValue()));
			sb.append(ENTRY_HTML_FOOTER);				
		}			
		return sb.toString();
	}
	
	private String renderWrappedIMElement(String leftLabel, String content) {
		StringBuffer sb = new StringBuffer();
		sb.append("<tr>\n");
		sb.append("<td width=\"150\" bgcolor=\"" + COLOR_TABLE_LEFT + "\" valign=\"top\"><b><big>" + leftLabel + "</big></b></td>\n");
		sb.append("<td bgcolor=\"" + COLOR_TABLE_CONTENT + "\" >\n");
		sb.append(content);
		sb.append("</td>\n");
		sb.append("</tr>\n");
		return sb.toString();
	}
	
	private String renderIM(InternalMetadata im) {
		StringBuffer sb = new StringBuffer();
		sb.append("<table width=\"100%\" border=\"1\" cellspacing=\"1\" cellpadding=\"10\">\n");
		
		sb.append("<tr>\n");
		sb.append("<td colspan=\"2\" bgcolor=\"" + COLOR_TABLE_TOP + "\" valign=\"top\"><b><big>OID: " + sParamOID + "</big></b></td>\n");
		sb.append("</tr>\n");
		
		sb.append(renderWrappedIMElement("Titel", renderTitles(im.getTitles())));
		sb.append(renderWrappedIMElement("Typen", renderTypes(im.getTypeValueList())));
		sb.append(renderWrappedIMElement("Autoren", renderAuthors(im.getAuthors())));
		sb.append(renderWrappedIMElement("Herausgeber", renderPublishers(im.getPublishers())));
		sb.append(renderWrappedIMElement("Datum", renderDateValues(im.getDateValues())));
		sb.append(renderWrappedIMElement("Formate", renderFormats(im.getFormatList())));
		sb.append(renderWrappedIMElement("Sprachen", renderLanguages(im.getLanguageList())));
		sb.append(renderWrappedIMElement("Schlagworte", renderKeywords(im.getKeywords())));
		sb.append(renderWrappedIMElement("Beschreibungen", renderDescriptions(im.getDescriptions())));
		sb.append(renderWrappedIMElement("Identifier", renderIdentifiers(im.getIdentifierList())));
		sb.append(renderWrappedIMElement("Klassifikation", renderClassifications(im.getClassificationList())));
		
		sb.append("</table>\n");
		return sb.toString();
	}
}
