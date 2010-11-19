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
	private Properties props = null;
	private CompleteMetadataJAXBMarshaller cmMarsh = null;
	
	private SearchBean parentSearchBean = null;
		
	private List<BigDecimal> listHitOID;
	private UpdatingHitBeanHashMap mapHitBean;
	
	//private HashMap<BigDecimal, HitBean> mapHitBean;
	private BigDecimal selectedDetailsOID = null;
	private TreeSet<BigDecimal> setClipboardOID;
	
	public HitlistBean() throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
		
		this.props = HelperMethods.loadPropertiesFromFileWithinWebcontainer ("webapps/findnbrowse/WEB-INF/userfrontend_gui.xml");
		this.cmMarsh = CompleteMetadataJAXBMarshaller.getInstance();
		
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
	private RestClient prepareRestTransmission (String resource) {
		
		return RestClient.createRestClient (new File (System.getProperty ("catalina.base") + this.props.getProperty ("restclientpropfile")), resource, this.props.getProperty ("username"), this.props.getProperty ("password"));
	}

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
}
