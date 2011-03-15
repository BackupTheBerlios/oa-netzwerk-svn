package de.dini.oanetzwerk.userfrontend;

import java.io.File;
import java.io.FileInputStream;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.servicemodule.RestClient;
import de.dini.oanetzwerk.utils.HelperMethods;

public class BrowseBean {

	private static Logger logger = Logger.getLogger (BrowseBean.class);
	private Properties props = null;
	
	private SearchBean parentSearchBean = null;

	DDCNameResolver myDDCNameResolver = null;
	private HashMap mapDDCNames_de = null;

	private List<String[]> simpleDDCCategorySums = null;
	private List<String[]> directDDCCategorySums = null;
	private HashMap<String,String> mapDDCSums = null;
	private List<DDCNaviNode> listDDCNaviNodes = null;
	
    private List<String> pathDDCCategories = null; 
	private String selectedDDCCatValue = null;
	private String selectedDDCCatName = "Alles";
	
	private Boolean showDDCNums;
    


	public BrowseBean() throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
		
		this.props = HelperMethods.loadPropertiesFromFileWithinWebcontainer (Utils.getWebappPath() + "/WEB-INF/userfrontend_gui.xml");
		//this.props = HelperMethods.loadPropertiesFromFile ("userfrontend_gui.xml");
		
		//mapDDCSums = new HashMap<String, String>();
		myDDCNameResolver = new DDCNameResolver();
		setupMapDDCNames();
				
		this.simpleDDCCategorySums = DDCDataSingleton.getInstance().getSimpleDDCCategorySums();
		this.directDDCCategorySums = DDCDataSingleton.getInstance().getDirectDDCCategorySums();
			//	new ArrayList<String[]>(); 
		//generateDirectDDCCategorySums(simpleDDCCategorySums);	
		this.listDDCNaviNodes = DDCDataSingleton.getInstance().getListDDCNaviNodes(this); 
			//new ArrayList<DDCNaviNode>(); 
		//generateListDDCNaviNodes(simpleDDCCategorySums);
		
		this.pathDDCCategories = new ArrayList<String>();
		this.showDDCNums = false;
	}

	///// auto generated /////////////////////////////////////////////////////////////////////////

	public List<String[]> getSimpleDDCCategorySums() {
		//if(simpleDDCCategorySums == null) this.simpleDDCCategorySums = generateSimpleDDCCategorySums();
		return simpleDDCCategorySums;
	}
	
	public void setSimpleDDCCategorySums(List<String[]> simpleDDCCategorySums) {		
		this.simpleDDCCategorySums = simpleDDCCategorySums;
	}
	
	public List<String[]> getDirectDDCCategorySums() {
		//if(directDDCCategorySums == null) this.directDDCCategorySums = generateDirectDDCCategorySums(simpleDDCCategorySums);
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

	public List<DDCNaviNode> getListDDCNaviNodes() {
		//if(listDDCNaviNodes == null) this.listDDCNaviNodes = generateListDDCNaviNodes(simpleDDCCategorySums);
		return listDDCNaviNodes;
	}
	public Boolean getBooleanListDDCNaviNodesIsEmpty(){
		//logger.info("listDDCNaviNodes = " + listDDCNaviNodes.size() + "");
		return listDDCNaviNodes.size() == 0;
	}

	public void setListDDCNaviNodes(List<DDCNaviNode> listDDCNaviNodes) {
		this.listDDCNaviNodes = listDDCNaviNodes;
	}

	public List<String> getPathDDCCategories() {
		return pathDDCCategories;
	}

	public void setPathDDCCategories(List<String> pathDDCCategories) {
		this.pathDDCCategories = pathDDCCategories;
	}

	public SearchBean getParentSearchBean() {
		return parentSearchBean;
	}

	public void setParentSearchBean(SearchBean parentSearchBean) {
		this.parentSearchBean = parentSearchBean;
	}
	
	public String getSelectedDDCCatValue() {
		return selectedDDCCatValue;
	}

	public void setSelectedDDCCatValue(String selectedDDCCatValue) {
		this.selectedDDCCatValue = selectedDDCCatValue;
	}

	public String getSelectedDDCCatName() {
		return selectedDDCCatName;
	}

	public void setSelectedDDCCatName(String selectedDDCCatName) {
		this.selectedDDCCatName = selectedDDCCatName;
	}
	
	public Boolean getShowDDCNums() {
		return showDDCNums;
	}

	public void setShowDDCNums(Boolean showDDCNums) {
		this.showDDCNums = showDDCNums;
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
	
	public void addDDCCategoryToPath(String strDDCValue) {
		int level = 1;
		int ddc = 0; try {ddc = Integer.parseInt(strDDCValue.substring(0, 3));} catch(Exception ex) {}
		if(ddc % 100 == 0) level = 0;
		if(ddc % 10 != 0 && ddc % 100 != 0) level = 2;
		this.pathDDCCategories = new ArrayList<String>();
		if(level == 0) {
			this.pathDDCCategories.add(strDDCValue);
		} else if(level == 1) {
			this.pathDDCCategories.add(strDDCValue.charAt(0)+ "00");
			this.pathDDCCategories.add(strDDCValue);
		} else if(level == 2) {
			this.pathDDCCategories.add(strDDCValue.charAt(0)+"00");
			this.pathDDCCategories.add(new StringBuffer().append(strDDCValue.charAt(0)).append(strDDCValue.charAt(1)).append('0').toString());
			this.pathDDCCategories.add(strDDCValue);
		} 
	}
		
	public String actionUnselectDDCCategoryLink() {
		this.setPathDDCCategories(new ArrayList<String>());
		this.setSelectedDDCCatValue(null);
		this.setSelectedDDCCatName("Alles");
		return "ddc_category_selected";
	}
	public String actionChangeVisibilityOfDDCCategoryNums() {
		this.setShowDDCNums(!this.showDDCNums);
		return "ddc_category_nums_visibility_changed";
	}
	
	
}
