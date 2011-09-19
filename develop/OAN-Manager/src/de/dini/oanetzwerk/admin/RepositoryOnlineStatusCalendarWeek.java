package de.dini.oanetzwerk.admin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

public class RepositoryOnlineStatusCalendarWeek {

	private String startOfWeek = "";
	private String startOfNextWeek = "";
	ArrayList<RepositoryOnlineStatusCalendarDay> days;
	private static Logger logger = Logger.getLogger(RepositoryOnlineStatusCalendarWeek.class);

	
	public RepositoryOnlineStatusCalendarWeek() {
		this.days = new ArrayList<RepositoryOnlineStatusCalendarDay>();
	}
	
	public void addDay(String timestamp, Boolean onlineStatus) {
		if (!this.isSameWeek(timestamp)) {
			return;
		}
//		logger.info("adding new Day to week: "+timestamp);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (days.isEmpty()) {
			
			
			
			try {
				Date d1 = sdf.parse(timestamp);
				Calendar cal = Calendar.getInstance();
				cal.setTime(d1);
				cal.set(cal.DAY_OF_WEEK, cal.getFirstDayOfWeek());
				cal.set(cal.HOUR_OF_DAY, 0);
				cal.set(cal.MINUTE, 0);
				cal.set(cal.SECOND, 0);
				Date d2 = new Date(cal.getTimeInMillis());
				this.startOfWeek = sdf.format(d2);
				
				cal.add(cal.DAY_OF_WEEK, 7); // is now the beginning of the following week
				this.startOfNextWeek = sdf.format(new Date(cal.getTimeInMillis()));
				
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (days.isEmpty()) {
//			logger.info("day list is empty - creating new one before inserting");
			RepositoryOnlineStatusCalendarDay day = new RepositoryOnlineStatusCalendarDay(timestamp);
			day.addOnlineStatus(timestamp, onlineStatus);
			days.add(day);
		}
		else { // erstma nachschauen ob es sich um den gleichen Tag handelt
			String currentDate = days.get(days.size() - 1).getDate();
//			logger.info("currentDate = "+currentDate);
			if (RepositoryOnlineStatusCalendarDay.calculateDateStamp(timestamp).equals(currentDate)) {
//				logger.info("given timestamp is for current day - inserting");
				RepositoryOnlineStatusCalendarDay currentDay = days.get(days.size() - 1);
				currentDay.addOnlineStatus(timestamp, onlineStatus);
				days.set(days.size() -1, currentDay);
			}
			else {
//				logger.info("given timestamp is not for current day - creating new day before inserting");
				RepositoryOnlineStatusCalendarDay newDay = new RepositoryOnlineStatusCalendarDay(timestamp);
				newDay.addOnlineStatus(timestamp, onlineStatus);
				days.add(newDay);
			}
		}
		
	}
	
	public void fillUp() {
		if (days.size() == 7) {
			return;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentDayStamp = this.startOfWeek;
		RepositoryOnlineStatusCalendarDay[] dayList = new RepositoryOnlineStatusCalendarDay[7];
		int currentDayFromList = 0; // to make sure that we don't access more days than present
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(sdf.parse(currentDayStamp));
			for (int i = 0; i < 7; i++) {
//				logger.info("fillUp() - days.size() = "+days.size());
//				logger.info("fillUp() - currentDay = "+currentDayFromList);
//				logger.info("fillUp() - currentDayStamp = "+currentDayStamp);
//				logger.info("fillUp() - calculated currentDayStamp = "+RepositoryOnlineStatusCalendarDay.calculateDateStamp(currentDayStamp));
				if (currentDayFromList < days.size() && RepositoryOnlineStatusCalendarDay.calculateDateStamp(currentDayStamp).equals(days.get(currentDayFromList).getDate())) {
					dayList[i] = days.get(currentDayFromList);
					currentDayFromList++;
//					logger.info("fillUp() - added day from list");
				}
				else {
					RepositoryOnlineStatusCalendarDay newDay = new RepositoryOnlineStatusCalendarDay(currentDayStamp);
					dayList[i] = newDay;
//					logger.info("fillUp() - added filler day");
				}
				// TODO: testen wie sich das beim Jahreswechsel verträgt
				cal.add(cal.DAY_OF_YEAR, 1);
				currentDayStamp = sdf.format(new Date(cal.getTimeInMillis()));
			}
			this.days.clear();
			for (int j = 0; j < dayList.length; j++) {
				this.days.add(dayList[j]);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public boolean isSameWeek(String timestamp) {
//		logger.info("Start of Week: "+this.startOfWeek);
//		logger.info("Timestamp: "+timestamp);
//		logger.info("Start of next Week: "+this.startOfNextWeek);
//		logger.info("---");
		if (
			this.startOfWeek.isEmpty() ||  			// day list is empty --> no days present
			this.startOfNextWeek.isEmpty() ||		// day list is empty --> no days present
			(
				this.startOfWeek.compareTo(timestamp) <= 0 && // startOfWeek nicht größer als timestamp
				this.startOfNextWeek.compareTo(timestamp) > 0 // startOfNextWeek größer als timestamp  
			)
		) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean isEmpty() {
		return days.isEmpty();
	}
	
	public ArrayList<RepositoryOnlineStatusCalendarDay> getDays() {
		return days;
	}
	public void setDays(ArrayList<RepositoryOnlineStatusCalendarDay> days) {
		this.days = days;
	}
}
