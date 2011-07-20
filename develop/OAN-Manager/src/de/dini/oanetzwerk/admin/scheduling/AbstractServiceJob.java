package de.dini.oanetzwerk.admin.scheduling;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.Trigger;

import de.dini.oanetzwerk.admin.Repository;
import de.dini.oanetzwerk.admin.RestConnector;
import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestXmlCodec;

/**
 * @author Sammy David
 * sammy.david@cms.hu-berlin.de
 * 
 */

public abstract class AbstractServiceJob implements Job {

	private static final Logger LOG = Logger.getLogger(AbstractServiceJob.class);
	
	@ManagedProperty(value = "#{restconnector}")
	private RestConnector connector;

	private Trigger trigger;
	
	private JobDataMap data;
	
	public AbstractServiceJob() {
		super();
	}


	public List<Repository> getRepositories() {

		// if (true)
		// return new ArrayList<RepositoryBean>();

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
					// System.out.println("Key: " + key);
					continue;
			}

			repoList.add(repo);

		}
		System.out.println(repoList.size());

		return repoList;
	}

	/*********************** Getter & Setter ***********************/

	
	public void setConnector(RestConnector connector) {
		System.out.println("conn setter");
		this.connector = connector;
	}

	public RestConnector getConnector() {
		return connector;
	}

	public Trigger getTrigger() {
		return trigger;
	}

	public void setTrigger(Trigger trigger) {
		this.trigger = trigger;
	}

	public JobDataMap getData() {
    	return data;
    }

	public void setData(JobDataMap data) {
    	this.data = data;
    }

}
