package de.dini.oanetzwerk.utils;

import java.util.HashMap;
import java.util.Locale;

/**
 * Methoden, um den String eines Language-Metadatums zu einer Locale mit ISO639-3-Kennung umzuwandeln
 * 
 * @author malitzro
 *
 */
public class ISO639LangNormalizer {

	private static HashMap<String,Locale> mapISO639_3 = new HashMap<String, Locale>();	
	private static HashMap<String,String> mapISO639_3to2 = new HashMap<String,String>();
	
	static {
		mapISO639_3to2.put("aar","aa");
		mapISO639_3to2.put("abk","ab");
		mapISO639_3to2.put("ave","ae");
		mapISO639_3to2.put("afr","af");
		mapISO639_3to2.put("aka","ak");
		mapISO639_3to2.put("amh","am");
		mapISO639_3to2.put("arg","an");
		mapISO639_3to2.put("ara","ar");
		mapISO639_3to2.put("asm","as");
		mapISO639_3to2.put("ava","av");
		mapISO639_3to2.put("aym","ay");
		mapISO639_3to2.put("aze","az");
		mapISO639_3to2.put("bak","ba");
		mapISO639_3to2.put("bel","be");
		mapISO639_3to2.put("bul","bg");
		mapISO639_3to2.put("bih","bh");
		mapISO639_3to2.put("bis","bi");
		mapISO639_3to2.put("bam","bm");
		mapISO639_3to2.put("ben","bn");
		mapISO639_3to2.put("bod","bo");
		mapISO639_3to2.put("bre","br");
		mapISO639_3to2.put("bos","bs");
		mapISO639_3to2.put("cat","ca");
		mapISO639_3to2.put("che","ce");
		mapISO639_3to2.put("cha","ch");
		mapISO639_3to2.put("cos","co");
		mapISO639_3to2.put("cre","cr");
		mapISO639_3to2.put("ces","cs");
		mapISO639_3to2.put("chu","cu");
		mapISO639_3to2.put("chv","cv");
		mapISO639_3to2.put("cym","cy");
		mapISO639_3to2.put("dan","da");
		mapISO639_3to2.put("deu","de");
		mapISO639_3to2.put("div","dv");
		mapISO639_3to2.put("dzo","dz");
		mapISO639_3to2.put("ewe","ee");
		mapISO639_3to2.put("ell","el");
		mapISO639_3to2.put("eng","en");
		mapISO639_3to2.put("epo","eo");
		mapISO639_3to2.put("spa","es");
		mapISO639_3to2.put("est","et");
		mapISO639_3to2.put("eus","eu");
		mapISO639_3to2.put("fas","fa");
		mapISO639_3to2.put("ful","ff");
		mapISO639_3to2.put("fin","fi");
		mapISO639_3to2.put("fij","fj");
		mapISO639_3to2.put("fao","fo");
		mapISO639_3to2.put("fra","fr");
		mapISO639_3to2.put("fry","fy");
		mapISO639_3to2.put("gle","ga");
		mapISO639_3to2.put("gla","gd");
		mapISO639_3to2.put("glg","gl");
		mapISO639_3to2.put("grn","gn");
		mapISO639_3to2.put("guj","gu");
		mapISO639_3to2.put("glv","gv");
		mapISO639_3to2.put("hau","ha");
		mapISO639_3to2.put("heb","he");
		mapISO639_3to2.put("hin","hi");
		mapISO639_3to2.put("hmo","ho");
		mapISO639_3to2.put("hrv","hr");
		mapISO639_3to2.put("hat","ht");
		mapISO639_3to2.put("hun","hu");
		mapISO639_3to2.put("hye","hy");
		mapISO639_3to2.put("her","hz");
		mapISO639_3to2.put("ina","ia");
		mapISO639_3to2.put("ind","id");
		mapISO639_3to2.put("ile","ie");
		mapISO639_3to2.put("ibo","ig");
		mapISO639_3to2.put("iii","ii");
		mapISO639_3to2.put("ipk","ik");
		mapISO639_3to2.put("ido","io");
		mapISO639_3to2.put("isl","is");
		mapISO639_3to2.put("ita","it");
		mapISO639_3to2.put("iku","iu");
		mapISO639_3to2.put("jpn","ja");
		mapISO639_3to2.put("jav","jv");
		mapISO639_3to2.put("kat","ka");
		mapISO639_3to2.put("kon","kg");
		mapISO639_3to2.put("kik","ki");
		mapISO639_3to2.put("kua","kj");
		mapISO639_3to2.put("kaz","kk");
		mapISO639_3to2.put("kal","kl");
		mapISO639_3to2.put("khm","km");
		mapISO639_3to2.put("kan","kn");
		mapISO639_3to2.put("kor","ko");
		mapISO639_3to2.put("kau","kr");
		mapISO639_3to2.put("kas","ks");
		mapISO639_3to2.put("kur","ku");
		mapISO639_3to2.put("kom","kv");
		mapISO639_3to2.put("cor","kw");
		mapISO639_3to2.put("kir","ky");
		mapISO639_3to2.put("lat","la");
		mapISO639_3to2.put("ltz","lb");
		mapISO639_3to2.put("lug","lg");
		mapISO639_3to2.put("lim","li");
		mapISO639_3to2.put("lin","ln");
		mapISO639_3to2.put("lao","lo");
		mapISO639_3to2.put("lit","lt");
		mapISO639_3to2.put("lub","lu");
		mapISO639_3to2.put("lav","lv");
		mapISO639_3to2.put("mlg","mg");
		mapISO639_3to2.put("mah","mh");
		mapISO639_3to2.put("mri","mi");
		mapISO639_3to2.put("mkd","mk");
		mapISO639_3to2.put("mal","ml");
		mapISO639_3to2.put("mon","mn");
		mapISO639_3to2.put("mar","mr");
		mapISO639_3to2.put("msa","ms");
		mapISO639_3to2.put("mlt","mt");
		mapISO639_3to2.put("mya","my");
		mapISO639_3to2.put("nau","na");
		mapISO639_3to2.put("nob","nb");
		mapISO639_3to2.put("nde","nd");
		mapISO639_3to2.put("nep","ne");
		mapISO639_3to2.put("ndo","ng");
		mapISO639_3to2.put("nld","nl");
		mapISO639_3to2.put("nno","nn");
		mapISO639_3to2.put("nor","no");
		mapISO639_3to2.put("nbl","nr");
		mapISO639_3to2.put("nav","nv");
		mapISO639_3to2.put("nya","ny");
		mapISO639_3to2.put("oci","oc");
		mapISO639_3to2.put("oji","oj");
		mapISO639_3to2.put("orm","om");
		mapISO639_3to2.put("ori","or");
		mapISO639_3to2.put("oss","os");
		mapISO639_3to2.put("pan","pa");
		mapISO639_3to2.put("pli","pi");
		mapISO639_3to2.put("pol","pl");
		mapISO639_3to2.put("pus","ps");
		mapISO639_3to2.put("por","pt");
		mapISO639_3to2.put("que","qu");
		mapISO639_3to2.put("roh","rm");
		mapISO639_3to2.put("run","rn");
		mapISO639_3to2.put("ron","ro");
		mapISO639_3to2.put("rus","ru");
		mapISO639_3to2.put("kin","rw");
		mapISO639_3to2.put("san","sa");
		mapISO639_3to2.put("srd","sc");
		mapISO639_3to2.put("snd","sd");
		mapISO639_3to2.put("sme","se");
		mapISO639_3to2.put("sag","sg");
		mapISO639_3to2.put("hbs","sh");
		mapISO639_3to2.put("sin","si");
		mapISO639_3to2.put("slk","sk");
		mapISO639_3to2.put("slv","sl");
		mapISO639_3to2.put("smo","sm");
		mapISO639_3to2.put("sna","sn");
		mapISO639_3to2.put("som","so");
		mapISO639_3to2.put("sqi","sq");
		mapISO639_3to2.put("srp","sr");
		mapISO639_3to2.put("ssw","ss");
		mapISO639_3to2.put("sot","st");
		mapISO639_3to2.put("sun","su");
		mapISO639_3to2.put("swe","sv");
		mapISO639_3to2.put("swa","sw");
		mapISO639_3to2.put("tam","ta");
		mapISO639_3to2.put("tel","te");
		mapISO639_3to2.put("tgk","tg");
		mapISO639_3to2.put("tha","th");
		mapISO639_3to2.put("tir","ti");
		mapISO639_3to2.put("tuk","tk");
		mapISO639_3to2.put("tgl","tl");
		mapISO639_3to2.put("tsn","tn");
		mapISO639_3to2.put("ton","to");
		mapISO639_3to2.put("tur","tr");
		mapISO639_3to2.put("tso","ts");
		mapISO639_3to2.put("tat","tt");
		mapISO639_3to2.put("twi","tw");
		mapISO639_3to2.put("tah","ty");
		mapISO639_3to2.put("uig","ug");
		mapISO639_3to2.put("ukr","uk");
		mapISO639_3to2.put("urd","ur");
		mapISO639_3to2.put("uzb","uz");
		mapISO639_3to2.put("ven","ve");
		mapISO639_3to2.put("vie","vi");
		mapISO639_3to2.put("vol","vo");
		mapISO639_3to2.put("wln","wa");
		mapISO639_3to2.put("wol","wo");
		mapISO639_3to2.put("xho","xh");
		mapISO639_3to2.put("yid","yi");
		mapISO639_3to2.put("yor","yo");
		mapISO639_3to2.put("zha","za");
		mapISO639_3to2.put("zho","zh");
		mapISO639_3to2.put("zul","zu");
	}
	
	public static String getISO639_2fromISO639_3(String iso3) {
		return mapISO639_3to2.get(iso3);
	}

	public static Locale getLocaleFromISO639_3(String iso3) {
		String iso2 = mapISO639_3to2.get(iso3);
		if(iso2 != null) {
	      return new Locale(iso2);	
		} return null;		
	}
	
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
