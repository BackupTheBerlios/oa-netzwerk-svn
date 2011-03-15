package de.dini.oanetzwerk.userfrontend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.search.SearchClient;
import de.dini.oanetzwerk.search.SearchClientException;
import de.dini.oanetzwerk.servicemodule.RestClient;
import de.dini.oanetzwerk.utils.HelperMethods;

public class SearchBean implements Serializable {
	/**
	 * 
	 */
	private static Logger logger = Logger.getLogger(SearchBean.class);
	private Properties props = null;
	private Properties search_props = null;
	private MetadataLoaderBean mdLoaderBean;
	private String strOneSlot = "";
	private final String fStrFreeSearch = "___";
	private BigDecimal directOID = null;
	private String strRepositoryFilterRID = null;
	private String strRepositoryFilterName = null;
	private String strRepositoryFilterURL = null;

	private SearchClient mySearchClient = null;

	private HitlistBean hitlist = null;
	private RankingBean ranking = null;
	private BrowseBean browse = null;

	private boolean bErrorLastSearch = false;
	private String strErrorLastSearch = "";
	

	public SearchBean() throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
		this.hitlist = new HitlistBean();

		init();
	}

	public SearchBean(HitlistBean hitlistBean) throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
		this.hitlist = hitlistBean;

		init();
	}

	private void init() throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
		this.props = HelperMethods.loadPropertiesFromFileWithinWebcontainer(Utils.getWebappPath() + "/WEB-INF/userfrontend_gui.xml");

		// this.hitlist = new HitlistBean();
		// this.hitlist.setParentSearchBean(this);
		this.hitlist.setParentSearchBean(this);

		this.browse = new BrowseBean();
		this.browse.setParentSearchBean(this);
		
		this.ranking = new RankingBean();
		this.ranking.setParentSearchBean(this);

		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

		this.strRepositoryFilterRID = request.getParameter("filterForRID");
		setupRepositoryFilterByRID(this.strRepositoryFilterRID);

		this.search_props = HelperMethods.loadPropertiesFromFileWithinWebcontainer(Utils.getWebappPath() + "/WEB-INF/searchclientprop.xml");
		this.mySearchClient = new SearchClient(this.search_props);

		String query = request.getParameter("query");

		if (query != null && query.length() > 0) {

			this.strOneSlot = query;
			actionSearchButton();
		}

	}

	public MetadataLoaderBean getMdLoaderBean() {
		return this.mdLoaderBean;
	}

	public void setMdLoaderBean(MetadataLoaderBean mdLoaderBean) {
		this.mdLoaderBean = mdLoaderBean;
	}

	public String getStrOneSlot() {
		return this.strOneSlot;
	}

	public void setStrOneSlot(String strOneSlot) {
		this.strOneSlot = strOneSlot;
	}

	public BigDecimal getDirectOID() {
		return this.directOID;
	}

	public void setDirectOID(BigDecimal directOID) {
		this.directOID = directOID;
	}

	public String getStrRepositoryFilterRID() {
		return this.strRepositoryFilterRID;
	}

	public void setStrRepositoryFilterRID(String strRepositoryFilterRID) {
		this.strRepositoryFilterRID = strRepositoryFilterRID;
	}

	public String getStrRepositoryFilterName() {
		return this.strRepositoryFilterName;
	}

	public void setStrRepositoryFilterName(String strRepositoryFilterName) {
		this.strRepositoryFilterName = strRepositoryFilterName;
	}

	public String getStrRepositoryFilterURL() {
		return this.strRepositoryFilterURL;
	}

	public void setStrRepositoryFilterURL(String strRepositoryFilterURL) {
		this.strRepositoryFilterURL = strRepositoryFilterURL;
	}

	public BrowseBean getBrowse() {
		return this.browse;
	}

	public void setBrowse(BrowseBean browse) {
		this.browse = browse;
	}

	public HitlistBean getHitlist() {
		return this.hitlist;
	}

	public void setHitlist(HitlistBean hitlist) {
		this.hitlist = hitlist;
	}

	public boolean isBErrorLastSearch() {
		return this.bErrorLastSearch;
	}

	public void setBErrorLastSearch(boolean errorLastSearch) {
		this.bErrorLastSearch = errorLastSearch;
	}

	public String getStrErrorLastSearch() {
		return this.strErrorLastSearch;
	}

	public void setStrErrorLastSearch(String strErrorLastSearch) {
		this.strErrorLastSearch = strErrorLastSearch;
	}

	public void searchFor(String strQuery, String strDDC) {
		List<BigDecimal> listOIDs = new ArrayList<BigDecimal>();
		try {
			listOIDs = this.mySearchClient.querySearchService(strQuery, strDDC);
			this.bErrorLastSearch = false;
			this.strErrorLastSearch = "";
		} catch (SearchClientException scex) {
			logger.error("SearchClientException: " + scex);
			this.bErrorLastSearch = true;
			this.strErrorLastSearch = ("Fehler: " + scex.getMessage());
		}
		this.hitlist.setListHitOID(listOIDs);
		this.hitlist.updateHitlistMetadata();
	}

	public void searchFor2(String strQuery, String strDDC) {
		List<BigDecimal> listOIDs = new ArrayList<BigDecimal>();
		try {
			listOIDs = this.mySearchClient.querySearchService2(strQuery, strDDC);
			this.bErrorLastSearch = false;
			this.strErrorLastSearch = "";
		} catch (SearchClientException scex) {
			logger.error("SearchClientException: " + scex);
			this.bErrorLastSearch = true;
			this.strErrorLastSearch = ("Fehler: " + scex.getMessage());
		}
		this.hitlist.setListHitOID(listOIDs);
		this.hitlist.updateHitlistMetadata();

	}

	public String getLinkForSearchFeed() {
		String sLink = "";
		try {
			if ((this.strOneSlot != null) && (this.strOneSlot.trim().length() > 0))
				sLink = "http://oanet.cms.hu-berlin.de/rssfeed/feed?search=" + URLEncoder.encode(this.strOneSlot.trim(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
		}
		return sLink;
	}

	private String evalOneSlotAndDDC() {
		logger.debug("evalOneSlotAndDDC");
		String strDDC = this.browse.getSelectedDDCCatValue();
		if ((this.strOneSlot != null) && (this.strOneSlot.length() > 0)) {
			String strQuery = new String(this.strOneSlot);
			strQuery = strQuery.toUpperCase();
			strQuery = strQuery.trim();
			searchFor(strQuery, strDDC);
		} else if (strDDC != null) {
			searchFor(null, strDDC);
		}
		return "search_clicked";
	}
	
	private String getRanking() {
		logger.debug("getRanking");
		browse.setPathDDCCategories(new ArrayList<String>());
		browse.setSelectedDDCCatValue(null);
		browse.setSelectedDDCCatName("Alles");
		List<BigDecimal> listOIDs = new ArrayList<BigDecimal>();
		listOIDs = this.ranking.getListHitOID();
/*		try {
			listOIDs = this.mySearchClient.querySearchService(strQuery, strDDC);
			this.bErrorLastSearch = false;
			this.strErrorLastSearch = "";
		} catch (SearchClientException scex) {
			logger.error("SearchClientException: " + scex);
			this.bErrorLastSearch = true;
			this.strErrorLastSearch = ("Fehler: " + scex.getMessage());
		}*/
		this.hitlist.setListHitOID(listOIDs);
		this.hitlist.updateHitlistMetadata();
		return "search_clicked";
	}

	private String evalOneSlotAndDDCV4() {
		logger.debug("evalOneSlotAndDDC");
		String strDDC = this.browse.getSelectedDDCCatValue();
		if ((this.strOneSlot != null) && (this.strOneSlot.length() > 0)) {
			String strQuery = new String(this.strOneSlot);
			strQuery = strQuery.toUpperCase();
			strQuery = strQuery.trim();
			searchFor(strQuery, strDDC);
		} else if (strDDC != null) {
			searchFor(null, strDDC);
		}
		return "search_clicked_v4";
	}

	private String freeSearchAndDDCV4() {
		logger.debug("freeSearchAndDDC");
		String strDDC = this.browse.getSelectedDDCCatValue();
		searchFor(fStrFreeSearch, strDDC);
		setStrOneSlot("");
		return "search_clicked_v4";
	}

	private String freeSearchAndDDC() {
		logger.debug("freeSearchAndDDC");
		String strDDC = this.browse.getSelectedDDCCatValue();
		searchFor(fStrFreeSearch, strDDC);
		setStrOneSlot("");
		return "search_clicked";
	}
	private String freeSearchAndDDCLevel(ET_DDCLevel ddcLevel) {
		logger.debug("freeSearchAndDDCLevel: " + ddcLevel + "");
		String strDDC = this.browse.getSelectedDDCCatValue();
		char hunderter = strDDC.charAt(0), zehner = strDDC.charAt(1), einer = strDDC.charAt(2);
		switch (ddcLevel) {
			case EINS: zehner = einer = '0'; break;
			// DDC Number 004 is of level type two 		
			case ZWEI: if(!strDDC.equals("004")){einer = '0';}; break;
			case DREI: break;					     
			default:   break;
		}
		strDDC = String.valueOf(hunderter) + String.valueOf(zehner) + String.valueOf(einer);
		searchFor(fStrFreeSearch, strDDC);
		setStrOneSlot("");
		return "search_clicked";
	}	

	private String parseOneSlotSearchField() {
		logger.debug("parseOneSlotSearchField");
		if ((this.strOneSlot != null) && (this.strOneSlot.length() > 0)) {
			String strQuery = new String(this.strOneSlot);
			strQuery = strQuery.toUpperCase();
			strQuery = strQuery.trim();
			if (strQuery.startsWith("OID:")) {
				return parseOneSlotForDirectOID(strQuery);
			}
			return parseOneSlotForQueryString(strQuery);
		}

		return "error";
	}

	private String parseOneSlotSearchField2() {
		logger.debug(this.strOneSlot);
		logger.debug("parseOneSlotSearchField");
		if ((this.strOneSlot != null) && (this.strOneSlot.length() > 0)) {
			String strQuery = new String(this.strOneSlot);
			strQuery = strQuery.toUpperCase();
			strQuery = strQuery.trim();
			if (strQuery.startsWith("OID:")) {
				return parseOneSlotForDirectOID(strQuery);
			}
			return parseOneSlotForQueryString2(strQuery);
		}

		return "error";
	}

	private String parseOneSlotForQueryString(String strQuery) {
		logger.debug("parseOneSlotForQueryString");
		searchFor(strQuery, null);
		return "search_clicked";
	}

	private String parseOneSlotForQueryString2(String strQuery) {
		logger.debug("parseOneSlotForQueryString");

		searchFor2(strQuery, null);
		return "search_clicked";
	}

	private String parseOneSlotForDirectOID(String strTest) {
		logger.debug("parseOneSlotForDirectOID");
		try {
			String[] oids = strTest.substring(strTest.indexOf("OID:") + 4).split(":");
			ArrayList<BigDecimal> listOIDs = new ArrayList<BigDecimal>();
			if ((oids != null) && (oids.length > 0)) {
				for (String strOid : oids) {
					BigDecimal oid = new BigDecimal(strOid);

					listOIDs.add(oid);
				}
				this.hitlist.setListHitOID(listOIDs);
				this.hitlist.updateHitlistMetadata();
				return "search_clicked";
			}
			this.hitlist.fakefillListHitOID();
			this.hitlist.updateHitlistMetadata();
			return "search_clicked";
		} catch (NumberFormatException ex) {
			this.hitlist.fakefillListHitOID();
			this.hitlist.updateHitlistMetadata();
		}
		return "error";
	}

	private RestClient prepareRestTransmission(String resource) {
		return RestClient.createRestClient(new File(System.getProperty("catalina.base") + this.props.getProperty("restclientpropfile")),
		                resource, this.props.getProperty("username"), this.props.getProperty("password"));
	}

	private void setupRepositoryFilterByRID(String strRID) {
		if (this.strRepositoryFilterRID == null) {
			this.strRepositoryFilterRID = "";
			return;
		}

		RestMessage rms = prepareRestTransmission("Repository/" + strRID).sendGetRestMessage();
		if ((rms == null) || (rms.getListEntrySets().isEmpty()) || (rms.getStatus() != RestStatusEnum.OK)) {
			logger.warn("received no repository data entry for rid " + strRID + ", status: " + rms.getStatus() + " : "
			                + rms.getStatusDescription());
			this.strRepositoryFilterRID = "";
			return;
		}

		for (RestEntrySet res : rms.getListEntrySets()) {
			Iterator it = res.getKeyIterator();
			String key = "";
			while (it.hasNext()) {
				key = (String) it.next();
				if (key.equalsIgnoreCase("name"))
					this.strRepositoryFilterName = res.getValue(key);
				if (key.equalsIgnoreCase("url"))
					;
				this.strRepositoryFilterURL = res.getValue(key);
			}
		}
	}

	public String actionSearchButton() {
		return parseOneSlotSearchField();
	}

	public String actionSearch2Button() {
		return parseOneSlotSearchField2();
	}

	public String actionSearchWithDDCButton() {
		return evalOneSlotAndDDC();
	}

	public String actionSearchFromDDCTreeDirectlyWithAutoHits() {
		return freeSearchAndDDC();
	}
	public String actionSearchFromDDCTreeDirectlyWithAutoHitsLevelEINS() {
		return freeSearchAndDDCLevel(ET_DDCLevel.EINS);
	}
	public String actionSearchFromDDCTreeDirectlyWithAutoHitsLevelZWEI() {
		return freeSearchAndDDCLevel(ET_DDCLevel.ZWEI);
	}
	public String actionSearchFromDDCTreeDirectlyWithAutoHitsLevelDREI() {
		return freeSearchAndDDCLevel(ET_DDCLevel.DREI);
	}

	public String actionSearchWithDDCButtonV4() {
		return evalOneSlotAndDDCV4();
	}


	public String actionSearchFromDDCTreeDirectlyWithAutoHitsV4() {
		return freeSearchAndDDCV4();
	}
	
	public String actionGetRanking() {
		return getRanking();
	}

	public String actionAddAllHitsToClipboard() {
		for (BigDecimal bdOID : this.hitlist.getListHitOID()) {
			this.hitlist.addSetClipboardOID(bdOID);
		}
		return "";
	}
}