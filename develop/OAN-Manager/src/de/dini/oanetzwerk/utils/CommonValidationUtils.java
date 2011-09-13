package de.dini.oanetzwerk.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonValidationUtils {

	
	private static final String regex = "^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
	private static final String emailRegex = "^[\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[a-zA-Z]{2,4}$"; 
	
	

	public static boolean isValidUrl(String url) {

		Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);

		if (!matcher.matches() || url.length() < 10) {
			return false;
		}
		
		return true;
    }
	
	public static boolean isValidEmail(String email) {
		
		Pattern p = Pattern.compile(emailRegex);
        Matcher m = p.matcher(email);

		if (!m.matches()) {
			return false;
		}
		
		return true;
	}
}
