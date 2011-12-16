package de.dini.oanetzwerk.utils.imf;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.commons.lang.StringUtils;

public abstract class Classification implements ClassificationInterface {
	
	private static String dini2010Pattern = 
		"^doc-type:(" +
			"preprint|" +
			"workingPaper|" +
			"article|" +
			"contributionToPeriodical|" +
			"PeriodicalPart|" +
			"Periodical|" +
			"book|" +
			"bookPart|" +
			"Manuscript|" +
			"StudyThesis|" +
			"bachelorThesis|" +
			"masterThesis|" +
			"doctoralThesis|" +
			"conferenceObject|" +
			"lecture|" +
			"review|" +
			"annotation|" +
			"patent|" +
			"report|" +
			"MusicalNotation|" +
			"Sound|" +
			"Image|" +
			"MovingImage|" +
			"StillImage|" +
			"CourseMaterial|" +
			"Website|" +
			"Software|" +
			"CartographicMaterial|" +
			"ResearchData|" +
			"Other|" +
			"Text" +
		")$";
	String value = "";
	Boolean generated = false;
	
	public static boolean isDDC(String testvalue) {
		if (testvalue.toLowerCase().startsWith("ddc:")) return true;
		else 
			return false;
	}
	
	/**
	 * Prüft ob eine Klassifikation eine DDC Classification sein könnte.
	 * Prüfmuster: \d\d\d(\.\d)?
	 * Es werden nur dreistellige DDC Classifications erkannt, da bei zweistelligen nicht mit Sicherheit gesagt werden kann ob es sich um eine DDC Classification oder um eine DNB Classification handelt.
	 * @param testvalue
	 * @return true wenn es sich um eine dreistellige Zahl (mit eventueller Nachkommastelle) im DDC Format handelt. Dreistellig deshalb weil bei zweistellig unklar ist ob es sich um einen DDC oder um eine DNB Klassifikation handelt (Weil es hier ja um Strings geht, die kein ddc: Präfix besitzen)
	 * 
	 */
	public static boolean isDDCMappable(String testvalue) {
		String valDDC = null;
		Matcher m = Pattern.compile("\\d\\d\\d(\\.\\d)?").matcher(testvalue); 
		if (!m.find()) { return false; }

		valDDC = m.toMatchResult().group();
		if(valDDC != null && valDDC.length() > 0) {
			return true;
		}
		else return false;
	}

	public static boolean isDNB(String testvalue) {
		if (testvalue.toLowerCase().startsWith("dnb:")) return true;
		else 
			return false;
	}
	
	public static boolean isDINISet(String testvalue) {
		Matcher m = Pattern.compile(
			Classification.dini2010Pattern			
		).matcher(testvalue);
		if (!m.find()) { return false; }
		
		String testresult = m.toMatchResult().group();
		if (testresult != null && testresult.length() > 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	
	public static boolean isDINI2010Mappable(String testvalue) {
		return (
			dini2010MappableFromOpus3(testvalue) ||
			dini2010MappableFromOlderDINISpecs(testvalue) ||
			dini2010MappableWithCaseInsensitivity(testvalue)
		);
	}
	
	public static String mapToDINI2010(String testvalue) {
		if (dini2010MappableFromOpus3(testvalue)) {
			return dini2010MapFromOpus3(testvalue);
		}
		if (dini2010MappableWithCaseInsensitivity(testvalue)) {
			return dini2010MapFromCaseInsensitivity(testvalue);
		}
		if (dini2010MappableFromOlderDINISpecs(testvalue)) {
			return dini2010MapFromOlderDINISpecs(testvalue);
		}
		
		return null;
		
	}
	

	/**
	 * Überprüft ob ein String in das Format passt, dass von OPUS 3 zur Klassifikation verwendet wird.
	 * Mangels OPUS3 Dokumentation konnten allerdings nur die Bestandteile eingearbeitet werden, zu denen aus den vorhandenen Repositorien Informationen abrufbar waren.
	 * 
	 * @param testvalue Auf Mappingmöglichkeiten zu überprüfende Klassifikation
	 * @return true falls ein Mapping von OPUS3-Basis in die DINI2010 Klassifikation möglich ist, false sonst
	 */
	private static boolean dini2010MappableFromOpus3(String testvalue) {
		Matcher m = Pattern.compile(
			"pub-type:(" +
			"1|2|4|5|7|8|9|" +
			"11|15|16|17|19|" +
			"20|22|23|24|25|26|27|" +
			"30|31|" +
			"50|51|" +
			"72|77|79|" +
			"171" +
			")").matcher(testvalue);
		if (!m.find()) { return false; }
		String testresult = m.toMatchResult().group();
		if (testresult != null && testresult.length() > 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * Konvertiert einen Publikationstyp vom OPUS3 Vokabular in das DINI2010 Vokabular
	 * @param testvalue String der aus dem OPUS3 Vokabular nach DINI2010 gemappt werden soll
	 * @return String nach DINI2010 oder null falls kein Mapping passt (sollte aber nicht passieren)
	 */
	
	private static String dini2010MapFromOpus3(String testvalue) {
		// Testwert präparieren dass nur noch die Zahl übrigbleibt
		String s = testvalue.replace("pub-type:", "");
		HashMap<String, String> mapping = new HashMap<String, String>();
		
		mapping.put("1", "doc-type:Text");
		mapping.put("2", "doc-type:article"); // laut Kommentar bei der RWTH Aachen nicht peer reviewed
		mapping.put("4", "doc-type:book");
		mapping.put("5", "doc-type:bookPart");
		mapping.put("7", "doc-type:masterThesis");
		mapping.put("8", "doc-type:doctoralThesis");
		mapping.put("9", "doc-type:Text");
		mapping.put("11", "doc-type:article"); // ursprünglich: "Journal", also vermutlich ein Artikel der peer reviewed ist und in einem Wissenschaftlichen Journal publiziert wurde
		mapping.put("15", "doc-type:conferenceObject"); // TODO: klären
		mapping.put("16", "doc-type:conferenceObject"); // TODO: klären (vorallem ob Proceedings/InProceedings trennbar ist)
		mapping.put("17", "doc-type:workingPaper"); // nach "Gemeinsames Vokabular für Publikations und Dokumenttypen" Version 1.0, Juni 2010, Seite 23
		mapping.put("19", "doc-type:report"); // dito
		mapping.put("20", "doc-type:report"); // Techreport -> Report... klingt akzeptabel
		mapping.put("22", "doc-type:preprint");
		mapping.put("23", "doc-type:Other");
		mapping.put("24", "doc-type:doctoralThesis");
		mapping.put("25", "doc-type:bachelorThesis");
		mapping.put("26", "doc-type:lecture");
		mapping.put("27", "doc-type:Other"); // TODO: abklären! LectureDirectory ist mangels Erklärung schwer mapbar
		mapping.put("30", "doc-type:CourseMaterial");
		mapping.put("31", "doc-type:masterThesis");
		mapping.put("50", "doc-type:masterThesis"); // ursprünglich: "DiplomaThesis". Laut Definition mittlerer Abschluss nach 4-5 Jahren Studium, erschließt sich auch auf Abschlüsse vor Bologna
		mapping.put("51", "doc-type:masterThesis"); // ursprünglich: "MagisterThesis". dito
		mapping.put("72", "doc-type:article");  // ursprünglich: "Postprint"
		mapping.put("77", "doc-type:Website");
		mapping.put("79", "doc-type:Text"); // Universitätsreden passen nicht so recht in die og. Typen, daher provisorisch unter Text einsortiert
		mapping.put("171", "doc-type:masterThesis"); // analog 50, keine Ahnung warum es Diplomarbeit mehrfach gibt (ne Anständige Definition der OPUS Definitionen wär ja zu einfach)
		
		if (mapping.containsKey(s)) {
			return mapping.get(s);
		}
		else {
			return null;
		}
	}

	private static boolean dini2010MappableFromOlderDINISpecs(String testvalue) {
		// TODO: Liste vervollständigen!
		Matcher m = Pattern.compile(
				"(pub-type:)?(" +
					"monograph|" +
					"dissertation|" + 
					"masterthesis|" +
					"report|" + 
					"paper|" + 
					"conf-proceeding|" + 
					"lecture|" + 
					"music|" + 
					"program|" + 
					"play|" + 
					"news|" + 
					"standards|" + 
					"article"+
				")",
				Pattern.UNICODE_CASE
			).matcher(testvalue);
			if (!m.find()) { return false; }
			String testresult = m.toMatchResult().group();
			if (testresult != null && testresult.length() > 0) {
				return true;
			}
			else {
				return false;
			}
	}
	
	private static String dini2010MapFromOlderDINISpecs(String testvalue) {
		String s = testvalue.replace("pub-type:", "");
		s = s.toLowerCase();
		
		HashMap<String, String> mapping = new HashMap<String, String>();
		mapping.put("monograph", "doc-type:book");
		mapping.put("dissertation", "doc-type:doctoralThesis");
		mapping.put("masterthesis", "doc-type:masterThesis");
		mapping.put("report", "doc-type:report");
		mapping.put("paper", "doc-type:Text");
		mapping.put("conf-proceeding", "doc-type:conferenceObject");
		mapping.put("lecture", "doc-type:lecture");
		mapping.put("music", "doc-type:Sound");
		mapping.put("program", "doc-type:Software");
		mapping.put("play", "doc-type:Other");
		mapping.put("news", "doc-type:report");
		mapping.put("standards", "doc-type:patent");
		mapping.put("article", "doc-type:article");
		
		if (mapping.containsKey(s)) {
			return mapping.get(s);
		}
		else {
			return null;
		}
	}
	
	/**
	 * Prüft ob ein Teststring einer DINI2010 Kategorie entspricht. Dabei ist es unerheblich ob der String mit einem "doc-type:" oder "pub-type:" Präfix versehen ist oder nicht. 
	 * Das Matching erfolgt ohne Berücksichtigung der Groß/Kleinschreibung.
	 * @param testvalue
	 * @return
	 */
	private static boolean dini2010MappableWithCaseInsensitivity(String testvalue) {
		testvalue = testvalue.replace("pub-type:", "");
		testvalue = testvalue.replace("doc-type:", "");
		Matcher m = Pattern.compile(
			Classification.dini2010Pattern.replace("doc-type:", "") , // matching nur auf den Kategorien, keine Präfixe
			Pattern.UNICODE_CASE
		).matcher(testvalue);
		if (!m.find()) { 
			return false; 
		}
		String testresult = m.toMatchResult().group();
		
		if (testresult != null && testresult.length() > 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private static String dini2010MapFromCaseInsensitivity(String testvalue) {
		String s = testvalue.replace("pub-type:", "");
		s = s.replace("doc-type:", "");
		s = s.toLowerCase();
		
		HashMap<String, String> mapping = new HashMap<String, String>();
		mapping.put("preprint", "doc-type:preprint");
		mapping.put("workingpaper", "doc-type:workingPaper");
		mapping.put("article", "doc-type:article");
		mapping.put("contributiontoperiodical", "doc-type:contributionToPeriodical");
		mapping.put("periodicalpart", "doc-type:PeriodicalPart");
		mapping.put("periodical", "doc-type:Periodical");
		mapping.put("book", "doc-type:book");
		mapping.put("bookpart", "doc-type:bookPart");
		mapping.put("manuscript", "doc-type:manuscript");
		mapping.put("studythesis", "doc-type:StudyThesis");
		mapping.put("bachelorthesis", "doc-type:bachelorThesis");
		mapping.put("masterthesis", "doc-type:masterThesis");
		mapping.put("doctoralthesis", "doc-type:doctoralThesis");
		mapping.put("conferenceobject", "doc-type:conferenceObject");
		mapping.put("lecture", "doc-type:lecture");
		mapping.put("review", "doc-type:review");
		mapping.put("annotation", "doc-type:annotation");
		mapping.put("patent", "doc-type:patent");
		mapping.put("report", "doc-type:report");
		mapping.put("musicalnotation", "doc-type:MusicalNotation");
		mapping.put("sound", "doc-type:Sound");
		mapping.put("image", "doc-type:Image");
		mapping.put("movingimage", "doc-type:MovingImage");
		mapping.put("stillimage", "doc-type:StillImage");
		mapping.put("coursematerial", "doc-type:CourseMaterial");
		mapping.put("website", "doc-type:Website");
		mapping.put("software", "doc-type:Software");
		mapping.put("cartographicmaterial", "doc-type:CartographicMaterial");
		mapping.put("researchdata", "doc-type:ResearchData");
		mapping.put("other", "doc-type:Other");
		mapping.put("text", "doc-type:Text");
		
		if (mapping.containsKey(s)) {
			return mapping.get(s);
		}
		else {
			return null;
		}
		
		
	}
	
	public static boolean isOther(String testvalue) {
		if (!(isDNB(testvalue)) && !(isDDC(testvalue)) && !(isDINISet(testvalue))) {
			return true;
		} else
			return false;
	}
	
	@Deprecated
	public void setSplitValue() {
		String[] temp = this.value.split(":");
		if (temp.length < 2) {
			// Fehler, keine korrekte DNB-Codierung bzw. kein Wert
			// ÜBergangsweise wird der gesamte Eintrag eingestellt
//			this.value = value;
		} else {
			this.value = value.split(":")[1];
		}
	}

	
	public Classification(String value) {
		this.value = value;
	}
	
	public Classification() {}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public Boolean isGenerated() {
    	return generated;
    }

	public void setGenerated(Boolean generated) {
    	this.generated = generated;
    }

	static class Adapter extends XmlAdapter<Classification,ClassificationInterface> {
		public ClassificationInterface unmarshal(Classification v) {
			System.out.println("unmarshal(" + v +")");
			return v;
		}
	    public Classification marshal(ClassificationInterface v) {
			System.out.println("marshal(" + v +")");
	    	return (Classification)v;
	    }
	}



	
}