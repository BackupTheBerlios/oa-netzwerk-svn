package de.dini.oanetzwerk.oaipmh;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.dini.oanetzwerk.oaipmh.ConnectionToolkit;
import de.dini.oanetzwerk.oaipmh.DataConnectionType;
import de.dini.oanetzwerk.oaipmh.OAIPMHVerbs;

/**
 * 
 * @author Sammy David
 *
 */

public abstract class AbstractOAIPMHVerb implements OAIPMHVerbs {

	private static Logger logger = Logger.getLogger(AbstractOAIPMHVerb.class);

	public static final String ID_PREFIX = "oai:oanet.de:";
	
	private static Map<String, String[]> allowedArguments;
	private static final int DEFAULT_MAX_RESULTS = 100;
	private static final String OAIPMH_CONFIG_PATH = 
		System.getProperty("catalina.home") + System.getProperty("file.separator") + "webapps/oaipmh/oaipmh.properties";
	
	protected ConnectionToolkit dataConnectionToolkit;
	private DataConnectionType connectionType = DataConnectionType.DB;
	private int maxResults = DEFAULT_MAX_RESULTS;

	static {

		allowedArguments = new HashMap<String, String[]>();
		allowedArguments.put("Identify", new String[] { "verb" });
		allowedArguments.put("GetRecord", new String[] { "verb", "identifier", "metadataPrefix" });
		allowedArguments.put("ListMetadataFormats", new String[] { "verb", "identifier" });
		allowedArguments.put("ListIdentifiers", new String[] { "verb", "from", "until", "set", "resumptionToken", "metadataPrefix" });
		allowedArguments.put("ListRecords", new String[] { "verb", "from", "until", "set", "resumptionToken", "metadataPrefix" });
		allowedArguments.put("ListSets", new String[] { "verb", "resumptionToken" });
	}

	public AbstractOAIPMHVerb() {
		super();
		loadConfig();

		this.dataConnectionToolkit = ConnectionToolkit.getFactory(this.connectionType);
	}

	
	abstract public String processRequest(Map<String, String[]> parameter);

	
	void loadConfig() {
		
		// read oaipmh properties
		
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(OAIPMH_CONFIG_PATH));

			String results = properties.getProperty("maximum_results");
			String connType = properties.getProperty("connection_type");

			if (StringUtils.isNotEmpty(results)) {
				maxResults = Integer.parseInt(results);
			}
			
			if (StringUtils.isNotEmpty(connType)) {
				if (connType.equals("REST")) {
					connectionType = DataConnectionType.REST;
				} else if (connType.equals("DB")){
					connectionType = DataConnectionType.DB;
				} else {
					logger.warn("Unknown Connection Type! Please check the oaipmh.properties file!");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	String checkForBadArguments(final Map<String, String[]> actualArguments) {

		if (actualArguments == null || actualArguments.size() == 0) {
			return new OAIPMHError(OAIPMHErrorcodeType.BAD_ARGUMENT, "The OAIPMH-Verb is missing!").toString();
		}

		Iterator<String> keyIterator = actualArguments.keySet().iterator();
		String verb = actualArguments.get("verb")[0];
		List<String> allowedArguments = Arrays.asList(AbstractOAIPMHVerb.allowedArguments.get(verb));
		String key;

		List<String> badArguments = new ArrayList<String>();

		while (keyIterator.hasNext()) {
			key = keyIterator.next();
			if (!allowedArguments.contains(key)) {
				badArguments.add(key);
			}
		}

		if (badArguments.isEmpty()) {
			return null;
		}

		StringBuffer errorMsg = new StringBuffer("The request contains invalid arguments! (");
		for (int i = 0; i < badArguments.size(); i++) {
			if (i == badArguments.size() - 1) {
				errorMsg.append("'").append(badArguments.get(i)).append("')");
			} else {

				errorMsg.append("'").append(badArguments.get(i)).append("', ");
			}
		}

		return new OAIPMHError(OAIPMHErrorcodeType.BAD_ARGUMENT, errorMsg.toString()).toString();
	}

	public int getMaxResults() {
		return maxResults;
	}
	
}
