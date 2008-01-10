/**
 * 
 */

package de.dini.oanetzwerk.utils;

import java.io.*;
import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;


/**
 * @author Michael Kühn
 *
 */

public class HelperMethods {
	
	public static String stream2String (InputStream stream) {
		
		BufferedReader in = new BufferedReader (new InputStreamReader (stream));
		
		StringBuilder sb = new StringBuilder ( );
		String line = null;
		
		try {
			
			while ((line = in.readLine ( )) != null)				
				sb.append (line + "\n");
			
		} catch (IOException ex) {
			
			ex.printStackTrace ( );
			
		} finally {
			
			try {
				
				in.close ( );
				
			} catch (IOException ex) {
				
				ex.printStackTrace ( );
			}
		}
		
		return sb.toString ( );
	}
	
	public static Properties loadPropertiesFromFile (String file) {
		
		Properties props = new Properties ( );
		
		try {
			
			props.loadFromXML (new FileInputStream (file));
			
		} catch (InvalidPropertiesFormatException ex) {
			
			ex.printStackTrace ( );
			
		} catch (FileNotFoundException ex) {
			
			ex.printStackTrace ( );
			
		} catch (IOException ex) {
			
			ex.printStackTrace ( );
		}
		
		return props;
	}

	/**
	 * @return
	 */
	
	public static Date today ( ) {
		
		Calendar cal = new GregorianCalendar ( );
		
		return new Date (cal.getTimeInMillis ( ));
	}

	/**
	 * @param options
	 */
	
	public static void printhelp (String syntax, Options options) {

		HelpFormatter formatter = new HelpFormatter ( );
		formatter.printHelp (syntax, options, true);
	}
}