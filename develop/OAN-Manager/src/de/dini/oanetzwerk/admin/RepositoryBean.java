/**
 * 
 */
package de.dini.oanetzwerk.admin;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.admin.utils.AbstractBean;
import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.server.database.DBAccessNG;
import de.dini.oanetzwerk.server.database.DeleteFromDB;
import de.dini.oanetzwerk.server.database.InsertIntoDB;
import de.dini.oanetzwerk.server.database.SingleStatementConnection;
import de.dini.oanetzwerk.server.database.UpdateInDB;
import de.dini.oanetzwerk.utils.DBHelper;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.MessageUtils;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;

/**
 * @author Michael K&uuml;hn
 * 
 */

@ManagedBean(name = "repo")
public class RepositoryBean extends AbstractBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(RepositoryBean.class);

	FacesContext ctx = FacesContext.getCurrentInstance();
	HttpSession session = (HttpSession) ctx.getExternalContext().getSession(
			false);

	private boolean deactivated = false;
	private boolean deleted = false;
	private boolean stored = false;

	private Long id = null;
	private String name;
	private String owner;
	private String ownerEmail;
	private String url;
	private String oaiUrl;
	private String harvestAmount = "10";
	private String harvestPause = "5000";
	private String lastFullHarvestBegin;
	private String testData;
	private boolean listRecords;
	private boolean active = true;

	public RepositoryBean() {
		super();
		initRepositoryBean();
	}

	public boolean success() {
		return deactivated || deleted || stored;
	}

	// public String message() {
	// return ctx.getMessages("repositories");
	// }

	private void initRepositoryBean() {

		HttpServletRequest request = (HttpServletRequest) ctx
				.getExternalContext().getRequest();
		String repoId = request.getParameter("rid");

		if (repoId == null) {
			return;
		}

		try {
			this.id = Long.parseLong(repoId);

		} catch (NumberFormatException e) {
			e.printStackTrace();
			// redirect to repo_main page
			try {
				ctx.getExternalContext().redirect("/repositories_main.xhtml");
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		// get repo from db, initialize
		HashMap<String, String> details = getDetails();

		for (String key : details.keySet()) {

			System.out.println("key: " + key);
			if (key == null) {
				continue;
			} else if (key.equals("name")) {
				this.name = details.get(key);
			} else if (key.equals("url")) {
				this.url = details.get(key);
			} else if (key.equals("oai_url")) {
				this.oaiUrl = details.get(key);
			} else if (key.equals("harvest_amount")) {
				this.harvestAmount = details.get(key);
			} else if (key.equals("last_full_harvest_begin")) {
				this.lastFullHarvestBegin = details.get(key);
			} else if (key.equals("testdata")) {
				this.testData = details.get(key);
			} else if (key.equals("active")) {
				this.active = Boolean.parseBoolean(details.get(key));
			}
		}
	}

	public HashMap<String, String> getDetails() {

		String result = this.prepareRestTransmission(
				"Repository/" + Long.toString(id)).GetData();

		HashMap<String, String> details = new HashMap<String, String>();
		RestMessage rms = RestXmlCodec.decodeRestMessage(result);

		if (rms == null || rms.getListEntrySets().isEmpty()) {

			logger.error("received no Repository Details at all from the server");
			return null;
		}

		RestEntrySet res = rms.getListEntrySets().get(0);
		Iterator<String> it = res.getKeyIterator();
		String key = "";

		while (it.hasNext()) {

			key = it.next();
			details.put(key, res.getValue(key));
		}

		return details;
	}

	
	
	public String storeRepository() {

		System.out.println("Name: " + name);
		System.out.println("Url: " + url);
		System.out.println("" + harvestAmount);
		logger.warn("jsdfbkj");


		// REST call
		

		RestMessage rms;
		RestEntrySet res;
		RestMessage result = null;

		rms = new RestMessage();

		rms.setKeyword(RestKeyword.Repository);
		rms.setStatus(RestStatusEnum.OK);

		res = new RestEntrySet();

		res.addEntry("name", this.getName());
		res.addEntry("url", this.getUrl());
		res.addEntry("owner", this.getOwner());
		res.addEntry("ownerEmail", this.getOwnerEmail());
		res.addEntry("oaiUrl", this.getOaiUrl());
		res.addEntry("harvestAmount", this.getHarvestAmount());
		res.addEntry("lastFullHarvestBegin", this.getLastFullHarvestBegin());
		res.addEntry("harvestPause", this.getHarvestPause());
		res.addEntry("testData", this.getTestData());
		
		if(this.isActive() == true){
			res.addEntry("isActive", "true");
		} else{
			res.addEntry("isActive", "false");
		}
		if(this.isListRecords() == true){
			res.addEntry("islistRecords", "true");
		} else{
			res.addEntry("isListRecords", "false");
		}	
		
		rms.addEntrySet(res);
		
		
		try {
			result = prepareRestTransmission("Repository/").sendPutRestMessage(rms);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.info("PUT sent to /Repository");

		

		ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
				"info.success_stored", null));

		return "success";
	}

	public String delete() {

		System.out.println("delete!");

		System.out.println("Name: " + name);
		System.out.println("Url: " + url);
		System.out.println("" + harvestAmount);
		logger.warn("jsdfbkj");
		try {

			SingleStatementConnection stmtconn = (SingleStatementConnection) new DBAccessNG()
					.getSingleStatementConnection();
			PreparedStatement statement = DeleteFromDB.Repositories(
					stmtconn.connection, id);

			new DBHelper().save(stmtconn, statement);

			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"info.success_deleted", null));

		} catch (WrongStatementException ex) {
			logger.error(ex.getLocalizedMessage(), ex);
		} catch (SQLException ex) {
			logger.error(ex.getLocalizedMessage(), ex);
		}

		return "";
	}

	public String deactivate() {

		System.out.println("deactivate!");

		System.out.println("Name: " + name);
		System.out.println("Url: " + url);
		System.out.println("" + harvestAmount);
		try {

			SingleStatementConnection stmtconn = (SingleStatementConnection) new DBAccessNG()
					.getSingleStatementConnection();
			PreparedStatement statement = UpdateInDB.Repository(
					stmtconn.connection, id, false);

			new DBHelper().save(stmtconn, statement);

			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"info.success_deactivated", null));

		} catch (WrongStatementException ex) {
			logger.error(ex.getLocalizedMessage(), ex);
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Error", null));
		} catch (SQLException ex) {
			logger.error(ex.getLocalizedMessage(), ex);
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Error", null));
		}

		return "";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setOwner(String owner){
		this.owner = owner;
	}
	
	public String getOwner(){
		return owner;
	}
	
	public void setOwnerEmail(String ownerEmail){
		this.ownerEmail = ownerEmail;
	}
	
	public String getOwnerEmail(){
		return ownerEmail;
	}
	
	public String getOaiUrl() {
		return oaiUrl;
	}

	public void setOaiUrl(String oaiUrl) {
		this.oaiUrl = oaiUrl;
	}

	public String getHarvestAmount() {
		return harvestAmount;
	}

	public void setHarvestAmount(String harvestAmount) {
		this.harvestAmount = harvestAmount;
	}

	public String getLastFullHarvestBegin() {
		return lastFullHarvestBegin;
	}

	public void setLastFullHarvestBegin(String lastFullHarvestBegin) {
		this.lastFullHarvestBegin = lastFullHarvestBegin;
	}

	public String getHarvestPause() {
		return harvestPause;
	}

	public void setHarvestPause(String harvestPause) {
		this.harvestPause = harvestPause;
	}

	public boolean isListRecords() {
		return listRecords;
	}

	public void setListRecords(boolean listRecords) {
		this.listRecords = listRecords;
	}

	public String getTestData() {
		return testData;
	}

	public void setTestData(String testData) {
		this.testData = testData;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
