package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.server.database.DBAccessNG;
import de.dini.oanetzwerk.server.database.DeleteFromDB;
import de.dini.oanetzwerk.server.database.InsertIntoDB;
import de.dini.oanetzwerk.server.database.MetadataDBMapper;
import de.dini.oanetzwerk.server.database.MultipleStatementConnection;
import de.dini.oanetzwerk.server.database.SelectFromDB;
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
		
		DBAccessNG dbng = new DBAccessNG (super.getDataSource ( ));
		MultipleStatementConnection stmtconn = null;
		RestEntrySet res = new RestEntrySet ( );
		
		try {
			
			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection ( );
			
			stmtconn.loadStatement (DeleteFromDB.Description (stmtconn.connection, object_id));
			logger.debug("BEFORE DeleteFromDB.Description");
			this.result = stmtconn.execute ( );
			
			if (this.result.getWarning ( ) != null) 
				for (Throwable warning : result.getWarning ( ))
					logger.warn (warning.getLocalizedMessage ( ));

			
			logger.debug("AFTER DeleteFromDB.Description");
			
			stmtconn.loadStatement (DeleteFromDB.DateValues (stmtconn.connection, object_id));
			logger.debug("BEFORE DeleteFromDB.DateValues");
			this.result = stmtconn.execute ( );
			logger.debug("AFTER DeleteFromDB.DateValues");
			
			if (this.result.getWarning ( ) != null) 
				for (Throwable warning : result.getWarning ( ))
					logger.warn (warning.getLocalizedMessage ( ));
			
			stmtconn.loadStatement (DeleteFromDB.Formats (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getWarning ( ) != null) 
				for (Throwable warning : result.getWarning ( ))
					logger.warn (warning.getLocalizedMessage ( ));

			stmtconn.loadStatement (DeleteFromDB.Identifiers (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getWarning ( ) != null) 
				for (Throwable warning : result.getWarning ( ))
					logger.warn (warning.getLocalizedMessage ( ));

			stmtconn.loadStatement (DeleteFromDB.TypeValue (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getWarning ( ) != null) 
				for (Throwable warning : result.getWarning ( ))
					logger.warn (warning.getLocalizedMessage ( ));

			stmtconn.loadStatement (DeleteFromDB.Titles (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getWarning ( ) != null) 
				for (Throwable warning : result.getWarning ( ))
					logger.warn (warning.getLocalizedMessage ( ));

			stmtconn.loadStatement (DeleteFromDB.Publishers (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getWarning ( ) != null) 
				for (Throwable warning : result.getWarning ( ))
					logger.warn (warning.getLocalizedMessage ( ));

			stmtconn.loadStatement (DeleteFromDB.Object2Author (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getWarning ( ) != null) 
				for (Throwable warning : result.getWarning ( ))
					logger.warn (warning.getLocalizedMessage ( ));

			stmtconn.loadStatement (DeleteFromDB.Object2Editor (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getWarning ( ) != null) 
				for (Throwable warning : result.getWarning ( ))
					logger.warn (warning.getLocalizedMessage ( ));
			
			stmtconn.loadStatement (DeleteFromDB.Object2Contributor (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getWarning ( ) != null) 
				for (Throwable warning : result.getWarning ( ))
					logger.warn (warning.getLocalizedMessage ( ));

			stmtconn.loadStatement (DeleteFromDB.Object2Language (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getWarning ( ) != null) 
				for (Throwable warning : result.getWarning ( ))
					logger.warn (warning.getLocalizedMessage ( ));

			stmtconn.loadStatement (DeleteFromDB.Object2Iso639Language(stmtconn.connection, object_id));
			logger.debug("BEFORE DeleteFromDB.Object2Iso639Language");
			this.result = stmtconn.execute ( );
			logger.debug("AFTER DeleteFromDB.Object2Iso639Language");

			if (this.result.getWarning ( ) != null) 
				for (Throwable warning : result.getWarning ( ))
					logger.warn (warning.getLocalizedMessage ( ));

			stmtconn.loadStatement (DeleteFromDB.Object2Keywords (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getWarning ( ) != null) 
				for (Throwable warning : result.getWarning ( ))
					logger.warn (warning.getLocalizedMessage ( ));

			stmtconn.loadStatement (DeleteFromDB.Other_Classification (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getWarning ( ) != null) 
				for (Throwable warning : result.getWarning ( ))
					logger.warn (warning.getLocalizedMessage ( ));

			stmtconn.loadStatement (DeleteFromDB.DDC_Classification (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getWarning ( ) != null) 
				for (Throwable warning : result.getWarning ( ))
					logger.warn (warning.getLocalizedMessage ( ));

			stmtconn.loadStatement (DeleteFromDB.DNB_Classification (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getWarning ( ) != null) 
				for (Throwable warning : result.getWarning ( ))
					logger.warn (warning.getLocalizedMessage ( ));

			stmtconn.loadStatement (DeleteFromDB.DINI_Set_Classification (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getWarning ( ) != null) 
				for (Throwable warning : result.getWarning ( ))
					logger.warn (warning.getLocalizedMessage ( ));

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
			
			stmtconn.loadStatement (DeleteFromDB.Other_Categories (stmtconn.connection));
			this.result = stmtconn.execute ( );
			
			if (this.result.getWarning ( ) != null) 
				for (Throwable warning : result.getWarning ( ))
					logger.warn (warning.getLocalizedMessage ( ));
			
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
		
		DBAccessNG dbng = new DBAccessNG (super.getDataSource ( ));
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
		
		DBAccessNG dbng = new DBAccessNG (super.getDataSource ( ));		
		MultipleStatementConnection stmtconn = null;
		
		RestMessage msgPutRequest = RestXmlCodec.decodeRestMessage (data);
		RestEntrySet res = msgPutRequest.getListEntrySets ( ).get (0);

		// FETCH ENTRY WITH XML
		
		String strXML = null;
		strXML = res.getValue ("internalmetadata");
		
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
					
					stmtconn.loadStatement (InsertIntoDB.Title (stmtconn.connection, object_id, title.getQualifier(), title.getTitle(), title.getLang()));
					this.result = stmtconn.execute ( );
					
					if (this.result.getWarning ( ) != null) 
						for (Throwable warning : result.getWarning ( ))
							logger.warn (warning.getLocalizedMessage ( ));

				}
			}

			// Datumswerte
			if (dateValueList != null) {
				for (DateValue dateValue : dateValueList) {
					try {

						if (logger.isDebugEnabled()) {
							logger.debug("dateValue hinzufügen" + dateValue.toString());
						}
						
						stmtconn.loadStatement (InsertIntoDB.DateValue (stmtconn.connection, object_id, dateValue.getNumber(), HelperMethods.java2sqlDate(dateValue.getDateValue()), dateValue.getStringValue()));
						this.result = stmtconn.execute ( );
						
						if (this.result.getWarning ( ) != null) 
							for (Throwable warning : result.getWarning ( ))
								logger.warn (warning.getLocalizedMessage ( ));

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
					stmtconn.loadStatement (InsertIntoDB.Format (stmtconn.connection, object_id, format.getNumber(), format.getSchema_f()));
					this.result = stmtconn.execute ( );
					
					if (this.result.getWarning ( ) != null) 
						for (Throwable warning : result.getWarning ( ))
							logger.warn (warning.getLocalizedMessage ( ));

				}
			}

			if (identifierList != null) {
				for (Identifier identifier : identifierList) {
					
					if (logger.isDebugEnabled()) {
						logger.debug("identifier-Informationen hinzufügen" + identifier.toString());
					}
					stmtconn.loadStatement (InsertIntoDB.Identifier (stmtconn.connection, object_id, identifier.getNumber(), identifier.getIdentifier()));
					this.result = stmtconn.execute ( );
					
					if (this.result.getWarning ( ) != null) 
						for (Throwable warning : result.getWarning ( ))
							logger.warn (warning.getLocalizedMessage ( ));

				}
			}

			if (descriptionList != null) {
				for (Description description : descriptionList) {
					if (logger.isDebugEnabled()) {
						logger.debug("Description-Informationen hinzufügen" + description.toString());
					}				
					stmtconn.loadStatement (InsertIntoDB.Description (stmtconn.connection, object_id, description.getNumber(), description.getDescription()));
					this.result = stmtconn.execute ( );
					
					if (this.result.getWarning ( ) != null) 
						for (Throwable warning : result.getWarning ( ))
							logger.warn (warning.getLocalizedMessage ( ));

				}
			}

			if (typeValueList != null) {
				for (TypeValue typeValue : typeValueList) {
					if (logger.isDebugEnabled()) {
						logger.debug("TypeValue-Informationen hinzufügen" + typeValue.toString());
					}
					stmtconn.loadStatement (InsertIntoDB.TypeValue (stmtconn.connection, object_id, typeValue.getTypeValue()));
					this.result = stmtconn.execute ( );
					
					if (this.result.getWarning ( ) != null) 
						for (Throwable warning : result.getWarning ( ))
							logger.warn (warning.getLocalizedMessage ( ));

				}
			}

			if (publisherList != null) {
				for (Publisher publisher : publisherList) {
					
					if (logger.isDebugEnabled()) {
						logger.debug("Publisher-Informationen hinzufügen" + publisher.toString());
					}
					stmtconn.loadStatement (InsertIntoDB.Publisher (stmtconn.connection, object_id, publisher.getNumber(), publisher.getName()));
					this.result = stmtconn.execute ( );
					
					if (this.result.getWarning ( ) != null) 
						for (Throwable warning : result.getWarning ( ))
							logger.warn (warning.getLocalizedMessage ( ));

				}
			}

			if (authorList != null) {
				for (Author author : authorList) {
					
					
					if (logger.isDebugEnabled()) {
						logger.debug("Autoren-Informationen hinzufügen" + author.toString());
					}
					
					BigDecimal person_id = null;
					
					stmtconn.loadStatement (InsertIntoDB.Person (stmtconn.connection, author.getFirstname(),
							author.getLastname(), author.getTitle(), author
							.getInstitution(), author.getEmail()));
					this.result = stmtconn.execute ( );
					
					if (this.result.getWarning ( ) != null) 
						for (Throwable warning : result.getWarning ( ))
							logger.warn (warning.getLocalizedMessage ( ));

					stmtconn.loadStatement (SelectFromDB.LatestPerson (stmtconn.connection, author.getFirstname(), author.getLastname()));
					this.result = stmtconn.execute ( );

					if (this.result.getWarning ( ) != null) 
						for (Throwable warning : result.getWarning ( ))
							logger.warn (warning.getLocalizedMessage ( ));

					while (this.result.getResultSet ( ).next ( )) {
						
						person_id = this.result.getResultSet ( ).getBigDecimal(1);
					}
					
					stmtconn.loadStatement (InsertIntoDB.Object2Author (stmtconn.connection, object_id, person_id, author.getNumber()));
					this.result = stmtconn.execute ( );
					
					if (this.result.getWarning ( ) != null) 
						for (Throwable warning : result.getWarning ( ))
							logger.warn (warning.getLocalizedMessage ( ));

				}
			}
			
			if (editorList != null) {
				for (Editor editor : editorList) {
					
					if (logger.isDebugEnabled()) {
						logger.debug("Editor-Informationen hinzufügen" + editor.toString());
					}
					
					BigDecimal person_id = null;
					
					stmtconn.loadStatement (InsertIntoDB.Person (stmtconn.connection, editor.getFirstname(),
							editor.getLastname(), editor.getTitle(), editor
							.getInstitution(), editor.getEmail()));
					this.result = stmtconn.execute ( );
					
					if (this.result.getWarning ( ) != null) 
						for (Throwable warning : result.getWarning ( ))
							logger.warn (warning.getLocalizedMessage ( ));

					stmtconn.loadStatement (SelectFromDB.LatestPerson (stmtconn.connection, editor.getFirstname(), editor.getLastname()));
					this.result = stmtconn.execute ( );
					
					if (this.result.getWarning ( ) != null) 
						for (Throwable warning : result.getWarning ( ))
							logger.warn (warning.getLocalizedMessage ( ));

					while (this.result.getResultSet ( ).next ( )) {
						
						person_id = this.result.getResultSet ( ).getBigDecimal(1);
					}
					
					stmtconn.loadStatement (InsertIntoDB.Object2Editor (stmtconn.connection, object_id, person_id, editor.getNumber()));
					this.result = stmtconn.execute ( );
					
					if (this.result.getWarning ( ) != null) 
						for (Throwable warning : result.getWarning ( ))
							logger.warn (warning.getLocalizedMessage ( ));

				}
			}
			
			if (contributorList != null) {
				for (Contributor contributor : contributorList) {

					if (logger.isDebugEnabled()) {
						logger.debug("Contributor-Informationen hinzufügen" + contributor.toString());
					}
					BigDecimal person_id = null;
					
					stmtconn.loadStatement (InsertIntoDB.Person (stmtconn.connection, contributor.getFirstname(), contributor
							.getLastname(), contributor.getTitle(), contributor
							.getInstitution(), contributor.getEmail()));
					this.result = stmtconn.execute ( );
					
					if (this.result.getWarning ( ) != null) 
						for (Throwable warning : result.getWarning ( ))
							logger.warn (warning.getLocalizedMessage ( ));
					
					stmtconn.loadStatement (SelectFromDB.LatestPerson (stmtconn.connection, contributor.getFirstname(), contributor.getLastname()));
					this.result = stmtconn.execute ( );
					
					if (this.result.getWarning ( ) != null) 
						for (Throwable warning : result.getWarning ( ))
							logger.warn (warning.getLocalizedMessage ( ));

					while (this.result.getResultSet ( ).next ( )) {
						
						person_id = this.result.getResultSet ( ).getBigDecimal(1);
					}
					
					stmtconn.loadStatement (InsertIntoDB.Object2Editor (stmtconn.connection, object_id, person_id, contributor.getNumber()));
					this.result = stmtconn.execute ( );
					
					if (this.result.getWarning ( ) != null) 
						for (Throwable warning : result.getWarning ( ))
							logger.warn (warning.getLocalizedMessage ( ));

					//TODO: Object2Editor für contributor???
				}
			}

			if (keywordList != null) {
				for (Keyword keyword : keywordList) {
					
					if (logger.isDebugEnabled()) {
						logger.debug("Keyword-Informationen hinzufügen" + keyword.toString());
					}
					BigDecimal keyword_id = null;
					
					stmtconn.loadStatement (InsertIntoDB.Keyword (stmtconn.connection, keyword.getKeyword(), keyword.getLanguage()));
					this.result = stmtconn.execute ( );
					
					if (this.result.getWarning ( ) != null) 
						for (Throwable warning : result.getWarning ( ))
							logger.warn (warning.getLocalizedMessage ( ));

					if (this.result.getUpdateCount ( ) < 1) {
						logger.error("Fehler bei OID:" + object_id + " INSERT Keyword:'" + keyword.getKeyword() + "'");
						//warn, error, rollback, nothing????
					} 
					
					stmtconn.loadStatement (SelectFromDB.LatestKeyword (stmtconn.connection, keyword.getKeyword(), keyword.getLanguage()));
					this.result = stmtconn.execute ( );
					
					if (this.result.getWarning ( ) != null) 
						for (Throwable warning : result.getWarning ( ))
							logger.warn (warning.getLocalizedMessage ( ));
					
					while (this.result.getResultSet ( ).next ( )) {
						keyword_id = this.result.getResultSet ( ).getBigDecimal(1);
					}
					
					if(keyword_id != null) {
					
						stmtconn.loadStatement (InsertIntoDB.Object2Keyword (stmtconn.connection, object_id, keyword_id));
						this.result = stmtconn.execute ( );
						
						if (this.result.getWarning ( ) != null) 
							for (Throwable warning : result.getWarning ( ))
								logger.warn (warning.getLocalizedMessage ( ));

						if (this.result.getUpdateCount ( ) < 1) {
							logger.error("Fehler bei OID:" + object_id + " INSERT Object2Keyword:'" + keyword_id + "'");
						}
					}
										
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

						stmtconn.loadStatement (SelectFromDB.LanguageByName (stmtconn.connection, language.getLanguage()));
						this.result = stmtconn.execute ( );
						
						if (this.result.getWarning ( ) != null) 
							for (Throwable warning : result.getWarning ( ))
								logger.warn (warning.getLocalizedMessage ( ));
						
						if (!this.result.getResultSet ( ).next ( )) {

							stmtconn.loadStatement (InsertIntoDB.Language (stmtconn.connection, language.getLanguage()));
							this.result = stmtconn.execute ( );

							if (this.result.getWarning ( ) != null) 
								for (Throwable warning : result.getWarning ( ))
									logger.warn (warning.getLocalizedMessage ( ));

						}

						stmtconn.loadStatement (SelectFromDB.LanguageByName (stmtconn.connection, language.getLanguage()));
						this.result = stmtconn.execute ( );

						if (this.result.getWarning ( ) != null) 
							for (Throwable warning : result.getWarning ( ))
								logger.warn (warning.getLocalizedMessage ( ));

						while (this.result.getResultSet ( ).next ( )) {

							language_id = this.result.getResultSet ( ).getBigDecimal(1);
						}

						stmtconn.loadStatement (InsertIntoDB.Object2Language (stmtconn.connection, object_id, language_id, language.getNumber()));
						this.result = stmtconn.execute ( );

						if (this.result.getWarning ( ) != null) 
							for (Throwable warning : result.getWarning ( ))
								logger.warn (warning.getLocalizedMessage ( ));

					}
					
					//////////////////////
					// ISO 639 Kodierung
					//////////////////////
					
					BigDecimal iso639language_id = null;
					
					if(language.getIso639language() != null) {
					
						stmtconn.loadStatement (SelectFromDB.Iso639LanguageByName (stmtconn.connection, language.getIso639language()));
						this.result = stmtconn.execute ( );

						if (this.result.getWarning ( ) != null) 
							for (Throwable warning : result.getWarning ( ))
								logger.warn (warning.getLocalizedMessage ( ));

						if (!this.result.getResultSet ( ).next ( )) {

							stmtconn.loadStatement (InsertIntoDB.Iso639Language (stmtconn.connection, language.getIso639language()));
							this.result = stmtconn.execute ( );

							if (this.result.getWarning ( ) != null) 
								for (Throwable warning : result.getWarning ( ))
									logger.warn (warning.getLocalizedMessage ( ));

						}

						stmtconn.loadStatement (SelectFromDB.Iso639LanguageByName (stmtconn.connection, language.getIso639language()));
						this.result = stmtconn.execute ( );

						if (this.result.getWarning ( ) != null) 
							for (Throwable warning : result.getWarning ( ))
								logger.warn (warning.getLocalizedMessage ( ));

						while (this.result.getResultSet ( ).next ( )) {

							iso639language_id = this.result.getResultSet ( ).getBigDecimal(1);

						}
						
						stmtconn.loadStatement (InsertIntoDB.Object2Iso639Language(stmtconn.connection, object_id, iso639language_id, language.getNumber()));
						this.result = stmtconn.execute ( );

						if (this.result.getWarning ( ) != null) 
							for (Throwable warning : result.getWarning ( ))
								logger.warn (warning.getLocalizedMessage ( ));

					}
				}
			}
			
			if (classificationList != null) {
				for (Classification classification : classificationList) {
					boolean notParsed = false;
					
					// fuer jeden Klassifikationstypen muessen unterschiedliche Aktionen erfolgen
					if (classification instanceof DDCClassification) {
						String ddcValue = null;
						
						stmtconn.loadStatement (SelectFromDB.DDCCategoriesByCategorie (stmtconn.connection, classification.getValue()));
						this.result = stmtconn.execute ( );
						
						if (this.result.getWarning ( ) != null) 
							for (Throwable warning : result.getWarning ( ))
								logger.warn (warning.getLocalizedMessage ( ));

						while (this.result.getResultSet ( ).next ( )) {
							
							ddcValue = this.result.getResultSet ( ).getString(1);
						}
		
						if (ddcValue != null) {
							
							// Daten zuordnen							
							stmtconn.loadStatement (InsertIntoDB.DDCClassification (stmtconn.connection, object_id, ddcValue));
							this.result = stmtconn.execute ( );
							
							if (this.result.getWarning ( ) != null) 
								for (Throwable warning : result.getWarning ( ))
									logger.warn (warning.getLocalizedMessage ( ));
							
						} else {
							logger.warn("Could not find a DDC_Value for '" + classification.getValue() + "', will be stored as OtherClassification");
							notParsed = true;
							aggregationWarning = true;
							aggregationWarningDescription = aggregationWarningDescription + "\nCould not find a DDC_Value for '" + classification.getValue() + "', will be stored as OtherClassification";
						}
					}
					if (classification instanceof DNBClassification) {
						String dnbValue = null;
						
						stmtconn.loadStatement (SelectFromDB.DNBCategoriesByCategorie (stmtconn.connection, classification.getValue()));
						this.result = stmtconn.execute ( );
						
						if (this.result.getWarning ( ) != null) 
							for (Throwable warning : result.getWarning ( ))
								logger.warn (warning.getLocalizedMessage ( ));

						while (this.result.getResultSet ( ).next ( )) {
							
							dnbValue = this.result.getResultSet ( ).getString(1);
						}
						
						if (dnbValue != null) {
							
							// Daten zuordnen
							stmtconn.loadStatement (InsertIntoDB.DNBClassification (stmtconn.connection, object_id, dnbValue));
							this.result = stmtconn.execute ( );
							
							if (this.result.getWarning ( ) != null) 
								for (Throwable warning : result.getWarning ( ))
									logger.warn (warning.getLocalizedMessage ( ));
							
						} else {
							logger.warn("Could not find a DNB_Value for '" + classification.getValue() + "', will be stored as OtherClassification");
							notParsed = true;
							aggregationWarning = true;
							aggregationWarningDescription = aggregationWarningDescription + "\nCould not find a DNB_Value for '" + classification.getValue() + "', will be stored as OtherClassification";
						}

					}					
					if (classification instanceof DINISetClassification) {
						
						BigDecimal DINI_set_id = null;
						
						stmtconn.loadStatement (SelectFromDB.DINISetCategoriesByName (stmtconn.connection, classification.getValue()));
						this.result = stmtconn.execute ( );
						
						if (this.result.getWarning ( ) != null) 
							for (Throwable warning : result.getWarning ( ))
								logger.warn (warning.getLocalizedMessage ( ));

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
							stmtconn.loadStatement (InsertIntoDB.DINISetClassification (stmtconn.connection, object_id, DINI_set_id));
							this.result = stmtconn.execute ( );
						
							if (this.result.getWarning ( ) != null) 
								for (Throwable warning : result.getWarning ( ))
									logger.warn (warning.getLocalizedMessage ( ));

						}
					}
					
					if ((classification instanceof OtherClassification) || (notParsed == true)) {
						
						BigDecimal other_id = null;

						// ID zum Klassifikationswort aus DB suchen 						
						stmtconn.loadStatement (SelectFromDB.LatestOtherCategories (stmtconn.connection, classification.getValue()));
						this.result = stmtconn.execute ( );
						
						if (this.result.getWarning ( ) != null) 
							for (Throwable warning : result.getWarning ( ))
								logger.warn (warning.getLocalizedMessage ( ));

						while (this.result.getResultSet ( ).next ( )) {
							
							other_id = this.result.getResultSet ( ).getBigDecimal(1);
						}

						if (other_id == null) {
							// Wort noch nicht vorhanden, neu eintragen
							// Klassifikation eintragen
							
							stmtconn.loadStatement (InsertIntoDB.OtherCategories (stmtconn.connection, classification.getValue()));
							this.result = stmtconn.execute ( );
							
							if (this.result.getWarning ( ) != null) 
								for (Throwable warning : result.getWarning ( ))
									logger.warn (warning.getLocalizedMessage ( ));

							stmtconn.loadStatement (SelectFromDB.LatestOtherCategories (stmtconn.connection, classification.getValue()));
							this.result = stmtconn.execute ( );
							
							if (this.result.getWarning ( ) != null) 
								for (Throwable warning : result.getWarning ( ))
									logger.warn (warning.getLocalizedMessage ( ));

							while (this.result.getResultSet ( ).next ( )) {
								
								other_id = this.result.getResultSet ( ).getBigDecimal(1);
							}
						}
						// ID dieser Klassifikation bestimmen und zuordnen
						stmtconn.loadStatement (InsertIntoDB.OtherClassification (stmtconn.connection, object_id, other_id));
						this.result = stmtconn.execute ( );
						
						if (this.result.getWarning ( ) != null) 
							for (Throwable warning : result.getWarning ( ))
								logger.warn (warning.getLocalizedMessage ( ));

					}
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
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			this.rms = new RestMessage (RestKeyword.InternalMetadataEntry);
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} catch (WrongStatementException ex) {

			logger.error (ex.getLocalizedMessage ( ), ex);
			this.rms = new RestMessage (RestKeyword.InternalMetadataEntry);
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
		
		return RestXmlCodec.encodeRestMessage (this.rms);
	}
}
