package de.dini.oanetzwerk.servicemodule.examples;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.servicemodule.RestClient;
import de.dini.oanetzwerk.utils.HelperMethods;

public class OIDGetter {

	private static Properties props;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String repID = "";
		String extOID = "";
		String oanOID = "-1";
		
		if(args.length != 2) {			
			System.err.println("usage: OIDGetter <repositoryID> <externalObjectID>");
			System.exit(-1);
		}
		
		repID = args[0];
		extOID = args[1];		
		
		try {
			props = HelperMethods.loadPropertiesFromFile("oidgetterprop.xml");
		} catch(IOException ioex) {
			System.err.println(ioex);
			System.exit(-1);
		}
			
		// REST-Anfrage auf Keyword mittels RestClient-Objekt formulieren
		RestClient restclient = RestClient.createRestClient(props.getProperty("host"),
				                                            "ObjectEntryID/" + repID + "/" + extOID,
				                                            props.getProperty("username"),
				                                            props.getProperty("password"));

		// REST-Anfrage abschicken und RestMessage-Objekt erhalten
		RestMessage msgResponse = restclient.sendGetRestMessage();

		// Daten aus dem RestMessage-Objekt entnehmen und benutzen
		for(RestEntrySet entrySet : msgResponse.getListEntrySets()) {
			Iterator<String> it = entrySet.getKeyIterator();
			while(it.hasNext()) {
				String key = it.next();
				if("oid".equalsIgnoreCase(key)) oanOID = entrySet.getValue(key);
			}
		}
		
		System.out.print(oanOID);
/*		if(msgResponse.getStatus() != RestStatusEnum.OK) {
			System.err.println(msgResponse.getStatus() + "\n" + msgResponse.getStatusDescription());
		}*/
	}

}
