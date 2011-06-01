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
import java.net.URL;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.servicemodule.RestClient;
import de.dini.oanetzwerk.utils.exceptions.ServiceIDException;
import de.dini.oanetzwerk.utils.exceptions.ValueFromKeyException;

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
		
//		if (logger.isDebugEnabled ( ))
			logger.info (new File (file).getAbsoluteFile ( ));
			System.out.println("out");
			System.out.println(new File (file).getAbsoluteFile ( ));
			System.out.println(file);
		props.loadFromXML (new FileInputStream (file));
		
		return props;
	}
	
	public static Properties loadPropertiesFromFileWithinWebcontainer (String file) throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
		
		Properties props = new Properties ( );
		
		if (logger.isDebugEnabled ( ))
			logger.debug (new File (file).getAbsoluteFile ( ));
			
		props.loadFromXML (new FileInputStream (System.getProperty("catalina.home") + System.getProperty("file.separator") + file));
		
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
	 * This method returns the todays date minus 2 weeks.
	 * 
	 * @return the date two weeks before today as {@link Date}
	 */
	
	public static Date twoWeeksBefore ( ) {
		
		Calendar cal = new GregorianCalendar ( );
		
		int day = cal.get (Calendar.DAY_OF_YEAR) - 14;
		
		if (day < 1) {
			
			day += 255;
			cal.set (Calendar.YEAR, cal.get (Calendar.YEAR) - 1);
		}
		
		cal.set (Calendar.DAY_OF_YEAR, day);
		
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
	
	/**
	 * @param javaDate
	 * @return
	 * @throws ParseException
	 */
	
	public static java.sql.Date java2sqlDate (java.util.Date javaDate) throws ParseException {		
		java.sql.Date sqlDate = new Date(javaDate.getTime());
		return sqlDate;
	}

	/**
	 * @param sqlDate
	 * @return
	 * @throws ParseException
	 */
	
	public static java.util.Date sql2javaDate (java.sql.Date sqlDate) throws ParseException {		
		java.util.Date javaDate = new java.util.Date(sqlDate.getTime());
		return javaDate;
	}
	
	/**
	 * @param dateString
	 * @return
	 * @throws ParseException
	 */
	
	public static java.util.Date extract_java_datestamp (String dateString) throws ParseException {
		java.util.Date sdf = new SimpleDateFormat ("yyyy-MM-dd").parse (dateString);
		return sdf;
	}
	

	public static BigDecimal getBigDecimalFromCmdLine (String optionValue) throws NumberFormatException {
		
		BigDecimal result = new BigDecimal (optionValue);
		
		return result;
	}
	
	/**
	 * This method decodes a RestMessage and extracts the value for a given key.
	 * 
	 * @param result the encoded RestMessage
	 * @param keyword the key for the value
	 * @return the extracted value
	 * @throws ValueFromKeyException 
	 */
	
	public static String getValueFromKey (RestMessage response, String keyword) throws ValueFromKeyException {

		if (response == null) {
			
			logger.error ("Received RestMessage empty");
			throw new ValueFromKeyException ("Received RestMessage empty");
		}
		
		if (response.getStatus ( ) != RestStatusEnum.OK) {
			
			if (response.getStatus ( ) == RestStatusEnum.NO_OBJECT_FOUND_ERROR) {
				
				logger.info ("Object does not exist, we'll care about this");
				throw new ValueFromKeyException ("Object does not exist, we'll care about this");
				
			} else {
				
				logger.error ("Rest-Error occured: " + response.getStatusDescription ( ));
				throw new ValueFromKeyException ("Rest-Error occured: " + response.getStatusDescription ( ));
			}
			
		} else /*status ok*/ {
			
		   if (response.getListEntrySets ( ).isEmpty ( )) {
			   
				logger.error ("Received RestMessage with RestStatusEnum.OK and no entryset to evaluate");
				throw new ValueFromKeyException ("Received RestMessage with RestStatusEnum.OK and no entryset to evaluate");
		   }
		}
		
		RestEntrySet res = response.getListEntrySets ( ).get (0);
		
		Iterator <String> it = res.getKeyIterator ( );
		String key = "";
		String value = null;
		
		while (it.hasNext ( )) {
			
			key = it.next ( );
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("key: " + key + " value: " + res.getValue (key));
			
			if (key.equalsIgnoreCase (keyword)) {
				
				value = res.getValue (key);
				break;
			}
		}
		
		return value;
	}
	
	/**
	 * @param service
	 * @param restclientprops
	 * @return
	 * @throws ServiceIDException
	 */
	
	public static BigDecimal getServiceId (String service, Properties restclientprops) throws ServiceIDException {
		
		RestMessage getServicesResponse = HelperMethods.prepareRestTransmission ("Services/byName/" + service + "/", restclientprops).sendGetRestMessage ( );
		
		if (getServicesResponse == null || getServicesResponse.getListEntrySets ( ).isEmpty ( ) || getServicesResponse.getStatus ( ) != RestStatusEnum.OK) {
			
			String description = RestStatusEnum.UNKNOWN_ERROR.toString ( );
			
			if (getServicesResponse != null)
				description = getServicesResponse.getStatusDescription ( );
			
			if (getServicesResponse == null || getServicesResponse.getStatus ( ) != RestStatusEnum.SQL_WARNING) {
				
				logger.error ("Could NOT get " + service + "-Service-ID from database! " + description);
				throw new ServiceIDException ("Could NOT get " + service + "-Service-ID from database! Cause: " + description);
				
			} else {
				
				logger.warn ("SQL_WARNING: " + description);
				throw new ServiceIDException ("SQL_WARNING: " + description);
			}
		}
		
		RestEntrySet res = getServicesResponse.getListEntrySets ( ).get (0);
		
		Iterator <String> it = res.getKeyIterator ( );
		String key = "";
		
		BigDecimal thisServiceID = new BigDecimal (0);
		
		while (it.hasNext ( )) {
			
			key = it.next ( );
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("key: " + key + " value: " + res.getValue (key));
			
			if (key.equalsIgnoreCase ("service_id")) {
				
				thisServiceID = new BigDecimal (res.getValue (key));
				continue;
				
			} else if (key.equalsIgnoreCase ("name")) {
				
				if (!res.getValue (key).equalsIgnoreCase ("Harvester"))
					logger.warn ("Should read Harvester and read " + res.getValue (key) + " instead!");
				
				continue;
				
			} else {
				
				logger.warn ("Unknown RestMessage key received: " + res.getValue (key));
				
				continue;
			}
		}
		
		return thisServiceID;
	}
	
	/**
	 * @param string
	 * @return
	 */
	
	public static RestClient prepareRestTransmission (String resource, Properties properties) {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("prepareRestTransmission");
		
		return RestClient.createRestClient (properties.getProperty ("host"), resource, properties.getProperty ("username"), properties.getProperty ("password"));
	}
	
	public static String getRestFailureMessage(final RestKeyword keyword, final RestStatusEnum status,  final String description) {
		
		logger.error (description);
		
		final RestMessage rms = new RestMessage (keyword);
		rms.setStatus (status);
		rms.setStatusDescription (description);
		
		return RestXmlCodec.encodeRestMessage (rms);
	}
	
	
	/**
	 * returns a date set the first day of the preceeding month relative
	 * to a given date (which can be "now" by setting it to new Date())
	 * 
	 * @param fromDate
	 * @return
	 */
	public static java.util.Date getFirstDayOfPreceedingMonth(java.util.Date relativeToDate) {
		java.util.Date dateToReturn = new java.util.Date();
		GregorianCalendar gc = new GregorianCalendar();
		
		gc.setTime(relativeToDate);
		gc.add(Calendar.MONTH, -1);
		gc.set(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH), 1);
		
		dateToReturn = gc.getTime();
		
		return dateToReturn; 
	}
	
	/**
	 * returns a date set the first day of the given month relative
	 * to a given date (which can be "now" by setting it to new Date())
	 * 
	 * @param fromDate
	 * @return
	 */
	public static java.util.Date getFirstDayOfGivenMonth(java.util.Date relativeToDate) {
		java.util.Date dateToReturn = new java.util.Date();
		GregorianCalendar gc = new GregorianCalendar();
		
		gc.setTime(relativeToDate);
		gc.set(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH), 1);
		
		dateToReturn = gc.getTime();
		
		return dateToReturn; 
	}
}
