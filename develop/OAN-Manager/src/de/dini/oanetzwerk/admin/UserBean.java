/**
 * 
 */
package de.dini.oanetzwerk.admin;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.admin.utils.AbstractBean;

/**
 * @author Michael K&uuml;hn
 * 
 */

@ManagedBean(name = "repo")
public class UserBean extends AbstractBean implements Serializable {

	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(UserBean.class);
	private static final SimpleDateFormat DATE_GER = new SimpleDateFormat("dd.MM.yyyy");
	
	FacesContext ctx = FacesContext.getCurrentInstance();
	HttpSession session = (HttpSession) ctx.getExternalContext().getSession(
			false);

	private boolean deactivated = false;
	private boolean deleted = false;

	private Long id = null;
	private String username;
	private String password;
	private String firstName;
	private String lastName;
	private String email;
	private boolean rightRepositoryManagement;
	private boolean rightServiceManagement;
	private boolean rightUserManagement;
	private boolean rightReportManagement;
	private Date lastLogin;

	public UserBean() {
		super();
		// initUserBean();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isRightRepositoryManagement() {
		return rightRepositoryManagement;
	}

	public void setRightRepositoryManagement(boolean rightRepositoryManagement) {
		this.rightRepositoryManagement = rightRepositoryManagement;
	}

	public boolean isRightServiceManagement() {
		return rightServiceManagement;
	}

	public void setRightServiceManagement(boolean rightServiceManagement) {
		this.rightServiceManagement = rightServiceManagement;
	}

	public boolean isRightUserManagement() {
		return rightUserManagement;
	}

	public void setRightUserManagement(boolean rightUserManagement) {
		this.rightUserManagement = rightUserManagement;
	}

	public boolean isRightReportManagement() {
		return rightReportManagement;
	}

	public void setRightReportManagement(boolean rightReportManagement) {
		this.rightReportManagement = rightReportManagement;
	}

	public String getLastLogin() {
		return DATE_GER.format(lastLogin);
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getName() {
		return firstName + " " + lastName;
	}
	
	
	public String getRights() {
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(rightRepositoryManagement ? "Repositoryverwaltung, " : "");
		buffer.append(rightServiceManagement? "Diensteverwaltung, " : "");
		buffer.append(rightUserManagement ? "Benutzerverwaltung, " : "");
		buffer.append(rightReportManagement ? "Reportzugriff, " : "");
		String rights = buffer.toString();
		
		return rights.endsWith(", ") ? rights.substring(0, rights.length()-2) : rights;
	}
	// public String message() {
	// return ctx.getMessages("repositories");
	// }

	// private void initUserBean() {
	//
	// HttpServletRequest request = (HttpServletRequest)
	// ctx.getExternalContext().getRequest();
	// String repoId = request.getParameter("rid");
	//
	// if (repoId == null) {
	// return;
	// }
	//
	// try {
	// this.id = Long.parseLong(repoId);
	//
	// } catch (NumberFormatException e) {
	// e.printStackTrace();
	// // redirect to repo_main page
	// try {
	// ctx.getExternalContext().redirect("/repositories_main.xhtml");
	// } catch (IOException ex) {
	// ex.printStackTrace();
	// }
	// }
	//
	// // get repo from db, initialize
	// HashMap<String, String> details = getDetails();
	//
	// for (String key : details.keySet()) {
	//
	// System.out.println("key: " + key);
	// if (key == null) {
	// continue;
	// } else if (key.equals("name")) {
	// this.name = details.get(key);
	// } else if (key.equals("url")) {
	// this.url = details.get(key);
	// } else if (key.equals("oai_url")) {
	// this.oaiUrl = details.get(key);
	// } else if (key.equals("harvest_amount")) {
	// this.harvestAmount = details.get(key);
	// } else if (key.equals("last_full_harvest_begin")) {
	// this.lastFullHarvestBegin = details.get(key);
	// } else if (key.equals("testdata")) {
	// this.testData = details.get(key);
	// } else if (key.equals("active")) {
	// this.active = Boolean.parseBoolean(details.get(key));
	// }
	// }
	// }
	//
	//
	// public HashMap<String, String> getDetails() {
	//
	// String result = this.prepareRestTransmission("Repository/" +
	// Long.toString(id)).GetData();
	//
	// HashMap<String, String> details = new HashMap<String, String>();
	// RestMessage rms = RestXmlCodec.decodeRestMessage(result);
	//
	// if (rms == null || rms.getListEntrySets().isEmpty()) {
	//
	// logger.error("received no Repository Details at all from the server");
	// return null;
	// }
	//
	// RestEntrySet res = rms.getListEntrySets().get(0);
	// Iterator<String> it = res.getKeyIterator();
	// String key = "";
	//
	// while (it.hasNext()) {
	//
	// key = it.next();
	// details.put(key, res.getValue(key));
	// }
	//
	// return details;
	// }
	//
	//
	// public String storeRepository() {
	//
	// System.out.println("Name: " + name);
	// System.out.println("Url: " + url);
	// System.out.println("" + harvestAmount);
	// logger.warn("jsdfbkj");
	// try {
	//
	// SingleStatementConnection stmtconn = (SingleStatementConnection) new
	// DBAccessNG().getSingleStatementConnection();
	// PreparedStatement statement =
	// InsertIntoDB.Repository(stmtconn.connection, name, url, oaiUrl,
	// Integer.parseInt(harvestAmount),
	// Integer.parseInt(harvestPause), listRecords,
	// Boolean.parseBoolean(testData), active);
	//
	// new DBHelper().save(stmtconn, statement);
	//
	// ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
	// "info.success_stored", null));
	//
	// } catch (WrongStatementException ex) {
	// logger.error(ex.getLocalizedMessage(), ex);
	// } catch (SQLException ex) {
	// logger.error(ex.getLocalizedMessage(), ex);
	// }
	//
	// return "success";
	// }
	//
	// public String delete() {
	//
	// System.out.println("delete!");
	//
	// System.out.println("Name: " + name);
	// System.out.println("Url: " + url);
	// System.out.println("" + harvestAmount);
	// logger.warn("jsdfbkj");
	// try {
	//
	// SingleStatementConnection stmtconn = (SingleStatementConnection) new
	// DBAccessNG().getSingleStatementConnection();
	// PreparedStatement statement =
	// DeleteFromDB.Repositories(stmtconn.connection, id);
	//
	// new DBHelper().save(stmtconn, statement);
	//
	// ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
	// "info.success_deleted", null));
	//
	// } catch (WrongStatementException ex) {
	// logger.error(ex.getLocalizedMessage(), ex);
	// } catch (SQLException ex) {
	// logger.error(ex.getLocalizedMessage(), ex);
	// }
	//
	//
	// return "";
	// }
	//
	// public String deactivate() {
	//
	// System.out.println("deactivate!");
	//
	// System.out.println("Name: " + name);
	// System.out.println("Url: " + url);
	// System.out.println("" + harvestAmount);
	// try {
	//
	// SingleStatementConnection stmtconn = (SingleStatementConnection) new
	// DBAccessNG().getSingleStatementConnection();
	// PreparedStatement statement = UpdateInDB.Repository(stmtconn.connection,
	// id, false);
	//
	// new DBHelper().save(stmtconn, statement);
	//
	// ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
	// "info.success_deactivated", null));
	//
	// } catch (WrongStatementException ex) {
	// logger.error(ex.getLocalizedMessage(), ex);
	// ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
	// "Error", null));
	// } catch (SQLException ex) {
	// logger.error(ex.getLocalizedMessage(), ex);
	// ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
	// "Error", null));
	// }
	//
	// return "";
	// }

}
