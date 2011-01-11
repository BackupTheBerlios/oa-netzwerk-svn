package de.dini.oanetzwerk.oaipmh;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class DriverCompliance {
	
	/*
	 * 
	 * Creator-Field Syntax Construction according to the Driver Guidelines v2.0, 
	 * see page 73f.
	 * 
	 */

	private static boolean driverComplianceEnabled = true;
	


	public static String getAuthor(String firstName, String lastName) {
		
		if (!driverComplianceEnabled) 
		{
			return firstName + " " + lastName;
		}
		
		String[] firstNames;
		String firstNameInitials = "";
		String firstNameFull = " (";
		
		if (!StringUtils.isEmpty(firstName)) {
			
			firstNames = firstName.split(" ");
			
			int i = 0;
			for (String fn : firstNames) {
				i++;
				firstNameInitials += fn.charAt(0) + ".";
				firstNameFull += i > 1 ? ", " + fn : fn;
			}
			
			firstNameFull += ")";
		}
		return lastName + (!StringUtils.isEmpty(firstName) ? ", " + firstNameInitials + firstNameFull : "");
	}
	
	
	/*
	 * 
	 * Type-Mapping
	 * 
	 * Mapping between custom types and types defined by the Driver Guidelines v2.0. (p. 69ff)
	 * For interpretation, see http://wiki.surffoundation.nl/display/standards/info-eu-repo#info-eu-repo-Publicationtypes
	 * 
	 */
	
	
	public final static String DRIVER_TYPE_ARTICLE 		= "info:eu-repo/semantics/article";
	public final static String DRIVER_TYPE_BACHELOR 	= "info:eu-repo/semantics/bachelorThesis";
	public final static String DRIVER_TYPE_MASTER 		= "info:eu-repo/semantics/masterThesis";
	public final static String DRIVER_TYPE_DOCTORAL 	= "info:eu-repo/semantics/doctoralThesis";
	public final static String DRIVER_TYPE_BOOK 		= "info:eu-repo/semantics/book";
	public final static String DRIVER_TYPE_BOOKPART 	= "info:eu-repo/semantics/bookPart";
	public final static String DRIVER_TYPE_REVIEW 		= "info:eu-repo/semantics/review";
	public final static String DRIVER_TYPE_CONFERENCE 	= "info:eu-repo/semantics/conferenceObject";
	public final static String DRIVER_TYPE_LECTURE 		= "info:eu-repo/semantics/lecture";
	public final static String DRIVER_TYPE_PAPER 		= "info:eu-repo/semantics/workingPaper";
	public final static String DRIVER_TYPE_PREPRINT 	= "info:eu-repo/semantics/preprint";
	public final static String DRIVER_TYPE_REPORT		= "info:eu-repo/semantics/report";
	public final static String DRIVER_TYPE_ANNOTATION 	= "info:eu-repo/semantics/annotation";
	public final static String DRIVER_TYPE_PERIODICAL	= "info:eu-repo/semantics/contributionToPeriodical";
	public final static String DRIVER_TYPE_PATENT		= "info:eu-repo/semantics/patent";
	public final static String DRIVER_TYPE_OTHER		= "info:eu-repo/semantics/other";
	
	public static Map<String, String> driverTypeMapping = new HashMap<String, String>();
	
	/* Dini type mappings */
	public final static String DINI_TYPE_ARTICLE 		= "doc-type:article";
	public final static String DINI_TYPE_BACHELOR 		= "doc-type:bachelorThesis";
	public final static String DINI_TYPE_MASTER 		= "doc-type:masterThesis";
	public final static String DINI_TYPE_DOCTORAL 		= "doc-type:doctoralThesis";
	public final static String DINI_TYPE_BOOK 			= "doc-type:book";
	public final static String DINI_TYPE_BOOKPART 		= "doc-type:bookPart";
	public final static String DINI_TYPE_REVIEW 		= "doc-type:review";
	public final static String DINI_TYPE_CONFERENCE 	= "doc-type:conferenceObject";
	public final static String DINI_TYPE_LECTURE 		= "doc-type:lecture";
	public final static String DINI_TYPE_PAPER 			= "doc-type:workingPaper";
	public final static String DINI_TYPE_PREPRINT 		= "doc-type:preprint";
	public final static String DINI_TYPE_REPORT			= "doc-type:report";
	public final static String DINI_TYPE_ANNOTATION 	= "doc-type:annotation";
	public final static String DINI_TYPE_PERIODICAL		= "doc-type:contributionToPeriodical";
	public final static String DINI_TYPE_PATENT			= "doc-type:patent";
	public final static String DINI_TYPE_OTHER			= "doc-type:other";
	
	public static Map<String, String> diniTypeMapping = new HashMap<String, String>();
	
	static {
		// typeMapping.put("       ", TYPE_CONFERENCE);
		
		// type article
		driverTypeMapping.put("article", DRIVER_TYPE_ARTICLE);
		driverTypeMapping.put("article, not peer-reviewed", DRIVER_TYPE_ARTICLE);
		driverTypeMapping.put("encyclopedia article", DRIVER_TYPE_ARTICLE);
		driverTypeMapping.put("journal article", DRIVER_TYPE_ARTICLE);
		driverTypeMapping.put("zeitschriftenartikel", DRIVER_TYPE_ARTICLE);
		
		
		// type bachelor
		driverTypeMapping.put("thesis.bachelor", DRIVER_TYPE_BACHELOR);
		driverTypeMapping.put("bachelorthesis", DRIVER_TYPE_BACHELOR);
		driverTypeMapping.put("bachelorarbeit", DRIVER_TYPE_BACHELOR);
		driverTypeMapping.put("bachelor thesis", DRIVER_TYPE_BACHELOR);
		driverTypeMapping.put("text.thesis.bachelor", DRIVER_TYPE_BACHELOR);
		driverTypeMapping.put("bachelor", DRIVER_TYPE_BACHELOR);
		driverTypeMapping.put("bsc", DRIVER_TYPE_BACHELOR);
		driverTypeMapping.put("b.sc", DRIVER_TYPE_BACHELOR);
		
		// type master
		driverTypeMapping.put("thesis.master", DRIVER_TYPE_MASTER);
		driverTypeMapping.put("mastersthesis", DRIVER_TYPE_MASTER);
		driverTypeMapping.put("masterthesis", DRIVER_TYPE_MASTER);
		driverTypeMapping.put("masterthesis", DRIVER_TYPE_MASTER);
		driverTypeMapping.put("masterarbeit", DRIVER_TYPE_MASTER);
		driverTypeMapping.put("masters-thesis.magister", DRIVER_TYPE_MASTER);
		driverTypeMapping.put("master thesis", DRIVER_TYPE_MASTER);
		driverTypeMapping.put("msc", DRIVER_TYPE_MASTER);
		driverTypeMapping.put("diplomarbeit", DRIVER_TYPE_MASTER);
		driverTypeMapping.put("diplomathesis", DRIVER_TYPE_MASTER);
		driverTypeMapping.put("diplomthesis", DRIVER_TYPE_MASTER);
		driverTypeMapping.put("thesis.diplom", DRIVER_TYPE_MASTER);
		driverTypeMapping.put("magisterthesis", DRIVER_TYPE_MASTER);
		driverTypeMapping.put("magisterarbeit", DRIVER_TYPE_MASTER);
		driverTypeMapping.put("dipl", DRIVER_TYPE_MASTER);
		driverTypeMapping.put("mag", DRIVER_TYPE_MASTER);
		
		//type doctoral
		driverTypeMapping.put("text.thesis.doctoral", DRIVER_TYPE_DOCTORAL);
		driverTypeMapping.put("thesis.doctoral", DRIVER_TYPE_DOCTORAL);
		driverTypeMapping.put("doctoralthesis", DRIVER_TYPE_DOCTORAL);
		driverTypeMapping.put("dissertation", DRIVER_TYPE_DOCTORAL);
		driverTypeMapping.put("aufsatz", DRIVER_TYPE_DOCTORAL);
		driverTypeMapping.put("habilitationthesis", DRIVER_TYPE_DOCTORAL);
		driverTypeMapping.put("habilitation", DRIVER_TYPE_DOCTORAL);
		driverTypeMapping.put("thesis.habilitation", DRIVER_TYPE_DOCTORAL);
		driverTypeMapping.put("habil", DRIVER_TYPE_DOCTORAL);
		driverTypeMapping.put("offlinethesishabilitation", DRIVER_TYPE_DOCTORAL);
		driverTypeMapping.put("text.thesis.habilitation", DRIVER_TYPE_DOCTORAL);
		
		
		//type book
		driverTypeMapping.put("book", DRIVER_TYPE_BOOK);
		driverTypeMapping.put("buch, monographie", DRIVER_TYPE_BOOK);
		driverTypeMapping.put("buch", DRIVER_TYPE_BOOK);
		
		
		// type book part
		driverTypeMapping.put("book chapter", DRIVER_TYPE_BOOKPART);
		driverTypeMapping.put("book part", DRIVER_TYPE_BOOKPART);
		driverTypeMapping.put("book article", DRIVER_TYPE_BOOKPART);
		driverTypeMapping.put("buchbeitrag", DRIVER_TYPE_BOOKPART);
		
		// type review
		driverTypeMapping.put("review", DRIVER_TYPE_REVIEW);
		driverTypeMapping.put("rezension", DRIVER_TYPE_REVIEW);
		
		// type conference
		driverTypeMapping.put("conference paper", DRIVER_TYPE_CONFERENCE);
		driverTypeMapping.put("tagungsbericht", DRIVER_TYPE_CONFERENCE);
		driverTypeMapping.put("conference proceedings", DRIVER_TYPE_CONFERENCE);
		driverTypeMapping.put("konferenzbeitrag", DRIVER_TYPE_CONFERENCE);
		driverTypeMapping.put("inproceedings (aufsatz/paper einer konferenz)", DRIVER_TYPE_CONFERENCE);
		driverTypeMapping.put("tagungsbericht, konferenzschrift", DRIVER_TYPE_CONFERENCE);
		driverTypeMapping.put("in-proceedings", DRIVER_TYPE_CONFERENCE);
		driverTypeMapping.put("inproceedings", DRIVER_TYPE_CONFERENCE);
		driverTypeMapping.put("proceeding", DRIVER_TYPE_CONFERENCE);
		driverTypeMapping.put("proceeding", DRIVER_TYPE_CONFERENCE);
		driverTypeMapping.put("proceedings", DRIVER_TYPE_CONFERENCE);
		driverTypeMapping.put("veranstaltungsprogramm", DRIVER_TYPE_CONFERENCE);
		driverTypeMapping.put("conference contributions", DRIVER_TYPE_CONFERENCE);
		driverTypeMapping.put("conference poster ", DRIVER_TYPE_CONFERENCE);
		
		// type lecture
		driverTypeMapping.put("lecture", DRIVER_TYPE_LECTURE);
		driverTypeMapping.put("coursematerial", DRIVER_TYPE_LECTURE);
		driverTypeMapping.put("lehrmaterial", DRIVER_TYPE_LECTURE);
		driverTypeMapping.put("material zur lehre", DRIVER_TYPE_LECTURE);
		driverTypeMapping.put("vorlesungsverzeichnis", DRIVER_TYPE_LECTURE);
		driverTypeMapping.put("vorlesungsmaterialien", DRIVER_TYPE_LECTURE);
		driverTypeMapping.put("sonstiges lehrmaterial", DRIVER_TYPE_LECTURE);
		driverTypeMapping.put("kurs- und seminarmaterial", DRIVER_TYPE_LECTURE);
		driverTypeMapping.put("teachingmaterial", DRIVER_TYPE_LECTURE);
		driverTypeMapping.put("public-lecture", DRIVER_TYPE_LECTURE);
		driverTypeMapping.put("vorlesungsskript", DRIVER_TYPE_LECTURE);
		
		// type working paper
		driverTypeMapping.put("paper", DRIVER_TYPE_PAPER);
		driverTypeMapping.put("research paper", DRIVER_TYPE_PAPER);
		driverTypeMapping.put("research-paper", DRIVER_TYPE_PAPER);
		driverTypeMapping.put("working paper", DRIVER_TYPE_PAPER);
		driverTypeMapping.put("research memorandum", DRIVER_TYPE_PAPER);
		driverTypeMapping.put("discussion paper", DRIVER_TYPE_PAPER);
		driverTypeMapping.put("researchpaper", DRIVER_TYPE_PAPER);
		
		// type preprint
		driverTypeMapping.put("preprint", DRIVER_TYPE_PREPRINT);
		
		// type report
		driverTypeMapping.put("techreport", DRIVER_TYPE_REPORT);
		driverTypeMapping.put("report", DRIVER_TYPE_REPORT);
		driverTypeMapping.put("research report", DRIVER_TYPE_REPORT);
		driverTypeMapping.put("report", DRIVER_TYPE_REPORT);
		driverTypeMapping.put("annual report", DRIVER_TYPE_REPORT);
		driverTypeMapping.put("bericht, report", DRIVER_TYPE_REPORT);
		driverTypeMapping.put("forschungsbericht", DRIVER_TYPE_REPORT);
		driverTypeMapping.put("project report", DRIVER_TYPE_REPORT);
		driverTypeMapping.put("technical report", DRIVER_TYPE_REPORT);
		driverTypeMapping.put("technischer report", DRIVER_TYPE_REPORT);
		driverTypeMapping.put("tech-report", DRIVER_TYPE_REPORT);
		driverTypeMapping.put("internal report", DRIVER_TYPE_REPORT);
		driverTypeMapping.put("statistical report", DRIVER_TYPE_REPORT);
		driverTypeMapping.put("technical documentation", DRIVER_TYPE_REPORT);
		driverTypeMapping.put("manual", DRIVER_TYPE_CONFERENCE);
		
		// type patent
		driverTypeMapping.put("patent", DRIVER_TYPE_PATENT);
		
		
		//
		// DINI-DRIVER Type Mapping
		//
		
		diniTypeMapping.put(DRIVER_TYPE_ARTICLE, DINI_TYPE_ARTICLE);
		diniTypeMapping.put(DRIVER_TYPE_ANNOTATION, DINI_TYPE_ANNOTATION);
		diniTypeMapping.put(DRIVER_TYPE_BACHELOR, DINI_TYPE_BACHELOR);
		diniTypeMapping.put(DRIVER_TYPE_BOOK, DINI_TYPE_BOOK);
		diniTypeMapping.put(DRIVER_TYPE_BOOKPART, DINI_TYPE_BOOKPART);
		diniTypeMapping.put(DRIVER_TYPE_CONFERENCE, DINI_TYPE_CONFERENCE);
		diniTypeMapping.put(DRIVER_TYPE_DOCTORAL, DINI_TYPE_DOCTORAL);
		diniTypeMapping.put(DRIVER_TYPE_LECTURE, DINI_TYPE_LECTURE);
		diniTypeMapping.put(DRIVER_TYPE_MASTER, DINI_TYPE_MASTER);
		diniTypeMapping.put(DRIVER_TYPE_OTHER, DINI_TYPE_OTHER);
		diniTypeMapping.put(DRIVER_TYPE_PAPER, DINI_TYPE_PAPER);
		diniTypeMapping.put(DRIVER_TYPE_PATENT, DINI_TYPE_PATENT);
		diniTypeMapping.put(DRIVER_TYPE_PERIODICAL, DINI_TYPE_PERIODICAL);
		diniTypeMapping.put(DRIVER_TYPE_PREPRINT, DINI_TYPE_PREPRINT);
		diniTypeMapping.put(DRIVER_TYPE_REPORT, DINI_TYPE_REPORT);
		diniTypeMapping.put(DRIVER_TYPE_REVIEW, DINI_TYPE_REVIEW);
	}
	
	public static String getDriverDCTypeForString(final String typeLiteral) {
		
		if (typeLiteral == null || typeLiteral.equals(""))
		{
			return typeLiteral;
		}
		
		// special case: Dissertation
		// 'Dissertation has different meanings in german and english
		// if it is written capitalized, we will assume the german meaning 
		// if it is written with only small letters we will use the english meaning
		if (typeLiteral.toLowerCase().equals("dissertation")) {
		
			if (typeLiteral.equals("Dissertation"))
				return DRIVER_TYPE_DOCTORAL;
			else 
				return DRIVER_TYPE_OTHER;
		}		
		
		if (driverTypeMapping.containsKey(typeLiteral.toLowerCase())) {
			
			return driverTypeMapping.get(typeLiteral.toLowerCase());
			
		} else {
			
			return DRIVER_TYPE_OTHER;
		}
	}
	
	
	
	public static String getDiniDCTypeByDriverDCType(final String driverTypeLiteral) {
		
		if (driverTypeLiteral == null || driverTypeLiteral.equals(""))
		{
			return driverTypeLiteral;
		}
		

		if (driverTypeMapping.containsKey(driverTypeLiteral)) {
			
			return diniTypeMapping.get(driverTypeLiteral);
			
		} else {
			
			return DINI_TYPE_OTHER;
		}
	}
	
	public static boolean isDriverComplianceEnabled() {
		return driverComplianceEnabled;
	}
}
