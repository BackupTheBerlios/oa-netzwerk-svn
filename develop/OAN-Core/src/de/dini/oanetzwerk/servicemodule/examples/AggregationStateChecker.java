package de.dini.oanetzwerk.servicemodule.examples;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.servicemodule.RestClient;
import de.dini.oanetzwerk.utils.HelperMethods;

public class AggregationStateChecker {

	private static Properties props;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			props = HelperMethods.loadPropertiesFromFile("aggregatorprop.xml");
		} catch(IOException ioex) {

		}
		
		// REST-Anfrage auf Keyword mittels RestClient-Objekt formulieren
		RestClient restclient = RestClient.createRestClient(props.getProperty("host"),
                "AllOIDs/markedAs/test",
                props.getProperty("username"),
                props.getProperty("password"));

		// REST-Anfrage abschicken und RestMessage-Objekt erhalten
		RestMessage msgResponse = restclient.sendGetRestMessage();

		// Daten aus dem RestMessage-Objekt entnehmen und benutzen
		for(RestEntrySet entrySet : msgResponse.getListEntrySets()) {
			Iterator<String> it = entrySet.getKeyIterator();

			while(it.hasNext()) {
				String key = it.next();
				String oid = entrySet.getValue(key);
				
//				REST-Anfrage auf Keyword mittels RestClient-Objekt formulieren
				restclient = RestClient.createRestClient(props.getProperty("host"),
		                "InternalMetadataEntry/" + oid,
		                props.getProperty("username"),
		                props.getProperty("password"));
				
//				REST-Anfrage abschicken und RestMessage-Objekt erhalten
				RestMessage msgIMFResponse = restclient.sendGetRestMessage();
				
				System.out.println("OID: " + oid + " -- " + msgIMFResponse.getStatus());				
			}
		}
		
	}

}
