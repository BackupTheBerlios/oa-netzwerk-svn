package de.dini.oanetzwerk.admin.utils;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.apache.log4j.Logger;

public class RMIRegistryHelper {

	private static final Logger logger = Logger.getLogger(RMIRegistryHelper.class);
	
	
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
