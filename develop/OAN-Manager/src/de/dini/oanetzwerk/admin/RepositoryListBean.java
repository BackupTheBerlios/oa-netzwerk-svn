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
@SessionScoped
public class RepositoryListBean extends AbstractBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(RepositoryListBean.class);

	FacesContext ctx = FacesContext.getCurrentInstance();
	HttpSession session = (HttpSession) ctx.getExternalContext().getSession(false);

	@ManagedProperty(value = "#{restConnector}")
	private RestConnector restConnector;
	
	private List<Repository> repoList;

	public RepositoryListBean() {

		super();
	}

	@PostConstruct
	public void init() {
		
		RepositoryBean.setRestConnector(restConnector);
		
		if (repoList != null && !repoList.isEmpty()) {
			return;
		}
		
		String result = restConnector.prepareRestTransmission("Repository/").GetData();
		repoList = new ArrayList<Repository>();
		RestMessage rms = RestXmlCodec.decodeRestMessage(result);

		if (rms == null || rms.getListEntrySets().isEmpty()) {

			logger.error("received no Repository Details at all from the server");
			return;
		}

		for (RestEntrySet res : rms.getListEntrySets()) {

			Iterator<String> it = res.getKeyIterator();
			String key = "";
			Repository repo = new Repository();
			String repoURL = null;
			while (it.hasNext()) {

				key = it.next();

				if (key.equalsIgnoreCase("name")) {

					repo.setName(res.getValue(key));

				} else if (key.equalsIgnoreCase("url")) {

					repo.setUrl(res.getValue(key));

				} else if (key.equalsIgnoreCase("repository_id")) {

					repo.setId(new Long(res.getValue(key)));

				} else if (key.equalsIgnoreCase("active")) {

					repo.setActive(Boolean.parseBoolean(res.getValue(key)));

				} else if (key.equalsIgnoreCase("last_full_harvest_begin")) {

					repo.setLastFullHarvestBegin(res.getValue(key));

				} else if (key.equalsIgnoreCase("oai_url")) {
					repoURL = res.getValue(key);
				}
				else
					// System.out.println("Key: " + key);
					continue;
			}

			// check the online status of the repository
			HttpClient htc = new HttpClient();
			htc.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
			
			String parameter = "?verb=identify";
			
			if (!parameter.isEmpty()) {
				// testen ob die Url auch mit einem / endet.
				// damit wird abgefangen wenn User die Schnittstellenadresse ohne / eingeben
				if (!repoURL.endsWith("/")) {
					repoURL = repoURL.concat("/");
				}
				repoURL = repoURL.concat(parameter);
			}
			HttpMethod method = new GetMethod(repoURL);
	        method.setFollowRedirects(true);
	        String response = null;
	        try {
				htc.executeMethod(method);
				if (method.getStatusCode() == 200) {
					repo.setStatus("online");
				}
				else {
					repo.setStatus("offline");
				}
				
	        }
	        catch (Exception e) {
	        	e.printStackTrace();
	        	repo.setStatus("offline");
	        }
	        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss - dd. MM. yyyy");
			String timestamp = dateFormat.format(new Date().getTime());
			repo.setLastStatusCheck(timestamp);
			
			repoList.add(repo);
		}

	}

	public List<Repository> getRepositories() {
		return repoList;
	}
	

	public void setRestConnector(RestConnector restConnector) {
		this.restConnector = restConnector;
	}

}
