package de.dini.oanetzwerk.utils;

import java.util.Locale;

//import sun.security.action.GetLongAction;


public class Test_ISO639LangNormalizer {
	
	
	private static String normalizeLang_ISO639_3(String input) {
		Locale localeISO = ISO639LangNormalizer.get_ISO639_3(input);
		if(localeISO != null) return localeISO.getDisplayLanguage(Locale.GERMAN) + " / " + localeISO.getDisplayLanguage(Locale.ENGLISH) +  " [ISO639-3:"+ISO639LangNormalizer.wrapDoubleISO(localeISO.getISO3Language())+"]";
		return input;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String[] inputs = new String[] {
				"eng","ger","lat","fre","spa","swe","por","ita","English",
				"German","mul","French","Polish","mis","Netherlands","Japanese",
				"esp","rus","de","GER","en","other","es","fr","en_US","ENG","en (US)","it"};
		
		System.out.println("unbuffered at first call");
		for(String lang : inputs) {
			System.out.println("'" + lang + "' --> '" + normalizeLang_ISO639_3(lang) + "'");
		}

		System.out.println("buffered afterwards");
		for(String lang : inputs) {
			System.out.println("'" + lang + "' --> '" + normalizeLang_ISO639_3(lang) + "'");
		}		
		
		System.out.println(ISO639LangNormalizer.getLocaleFromISO639_3("eng"));
		System.out.println(ISO639LangNormalizer.getLocaleFromISO639_3("eng").getDisplayName());
	}

}
