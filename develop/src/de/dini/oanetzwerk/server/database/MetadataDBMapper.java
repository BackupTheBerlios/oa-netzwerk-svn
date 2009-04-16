package de.dini.oanetzwerk.server.database;

import java.sql.SQLException;
import java.text.ParseException;

import org.apache.log4j.Logger;

//import de.dini.oanetzwerk.server.handler.InternalMetadataEntry;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;
import de.dini.oanetzwerk.utils.imf.Author;
import de.dini.oanetzwerk.utils.imf.Classification;
import de.dini.oanetzwerk.utils.imf.Contributor;
import de.dini.oanetzwerk.utils.imf.DDCClassification;
import de.dini.oanetzwerk.utils.imf.DINISetClassification;
import de.dini.oanetzwerk.utils.imf.DNBClassification;
import de.dini.oanetzwerk.utils.imf.DateValue;
import de.dini.oanetzwerk.utils.imf.Description;
import de.dini.oanetzwerk.utils.imf.Editor;
import de.dini.oanetzwerk.utils.imf.Format;
import de.dini.oanetzwerk.utils.imf.HitlistMetadata;
import de.dini.oanetzwerk.utils.imf.Identifier;
import de.dini.oanetzwerk.utils.imf.InternalMetadata;
import de.dini.oanetzwerk.utils.imf.Keyword;
import de.dini.oanetzwerk.utils.imf.Language;
import de.dini.oanetzwerk.utils.imf.OtherClassification;
import de.dini.oanetzwerk.utils.imf.Publisher;
import de.dini.oanetzwerk.utils.imf.Title;
import de.dini.oanetzwerk.utils.imf.TypeValue;

public class MetadataDBMapper {

	private static Logger logger = Logger.getLogger (MetadataDBMapper.class);
	
	public static void fillHitlistMetadataFromDB(HitlistMetadata hmf, MultipleStatementConnection stmtconn) throws SQLException, WrongStatementException {
		
		QueryResult queryResult;
		
		stmtconn.loadStatement (SelectFromDB.Title (stmtconn.connection, hmf.getOid()));
		queryResult = stmtconn.execute ( );
		
		// Auswertung der Titel
		while (queryResult.getResultSet ( ).next ( )) {
			
			Title temp = new Title ( );
			temp.setTitle (queryResult.getResultSet ( ).getString ("title"));
			temp.setQualifier (queryResult.getResultSet ( ).getString ("qualifier"));
			temp.setLang (queryResult.getResultSet ( ).getString ("lang"));
//			temp.setNumber (queryResult.getResultSet ( ).getInt ("number"));
			hmf.addTitle (temp);
		}
		
		// Auswertung der Autoren
		stmtconn.loadStatement (SelectFromDB.Authors (stmtconn.connection, hmf.getOid()));
		queryResult = stmtconn.execute ( );
		
		while (queryResult.getResultSet ( ).next ( )) {
			
			Author temp = new Author ( );
			temp.setNumber(queryResult.getResultSet ( ).getInt("number"));
			temp.setFirstname(queryResult.getResultSet ( ).getString("firstname"));
			temp.setLastname(queryResult.getResultSet ( ).getString("lastname"));
			temp.setInstitution(queryResult.getResultSet ( ).getString("institution"));
			temp.setEmail(queryResult.getResultSet ( ).getString("email"));
			temp.setTitle(queryResult.getResultSet ( ).getString("title"));
			hmf.addAuthor (temp);
		}
		
				// Auswertung des Identifiers
		stmtconn.loadStatement (SelectFromDB.Identifier (stmtconn.connection, hmf.getOid()));
		queryResult = stmtconn.execute ( );
		
		while (queryResult.getResultSet ( ).next ( )) {
			
			Identifier temp = new Identifier ( );
			temp.setIdentifier(queryResult.getResultSet ( ).getString ("identifier"));
//			temp.setLanguage(queryResult.getResultSet ( ).getString ("lang"));
			temp.setNumber (queryResult.getResultSet ( ).getInt ("number"));
			hmf.addIdentifier (temp);
		}
		
	}
	
	public static void fillInternalMetadataFromDB(InternalMetadata hmf, MultipleStatementConnection stmtconn) throws SQLException, WrongStatementException {
		
		QueryResult queryResult;
		
		stmtconn.loadStatement (SelectFromDB.Title (stmtconn.connection, hmf.getOid()));
		queryResult = stmtconn.execute ( );
		
		// Auswertung der Titel
		while (queryResult.getResultSet ( ).next ( )) {
			
			Title temp = new Title ( );
			temp.setTitle (queryResult.getResultSet ( ).getString ("title"));
			temp.setQualifier (queryResult.getResultSet ( ).getString ("qualifier"));
			temp.setLang (queryResult.getResultSet ( ).getString ("lang"));
//			temp.setNumber (queryResult.getResultSet ( ).getInt ("number"));
			hmf.addTitle (temp);
		}
		hmf.setTitleCounter(hmf.getTitleList().size());
		
		// Auswertung der Autoren
		stmtconn.loadStatement (SelectFromDB.Authors (stmtconn.connection, hmf.getOid()));
		queryResult = stmtconn.execute ( );
		
		while (queryResult.getResultSet ( ).next ( )) {
			
			Author temp = new Author ( );
			temp.setNumber(queryResult.getResultSet ( ).getInt("number"));
			temp.setFirstname(queryResult.getResultSet ( ).getString("firstname"));
			temp.setLastname(queryResult.getResultSet ( ).getString("lastname"));
			temp.setInstitution(queryResult.getResultSet ( ).getString("institution"));
			temp.setEmail(queryResult.getResultSet ( ).getString("email"));
			temp.setTitle(queryResult.getResultSet ( ).getString("title"));
			hmf.addAuthor (temp);
		}
		hmf.setAuthorCounter(hmf.getAuthorList().size());
		
		// Auswertung der Editoren
		stmtconn.loadStatement (SelectFromDB.Editors (stmtconn.connection, hmf.getOid()));
		queryResult = stmtconn.execute ( );
		
		while (queryResult.getResultSet ( ).next ( )) {
			
			Editor temp = new Editor ( );
			temp.setNumber(queryResult.getResultSet ( ).getInt("number"));
			temp.setFirstname(queryResult.getResultSet ( ).getString("firstname"));
			temp.setLastname(queryResult.getResultSet ( ).getString("lastname"));
			temp.setInstitution(queryResult.getResultSet ( ).getString("institution"));
			temp.setEmail(queryResult.getResultSet ( ).getString("email"));
			temp.setTitle(queryResult.getResultSet ( ).getString("title"));
			hmf.addEditor (temp);
		}
		hmf.setEditorCounter(hmf.getEditorList().size());
		
		// Auswertung der Bearbeiter
		stmtconn.loadStatement (SelectFromDB.Contributors (stmtconn.connection, hmf.getOid()));
		queryResult = stmtconn.execute ( );
		
		while (queryResult.getResultSet ( ).next ( )) {
			
			Contributor temp = new Contributor ( );
			temp.setNumber(queryResult.getResultSet ( ).getInt("number"));
			temp.setFirstname(queryResult.getResultSet ( ).getString("firstname"));
			temp.setLastname(queryResult.getResultSet ( ).getString("lastname"));
			temp.setInstitution(queryResult.getResultSet ( ).getString("institution"));
			temp.setEmail(queryResult.getResultSet ( ).getString("email"));
			temp.setTitle(queryResult.getResultSet ( ).getString("title"));
			hmf.addContributor (temp);
		}
		hmf.setContributorCounter(hmf.getContributorList().size());
	
		// Auswertung des Formats
		stmtconn.loadStatement (SelectFromDB.Format (stmtconn.connection, hmf.getOid()));
		queryResult = stmtconn.execute ( );
		
		while (queryResult.getResultSet ( ).next ( )) {
			
			Format temp = new Format ( );
			temp.setSchema_f(queryResult.getResultSet ( ).getString ("schema_f"));
			temp.setNumber (queryResult.getResultSet ( ).getInt ("number"));
			hmf.addFormat (temp);
		}
		hmf.setFormatCounter(hmf.getFormatList().size());
		
		// Auswertung des Identifiers
		stmtconn.loadStatement (SelectFromDB.Identifier (stmtconn.connection, hmf.getOid()));
		queryResult = stmtconn.execute ( );
		
		while (queryResult.getResultSet ( ).next ( )) {
			
			Identifier temp = new Identifier ( );
			temp.setIdentifier(queryResult.getResultSet ( ).getString ("identifier"));
//			temp.setLanguage(queryResult.getResultSet ( ).getString ("lang"));
			temp.setNumber (queryResult.getResultSet ( ).getInt ("number"));
			hmf.addIdentifier (temp);
		}
		hmf.setIdentifierCounter(hmf.getIdentifierList().size());

		
		// Auswertung der Description
		stmtconn.loadStatement (SelectFromDB.Description (stmtconn.connection, hmf.getOid()));
		queryResult = stmtconn.execute ( );
		
		while (queryResult.getResultSet ( ).next ( )) {
			
			Description temp = new Description ( );
			temp.setDescription(queryResult.getResultSet ( ).getString ("abstract"));
			temp.setLanguage(queryResult.getResultSet ( ).getString ("lang"));
			temp.setNumber (queryResult.getResultSet ( ).getInt ("number"));
			hmf.addDescription (temp);
		}
		hmf.setDescriptionCounter(hmf.getDescriptionList().size());


		// Auswertung der DateValue-Werte
		stmtconn.loadStatement (SelectFromDB.DateValues (stmtconn.connection, hmf.getOid()));
		queryResult = stmtconn.execute ( );
		
		while (queryResult.getResultSet ( ).next ( )) {
			try {
			  DateValue temp = new DateValue ( );
			  temp.setDateValue(HelperMethods.sql2javaDate(queryResult.getResultSet ( ).getDate("value")));
			  temp.setNumber (queryResult.getResultSet ( ).getInt ("number"));
			  temp.setStringValue (queryResult.getResultSet ( ).getString ("originalValue"));
			  hmf.addDateValue (temp);
			} catch(ParseException pex) {
				logger.warn("skip dateValue '" + queryResult.getResultSet ( ).getDate("value") +"': ", pex);
			}
		}
		hmf.setDateValueCounter(hmf.getDateValueList().size());

				
		// Auswertung der TypeValue-Werte
		stmtconn.loadStatement (SelectFromDB.TypeValues (stmtconn.connection, hmf.getOid()));
		queryResult = stmtconn.execute ( );
		
		while (queryResult.getResultSet ( ).next ( )) {
			
			TypeValue temp = new TypeValue ( );
			temp.setTypeValue(queryResult.getResultSet ( ).getString ("value"));
//			temp.setNumber (queryResult.getResultSet ( ).getInt ("number"));
			hmf.addTypeValue (temp);
		}
		hmf.setTypeValueCounter(hmf.getTypeValueList().size());


		// Auswertung der Publisher-Werte
		stmtconn.loadStatement (SelectFromDB.Publisher (stmtconn.connection, hmf.getOid()));
		queryResult = stmtconn.execute ( );
		
		while (queryResult.getResultSet ( ).next ( )) {
			
			Publisher temp = new Publisher ( );
			temp.setName(queryResult.getResultSet ( ).getString ("name"));
			temp.setNumber (queryResult.getResultSet ( ).getInt ("number"));
			hmf.addPublisher (temp);
		}
		hmf.setPublisherCounter(hmf.getPublisherList().size());

		
		// Auswertung der DDC-Classifications-Werte
		stmtconn.loadStatement (SelectFromDB.DDCClassification (stmtconn.connection, hmf.getOid()));
		queryResult = stmtconn.execute ( );
		
		while (queryResult.getResultSet ( ).next ( )) {
			
			Classification cl = new DDCClassification();
			cl.setValue(queryResult.getResultSet ( ).getString("DDC_Categorie"));
//			temp.setNumber (queryResult.getResultSet ( ).getInt ("number"));
			hmf.addClassfication(cl);
		}
		
		// Auswertung der DNB-Classifications-Werte
		stmtconn.loadStatement (SelectFromDB.DNBClassification (stmtconn.connection, hmf.getOid()));
		queryResult = stmtconn.execute ( );
		
		while (queryResult.getResultSet ( ).next ( )) {
			
			Classification cl = new DNBClassification();
			cl.setValue(queryResult.getResultSet ( ).getString("DNB_Categorie"));
//			temp.setNumber (queryResult.getResultSet ( ).getInt ("number"));
			hmf.addClassfication(cl);
		}

		// Auswertung der DINI-Set-Classifications-Werte
		stmtconn.loadStatement (SelectFromDB.DINISetClassification (stmtconn.connection, hmf.getOid()));
		queryResult = stmtconn.execute ( );
		
		while (queryResult.getResultSet ( ).next ( )) {
			
			Classification cl = new DINISetClassification();
			cl.setValue(queryResult.getResultSet ( ).getString("name"));
//			temp.setNumber (queryResult.getResultSet ( ).getInt ("number"));
			hmf.addClassfication(cl);
		}

		// Auswertung der Other-Classifications-Werte
		stmtconn.loadStatement (SelectFromDB.OtherClassification (stmtconn.connection, hmf.getOid()));
		queryResult = stmtconn.execute ( );
		
		while (queryResult.getResultSet ( ).next ( )) {
			
			Classification cl = new OtherClassification();
			cl.setValue(queryResult.getResultSet ( ).getString("name"));
//			temp.setNumber (queryResult.getResultSet ( ).getInt ("number"));
			hmf.addClassfication(cl);
		}
		hmf.setClassificationCounter(hmf.getClassificationList().size());

		// Auswertung der Keywords-Werte
		stmtconn.loadStatement (SelectFromDB.Keywords (stmtconn.connection, hmf.getOid()));
		queryResult = stmtconn.execute ( );
		
		while (queryResult.getResultSet ( ).next ( )) {
			
			Keyword temp = new Keyword();
			temp.setKeyword(queryResult.getResultSet ( ).getString("keyword"));
			temp.setLanguage(queryResult.getResultSet ( ).getString("lang"));
//			temp.setNumber (queryResult.getResultSet ( ).getInt ("number"));
			hmf.addKeyword(temp);
		}
		hmf.setKeywordCounter(hmf.getKeywordList().size());

		
		// Auswertung der Language-Werte
		stmtconn.loadStatement (SelectFromDB.Languages (stmtconn.connection, hmf.getOid()));
		queryResult = stmtconn.execute ( );
		
		while (queryResult.getResultSet ( ).next ( )) {
			
			Language temp = new Language();
			temp.setLanguage(queryResult.getResultSet ( ).getString("language"));
			temp.setIso639language(queryResult.getResultSet ( ).getString("iso639language"));
			temp.setNumber (queryResult.getResultSet ( ).getInt ("number"));
			hmf.addLanguage(temp);
		}
		hmf.setLanguageCounter(hmf.getLanguageList().size());

		
	}
}
