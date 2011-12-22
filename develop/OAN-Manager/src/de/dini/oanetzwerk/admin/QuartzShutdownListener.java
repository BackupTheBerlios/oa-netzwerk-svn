package de.dini.oanetzwerk.admin;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class QuartzShutdownListener implements ServletContextListener {
	
	ServletContext context;
	public void contextInitialized(ServletContextEvent contextEvent) {
		System.out.println("Context Created");
	
	}
	public void contextDestroyed(ServletContextEvent contextEvent) {
		System.out.println("Context Destroyed");
		context = contextEvent.getServletContext();
		// Get current FacesContext.
        FacesContext facesContext = FacesContext.getCurrentInstance();
        
        if (facesContext  == null) {
        	System.out.println("FCONTEXT IS NULL");
        } else {
        	System.out.println("FCONTEXT IS NOT NULL");
        	
        	SchedulerControl bean = (SchedulerControl) facesContext.getApplication().evaluateExpressionGet(facesContext, "#{schedulerControl}", SchedulerControl.class);
        	if (bean == null) {
               	System.out.println("FBEAN IS NULL");
            } else {
            	System.out.println("FBEAN IS NOT NULL");
        	}
        }
	}


}
