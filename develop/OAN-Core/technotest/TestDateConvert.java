package technotest;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.dini.oanetzwerk.utils.ISO8601DateNormalizer;

public class TestDateConvert {

	public static void main(String args[]) {
	
		String[] strDates = {"1999-01-23T22:11:33+01:00","1999-01-23T22:11:33Z","1999-01-23","1999"};
		
		for(String strDate : strDates) {
			Date date = ISO8601DateNormalizer.getDateFromUTCString(strDate);
			System.out.println("strDate = " + strDate);
			System.out.println("date = " + date);
			System.out.println("date(formatted) = " + ISO8601DateNormalizer.getUTCStringFromData(date));
		}
		
	}
	
}
