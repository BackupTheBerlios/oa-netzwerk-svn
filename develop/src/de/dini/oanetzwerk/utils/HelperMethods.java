/**
 * 
 */

package de.dini.oanetzwerk.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;

/**
 * @author Michael K&uuml;hn
 *
 */

public class HelperMethods {
	
	private static Logger logger = Logger.getLogger (HelperMethods.class);;
	
	/**
	 * This method converts an InputStream into a String.
	 *
	 * @param stream the stream to be converted
	 * @return the converted stream as String
	 * @throws IOException
	 */
	
	public static String stream2String (InputStream stream) throws IOException {
		
		BufferedReader in = new BufferedReader (new InputStreamReader (stream));
		
		StringBuilder sb = new StringBuilder ( );
		String line = null;
		
		try {
			
			while ((line = in.readLine ( )) != null)				
				//sb.append (line + "\n");
				sb.append (line + "\n");
			
		} catch (IOException ex) {
			
			throw ex;
			
		} finally {
			
			try {
				
				in.close ( );
				in = null;
				line = null;
				stream = null;
				
			} catch (IOException ex) {
				
				throw ex;
			}
		}
		
		return sb.toString ( );
	}
	
	/**
	 * This methods loads Properties from a given file. The properties must be XML.
	 * 
	 * @param file the file name from which the properties are read.
	 * @return the properties read from file
	 * @throws InvalidPropertiesFormatException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	
	public static Properties loadPropertiesFromFile (String file) throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
		
		Properties props = new Properties ( );
		
		if (logger.isDebugEnabled ( ))
			logger.debug (new File (file).getAbsoluteFile ( ));
		
		props.loadFromXML (new FileInputStream (file));
		
		return props;
	}

	/**
	 * This method returns the todays date.
	 * 
	 * @return the date as {@link Date}
	 */
	
	public static Date today ( ) {
		
		Calendar cal = new GregorianCalendar ( );
		
		return new Date (cal.getTimeInMillis ( ));
	}

	/**
	 * This method handles the printing of the help for the given syntax and options.
	 * 
	 * @param syntax the command line syntax
	 * @param options the options instance {@link Options}
	 */
	
	public static void printhelp (String syntax, Options options) {

		HelpFormatter formatter = new HelpFormatter ( );
		formatter.printHelp (syntax, options, true);
	}
	
	/**
	 * This method extracts the datestamp from a string. The datestamp within the dateString 
	 * MUST have the following format: yyyy-MM-dd
	 * 
	 * @param dateString the String from which the date will be extracted
	 * @return the extracted {@link Date}
	 * @throws ParseException
	 */
	
	public static Date extract_datestamp (String dateString) throws ParseException {
		
		Date date = null;
		
		java.util.Date sdf = new SimpleDateFormat ("yyyy-MM-dd").parse (dateString);
		
		date = new Date (sdf.getTime ( ));
		
		sdf = null;
		
		return date;
	}
	
	public static java.sql.Date java2sqlDate (java.util.Date javaDate) throws ParseException {		
		java.sql.Date sqlDate = new Date(javaDate.getTime());
		return sqlDate;
	}

	public static java.util.Date sql2javaDate (java.sql.Date sqlDate) throws ParseException {		
		java.util.Date javaDate = new java.util.Date(sqlDate.getTime());
		return javaDate;
	}
	
	public static java.util.Date extract_java_datestamp (String dateString) throws ParseException {
		java.util.Date sdf = new SimpleDateFormat ("yyyy-MM-dd").parse (dateString);
		return sdf;
	}
	

	public static BigDecimal getBigDecimalFromCmdLine (String optionValue) throws NumberFormatException {
		
		BigDecimal result = new BigDecimal (optionValue);
		
		return result;
	}
}
