package de.dini.oanetzwerk.userfrontend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.servicemodule.RestClient;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.imf.CompleteMetadata;
import de.dini.oanetzwerk.utils.imf.CompleteMetadataJAXBMarshaller;
import de.dini.oanetzwerk.utils.imf.DuplicateProbability;
import de.dini.oanetzwerk.utils.imf.FullTextLink;
import de.dini.oanetzwerk.utils.imf.InternalMetadata;

public class HitlistBean implements Serializable {
	
	private static Logger logger = Logger.getLogger (HitlistBean.class);
	private CompleteMetadataJAXBMarshaller cmMarsh = null;
	
	private SearchBean parentSearchBean = null;
		
	private List<BigDecimal> listHitOID;
	private UpdatingHitBeanHashMap mapHitBean;
	
	//private HashMap<BigDecimal, HitBean> mapHitBean;
	private BigDecimal selectedDetailsOID = null;
	private TreeSet<BigDecimal> setClipboardOID;
	
	private String strUsageStatisticsRID = null;
	private String strUsageStatisticsName = null;
	private String strUsageStatisticsURL = null;
	
	private boolean sortAscending = true; 

	
	public HitlistBean() throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
		String serverPath = WebUtils.getWebappPath();
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();		
		
		String query = request.getParameter("query");

		//init search bean
//		if (parentSearchBean == null) {
//			
//			System.out.println("Calling class: " + new Throwable().fillInStackTrace().getStackTrace()[1].getClassName()
//);
//			System.out.println("Init SearchBean from HitlistBean...");
//			parentSearchBean = new SearchBean(this);
//			parentSearchBean.setStrOneSlot(query);
//			parentSearchBean.actionSearchButton();
//		}
		
				
		//init xml marshaller
		this.cmMarsh = CompleteMetadataJAXBMarshaller.getInstance();
		
		// new list of oids (hits)
		this.listHitOID = new ArrayList<BigDecimal>();
		
		//this.mapHitBean = new HashMap<BigDecimal, HitBean>();
		this.mapHitBean = new UpdatingHitBeanHashMap(this);	
		
		this.setClipboardOID = new TreeSet<BigDecimal>(new Comparator<BigDecimal>() {
            // das Set ist immer nach dem trimmed title sortiert
			public int compare(BigDecimal o1, BigDecimal o2) {
				String title1 = mapHitBean.get(o1).getTrimmedTitle();
				String title2 = mapHitBean.get(o2).getTrimmedTitle();			
				return title1.compareTo(title2);
			}
		});

		
		
		
		//this.fakefillListHitOID();
		//this.updateHitlistMetadata();
		
	}
	
	///////////////////////////// AUTO GENERATED ///////////////////////////////
	
	public List<BigDecimal> getListHitOID() {
		return listHitOID;
	}

	public void setListHitOID(List<BigDecimal> listHitOID) {
		this.listHitOID = listHitOID;
	}

//	public HashMap<BigDecimal, HitBean> getMapHitBean() {
//		return mapHitBean;
//	}
//
//	public void setMapHitBean(HashMap<BigDecimal, HitBean> mapHitBean) {
//		this.mapHitBean = mapHitBean;
//	}

	public UpdatingHitBeanHashMap getMapHitBean() {
	    return mapHitBean;
    }

    public void setMapHitBean(UpdatingHitBeanHashMap mapHitBean) {
    	this.mapHitBean = mapHitBean;
    }

	public SearchBean getParentSearchBean() {
		return parentSearchBean;
	}

	public void setParentSearchBean(SearchBean parentSearchBean) {
		this.parentSearchBean = parentSearchBean;
	}	

	public TreeSet<BigDecimal> getSetClipboardOID() {
		return setClipboardOID;
	}

	public void setSetClipboardOID(TreeSet<BigDecimal> setClipboardOID) {
		this.setClipboardOID = setClipboardOID;
	}

	public BigDecimal getSelectedDetailsOID() {
		return selectedDetailsOID;
	}

	public void setSelectedDetailsOID(BigDecimal selectedDetailsOID) {
		this.selectedDetailsOID = selectedDetailsOID;
	}
	
	//////////////////////////////////////////////////////////////////////////////

	public void addSetClipboardOID(BigDecimal anOIDtoAdd) {
		this.setClipboardOID.add(anOIDtoAdd);
	}
	
	public void removeSetClipboardOID(BigDecimal anOIDtoAdd) {
		this.setClipboardOID.remove(anOIDtoAdd);
	}
	
	public List<BigDecimal> getListClipboardOID() {
		return new ArrayList<BigDecimal>(setClipboardOID);
	}
	
	public String getKommaSepClipboardOIDs() {
		StringBuffer result = new StringBuffer();
		Iterator<BigDecimal> it = setClipboardOID.iterator();
        int i = 0;
        while(it.hasNext()) {        	
		    if(i > 0) result.append(",");
			result.append(it.next());
			i++;
		}
		return result.toString();
	}
	
	public int getSizeListClipboardOID() {
		return setClipboardOID.size();
	}
	
	public int getSizeListHitOID() {
		return listHitOID.size();
	}
	public Boolean getBooleanListHitOIDEmpty() {
		return listHitOID.size()<1;
	}
	public Boolean getBooleanListHitOIDNotEmpty() {
		return listHitOID.size()>0;
	}
	public Boolean getBooleanShowSearchHelpNotice(){
		return (parentSearchBean.getStrOneSlot() == "" && getBooleanListHitOIDEmpty());
	}

	
	public void fakefillListHitOID() {
		listHitOID = new ArrayList<BigDecimal>();
				
		listHitOID.add(new BigDecimal("2569"));
		listHitOID.add(new BigDecimal("2143"));
		listHitOID.add(new BigDecimal("1402"));
		listHitOID.add(new BigDecimal("879"));
		listHitOID.add(new BigDecimal("16"));
		listHitOID.add(new BigDecimal("5175"));
		listHitOID.add(new BigDecimal("4786"));
		listHitOID.add(new BigDecimal("3892"));
		listHitOID.add(new BigDecimal("3664"));
		listHitOID.add(new BigDecimal("3161"));
		listHitOID.add(new BigDecimal("570"));
		listHitOID.add(new BigDecimal("4654"));

	}
	
	/**
	 * 
	 * Die Methode verwirft leere Treffer. Das konkrete Anlegen von HitBeans f�r die OID-Liste
	 * ist auf just-in-time Laden durch die UpdatingHitBeanHashMap verschoben, die get() so
	 * �berschreibt, dass erst im Bedarfsfall eine neue HitBean mit einem CompleteMetadata aus 
	 * der application-scope MetadataLoaderBean anlegt.
	 * 
	 */
	public void updateHitlistMetadata() {				
				
		//for(BigDecimal bdOID : listHitOID) {
			//if(mapHitBean.containsKey(bdOID)) continue;
			
			//CompleteMetadata cm = fetchCompleteMetadataByOID(bdOID);			
			//logger.debug("try to fetch cm for oid " + bdOID + " from metadataLoader");
			//CompleteMetadata cm = parentSearchBean.getMdLoaderBean().getMapCompleteMetadata().get(bdOID);
			
			//HitBean hb = new HitBean();
			//hb.setParentHitlistBean(this);

			//if(cm.getDuplicateProbabilityList() == null) cm.setDuplicateProbabilityList(new ArrayList<DuplicateProbability>());			
			//if(cm.getFullTextLinkList() == null) cm.setFullTextLinkList(new ArrayList<FullTextLink>());			
			
			//hb.setCompleteMetadata(cm);
			//mapHitBean.put(bdOID, hb);
		//}
		
		for(BigDecimal bdOID : listHitOID) {
			if(!mapHitBean.containsKey(bdOID)) {
				logger.debug("create emptry wrapper HitBean for oid " + bdOID + "");
				HitBean hb = new HitBean(bdOID);
				hb.setParentHitlistBean(this);
				mapHitBean.put(bdOID, hb);							
			}
		}
		
		// delete all oid from list (and hash) that have no real metadata
//		List<BigDecimal> cleanedListHitOID = new ArrayList<BigDecimal>();
//		for(int i = 0; i < listHitOID.size(); i++) {
//			BigDecimal bdOID = listHitOID.get(i);
//			logger.debug("check cm for oid " + bdOID);
//			CompleteMetadata cmCheck = mapHitBean.get(bdOID).getCompleteMetadata();
//			if(cmCheck != null && !cmCheck.isEmpty()) {
//				cleanedListHitOID.add(bdOID);
//				logger.debug("keep cm for oid " + bdOID);
//			}
//		}
//		listHitOID = cleanedListHitOID;
		
	}
	
	public void addHitbeanToMap(BigDecimal bdOID) {
			if(!mapHitBean.containsKey(bdOID)) {
				logger.debug("create emptry wrapper HitBean for oid " + bdOID + "");
				HitBean hb = new HitBean(bdOID);
				hb.setParentHitlistBean(this);
				mapHitBean.put(bdOID, hb);							
			}
	}
	
	/*

	private CompleteMetadata fetchCompleteMetadataByOID(BigDecimal oid) {
						
		RestMessage rms = this.prepareRestTransmission ("CompleteMetadataEntry/" + oid).sendGetRestMessage();
		CompleteMetadata cm = new CompleteMetadata();
		cm.setOid(oid);
		
		if (rms == null || rms.getListEntrySets ( ).isEmpty ( )) {
			
			logger.error ("received no complete metadata entry for oid " + oid);
			return null;
		}
		
		if (rms.getStatus() != RestStatusEnum.OK) {
			
			logger.error ("received status " + rms.getStatus() + " (" + rms.getStatusDescription() + ") for complete metadata entry for oid " + oid);
			return null;
		}
		
		logger.debug("resultset for oid " + oid);		
		
		for (RestEntrySet res : rms.getListEntrySets ( )) {
			
			Iterator <String> it = res.getKeyIterator ( );
			String key = "";
			
			while (it.hasNext ( )) {
				
				key = it.next ( );
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("key: " + key + " value: " + res.getValue (key));
				
				if (key.equalsIgnoreCase ("completemetadata")) {
					
					String strXML = res.getValue (key);
					
					cm = cmMarsh.unmarshall(strXML); 

				} else
					continue;
			}
			
		}
		
		return cm;
		
	}
*/	
	


	public void setHitStatisticstoMapHitBean() {
		for(BigDecimal bdOID : listHitOID) {
			RestMessage rms = WebUtils.prepareRestTransmission("UsageStatistics/"+ bdOID).sendGetRestMessage();
			if ((rms == null) ||((rms.getListEntrySets().isEmpty()) && (rms.getStatus() != RestStatusEnum.OK))|| (rms.getStatus() != RestStatusEnum.OK)) {
				logger.warn("received no UsageData_Ranking data ");
				return;
			}else if((rms.getListEntrySets().isEmpty()) && (rms.getStatus() == RestStatusEnum.OK)){
				mapHitBean.get(bdOID).setHitcounter(new BigDecimal(0));
				logger.info("received no UsageData_Ranking data, but Reststatus OK, there are no statistics for this OID: "+bdOID+", its Hitcounter was set to 0.");
			}else {			
				for (RestEntrySet res : rms.getListEntrySets()) {
					Iterator<String> it = res.getKeyIterator();
					String key = "";
					while (it.hasNext()) {
						key = (String) it.next();
	
						if ("counter".equals(key)){
							mapHitBean.get(bdOID).setHitcounter(new BigDecimal(res.getValue(key)));
							logger.info("Key"+ key + ": " + res.getValue(key) + "");
						}
						
					}
				}
			}
			
		}
	}
//	//sort MapHitBean by OID
//	public void sortMapHitBeanByOID() {
// 
//	   if(sortAscending){
// 
//		//ascending order
//		Collections.sort(orderArrayList, new Comparator<Order>() {
// 
//			@Override
//			public int compare(Order o1, Order o2) {
// 
//				return o1.getOrderNo().compareTo(o2.getOrderNo());
// 
//			}
// 
//		});
//		sortAscending = false;
// 
//	   }else{
// 
//		//descending order
//		Collections.sort(orderArrayList, new Comparator<Order>() {
// 
//			@Override
//			public int compare(Order o1, Order o2) {
// 
//				return o2.getOrderNo().compareTo(o1.getOrderNo());
// 
//			}
// 
//		});
//		sortAscending = true;
//	   }
//	}

}
