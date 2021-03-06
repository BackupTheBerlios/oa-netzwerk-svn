package de.dini.oanetzwerk.admin;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;


public class ValidationBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private static SimpleDateFormat germanFormat = new SimpleDateFormat("dd.MM.yyyy");
	private static SimpleDateFormat englishFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	private Logger logger = Logger.getLogger(ValidationBean.class);
	
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

		
	ValidationBean(){
		super();
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
	
	public String getPrettyDate() {
		try {
	        return FacesContext.getCurrentInstance().getViewRoot().getLocale().equals(Locale.GERMAN) 
	        		? germanFormat.format(englishFormat.parse(date))
	        		: date;
        } catch (ParseException e) {
	        logger.warn("Could not format date according to current locale. " + date);
	        return date;
        }
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
