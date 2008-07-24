package de.dini.oanetzwerk.servicemodule.harvester;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OAIRecoveryMatcher {
	
	public static final int TRY_EVERYTHING = 0;
	public static final int RECOVER_BY_ENDTAG = 1;
	
	public static Iterable<MatchResult> findMatches( String pattern, CharSequence s ) { 
		List<MatchResult> results = new ArrayList<MatchResult>(); 

		for ( Matcher m = Pattern.compile(pattern).matcher(s); m.find(); ) {
			results.add( m.toMatchResult() );  
		}	     
		return results; 
	}

	/*-------------------------------------------------------------------------------------*/

	public static String recoverResumptionToken(String xmldata) {
		return recoverResumptionToken(xmldata, TRY_EVERYTHING);
	}
	
	public static String recoverResumptionToken(String xmldata, int recoveryMode) {
		String result = null;
		switch(recoveryMode) {
			case RECOVER_BY_ENDTAG:
				result = recoverResumptionToken_byEndTag(xmldata);
				break;
			case TRY_EVERYTHING:
			default:
				// try all recovery methods that might work
				result = recoverResumptionToken_byEndTag(xmldata);
		}
		return result;
	}
	
	public static String recoverResumptionToken_byEndTag(String xmldata) {
		String pattern = ">([^<>]*)</resumptionToken>"; 
		String result = null;
		for ( MatchResult r : findMatches( pattern, xmldata ) ) {
			result = r.group(1);		
		}
		return result;
	}

	/*-------------------------------------------------------------------------------------*/

	public static Set<String> recoverIdentifiers(String xmldata) {
		return recoverIdentifiers(xmldata,TRY_EVERYTHING );
	}
	
	public static Set<String> recoverIdentifiers(String xmldata, int recoveryMode) {
		Set<String> result = null;
		switch(recoveryMode) {
			case RECOVER_BY_ENDTAG:
				result = recoverIdentifiers_byEndTag(xmldata);
				break;
			case TRY_EVERYTHING:
			default:
				// try all recovery methods that might work
				result = recoverIdentifiers_byEndTag(xmldata);
		}
		return result;
	}
	
	public static Set<String> recoverIdentifiers_byEndTag(String xmldata) {
		Set<String> ids = new HashSet<String>();
		String pattern = ">([^<>]*)</identifier>"; 

		for ( MatchResult r : findMatches( pattern, xmldata ) ) {
			ids.add(r.group(1));
		}
		return ids;
	}
}
