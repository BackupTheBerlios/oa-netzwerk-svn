/**
 * 
 */
package de.dini.oanetzwerk.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.admin.utils.AbstractBean;


@ManagedBean(name="userList")
@SessionScoped 
public class UserListBean extends AbstractBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(RepositoryListBean.class);

	FacesContext ctx = FacesContext.getCurrentInstance();
	HttpSession session = (HttpSession) ctx.getExternalContext().getSession(false);
	
	private RepositoryBean repo;

	public UserListBean() {

		super();
	}

		
	public List<UserBean> getUsers() {

//		if (true)
//			return new ArrayList<RepositoryBean>();
		
		// mock logic
		List<UserBean> userList = new ArrayList<UserBean>();
		
		UserBean ub1 = new UserBean();
		ub1.setUsername("oanadmin");
		ub1.setEmail("sammy.david@cms.hu-berlin.de");
		ub1.setLastName("David");
		ub1.setFirstName("Sammy");
		ub1.setRightReportManagement(true);
		ub1.setRightRepositoryManagement(true);
		ub1.setRightServiceManagement(true);
		ub1.setRightUserManagement(true);
		ub1.setLastLogin(new Date());
		
		UserBean ub2 = new UserBean();
		ub2.setUsername("testuser");
		ub2.setEmail("davidsam@cms.hu-berlin.de");
		ub2.setLastName("Mustermann");
		ub2.setFirstName("Max");
		ub2.setRightReportManagement(true);
		ub2.setRightRepositoryManagement(false);
		ub2.setRightServiceManagement(true);
		ub2.setRightUserManagement(false);
		ub2.setLastLogin(new Date());
		
		userList.add(ub1);
		userList.add(ub2);
		
		// TODO. actually fetch data from db,
		// use security context
		
		
//		String result = this.prepareRestTransmission("Repository/").GetData();
//		List<RepositoryBean> repoList = new ArrayList<RepositoryBean>();
//		RestMessage rms = RestXmlCodec.decodeRestMessage(result);
//
//		if (rms == null || rms.getListEntrySets().isEmpty()) {
//
//			logger.error("received no Repository Details at all from the server");
//			return null;
//		}
//
//		for (RestEntrySet res : rms.getListEntrySets()) {
//
//			Iterator<String> it = res.getKeyIterator();
//			String key = "";
//			RepositoryBean repo = new RepositoryBean();
//
//			while (it.hasNext()) {
//
//				key = it.next();
//
//				// if (logger.isDebugEnabled ( ))
//				// logger.debug ("key: " + key + " value: " + res.getValue
//				// (key));
//
//				if (key.equalsIgnoreCase("name")) {
//
//					repo.setName(res.getValue(key));
//
//				} else if (key.equalsIgnoreCase("url")) {
//
//					repo.setUrl(res.getValue(key));
//
//				} else if (key.equalsIgnoreCase("repository_id")) {
//
//					repo.setId(new Long(res.getValue(key)));
//
//				} else
////					System.out.println("Key: " + key);
//					continue;
//			}
//
//			repoList.add(repo);
//			
//		}
//		System.out.println(repoList.size());
//		
//		if (logger.isDebugEnabled())
//			logger.debug("DBAccessNG Instance will be prepared!");
//
//		try {
//
//			DataSource datasource = (DataSource) ((Context) new InitialContext().lookup("java:comp/env")).lookup("jdbc/oanetztest");
//			Connection connection = datasource.getConnection();
//			Connection conn2 = connection;
//			System.out.println(conn2);
//			
//		} catch (NamingException ex) {
//
////			this.datasource = new BasicDataSource();
////			this.datasource.
//			logger.error(ex.getLocalizedMessage(), ex);
//		} catch (SQLException ex) {
//			
//		}
//		
		return userList;
	}
	
}
