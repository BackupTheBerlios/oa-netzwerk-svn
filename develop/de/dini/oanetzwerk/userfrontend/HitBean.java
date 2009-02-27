package de.dini.oanetzwerk.userfrontend;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.utils.imf.Author;
import de.dini.oanetzwerk.utils.imf.CompleteMetadata;
import de.dini.oanetzwerk.utils.imf.Contributor;
import de.dini.oanetzwerk.utils.imf.DateValue;
import de.dini.oanetzwerk.utils.imf.Description;
import de.dini.oanetzwerk.utils.imf.DuplicateProbability;
import de.dini.oanetzwerk.utils.imf.FullTextLink;
import de.dini.oanetzwerk.utils.imf.Identifier;
import de.dini.oanetzwerk.utils.imf.Keyword;
import de.dini.oanetzwerk.utils.imf.Title;

public class HitBean implements Serializable {
	
	private static Logger logger = Logger.getLogger (HitBean.class);

	private HitlistBean parentHitlistBean = null;
	private BigDecimal bdOID = null;
	private CompleteMetadata completeMetadata;
	
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
		Title title = getCompleteMetadata().getTitleList().get(0);
		String s = title.getTitle();
		if(s.length() > FrontendConstants.INT_TITLE_TRIMSIZE) s = s.substring(0, FrontendConstants.INT_TITLE_TRIMSIZE-4) + "...";
		return s;
	}

	public String getTrimmedClipboardTitle() {
		//logger.debug("getTrimmedClipboardTitle() for oid " + getCompleteMetadata().getOid());
		Title title = getCompleteMetadata().getTitleList().get(0);
		String s = title.getTitle();
		if(s.length() > FrontendConstants.INT_CLIPBOARD_TITLE_TRIMSIZE) s = s.substring(0, FrontendConstants.INT_CLIPBOARD_TITLE_TRIMSIZE-4) + "...";
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
			strFTL = listFTL.get(0).getUrl();
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
	
	public String getMetadatastring() {
		String s = this.getCompleteMetadata().toString();
		s = s.replaceAll("\n", "<br/>");
		return s;
	}
	
	/////////////////////// action method //////////////////////////////

	public String actionDetailsLink() {
		this.parentHitlistBean.setSelectedDetailsOID(this.getCompleteMetadata().getOid());
		for(DuplicateProbability dupPro : this.getCompleteMetadata().getDuplicateProbabilityList()) {
			this.parentHitlistBean.addHitbeanToMap(dupPro.getReferToOID());			
		}
		return "details_clicked";
	}

	public String actionMerkenLink() {
		this.parentHitlistBean.addSetClipboardOID(this.getCompleteMetadata().getOid());
		return "merken_clicked";
	}
	
	public String actionVerwerfenLink() {
		this.parentHitlistBean.removeSetClipboardOID(this.getCompleteMetadata().getOid());
		return "verwerfen_clicked";
	}
	
}
