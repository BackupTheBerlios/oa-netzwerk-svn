package de.dini.oanetzwerk.oaipmh;

import org.apache.commons.lang.StringUtils;

public class DriverCompliance {

	
	
	public static String getAuthor(String firstName, String lastName) {
		
		String[] firstNames;
		String firstNameInitials = "";
		String firstNameFull = " (";
		
		if (!StringUtils.isEmpty(firstName)) {
			
			firstNames = firstName.split(" ");
			
			int i = 0;
			for (String fn : firstNames) {
				i++;
				firstNameInitials += fn.charAt(0) + ".";
				firstNameFull += i > 1 ? ", " + fn : fn;
			}
			
			firstNameFull += ")";
		}
		return lastName + (!StringUtils.isEmpty(firstName) ? ", " + firstNameInitials + firstNameFull : "");
	}
}
