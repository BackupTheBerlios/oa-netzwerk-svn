package de.dini.oanetzwerk.userfrontend;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.dini.oanetzwerk.utils.imf.CompleteMetadata;

public class HitlistBean {
	
	private List<BigDecimal> listHitOID;
	
	//private List<CompleteMetadata> listCompleteMetadata;
	
	private HashMap<BigDecimal, CompleteMetadata> mapCompleteMetadata;
	
	public HitlistBean() {
		listHitOID = new ArrayList<BigDecimal>();
		mapCompleteMetadata = new HashMap<BigDecimal, CompleteMetadata>();	
//		listCompleteMetadata = new ArrayList<CompleteMetadata>();		
		
//		listCompleteMetadata.add(CompleteMetadata.createDummy());
//		listCompleteMetadata.add(CompleteMetadata.createDummy());
//		listCompleteMetadata.add(CompleteMetadata.createDummy());
		
		listHitOID.add(new BigDecimal("633"));
		listHitOID.add(new BigDecimal("2063"));
		listHitOID.add(new BigDecimal("2064"));
		
		for(BigDecimal oid : listHitOID) {
			CompleteMetadata cm = CompleteMetadata.createDummy();
			cm.setOid(oid);
			mapCompleteMetadata.put(oid, cm);
		}
		
	}
	
	public List<BigDecimal> getListHitOID() {
		return listHitOID;
	}

	public void setListHitOID(List<BigDecimal> listHitOID) {
		this.listHitOID = listHitOID;
	}

	public HashMap<BigDecimal, CompleteMetadata> getMapCompleteMetadata() {
		return mapCompleteMetadata;
	}

	public void setMapCompleteMetadata(
			HashMap<BigDecimal, CompleteMetadata> mapCompleteMetadata) {
		this.mapCompleteMetadata = mapCompleteMetadata;
	}

	public int getSizeListHitOID() {
		return listHitOID.size();
	}
	
//	public List<CompleteMetadata> getListCompleteMetadata() {
//		return listCompleteMetadata;
//	}
//
//	public void setListCompleteMetadata(List<CompleteMetadata> listCompleteMetadata) {
//		this.listCompleteMetadata = listCompleteMetadata;
//	}
//
//	public int getSizeListCompleteMetadata() {
//		return listCompleteMetadata.size();
//	}
	
}
