package de.dini.oanetzwerk.admin;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class InitializationServlet implements Servlet {

	@Override
    public void destroy() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public ServletConfig getServletConfig() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public String getServletInfo() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public void init(ServletConfig config) throws ServletException {

			ServletContext context = config.getServletContext();
			SchedulerControl scheduler = new SchedulerControl();
//			myBean.setTest("test");
			context.setAttribute("schedulerControl", scheduler);
//			super.init(config);
    }

	@Override
    public void service(ServletRequest arg0, ServletResponse arg1) throws ServletException, IOException {
	    // TODO Auto-generated method stub
	    
    }

}
