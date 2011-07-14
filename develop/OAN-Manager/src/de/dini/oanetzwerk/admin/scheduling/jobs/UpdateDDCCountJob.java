package de.dini.oanetzwerk.admin.scheduling.jobs;

import java.io.UnsupportedEncodingException;

import javax.faces.bean.ManagedProperty;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.dini.oanetzwerk.admin.RestConnector;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestXmlCodec;

public class UpdateDDCCountJob implements Job {

	private static Logger logger = Logger.getLogger(UpdateDDCCountJob.class);

	@ManagedProperty(value = "#{restconnector}")
	private RestConnector connector;
	
	public UpdateDDCCountJob() {
	    super();
    }
	

	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		logger.info("DDC-Counter has been initiated! Updating DDC counts...");
		
		String result = null;
        try {
	        result = connector.prepareRestTransmission("DDCStatistics/").PostData("");
        } catch (UnsupportedEncodingException e) {
	        e.printStackTrace();
        }
        
		RestMessage rms = RestXmlCodec.decodeRestMessage(result);
		
		System.out.println("DDC-Update result: " + result);
		
		if (rms == null || rms.getListEntrySets().isEmpty()) {

			logger.error("received no Repository Details at all from the server");
		}
		
		logger.info("DDC-Counter finished! DDC counts are now up to date.");
	}
	
	
	/*********************** Getter & Setter ***********************/


	public void setConnector(RestConnector connector) {
		this.connector = connector;
	}
}
