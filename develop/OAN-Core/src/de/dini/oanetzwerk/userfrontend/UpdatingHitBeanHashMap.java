package de.dini.oanetzwerk.userfrontend;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.utils.imf.CompleteMetadata;
import de.dini.oanetzwerk.utils.imf.DuplicateProbability;
import de.dini.oanetzwerk.utils.imf.FullTextLink;

public class UpdatingHitBeanHashMap extends HashMap<BigDecimal, HitBean> {

	private static Logger logger = Logger.getLogger (UpdatingHitBeanHashMap.class);

	HitlistBean parentHitlistBean;
	
	public UpdatingHitBeanHashMap(HitlistBean hlb) {
		super();
		this.parentHitlistBean = hlb;
	}
	
	public HitBean get(BigDecimal bdOID) {
//		logger.debug("UpdatingHitBeanHashMap.get("+bdOID+") called");
		HitBean hb = null;
		
		hb = super.get(bdOID);
		
		if(hb == null) {
			logger.debug("try to fetch cm for oid " + bdOID + " from metadataLoader");
			CompleteMetadata cm = parentHitlistBean.getParentSearchBean().getMdLoaderBean().getMapCompleteMetadata().get(bdOID);
			hb = new HitBean();
			hb.setParentHitlistBean(parentHitlistBean);
			if(cm.getDuplicateProbabilityList() == null) cm.setDuplicateProbabilityList(new ArrayList<DuplicateProbability>());			
			if(cm.getFullTextLinkList() == null) cm.setFullTextLinkList(new ArrayList<FullTextLink>());			
			hb.setCompleteMetadata(cm);
			super.put(bdOID, hb);
		}
		
		return hb; 
	}
		
}
