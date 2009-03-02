package de.dini.oanetzwerk.utils;

import java.util.HashMap;
import java.util.Locale;

/**
 * Methoden, um den String eines Language-Metadatums zu einer Locale mit ISO639-3-Kennung umzuwandeln
 * 
 * @author malitzro
 *
 */
public class ISO639Normalizer {

	private static HashMap<String,Locale> mapISO639_3 = new HashMap<String, Locale>();
	
	public static Locale getLocaleFromDisplayName(String displayName) {
		for(String iso639_1 : Locale.getISOLanguages()) {
			Locale tmpLocale = new Locale(iso639_1);
			for(Locale availabelLocale : Locale.getAvailableLocales()) {
			  if(tmpLocale.getDisplayLanguage(availabelLocale).equalsIgnoreCase(displayName)) {				
				return tmpLocale;				
			  }
			}
		}
		return null;
	}
		
	public static Locale get_ISO639_3(String input) {
		if(mapISO639_3.containsKey(input)) {
			return mapISO639_3.get(input);
		} else {
			Locale result = get_ISO639_3_unbuffered(input);
			mapISO639_3.put(input, result);
			return result;
		}	    
	}
	
	public static Locale get_ISO639_3_unbuffered(String input) {
		
		// Null ignorieren
		if(input == null) return null;
				
		// teste auf ISO639_3
		for(String iso639_1 : Locale.getISOLanguages()) {
			Locale tmpLocale = new Locale(iso639_1);
			if(tmpLocale.getISO3Language().equalsIgnoreCase(toISO639_3T(input))) return tmpLocale;
		}
		
		// teste auf ISO639_2 bzw. Java-Locale konforme String-Repr�sentation
		if(input.length() == 2 || (input.length() > 2 && (input.charAt(2) == '_' || input.charAt(2) == ' '))) {
			Locale localeTestJavaISO = new Locale(input.substring(0,2));
			String isolang = null;
			try {isolang = localeTestJavaISO.getISO3Language();} catch(Exception ex) {}
			if(isolang != null) return localeTestJavaISO;
		}
		
		// teste auf Klartext-Benennung
		Locale localeTestDisplay = getLocaleFromDisplayName(input);
		if(localeTestDisplay != null) return localeTestDisplay;
				
		return null;
	}
	
	/**
	 * When two codes are provided for the same language, they are to be considered synonyms. 
	 * One is from the code set for terminology applications (ISO 639-2/T) and the other 
	 * is from the code set for bibliographic applications (ISO 639-2/B).
	 * 
	 * @param isolang
	 * @return
	 */
	public static String toISO639_3B(String isolang) {
		if(isolang == null) return isolang;
		if(isolang.equalsIgnoreCase("sqi")) return "alb";
		if(isolang.equalsIgnoreCase("hye")) return "arm";
		if(isolang.equalsIgnoreCase("eus")) return "baq";
		if(isolang.equalsIgnoreCase("bod")) return "tib";
		if(isolang.equalsIgnoreCase("mya")) return "bur";
		if(isolang.equalsIgnoreCase("ces")) return "cze";
		if(isolang.equalsIgnoreCase("zho")) return "chi";
		if(isolang.equalsIgnoreCase("cym")) return "wel";
		if(isolang.equalsIgnoreCase("deu")) return "ger";
		if(isolang.equalsIgnoreCase("nld")) return "dut";
		if(isolang.equalsIgnoreCase("ell")) return "gre";
		if(isolang.equalsIgnoreCase("fas")) return "per";
		if(isolang.equalsIgnoreCase("fra")) return "fre";
		if(isolang.equalsIgnoreCase("kat")) return "geo";
		if(isolang.equalsIgnoreCase("hrv")) return "scr";
		if(isolang.equalsIgnoreCase("isl")) return "ice";
		if(isolang.equalsIgnoreCase("mkd")) return "mac";
		if(isolang.equalsIgnoreCase("msa")) return "may";
		if(isolang.equalsIgnoreCase("mri")) return "mao";
		if(isolang.equalsIgnoreCase("deu")) return "ger";
		if(isolang.equalsIgnoreCase("slk")) return "slo";
		if(isolang.equalsIgnoreCase("srp")) return "scc";
		return isolang;
	}
	
	/**
	 * When two codes are provided for the same language, they are to be considered synonyms. 
	 * One is from the code set for terminology applications (ISO 639-2/T) and the other 
	 * is from the code set for bibliographic applications (ISO 639-2/B).
	 * 
	 * @param isolang
	 * @return
	 */
	public static String toISO639_3T(String isolang) {
		if(isolang == null) return isolang;
		if(isolang.equalsIgnoreCase("alb")) return "sqi";
		if(isolang.equalsIgnoreCase("arm")) return "hye";
		if(isolang.equalsIgnoreCase("baq")) return "eus";
		if(isolang.equalsIgnoreCase("tib")) return "bod";
		if(isolang.equalsIgnoreCase("bur")) return "mya";
		if(isolang.equalsIgnoreCase("cze")) return "ces";
		if(isolang.equalsIgnoreCase("chi")) return "zho";
		if(isolang.equalsIgnoreCase("wel")) return "cym";
		if(isolang.equalsIgnoreCase("ger")) return "deu";
		if(isolang.equalsIgnoreCase("dut")) return "nld";
		if(isolang.equalsIgnoreCase("gre")) return "ell";
		if(isolang.equalsIgnoreCase("per")) return "fas";
		if(isolang.equalsIgnoreCase("fre")) return "fra";
		if(isolang.equalsIgnoreCase("geo")) return "kat";
		if(isolang.equalsIgnoreCase("scr")) return "hrv";
		if(isolang.equalsIgnoreCase("ice")) return "isl";
		if(isolang.equalsIgnoreCase("mac")) return "mkd";
		if(isolang.equalsIgnoreCase("may")) return "msa";
		if(isolang.equalsIgnoreCase("mao")) return "mri";
		if(isolang.equalsIgnoreCase("ger")) return "deu";
		if(isolang.equalsIgnoreCase("slo")) return "slk";
		if(isolang.equalsIgnoreCase("scc")) return "srp";
		return isolang;
	}
	
	public static String wrapDoubleISO(String isolang) {
		if(isolang == null) return isolang;
		if(isolang.equalsIgnoreCase("sqi") || isolang.equalsIgnoreCase("alb")) return "sqi/alb";
		if(isolang.equalsIgnoreCase("hye") || isolang.equalsIgnoreCase("arm")) return "hye/arm";
		if(isolang.equalsIgnoreCase("eus") || isolang.equalsIgnoreCase("baq")) return "eus/baq";
		if(isolang.equalsIgnoreCase("bod") || isolang.equalsIgnoreCase("tib")) return "bod/tib";
		if(isolang.equalsIgnoreCase("mya") || isolang.equalsIgnoreCase("bur")) return "mya/bur";
		if(isolang.equalsIgnoreCase("ces") || isolang.equalsIgnoreCase("cze")) return "ces/cze";
		if(isolang.equalsIgnoreCase("zho") || isolang.equalsIgnoreCase("chi")) return "zho/chi";
		if(isolang.equalsIgnoreCase("cym") || isolang.equalsIgnoreCase("wel")) return "cym/wel";
		if(isolang.equalsIgnoreCase("deu") || isolang.equalsIgnoreCase("ger")) return "deu/ger";
		if(isolang.equalsIgnoreCase("nld") || isolang.equalsIgnoreCase("dut")) return "nld/dut";
		if(isolang.equalsIgnoreCase("ell") || isolang.equalsIgnoreCase("gre")) return "ell/gre";
		if(isolang.equalsIgnoreCase("fas") || isolang.equalsIgnoreCase("per")) return "fas/per";
		if(isolang.equalsIgnoreCase("fra") || isolang.equalsIgnoreCase("fre")) return "fra/fre";
		if(isolang.equalsIgnoreCase("kat") || isolang.equalsIgnoreCase("geo")) return "kat/geo";
		if(isolang.equalsIgnoreCase("hrv") || isolang.equalsIgnoreCase("scr")) return "hrv/scr";
		if(isolang.equalsIgnoreCase("isl") || isolang.equalsIgnoreCase("ice")) return "isl/ice";
		if(isolang.equalsIgnoreCase("mkd") || isolang.equalsIgnoreCase("mac")) return "mkd/mac";
		if(isolang.equalsIgnoreCase("msa") || isolang.equalsIgnoreCase("may")) return "mas/may";
		if(isolang.equalsIgnoreCase("mri") || isolang.equalsIgnoreCase("mao")) return "mri/mao";
		if(isolang.equalsIgnoreCase("deu") || isolang.equalsIgnoreCase("ger")) return "deu/ger";
		if(isolang.equalsIgnoreCase("slk") || isolang.equalsIgnoreCase("slo")) return "slk/slo";
		if(isolang.equalsIgnoreCase("srp") || isolang.equalsIgnoreCase("scc")) return "srp/scc";
		return isolang;
	}
	
}
