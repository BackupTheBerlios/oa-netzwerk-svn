package de.dini.oanetzwerk.admin;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.admin.utils.AbstractBean;

/**
 * @author Michael K&uuml;hn
 * 
 */

@ManagedBean(name = "log")
public class LiveLogBean extends AbstractBean implements Serializable {

	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(UserBean.class);
	private static final SimpleDateFormat DATE_GER = new SimpleDateFormat("dd.MM.yyyy");
	
	FacesContext ctx = FacesContext.getCurrentInstance();
	HttpSession session = (HttpSession) ctx.getExternalContext().getSession(
			false);


	private Long id = null;
	private String update = "Currently no service in progress...";
	
	
	public String getUpdate() {
		return update;
	}
	public void setUpdate(String update) {
		this.update = update;
	}

	

}
