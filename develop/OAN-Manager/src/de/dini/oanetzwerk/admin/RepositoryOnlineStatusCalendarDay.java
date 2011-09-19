package de.dini.oanetzwerk.admin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RepositoryOnlineStatusCalendarDay {

	private String date;
	private ArrayList<RepositoryOnlineStatusMeasure> onlineStatus;
	
	public RepositoryOnlineStatusCalendarDay(String timestamp) {
		this.onlineStatus = new ArrayList<RepositoryOnlineStatusMeasure>();
		this.date = calculateDateStamp(timestamp);
	}
	
	public boolean isSameDay(String timestamp) {
		return (calculateDateStamp(timestamp) == this.date);
	}
	
	public void addOnlineStatus(String timestamp, Boolean status) {
		RepositoryOnlineStatusMeasure newStatus = new RepositoryOnlineStatusMeasure(timestamp, status);
//		newStatus.setOnlineStatus(status);
//		newStatus.setTime(timestamp);
		this.onlineStatus.add(newStatus);
	}
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public ArrayList<RepositoryOnlineStatusMeasure> getOnlineStatus() {
		return onlineStatus;
	}
	public void setOnlineStatus(ArrayList<RepositoryOnlineStatusMeasure> onlineStatus) {
		this.onlineStatus = onlineStatus;
	}
	
	public static String calculateDateStamp(String timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date d = sdf.parse(timestamp);
			SimpleDateFormat sdf2 = new SimpleDateFormat("dd. MMM. yyyy");
			return  sdf2.format(d);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return  "Calculation Error";
		}
	}
}
