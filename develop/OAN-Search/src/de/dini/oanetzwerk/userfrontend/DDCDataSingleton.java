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


public final class DDCDataSingleton {

	private static Logger logger = Logger.getLogger (DDCDataSingleton.class);

	private static final DDCDataSingleton INSTANCE = new DDCDataSingleton();
	
	private Properties props = null;
	
	DDCNameResolver myDDCNameResolver = null;
	private HashMap mapDDCNames_de = null;
	private HashMap<String,String> mapDDCNames_de_fromDB = null;
	private List<String[]> simpleDDCCategorySums = null;
	private HashMap<String,String[]> mapDDCSums = null;
	private List<String[]> directDDCCategorySums = null;
	private List<DDCNaviNode> listDDCNaviNodes = null;
	
	private DDCDataSingleton() {
		simpleDDCCategorySums = new ArrayList<String[]>();
		mapDDCSums = new HashMap<String,String[]>();
		mapDDCNames_de_fromDB = new HashMap<String, String>();
		try {
			myDDCNameResolver = new DDCNameResolver();
			setupMapDDCNames();			
			try {
		      this.props = HelperMethods.loadPropertiesFromFileWithinWebcontainer(Utils.getWebappPath() + "/WEB-INF/userfrontend_gui.xml");
			} catch(Exception ex) {
				// to test this external from server context
  			  this.props = HelperMethods.loadPropertiesFromFile ("userfrontend_gui.xml");	
			}
		    simpleDDCCategorySums = generateSimpleDDCCategorySums();
		    directDDCCategorySums = new ArrayList(); //generateDirectDDCCategorySums(simpleDDCCategorySums);
		    listDDCNaviNodes = generateListDDCNaviNodes(simpleDDCCategorySums);
		    
		} catch(Exception ex) {
			logger.error("error at singleton startup : ", ex);
		}
	}
	
	public static DDCDataSingleton getInstance() {
		return INSTANCE;
	}

	public List<String[]> getSimpleDDCCategorySums() {
		return simpleDDCCategorySums;
	}
	
	public List<String[]> getDirectDDCCategorySums() {
		return directDDCCategorySums;
	}

	public HashMap getMapDDCNames_de() {
		return mapDDCNames_de;
	}

	public List<DDCNaviNode> getListDDCNaviNodes() {
		return listDDCNaviNodes;
	}
	
	private void setParentBrowseBeanRecursive(DDCNaviNode node, BrowseBean parentBrowseBean) {
		node.setParentBrowseBean(parentBrowseBean);
		List<DDCNaviNode> listNodes = node.getListSubnodes();
		if(listNodes != null && !listNodes.isEmpty()) {
		   for(DDCNaviNode subnode: listNodes) setParentBrowseBeanRecursive(subnode, parentBrowseBean);
		}
	}
	
	public List<DDCNaviNode> getListDDCNaviNodes(BrowseBean parentBrowseBean) {
		List<DDCNaviNode> newListDDCNaviNodes = generateListDDCNaviNodes(simpleDDCCategorySums);
		for(DDCNaviNode node: newListDDCNaviNodes) setParentBrowseBeanRecursive(node, parentBrowseBean);
		return newListDDCNaviNodes;
	}
	
	public HashMap<String, String> getMapDDCNames_de_fromDB() {
		return mapDDCNames_de_fromDB;
	}

	public void setMapDDCNames_de_fromDB(
			HashMap<String, String> mapDDCNames_de_fromDB) {
		this.mapDDCNames_de_fromDB = mapDDCNames_de_fromDB;
	}	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	

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
				cat1 = Integer.parseInt(o1[0].substring(0, 3));
				cat2 = Integer.parseInt(o2[0].substring(0, 3));
			} catch(Exception ex) {
				return 0;
			}			
			return cat1 - cat2;
		}
		
	}
	
	private class CategoryCountComparator implements Comparator<String[]> {

		public int compare(String[] o1, String[] o2) {
			int cat1, cat2;
			try {
				cat1 = Integer.parseInt(o1[1]);
				cat2 = Integer.parseInt(o2[1]);
			} catch(Exception ex) {
				return 0;
			}			
			return cat2 - cat1;
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
			
			String strCat = null;
			String strDirectSum = null;
			String strSubSum = null;
			String strCatName = null;
			while (it.hasNext ( )) {
				key = it.next ( );
				if("DDC_Categorie".equals(key)) strCat = res.getValue (key);
				if("direct_count".equals(key)) strDirectSum = res.getValue (key);
				if("sub_count".equals(key)) strSubSum = res.getValue (key);
				if("name_deu".equals(key)) strCatName = res.getValue (key);
			}
			listDDCCategoriesAndSum.add(new String[] {strCat, strDirectSum} );	
			mapDDCSums.put(strCat, new String[] {strDirectSum, strSubSum});
			mapDDCNames_de_fromDB.put(strCat, strCatName);
		}
		
		//Collections.sort(listDDCCategoriesAndSum, new CategoryNumberComparator());
		Collections.sort(listDDCCategoriesAndSum, new CategoryCountComparator());
		
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

	private DDCNaviNode getDDCNaviNodeForCat(String strCategory) {
		DDCNaviNode node = new DDCNaviNode();
		
		String strWildcardCategory = strCategory;
		strWildcardCategory.replaceAll("00$", "x");
		strWildcardCategory.replaceAll("0$", "x");
		strWildcardCategory.replaceAll("[.][0-9]", ".x");
		
		String sum = "";
					
		RestMessage rms = this.prepareRestTransmission ("DDCCategories/" + strWildcardCategory).sendGetRestMessage();

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
				if(key.equals("sum")) sum = res.getValue(key);
			}												
		}
		
		node.setStrDDCValue(strCategory);
		String name = (String)mapDDCNames_de.get(strCategory);
		node.setStrNameDE(name);
		try {
			node.setLongItemCount(Long.parseLong(sum));
		} catch (Exception ex) {
			node.setLongItemCount(-1);
		}
		
		return node;
	}
	
	private List<DDCNaviNode> generateListDDCNaviNodes() {
		
		List<DDCNaviNode> newList = new ArrayList<DDCNaviNode>();
		
		for(int i = 0; i < 10; i++) {			
			
			String strCategory_lvl1 = "" + i + "00";
			DDCNaviNode node_lvl1 = getDDCNaviNodeForCat(strCategory_lvl1);
			
			for(int j = 1; j < 10; j++) {
				
				String strCategory_lvl2 = "" + i + j + "0";
				DDCNaviNode node_lvl2 = getDDCNaviNodeForCat(strCategory_lvl2);
				
				for(int k = 1; k < 10; k++) {
					
					String strCategory_lvl3 = "" + i + j + k;
					DDCNaviNode node_lvl3 = getDDCNaviNodeForCat(strCategory_lvl3);
					
					
					/*for(int l = 1; l < 10; l++) {
						
						String strCategory_lvl4 = "" + i + j + k + "." + l;
						DDCNaviNode node_lvl4 = getDDCNaviNodeForCat(strCategory_lvl4);
												
						if(node_lvl4.getLongItemCount() > 0) node_lvl3.getListSubnodes().add(node_lvl4);
						
					}*/
					
					if(node_lvl3.getLongItemCount() > 0) node_lvl2.getListSubnodes().add(node_lvl3);
					
				}
				
				if(node_lvl2.getLongItemCount() > 0) node_lvl1.getListSubnodes().add(node_lvl2);
			}
			
			if(node_lvl1.getLongItemCount() > 0) newList.add(node_lvl1);
		}
		
		return newList;
	}
	
	private List<DDCNaviNode> generateListDDCNaviNodes(List<String[]> flatList) {
		
		List<DDCNaviNode> nodeList = new ArrayList<DDCNaviNode>();
		/*		
		for(String[] entry : flatList) {
			Pattern p = Pattern.compile( "[0-9]00");		
			Matcher m = p.matcher(entry[0]); 
			if(m.matches()) {
				DDCNaviNode node = new DDCNaviNode();
				node.setStrDDCValue(entry[0]);
				try {
					node.setLongItemCount(Long.parseLong(entry[1]));
				} catch (Exception ex) {
					node.setLongItemCount(-1);
				}
				String name = (String)mapDDCNames_de.get(entry[0]);
				node.setStrNameDE(name);
				nodeList.add(node);
			}
		}	*/
		
		for(int i = 0; i < 10; i++) {
			DDCNaviNode node1 = new DDCNaviNode();
			String cat1 = ""+i+"00";
			node1.setStrDDCValue(cat1);
			try {
				String sum = mapDDCSums.get(cat1)[0];
				if(sum == null) sum = "0";
				node1.setLongItemCount(Long.parseLong(sum));
				sum = mapDDCSums.get(cat1)[1];
				if(sum == null) sum = "0";
				node1.setLongItemSubCount(Long.parseLong(sum));
			} catch (Exception ex) {
				node1.setLongItemCount(-1);
			}			
			String name1 = (String)mapDDCNames_de.get(cat1);
			node1.setStrNameDE(name1);
			
			
				for(int k = 1; k < 10; k++) {
					DDCNaviNode node3 = new DDCNaviNode();
					String cat3 = i+"0"+k;
					node3.setStrDDCValue(cat3);
					try {
						String sum = mapDDCSums.get(cat3)[0];
						if(sum == null) sum = "0";
						node3.setLongItemCount(Long.parseLong(sum));
						sum = mapDDCSums.get(cat3)[1];
						if(sum == null) sum = "0";
						node3.setLongItemSubCount(Long.parseLong(sum));
					} catch (Exception ex) {
						node3.setLongItemCount(-1);
					}		
					String name3 = (String)mapDDCNames_de.get(cat3);
					node3.setStrNameDE(name3);

					if(node3.getLongItemCount() > 0) node1.getListSubnodes().add(node3);
				}
			
			
			for(int j = 1; j < 10; j++) {
				DDCNaviNode node2 = new DDCNaviNode();
				String cat2 = ""+i+j+"0";
				node2.setStrDDCValue(cat2);
				try {
					String sum = mapDDCSums.get(cat2)[0];
					if(sum == null) sum = "0";
					node2.setLongItemCount(Long.parseLong(sum));
					sum = mapDDCSums.get(cat2)[1];
					if(sum == null) sum = "0";
					node2.setLongItemSubCount(Long.parseLong(sum));
				} catch (Exception ex) {
					node2.setLongItemCount(-1);
				}		
				String name2 = (String)mapDDCNames_de.get(cat2);
				node2.setStrNameDE(name2);
				
				for(int k = 1; k < 10; k++) {
					DDCNaviNode node3 = new DDCNaviNode();
					String cat3 = ""+i+j+k;
					node3.setStrDDCValue(cat3);
					try {
						String sum = mapDDCSums.get(cat3)[0];
						if(sum == null) sum = "0";
						node3.setLongItemCount(Long.parseLong(sum));
						sum = mapDDCSums.get(cat3)[1];
						if(sum == null) sum = "0";
						node3.setLongItemSubCount(Long.parseLong(sum));
					} catch (Exception ex) {
						node3.setLongItemCount(-1);
					}		
					String name3 = (String)mapDDCNames_de.get(cat3);
					node3.setStrNameDE(name3);

					if(node3.getLongItemCount() > 0) node2.getListSubnodes().add(node3);
				}				
				
				if(node2.getLongItemCount() > 0) node1.getListSubnodes().add(node2);
				
			}
			
			if(node1.getLongItemCount() > 0) nodeList.add(node1);							

		}		
		
		return nodeList;
	}
	

	
}
