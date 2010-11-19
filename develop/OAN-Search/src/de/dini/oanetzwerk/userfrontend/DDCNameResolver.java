package de.dini.oanetzwerk.userfrontend;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.utils.HelperMethods;

public class DDCNameResolver {

	private static Logger logger = Logger.getLogger (BrowseBean.class);
	private static Properties propNames_de = null;

	public DDCNameResolver() throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
		try {
		  this.propNames_de = HelperMethods.loadPropertiesFromFileWithinWebcontainer (Utils.getWebappPath() + "/WEB-INF/ddc_names_de.xml");
		} catch(Exception ex) {
  		  this.propNames_de = HelperMethods.loadPropertiesFromFile ("ddc_names_de.xml");
		}
	}
	
	public static String getCategoryName(String strDDCNumber, String strLang) {
		return (String)propNames_de.get(strDDCNumber);
	}
	
	public static List<String> getListDDCNumbers() {
		List<String> listDDCNumbers = new ArrayList<String>(); 
		Set<Object> set = propNames_de.keySet();
		Iterator<Object> it = set.iterator();
		while(it.hasNext()) {
			String strNumber = (String)it.next();
			listDDCNumbers.add(strNumber);
		}
		return listDDCNumbers;
	}
}
