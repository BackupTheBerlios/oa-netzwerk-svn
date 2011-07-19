package de.dini.oanetzwerk.admin;

import java.io.Serializable;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.admin.utils.AbstractBean;
import de.dini.oanetzwerk.admin.utils.RMIRegistryHelper;
import de.dini.oanetzwerk.servicemodule.IHarvesterMonitor;

/**
 * @author Sammy David
 * sammy.david@cms.hu-berlin.de
 * 
 */

@ManagedBean(name = "log")
public class LiveLogBean extends AbstractBean implements Serializable, IHarvesterMonitor {

	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(UserBean.class);
	private static final SimpleDateFormat DATE_GER = new SimpleDateFormat("dd.MM.yyyy");
	
	FacesContext ctx = FacesContext.getCurrentInstance();
	HttpSession session = (HttpSession) ctx.getExternalContext().getSession(
			false);

	
	private Long id = null;
	private String update = "";
	private String updateMessage = "Currently no service in progress...";

	
	public LiveLogBean() {
		super();
		
		String name = "HarvesterMonitorService";

		
		try {
//			FileWriter writer = new FileWriter(new File("/home/davidsam/Desktop/1234567.txt"));
//			writer.write("blabla");
//			writer.flush();
//			writer.close();
			
			IHarvesterMonitor stub = (IHarvesterMonitor) UnicastRemoteObject.exportObject(this, 0);
			Registry registry = RMIRegistryHelper.getRegistry();
			
			if (registry == null) {
				logger.error("Could not obtain an existing RMI-Registry nor create one ourselves! Aborting to bind to obtain Harvester-Monitoring!");
				return;
			}
			
			registry.rebind(name, stub);
			System.out.println(name + " bound");
		} catch (Exception e) {
			System.err.println(name + " could not be bound: ");
			e.printStackTrace();
		}
	}
	
	
	public String getUpdate() {
		return update.length() == 0 ? updateMessage : update;
	}
	public void setUpdate(String update) {
		this.update = update;
	}
	
	
	
	@Override
    public void publishServiceUpdates(Map<String, String> updates) {
	    
		update += updates.get("messages");
	    
    }

	

}
