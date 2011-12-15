package de.dini.oanetzwerk.admin;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import de.dini.oanetzwerk.utils.PropertyManager;

@ManagedBean(name = "repositoryOnlineStatus")
@ApplicationScoped
public class RepositoryOnlineStatusBean {

	/* offene Fragen
	   - was passiert wenn ich ein neues Repository hinzufüge? (dann hat die Liste das ja noch nicht drin)
	   - was passiert mit gelöschten Repositories (Ignorieren bis zum nächsten Erstellen der Liste)
	   - Datenstruktur:
	   		array(
	   			Repository-ID,
	   			array(
	   				timestamp, 
	   				onlineStatus
	   			)
	   		)
	*/
	private static Logger logger = Logger.getLogger(RepositoryOnlineStatusBean.class);

	private String lastUpdate = "";
	private HashMap<Long, HashMap<String, Boolean>> statusList; 
	private boolean deleteMap = false;
	private String onlineStatusPath = "";
	
	@ManagedProperty(value = "#{restConnector}")
	private RestConnector restConnector;
	
	@ManagedProperty(value = "#{propertyManager}")
	private PropertyManager propertyManager;
	
	private List<Repository> repoList;
	private int saveIntervalInDays = 30; // save up to saveIntervalInDays days of onlineStatus information
	
	

	@PostConstruct
	public void init() {
		
		this.onlineStatusPath = propertyManager.getWebApplicationRootDirectory() + "/WEB-INF/";
	
		//TODO: remove
//		// trigger onlinestatus-check on startup of webapp (managedbean, eager=true)
//		checkOnlineStatus();
	}


	public HashMap<Long, HashMap<String, Boolean>> getOnlineStatus() {
		deserialize();
		if (statusList == null) {	
			checkOnlineStatus();
		}
		return statusList;
	}
	
	public void refreshOnlineStatus() {

		checkOnlineStatus();
	}
	
	public String getLastUpdate() {
		deserialize();
		if (lastUpdate.equals("") || statusList == null) {
			getOnlineStatus();
		}
		return lastUpdate;
	}
	
	private boolean deserialize() {
		if (this.statusList == null) {
			try {
				logger.info("Trying to read repository online status list from serialized file");
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(this.onlineStatusPath + "onlinestatus.oan"));
				
				this.statusList = (HashMap<Long, HashMap<String, Boolean>>) ois.readObject();
				
				ois.close();
				
			} catch (FileNotFoundException e) {
				logger.info("no serialized file found");
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		
		// TODO setze lastUpdate !!
		setLastUpdate();
		return true;
	}
	
	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	/**
	 * Sets the lastUpdate field from the data in statusList
	 */
	private void setLastUpdate() {
		Set<Long> repoIDs = this.statusList.keySet();
		Iterator<Long> repoIDIterator = repoIDs.iterator();
		Set<String> timestamps = new HashSet<String>();
		while (repoIDIterator.hasNext()) {
			long repoID = repoIDIterator.next();
			timestamps.addAll(this.getOnlineStatusForRepository(repoID).keySet());
		}
		ArrayList<String> keks = new ArrayList<String>(timestamps);
		Collections.sort(keks);
		if (keks.isEmpty()) {
			this.lastUpdate = "";
			return;
		}
		this.lastUpdate = keks.get(keks.size() - 1);
	}
	
	
	/*
	 * Step 0: copy current list to tempList
	 * Step 1: clear old entries in tempList (depending on saveInterval)
	 * Step 2: add new entry to the list of a repository ID or create new ID+List if ID not already in list
	 * Step 3: overwrite current list with updated tempList
	 */
	
	private void checkOnlineStatus() {
		
		
		String repoURL = null;
		HashMap<Long, HashMap<String, Boolean>> tempMap;
		
		Calendar currentTimestamp;
		Calendar oldTimestamp;
		currentTimestamp = Calendar.getInstance();
		oldTimestamp = Calendar.getInstance();
		oldTimestamp.add(Calendar.DAY_OF_YEAR, - this.saveIntervalInDays);

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String newUpdate = dateFormat.format(currentTimestamp.getTime());
	
//		logger.debug("StatusListe vor dem Update");
//		debugStatusList();
		
		long time1 = System.currentTimeMillis();
		tempMap = deleteEntriesOlderThan(dateFormat.format(oldTimestamp.getTime()));
		long time2 = System.currentTimeMillis();
		System.out.println("deleteEntriesOlderThan took "+(time2 - time1)+" ms");
		if (tempMap == null) {
			tempMap = new HashMap<Long, HashMap<String, Boolean>>();
		}
		
		repoList = restConnector.fetchRepositories();
		if (repoList == null || repoList.isEmpty()) {
			logger.warn("Repoliste leer, abbrechen");
			return;
		}
		
		
		
		if (this.statusList == null) {
			logger.info("Status List is null - no serialized data available");
			this.statusList = new HashMap<Long, HashMap<String, Boolean>>();
		}

		for (Repository repo : repoList) {
			
			
			// check the online status of the repository
			HttpClient htc = new HttpClient();
			htc.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
			
			String parameter = "?verb=Identify";
			
			if (!parameter.isEmpty()) {
				// testen ob die Url auch mit einem / endet.
				// damit wird abgefangen wenn User die Schnittstellenadresse ohne / eingeben
				if (!repo.getOaiUrl().endsWith("/")) {
					repoURL = repo.getOaiUrl().concat("/");
				}
				repoURL = repo.getOaiUrl().concat(parameter);
			}
			HttpMethod method = new GetMethod(repoURL);
	        method.setFollowRedirects(true);
	        String response = null;
	        
	     // check if repository already exists in the status list --> if not, create a new entry for it
			HashMap<String, Boolean> currentMap = (tempMap.containsKey(repo.getId()) ? tempMap.get(repo.getId()) : new HashMap<String, Boolean>());
			try {
				htc.executeMethod(method);
				if (method.getStatusCode() == 200) {
					logger.info("Repo "+repo.getId()+" is online");
					currentMap.put(newUpdate, new Boolean(true));
					tempMap.put(
						repo.getId(), 
						currentMap
					);
				}
				else {
					logger.info("Repo "+repo.getId()+" is offline");
					currentMap.put(newUpdate, new Boolean(false));
					tempMap.put(
						repo.getId(), 
						currentMap
					);
				}
				
	        }
	        catch (Exception e) {
	        	e.printStackTrace();
	        	logger.info("Repo "+repo.getId()+" is unknown");
				currentMap.put(newUpdate, new Boolean(false));
				tempMap.put(
					repo.getId(), 
					currentMap
				);
	        }
		}
		logger.info("synchronizing repository online status list after update");
		synchronized (this) {
			this.statusList = tempMap;
			this.lastUpdate = newUpdate;
			
			
		}
//		logger.info("synchronizing finished");
//		logger.debug("Statusliste nach dem Update");
//		debugStatusList();
//		this.deleteMap = false;
		
		serialize();
	}
	
	private boolean serialize() {
		try {
			logger.info("serializing repository online status list to file");
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.onlineStatusPath + "onlinestatus.oan"));
			
			oos.writeObject(this.statusList);
			
			oos.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private HashMap<Long, HashMap<String, Boolean>> deleteEntriesOlderThan(String timestamp) {
		if (this.statusList == null) {
			return null;
		}
		HashMap<Long, HashMap<String, Boolean>> tempMap = this.statusList;
		Set<Long> keys = new HashSet<Long>(this.statusList.keySet());
		Iterator<Long> i = keys.iterator();
		
		while(i.hasNext()) {
			Long k = i.next();
			HashMap<String, Boolean> onlineStatusForRepository = this.statusList.get(k);
			logger.debug("Repo "+k+": Size vor Bearbeitung = "+onlineStatusForRepository.size());

			Set<String> timestampsForRepository = new HashSet<String>(onlineStatusForRepository.keySet());
			Iterator<String> timestampIterator = timestampsForRepository.iterator();
			while (timestampIterator.hasNext()) {
				String currentTimestamp = timestampIterator.next();
				if (currentTimestamp.compareTo(timestamp) < 0) { // current timestamp is older than the provided timestamp, i.e. entry can be deleted
					
					logger.debug("Repo "+k+": currentTimestamp = "+currentTimestamp+", oldTimestamp = "+timestamp);
					onlineStatusForRepository.remove(currentTimestamp);
				}
			}
			logger.debug("Repo "+k+": Size vor Bearbeitung = "+onlineStatusForRepository.size());
			// update tempMap
			// check if list is now empty --> if it is, delete the repository entry in the tempMap
			if (onlineStatusForRepository.size() == 0) {
				tempMap.remove(k);
			}
			else {
				tempMap.remove(k);
				tempMap.put(k, onlineStatusForRepository);
			}
		}
		
		return tempMap;
	}
	
	private void debugStatusList() {
		if (this.statusList == null || this.statusList.isEmpty()) {
			logger.info("debugStatusList - Liste ist leer");
			return;
		}
		Set<Long> listKeys = this.statusList.keySet();
		Iterator<Long> listKeyIterator = listKeys.iterator();
		
		while(listKeyIterator.hasNext()) {
			Long currentKey = listKeyIterator.next();
			logger.info("debugStatusList - Repository ID : "+currentKey);
			
			HashMap<String, Boolean> onlineStatusForRepository = this.statusList.get(currentKey);
			Set<String> timestampsForRepository = onlineStatusForRepository.keySet();
			Iterator<String> timestampIterator = timestampsForRepository.iterator();
			while (timestampIterator.hasNext()) {
				String currentTimestamp = timestampIterator.next();
				logger.info("debugStatusList - "+currentTimestamp+" = "+(onlineStatusForRepository.get(currentTimestamp) ? "online" : "offline"));
			}
		}
	}
	
	public HashMap<String, Boolean> getOnlineStatusForRepository(Long repositoryID) {
		logger.info("getOnlineStatusForRepository("+repositoryID+")");
		if ( this.statusList == null) {
			logger.info("statusList null --> calling getOnlineStatus()");
			getOnlineStatus();
			return this.statusList.get(repositoryID);
		}
		else if (this.statusList != null && this.statusList.size() == 0) {
			logger.info("statusList not null but empty");
			return new HashMap<String, Boolean>();
		}
		else if (this.statusList != null && !this.statusList.containsKey(repositoryID)) {
			logger.info("statusList not null and not empty but does not contain Repository ID "+repositoryID+" as key");
			return new HashMap<String, Boolean>();
		}
		else {
			logger.info("returning statusList for Repository "+repositoryID);
			return this.statusList.get(repositoryID);
		}
			
	}
	
	public RestConnector getRestConnector() {
		return restConnector;
	}
	
	public void setRestConnector(RestConnector connector) {
		restConnector = connector;
	}

	public PropertyManager getPropertyManager() {
		return propertyManager;
	}

	public void setPropertyManager(PropertyManager propertyManager) {
		this.propertyManager = propertyManager;
	}
	
//	public void deleteOnlineStatus() {
//		if (this.onlineStatus != null) {
//			this.deleteMap = true;
//		}
//	}
	
}
