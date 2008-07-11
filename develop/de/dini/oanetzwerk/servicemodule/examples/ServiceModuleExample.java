package de.dini.oanetzwerk.servicemodule.examples;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.servicemodule.RestClient;
import de.dini.oanetzwerk.utils.HelperMethods;

public class ServiceModuleExample {

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
				                                            "RawRecordData/6100",
				                                            props.getProperty("username"),
				                                            props.getProperty("password"));

		// REST-Anfrage abschicken und RestMessage-Objekt erhalten
		RestMessage msgResponse = restclient.sendGetRestMessage();

		// Daten aus dem RestMessage-Objekt entnehmen und benutzen
		for(RestEntrySet entrySet : msgResponse.getListEntrySets()) {
			Iterator<String> it = entrySet.getKeyIterator();
			System.out.println("--EntrySet--");
			while(it.hasNext()) {
				String key = it.next();
				System.out.println("key = " + key);
				System.out.println("value = " + entrySet.getValue(key));
			}
		}
		
	}

}
