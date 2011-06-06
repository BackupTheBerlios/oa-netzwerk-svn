/**
 * 
 */
package de.dini.oanetzwerk.admin;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.admin.ServiceSchedulingBean.JobType;
import de.dini.oanetzwerk.admin.utils.AbstractBean;
import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.server.database.DBAccessNG;
import de.dini.oanetzwerk.server.database.DeleteFromDB;
import de.dini.oanetzwerk.server.database.SingleStatementConnection;
import de.dini.oanetzwerk.server.database.UpdateInDB;
import de.dini.oanetzwerk.utils.DBHelper;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;

/**
 * @author Michael K&uuml;hn
 * 
 */

@ManagedBean(name = "repo")
@RequestScoped
public class RepositoryBean extends AbstractBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(RepositoryBean.class);

	private static RestConnector connector;	
	
	FacesContext ctx = FacesContext.getCurrentInstance();
	HttpSession session = (HttpSession) ctx.getExternalContext().getSession(
			false);
	
//	@ManagedProperty(value="#{restConnector}")
//	private RestConnector restConnector;	
	
	private Repository repository;
	private String repoId;

	public RepositoryBean() {
		super();
//		if (restConnector != null) 
//		{
//			connector = restConnector;
//		}
		System.out.println("RepositoryBean constructor");
		init();
		
	}


	public String init() {

		repository = new Repository();
		
		HttpServletRequest request = (HttpServletRequest) ctx
				.getExternalContext().getRequest();
		

		String repoId = request.getParameter("rid");
//		String repoId = FacesContext.getCurrentInstance().
//		getExternalContext().getRequestParameterMap().get("identifier");

		System.out.println("ID1: " + repoId);
		System.out.println("ID2: " + this.repoId);
		if (repoId == null) {
			return null;
		}

		try {
			repository.setId(Long.parseLong(repoId));

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
				repository.setName(details.get(key));
			} else if (key.equals("url")) {
				repository.setUrl(details.get(key));
			} else if (key.equals("oai_url")) {
				repository.setOaiUrl(details.get(key));
			} else if (key.equals("harvest_amount")) {
				repository.setHarvestAmount(details.get(key));
			} else if (key.equals("last_full_harvest_begin")) {
				repository.setLastFullHarvestBegin(details.get(key));
			} else if (key.equals("testdata")) {
				repository.setTestData(details.get(key));
			} else if (key.equals("active")) {
				repository.setActive(Boolean.parseBoolean(details.get(key)));
			}
		}
		return "repository_view.xhtml";
	}

	public HashMap<String, String> getDetails() {

		if (connector == null) 
		{
			System.out.println("yes is null");
		}
		String result = connector.prepareRestTransmission(
				"Repository/" + Long.toString(repository.getId())).GetData();

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

//	public boolean validate()
//	{
//		
//		boolean valid = true;
//		System.out.println("Vaildating");
//		FacesContext context = FacesContext.getCurrentInstance();
//		System.out.println(chosenDate);
//		if (JobType.OneTime.toString().equals(jobType) && !startRightNow) {
//			try {
//				Date date = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse(new SimpleDateFormat("dd-MM-yyyy").format(new Date()) + " " + chosenHour);
//				job.setNonperiodicTimestamp(date);
//
//				if (System.currentTimeMillis() > date.getTime()) {
////					((UIInput) toValidate).setValid(false);
//
//					FacesMessage message = new FacesMessage("Das gewählte Datum muss in der Zukunft liegen.");
//					context.addMessage("1", message);
//					valid = false;
//				}
//			} catch (ParseException e) {
//				FacesMessage message = new FacesMessage("Bitte überprüfen sie das gewählte Datum! (Format: TT.MM.JJJJ)");
//				context.addMessage("1", message);
//				valid = false;
//			}
//		}
//		return valid;
//	}
	
	public String storeRepository() {

		System.out.println("Name: " + repository.getName());
		System.out.println("Url: " + repository.getUrl());
		System.out.println("" + repository.getHarvestAmount());

		// REST call
		RestMessage rms;
		RestEntrySet res;
		RestMessage result = null;

		rms = new RestMessage();

		rms.setKeyword(RestKeyword.Repository);
		rms.setStatus(RestStatusEnum.OK);

		res = new RestEntrySet();

		res.addEntry("name", repository.getName());
		res.addEntry("url", repository.getUrl());
		res.addEntry("owner", repository.getOwner());
		res.addEntry("ownerEmail", repository.getOwnerEmail());
		res.addEntry("oaiUrl", repository.getOaiUrl());
		res.addEntry("harvestAmount", repository.getHarvestAmount());
		res.addEntry("lastFullHarvestBegin", repository.getLastFullHarvestBegin());
		res.addEntry("harvestPause", repository.getHarvestPause());
		res.addEntry("testData", repository.getTestData());
		res.addEntry("active", Boolean.toString(repository.isActive()));
		res.addEntry("listRecords", Boolean.toString(repository.isListRecords()));
			
		
		rms.addEntrySet(res);
		
		
		try {
			result = connector.prepareRestTransmission("Repository/").sendPutRestMessage(rms);
			
			if (rms.getStatus() != RestStatusEnum.OK) {

				logger.error("/Repository response failed: " + rms.getStatus() + "("
						+ rms.getStatusDescription() + ")");
				return "failed";
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "failed";
		}
		
		logger.info("PUT sent to /Repository");

		

		ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
				"info.success_stored", null));

		return "success";
	}

	// TODO: reimplement
//	public String delete() {
//
//		System.out.println("delete!");
//
//		System.out.println("Name: " + repository.getName());
//		System.out.println("Url: " + repository.getUrl());
//		System.out.println("" + repository.getHarvestAmount());
//
//		try {
//
//			SingleStatementConnection stmtconn = (SingleStatementConnection) new DBAccessNG()
//					.getSingleStatementConnection();
//			PreparedStatement statement = DeleteFromDB.Repositories(
//					stmtconn.connection, repository.getId());
//
//			new DBHelper().save(stmtconn, statement);
//
//			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
//					"info.success_deleted", null));
//
//		} catch (WrongStatementException ex) {
//			logger.error(ex.getLocalizedMessage(), ex);
//		} catch (SQLException ex) {
//			logger.error(ex.getLocalizedMessage(), ex);
//		}
//
//		return "";
//	}

//	public String deactivate() {
//
//		System.out.println("deactivate!");
//
//		System.out.println("Name: " + repository.getName());
//		System.out.println("Url: " + repository.getUrl());
//		System.out.println("" + repository.getHarvestAmount());
//		try {
//
//			SingleStatementConnection stmtconn = (SingleStatementConnection) new DBAccessNG()
//					.getSingleStatementConnection();
//			PreparedStatement statement = UpdateInDB.Repository(
//					stmtconn.connection, repository.getId(), false);
//
//			new DBHelper().save(stmtconn, statement);
//
//			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
//					"info.success_deactivated", null));
//
//		} catch (WrongStatementException ex) {
//			logger.error(ex.getLocalizedMessage(), ex);
//			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
//					"Error", null));
//		} catch (SQLException ex) {
//			logger.error(ex.getLocalizedMessage(), ex);
//			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
//					"Error", null));
//		}
//
//		return "";
//	}

	
	public Repository getRepository() {
		return repository;
	}
	
	

	public void setRepoId(String repoId) {
		this.repoId = repoId;
	}


//	public void setRestConnector(RestConnector restConnector) {
//		this.restConnector = restConnector;
//		System.out.println("RepositoryBean restConnector setter");
//	}

	public static void setRestConnector(RestConnector restConnector) {
		connector = restConnector;
		System.out.println("RepositoryBean restConnector setter");
	}
}
