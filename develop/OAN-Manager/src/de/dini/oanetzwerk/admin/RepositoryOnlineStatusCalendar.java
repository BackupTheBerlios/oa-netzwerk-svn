package de.dini.oanetzwerk.admin;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

@ManagedBean(name = "repositoryOnlineStatusCalendar")
@RequestScoped
public class RepositoryOnlineStatusCalendar {

	
	@ManagedProperty(value="#{repositoryOnlineStatus}")
	private RepositoryOnlineStatusBean repoStatus;
	private ArrayList<RepositoryOnlineStatusCalendarWeek> weekList;
	private Long repositoryID;
	FacesContext ctx = FacesContext.getCurrentInstance();
	HttpSession session = (HttpSession) ctx.getExternalContext().getSession(false);
	private static Logger logger = Logger.getLogger(RepositoryOnlineStatusCalendar.class);

	public RepositoryOnlineStatusCalendar() {
		HttpServletRequest request = (HttpServletRequest) ctx.getExternalContext().getRequest();
		this.repositoryID = Long.decode(request.getParameter("id"));
		this.setRepositoryID(repositoryID);
		this.weekList = new ArrayList<RepositoryOnlineStatusCalendarWeek>();
		logger.info("RepositoryOnlineStatusCalendar");
		logger.info("Current Repository ID = "+this.repositoryID);
	}
	
	public String[] getWeekDays() {
		Calendar cal = Calendar.getInstance();
		int startValue = cal.getFirstDayOfWeek();
		String[] weekdays = DateFormatSymbols.getInstance().getWeekdays();
		String[] returnvalues = new String[7];
		int arrayIndex = 0;
		for (int i = startValue; i < startValue + 7; i++ ) {
//			logger.info(i);
			returnvalues[arrayIndex] = weekdays[((i - 1) % 7) + 1];
			arrayIndex++;
		}
//		logger.info(cal.getFirstDayOfWeek());
//		logger.info(returnvalues.length);
//		logger.info(returnvalues[0]);
//		logger.info(returnvalues[1]);
//		logger.info(returnvalues[2]);
//		logger.info(returnvalues[3]);
//		logger.info(returnvalues[4]);
//		logger.info(returnvalues[5]);
//		logger.info(returnvalues[6]);
		return returnvalues;
	}

	public synchronized ArrayList<RepositoryOnlineStatusCalendarWeek> getWeekList() {
		
		this.weekList.clear();
		
		// sort timestamps
		long time1 = System.currentTimeMillis();
		HashMap<String, Boolean> statusMap = repoStatus.getOnlineStatusForRepository(this.repositoryID);
		System.out.println("Time to get the statusMap: "+(System.currentTimeMillis() - time1)+"ms");
		logger.info("statusMap size = "+statusMap.size());
		Set<String> timestamps = statusMap.keySet();
		ArrayList<String> keks = new ArrayList<String>(timestamps);
		Collections.sort(keks);
		
		// split them up into weeks
		Iterator<String> i = keks.iterator();
		RepositoryOnlineStatusCalendarWeek week = new RepositoryOnlineStatusCalendarWeek();
		while (i.hasNext()) {
			String timestamp = i.next();
			if (!week.isEmpty() && !week.isSameWeek(timestamp)) {
//				logger.info("timestamp = "+timestamp);
//				logger.info("New Timestamp is for new Week - filling up current week and creating a new week");
				week.fillUp();
				this.weekList.add(week);
				week = new RepositoryOnlineStatusCalendarWeek();
			}
			week.addDay(timestamp, statusMap.get(timestamp));
			
		}
		if (!week.isEmpty()) {
			week.fillUp();
			this.weekList.add(week);
		}
//		logger.info("Weeklist Size = "+this.weekList.size());
//		logger.info("checking day count for all weeks");
//		for (int j = 0; j < this.weekList.size(); j++) {
//			logger.info("current Week has "+this.weekList.get(j).getDays().size()+" days");
//			for (int k = 0; k < this.weekList.get(j).getDays().size(); k++) {
//				logger.info("	current day is "+this.weekList.get(j).getDays().get(k).getDate()+" and it has "+this.weekList.get(j).getDays().get(k).getOnlineStatus().size()+" measurement values");
//			}
//		}
		return this.weekList;
	}

	public RepositoryOnlineStatusBean getRepoStatus() {
		return repoStatus;
	}
	public void setRepoStatus(RepositoryOnlineStatusBean repoStatus) {
		this.repoStatus = repoStatus;
	}
	public void setWeekList(ArrayList<RepositoryOnlineStatusCalendarWeek> weekList) {
		this.weekList = weekList;
	}
	public Long getRepositoryID() {
		return repositoryID;
	}
	public void setRepositoryID(Long repositoryID) {
		this.repositoryID = repositoryID;
	}
}
