package de.dini.oanetzwerk.utils;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;

public class DDCMatcher_DINI {

	public static String fillUpWithZeros(String rawDDCvalue) {
		String val = "";
		String[] s = rawDDCvalue.split("[.]"); 
		val = StringUtils.leftPad(s[0], 3, "0");
		if(s.length > 1) val += "." + s[1];
		return val;
	}
	
	public static String[] convert(String rawDDCvalue) {
		
		// hier werden alle durch DINI erlaubten Ausnahmen für tiefere Level durchgelassen
		
		if("943".equals(rawDDCvalue)) return new String[] {rawDDCvalue};
		if("914.3".equals(rawDDCvalue)) return new String[] {rawDDCvalue};
		if("839".equals(rawDDCvalue)) return new String[] {rawDDCvalue};
		if("796".equals(rawDDCvalue)) return new String[] {rawDDCvalue};
		if("793".equals(rawDDCvalue)) return new String[] {rawDDCvalue};
		if("792".equals(rawDDCvalue)) return new String[] {rawDDCvalue};
		if("791".equals(rawDDCvalue)) return new String[] {rawDDCvalue};
		if("741.5".equals(rawDDCvalue)) return new String[] {rawDDCvalue};
		if("439".equals(rawDDCvalue)) return new String[] {rawDDCvalue};
		if("355".equals(rawDDCvalue)) return new String[] {rawDDCvalue};
		if("004".equals(rawDDCvalue)) return new String[] {rawDDCvalue};

		String[] s = rawDDCvalue.split("[.]");
		
		if("943".equals(s[0])) return new String[] {s[0]};
		if("914.3".equals(s[0])) return new String[] {s[0]};
		if("839".equals(s[0])) return new String[] {s[0]};
		if("796".equals(s[0])) return new String[] {s[0]};
		if("793".equals(s[0])) return new String[] {s[0]};
		if("792".equals(s[0])) return new String[] {s[0]};
		if("791".equals(s[0])) return new String[] {s[0]};
		if("741.5".equals(s[0])) return new String[] {s[0]};
		if("439".equals(s[0])) return new String[] {s[0]};
		if("355".equals(s[0])) return new String[] {s[0]};
		if("004".equals(s[0])) return new String[] {s[0]};
		
		// die anderen werden auf die höhere Kategorie "aufgerundet"
		String roundedDDC = s[0].substring(0, 2) + "0";
		
		if(roundedDDC.equals(rawDDCvalue)) {
			return new String[] {rawDDCvalue};
		} else {
			return new String[] {roundedDDC, rawDDCvalue};			
		}
			
	}
	
	public static void main(String args[]) {
		
		String[] tests = {"62","4.4","04","004","060","062","914.3","914.7","793.1"};
		
		for(String test: tests) {
			String val = fillUpWithZeros(test);
			System.out.println(test + " --> " + val + " --> " + Arrays.asList(convert(val)));
		}
		
	}
	
}
