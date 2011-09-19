package de.dini.oanetzwerk.admin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

public class RepositoryOnlineStatusMeasure {

	private String time;
	private Boolean onlineStatus;
	private static Logger logger = Logger.getLogger(RepositoryOnlineStatusMeasure.class);

	
	public RepositoryOnlineStatusMeasure(String timestamp, Boolean status) {
		this.setTime(timestamp);
		this.setOnlineStatus(status);
	}
	
	
	public String getTime() {
		return time;
	}
	public void setTime(String timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date d = sdf.parse(timestamp);
			SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
			this.time = sdf2.format(d);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.time = "Calculation Error";
		}
		
	}
	public boolean getOnlineStatus() {
		logger.info("getOnlineStatus - "+time+" - "+(onlineStatus.booleanValue() ? "anwesend" : "nicht anwesend"));
		return onlineStatus.booleanValue();
	}
	public void setOnlineStatus(Boolean onlineStatus) {
		logger.info("Setze Status für "+time+" auf "+onlineStatus.toString());
		this.onlineStatus = onlineStatus;
	}
	public void setOnlineStatus(boolean onlineStatus) {
		logger.info("Setze OnlineStatus für "+time+" auf "+(onlineStatus ? "wahr" : "falsch"));
		this.onlineStatus = new Boolean(onlineStatus);
	}
	
	
}
