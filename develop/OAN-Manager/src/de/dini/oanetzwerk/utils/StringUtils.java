package de.dini.oanetzwerk.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

	
	public static String getPrettyNameFromCamelCase(String camelCase) {
		return camelCase.replaceAll(
						String.format("%s|%s|%s",
					    			         "(?<=[A-Z])(?=[A-Z][a-z])",
					    			         "(?<=[^A-Z])(?=[A-Z])",
					    			         "(?<=[A-Za-z])(?=[^A-Za-z])"
					    			      ), " ");
	}
	
	// Pattern to spot placeholders like ${variable}
	private final static Pattern p = Pattern.compile("\\$\\{(.*?)\\}");
	
	// finds and resolves the placeholders via System.getenv(variable)
	public static String resolveSystemVariable(String text) {
		
		
		if (text == null || text.length() == 0) {
			return text;
		}
		
		Matcher m = p.matcher(text);
		String resolvedPath = "";
		
		if (m.find()) {	
			String variable = m.group();
			int length = variable.length();

			variable = variable.substring(2, variable.length()-1);
			
//			if (logger.isDebugEnabled) {
//			logger.debug("Found variable usage in services.properties: " + variable + ". Resolving variable...");
//				if (System.getenv(variable) != null) {
//					logger.debug("Variable successfully resolved! (" + variable + ")");
//				} else {
//					logger.debug("Variable could not be resolved! (" + variable + ")");
//				}
//			}
			
			if (m.start() == 0) {
				resolvedPath = System.getenv(variable) + text.substring(length);				
			} else {
				resolvedPath = text.substring(0, m.start()) + System.getenv(variable) + text.substring(m.end());	
			}
		} else {
			return text;
		}
		
		return resolvedPath;
	}
}
