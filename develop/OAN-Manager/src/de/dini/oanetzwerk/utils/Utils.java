package de.dini.oanetzwerk.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * @author Sammy David
 * sammy.david@cms.hu-berlin.de
 * 
 */
public class Utils {

	private static SimpleDateFormat germanTimestampFormat 	= new SimpleDateFormat("dd.MM.yyyy HH:mm:SS");
	private static SimpleDateFormat englishTimestampFormat 	= new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
	private static SimpleDateFormat germanDateFormat 	= new SimpleDateFormat("dd.MM.yyyy");
	private static SimpleDateFormat englishDateFormat 	= new SimpleDateFormat("yyyy-MM-dd");
	
	private final static String WEBAPP_DIR 			= "/webapps";
	private final static String DEFAULT_CONTEXT 	= "OAN-Manager";
	
	
	public static void loadResource() {
		
	}
	
	public static String getWebappPath()
	{
		return WEBAPP_DIR + DEFAULT_CONTEXT;
	}

	public static String getDefaultContext() {
    	return DEFAULT_CONTEXT;
    }
	
	
	public static String millisToUserReadableTime(long millis, Locale locale) {
		
		if (millis < 0) {
			return "";
		}
		
		if (millis < 1000) {
			return "1 sec";
		}
		
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        
        boolean isGerman = locale != null && Locale.GERMAN.equals(locale);
        
        StringBuilder sb = new StringBuilder(64);
        if (days > 0) {
        	sb.append(days);
        	sb.append(isGerman ? " Tage " : " days ");
        }
        if (days > 0 || hours > 0) {
        	sb.append(hours);
        	sb.append("h ");
        }
        sb.append(minutes);
        sb.append("min ");
        sb.append(seconds);
        sb.append("s ");

        return(sb.toString());

	}
	
	public static String localizedTimestamp(Date date, Locale locale) {
		
		if (date == null) {
			return "";
		}
		
		String timestamp;
		
		if (locale == null) {
			return englishTimestampFormat.format(date);
		} else if (locale.GERMAN.equals(locale)) {
			return germanTimestampFormat.format(date);
		} 
		
		return englishTimestampFormat.format(date);
	}
	
	public static String bold(String text, Boolean show) {

		return show ? "<b>" + text + "</b>" : text;
	}
}
