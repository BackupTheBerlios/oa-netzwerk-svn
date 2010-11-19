package de.dini.oanetzwerk.servicemodule.examples;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.server.handler.CompleteMetadataEntry;
import de.dini.oanetzwerk.servicemodule.RestClient;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.imf.Author;
import de.dini.oanetzwerk.utils.imf.CompleteMetadata;
import de.dini.oanetzwerk.utils.imf.CompleteMetadataJAXBMarshaller;
import de.dini.oanetzwerk.utils.imf.Title;

public class OpenRestClientExample {

	private static Properties props;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String objectID = "";
        String strXML = "";
		
		if(args.length != 1) {			
			System.out.println("object id as parameter expected");
			System.exit(-1);
		}
		
		objectID = args[0];
	
		
		try {
			props = HelperMethods.loadPropertiesFromFile("openrestclientprop.xml");
		} catch(IOException ioex) {
			System.out.println(ioex);
			System.exit(-1);
		}
			
		// REST-Anfrage auf Keyword mittels RestClient-Objekt formulieren
		RestClient restclient = RestClient.createRestClient(props.getProperty("host"),
				                                            "CompleteMetadataEntry/" + objectID,
				                                            props.getProperty("username"),
				                                            props.getProperty("password"));

		// REST-Anfrage abschicken und RestMessage-Objekt erhalten
		RestMessage msgResponse = restclient.sendGetRestMessage();

		// Status abfragen
		if(msgResponse.getStatus() != RestStatusEnum.OK) {
			System.out.println("Die Abfrage liefert folgenden Fehler:");
			System.out.println(msgResponse.getStatus());
			System.out.println(msgResponse.getStatusDescription());
			System.exit(-1);
		}
		
		// Daten aus dem RestMessage-Objekt entnehmen und benutzen
		for(RestEntrySet entrySet : msgResponse.getListEntrySets()) {
			Iterator<String> it = entrySet.getKeyIterator();
			while(it.hasNext()) {
				String key = it.next();
				if("completemetadata".equalsIgnoreCase(key)) strXML = entrySet.getValue(key);
			}
		}
		
		System.out.println("XML:");
		System.out.println(strXML);

		// so bekommt man eine Objekt-Struktur aus dem XML		
		CompleteMetadataJAXBMarshaller marshaller = CompleteMetadataJAXBMarshaller.getInstance();
		CompleteMetadata completeMetadata = marshaller.unmarshall(strXML);
		
		System.out.println("aus dem Objekt:");
	
		for(Title title : completeMetadata.getTitleList()) {
			System.out.println(title.getTitle());
		}
		for(Author author : completeMetadata.getAuthorList()) {
			System.out.println(author.getFirstname() + " " + author.getLastname());
		}
	}

}
