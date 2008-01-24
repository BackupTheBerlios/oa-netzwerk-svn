/**
 * 
 */

package de.dini.oanetzwerk.utils;

import java.io.*;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
	
	public static String stream2String (InputStream stream) throws IOException {
		
		BufferedReader in = new BufferedReader (new InputStreamReader (stream));
		
		StringBuilder sb = new StringBuilder ( );
		String line = null;
		
		try {
			
			while ((line = in.readLine ( )) != null)				
				sb.append (line + "\n");
			
		} catch (IOException ex) {
			
			throw ex;
			
		} finally {
			
			try {
				
				in.close ( );
				
			} catch (IOException ex) {
				
				throw ex;
			}
		}
		
		return sb.toString ( );
	}
	
	public static Properties loadPropertiesFromFile (String file) throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
		
		Properties props = new Properties ( );
			
		props.loadFromXML (new FileInputStream (file));
		
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
	
	public static Date extract_repository_datestamp (String dateString) throws ParseException {
		
		Date date = null;
		//date = new Date (new SimpleDateFormat ("yyyy-MM-dd").parse (parseXML (data, "repository_datestamp")).getTime ( ));			
		
		//String dateString = parseXML (data, "repository_datestamp");
		
		java.util.Date sdf = new SimpleDateFormat ("yyyy-MM-dd").parse (dateString);
		
		date = new Date (sdf.getTime ( ));
		
		return date;
	}
}
