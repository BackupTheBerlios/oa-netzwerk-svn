package de.dini.oanetzwerk.userfrontend;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;

import org.apache.log4j.Logger;

public class MetadataLoaderBean {
	
	private static Logger logger = Logger.getLogger (MetadataLoaderBean.class);
	
	private CachingMetadataHashMap mapCompleteMetadata;
	
	public MetadataLoaderBean() throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
		logger.debug("MetadataLoaderBean instance initialised");		
		mapCompleteMetadata = new CachingMetadataHashMap();
	}

	public CachingMetadataHashMap getMapCompleteMetadata() {
		return mapCompleteMetadata;
	}

	public void setMapCompleteMetadata(CachingMetadataHashMap mapCompleteMetadata) {
		this.mapCompleteMetadata = mapCompleteMetadata;
	}
	
}
