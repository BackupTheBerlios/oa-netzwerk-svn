package de.dini.oanetzwerk.userfrontend;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import java.util.ArrayList;

import java.util.InvalidPropertiesFormatException;

import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
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
		
		this.fakefillListHitOID();
		
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
