package de.dini.oanetzwerk.userfrontend;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import de.dini.oanetzwerk.utils.ISO639LangNormalizer;
import de.dini.oanetzwerk.utils.imf.Author;
import de.dini.oanetzwerk.utils.imf.Classification;
import de.dini.oanetzwerk.utils.imf.CompleteMetadata;
import de.dini.oanetzwerk.utils.imf.Contributor;
import de.dini.oanetzwerk.utils.imf.DDCClassification;
import de.dini.oanetzwerk.utils.imf.DINISetClassification;
import de.dini.oanetzwerk.utils.imf.DNBClassification;
import de.dini.oanetzwerk.utils.imf.DateValue;
import de.dini.oanetzwerk.utils.imf.Description;
import de.dini.oanetzwerk.utils.imf.DuplicateProbability;
import de.dini.oanetzwerk.utils.imf.FullTextLink;
import de.dini.oanetzwerk.utils.imf.Identifier;
import de.dini.oanetzwerk.utils.imf.InterpolatedDDCClassification;
import de.dini.oanetzwerk.utils.imf.Keyword;
import de.dini.oanetzwerk.utils.imf.Language;
import de.dini.oanetzwerk.utils.imf.OtherClassification;
import de.dini.oanetzwerk.utils.imf.RepositoryData;
import de.dini.oanetzwerk.utils.imf.Title;

public class HitBean implements Serializable {
	
	private static Logger logger = Logger.getLogger (HitBean.class);

	private HitlistBean parentHitlistBean = null;
	private BigDecimal bdOID = null;
	private final String googleStartURIRart 	= "http://www.google.de/search?q=%22",
						 wikipediaStartURIPart 	= "http://de.wikipedia.org/wiki/Spezial:Search?search=%22",
						 endURIPart 			= "%22";

	private CompleteMetadata completeMetadata;
	
	// For sorting by hit statistics
	private BigDecimal hitcounter = null;
	
	public HitBean() {
		
	}
	
    public HitBean(BigDecimal bdOID) {
		this.bdOID = bdOID;
	}
	
	///////////////////////////// AUTO GENERATED ///////////////////////////////
		
	public CompleteMetadata getCompleteMetadata() {
		if(completeMetadata == null) {
		  //logger.debug("try to fetch Metadata here");
		  completeMetadata = parentHitlistBean.getParentSearchBean()
		                                      .getMdLoaderBean()
		                                      .getMapCompleteMetadata()
		                                      .get(bdOID);
		}
		return completeMetadata;
	}

	public void setCompleteMetadata(CompleteMetadata completeMetadata) {
		this.completeMetadata = completeMetadata;
	}
	
	public HitlistBean getParentHitlistBean() {
		return parentHitlistBean;
	}

	public void setParentHitlistBean(HitlistBean parentHitlistBean) {
		this.parentHitlistBean = parentHitlistBean;
	}

	
	//////////////////////////////////////////////////////////////////////////////
	
	public String getTrimmedTitle() {
		//logger.debug("getTrimmedTitle() for oid " + completeMetadata.getOid());
		String s = "";
		if(getCompleteMetadata().getTitleList().size() > 0) {
		  Title title = getCompleteMetadata().getTitleList().get(0);
		  s = title.getTitle();
		  if(s.length() > FrontendConstants.INT_TITLE_TRIMSIZE) s = s.substring(0, FrontendConstants.INT_TITLE_TRIMSIZE-4) + "...";
		} else {
		  s = getBestLink();
		}
		return s;
	}

	public String getTrimmedClipboardTitle() {
		//logger.debug("getTrimmedClipboardTitle() for oid " + getCompleteMetadata().getOid());
		String s = "";
		if(getCompleteMetadata().getTitleList().size() > 0) {
		  Title title = getCompleteMetadata().getTitleList().get(0);
		  s = title.getTitle();
		  if(s.length() > FrontendConstants.INT_CLIPBOARD_TITLE_TRIMSIZE) s = s.substring(0, FrontendConstants.INT_CLIPBOARD_TITLE_TRIMSIZE-4) + "...";
		} else {
		  s = getBestLink();
		}
		return s;
	}
	
	public String getTrimmedCreators() {
		StringBuffer sb = new StringBuffer();
		List<Author> listAuthors = getCompleteMetadata().getAuthorList();
		if(listAuthors != null && listAuthors.size() > 0) {
			for(int i = 0; i < listAuthors.size(); i++) {
				Author author = listAuthors.get(i);
				sb.append(author.getFirstname()).append(" ").append(author.getLastname());
				if(i < listAuthors.size()-1) sb.append(", ");
			}
		}
		List<Contributor> listCon = getCompleteMetadata().getContributorList();
		if(listCon != null && listCon.size() > 0) {
			if(listAuthors != null && listAuthors.size() > 0) sb.append(", ");
			for(int i = 0; i < listCon.size(); i++) {
				Contributor contributor = listCon.get(i);
				sb.append(contributor.getFirstname()).append(" ").append(contributor.getLastname());
				if(i < listCon.size()-1) sb.append(", ");
			}		
		}
		String s = sb.toString();
		if(s.length() > FrontendConstants.INT_CREATORS_TRIMSIZE) s = s.substring(0, FrontendConstants.INT_CREATORS_TRIMSIZE-4) + "...";			
		return s;
	}
	
	public String getTrimmedKeywords() {
		StringBuffer sb = new StringBuffer();
		List<Keyword> listKeywords = getCompleteMetadata().getKeywordList();
		if(listKeywords != null && listKeywords.size() > 0) {
			for(int i = 0; i < listKeywords.size(); i++) {
				Keyword kw = listKeywords.get(i);
				sb.append(kw.getKeyword());
				String lang = kw.getLanguage();
				if(lang != null && lang.length() > 0) sb.append("(").append(lang).append(")");
				if(i < listKeywords.size()-1) sb.append(", ");
			}
		}
		String s = sb.toString();
		if(s.length() == 0) {
			List<Description> listDesc = getCompleteMetadata().getDescriptionList();
			if(listDesc != null && listDesc.size() > 0) {
				Description desc = listDesc.get(0);
				sb.append(desc.getDescription());
			}
			s = sb.toString();
		}		
		if(s.length() > FrontendConstants.INT_KEYWORDS_TRIMSIZE) s = s.substring(0, FrontendConstants.INT_KEYWORDS_TRIMSIZE-4) + "...";			
		return s;
	}
	
	public String getMergedKeywords() {
		StringBuffer sb = new StringBuffer();
		List<Keyword> listKeywords = getCompleteMetadata().getKeywordList();
		if(listKeywords != null && listKeywords.size() > 0) {
			for(int i = 0; i < listKeywords.size(); i++) {
				Keyword kw = listKeywords.get(i);
				sb.append(kw.getKeyword());
				String lang = kw.getLanguage();
				if(lang != null && lang.length() > 0) sb.append("(").append(lang).append(")");
				if(i < listKeywords.size()-1) sb.append(", ");
			}
		}
		String s = sb.toString();
		return s;
	}
	
	public int getFullTextLinkListSize() {
		return getCompleteMetadata().getFullTextLinkList().size();
	}
	
	public String getBestLink() {
		List<FullTextLink> listFTL = getCompleteMetadata().getFullTextLinkList();
		String strFTL = "";
		if(listFTL != null && listFTL.size() > 0) {
			if(!listFTL.get(0).getUrl().equalsIgnoreCase("na")) strFTL = listFTL.get(0).getUrl();
		}
		if(strFTL.length() == 0) {
			List<Identifier> listIdent = getCompleteMetadata().getIdentifierList();		
			if(listIdent != null && listIdent.size() > 0) {
				for(int i = 0; i < listIdent.size(); i++) {
					String s = listIdent.get(i).getIdentifier();					 
					if(s.startsWith("http://")) strFTL = s; 
					if(s.endsWith(".pdf")) break;
				}
			}	
		}
        if(strFTL.indexOf("\"") != -1) strFTL = strFTL.substring(0, strFTL.indexOf("\""));
	    strFTL = StringEscapeUtils.escapeHtml(strFTL);
		return strFTL;
	}
	
	public String getTrimmedDate() {
		String s = "*";
		//logger.debug("getTrimmedDate() for oid " + getCompleteMetadata().getOid());
		if(getCompleteMetadata().getDateValueList().isEmpty()) return "*";
		DateValue dv = getCompleteMetadata().getDateValueList().get(0);
		//String s = (dv.getDateValue().split("-"))[0];
		if(dv.getDateValue() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");			
			s = sdf.format(dv.getDateValue());
		} else if(dv.getStringValue() != null && dv.getStringValue().length() > 0) {
			s = (dv.getStringValue().split("-"))[0]; 
		}		
		return s;
	}
	
	public String getTrimmedFullOAIURL() {
		StringBuffer sb = new StringBuffer();
		sb.append(getCompleteMetadata().getRepositoryData().getRepositoryOAI_BASEURL());
		sb.append("?verb=GetRecord&metadataPrefix=oai_dc&identifier=");
		sb.append(getCompleteMetadata().getRepositoryData().getRepositoryOAI_EXTID());
		return sb.toString();
	}
	
	public List<String> getTrimmedLanguageList() {
		List<String> trimmedLangs = new ArrayList<String>();
		for(Language lang : getCompleteMetadata().getLanguageList()) {
			Locale locale = null;
			String specialValue = "";
			if(lang != null && lang.getIso639language() != null) {
				if(lang.getIso639language().equalsIgnoreCase("mis")) {
					specialValue = "(mis = Angabe fehlt leider)";
				} else if(lang.getIso639language().equalsIgnoreCase("mul")) {
					specialValue = "(mul = gemischt)";
				} else if(lang.getIso639language().equalsIgnoreCase("und")) {
					specialValue = "(und = unbestimmt)";
				} else {
				  locale = ISO639LangNormalizer.getLocaleFromISO639_3(lang.getIso639language());				  
				}
			} 
//			else {
//			  locale = ISO639LangNormalizer.get_ISO639_3(lang.getLanguage());
//			}
			if(locale != null) {
				String iso = ISO639LangNormalizer.wrapDoubleISO(lang.getIso639language());
				if(iso != null) {
					trimmedLangs.add(locale.getDisplayLanguage(Locale.GERMAN) + " ("+ iso +")");
				} else {
					trimmedLangs.add(locale.getDisplayLanguage(Locale.GERMAN));
				}
			} else {
			    trimmedLangs.add(lang.getLanguage() + " " + specialValue);
			}
		}
		return trimmedLangs;
	}
	
	public List<String> getTrimmedClassificationList() {
		List<String> trimmedClassis = new ArrayList<String>();
		for(Classification classi : getCompleteMetadata().getClassificationList()) {
			if(classi instanceof DNBClassification) {
				trimmedClassis.add("DNB: " + ((DNBClassification)classi).getValue());
			} else if(classi instanceof DINISetClassification) {
				trimmedClassis.add("DINI: " + ((DINISetClassification)classi).getValue());
			} else if(classi instanceof DDCClassification) {
				String ddcName_de = DDCDataSingleton.getInstance().getMapDDCNames_de_fromDB().get(((DDCClassification)classi).getValue());
				if(ddcName_de != null && ddcName_de.length() > 0) {
				  trimmedClassis.add("DDC: " + ddcName_de + " (" + ((DDCClassification)classi).getValue()+ ")");
				} else {
				  trimmedClassis.add("DDC: " + ((DDCClassification)classi).getValue());
				}
			} else if(classi instanceof InterpolatedDDCClassification) {
				String ddcName_de = DDCDataSingleton.getInstance().getMapDDCNames_de_fromDB().get(((InterpolatedDDCClassification)classi).getValue());
				if(ddcName_de != null && ddcName_de.length() > 0) {
				  trimmedClassis.add("DDC*: " + ddcName_de + " (" + ((InterpolatedDDCClassification)classi).getValue()+ ")" + " (" + ((InterpolatedDDCClassification)classi).getProbability() + "%)");
				} else {
				  trimmedClassis.add("DDC*: " + ((InterpolatedDDCClassification)classi).getValue() + " (" + ((InterpolatedDDCClassification)classi).getProbability() + "%)");
				}
			} else if(classi instanceof OtherClassification) {
				trimmedClassis.add("Sonstige: " + ((OtherClassification)classi).getValue());
			}
		}
		return trimmedClassis;
	}
	
	public List<Object[]> getTrimmedKeywordList() {
		List<Object[]> trimmedKeywords = new ArrayList<Object[]>();
		for(Keyword keyword : getCompleteMetadata().getKeywordList()) {
			String keyword_val = keyword.getKeyword();
			Object[] s = new Object[4];
			s[0] = keyword_val;
			if(keyword.getLanguage() != null && keyword.getLanguage().length() > 0) s[0] = keyword_val + " (" + keyword.getLanguage() + ")";
			s[1] = googleStartURIRart + keyword_val + endURIPart;
			s[2] = wikipediaStartURIPart + keyword_val + endURIPart;
			KeywordBean kb = new KeywordBean();
			kb.setParentHitBean(this);
			kb.setKeyword(keyword);
			s[3] = kb;
			trimmedKeywords.add(s);
		}
		return trimmedKeywords;
	}
	

	public List<Object[]> getFilteredKeywordList() {
		List<Object[]> filteredKeywordList = new ArrayList<Object[]>();
		List<Object[]> trimmedKeywordList = getTrimmedKeywordList();
		final String komma 		= ",",
					 semikolon 	= ";",
					 punkt 		= ".";
		for(Object[] keywordObject : trimmedKeywordList) {
			
			//keyword = keywordObject[0].toString();
			List<String> keywords = new ArrayList<String>();
			if (keywordObject[0].toString().indexOf(komma) == -1 && 
					keywordObject[0].toString().indexOf(semikolon) == -1 && 
						keywordObject[0].toString().indexOf(punkt) == -1){
				keywords.add(keywordObject[0].toString());
			} else {
				if (keywordObject[0].toString().indexOf(komma) != -1){
					String[] tempString = keywordObject[0].toString().split(komma);
					for (String tempSubString : tempString){
						keywords.add(tempSubString);
					}
				}
				if (keywordObject[0].toString().indexOf(semikolon) != -1){
					String[] tempString = keywordObject[0].toString().split(semikolon);
					for (String tempSubString : tempString){
						keywords.add(tempSubString);
					}
				} 
				if (keywordObject[0].toString().indexOf(punkt) != -1){
					String[] tempString = keywordObject[0].toString().split(punkt);
					for (String tempSubString : tempString){
						keywords.add(tempSubString);
					}
				}
			}
			if (keywords.size() > 1){
				KeywordBean kb = (KeywordBean) keywordObject[3];
				for (String keyword : keywords){
					if (!keyword.matches("\\d+")){
						Object[] s = new Object[4];
						s[0] = keyword;
						s[1] = googleStartURIRart + keyword + endURIPart;
						s[2] = wikipediaStartURIPart + keyword + endURIPart;
						KeywordBean newkb = new KeywordBean();
						newkb.setParentHitBean(kb.getParentHitBean());
						newkb.setKeyword(new Keyword(keyword, kb.getKeyword().getLanguage()));
						s[3] = newkb;
						filteredKeywordList.add(s);						
					}

				}
			} else if (keywords.size() == 1){
				if (!keywords.get(0).matches("\\d+")){					
					filteredKeywordList.add(keywordObject);
				}
			}			
		}
		return filteredKeywordList;
	}
	
	public String getMetadatastring() {
		String s = this.getCompleteMetadata().toString();
		s = s.replaceAll("\n", "<br/>");
		return s;
	}
	
	public String getFlagIMG() {
		String img = "unknown.png";
		
		for(Language lang : this.getCompleteMetadata().getLanguageList()) {
			if(lang != null && lang.getIso639language() != null) {
				if(lang.getIso639language().equalsIgnoreCase("DEU")) return "de.png";
				if(lang.getIso639language().equalsIgnoreCase("FRA")) return "fr.png";
				if(lang.getIso639language().equalsIgnoreCase("SPA")) return "es.png";
				if(lang.getIso639language().equalsIgnoreCase("ENG")) return "gb.png";
				if(lang.getIso639language().equalsIgnoreCase("ITA")) return "it.png";
				if(lang.getIso639language().equalsIgnoreCase("MUL")) return "europeanunion.png";
			}
		}
		 
		return img;
	}
	
	public String getFlagALT() {
		String result = "Sprache: unbekannt";
		
		for(Language lang : this.getCompleteMetadata().getLanguageList()) {
			if(lang != null && lang.getIso639language() != null) {
				if(lang.getIso639language().equalsIgnoreCase("mis")) {
				  result = "Sprache: Angabe fehlt leider";
				} else if(lang.getIso639language().equalsIgnoreCase("mul")) {
			      result = "Sprache: gemischt";
				} else if(lang.getIso639language().equalsIgnoreCase("und")) {
				  result = "Sprache: unbestimmt";
				} else {
				  Locale locale = ISO639LangNormalizer.getLocaleFromISO639_3(lang.getIso639language());
				  result = "Sprache: " + (locale.getDisplayLanguage(Locale.GERMAN));
				}
			}
		}
		 
		return result;
	}
	
	public String getUrlIRIcon() {
		String url = "";
		
		RepositoryData rd = this.completeMetadata.getRepositoryData();
		if(rd != null) {
			String s = rd.getRepositoryURL();
			if(s != null && s.length() > 0) {
				url = s + "/favicon.ico";
			}
		}
		 
		return url;
	}
	
	public List<DuplicateProbability> getTrimmedDuplicateProbabilityList() {
		List<DuplicateProbability> trimmedDupPros = new ArrayList<DuplicateProbability>();
		for(DuplicateProbability dupPro : getCompleteMetadata().getDuplicateProbabilityList()) {
			if(dupPro.getProbability() >= FrontendConstants.DOUBLE_DUPPRO_THREASHOLD) trimmedDupPros.add(dupPro);
		}
		Collections.sort(trimmedDupPros, new DuplicateProbabilityComparator(false));
		return trimmedDupPros;
	}
	
	/////////////////////// action method //////////////////////////////

	public String actionDetailsLink() {
		this.parentHitlistBean.setSelectedDetailsOID(this.getCompleteMetadata().getOid());
		for(DuplicateProbability dupPro : this.getCompleteMetadata().getDuplicateProbabilityList()) {
			this.parentHitlistBean.addHitbeanToMap(dupPro.getReferToOID());			
		}
		return "details_clicked";
	}
	
	public String actionDetailsV4Link() {
		this.parentHitlistBean.setSelectedDetailsOID(this.getCompleteMetadata().getOid());
		for(DuplicateProbability dupPro : this.getCompleteMetadata().getDuplicateProbabilityList()) {
			this.parentHitlistBean.addHitbeanToMap(dupPro.getReferToOID());			
		}
		return "details_clicked_v4";
	}

	public String actionMerkenLink() {
		this.parentHitlistBean.addSetClipboardOID(this.getCompleteMetadata().getOid());
		return "merken_clicked";
	}
	
	public String actionVerwerfenLink() {
		this.parentHitlistBean.removeSetClipboardOID(this.getCompleteMetadata().getOid());
		return "verwerfen_clicked";
	}

	public void setHitcounter(BigDecimal hitcounter) {
		this.hitcounter = hitcounter;
	}

	public BigDecimal getHitcounter() {
		return hitcounter;
	}	
	
}
