package de.dini.oanetzwerk.userfrontend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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

public class BrowseBean {

	private static Logger logger = Logger.getLogger (BrowseBean.class);
	private Properties props = null;

	private HashMap mapDDCNames_de = null;
	
	private SearchBean parentSearchBean = null;
	
	public List<String[]> simpleDDCCategorySums = null;
	public List<String[]> directDDCCategorySums = null;
	
	DDCNameResolver myDDCNameResolver = null;
	
	public BrowseBean() throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
		
		this.props = HelperMethods.loadPropertiesFromFile ("webapps/findnbrowse/WEB-INF/userfrontend_gui.xml");
		this.simpleDDCCategorySums = generateSimpleDDCCategorySums();
		this.directDDCCategorySums = generateDirectDDCCategorySums(simpleDDCCategorySums);
		
		myDDCNameResolver = new DDCNameResolver();
		setupMapDDCNames();
	}

	///// auto generated /////////////////////////////////////////////////////////////////////////

	public List<String[]> getSimpleDDCCategorySums() {
		return simpleDDCCategorySums;
	}
	
	public void setSimpleDDCCategorySums(List<String[]> simpleDDCCategorySums) {
		this.simpleDDCCategorySums = simpleDDCCategorySums;
	}
	
	public List<String[]> getDirectDDCCategorySums() {
		return directDDCCategorySums;
	}

	public void setDirectDDCCategorySums(List<String[]> directDDCCategorySums) {
		this.directDDCCategorySums = directDDCCategorySums;
	}

	public HashMap getMapDDCNames_de() {
		return mapDDCNames_de;
	}

	public void setMapDDCNames_de(HashMap mapDDCNames_de) {
		this.mapDDCNames_de = mapDDCNames_de;
	}

	public SearchBean getParentSearchBean() {
		return parentSearchBean;
	}

	public void setParentSearchBean(SearchBean parentSearchBean) {
		this.parentSearchBean = parentSearchBean;
	}
	
	///////////////////////////////////////////////////////////////////////////////
	
	private void setupMapDDCNames() {
		this.mapDDCNames_de = new HashMap<String,String>();
		for(String key : DDCNameResolver.getListDDCNumbers()) {
			mapDDCNames_de.put(key, DDCNameResolver.getCategoryName(key, "de"));
		}
	}
	
	private RestClient prepareRestTransmission (String resource) {
		
		return RestClient.createRestClient (new File (System.getProperty ("catalina.base") + this.props.getProperty ("restclientpropfile")), resource, this.props.getProperty ("username"), this.props.getProperty ("password"));
	}
	
	private class CategoryNumberComparator implements Comparator<String[]> {

		public int compare(String[] o1, String[] o2) {
			int cat1, cat2;
			try {
				cat1 = Integer.parseInt(o1[0].substring(0, 2));
				cat2 = Integer.parseInt(o2[0].substring(0, 2));
			} catch(Exception ex) {
				return 0;
			}			
			return cat1 - cat2;
		}
		
	}
	
	private List<String[]> generateSimpleDDCCategorySums() {
			
		ArrayList<String[]> listDDCCategoriesAndSum = new ArrayList<String[]>();
		RestMessage rms = this.prepareRestTransmission ("DDCCategories/").sendGetRestMessage();
		
		if (rms == null || rms.getListEntrySets ( ).isEmpty ( )) {
			
			logger.error ("received no DDCCategories");
			return null;
		}
		
		if (rms.getStatus() != RestStatusEnum.OK) {
			
			logger.error ("received status " + rms.getStatus() + " (" + rms.getStatusDescription() + ") for DDCCategories");
			return null;
		}
		
		for (RestEntrySet res : rms.getListEntrySets ( )) {
			
			Iterator <String> it = res.getKeyIterator ( );
			String key = "";
			
			while (it.hasNext ( )) {
				key = it.next ( );
				listDDCCategoriesAndSum.add(new String[] {key, res.getValue (key)} );				
			}
			
		}
		
		Collections.sort(listDDCCategoriesAndSum, new CategoryNumberComparator());
		
		return listDDCCategoriesAndSum;
	}

	private List<String[]> generateDirectDDCCategorySums(List<String[]> listSimpleSums) {
		
		ArrayList<String[]> listDDCCategoriesAndSum = new ArrayList<String[]>();
		
		for(String entry[] : listSimpleSums) {
			
			String wildcardCategory = entry[0];
			wildcardCategory = wildcardCategory.replaceAll("00$", "x");
			wildcardCategory = wildcardCategory.replaceAll("0$", "x");
			
			RestMessage rms = this.prepareRestTransmission ("DDCCategories/" + wildcardCategory).sendGetRestMessage();

			if (rms == null || rms.getListEntrySets ( ).isEmpty ( )) {
				logger.error ("received no DDCCategories");
				return null;
			}

			if (rms.getStatus() != RestStatusEnum.OK) {
				logger.error ("received status " + rms.getStatus() + " (" + rms.getStatusDescription() + ") for DDCCategories");
				return null;
			}

			for (RestEntrySet res : rms.getListEntrySets ( )) {

				Iterator <String> it = res.getKeyIterator ( );
				String key = "";

				while (it.hasNext ( )) {
					key = it.next ( );
					if(key.equals("sum")) listDDCCategoriesAndSum.add(new String[] {entry[0], res.getValue (key), wildcardCategory} );				
				}

			}
		}
				
		return listDDCCategoriesAndSum;
	}

}
