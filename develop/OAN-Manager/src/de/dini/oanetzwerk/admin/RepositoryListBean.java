/**
 * 
 */
package de.dini.oanetzwerk.admin;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import de.dini.oanetzwerk.admin.utils.AbstractBean;
import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestXmlCodec;

/**
 * @author Sammy David
 * sammy.david@cms.hu-berlin.de
 * 
 */
@ManagedBean(name = "repoList")
@RequestScoped
public class RepositoryListBean extends AbstractBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(RepositoryListBean.class);

	FacesContext ctx = FacesContext.getCurrentInstance();
	HttpSession session = (HttpSession) ctx.getExternalContext().getSession(false);

	@ManagedProperty(value = "#{restConnector}")
	private RestConnector restConnector;
	
	@ManagedProperty(value = "#{repositoryOnlineStatus}")
	private RepositoryOnlineStatusBean repositoryOnlineStatus;
	
	private List<Repository> repoList;

	public RepositoryListBean() {

		super();
	}

	@PostConstruct
	public void init() {
		
		RepositoryBean.setRestConnector(restConnector);
		
		repoList = restConnector.fetchRepositories();
		
		System.out.println("size: " + repoList.size());
		String updateTimestamp = repositoryOnlineStatus.getLastUpdate();
		logger.info(updateTimestamp);
		
		for (Repository repo : repoList) {
			
			if (repo.getId() == null) {
				System.out.println("Repo.getId = null");
			}
			else {
				System.out.println("Setting online Status of Repo No "+repo.getId());
			}
			if (!repositoryOnlineStatus.getOnlineStatus().containsKey(repo.getId())) {
				repo.setStatus("unchecked");
			}
			repo.setLastStatusCheck(updateTimestamp);
			if (repositoryOnlineStatus.getOnlineStatus().size() == 0) {
				repo.setStatus("wird geprüft...");	
			} else {
				
				if (repositoryOnlineStatus.getOnlineStatus().get(repo.getId()).containsKey(updateTimestamp)) {
					repo.setStatus(
						repositoryOnlineStatus.
						getOnlineStatus().
						get(
							repo.
							getId()
						).
						get(
							updateTimestamp
						) == true ? "online" : "offline"
					);
				}
				else {
					repo.setStatus("unknown");
				}
			}
		}
		
	}

	public List<Repository> getRepositories() {
		return repoList;
	}
	

	public void setRestConnector(RestConnector restConnector) {
		this.restConnector = restConnector;
	}
	
	public void setRepositoryOnlineStatus(RepositoryOnlineStatusBean repositoryOnlineStatus) {
		this.repositoryOnlineStatus = repositoryOnlineStatus;
	}

}
