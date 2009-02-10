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
	
	public BrowseBean() throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
		this.props = HelperMethods.loadPropertiesFromFile ("webapps/findnbrowse/WEB-INF/userfrontend_gui.xml");

		//mapDDCSums = new HashMap<String, String>();
		myDDCNameResolver = new DDCNameResolver();
		setupMapDDCNames();
				
		this.simpleDDCCategorySums = DDCDataSingleton.getInstance().getSimpleDDCCategorySums();
		this.directDDCCategorySums = DDCDataSingleton.getInstance().getDirectDDCCategorySums();
			//	new ArrayList<String[]>(); 
		//generateDirectDDCCategorySums(simpleDDCCategorySums);	
		this.listDDCNaviNodes = DDCDataSingleton.getInstance().getListDDCNaviNodes(); 
			//new ArrayList<DDCNaviNode>(); 
		//generateListDDCNaviNodes(simpleDDCCategorySums);
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

	public void setListDDCNaviNodes(List<DDCNaviNode> listDDCNaviNodes) {
		this.listDDCNaviNodes = listDDCNaviNodes;
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
	
//	private List<String[]> generateSimpleDDCCategorySums() {
//			
//		ArrayList<String[]> listDDCCategoriesAndSum = new ArrayList<String[]>();
//		RestMessage rms = this.prepareRestTransmission ("DDCCategories/").sendGetRestMessage();
//		
//		if (rms == null || rms.getListEntrySets ( ).isEmpty ( )) {
//			
//			logger.error ("received no DDCCategories");
//			return null;
//		}
//		
//		if (rms.getStatus() != RestStatusEnum.OK) {
//			
//			logger.error ("received status " + rms.getStatus() + " (" + rms.getStatusDescription() + ") for DDCCategories");
//			return null;
//		}
//		
//		for (RestEntrySet res : rms.getListEntrySets ( )) {
//			
//			Iterator <String> it = res.getKeyIterator ( );
//			String key = "";
//			
//			while (it.hasNext ( )) {
//				key = it.next ( );
//				listDDCCategoriesAndSum.add(new String[] {key, res.getValue (key)} );	
//				mapDDCSums.put(key, res.getValue (key));
//			}
//			
//		}
//		
//		Collections.sort(listDDCCategoriesAndSum, new CategoryNumberComparator());
//		
//		return listDDCCategoriesAndSum;
//	}

//	private List<String[]> generateDirectDDCCategorySums(List<String[]> listSimpleSums) {
//		
//		ArrayList<String[]> listDDCCategoriesAndSum = new ArrayList<String[]>();
//		
//		for(String entry[] : listSimpleSums) {
//			
//			String wildcardCategory = entry[0];
//			wildcardCategory = wildcardCategory.replaceAll("00$", "x");
//			wildcardCategory = wildcardCategory.replaceAll("0$", "x");
//			
//			RestMessage rms = this.prepareRestTransmission ("DDCCategories/" + wildcardCategory).sendGetRestMessage();
//
//			if (rms == null || rms.getListEntrySets ( ).isEmpty ( )) {
//				logger.error ("received no DDCCategories");
//				return null;
//			}
//
//			if (rms.getStatus() != RestStatusEnum.OK) {
//				logger.error ("received status " + rms.getStatus() + " (" + rms.getStatusDescription() + ") for DDCCategories");
//				return null;
//			}
//
//			for (RestEntrySet res : rms.getListEntrySets ( )) {
//
//				Iterator <String> it = res.getKeyIterator ( );
//				String key = "";
//
//				while (it.hasNext ( )) {
//					key = it.next ( );
//					if(key.equals("sum")) listDDCCategoriesAndSum.add(new String[] {entry[0], res.getValue (key), wildcardCategory} );				
//				}
//
//			}
//		}
//				
//		return listDDCCategoriesAndSum;
//	}
//
//	private DDCNaviNode getDDCNaviNodeForCat(String strCategory) {
//		DDCNaviNode node = new DDCNaviNode();
//		
//		String strWildcardCategory = strCategory;
//		strWildcardCategory.replaceAll("00$", "x");
//		strWildcardCategory.replaceAll("0$", "x");
//		strWildcardCategory.replaceAll("[.][0-9]", ".x");
//		
//		String sum = "";
//					
//		RestMessage rms = this.prepareRestTransmission ("DDCCategories/" + strWildcardCategory).sendGetRestMessage();
//
//		if (rms == null || rms.getListEntrySets ( ).isEmpty ( )) {
//			logger.error ("received no DDCCategories");
//			return null;
//		}
//
//		if (rms.getStatus() != RestStatusEnum.OK) {
//			logger.error ("received status " + rms.getStatus() + " (" + rms.getStatusDescription() + ") for DDCCategories");
//			return null;
//		}
//
//		for (RestEntrySet res : rms.getListEntrySets ( )) {
//			Iterator <String> it = res.getKeyIterator ( );
//			String key = "";
//			while (it.hasNext ( )) {
//				key = it.next ( );					
//				if(key.equals("sum")) sum = res.getValue(key);
//			}												
//		}
//		
//		node.setStrDDCValue(strCategory);
//		String name = (String)mapDDCNames_de.get(strCategory);
//		node.setStrNameDE(name);
//		try {
//			node.setLongItemCount(Long.parseLong(sum));
//		} catch (Exception ex) {
//			node.setLongItemCount(-1);
//		}
//		
//		return node;
//	}
//	
//	private List<DDCNaviNode> generateListDDCNaviNodes() {
//		
//		List<DDCNaviNode> newList = new ArrayList<DDCNaviNode>();
//		
//		for(int i = 0; i < 10; i++) {			
//			
//			String strCategory_lvl1 = "" + i + "00";
//			DDCNaviNode node_lvl1 = getDDCNaviNodeForCat(strCategory_lvl1);
//			
//			for(int j = 1; j < 10; j++) {
//				
//				String strCategory_lvl2 = "" + i + j + "0";
//				DDCNaviNode node_lvl2 = getDDCNaviNodeForCat(strCategory_lvl2);
//				
//				for(int k = 1; k < 10; k++) {
//					
//					String strCategory_lvl3 = "" + i + j + k;
//					DDCNaviNode node_lvl3 = getDDCNaviNodeForCat(strCategory_lvl3);
//					
//					/*
//					for(int l = 1; l < 10; l++) {
//						
//						String strCategory_lvl4 = "" + i + j + k + "." + l;
//						DDCNaviNode node_lvl4 = getDDCNaviNodeForCat(strCategory_lvl4);
//												
//						if(node_lvl4.getLongItemCount() > 0) node_lvl3.getListSubnodes().add(node_lvl4);
//						
//					}*/
//					
//					if(node_lvl3.getLongItemCount() > 0) node_lvl2.getListSubnodes().add(node_lvl3);
//					
//				}
//				
//				if(node_lvl2.getLongItemCount() > 0) node_lvl1.getListSubnodes().add(node_lvl2);
//			}
//			
//			if(node_lvl1.getLongItemCount() > 0) newList.add(node_lvl1);
//		}
//		
//		return newList;
//	}
//	
//	private List<DDCNaviNode> generateListDDCNaviNodes(List<String[]> flatList) {
//		
//		List<DDCNaviNode> nodeList = new ArrayList<DDCNaviNode>();
//		/*		
//		for(String[] entry : flatList) {
//			Pattern p = Pattern.compile( "[0-9]00");		
//			Matcher m = p.matcher(entry[0]); 
//			if(m.matches()) {
//				DDCNaviNode node = new DDCNaviNode();
//				node.setStrDDCValue(entry[0]);
//				try {
//					node.setLongItemCount(Long.parseLong(entry[1]));
//				} catch (Exception ex) {
//					node.setLongItemCount(-1);
//				}
//				String name = (String)mapDDCNames_de.get(entry[0]);
//				node.setStrNameDE(name);
//				nodeList.add(node);
//			}
//		}	*/
//		
//		for(int i = 0; i < 10; i++) {
//			DDCNaviNode node1 = new DDCNaviNode();
//			String cat1 = ""+i+"00";
//			node1.setStrDDCValue(cat1);
//			try {
//				String sum = mapDDCSums.get(cat1);
//				if(sum == null) sum = "0";
//				node1.setLongItemCount(Long.parseLong(sum));
//			} catch (Exception ex) {
//				node1.setLongItemCount(-1);
//			}
//			String name1 = (String)mapDDCNames_de.get(cat1);
//			node1.setStrNameDE(name1);
//			for(int j = 1; j < 10; j++) {
//				DDCNaviNode node2 = new DDCNaviNode();
//				String cat2 = ""+i+j+"0";
//				node2.setStrDDCValue(cat2);
//				try {
//					String sum = mapDDCSums.get(cat2);
//					if(sum == null) sum = "0";
//					node2.setLongItemCount(Long.parseLong(sum));
//				} catch (Exception ex) {
//					node2.setLongItemCount(-1);
//				}		
//				String name2 = (String)mapDDCNames_de.get(cat2);
//				node2.setStrNameDE(name2);
//				if(node2.getLongItemCount() > 0) node1.getListSubnodes().add(node2);
//			}
//			if(node1.getLongItemCount() > 0) nodeList.add(node1);			
//		}		
//		
//		return nodeList;
//	}
}
