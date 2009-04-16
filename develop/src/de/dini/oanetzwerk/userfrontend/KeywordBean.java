package de.dini.oanetzwerk.userfrontend;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import de.dini.oanetzwerk.utils.imf.DuplicateProbability;
import de.dini.oanetzwerk.utils.imf.Keyword;

public class KeywordBean {

	HitBean parentHitBean = null;
	Keyword keyword = null;
	
	KeywordBean() {
		
	}

	
	public HitBean getParentHitBean() {
		return parentHitBean;
	}

	public void setParentHitBean(HitBean parentHitBean) {
		this.parentHitBean = parentHitBean;
	}

	public Keyword getKeyword() {
		return keyword;
	}

	public void setKeyword(Keyword keyword) {
		this.keyword = keyword;
	}

    /////////////////////// action method //////////////////////////////

	public String actionSearchForKeywordLink() {
		this.parentHitBean
		    .getParentHitlistBean()
		    .getParentSearchBean()
		    .setStrOneSlot(keyword.getKeyword());
		this.parentHitBean
	        .getParentHitlistBean()
	        .getParentSearchBean()
	        .searchFor(keyword.getKeyword());
		return "search4keyword_clicked";
	}
	
}
