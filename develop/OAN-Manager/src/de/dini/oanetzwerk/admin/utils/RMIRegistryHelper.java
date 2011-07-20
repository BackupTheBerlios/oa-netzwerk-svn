package de.dini.oanetzwerk.admin.utils;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.apache.log4j.Logger;

/**
 * @author Sammy David
 * sammy.david@cms.hu-berlin.de
 * 
 */
public class RMIRegistryHelper {

	private static final Logger logger = Logger.getLogger(RMIRegistryHelper.class);
	
	
	public static Registry getRegistry(String host) {
		
		try {
	        
			// try to obtain an already started registry
			if (host == null || host.length() == 0) {
				logger.info("Trying to connect to rmi-registry on host 'localhost'!");				
				return LocateRegistry.getRegistry();
			}
			
			logger.info("Trying to connect to rmi-registry on host '"+ host +"'!");
			return LocateRegistry.getRegistry(host);
			
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
	
	public static Registry getRegistry() {
		
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
	
}
