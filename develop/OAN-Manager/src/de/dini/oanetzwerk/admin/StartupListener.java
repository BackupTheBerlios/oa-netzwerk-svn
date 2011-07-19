package de.dini.oanetzwerk.admin;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.servicemodule.IHarvesterMonitor;

/**
 * @author Sammy David
 * sammy.david@cms.hu-berlin.de
 * 
 */
public class StartupListener implements ServletContextListener, IHarvesterMonitor {

	private static final Logger logger = Logger.getLogger(StartupListener.class);
	
	private static final String SERVICE_NAME = "HarvesterMonitorService";
	
	private Registry registry = null;
	
	
	@Override
    public void contextInitialized(ServletContextEvent arg0) {

		logger.info("Initializing rmi-monitoring service ...");
		// do stuff right on the webapp startup and not just on a page visit
		// TODO: start the RMI-monitor service 
		
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

//		startMonitorService();
    }
	

	private void startMonitorService() {

		try {
			// FileWriter writer = new FileWriter(new
			// File("/home/davidsam/Desktop/1234567.txt"));
			// writer.write("blabla");
			// writer.flush();
			// writer.close();

			
			IHarvesterMonitor server = this;
			IHarvesterMonitor stub = (IHarvesterMonitor) UnicastRemoteObject.exportObject(server, 0);
			registry = getRegistry();

			if (registry == null) {
				logger.error("Could not obtain an existing RMI-Registry nor create one ourselves! Aborting to start RMI-Harvester!");
				return;
			}

			registry.rebind(SERVICE_NAME, stub);
			logger.info(SERVICE_NAME + " bound");
		} catch (Exception e) {
			logger.warn(SERVICE_NAME + " could not be bound: ");
			e.printStackTrace();
		}

	}

	private static Registry getRegistry() {

		try {

			// try to obtain an already started registry
			return LocateRegistry.getRegistry();

		} catch (RemoteException e) {

			logger.warn("Could not retrieve RMI-Registry! Starting a registry ...");

			// create registry if we failed to obtain an existing one
			try {

				return LocateRegistry.createRegistry(1099);

			} catch (RemoteException e2) {
				logger.error("Failed to create own RMI-Registry!");
				e2.printStackTrace();
			}
		}
		return null;
	}

	@Override
    public void contextDestroyed(ServletContextEvent arg0) {
	    
		logger.info("Stopping rmi-monitoring service ...");
//	    stopMonitorService();
    }
	
	public boolean stopMonitorService() {
		logger.info("Unbinding " + SERVICE_NAME + " !");
		
		try {

			if (registry == null)
			{
				logger.info("Creating rmi-registry as there has no running instance been detected.");
				registry = getRegistry();
			}
			registry.unbind(SERVICE_NAME);
		} catch (NotBoundException e) {
			logger.info(SERVICE_NAME + " already unbound.");
		} catch (AccessException e) {
			logger.info("Could not unbind " + SERVICE_NAME + ", No access rights to do so.", e);
		} catch (RemoteException e) {
			logger.info("Could not unbind " + SERVICE_NAME + ", RMI-registry not available.", e);
		}
		return true;
	}


	@Override
    public void publishServiceUpdates(Map<String, String> updates) {
	    System.out.println("Update received, progress: " + updates.get("progress"));
	    
    }

	
}
