package de.dini.oanetzwerk.userfrontend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.servicemodule.RestClient;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.RepositoryConfig;

public class RepositoriesBean implements Serializable {
	
	private static Logger logger = Logger.getLogger (RepositoriesBean.class);
	private Properties props = null;

	private Date fromDate;
	private int repoCount;
	private List<RepositoryConfig> listRepositoryConfig = null;
	
    public RepositoriesBean() throws InvalidPropertiesFormatException, FileNotFoundException, IOException  {
    	    	
		this.props = HelperMethods.loadPropertiesFromFileWithinWebcontainer ("webapps/findnbrowse/WEB-INF/userfrontend_gui.xml");
		listRepositoryConfig = new ArrayList<RepositoryConfig>();		
		
		initListRepositoryConfig();
		fromDate = (new GregorianCalendar()).getTime();
		repoCount = listRepositoryConfig.size();
    }
    
    ////////////// AUTO GENERATED ////////////////////
    
	public List<RepositoryConfig> getListRepositoryConfig() {
		return listRepositoryConfig;
	}

	public void setListRepositoryConfig(List<RepositoryConfig> listRepositoryConfig) {
		this.listRepositoryConfig = listRepositoryConfig;
	}
	
	public int getRepoCount() {
		return repoCount;
	}

	public void setRepoCount(int repoCount) {
		this.repoCount = repoCount;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	
	//////////////// UTIL methods ///////////////////////		
	

	private RestClient prepareRestTransmission (String resource) {
		
		return RestClient.createRestClient (new File (System.getProperty ("catalina.base") + this.props.getProperty ("restclientpropfile")), resource, this.props.getProperty ("username"), this.props.getProperty ("password"));
	}
	
	private void initListRepositoryConfig() {
		
		RestMessage rms = this.prepareRestTransmission ("Repository/").sendGetRestMessage();	
		if (rms == null || rms.getListEntrySets().isEmpty() || rms.getStatus() != RestStatusEnum.OK) {			
			logger.warn ("received no repository data : " + rms.getStatus() + " : " + rms.getStatusDescription());
			return;
		}

		for (RestEntrySet res : rms.getListEntrySets ( )) {
			Iterator <String> it = res.getKeyIterator ( );
			String key = "";

			while (it.hasNext ( )) {
				RepositoryConfig repoConfig = new RepositoryConfig();
				key = it.next ( );
				if (key.equalsIgnoreCase ("repository_id")) {
					repoConfig.setRepositoryID(Integer.parseInt(res.getValue(key)));
				} else continue;
				this.listRepositoryConfig.add(repoConfig);
			}
		}

		// alle IDs auch mit Werten fuellen
		
		for(RepositoryConfig repoConfig : listRepositoryConfig) {
			
			rms = this.prepareRestTransmission ("Repository/" + repoConfig.getRepositoryID()).sendGetRestMessage();	
			if (rms == null || rms.getListEntrySets().isEmpty() || rms.getStatus() != RestStatusEnum.OK) {			
				logger.warn ("received no repository data for id '"+repoConfig.getRepositoryID()+"': " + rms.getStatus() + " : " + rms.getStatusDescription());
				continue;
			}

			for (RestEntrySet res : rms.getListEntrySets ( )) {
				Iterator <String> it = res.getKeyIterator ( );
				String key = "";
				while (it.hasNext ( )) {
					key = it.next ( );
					if (key.equalsIgnoreCase ("name")) {
						repoConfig.setRepositoryName(res.getValue(key));
					} else if (key.equalsIgnoreCase ("oai_url")) {
						repoConfig.setRepositoryOAI_BASEURL(res.getValue(key));
					} else if (key.equalsIgnoreCase ("url")) {
						repoConfig.setRepositoryURL(res.getValue(key));
					} else if (key.equalsIgnoreCase ("harvest_amount")) {
						repoConfig.setRepositoryHARVEST_AMOUNT(res.getValue(key));
					} else if (key.equalsIgnoreCase ("harvest_pause")) {
						repoConfig.setRepositoryHARVEST_PAUSE(res.getValue(key));
					} else if (key.equalsIgnoreCase ("test_data")) {
						if("true".equalsIgnoreCase(res.getValue(key))) repoConfig.setRepositoryTEST_DATA(true);
					} else continue;
				}
			}
			
		}
		
	}
	
	//////////////// Action Methods /////////////////////
	
}
