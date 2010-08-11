package de.dini.oanetzwerk.oaipmh.tokencleaner;

import java.io.File;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;



/**
 * This class simply removes expired tokens from the resumptionToken directory within the servlet container.
 * 
 * @author Sammy David
 *
 */

public class TokenCleaner {

	private static final Logger LOG = Logger.getLogger("TokenCleanerLog");
	
	private static final long MILLIS_PER_DAY = 86400000;
//	private static final long MILLIS_PER_MINUTE = 60000; // testing only
	
	private String tokenDirectoryPath = null;
	
	public static void main(String[] args) {

		// setup log4j config-file
		DOMConfigurator.configure("log4j.xml");
		
		new TokenCleaner().clean();
	}
	
	
	
	
	
	public TokenCleaner() {
		super();

		init();
	}
	

	private void init() {
		
		final String separator = System.getProperty("file.separator");
		final String catalina = System.getProperty("catalina.home");
		
		if (catalina == null)
		{
			LOG.error("Could not run TokenCleaner, system property 'catalina.home' could not be found/has not been set!");
			return;
		}
		
		tokenDirectoryPath = new StringBuffer(catalina)
								.append(separator).append("webapps")
								.append(separator).append("oaipmh")
								.append(separator).append("resumptionToken")
								.toString();
	}
	

	public void clean()
	{
		if (tokenDirectoryPath == null)
			return;
		
		LOG.info("Starting to clean resumptionToken-directory!");
		
		File tokenDirectory = new File(tokenDirectoryPath);
		Date now = new Date();
		
		if (tokenDirectory.isDirectory()) {
			
			String[] tokenNames = tokenDirectory.list();
			File token;
			
			for (String tokenName : tokenNames) {
				
				if (tokenName != null && tokenName.startsWith("oanetToken")) {
					
					token = new File(tokenDirectoryPath + "/" + tokenName);
					if (token.lastModified() + MILLIS_PER_DAY <= now.getTime()) {
						// remove file as it is expired
						boolean deleted = token.delete();
						
						if (deleted)
							LOG.info("Successfully deleted expired resumptionToken with id '" + tokenName + "'!");
					}
					
				}
			}
		}
		LOG.info("Finished cleaning resumptionToken-directory!");
	}
	
	
}
