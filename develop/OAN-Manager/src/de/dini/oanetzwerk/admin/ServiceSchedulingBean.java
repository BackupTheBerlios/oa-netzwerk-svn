package de.dini.oanetzwerk.admin;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;

import de.dini.oanetzwerk.admin.utils.AbstractBean;

@ManagedBean(name = "sersche")
public class ServiceSchedulingBean extends AbstractBean implements Serializable{
	
	
    private static final long serialVersionUID = 1L;
    
	private String job;
	private String type;
	private int interval;
	private String info;
	private boolean monthChoice;
	private int month;
	private boolean weekChoice;
	private String week;
	private boolean dayChoice;
	private int day;
	private boolean changeJob;
	
	private boolean deactivated;
	private boolean deleted;
	private boolean stored;
	
	
	public ServiceSchedulingBean(){
		
	}
	
	public boolean success(){
		return deactivated || deleted || stored;
	}
	
	public void initServiceSchedulingBean(){
		
	}
	
//	public HashMap<String, String> getDetails() {
//		
//	}
	
	
	/*
	 * Nachfolgend store, delete und deactivate f�r einen Eintrag
	 */
	
	
//	public String store(){
//		
//	}
//	
//	public String delete(){
//		
//	}
//	
//	public String deactivate(){
//		
//	}
	
	
	
	
	/*
	 * Nachfolgend die setter- und getter- Methoden
	 */
	public void setJob(String s){
		this.job = s;
	}
	
	public String getJob(){
		return this.job;
	}
	
	public void setType(String s){
		this.type = s;
	}
	
	public String getType(){
		return this.type;
	}
	
	public void setInterval(int s){
		this.interval = s;
	}
	
	public int getInterval(){
		return this.interval;
	}
	
	public void setInfo(String s){
		this.info = s;
	}
	
	public String getInfo(){
		return this.info;
	}
	
	public void setMonthChoice(boolean s){
		this.monthChoice = s;
	}
	
	public boolean getMonthChoice(){
		return monthChoice;
	}
	
	public void setMonth(int s){
		this.month = s;
	}
	
	public int getMonth(){
		return this.month;
	}
	
	public void setWeekChoice(boolean s){
		this.weekChoice = s;
	}
	
	public boolean getWeekChoice(){
		return weekChoice;
	}
	
	public void setWeek(String s){
		this.week = s;
	}
	
	public String getWeek(){
		return this.week;
	}
	
	public void setDayChoice(boolean s){
		this.dayChoice = s;
	}
	
	public boolean getDayChoice(){
		return dayChoice;
	}
	
	public void setDay(int s){
		this.day = s;
	}
	
	public int getDay(){
		return this.day;
	}
	
	public void setChangeJob(boolean s){
		this.changeJob = s;
	}
	
	public boolean getChangeJob(){
		return this.changeJob;
	}
}
