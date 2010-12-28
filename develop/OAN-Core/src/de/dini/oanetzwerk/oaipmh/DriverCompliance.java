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
	
	public static String getAuthor(String firstName, String lastName) {
		
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
	 * For interpretation, see http://wiki.surffoundation.nl/display/standards/info-eu-repo 
	 * 
	 */
	
	
	public final static String TYPE_ARTICLE 	= "info:eu-repo/semantics/article";
	public final static String TYPE_BACHELOR 	= "info:eu-repo/semantics/bachelorThesis";
	public final static String TYPE_MASTER 		= "info:eu-repo/semantics/masterThesis";
	public final static String TYPE_DOCTORAL 	= "info:eu-repo/semantics/doctoralThesis";
	public final static String TYPE_BOOK 		= "info:eu-repo/semantics/book";
	public final static String TYPE_BOOKPART 	= "info:eu-repo/semantics/bookPart";
	public final static String TYPE_REVIEW 		= "info:eu-repo/semantics/review";
	public final static String TYPE_CONFERENCE 	= "info:eu-repo/semantics/conferenceObject";
	public final static String TYPE_LECTURE 	= "info:eu-repo/semantics/lecture";
	public final static String TYPE_PAPER 		= "info:eu-repo/semantics/workingPaper";
	public final static String TYPE_PREPRINT 	= "info:eu-repo/semantics/preprint";
	public final static String TYPE_REPORT		= "info:eu-repo/semantics/report";
	public final static String TYPE_ANNOTATION 	= "info:eu-repo/semantics/annotation";
	public final static String TYPE_PERIODICAL	= "info:eu-repo/semantics/contributionToPeriodical";
	public final static String TYPE_PATENT		= "info:eu-repo/semantics/patent";
	public final static String TYPE_OTHER		= "info:eu-repo/semantics/other";
	
	public static Map<String, String> typeMapping = new HashMap<String, String>();
	
	static {
		// typeMapping.put("       ", TYPE_CONFERENCE);
		
		// type article
		typeMapping.put("article", TYPE_ARTICLE);
		typeMapping.put("article, not peer-reviewed", TYPE_ARTICLE);
		typeMapping.put("encyclopedia article", TYPE_ARTICLE);
		typeMapping.put("journal article", TYPE_ARTICLE);
		typeMapping.put("zeitschriftenartikel", TYPE_ARTICLE);
		
		
		// type bachelor
		typeMapping.put("thesis.bachelor", TYPE_BACHELOR);
		typeMapping.put("bachelorthesis", TYPE_BACHELOR);
		typeMapping.put("bachelorarbeit", TYPE_BACHELOR);
		typeMapping.put("bachelor thesis", TYPE_BACHELOR);
		typeMapping.put("text.thesis.bachelor", TYPE_BACHELOR);
		typeMapping.put("bachelor", TYPE_BACHELOR);
		typeMapping.put("bsc", TYPE_BACHELOR);
		typeMapping.put("b.sc", TYPE_BACHELOR);
		
		// type master
		typeMapping.put("thesis.master", TYPE_MASTER);
		typeMapping.put("mastersthesis", TYPE_MASTER);
		typeMapping.put("masterthesis", TYPE_MASTER);
		typeMapping.put("masterthesis", TYPE_MASTER);
		typeMapping.put("masterarbeit", TYPE_MASTER);
		typeMapping.put("masters-thesis.magister", TYPE_MASTER);
		typeMapping.put("master thesis", TYPE_MASTER);
		typeMapping.put("msc", TYPE_MASTER);
		typeMapping.put("diplomarbeit", TYPE_MASTER);
		typeMapping.put("diplomathesis", TYPE_MASTER);
		typeMapping.put("diplomthesis", TYPE_MASTER);
		typeMapping.put("thesis.diplom", TYPE_MASTER);
		typeMapping.put("magisterthesis", TYPE_MASTER);
		typeMapping.put("magisterarbeit", TYPE_MASTER);
		typeMapping.put("dipl", TYPE_MASTER);
		typeMapping.put("mag", TYPE_MASTER);
		
		//type doctoral
		typeMapping.put("text.thesis.doctoral", TYPE_DOCTORAL);
		typeMapping.put("thesis.doctoral", TYPE_DOCTORAL);
		typeMapping.put("doctoralthesis", TYPE_DOCTORAL);
		typeMapping.put("dissertation", TYPE_DOCTORAL);
		typeMapping.put("aufsatz", TYPE_DOCTORAL);
		typeMapping.put("habilitationthesis", TYPE_DOCTORAL);
		typeMapping.put("habilitation", TYPE_DOCTORAL);
		typeMapping.put("thesis.habilitation", TYPE_DOCTORAL);
		typeMapping.put("habil", TYPE_DOCTORAL);
		typeMapping.put("offlinethesishabilitation", TYPE_DOCTORAL);
		typeMapping.put("text.thesis.habilitation", TYPE_DOCTORAL);
		
		
		//type book
		typeMapping.put("book", TYPE_BOOK);
		typeMapping.put("buch, monographie", TYPE_BOOK);
		typeMapping.put("buch", TYPE_BOOK);
		
		
		// type book part
		typeMapping.put("book chapter", TYPE_BOOKPART);
		typeMapping.put("book part", TYPE_BOOKPART);
		typeMapping.put("book article", TYPE_BOOKPART);
		typeMapping.put("buchbeitrag", TYPE_BOOKPART);
		
		// type review
		typeMapping.put("review", TYPE_REVIEW);
		typeMapping.put("rezension", TYPE_REVIEW);
		
		// type conference
		typeMapping.put("conference paper", TYPE_CONFERENCE);
		typeMapping.put("tagungsbericht", TYPE_CONFERENCE);
		typeMapping.put("conference proceedings", TYPE_CONFERENCE);
		typeMapping.put("konferenzbeitrag", TYPE_CONFERENCE);
		typeMapping.put("inproceedings (aufsatz/paper einer konferenz)", TYPE_CONFERENCE);
		typeMapping.put("tagungsbericht, konferenzschrift", TYPE_CONFERENCE);
		typeMapping.put("in-proceedings", TYPE_CONFERENCE);
		typeMapping.put("inproceedings", TYPE_CONFERENCE);
		typeMapping.put("proceeding", TYPE_CONFERENCE);
		typeMapping.put("proceeding", TYPE_CONFERENCE);
		typeMapping.put("proceedings", TYPE_CONFERENCE);
		typeMapping.put("veranstaltungsprogramm", TYPE_CONFERENCE);
		typeMapping.put("conference contributions", TYPE_CONFERENCE);
		typeMapping.put("conference poster ", TYPE_CONFERENCE);
		
		// type lecture
		typeMapping.put("lecture", TYPE_LECTURE);
		typeMapping.put("coursematerial", TYPE_LECTURE);
		typeMapping.put("lehrmaterial", TYPE_LECTURE);
		typeMapping.put("material zur lehre", TYPE_LECTURE);
		typeMapping.put("vorlesungsverzeichnis", TYPE_LECTURE);
		typeMapping.put("vorlesungsmaterialien", TYPE_LECTURE);
		typeMapping.put("sonstiges lehrmaterial", TYPE_LECTURE);
		typeMapping.put("kurs- und seminarmaterial", TYPE_LECTURE);
		typeMapping.put("teachingmaterial", TYPE_LECTURE);
		typeMapping.put("public-lecture", TYPE_LECTURE);
		typeMapping.put("vorlesungsskript", TYPE_LECTURE);
		
		// type working paper
		typeMapping.put("paper", TYPE_PAPER);
		typeMapping.put("research paper", TYPE_PAPER);
		typeMapping.put("research-paper", TYPE_PAPER);
		typeMapping.put("working paper", TYPE_PAPER);
		typeMapping.put("research memorandum", TYPE_PAPER);
		typeMapping.put("discussion paper", TYPE_PAPER);
		typeMapping.put("researchpaper", TYPE_PAPER);
		
		// type preprint
		typeMapping.put("preprint", TYPE_PREPRINT);
		
		// type report
		typeMapping.put("techreport", TYPE_REPORT);
		typeMapping.put("report", TYPE_REPORT);
		typeMapping.put("research report", TYPE_REPORT);
		typeMapping.put("report", TYPE_REPORT);
		typeMapping.put("annual report", TYPE_REPORT);
		typeMapping.put("bericht, report", TYPE_REPORT);
		typeMapping.put("forschungsbericht", TYPE_REPORT);
		typeMapping.put("project report", TYPE_REPORT);
		typeMapping.put("technical report", TYPE_REPORT);
		typeMapping.put("technischer report", TYPE_REPORT);
		typeMapping.put("tech-report", TYPE_REPORT);
		typeMapping.put("internal report", TYPE_REPORT);
		typeMapping.put("statistical report", TYPE_REPORT);
		typeMapping.put("technical documentation", TYPE_REPORT);
		typeMapping.put("manual", TYPE_CONFERENCE);
		
		// type patent
		typeMapping.put("patent", TYPE_PATENT);
	}
	
	public static String getTypeForString(final String typeLiteral) {
		
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
				return TYPE_DOCTORAL;
			else 
				return TYPE_OTHER;
		}		
		
		if (typeMapping.containsKey(typeLiteral.toLowerCase())) {
			
			return typeMapping.get(typeLiteral.toLowerCase());
			
		} else {
			
			return TYPE_OTHER;
		}
	}
}
