package de.dini.oanetzwerk.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import de.dini.oanetzwerk.servicemodule.RestClient;
import de.dini.oanetzwerk.utils.imf.Author;
import de.dini.oanetzwerk.utils.imf.Classification;
import de.dini.oanetzwerk.utils.imf.DateValue;
import de.dini.oanetzwerk.utils.imf.Description;
import de.dini.oanetzwerk.utils.imf.Format;
import de.dini.oanetzwerk.utils.imf.Identifier;
import de.dini.oanetzwerk.utils.imf.InternalMetadata;
import de.dini.oanetzwerk.utils.imf.Keyword;
import de.dini.oanetzwerk.utils.imf.Language;
import de.dini.oanetzwerk.utils.imf.Publisher;
import de.dini.oanetzwerk.utils.imf.Title;
import de.dini.oanetzwerk.utils.imf.TypeValue;

@SuppressWarnings("serial")
public class InspectorServlet extends HttpServlet {

	public final static String COLOR_TABLE_CONTENT = "LightGoldenrodYellow";
	public final static String COLOR_TABLE_LEFT = "Gold";
	public final static String COLOR_TABLE_TOP = "Goldenrod";
	
	private PrintWriter out;
	
	private String sParamOID;
	private String sSelfURL;
	private String sParamShowRestData;
	private String sParamShowCodecData;
	private String sParamMode;
	
	private StringBuffer sbErrors;
	
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		out = res.getWriter();
		
		sParamOID = StringUtils.defaultString(req.getParameter("OID"),"633");
		sParamShowRestData = StringUtils.defaultString(req.getParameter("ShowRestData"),"true");
		sParamShowCodecData = StringUtils.defaultString(req.getParameter("ShowCodecData"),"true");
		sParamMode = StringUtils.defaultString(req.getParameter("Mode"),"RawRecordData");
		
		sSelfURL = "http://lolita.cms.hu-berlin.de:8080/inspector/";//getServletContext().getInitParameter("");
				
		sbErrors = new StringBuffer();
		
		out.write(renderContent(sParamMode));
	}

	private String renderContent(String mode) {
		StringBuffer sb = new StringBuffer();

		sb.append(renderHTMLHeader());
		sb.append(renderContentHeader());		
		
		if(mode != null) {

			sb.append("<br/>\n");
			sb.append(renderModeForm());		
			sb.append(renderOIDForm());
			sb.append("<br/>\n");

			sb.append("<h3>Parameter der WebApplikation</h3>\n");
			sb.append("<b>OID: '" + sParamOID + "'</b><br/>\n");
			sb.append("<h3>Parsing</h3>\n");	
			
			if(mode.equals("RawRecordData")) {
												
				String response = fetchRESTResponse(mode);
				if(response != null) {
					sb.append(renderRESTResponse(response));

					String data = decodeRESTResponse_RawData(response);
					sb.append(renderDecodedData(data));
				}

			} else if(mode.equals("InternalMetadataEntry")) {
			
				String response = fetchRESTResponse(mode);
				if(response != null) {
					sb.append(renderRESTResponse(response));

					String data = decodeRESTResponse_RawData(response);
					sb.append(renderDecodedData(data));

				}
				sb.append("<h3>Webdarstellung eines InternalMetadata-Objektes</h3>\n");

				InternalMetadata myIM = InternalMetadata.createDummy();

				sb.append(renderIM(myIM));
				
			} else {
				
				sb.append("<br/>keine Daten verf&uuml;gbar<br/><br/>");
				
			}

		}
		
		sb.append(sbErrors.toString());
		
		sb.append(renderHTMLFooter());
		
		return sb.toString();
	}
	
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
	
	private String renderHTMLFooter() {
		StringBuffer sb = new StringBuffer();
		sb.append("</body>\n");
		sb.append("</html>\n");
		return sb.toString();
	}
	
	private String renderContentHeader() {
		StringBuffer sb = new StringBuffer();
		sb.append("<table border=\"0\">\n<tr>\n");
		sb.append("<td><img src=\"" + sSelfURL + "img/logo_s.PNG\"/></td>\n");
		sb.append("<td widht=\"20\">&nbsp;</td>\n");
		sb.append("<td valign=\"center\"><font size=\"+4\"><b>INSPECTOR</b></font><br/>Entwicklungswerkzeug zum &Uuml;berwachen der Infrastruktur</td>\n");
		sb.append("</tr>\n</table>\n");
		return sb.toString();
	}
	
	private String fetchRESTResponse(String mode) {
		
		RestClient restclient = null;
		
		if(mode != null) {		
			if(mode.equals("RawRecordData")) {					
				String ressource = "RawRecordData/" + sParamOID;
				restclient = RestClient.createRestClient("xml7.cms.hu-berlin.de", ressource, "Harvester", "retsevrah");		
			} else if(mode.equals("InternalMetadataEntry")) {			
				String ressource = "InternalMetadataEntry/" + sParamOID;
				restclient = RestClient.createRestClient("xml7.cms.hu-berlin.de", ressource, "Aggregator", "rotagergga");				
			}		
		}
			
		if(restclient == null) {
			sbErrors.append("<br/><i>Fehler beim Beziehen der Daten &uuml;ber die Rest-Schnittstelle</i><br/>\n");
			sbErrors.append("<br/><i>REST-Client konnte nicht initialisiert werden</i><br/>\n");	
			return null;
		}
		
		String result = restclient.GetData();		
		if(result == null) {
			sbErrors.append("<br/><i>Fehler beim Beziehen der Daten &uuml;ber die Rest-Schnittstelle</i><br/>\n");
			sbErrors.append("<br/><i>REST-Response ist null</i><br/>\n");		
		}
		
		return result;
	}
	
	private String renderRESTResponse(String response) {
		StringBuffer sb = new StringBuffer();
		sb.append("<h4>von der Rest-Schnittstelle &uuml;bermittelte Daten:</h4>\n");
		if(sParamShowRestData.equals("true")) {
			sb.append("<a href=\"");
			sb.append(encodeParameterIntoURL(sSelfURL, sParamOID, "false", sParamShowCodecData, sParamMode));
			sb.append("\"><small> (verbergen)</small></a><br/>\n");
			if(response.indexOf("<html>") != -1) {
				sb.append(frameHTMLString(response));
			} else {
				sb.append(frameXMLString(response,true));
			}
		} else {
			sb.append("<a href=\"");
			sb.append(encodeParameterIntoURL(sSelfURL, sParamOID, "true", sParamShowCodecData, sParamMode));
			sb.append("\"><small> (anzeigen)</small></a><br/>\n");
		}
		return sb.toString();
	}
	
	private String decodeRESTResponse_RawData(String response) {
				
		// Resultat ist ein XML-Fragment, hier muss das Resultat noch aus dem
		// XML extrahiert werden

		List<HashMap<String, String>> listentries = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> mapEntry = new HashMap<String, String>();

		listentries = null;
		mapEntry = null;

		String sMessageType = RestXmlCodec.fetchMessageType(response);
		
		if(sMessageType != null) {
			if (!sMessageType.equals("response")) {
				sbErrors.append("<br/><i>Fehler beim Beziehen der Daten &uuml;ber die Rest-Schnittstelle</i><br/>\n");
				sbErrors.append("<br/><i>error response</i><br/>\n");
				return null;
			}
		} else {
			sbErrors.append("<br/><i>Fehler beim Beziehen der Daten &uuml;ber die Rest-Schnittstelle</i><br/>\n");
			sbErrors.append("<br/><i>message type unbekannt</i><br/>\n");
			return null;
		}
		
		listentries = RestXmlCodec.decodeEntrySet(response);
					
		if (listentries != null && listentries.size() > 0) {
			
			mapEntry = listentries.get(0);
			Iterator<String> it = mapEntry.keySet().iterator();
			String key = "";
			String value = "";

			while (it.hasNext()) {

				key = it.next();

				if (key.equalsIgnoreCase("data")) {
					value = mapEntry.get(key);					
					value = decodeBase64(value);
					return value;
				}
			}
			
		} else {
			sbErrors.append("<br/><i>Fehler beim Beziehen der Daten &uuml;ber die Rest-Schnittstelle</i><br/>\n");
			sbErrors.append("<br/><i>keine Daten im REST-Response</i><br/>\n");
			return null;
		}
		
		return null;
	}
	
	private String renderDecodedData(String data) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("<h4>vom RestXMLCodec extrahierte Daten:</h4>\n");
		if(sParamShowCodecData.equals("true")) {
			sb.append("<a href=\"");
			sb.append(encodeParameterIntoURL(sSelfURL, sParamOID, sParamShowRestData, "false", sParamMode));
			sb.append("\"><small> (verbergen)</small></a><br/>\n");			
			sb.append(frameXMLString(data,true));
		} else {
			sb.append("<a href=\"");
			sb.append(encodeParameterIntoURL(sSelfURL, sParamOID, sParamShowRestData, "true", sParamMode));
			sb.append("\"><small> (anzeigen)</small></a><br/>\n");
		}

		return sb.toString();
	}
	
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
	
	private String frameXMLString(String content, boolean xmlSyntaxHighlight) {
		StringBuffer sb = new StringBuffer();

		if(content == null) return "(null)";
		
		content = StringEscapeUtils.escapeHtml(content).replaceAll("\n", "<br/>\n");
		if(xmlSyntaxHighlight) {
			content = content.replaceAll("&lt;", "<b><small>&lt;");
			content = content.replaceAll("&gt;", "&gt;</small></b>");
			content = content.replaceAll(" ", "&nbsp;");
		}
				
		sb.append("<table width=\"100%\" border=\"1\" cellspacing=\"1\" cellpadding=\"10\">\n");
		sb.append("<tr>\n");
		sb.append("<td bgcolor=\"" + COLOR_TABLE_CONTENT + "\" >\n");
		sb.append(content);
		sb.append("</td>\n");
		sb.append("</tr>\n");		
		sb.append("</table>\n");
		
		return sb.toString();
	}
	
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
	
	private String decodeBase64(String data) {
		try {
			return new String(Base64.decodeBase64(((String) data).getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String renderOIDForm() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("<form action=\"" + sSelfURL + "index.html\" method=\"get\">\n");
		sb.append("<fieldset>\n");
//		sb.append("<legend>Welches Objekt soll abgerufen werden?</legend>\n");
		sb.append("<label for=\"oid_input\">OID: </label>");
		sb.append("<input id=\"oid_input\" name=\"OID\" type=\"text\" size=\"30\" maxlength=\"30\" value=\"" + sParamOID + "\"/>\n");
		sb.append("<input type=\"submit\" value=\" Anfrage auf diese OID \">");
		sb.append("<input type=\"hidden\" name=\"ShowRestData\" value=\"" + sParamShowRestData + "\"/>");
		sb.append("<input type=\"hidden\" name=\"ShowCodecData\" value=\"" + sParamShowCodecData + "\"/>");
		sb.append("<input type=\"hidden\" name=\"Mode\" value=\"" + sParamMode + "\"/>");
		sb.append("</fieldset>\n");
		sb.append("</form>\n");
		
		return sb.toString();
	}
	
	private String renderModeForm() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("<small>Eingestelltes REST-Schl&uuml;sselwort: </small>");
		if(sParamMode.equals("RawRecordData")) {
			
			sb.append("<b>[RawRecordData]</b> ");
			sb.append("<a href=\"");
			sb.append(encodeParameterIntoURL(sSelfURL, sParamOID, sParamShowRestData, sParamShowCodecData, "InternalMetadataEntry"));
			sb.append("\">[InternalMetadataEntry]</a> \n");
			
		} else if (sParamMode.equals("InternalMetadataEntry")) {

			sb.append("<a href=\"");
			sb.append(encodeParameterIntoURL(sSelfURL, sParamOID, sParamShowRestData, sParamShowCodecData, "RawRecordData"));
			sb.append("\">[RawRecordData]</a> ");
			sb.append("<b>[InternalMetadataEntry]</b> ");
			
		} else {
						
			sb.append("<i>(aktueller Modus unbekannt)</i> ");
			sb.append("[RawRecordData] ");
			sb.append("<a href=\"");
			sb.append(encodeParameterIntoURL(sSelfURL, sParamOID, sParamShowRestData, sParamShowCodecData, "RawRecordData"));
			sb.append("\">[RawRecordData]</a> ");
			sb.append("<a href=\"");
			sb.append(encodeParameterIntoURL(sSelfURL, sParamOID, sParamShowRestData, sParamShowCodecData, "InternalMetadataEntry"));
			sb.append("\">[InternalMetadataEntry]</a> \n");
			
		}	
		
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
