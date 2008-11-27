package de.dini.oanetzwerk.userfrontend;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.utils.imf.Author;
import de.dini.oanetzwerk.utils.imf.CompleteMetadata;
import de.dini.oanetzwerk.utils.imf.Contributor;
import de.dini.oanetzwerk.utils.imf.Description;
import de.dini.oanetzwerk.utils.imf.FullTextLink;
import de.dini.oanetzwerk.utils.imf.Identifier;
import de.dini.oanetzwerk.utils.imf.Keyword;
import de.dini.oanetzwerk.utils.imf.Title;

public class HitBean implements Serializable {
	
	private static Logger logger = Logger.getLogger (HitBean.class);

	private HitlistBean parentHitlistBean = null;
	
	private CompleteMetadata completeMetadata;
	
	public HitBean() {
		
	}
	
	///////////////////////////// AUTO GENERATED ///////////////////////////////
		
	public CompleteMetadata getCompleteMetadata() {
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
		logger.debug("getTrimmedTitle() for oid " + completeMetadata.getOid());
		Title title = completeMetadata.getTitleList().get(0);
		String s = title.getTitle();
		if(s.length() > FrontentConstants.INT_TITLE_TRIMSIZE) s = s.substring(0, FrontentConstants.INT_TITLE_TRIMSIZE-4) + "...";
		return s;
	}
	
	public String getTrimmedCreators() {
		StringBuffer sb = new StringBuffer();
		List<Author> listAuthors = completeMetadata.getAuthorList();
		if(listAuthors != null && listAuthors.size() > 0) {
			for(int i = 0; i < listAuthors.size(); i++) {
				Author author = listAuthors.get(i);
				sb.append(author.getFirstname()).append(" ").append(author.getLastname());
				if(i < listAuthors.size()-1) sb.append(", ");
			}
		}
		List<Contributor> listCon = completeMetadata.getContributorList();
		if(listCon != null && listCon.size() > 0) {
			if(listAuthors != null && listAuthors.size() > 0) sb.append(", ");
			for(int i = 0; i < listCon.size(); i++) {
				Contributor contributor = listCon.get(i);
				sb.append(contributor.getFirstname()).append(" ").append(contributor.getLastname());
				if(i < listCon.size()-1) sb.append(", ");
			}		
		}
		String s = sb.toString();
		if(s.length() > FrontentConstants.INT_CREATORS_TRIMSIZE) s = s.substring(0, FrontentConstants.INT_CREATORS_TRIMSIZE-4) + "...";			
		return s;
	}
	
	public String getTrimmedKeywords() {
		StringBuffer sb = new StringBuffer();
		List<Keyword> listKeywords = completeMetadata.getKeywordList();
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
			List<Description> listDesc = completeMetadata.getDescriptionList();
			if(listDesc != null && listDesc.size() > 0) {
				Description desc = listDesc.get(0);
				sb.append(desc.getDescription());
			}
			s = sb.toString();
		}		
		if(s.length() > FrontentConstants.INT_KEYWORDS_TRIMSIZE) s = s.substring(0, FrontentConstants.INT_KEYWORDS_TRIMSIZE-4) + "...";			
		return s;
	}
	
	public String getBestLink() {
		List<FullTextLink> listFTL = completeMetadata.getFullTextLinkList();
		String strFTL = "";
		if(listFTL != null && listFTL.size() > 0) {
			strFTL = listFTL.get(0).getUrl();
		}
		if(strFTL.length() == 0) {
			List<Identifier> listIdent = completeMetadata.getIdentifierList();		
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
	
	/////////////////////// action method //////////////////////////////

	public String actionDetailsLink() {
		this.parentHitlistBean.setSelectedDetailsOID(this.completeMetadata.getOid());
		return "details_clicked";
	}
	
	
}
