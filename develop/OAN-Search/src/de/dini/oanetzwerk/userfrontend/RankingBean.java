package de.dini.oanetzwerk.userfrontend;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import java.util.ArrayList;

import java.util.InvalidPropertiesFormatException;


import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.servicemodule.RestClient;

import de.dini.oanetzwerk.utils.HelperMethods;

/**
 * 
 * @author schoenfa
 * 
 * RankingBean ermittelt die am meisten gelesenen Einträge anhand der Daten von OA-Statistik.
 *
 */
public class RankingBean {

	private static Logger logger = Logger.getLogger (RankingBean.class);
	private Properties props = null;
	
	private SearchBean parentSearchBean = null;
	
	private List<BigDecimal> listHitOID;
	private String strUsageStatisticsRID = null;
	private String strUsageStatisticsName = null;
	private String strUsageStatisticsURL = null;
	
	/**
	 * Befindet sich noch im Entwurfsstadium
	 * 
	 * @throws InvalidPropertiesFormatException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public RankingBean() throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
		
		this.props = HelperMethods.loadPropertiesFromFileWithinWebcontainer (Utils.getWebappPath() + "/WEB-INF/userfrontend_gui.xml");
		
		// new list of oids (hits)
		this.listHitOID = new ArrayList<BigDecimal>();
		
		this.setupRankingList();
		
	}

	/**
	 * Pseudodaten zu Testzwecken
	 */
	public void fakefillListHitOID() {
		listHitOID = new ArrayList<BigDecimal>();
				
		listHitOID.add(new BigDecimal("2569"));
		listHitOID.add(new BigDecimal("2143"));
		listHitOID.add(new BigDecimal("1402"));
		listHitOID.add(new BigDecimal("879"));
		listHitOID.add(new BigDecimal("16"));

	}
	
	private RestClient prepareRestTransmission(String resource) {
		return RestClient.createRestClient(new File(System.getProperty("catalina.base") + this.props.getProperty("restclientpropfile")),
		                resource, this.props.getProperty("username"), this.props.getProperty("password"));
	}

	private void setupRankingList() {

		RestMessage rms = prepareRestTransmission("UsageStatistics/").sendGetRestMessage();
		if ((rms == null) || (rms.getListEntrySets().isEmpty()) || (rms.getStatus() != RestStatusEnum.OK)) {
			logger.warn("received no UsageData_Ranking data ");
			return;
		}
		listHitOID = new ArrayList<BigDecimal>();
		
		
		for (RestEntrySet res : rms.getListEntrySets()) {
			Iterator<String> it = res.getKeyIterator();
			String key = "";
			while (it.hasNext()) {
				key = (String) it.next();

				if ("object_id".equals(key)){
					listHitOID.add(new BigDecimal(res.getValue(key)));
					logger.info("Key"+ key + ": " + res.getValue(key) + "");
				}
				
			}
		}
	}	
	
	
	public SearchBean getParentSearchBean() {
		return parentSearchBean;
	}

	public void setParentSearchBean(SearchBean parentSearchBean) {
		this.parentSearchBean = parentSearchBean;
	}
	public List<BigDecimal> getListHitOID() {
		return listHitOID;
	}
	public void setListHitOID(List<BigDecimal> listHitOID) {
		this.listHitOID = listHitOID;
	}
	
}
