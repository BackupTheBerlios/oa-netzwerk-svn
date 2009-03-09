package de.dini.oanetzwerk.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ISO8601DateNormalizer {

	public static final SimpleDateFormat sdf_out = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
	public static final SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	public static final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	public static final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy");
	
	/**
	 * extrahiert java.util.Date aus UTC-kodiertem String
	 * 
	 * @param strDate
	 * @return
	 */
	public static Date getDateFromUTCString(String strDate) {
		Date date = null;
		if(date == null) try {date = sdf0.parse(strDate);} catch(ParseException pex) {}
		if(date == null) try {date = sdf1.parse(strDate);} catch(ParseException pex) {}
		if(date == null) try {date = sdf2.parse(strDate);} catch(ParseException pex) {}
		if(date == null) try {date = sdf3.parse(strDate);} catch(ParseException pex) {}		
		return date;
	}
	
	public static String getUTCStringFromData(Date date) {
		return sdf_out.format(date);
	}
	
}
