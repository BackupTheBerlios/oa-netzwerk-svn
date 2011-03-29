package de.dini.oanetzwerk.servicemodule;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;


/**
 * This interface is a RMI-contract and is implemented by all services (harvester, aggregator, markerAndEraser)
 */
public interface IService extends Remote {

	
	int getCurrentRepository() throws RemoteException;
	
	int getCurrentStatus() throws RemoteException;
	
	int getUpdateInterval() throws RemoteException;
	
	void setUpdateInterval(int ms) throws RemoteException;
	
	boolean start(Map<String, String> data) throws RemoteException;
	
	boolean stop() throws RemoteException;
	
	
}
