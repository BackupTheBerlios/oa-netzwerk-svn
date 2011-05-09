package de.dini.oanetzwerk.admin.scheduling;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.bean.ManagedProperty;

import org.apache.log4j.Logger;
import org.quartz.Job;

import de.dini.oanetzwerk.admin.Repository;
import de.dini.oanetzwerk.admin.RestConnector;
import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestXmlCodec;

public abstract class AbstractServiceJob implements Job {

	private static final Logger LOG = Logger.getLogger(AbstractServiceJob.class);
	
	@ManagedProperty(value="#{restconnector}")
	private RestConnector connector;
	
	private String serviceName;
	private ProcessingType processingType;
	private int repositoryId;
	private int interval;
	private String additionalInfo;	
	
	
	
	public AbstractServiceJob() {
	    super();
    }
		
	

//	@Override
//    public void execute(JobExecutionContext arg0) throws JobExecutionException {
//	    // to be overridden by subclasses
//	    
//    }
	
	
	
	public enum ProcessingType {
		
		SINGLE,  // Only a single repository should be processed by this job (repositoryId)
		MULTI;	 // all repositories should be processed by this job
	}

	
	public List<Repository> getRepositories() {

//		if (true)
//			return new ArrayList<RepositoryBean>();
		
		String result = connector.prepareRestTransmission("Repository/").GetData();
		List<Repository> repoList = new ArrayList<Repository>();
		RestMessage rms = RestXmlCodec.decodeRestMessage(result);

		if (rms == null || rms.getListEntrySets().isEmpty()) {

			LOG.error("received no Repository Details at all from the server");
			return null;
		}

		for (RestEntrySet res : rms.getListEntrySets()) {

			Iterator<String> it = res.getKeyIterator();
			String key = "";
			Repository repo = new Repository();

			while (it.hasNext()) {

				key = it.next();

				// if (logger.isDebugEnabled ( ))
				// logger.debug ("key: " + key + " value: " + res.getValue
				// (key));

				if (key.equalsIgnoreCase("name")) {

					repo.setName(res.getValue(key));

				} else if (key.equalsIgnoreCase("url")) {

					repo.setUrl(res.getValue(key));

				} else if (key.equalsIgnoreCase("repository_id")) {

					repo.setId(new Long(res.getValue(key)));

				} else
//					System.out.println("Key: " + key);
					continue;
			}

			repoList.add(repo);
			
		}
		System.out.println(repoList.size());
		
		return repoList;
	}
	
	

	
	
	/*********************** Getter & Setter ***********************/
	
	public String getServiceName() {
    	return serviceName;
    }


	public void setServiceName(String serviceName) {
    	this.serviceName = serviceName;
    }


	public ProcessingType getProcessingType() {
    	return processingType;
    }


	public void setProcessingType(ProcessingType processingType) {
    	this.processingType = processingType;
    }


	public int getRepositoryId() {
    	return repositoryId;
    }


	public void setRepositoryId(int repositoryId) {
    	this.repositoryId = repositoryId;
    }


	public int getInterval() {
    	return interval;
    }


	public void setInterval(int interval) {
    	this.interval = interval;
    }


	public String getAdditionalInfo() {
    	return additionalInfo;
    }


	public void setAdditionalInfo(String additionalInfo) {
    	this.additionalInfo = additionalInfo;
    }
	
	public void setConnector(RestConnector connector) {
    	this.connector = connector;
    }
}
