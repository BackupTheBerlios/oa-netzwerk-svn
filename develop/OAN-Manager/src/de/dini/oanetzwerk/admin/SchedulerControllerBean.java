//package de.dini.oanetzwerk.admin;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.faces.bean.ManagedBean;
//import javax.faces.context.FacesContext;
//
//import org.apache.log4j.Logger;
//
///**
// * @author Sammy David
// * sammy.david@cms.hu-berlin.de
// * 
// */
//@ManagedBean(name="scheduler")
//public class SchedulerControllerBean {
//
//	private static final long serialVersionUID = 1L;
//	private static Logger logger = Logger.getLogger(SchedulerControllerBean.class);
//	
//	FacesContext ctx = FacesContext.getCurrentInstance();
//
//	
//	private String selectedMonth;
//	
//	private String selectedIntervalType;
//	
//	
//	public SchedulerControllerBean() {
//	    super();
//    }
//	
//	
//	public List<String> getMonths() {
//	    
//		List months = new ArrayList<String>();
//		months.add("January");
//		months.add("February");
//		months.add("March");
//		months.add("April");
//		months.add("May");
//		months.add("June");
//		return months;
//    }
//
//	public List<String> getIntervalTypes() {
//	    
//		List intervalTypes = new ArrayList<String>();
//		for (IntervalType e : IntervalType.values()) {
//			
//			intervalTypes.add(e.name());
//		}
//		
//		return intervalTypes;
//    }
//	
//	private enum IntervalType {
//		MONTHLY, WEEKLY, DAILY, CUSTOM;
//	}
//
//	public String getSelectedMonth() {
//    	return selectedMonth;
//    }
//
//
//	public void setSelectedMonth(String selectedMonth) {
//    	this.selectedMonth = selectedMonth;
//    }
//
//
//	public String getSelectedIntervalType() {
//    	return selectedIntervalType;
//    }
//
//
//	public void setSelectedIntervalType(String selectedIntervalType) {
//    	this.selectedIntervalType = selectedIntervalType;
//    }
//	
//	
//	
//}
