package de.dini.oanetzwerk.oaipmh;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.apache.log4j.Logger;

/**
 * 
 * @author Sammy David
 *
 */

public class ResumptionTokenManager {

	private static final Logger LOG = Logger.getLogger(ListRecords.class);
	public	static final String RESUMPTION_TOKEN_STORAGE_PATH = 
		System.getProperty("catalina.home") + System.getProperty("file.separator") + "webapps/oaipmh/resumptionToken/";
	
	
	public static String createNewResumptionToken() {

		String resumptionToken = "oanetToken" + UUID.randomUUID().hashCode();

		return resumptionToken;
	}

	public static ResumptionToken storeResumptionToken(String resumptionTokenId, BigInteger idOffset, BigInteger completeListSize, BigInteger cursor, HashMap<String, Object> parameters) {

		Date expirationDate = new Date(new Date().getTime() + 86400000);
		
		ObjectOutputStream oos;
		ResumptionToken token = new ResumptionToken(resumptionTokenId, idOffset, expirationDate, completeListSize, cursor, parameters);
		try {

			oos = new ObjectOutputStream(new FileOutputStream(RESUMPTION_TOKEN_STORAGE_PATH + resumptionTokenId));
			oos.writeObject(token);
			
			return token;
		} catch (FileNotFoundException ex) {

			LOG.error(ex.getLocalizedMessage(), ex);

		} catch (IOException ex) {

			LOG.error(ex.getLocalizedMessage(), ex);
		}
		return null;
	}
	
	public static ResumptionToken loadResumptionToken(String resumptionTokenId)
	{
		ResumptionToken token = null;
		
		try {

			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(RESUMPTION_TOKEN_STORAGE_PATH + resumptionTokenId));
			token = (ResumptionToken) ois.readObject();

		} catch (FileNotFoundException ex) {

			LOG.error(ex.getLocalizedMessage(), ex);
			
		} catch (IOException ex) {

			LOG.error(ex.getLocalizedMessage(), ex);

		} catch (ClassNotFoundException ex) {

			LOG.error(ex.getLocalizedMessage(), ex);
		}
		return token;
	}
}
