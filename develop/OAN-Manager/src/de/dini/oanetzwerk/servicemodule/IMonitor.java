package de.dini.oanetzwerk.servicemodule;

import java.rmi.Remote;
import java.util.Map;

public interface IMonitor extends Remote {

	
	void publishServiceUpdates(Map<String, String> updates);

}
