package de.dini.oanetzwerk.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestXmlCodec;

/**
 * @author Johannes Haubold
 */
@ManagedBean(name = "Statistics")
@RequestScoped
public class StatisticsBean {
	private Long objectCount = null;
	private Long fullTextLinkCount = null;
	private ArrayList<ArrayList<String>> recordsPerDDCCategory = null;
	private ArrayList<ArrayList<String>> recordsPerDINISetCategory = null;
	private ArrayList<ArrayList<String>> recordsPerIso639Language = null;
	private HashMap<Long, Long> recordsPerRepository = null;
	private Long repositoryID = null; 
	
	@ManagedProperty(value = "#{restConnector}")
	private RestConnector restConnector;
	
	FacesContext ctx = FacesContext.getCurrentInstance();
	HttpSession session = (HttpSession) ctx.getExternalContext().getSession(false);
	private HashMap<Long, ArrayList<String>> peculiarAndOutdatedCount;
	private ArrayList<ArrayList<String>> peculiarAndOutdatedObjects;
	
	public StatisticsBean() {
		super();

		
	}
	
	@PostConstruct
	private void init() {
		HttpServletRequest request = (HttpServletRequest) ctx.getExternalContext().getRequest();
		String repositoryID = request.getParameter("repositoryID");
		String objectID = request.getParameter("objectID");
		String action = request.getParameter("action");
		if (repositoryID != null && !repositoryID.isEmpty()) {
			try {
				this.repositoryID = Long.parseLong(repositoryID);
				//System.out.println("Got a repository ID from the request");
			}
			catch (NumberFormatException n) {
				this.repositoryID = null;
				//System.out.println("Provided Parameter is not valid as a repository ID");
			}
			
		}
		else {
			this.repositoryID = null;
			//System.out.println("No repository ID received");
		}
		if (objectID != null && !objectID.isEmpty() && action != null && !action.isEmpty() ) {
			if (action.equals("deleteObject")) {
				deleteObject(objectID);
			}
		}
	}
	
	private boolean deleteObject(String objectID) {
		String result = restConnector.
			prepareRestTransmission("ObjectEntry/"+objectID).
				DeleteData();
		
		return true;
	}
	
	public HashMap<Long, ArrayList<String>> getPeculiarAndOutdatedCount() {
		return getPeculiarAndOutdatedCount(this.repositoryID);
	}
	
	public HashMap<Long, ArrayList<String>> getPeculiarAndOutdatedCount(Long repoID) {
		if (this.peculiarAndOutdatedCount != null) {
			if (this.repositoryID != null) {
				HashMap<Long, ArrayList<String>> retval = new HashMap<Long, ArrayList<String>>();
				retval.put(repoID, this.peculiarAndOutdatedCount.get(repoID));
				return retval;
			}
			else {
				return this.peculiarAndOutdatedCount;
			}
		}
		String result = "";
		if (repoID != null) {
			result = restConnector.prepareRestTransmission("Statistics/getMarkedPublicationsCount/"+repoID.toString()).GetData();
		}
		else {
			result = restConnector.prepareRestTransmission("Statistics/getMarkedPublicationsCount").GetData();
		}
		RestMessage rms = RestXmlCodec.decodeRestMessage(result);
		HashMap<Long, ArrayList<String>> returnvalues = new HashMap<Long, ArrayList<String>>();
		for (RestEntrySet res : rms.getListEntrySets()) {

			Iterator<String> it = res.getKeyIterator();
			String key = "";
			ArrayList<String> data = new ArrayList<String>();
			String peculiar = "";
			String outdated  = "";
			String repositoryID = "";
			String repositoryName = "";
			while (it.hasNext()) {

				key = it.next();

				if (key.equalsIgnoreCase("peculiar")) {
					peculiar = res.getValue(key);
				}
				else if (key.equalsIgnoreCase("outdated")) {
					outdated = res.getValue(key);
				}
				else if (key.equalsIgnoreCase("repositoryID")) {
					repositoryID = res.getValue(key);
				}
				else if (key.equalsIgnoreCase("repositoryName")) {
					repositoryName = res.getValue(key);
				}
				
			}
			data.add(peculiar);
			data.add(outdated);
			data.add(repositoryID);
			data.add(repositoryName);
			returnvalues.put(new Long(repositoryID), data);
		}
		this.peculiarAndOutdatedCount = returnvalues;
		return returnvalues;
	}
	
	
	public ArrayList<ArrayList<String>> getPeculiarAndOutdatedObjects() {
		return getPeculiarAndOutdatedObjects(this.repositoryID);
	}
	
	public ArrayList<ArrayList<String>> getPeculiarAndOutdatedObjects(Long repoID) {
		if (this.peculiarAndOutdatedObjects != null) {
			if (this.repositoryID != null) {
				ArrayList<ArrayList<String>> retval = new ArrayList<ArrayList<String>>();
				for (int i = 0; i < this.peculiarAndOutdatedObjects.size(); i++) {
					if (new Long(this.peculiarAndOutdatedObjects.get(i).get(1)).equals(new Long(repoID))) {
						retval.add(this.peculiarAndOutdatedObjects.get(i));
					}
				}
				return retval;
			}
			else {
				return this.peculiarAndOutdatedObjects;
			}
		}
		String result = "";
		if (repoID != null) {
			result = restConnector.prepareRestTransmission("Statistics/getMarkedPublications/"+repoID.toString()).GetData();
		}
		else {
			result = restConnector.prepareRestTransmission("Statistics/getMarkedPublications").GetData();
		}
		RestMessage rms = RestXmlCodec.decodeRestMessage(result);
		ArrayList<ArrayList<String>> returnvalues = new ArrayList<ArrayList<String>>();
		for (RestEntrySet res : rms.getListEntrySets()) {

			Iterator<String> it = res.getKeyIterator();
			String key = "";
			ArrayList<String> data = new ArrayList<String>();
			String objectID = "";
			String repositoryID = "";
			String peculiar = "";
			String outdated = "";
			String repoName  = "";
			String title = "";
			while (it.hasNext()) {

				key = it.next();

				if (key.equalsIgnoreCase("objectID")) {
					objectID = res.getValue(key);
				}
				else if (key.equalsIgnoreCase("repositoryID")) {
					repositoryID = res.getValue(key);
				}
				else if (key.equalsIgnoreCase("peculiar")) {
					peculiar = res.getValue(key);
				}
				else if (key.equalsIgnoreCase("outdated")) {
					outdated = res.getValue(key);
				}
				else if (key.equalsIgnoreCase("repoName")) {
					repoName = res.getValue(key);
				}
				else if (key.equalsIgnoreCase("title")) {
					title = res.getValue(key);
				}
				
			}
			data.add(objectID);
			data.add(repositoryID);
			data.add(peculiar);
			data.add(outdated);
			data.add(repoName);
			data.add(title);
			returnvalues.add(data);
		}
		this.peculiarAndOutdatedObjects = returnvalues;
		return returnvalues;
	}
	
	public HashMap<Long, Long> getRecordsPerRepository() {
		
		if (this.recordsPerRepository != null && !this.recordsPerRepository.isEmpty()) {
			return this.recordsPerRepository;
		}
		String result = restConnector.prepareRestTransmission("Statistics/RecordsPerRepository").GetData();
		RestMessage rms = RestXmlCodec.decodeRestMessage(result);
		HashMap<Long, Long> returnvalues = new HashMap<Long, Long>();
		Long repository_id = null;
		Long recordCount = null;
		for (RestEntrySet res : rms.getListEntrySets()) {

			Iterator<String> it = res.getKeyIterator();
			String key = "";

			while (it.hasNext()) {

				key = it.next();

				if (key.equalsIgnoreCase("repository_id")) {
					repository_id = new Long(res.getValue(key));
				}
				else if (key.equalsIgnoreCase("recordCount")) {
					recordCount = new Long(res.getValue(key));
				}
			}
			if (repository_id != null && recordCount != null) {
				returnvalues.put(repository_id, recordCount);
			}
		}
		this.recordsPerRepository = returnvalues;
		return returnvalues;
	}
	
	public ArrayList<ArrayList<String>> getRecordsPerDDCCategory() {
		if (this.recordsPerDDCCategory != null && !this.recordsPerDDCCategory.isEmpty()) {
			return this.recordsPerDDCCategory;
		}
		String result = restConnector.prepareRestTransmission("Statistics/RecordsPerDDCCategory").GetData();
		if (result.isEmpty()) {
			System.out.println("Result is empty");
		}
		RestMessage rms = RestXmlCodec.decodeRestMessage(result);
		ArrayList<ArrayList<String>> returnvalues = new ArrayList<ArrayList<String>>();
		for (RestEntrySet res : rms.getListEntrySets()) {

			Iterator<String> it = res.getKeyIterator();
			String key = "";
			ArrayList<String> data = new ArrayList<String>();
			String recordCount = "";
			String categoryID = "";
			String name = "";
			String name_en = "";
			while (it.hasNext()) {

				key = it.next();

				if (key.equalsIgnoreCase("recordCount")) {
					recordCount = res.getValue(key).toString();
				}
				else if (key.equalsIgnoreCase("categoryID")) {
					categoryID = res.getValue(key).toString();
					System.out.println("DDC Cat = "+res.getValue(key));
				}
				else if (key.equalsIgnoreCase("name")) {
					name = res.getValue(key).toString();
				}
				else if (key.equalsIgnoreCase("name_en")) {
					name_en = res.getValue(key).toString();
				}
			}
			data.add(recordCount);
			data.add(categoryID);
			data.add(name);
			data.add(name_en);
			returnvalues.add(data);
		}
		this.recordsPerDDCCategory = returnvalues;
		return returnvalues;
	}
	
	public ArrayList<ArrayList<String>> getRecordsPerDINISetCategory() {
		if (this.recordsPerDINISetCategory != null && !this.recordsPerDINISetCategory.isEmpty()) {
			return this.recordsPerDINISetCategory;
		}
		String result = restConnector.prepareRestTransmission("Statistics/RecordsPerDINISetCategory").GetData();
		if (result.isEmpty()) {
			System.out.println("Result is empty");
		}
		RestMessage rms = RestXmlCodec.decodeRestMessage(result);
		ArrayList<ArrayList<String>> returnvalues = new ArrayList<ArrayList<String>>();
		for (RestEntrySet res : rms.getListEntrySets()) {

			Iterator<String> it = res.getKeyIterator();
			String key = "";
			ArrayList<String> data = new ArrayList<String>();
			String recordCount = "";
			String DINI_Set_ID = "";
			String name = "";
			String SetNameEng = "";
			String SetNameDeu = "";
			while (it.hasNext()) {

				key = it.next();
				if (key.equalsIgnoreCase("recordCount")) {
					recordCount = res.getValue(key).toString();
				}
				else if (key.equalsIgnoreCase("DINI_Set_Id")) {
					DINI_Set_ID = res.getValue(key).toString();
				}
				else if (key.equalsIgnoreCase("name")) {
					name = res.getValue(key).toString();
				}
				else if (key.equalsIgnoreCase("SetNameEng")) {
					SetNameEng = res.getValue(key).toString();
				}
				else if (key.equalsIgnoreCase("SetNameDeu")) {
					SetNameDeu = res.getValue(key).toString();
				}
			}
			data.add(recordCount);
			data.add(DINI_Set_ID);
			data.add(name);
			data.add(SetNameEng);
			data.add(SetNameDeu);
			returnvalues.add(data);
		}
		this.recordsPerDINISetCategory = returnvalues;
		return returnvalues;
	}
	
	public ArrayList<ArrayList<String>> getRecordsPerIso639Language() {
		if (this.recordsPerIso639Language != null && !this.recordsPerIso639Language.isEmpty()) {
			return this.recordsPerIso639Language;
		}
		String result = restConnector.prepareRestTransmission("Statistics/RecordsPerIso639Language").GetData();
		if (result.isEmpty()) {
			System.out.println("Result is empty");
		}
		RestMessage rms = RestXmlCodec.decodeRestMessage(result);
		ArrayList<ArrayList<String>> returnvalues = new ArrayList<ArrayList<String>>();
		for (RestEntrySet res : rms.getListEntrySets()) {

			Iterator<String> it = res.getKeyIterator();
			String key = "";
			ArrayList<String> data = new ArrayList<String>();
			String recordCount = "";
			String languageID = "";
			String language = "";
			while (it.hasNext()) {

				key = it.next();

				if (key.equalsIgnoreCase("recordCount")) {
					recordCount = res.getValue(key).toString();
				}
				else if (key.equalsIgnoreCase("languageID")) {
					languageID = res.getValue(key).toString();
				}
				else if (key.equalsIgnoreCase("language")) {
					language = res.getValue(key).toString();
				}
			}
			data.add(recordCount);
			data.add(languageID);
			data.add(language);
			returnvalues.add(data);
		}
		this.recordsPerIso639Language = returnvalues;
		return returnvalues;
	}
	
	public Long getFullTextLinkCount() {
		if (this.fullTextLinkCount != null) {
			return this.fullTextLinkCount;
		}
		String result = restConnector.prepareRestTransmission("Statistics/FullTextLinkCount").GetData();
		if (result.isEmpty()) {
			//System.out.println("Result is empty");
		}
		RestMessage rms = RestXmlCodec.decodeRestMessage(result);
		for (RestEntrySet res : rms.getListEntrySets()) {

			Iterator<String> it = res.getKeyIterator();
			String key = "";
			while (it.hasNext()) {

				key = it.next();

				if (key.equalsIgnoreCase("FullTextLinkCount")) {
					this.fullTextLinkCount = new Long(res.getValue(key).toString());
				}
			}
		}
		return this.fullTextLinkCount;
	}
	
	public Long getObjectCount() {
		if (this.objectCount != null) {
			return this.objectCount;
		}
		String result = restConnector.prepareRestTransmission("Statistics/ObjectCount").GetData();
		if (result.isEmpty()) {
			//System.out.println("Result is empty");
		}
		RestMessage rms = RestXmlCodec.decodeRestMessage(result);
		for (RestEntrySet res : rms.getListEntrySets()) {

			Iterator<String> it = res.getKeyIterator();
			String key = "";
			while (it.hasNext()) {

				key = it.next();

				if (key.equalsIgnoreCase("ObjectCount")) {
					this.objectCount = new Long(res.getValue(key).toString());
				}
			}
		}
		return this.objectCount;
	}

	public RestConnector getRestConnector() {
		return restConnector;
	}

	public void setRestConnector(RestConnector restConnector) {
		this.restConnector = restConnector;
	}
	
	
	
}
