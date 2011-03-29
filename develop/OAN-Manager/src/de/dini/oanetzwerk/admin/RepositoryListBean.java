/**
 * 
 */
package de.dini.oanetzwerk.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.admin.utils.AbstractBean;
import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestXmlCodec;

@ManagedBean(name = "repoList")
@SessionScoped
public class RepositoryListBean extends AbstractBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(RepositoryListBean.class);

	FacesContext ctx = FacesContext.getCurrentInstance();
	HttpSession session = (HttpSession) ctx.getExternalContext().getSession(false);

	@ManagedProperty(value = "#{restconnector}")
	private RestConnector connector;

	private RepositoryBean repo;
	private List<RepositoryBean> repoList;

	public RepositoryListBean() {

		super();
	}

	@PostConstruct
	public void init() {
		String result = connector.prepareRestTransmission("Repository/").GetData();
		repoList = new ArrayList<RepositoryBean>();
		RestMessage rms = RestXmlCodec.decodeRestMessage(result);

		if (rms == null || rms.getListEntrySets().isEmpty()) {

			logger.error("received no Repository Details at all from the server");
			return;
		}

		for (RestEntrySet res : rms.getListEntrySets()) {

			Iterator<String> it = res.getKeyIterator();
			String key = "";
			RepositoryBean repo = new RepositoryBean();

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

		// if (logger.isDebugEnabled())
		// logger.debug("DBAccessNG Instance will be prepared!");
		//
		// try {
		//
		// DataSource datasource = (DataSource) ((Context) new
		// InitialContext().lookup("java:comp/env")).lookup("jdbc/oanetztest");
		// Connection connection = datasource.getConnection();
		// Connection conn2 = connection;
		// System.out.println(conn2);
		//
		// } catch (NamingException ex) {
		//
		// // this.datasource = new BasicDataSource();
		// // this.datasource.
		// logger.error(ex.getLocalizedMessage(), ex);
		// } catch (SQLException ex) {
		//
		// }

	}

	public List<RepositoryBean> getRepositories() {
		return repoList;
	}

	public String toBetested() {

		return "z";
	}

	public void setConnector(RestConnector connector) {
		this.connector = connector;
	}

}
