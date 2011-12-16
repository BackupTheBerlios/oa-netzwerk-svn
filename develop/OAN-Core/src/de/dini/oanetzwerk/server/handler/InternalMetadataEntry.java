package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.server.database.DBAccessNG;
import de.dini.oanetzwerk.server.database.MetadataDBMapper;
import de.dini.oanetzwerk.server.database.MultipleStatementConnection;
import de.dini.oanetzwerk.server.database.QueryResult;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.exceptions.MethodNotImplementedException;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;
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
import de.dini.oanetzwerk.utils.imf.Identifier;
import de.dini.oanetzwerk.utils.imf.InternalMetadata;
import de.dini.oanetzwerk.utils.imf.InternalMetadataJAXBMarshaller;
import de.dini.oanetzwerk.utils.imf.Keyword;
import de.dini.oanetzwerk.utils.imf.Language;
import de.dini.oanetzwerk.utils.imf.OtherClassification;
import de.dini.oanetzwerk.utils.imf.Publisher;
import de.dini.oanetzwerk.utils.imf.Title;
import de.dini.oanetzwerk.utils.imf.TypeValue;

/**
 * @author Manuel Klatt-Kafemann
 * @author Robin Malitz
 * @author Michael K&uuml;hn
 *
 */

public class InternalMetadataEntry extends AbstractKeyWordHandler implements KeyWord2DatabaseInterface {
	
	/**
	 * 
	 */
	
	private static Logger logger = Logger.getLogger (InternalMetadataEntry.class);
	
	/**
	 * 
	 */
	
	private final InternalMetadataJAXBMarshaller imMarsch;
	
	/**
	 * 
	 */
	
	public InternalMetadataEntry ( ) {
		
		super (InternalMetadataEntry.class.getName ( ), RestKeyword.InternalMetadataEntry);
		this.imMarsch = InternalMetadataJAXBMarshaller.getInstance ( );
	}

	/**
	 * @throws NotEnoughParametersException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 */
	
	@Override
	protected String deleteKeyWord (String [ ] path) throws NotEnoughParametersException {
		
		if (path.length < 1)
			throw new NotEnoughParametersException ("This method needs at least 1 parameter: the internal object ID");
		
		BigDecimal object_id;
		
		try {
			
			object_id = new BigDecimal (path [0]);
			
		} catch (NumberFormatException ex) {
			
			logger.error (path [0] + " is NOT a number!");
			
			this.rms = new RestMessage (RestKeyword.InternalMetadataEntry);
			this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription (path [0] + " is NOT a number!");
			
			return RestXmlCodec.encodeRestMessage (this.rms);
		}
		
		DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
		MultipleStatementConnection stmtconn = null;
		RestEntrySet res = new RestEntrySet ( );
		
		try {
			
			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection ( );
			
			stmtconn.loadStatement (DBAccessNG.deleteFromDB().Description (stmtconn.connection, object_id));
			logger.debug("BEFORE DeleteFromDB.Description");
			this.result = stmtconn.execute ( );
			
			logWarnings();

			
			logger.debug("AFTER DeleteFromDB.Description");
			
			stmtconn.loadStatement (DBAccessNG.deleteFromDB().DateValues (stmtconn.connection, object_id));
			logger.debug("BEFORE DeleteFromDB.DateValues");
			this.result = stmtconn.execute ( );
			logger.debug("AFTER DeleteFromDB.DateValues");
			
			logWarnings();
			
			stmtconn.loadStatement (DBAccessNG.deleteFromDB().Formats (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();

			stmtconn.loadStatement (DBAccessNG.deleteFromDB().Identifiers (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();

			stmtconn.loadStatement (DBAccessNG.deleteFromDB().TypeValue (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();

			stmtconn.loadStatement (DBAccessNG.deleteFromDB().Titles (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();

			stmtconn.loadStatement (DBAccessNG.deleteFromDB().Publishers (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();

			stmtconn.loadStatement (DBAccessNG.deleteFromDB().Object2Author (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();

			stmtconn.loadStatement (DBAccessNG.deleteFromDB().Object2Editor (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();
			
			stmtconn.loadStatement (DBAccessNG.deleteFromDB().Object2Contributor (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();

			stmtconn.loadStatement (DBAccessNG.deleteFromDB().Object2Language (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();

			stmtconn.loadStatement (DBAccessNG.deleteFromDB().Object2Iso639Language(stmtconn.connection, object_id, false));
			logger.debug("BEFORE DeleteFromDB.Object2Iso639Language");
			this.result = stmtconn.execute ( );
			logger.debug("AFTER DeleteFromDB.Object2Iso639Language");

			logWarnings();

			stmtconn.loadStatement (DBAccessNG.deleteFromDB().Object2Keywords (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();

			stmtconn.loadStatement (DBAccessNG.deleteFromDB().Other_Classification (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();

			stmtconn.loadStatement (DBAccessNG.deleteFromDB().DDC_Classification (stmtconn.connection, object_id, false));
			this.result = stmtconn.execute ( );
			
			logWarnings();

			stmtconn.loadStatement (DBAccessNG.deleteFromDB().DNB_Classification (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();

			stmtconn.loadStatement (DBAccessNG.deleteFromDB().DINI_Set_Classification (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();

//			stmtconn.loadStatement (DeleteFromDB.PersonWithoutReference (stmtconn.connection));
//			this.result = stmtconn.execute ( );
//			
//						if (this.result.getWarning ( ) != null) 
//			for (Throwable warning : result.getWarning ( ))
//				logger.warn (warning.getLocalizedMessage ( ));

			// TODO: Diese Anfrage braucht etwa 24 sec und sollte daher sparsam, nach einem Aggregatorlauf im Automodus gefeuert werden
//			stmtconn.loadStatement (DeleteFromDB.KeywordsWithoutReference (stmtconn.connection));
//			logger.debug("BEFORE DeleteFromDB.KeywordsWithoutReference");
//			this.result = stmtconn.execute ( );
//			logger.debug("AFTER DeleteFromDB.KeywordsWithoutReference");
//			
//						if (this.result.getWarning ( ) != null) 
//			for (Throwable warning : result.getWarning ( ))
//				logger.warn (warning.getLocalizedMessage ( ));

			
			// TODO: DELETE Languages/Iso639Languages without references !!!
			
			stmtconn.loadStatement (DBAccessNG.deleteFromDB().Other_Categories (stmtconn.connection));
			this.result = stmtconn.execute ( );
			
			logWarnings();
			
			logger.debug("BEFORE commit");
			stmtconn.commit ( );
			logger.debug("AFTER commit");
			
			res.addEntry("oid", object_id.toPlainString());
			this.rms.setStatus(RestStatusEnum.OK);
			this.rms.addEntrySet(res);
			
		} catch (SQLException ex) {
			
			try {
				
				stmtconn.rollback ( );
				
			} catch (SQLException ex1) {
				
				logger.error (ex1.getLocalizedMessage ( ), ex1);
			}
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} catch (WrongStatementException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			this.rms.setStatus (RestStatusEnum.WRONG_STATEMENT);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} finally {
			
			if (stmtconn != null) {
				
				try {
					
					stmtconn.close ( );
					stmtconn = null;
					
				} catch (SQLException ex) {
					
					logger.error (ex.getLocalizedMessage ( ), ex);
				}
			}
			
			this.rms.addEntrySet (res);
			res = null;
			this.result = null;
			dbng = null;
		}

		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	/**
	 * @throws NotEnoughParametersException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#getKeyWord(java.lang.String[])
	 */
	
	@Override
	protected String getKeyWord (String [ ] path) throws NotEnoughParametersException {
		
		if (path.length < 1)
			throw new NotEnoughParametersException ("This method needs at least 1 parameter: the internal object ID");

		// erzeuge imf-Object, das Schrittweise mit Daten befüllt wird
		InternalMetadata imf = new InternalMetadata ( );
		
		try {
			
            imf.setOid(new BigDecimal (path [0]));
			
		} catch (NumberFormatException ex) {
			
			logger.error (path [0] + " is NOT a number!", ex);
			
			this.rms = new RestMessage (RestKeyword.InternalMetadataEntry);
			this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription (path [0] + " is NOT a number!");
			
			return RestXmlCodec.encodeRestMessage (this.rms);
		}
		
		DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());;
		MultipleStatementConnection stmtconn = null;
		RestEntrySet res = new RestEntrySet ( );
		
		try {
			
			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection ( );
			
			// ausgelagert in separate Klasse, um den Code für andere Metadatenviews nachzunutzen
			MetadataDBMapper.fillInternalMetadataFromDB(imf, stmtconn);
			
			if(imf.isEmpty()) {
				// leeres IMF soll Fehler geben
				
				logger.info("leeres IMF über Keyword angefragt");
				this.rms.setStatus (RestStatusEnum.NO_OBJECT_FOUND_ERROR);
				this.rms.setStatusDescription ("Die Anfrage auf OID ("+imf.getOid()+") liefert ein leeres IMF aus der Datenbank.");
				
			} else {
				// nichtleeres IMF soll auch verschickt werden

				String xmlData;
				xmlData = imMarsch.marshall (imf);				
				res.addEntry ("internalmetadata", xmlData);
				this.rms.setStatus (RestStatusEnum.OK);
				this.rms.addEntrySet (res);
			}
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		}  catch (WrongStatementException ex) {
			
			logger.error ("An error occured while processing Get InternalMetadataEntry: " + ex.getLocalizedMessage ( ), ex);
			this.rms.setStatus (RestStatusEnum.WRONG_STATEMENT);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} finally {
			
			if (stmtconn != null) {
				
				try {
					
					stmtconn.close ( );
					stmtconn = null;
					
				} catch (SQLException ex) {
					
					logger.error (ex.getLocalizedMessage ( ), ex);
				}
			}
						
			res = null;
			this.result = null;
			dbng = null;
		}
//		
//		String xmlData;
//		xmlData = imMarsch.marshall (imf);
//		
////		RestEntrySet res = new RestEntrySet ( );
//		
//		res.addEntry ("internalmetadata", xmlData);
//		
//		this.rms.setStatus (RestStatusEnum.OK);
//		this.rms.addEntrySet (res);
		
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	/**
	 * @throws MethodNotImplementedException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#postKeyWord(java.lang.String[], java.lang.String)
	 */
	
	@Override
	protected String postKeyWord (String [ ] path, String data) throws MethodNotImplementedException {

		this.rms = new RestMessage (RestKeyword.InternalMetadataEntry);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		this.rms.setStatusDescription("POST method is not implemented for ressource '"+RestKeyword.InternalMetadataEntry+"'.");
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	/**
	 * @throws NotEnoughParametersException  
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#putKeyWord(java.lang.String[], java.lang.String)
	 */
	
	@Override
	protected String putKeyWord (String [ ] path, String data) throws NotEnoughParametersException {
		
		if (path.length < 1)
			throw new NotEnoughParametersException ("This method needs at least 1 parameters: the internal object ID");
		
		BigDecimal object_id;
		
		try {
			
			object_id = new BigDecimal (path [0]);
			
		} catch (NumberFormatException ex) {
			
			logger.error (path [0] + " is NOT a number!", ex);
			
			this.rms = new RestMessage (RestKeyword.InternalMetadataEntry);
			this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription (path [0] + " is NOT a number!");
			
			return RestXmlCodec.encodeRestMessage (this.rms);
		}
		
		DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
		MultipleStatementConnection stmtconn = null;
		
		RestMessage msgPutRequest = RestXmlCodec.decodeRestMessage (data);
		RestEntrySet res = msgPutRequest.getListEntrySets ( ).get (0);

		// FETCH ENTRY WITH XML
		
		String strXML = null;
		strXML = res.getValue ("internalmetadata");
		System.out.println("decoded internalmetadata: \n" + strXML);
		
		if (strXML == null) {
			
			this.rms = new RestMessage (RestKeyword.InternalMetadataEntry);
			this.rms.setStatus (RestStatusEnum.INCOMPLETE_ENTRYSET_ERROR);
			this.rms.setStatusDescription ("received no entry 'internalmetadata' :" + data);
			
			return RestXmlCodec.encodeRestMessage (this.rms);
		}
		
		// UNMARSHALL XML
		
		InternalMetadata imf = null;	
		
		try {
			
			imf = imMarsch.unmarshall (strXML);
			
		} catch (Exception ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
			this.rms = new RestMessage (RestKeyword.InternalMetadataEntry);
			this.rms.setStatus (RestStatusEnum.REST_XML_DECODING_ERROR);
			this.rms.setStatusDescription("unable to unmarshall xml " + strXML + " :" + ex);
			
			return RestXmlCodec.encodeRestMessage (this.rms);			
		}
	
		// PREPARE SQL STATEMENTS
		
		List <Title> titleList = imf.getTitleList ( );
		List <DateValue> dateValueList = imf.getDateValueList ( );
		List <Format> formatList = imf.getFormatList ( );
		List <Identifier> identifierList = imf.getIdentifierList ( );
		List <Description> descriptionList = imf.getDescriptionList();
		List <Publisher> publisherList = imf.getPublisherList();
		
		List <Author> authorList = imf.getAuthorList();
		List <Editor> editorList = imf.getEditorList();
		List <Contributor> contributorList = imf.getContributorList();
		
		List <Keyword> keywordList = imf.getKeywordList ( );
		List <TypeValue> typeValueList = imf.getTypeValueList ( );

		List <Language> languageList = imf.getLanguageList();
		List <Classification> classificationList = imf.getClassificationList();
		
		try {
			
			// 2 Werte, die eventuell auftretende Warnungen speichern können
			boolean aggregationWarning = false;
			String aggregationWarningDescription = "";
			
			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection ( );
			
			// Titel speichern
			if (titleList != null) {
				
				for (Title title : titleList) {
					
					if (logger.isDebugEnabled()) {
						logger.debug("Title-Informationen hinzufügen" + title.toString());
					}
					
					stmtconn.loadStatement (DBAccessNG.insertIntoDB().Title (stmtconn.connection, object_id, title.getQualifier(), title.getTitle(), title.getLang()));
					this.result = stmtconn.execute ( );
					
					logWarnings();

				}
			}

			// Datumswerte
			if (dateValueList != null) {
				for (DateValue dateValue : dateValueList) {
					try {

						if (logger.isDebugEnabled()) {
							logger.debug("dateValue hinzufügen" + dateValue.toString());
						}
						
						stmtconn.loadStatement (DBAccessNG.insertIntoDB().DateValue (stmtconn.connection, object_id, dateValue.getNumber(), HelperMethods.java2sqlDate(dateValue.getDateValue()), dateValue.getStringValue()));
						this.result = stmtconn.execute ( );
						
						logWarnings();

					} catch (ParseException ex) {
						
						logger.error("Datestamp with datevalue incorrect:  dateValue=" + dateValue.toString ( ), ex);
					}
				}
			}

			if (formatList != null) {
				for (Format format : formatList) {
					
					if (logger.isDebugEnabled()) {
						logger.debug("format-Informationen hinzufügen" + format.toString());
					}
					stmtconn.loadStatement (DBAccessNG.insertIntoDB().Format (stmtconn.connection, object_id, format.getNumber(), format.getSchema_f()));
					this.result = stmtconn.execute ( );
					
					logWarnings();

				}
			}

			if (identifierList != null) {
				for (Identifier identifier : identifierList) {
					
					if (logger.isDebugEnabled()) {
						logger.debug("identifier-Informationen hinzufügen" + identifier.toString());
					}
					stmtconn.loadStatement (DBAccessNG.insertIntoDB().Identifier (stmtconn.connection, object_id, identifier.getNumber(), identifier.getIdentifier()));
					this.result = stmtconn.execute ( );
					
					logWarnings();

				}
			}

			if (descriptionList != null) {
				for (Description description : descriptionList) {
					if (logger.isDebugEnabled()) {
						logger.debug("Description-Informationen hinzufügen" + description.toString());
					}				
					stmtconn.loadStatement (DBAccessNG.insertIntoDB().Description (stmtconn.connection, object_id, description.getNumber(), description.getDescription()));
					this.result = stmtconn.execute ( );
					
					logWarnings();

				}
			}

			if (publisherList != null) {
				for (Publisher publisher : publisherList) {
					
					if (logger.isDebugEnabled()) {
						logger.debug("Publisher-Informationen hinzufügen" + publisher.toString());
					}
					stmtconn.loadStatement (DBAccessNG.insertIntoDB().Publisher (stmtconn.connection, object_id, publisher.getNumber(), publisher.getName()));
					this.result = stmtconn.execute ( );
					
					logWarnings();

				}
			}

			if (authorList != null) {
				System.out.println("========== Autoren eintragen ===========");
				for (Author author : authorList) {
					
					
					if (logger.isDebugEnabled()) {
						logger.debug("Autoren-Informationen hinzufügen" + author.toString());
					}
					
					BigDecimal person_id = null;
					
					stmtconn.loadStatement (DBAccessNG.insertIntoDB().Person (stmtconn.connection, author.getFirstname(),
							author.getLastname(), author.getTitle(), author
							.getInstitution(), author.getEmail()));
					this.result = stmtconn.execute ( );
					
					logWarnings();

					stmtconn.loadStatement (DBAccessNG.selectFromDB().LatestPerson (stmtconn.connection, author.getFirstname(), author.getLastname()));
					this.result = stmtconn.execute ( );

					logWarnings();

					while (this.result.getResultSet ( ).next ( )) {
						
						person_id = this.result.getResultSet ( ).getBigDecimal(1);
					}
					
					stmtconn.loadStatement (DBAccessNG.insertIntoDB().Object2Author (stmtconn.connection, object_id, person_id, author.getNumber()));
					this.result = stmtconn.execute ( );
					
					logWarnings();

				}
			}
			
			if (editorList != null) {
				for (Editor editor : editorList) {
					
					if (logger.isDebugEnabled()) {
						logger.debug("Editor-Informationen hinzufügen" + editor.toString());
					}
					
					BigDecimal person_id = null;
					
					stmtconn.loadStatement (DBAccessNG.insertIntoDB().Person (stmtconn.connection, editor.getFirstname(),
							editor.getLastname(), editor.getTitle(), editor
							.getInstitution(), editor.getEmail()));
					this.result = stmtconn.execute ( );
					
					logWarnings();

					stmtconn.loadStatement (DBAccessNG.selectFromDB().LatestPerson (stmtconn.connection, editor.getFirstname(), editor.getLastname()));
					this.result = stmtconn.execute ( );
					
					logWarnings();

					while (this.result.getResultSet ( ).next ( )) {
						
						person_id = this.result.getResultSet ( ).getBigDecimal(1);
					}
					
					stmtconn.loadStatement (DBAccessNG.insertIntoDB().Object2Editor (stmtconn.connection, object_id, person_id, editor.getNumber()));
					this.result = stmtconn.execute ( );
					
					logWarnings();

				}
			}
			
			if (contributorList != null) {
				for (Contributor contributor : contributorList) {

					if (logger.isDebugEnabled()) {
						logger.debug("Contributor-Informationen hinzufügen" + contributor.toString());
					}
					BigDecimal person_id = null;
					
					stmtconn.loadStatement (DBAccessNG.insertIntoDB().Person (stmtconn.connection, contributor.getFirstname(), contributor
							.getLastname(), contributor.getTitle(), contributor
							.getInstitution(), contributor.getEmail()));
					this.result = stmtconn.execute ( );
					
					logWarnings();
					
					stmtconn.loadStatement (DBAccessNG.selectFromDB().LatestPerson (stmtconn.connection, contributor.getFirstname(), contributor.getLastname()));
					this.result = stmtconn.execute ( );
					
					logWarnings();

					while (this.result.getResultSet ( ).next ( )) {
						
						person_id = this.result.getResultSet ( ).getBigDecimal(1);
					}
					
					stmtconn.loadStatement (DBAccessNG.insertIntoDB().Object2Editor (stmtconn.connection, object_id, person_id, contributor.getNumber()));
					this.result = stmtconn.execute ( );
					
					logWarnings();

					//TODO: Object2Editor für contributor???
				}
			}

			if (keywordList != null) {
				PreparedStatement checkForExistingKeyword = null;
				ArrayList<BigDecimal> insertedKeywords = new ArrayList<BigDecimal>();
				for (Keyword keyword : keywordList) {
					
					if (logger.isDebugEnabled()) {
						logger.debug("Keyword-Informationen hinzufügen" + keyword.toString());
					}
					BigDecimal keyword_id = null;
					
					// test if the keyword is already present in the database
					checkForExistingKeyword = stmtconn.connection.prepareStatement("SELECT keyword_id, keyword FROM \"Keywords\" WHERE keyword = ?");
					checkForExistingKeyword.setString(1, keyword.getKeyword());
					stmtconn.loadStatement(checkForExistingKeyword);
					QueryResult qrs = stmtconn.execute();
					ResultSet rs = qrs.getResultSet();
					
					// keyword not found --> insert new keyword
					if (!rs.next()) {
						stmtconn.loadStatement (DBAccessNG.insertIntoDB().Keyword (stmtconn.connection, keyword.getKeyword(), keyword.getLanguage()));
						this.result = stmtconn.execute ( );
						logWarnings();
	
						if (this.result.getUpdateCount ( ) < 1) {
							logger.error("Fehler bei OID:" + object_id + " INSERT Keyword:'" + keyword.getKeyword() + "'");
							//warn, error, rollback, nothing????
						} 
						stmtconn.loadStatement (DBAccessNG.selectFromDB().LatestKeyword (stmtconn.connection, keyword.getKeyword(), keyword.getLanguage()));
						this.result = stmtconn.execute ( );
						
						logWarnings();
						
						
						while (this.result.getResultSet ( ).next ( )) {
							keyword_id = this.result.getResultSet ( ).getBigDecimal(1);
						}
					}
					// keyword found --> get ID to insert
					else {
						keyword_id = rs.getBigDecimal(1);
					}
					if(keyword_id != null) {
						if (!insertedKeywords.contains(keyword_id)) {
							insertedKeywords.add(keyword_id);
							try {
								stmtconn.loadStatement (DBAccessNG.insertIntoDB().Object2Keyword (stmtconn.connection, object_id, keyword_id));
								this.result = stmtconn.execute ( );
								
								logWarnings();
								
								if (this.result.getUpdateCount ( ) < 1) {
									logger.error("Fehler bei OID:" + object_id + " INSERT Object2Keyword:'" + keyword_id + "'");
								}
							} catch (SQLException e) {
								logger.warn(e);
							}
						}
						else {
							logger.debug("Keyword with ID "+keyword_id+" already entered for this publication");
						}
					}
					//
				}
			}
			
			if (languageList != null) {
				for (Language language : languageList) {

					if (logger.isDebugEnabled()) {
						logger.debug("Language-Informationen hinzufügen" + language.toString());
					}
					
					//////////////////
					// Originalwert
					//////////////////
					
					if(language.getLanguage() != null) {
					
						BigDecimal language_id = null;

						stmtconn.loadStatement (DBAccessNG.selectFromDB().LanguageByName (stmtconn.connection, language.getLanguage()));
						this.result = stmtconn.execute ( );
						
						logWarnings();
						
						if (!this.result.getResultSet ( ).next ( )) {

							stmtconn.loadStatement (DBAccessNG.insertIntoDB().Language (stmtconn.connection, language.getLanguage()));
							this.result = stmtconn.execute ( );

							logWarnings();

						}

						stmtconn.loadStatement (DBAccessNG.selectFromDB().LanguageByName (stmtconn.connection, language.getLanguage()));
						this.result = stmtconn.execute ( );

						logWarnings();

						while (this.result.getResultSet ( ).next ( )) {

							language_id = this.result.getResultSet ( ).getBigDecimal(1);
						}

						stmtconn.loadStatement (DBAccessNG.insertIntoDB().Object2Language (stmtconn.connection, object_id, language_id, language.getNumber()));
						this.result = stmtconn.execute ( );

						logWarnings();

					}
					
					//////////////////////
					// ISO 639 Kodierung
					//////////////////////
					
					BigDecimal iso639language_id = null;
					
					if(language.getIso639language() != null) {
					
						stmtconn.loadStatement (DBAccessNG.selectFromDB().Iso639LanguageByName (stmtconn.connection, language.getIso639language()));
						this.result = stmtconn.execute ( );

						logWarnings();

						if (!this.result.getResultSet ( ).next ( )) {

							stmtconn.loadStatement (DBAccessNG.insertIntoDB().Iso639Language (stmtconn.connection, language.getIso639language()));
							this.result = stmtconn.execute ( );

							logWarnings();

						}

						stmtconn.loadStatement (DBAccessNG.selectFromDB().Iso639LanguageByName (stmtconn.connection, language.getIso639language()));
						this.result = stmtconn.execute ( );

						logWarnings();

						while (this.result.getResultSet ( ).next ( )) {

							iso639language_id = this.result.getResultSet ( ).getBigDecimal(1);

						}
						
						stmtconn.loadStatement (DBAccessNG.insertIntoDB().Object2Iso639Language(stmtconn.connection, object_id, iso639language_id, language.getNumber(), false));
						this.result = stmtconn.execute ( );

						logWarnings();

					}
				}
			}
			
			
			HashMap<String, Boolean> usedDDCs = new HashMap<String, Boolean>();
			HashMap<BigDecimal, Boolean> usedDINICategories = new HashMap<BigDecimal, Boolean>();
			
			if (classificationList != null) {
				for (Classification classification : classificationList) {
					boolean notParsed = false;
					
					// fuer jeden Klassifikationstypen muessen unterschiedliche Aktionen erfolgen
					if (classification instanceof DDCClassification) {
						String ddcValue = null;
						
						stmtconn.loadStatement (DBAccessNG.selectFromDB().DDCCategoriesByCategorie (stmtconn.connection, classification.getValue()));
						this.result = stmtconn.execute ( );
						
						logWarnings();

						while (this.result.getResultSet ( ).next ( )) {
							
							ddcValue = this.result.getResultSet ( ).getString(1);
						}
		
						if (ddcValue != null) {
							
							// Daten zuordnen							
							stmtconn.loadStatement (DBAccessNG.insertIntoDB().DDCClassification (stmtconn.connection, object_id, ddcValue, classification.isGenerated()));
							this.result = stmtconn.execute ( );
							
							logWarnings();
							
							// abspeichern dass ein Eintrag in die DDC_Classification Tabelle eingefügt wurde (zur Duplikaterkennung)
							usedDDCs.put(ddcValue, true);
							
						} else {
							logger.warn("Could not find a DDC_Value for '" + classification.getValue() + "', will be stored as OtherClassification");
							notParsed = true;
							aggregationWarning = true;
							aggregationWarningDescription = aggregationWarningDescription + "\nCould not find a DDC_Value for '" + classification.getValue() + "', will be stored as OtherClassification";
						}
					}
					if (classification instanceof DNBClassification) {
						String dnbValue = null;
						
						stmtconn.loadStatement (DBAccessNG.selectFromDB().DNBCategoriesByCategorie (stmtconn.connection, classification.getValue()));
						this.result = stmtconn.execute ( );
						
						logWarnings();

						while (this.result.getResultSet ( ).next ( )) {
							
							dnbValue = this.result.getResultSet ( ).getString(1);
						}
						
						if (dnbValue != null) {
							
							// Daten zuordnen
							stmtconn.loadStatement (DBAccessNG.insertIntoDB().DNBClassification (stmtconn.connection, object_id, dnbValue));
							this.result = stmtconn.execute ( );
							
							logWarnings();
							
						} else {
							logger.warn("Could not find a DNB_Value for '" + classification.getValue() + "', will be stored as OtherClassification");
							notParsed = true;
							aggregationWarning = true;
							aggregationWarningDescription = aggregationWarningDescription + "\nCould not find a DNB_Value for '" + classification.getValue() + "', will be stored as OtherClassification";
						}

					}					
					if (classification instanceof DINISetClassification) {
						
						BigDecimal DINI_set_id = null;
						
						stmtconn.loadStatement (DBAccessNG.selectFromDB().DINISetCategoriesByName (stmtconn.connection, classification.getValue()));
						this.result = stmtconn.execute ( );
						
						logWarnings();

						logger.debug("before result of DINI set id fetch for '"+classification.getValue()+"'");
						
						while (this.result.getResultSet ( ).next ( )) {
							logger.debug("result = " + this.result.getResultSet ( ).getBigDecimal(1));
							DINI_set_id = this.result.getResultSet ( ).getBigDecimal(1);
						}
						
						if (DINI_set_id == null) {
							// wenn kein Wert in der DB gefunden werden konnte, soll
							// 1. eine Warnung geworfen werden und 
							// 2. der Eintrag in eine OtherClassification umgebogen wird
							notParsed = true;
							logger.warn("Could not find a DINI_Set_id for '" + classification.getValue() + "', will be stored as OtherClassification");
							aggregationWarning = true;
							aggregationWarningDescription = aggregationWarningDescription + "\nCould not find a DINI_Set_id for '" + classification.getValue() + "', will be stored as OtherClassification";
							
						} else {
						
							// Daten zuordnen
							stmtconn.loadStatement (DBAccessNG.insertIntoDB().DINISetClassification (stmtconn.connection, object_id, DINI_set_id, classification.isGenerated()));
							this.result = stmtconn.execute ( );
						
							logWarnings();
							
							// abspeichern welche DINI Categorien bereits eingetragen wurden damit Duplikate verhindert werden
							usedDINICategories.put(DINI_set_id, true);

						}
					}
					
					if ((classification instanceof OtherClassification) || (notParsed == true)) {
						
						// erstmal nachsehen ob es sich um einen mapbaren String handelt. Dann kann man nämlich zusätzlich ein
						if (Classification.isDDCMappable(classification.getValue())) {
							String ddcValue = null;
							
							stmtconn.loadStatement (DBAccessNG.selectFromDB().DDCCategoriesByCategorie (stmtconn.connection, classification.getValue()));
							this.result = stmtconn.execute ( );
							
							logWarnings();

							while (this.result.getResultSet ( ).next ( )) {
								ddcValue = this.result.getResultSet ( ).getString(1);
							}
			
							if (ddcValue != null && !usedDDCs.containsKey(ddcValue)) {
								// sicherstellen dass klar ist, dass es sich hierbei um generierte Daten handelt!
								classification.setGenerated(true);
								// Daten zuordnen							
								stmtconn.loadStatement (DBAccessNG.insertIntoDB().DDCClassification (stmtconn.connection, object_id, ddcValue, classification.isGenerated()));
								this.result = stmtconn.execute ( );
								
								logWarnings();
								
								// ddc Code mit in die Liste speichern, falls danach doch nochmal irgendwas gemappt werden sollte was auf DDC passt
								usedDDCs.put(ddcValue, true);
							}
						}
						else if (Classification.isDINI2010Mappable(classification.getValue())) {
							
							BigDecimal DINI_set_id = null;
							
							// Der übergebene Wert ist vielleicht mapbar, aber in die DB soll nur der gemappte Wert.
							// Daher muss hier erstmal konvertiert werden
							String mappedValue = Classification.mapToDINI2010(classification.getValue());
							logger.debug("mapped "+classification.getValue()+" to "+mappedValue);
							
							stmtconn.loadStatement (DBAccessNG.selectFromDB().DINISetCategoriesByName (stmtconn.connection, mappedValue));
							this.result = stmtconn.execute ( );
							
							logWarnings();

							logger.debug("before result of DINI set id fetch for mappable value '"+classification.getValue()+"'");
							
							while (this.result.getResultSet ( ).next ( )) {
								logger.debug("result = " + this.result.getResultSet ( ).getBigDecimal(1));
								DINI_set_id = this.result.getResultSet ( ).getBigDecimal(1);
							}
							
							if (DINI_set_id != null && !usedDINICategories.containsKey(DINI_set_id)) {
								// sicherstellen dass klar ist, dass es sich hierbei um generierte Daten handelt!
								classification.setGenerated(true);
								// Daten zuordnen
								stmtconn.loadStatement (DBAccessNG.insertIntoDB().DINISetClassification (stmtconn.connection, object_id, DINI_set_id, classification.isGenerated()));
								this.result = stmtconn.execute ( );
							
								logWarnings();
								
								// DINI Set ID mit in die Liste schreiben, damit beim Mapping der TypeValues auch keine Duplikate entstehen können
								usedDINICategories.put(DINI_set_id, true);

							}
						}
						BigDecimal other_id = null;

						// ID zum Klassifikationswort aus DB suchen 						
						stmtconn.loadStatement (DBAccessNG.selectFromDB().LatestOtherCategories (stmtconn.connection, classification.getValue()));
						this.result = stmtconn.execute ( );
						
						logWarnings();

						while (this.result.getResultSet ( ).next ( )) {
							
							other_id = this.result.getResultSet ( ).getBigDecimal(1);
						}

						if (other_id == null) {
							// Wort noch nicht vorhanden, neu eintragen
							// Klassifikation eintragen
							
							stmtconn.loadStatement (DBAccessNG.insertIntoDB().OtherCategories (stmtconn.connection, classification.getValue()));
							this.result = stmtconn.execute ( );
							
							logWarnings();

							stmtconn.loadStatement (DBAccessNG.selectFromDB().LatestOtherCategories (stmtconn.connection, classification.getValue()));
							this.result = stmtconn.execute ( );
							
							logWarnings();

							while (this.result.getResultSet ( ).next ( )) {
								other_id = this.result.getResultSet ( ).getBigDecimal(1);
							}
						}
						// ID dieser Klassifikation bestimmen und zuordnen
						stmtconn.loadStatement (DBAccessNG.insertIntoDB().OtherClassification (stmtconn.connection, object_id, other_id));
						this.result = stmtconn.execute ( );
						
						logWarnings();
					}
				}
			}
			
			if (typeValueList != null) {
				for (TypeValue typeValue : typeValueList) {
					
					if (logger.isDebugEnabled()) {
						logger.debug("TypeValue-Information auf Mapping mit DINI2010 Kategorien überprüfen und ggf einsetzen");
					}
					if (Classification.isDINI2010Mappable(typeValue.getTypeValue())) {
						BigDecimal DINI_set_id = null;
						
						stmtconn.loadStatement (DBAccessNG.selectFromDB().DINISetCategoriesByName (stmtconn.connection, typeValue.getTypeValue()));
						this.result = stmtconn.execute ( );
						
						logWarnings();

						logger.debug("before result of DINI set id fetch for mappable value '"+typeValue.getTypeValue()+"'");
						
						while (this.result.getResultSet ( ).next ( )) {
							logger.debug("result = " + this.result.getResultSet ( ).getBigDecimal(1));
							DINI_set_id = this.result.getResultSet ( ).getBigDecimal(1);
						}
						
						if (DINI_set_id != null && !usedDINICategories.containsKey(DINI_set_id)) {
							// sicherstellen dass klar ist, dass es sich hierbei um generierte Daten handelt!
							DINISetClassification classification = new DINISetClassification(typeValue.getTypeValue());
							classification.setGenerated(true);
							// Daten zuordnen
							stmtconn.loadStatement (DBAccessNG.insertIntoDB().DINISetClassification (stmtconn.connection, object_id, DINI_set_id, classification.isGenerated()));
							this.result = stmtconn.execute ( );
						
							logWarnings();
							
							// DINI Set ID in die Liste schreiben, falls hiernach nochmal was kommen sollte was gemappt wird
							usedDINICategories.put(DINI_set_id, true);
						}
					}
					
					if (logger.isDebugEnabled()) {
						logger.debug("TypeValue-Informationen hinzufügen" + typeValue.toString());
					}
					stmtconn.loadStatement (DBAccessNG.insertIntoDB().TypeValue (stmtconn.connection, object_id, typeValue.getTypeValue()));
					this.result = stmtconn.execute ( );
					
					logWarnings();
				}
			}

			stmtconn.commit ( );
			
			this.rms = new RestMessage (RestKeyword.InternalMetadataEntry);
			this.rms.setStatus (RestStatusEnum.OK);
			if (aggregationWarning) {
				this.rms.setStatus(RestStatusEnum.AGGREGATION_WARNING);
				this.rms.setStatusDescription(aggregationWarningDescription);
			}
			res = new RestEntrySet();
			res.addEntry ("oid", object_id.toPlainString ( ));
			this.rms.addEntrySet (res);
			
		} 
		catch (SQLException ex) {
			logger.error (ex.getLocalizedMessage ( ), ex);
			this.rms = new RestMessage (RestKeyword.InternalMetadataEntry);
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
		} 
		catch (WrongStatementException ex) {
			logger.error (ex.getLocalizedMessage ( ), ex);
			this.rms = new RestMessage (RestKeyword.InternalMetadataEntry);
			this.rms.setStatus (RestStatusEnum.WRONG_STATEMENT);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
		} 
		finally {
			
			if (stmtconn != null) {
				
				try {
					stmtconn.close ( );
					stmtconn = null;
				} catch (SQLException ex) {
					logger.error (ex.getLocalizedMessage ( ), ex);
				}
			}
			
			res = null;
			this.result = null;
			dbng = null;
		}
		return RestXmlCodec.encodeRestMessage (this.rms);
	}
	
	/**
	 * Checks if aggregated data for a given object id exists
	 * Returns a HTTP Status Code<br />
	 * 		200 - if the Object and its metadata are present
	 * 		204 - if the Object is present, but the metadata is not
	 * 		404 - if neither object nor metadata are present
	 * 		400 - if the provided parameter is not an object id
	 * 		500 - if there are server errors
	 */
	@Override
	protected String headKeyWord (String [ ] path) throws NotEnoughParametersException {
		if (path.length < 1)
			throw new NotEnoughParametersException ("This method needs at least 1 parameters: the internal object ID");
		
		BigDecimal object_id;
		
		try {
			
			object_id = new BigDecimal (path [0]);
			
		} catch (NumberFormatException ex) {
			
			logger.error (path [0] + " is NOT a number! - returning http status code 400", ex);
			
			return "400";
		}
		DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
		MultipleStatementConnection stmtconn = null;
		
		try {
			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection();
			if (MetadataDBMapper.objectAvailable(object_id, stmtconn)) {
				// object is present in the database
				logger.debug("object present in the database");
				if (MetadataDBMapper.aggregatedMetadataForObjectPresent(object_id, stmtconn)) {
					// object has metadata
					logger.debug("object has metadata - returning http status code 200");
					return "200";
				}
				else {
					// object does not have metadata
					logger.debug("object does not have metadata - returning http status code ");
					return "204";
				}
			}
			else {
				// object is not present in the database
				logger.debug("object not present in the database - returning http status code 404");
				return "404";
			}
		}
		catch (SQLException e) {
			logger.debug("SQLException: "+e.getLocalizedMessage());
			e.printStackTrace();
			logger.debug("returning http status code 500");
			return "500";
			
		}
		catch (WrongStatementException e) {
			logger.debug("WrongStatementException: "+e.getLocalizedMessage());
			e.printStackTrace();
			logger.debug("returning http status code 500");
		}
		// we should at least return something... so we return the code for "Object not found"
		return "404";
	}
	
}