package de.dini.oanetzwerk.validator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.validator.utils.AbstractBean;

@ManagedBean(name = "vali")
public class ValidationBean extends AbstractBean implements Serializable{
	
	private Logger logger = Logger.getLogger(ValidationBean.class);
	
	private boolean deactivated = false;
	private boolean deleted = false;
	private boolean stored = false;
	
	FacesContext ctx = FacesContext.getCurrentInstance();
	HttpSession session = (HttpSession) ctx.getExternalContext().getSession(false);
	
	ValidationBean(){
		super();
	}
	
	private Integer id;
	private String oaiUrl;
	private String date;
	private String state;
	private String duration;
	private String rule;
	private String rstate;
	private String detail;
	private String ruleId;
	private List<String> errorList;

	
	//Die drei Möglichen Zustände einer Session als Boolean ausgeben
	public boolean success(){
		return deactivated || deleted || stored;
	}
	


	//Liefert Details zu den einzelnen Eintraegen
	public HashMap<String, String> getDetails() {

		String result = this.prepareRestTransmission("Validation/" + Long.toString(id)).GetData();

		HashMap<String, String> details = new HashMap<String, String>();
		RestMessage rms = RestXmlCodec.decodeRestMessage(result);

	if (rms == null || rms.getListEntrySets().isEmpty()) {

		logger.error("received no Repository Details at all from the server");
		return null;
	}

	RestEntrySet res = rms.getListEntrySets().get(0);
	Iterator<String> it = res.getKeyIterator();
	String key = "";

	while (it.hasNext()) {
	key = it.next();
	details.put(key, res.getValue(key));
	}
	return details;
	}
	
	
	/*
	 * Nachfolgend werden die Daten entweder gespeichert, geloescht oder 
	 */
	public String storeRepository(){
		System.out.println("OAI-Url: " + oaiUrl);
		System.out.println("Datum: " + date);
		System.out.println("Dauer: " + duration);
		System.out.println("Status: " + state);
		System.out.println("Regel-ID: " + ruleId);
		System.out.println("Regel: " + rule);
		System.out.println("RStatus: " + rstate);
		System.out.println("Details: " + detail);
		logger.warn("Bla");
		
		RestMessage rms;
		RestEntrySet res;
		RestMessage result = null;

//		rms = new RestMessage();
//
//		rms.setKeyword(RestKeyword.Repository);
//		rms.setStatus(RestStatusEnum.OK);
//
//		res = new RestEntrySet();
//		
//		res.addEntry(oaiUrl, this.getOaiUrl());
//		res.addEntry(date, this.getDate());
//		res.addEntry(state, this.getState());
//		//res.addEntry(rule, this.getRule());
//		//res.addEntry(rstate, this.getRState());
//		//res.addEntry(rule, this.getRule());
//		
//		
//		if(this.isActive() == true){
//			res.addEntry("isActive", "true");
//		} else{
//			res.addEntry("isActive", "false");
//		}
//		if(this.li() == true){
//			res.addEntry("islistRecords", "true");
//		} else{
//			res.addEntry("isListRecords", "false");
//		}	
//		
//		rms.addEntrySet(res);
//		
//		
//		try {
//			result = prepareRestTransmission("Validation/").sendPostRestMessage(rms);
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		logger.info("PUT sent to /Validation");
//
//		
//
//		ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
//				"info.success_stored", null));

		return "success";
		
	}
	

	
	
	/*
	 * Nachfolgend die setter- und getter- Methoden
	 * */
	public String getOaiUrl(){
		return oaiUrl;
	}
	
	public void setOaiUrl(String oaiUrl){
		this.oaiUrl = oaiUrl;
	}
	
	public String getDate(){
		return date;
	}
	
	public void setDate(String date){
		this.date = date;
	}
	
	public String getState(){
		return state;
	}
	
	public void setState(String state){
		this.state = state;
	}
	
	public String getDuration(){
		return duration;
	}
	
	public void setDuration(String duration){
		this.duration = duration;
	}
	
	public String getRule(){
		return rule;
	}
	
	public void setRule(String rule){
		this.rule = rule;
	}
	
	public String getRstate(){
		return rstate;
	}

	public void setRstate(String rstate){
		this.rstate = rstate;
	}
	
	public String getDetail(){
		return detail;
	}

	public void setDetail(String detail){
		this.detail = detail;
	}
	
	public String getRuleId(){
		return ruleId;
	}

	public void setRuleId(String ruleId){
		this.ruleId = ruleId;
		}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
		
	
	
}


