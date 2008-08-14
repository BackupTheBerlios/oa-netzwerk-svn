//package technotest;
//
////import java.math.BigDecimal;
////import java.sql.ResultSet;
////import java.sql.SQLException;
////import java.text.ParseException;
////import java.util.List;
//
//import java.math.BigDecimal;
//import java.sql.SQLException;
//
//import org.apache.log4j.Logger;
//
//import de.dini.oanetzwerk.codec.RestEntrySet;
//import de.dini.oanetzwerk.codec.RestKeyword;
//import de.dini.oanetzwerk.codec.RestMessage;
//import de.dini.oanetzwerk.codec.RestStatusEnum;
//import de.dini.oanetzwerk.codec.RestXmlCodec;
//import de.dini.oanetzwerk.server.database.DBAccessNG;
//import de.dini.oanetzwerk.server.database.MultipleStatementConnection;
//import de.dini.oanetzwerk.server.database.QueryResult;
//import de.dini.oanetzwerk.server.database.SelectFromDB;
//import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;
//import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;
//import de.dini.oanetzwerk.utils.imf.Author;
//import de.dini.oanetzwerk.utils.imf.Classification;
//import de.dini.oanetzwerk.utils.imf.Contributor;
//import de.dini.oanetzwerk.utils.imf.DDCClassification;
//import de.dini.oanetzwerk.utils.imf.DINISetClassification;
//import de.dini.oanetzwerk.utils.imf.DNBClassification;
//import de.dini.oanetzwerk.utils.imf.DateValue;
//import de.dini.oanetzwerk.utils.imf.Description;
//import de.dini.oanetzwerk.utils.imf.Editor;
//import de.dini.oanetzwerk.utils.imf.Format;
//import de.dini.oanetzwerk.utils.imf.Identifier;
//import de.dini.oanetzwerk.utils.imf.InternalMetadata;
//import de.dini.oanetzwerk.utils.imf.Keyword;
//import de.dini.oanetzwerk.utils.imf.Language;
//import de.dini.oanetzwerk.utils.imf.OtherClassification;
//import de.dini.oanetzwerk.utils.imf.Publisher;
//import de.dini.oanetzwerk.utils.imf.Title;
//import de.dini.oanetzwerk.utils.imf.TypeValue;
//
////import de.dini.oanetzwerk.utils.HelperMethods;
////import de.dini.oanetzwerk.utils.imf.*;
//
//public class TestInternal {
//
//	
//	static Logger logger = Logger.getLogger(TestInternal.class);
//	
//	
//	public static void getNewVersion(BigDecimal object_id) {
//		
//		InternalMetadata imf = new InternalMetadata ( );
//		
//		BigDecimal oid;
//		oid = object_id;
//		
//		QueryResult result;
//		
//		DBAccessNG dbng = new DBAccessNG ( );
//		MultipleStatementConnection stmtconn = null;
//		RestEntrySet res = new RestEntrySet ( );
//		
//		try {
//			
//			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection ( );
//			
//			stmtconn.loadStatement (SelectFromDB.Title (stmtconn.connection, oid));
//			result = stmtconn.execute ( );
//			
//			// Auswertung der Titel
//			while (result.getResultSet ( ).next ( )) {
//				
//				Title temp = new Title ( );
//				temp.setTitle (result.getResultSet ( ).getString ("title"));
//				temp.setQualifier (result.getResultSet ( ).getString ("qualifier"));
//				temp.setLang (result.getResultSet ( ).getString ("lang"));
////				temp.setNumber (this.result.getResultSet ( ).getInt ("number"));
//				imf.addTitle (temp);
//			}
//			
//			// Auswertung der Titel
//			//rs = db.selectTitle (oid);
///*			if (rs == null) {
//				System.out.println("kein Title vorhanden");
//				// logger.warn ("resultset empty!");
//			} else {
//				System.out.println("Title vorhanden");
//					while (rs.next ( )) {
//						Title temp = new Title ( );
//						temp.setTitle (rs.getString ("title"));
//						temp.setQualifier (rs.getString ("qualifier"));
//						temp.setLang (rs.getString ("lang"));
////						temp.setNumber (rs.getInt ("number"));
//						imf.addTitle (temp);
//				}
//			}*/
//
//
//			// Auswertung der Autoren
//			stmtconn.loadStatement (SelectFromDB.Authors (stmtconn.connection, oid));
//			this.result = stmtconn.execute ( );
//			
//			while (this.result.getResultSet ( ).next ( )) {
//				
//				Author temp = new Author ( );
//				temp.setNumber(this.result.getResultSet ( ).getInt("number"));
//				temp.setFirstname(this.result.getResultSet ( ).getString("firstname"));
//				temp.setLastname(this.result.getResultSet ( ).getString("lastname"));
//				temp.setInstitution(this.result.getResultSet ( ).getString("institution"));
//				temp.setEmail(this.result.getResultSet ( ).getString("email"));
//				temp.setTitle(this.result.getResultSet ( ).getString("title"));
//				imf.addAuthor (temp);
//			}
//			
////			rs = db.selectAuthors(oid);
////			if (rs == null) {
////				System.out.println("kein Author vorhanden");
////			} else {
////				System.out.println("Author vorhanden");
////				try {
////					while (rs.next ( )) {
////						Author temp = new Author ( );
////						temp.setNumber(rs.getInt("number"));
////						temp.setFirstname(rs.getString("firstname"));
////						temp.setLastname(rs.getString("lastname"));
////						temp.setInstitution(rs.getString("institution"));
////						temp.setEmail(rs.getString("email"));
////						temp.setTitle(rs.getString("title"));
////						imf.addAuthor (temp);
////					}
////				} catch (SQLException ex) {
////					ex.printStackTrace ( );
////				}
////			}	
//
//
//			// Auswertung der Editoren
//			stmtconn.loadStatement (SelectFromDB.Editors (stmtconn.connection, oid));
//			this.result = stmtconn.execute ( );
//			
//			while (this.result.getResultSet ( ).next ( )) {
//				
//				Editor temp = new Editor ( );
//				temp.setNumber(this.result.getResultSet ( ).getInt("number"));
//				temp.setFirstname(this.result.getResultSet ( ).getString("firstname"));
//				temp.setLastname(this.result.getResultSet ( ).getString("lastname"));
//				temp.setInstitution(this.result.getResultSet ( ).getString("institution"));
//				temp.setEmail(this.result.getResultSet ( ).getString("email"));
//				temp.setTitle(this.result.getResultSet ( ).getString("title"));
//				imf.addEditor (temp);
//			}
////			
////			rs = db.selectEditors(oid);
////			if (rs == null) {
////				System.out.println("kein Editor vorhanden");
////			} else {
////				System.out.println("Editor vorhanden");
////				try {
////					while (rs.next ( )) {
////						Editor temp = new Editor ( );
////						temp.setNumber(rs.getInt("number"));
////						temp.setFirstname(rs.getString("firstname"));
////						temp.setLastname(rs.getString("lastname"));
////						temp.setInstitution(rs.getString("institution"));
////						temp.setEmail(rs.getString("email"));
////						temp.setTitle(rs.getString("title"));
////						imf.addEditor (temp);
////					}
////				} catch (SQLException ex) {
////					ex.printStackTrace ( );
////				}
////			}	
//			
//
//
//			// Auswertung der Bearbeiter
//			stmtconn.loadStatement (SelectFromDB.Contributors (stmtconn.connection, oid));
//			this.result = stmtconn.execute ( );
//			
//			while (this.result.getResultSet ( ).next ( )) {
//				
//				Contributor temp = new Contributor ( );
//				temp.setNumber(this.result.getResultSet ( ).getInt("number"));
//				temp.setFirstname(this.result.getResultSet ( ).getString("firstname"));
//				temp.setLastname(this.result.getResultSet ( ).getString("lastname"));
//				temp.setInstitution(this.result.getResultSet ( ).getString("institution"));
//				temp.setEmail(this.result.getResultSet ( ).getString("email"));
//				temp.setTitle(this.result.getResultSet ( ).getString("title"));
//				imf.addContributor (temp);
//			}
//
////			rs = db.selectContributors(oid);
////			if (rs == null) {
////				System.out.println("kein Contirbutor vorhanden");
////			} else {
////				System.out.println("Contributor vorhanden");
////				try {
////					while (rs.next ( )) {
////						Contributor temp = new Contributor ( );
////						temp.setNumber(rs.getInt("number"));
////						temp.setFirstname(rs.getString("firstname"));
////						temp.setLastname(rs.getString("lastname"));
////						temp.setInstitution(rs.getString("institution"));
////						temp.setEmail(rs.getString("email"));
////						temp.setTitle(rs.getString("title"));
////						imf.addContributor (temp);
////					}
////				} catch (SQLException ex) {
////					ex.printStackTrace ( );
////				}
////			}			
//			
//			
//			
//			// Auswertung des Formats
//			stmtconn.loadStatement (SelectFromDB.Format (stmtconn.connection, oid));
//			this.result = stmtconn.execute ( );
//			
//			while (this.result.getResultSet ( ).next ( )) {
//				
//				Format temp = new Format ( );
//				temp.setSchema_f(this.result.getResultSet ( ).getString ("schema_f"));
//				temp.setNumber (this.result.getResultSet ( ).getInt ("number"));
//				imf.addFormat (temp);
//			}
//
////			rs = db.selectFormat(oid);
////			if (rs == null) {
////				System.out.println("kein Format");
////			} else {
////				System.out.println("Format vorhanden");
////				try {
////					while (rs.next ( )) {
////						Format temp = new Format ( );
////						temp.setSchema_f(rs.getString ("schema_f"));
////						temp.setNumber (rs.getInt ("number"));
////						imf.addFormat (temp);
////					}
////				} catch (SQLException ex) {
////					ex.printStackTrace ( );
////				}
////			}
//			
//			
//			// Auswertung des Identifiers
//			stmtconn.loadStatement (SelectFromDB.Identifier (stmtconn.connection, oid));
//			this.result = stmtconn.execute ( );
//			
//			while (this.result.getResultSet ( ).next ( )) {
//				
//				Identifier temp = new Identifier ( );
//				temp.setIdentifier(this.result.getResultSet ( ).getString ("identifier"));
////				temp.setLanguage(this.result.getResultSet ( ).getString ("lang"));
//				temp.setNumber (this.result.getResultSet ( ).getInt ("number"));
//				imf.addIdentifier (temp);
//			}
////			rs = db.selectIdentifier(oid);
////			if (rs == null) {
////				System.out.println("kein Identifier");
////			} else {
////				System.out.println("Identifier vorhanden");
////				try {
////					while (rs.next ( )) {
////						Identifier temp = new Identifier ( );
////						temp.setIdentifier(rs.getString ("identifier"));
//////						temp.setLanguage(rs.getString ("lang"));
////						temp.setNumber (rs.getInt ("number"));
////						imf.addIdentifier (temp);
////					}
////				} catch (SQLException ex) {
////					ex.printStackTrace ( );
////				}
////			}
//
//			
//			// Auswertung der Description
//			stmtconn.loadStatement (SelectFromDB.Description (stmtconn.connection, oid));
//			this.result = stmtconn.execute ( );
//			
//			while (this.result.getResultSet ( ).next ( )) {
//				
//				Description temp = new Description ( );
//				temp.setDescription(this.result.getResultSet ( ).getString ("abstract"));
//				temp.setLanguage(this.result.getResultSet ( ).getString ("lang"));
//				temp.setNumber (this.result.getResultSet ( ).getInt ("number"));
//				imf.addDescription (temp);
//			}
////			rs = db.selectDescription(oid);
////			if (rs == null) {
////				System.out.println("keine Description");
////			} else {
////				System.out.println("Description vorhanden");
////				try {
////					while (rs.next ( )) {
////						Description temp = new Description ( );
////						temp.setDescription(rs.getString ("abstract"));
////						temp.setLanguage(rs.getString ("lang"));
////						temp.setNumber (rs.getInt ("number"));
////						imf.addDescription (temp);
////					}
////				} catch (SQLException ex) {
////					ex.printStackTrace ( );
////				}
////			}
//
//			
//			// Auswertung der DateValue-Werte
//			stmtconn.loadStatement (SelectFromDB.DateValues (stmtconn.connection, oid));
//			this.result = stmtconn.execute ( );
//			
//			while (this.result.getResultSet ( ).next ( )) {
//				
//				DateValue temp = new DateValue ( );
//				temp.setDateValue(this.result.getResultSet ( ).getString ("value"));
//				temp.setNumber (this.result.getResultSet ( ).getInt ("number"));
//				imf.addDateValue (temp);
//			}
//			
////			rs = db.selectDateValues(oid);
////			if (rs == null) {
////				System.out.println("keine Date-Value-Werte vorhanden");
////			} else {
////				System.out.println("DateValue-Wert vorhanden");
////				try {
////					while (rs.next ( )) {
////						DateValue temp = new DateValue ( );
////						temp.setDateValue(rs.getString ("value"));
////						temp.setNumber (rs.getInt ("number"));
////						imf.addDateValue (temp);
////					}
////				} catch (SQLException ex) {
////					ex.printStackTrace ( );
////				}
////			}
//
//			
//			// Auswertung der TypeValue-Werte
//			stmtconn.loadStatement (SelectFromDB.TypeValues (stmtconn.connection, oid));
//			this.result = stmtconn.execute ( );
//			
//			while (this.result.getResultSet ( ).next ( )) {
//				
//				TypeValue temp = new TypeValue ( );
//				temp.setTypeValue(this.result.getResultSet ( ).getString ("value"));
////				temp.setNumber (this.result.getResultSet ( ).getInt ("number"));
//				imf.addTypeValue (temp);
//			}
////			rs = db.selectTypeValue(oid);
////			if (rs == null) {
////				System.out.println("keine TypeValue-Werte vorhanden");
////			} else {
////				System.out.println("TypeValue-Wert vorhanden");
////				try {
////					while (rs.next ( )) {
////						TypeValue temp = new TypeValue ( );
////						temp.setTypeValue(rs.getString ("value"));
//////						temp.setNumber (rs.getInt ("number"));
////						imf.addTypeValue (temp);
////					}
////				} catch (SQLException ex) {
////					ex.printStackTrace ( );
////				}
////			}
//
//			
//			// Auswertung der Publisher-Werte
//			stmtconn.loadStatement (SelectFromDB.Publisher (stmtconn.connection, oid));
//			this.result = stmtconn.execute ( );
//			
//			while (this.result.getResultSet ( ).next ( )) {
//				
//				Publisher temp = new Publisher ( );
//				temp.setName(this.result.getResultSet ( ).getString ("name"));
//				temp.setNumber (this.result.getResultSet ( ).getInt ("number"));
//				imf.addPublisher (temp);
//			}
//			
////			rs = db.selectPublisher(oid);
////			if (rs == null) {
////				System.out.println("keine Publisher-Werte vorhanden");
////			} else {
////				System.out.println("Publisher-Wert vorhanden");
////				try {
////					while (rs.next ( )) {
////						Publisher temp = new Publisher ( );
////						temp.setName(rs.getString ("name"));
////						temp.setNumber (rs.getInt ("number"));
////						imf.addPublisher (temp);
////					}
////				} catch (SQLException ex) {
////					ex.printStackTrace ( );
////				}
////			}			
//
//
//			// Auswertung der DCC-Classifications-Werte
//			stmtconn.loadStatement (SelectFromDB.DDCClassification (stmtconn.connection, oid));
//			this.result = stmtconn.execute ( );
//			
//			while (this.result.getResultSet ( ).next ( )) {
//				
//				Classification cl = new DDCClassification();
//				cl.setValue(this.result.getResultSet ( ).getString("D.DCC_Categorie"));
////				temp.setNumber (this.result.getResultSet ( ).getInt ("number"));
//				imf.addClassfication(cl);
//			}
////			rs = db.selectDDCClassification(oid);
////			if (rs == null) {
////				System.out.println("keine DDC-Werte vorhanden");
////			} else {
////				System.out.println("DDC-Wert vorhanden");
////				try {
////					while (rs.next ( )) {
////						Classification cl = new DDCClassification();
////						cl.setValue(rs.getString("D.DCC_Categorie"));
//////						temp.setNumber (rs.getInt ("number"));
////						imf.addClassfication(cl);
////					}
////				} catch (SQLException ex) {
////					ex.printStackTrace ( );
////				}
////			}	
//			
//			// Auswertung der DNB-Classifications-Werte
//			stmtconn.loadStatement (SelectFromDB.DNBClassification (stmtconn.connection, oid));
//			this.result = stmtconn.execute ( );
//			
//			while (this.result.getResultSet ( ).next ( )) {
//				
//				Classification cl = new DNBClassification();
//				cl.setValue(this.result.getResultSet ( ).getString("D.DNB_Categorie"));
////				temp.setNumber (this.result.getResultSet ( ).getInt ("number"));
//				imf.addClassfication(cl);
//			}
////			rs = db.selectDNBClassification(oid);
////			if (rs == null) {
////				System.out.println("keine DNB-Werte vorhanden");
////			} else {
////				System.out.println("DNB-Wert vorhanden");
////				try {
////					while (rs.next ( )) {
////						Classification cl = new DNBClassification();
////						cl.setValue(rs.getString("D.DNB_Categorie"));
//////						temp.setNumber (rs.getInt ("number"));
////						imf.addClassfication(cl);
////					}
////				} catch (SQLException ex) {
////					ex.printStackTrace ( );
////				}
////			}	
//
//			// Auswertung der DINI-Set-Classifications-Werte
//			stmtconn.loadStatement (SelectFromDB.DINISetClassification (stmtconn.connection, oid));
//			this.result = stmtconn.execute ( );
//			
//			while (this.result.getResultSet ( ).next ( )) {
//				
//				Classification cl = new DINISetClassification();
//				cl.setValue(this.result.getResultSet ( ).getString("name"));
////				temp.setNumber (this.result.getResultSet ( ).getInt ("number"));
//				imf.addClassfication(cl);
//			}
////			rs = db.selectDINISetClassification(oid);
////			if (rs == null) {
////				System.out.println("keine DINI-Set-Werte vorhanden");
////			} else {
////				System.out.println("DINI-Set-Wert vorhanden");
////				try {
////					while (rs.next ( )) {
////						Classification cl = new DINISetClassification();
////						cl.setValue(rs.getString("name"));
//////						temp.setNumber (rs.getInt ("number"));
////						imf.addClassfication(cl);
////					}
////				} catch (SQLException ex) {
////					ex.printStackTrace ( );
////				}
////			}
//
//			// Auswertung der Other-Classifications-Werte
//			stmtconn.loadStatement (SelectFromDB.OtherClassification (stmtconn.connection, oid));
//			this.result = stmtconn.execute ( );
//			
//			while (this.result.getResultSet ( ).next ( )) {
//				
//				Classification cl = new OtherClassification();
//				cl.setValue(this.result.getResultSet ( ).getString("name"));
////				temp.setNumber (this.result.getResultSet ( ).getInt ("number"));
//				imf.addClassfication(cl);
//			}
////			rs = db.selectOtherClassification(oid);
////			if (rs == null) {
////				System.out.println("keine Other-Classifiation-Werte vorhanden");
////			} else {
////				System.out.println("Other-Classification-Wert vorhanden");
////				try {
////					while (rs.next ( )) {
////						Classification cl = new OtherClassification();
////						cl.setValue(rs.getString("name"));
//////						temp.setNumber (rs.getInt ("number"));
////						imf.addClassfication(cl);
////					}
////				} catch (SQLException ex) {
////					ex.printStackTrace ( );
////				}
////			}
//
//			// Auswertung der Keywords-Werte
//			stmtconn.loadStatement (SelectFromDB.Keywords (stmtconn.connection, oid));
//			this.result = stmtconn.execute ( );
//			
//			while (this.result.getResultSet ( ).next ( )) {
//				
//				Keyword temp = new Keyword();
//				temp.setKeyword(this.result.getResultSet ( ).getString("keyword"));
//				temp.setLanguage(this.result.getResultSet ( ).getString("language"));
////				temp.setNumber (this.result.getResultSet ( ).getInt ("number"));
//				imf.addKeyword(temp);
//			}
////			rs = db.selectKeywords(oid);
////			if (rs == null) {
////				System.out.println("keine Keyword-Werte vorhanden");
////			} else {
////				System.out.println("Keyword vorhanden");
////				try {
////					while (rs.next ( )) {
////						Keyword temp = new Keyword();
////						temp.setKeyword(rs.getString("keyword"));
////						temp.setLanguage(rs.getString("language"));
//////						temp.setNumber (rs.getInt ("number"));
////						imf.addKeyword(temp);
////					}
////				} catch (SQLException ex) {
////					ex.printStackTrace ( );
////				}
////			}
//			
//			// Auswertung der Keywords-Werte
//			stmtconn.loadStatement (SelectFromDB.Languages (stmtconn.connection, oid));
//			this.result = stmtconn.execute ( );
//			
//			while (this.result.getResultSet ( ).next ( )) {
//				
//				Language temp = new Language();
//				temp.setLanguage(this.result.getResultSet ( ).getString("L.language"));
//				temp.setNumber (this.result.getResultSet ( ).getInt ("number"));
//				imf.addLanguage(temp);
//			}
////			rs = db.selectLanguages(oid);
////			if (rs == null) {
////				System.out.println("keine Language-Werte vorhanden");
////			} else {
////				System.out.println("Language vorhanden");
////				try {
////					while (rs.next ( )) {
////						Language temp = new Language();
////						temp.setLanguage(rs.getString("L.language"));
////						temp.setNumber (rs.getInt ("number"));
////						imf.addLanguage(temp);
////					}
////				} catch (SQLException ex) {
////					ex.printStackTrace ( );
////				}
////			}
//			
//			stmtconn.commit ( );
//			
//			//db.closeStatement ( );
//			
//			String xmlData;
//			xmlData = imMarsch.marshall (imf);
//			
////			RestEntrySet res = new RestEntrySet ( );
//			
//			res.addEntry ("internalmetadata", xmlData);
//			this.rms.setStatus (RestStatusEnum.OK);
//
//		} catch (SQLException ex) {
//			
//			logger.error (ex.getLocalizedMessage ( ));
//			ex.printStackTrace ( );
//			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
//			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
//			
//		}  catch (WrongStatementException ex) {
//			
//			logger.error ("An error occured while processing Get ObjectEntry: " + ex.getLocalizedMessage ( ));
//			ex.printStackTrace ( );
//			this.rms.setStatus (RestStatusEnum.WRONG_STATEMENT);
//			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
//			
//		} finally {
//			
//			if (stmtconn != null) {
//				
//				try {
//					
//					stmtconn.close ( );
//					stmtconn = null;
//					
//				} catch (SQLException ex) {
//					
//					ex.printStackTrace ( );
//					logger.error (ex.getLocalizedMessage ( ));
//				}
//			}
//			
//			this.rms.addEntrySet (res);
//			res = null;
//			this.result = null;
//			dbng = null;
//	}
//	/*
//	@Deprecated
//	public static void delete(BigDecimal object_id) {
//		
//		System.out.println("Beginn Delete");
//		
//		DBAccess db = DBAccess.createDBAccess ( );
//		db.createConnection ( );
//		db.setAutoCom(false);
//		
//		try {
//			db.deleteDescription(object_id);
//			db.deleteDateValues(object_id);
//			db.deleteFormats(object_id);
//			db.deleteIdentifiers(object_id);
//			db.deleteTypeValue(object_id);
//			db.deleteTitles(object_id);
//			db.deletePublishers(object_id);
//			
//			db.deleteObject2Author(object_id);
//			db.deleteObject2Editor(object_id);
//			db.deleteObject2Contributor(object_id);
//			db.deleteObject2Language(object_id);
//			db.deleteObject2Keywords(object_id);
//			db.deleteOther_Classification(object_id);
//			db.deleteDDC_Classification(object_id);
//			db.deleteDNB_Classification(object_id);
//			db.deleteDINI_Set_Classification(object_id);
//			
//			db.deletePersonWithoutReference();
//			db.deleteKeywordsWithoutReference();
//			db.deleteOther_Categories();
//		
//			db.commit();
//		} catch (SQLException sqlex) {
//			System.out.println("Exception geworfen");
//			logger.error (sqlex.getLocalizedMessage ( ));
//			sqlex.printStackTrace ( );
//			db.rollback();
//		}
//		
//		db.closeConnection();
//		
//		System.out.println("Ende Delete");
//	}
//	
//	
//	public static void put(InternalMetadata imf) {
//		
//		BigDecimal object_id = new BigDecimal ("638");
//		
//		DBAccessInterface db = DBAccess.createDBAccess ( );
//		db.createConnection ( );
//		ResultSet rs;
//		
////		String key = "";
//
//		
//		
//		
//		List <Title> titleList = imf.getTitles ( );
//		List <DateValue> dateValueList = imf.getDateValues ( );
//		List <Format> formatList = imf.getFormatList ( );
//		List <Identifier> identifierList = imf.getIdentifierList ( );
//		List <Description> descriptionList = imf.getDescriptions();
//		List <Publisher> publisherList = imf.getPublishers();
//		
//		List <Author> authorList = imf.getAuthors();
//		List <Editor> editorList = imf.getEditorList();
//		List <Contributor> contributorList = imf.getContributorList();
//		
//		List <Keyword> keywordList = imf.getKeywords ( );
//		List <TypeValue> typeValueList = imf.getTypeValueList ( );
//
//		List <Language> languageList = imf.getLanguageList();
//		List<Classification> classificationList = imf.getClassificationList();
//		
//		//ResultSet resultset;
//
//		// Autocommit ausschalten, nur eine vollständige Transaktion darf durchlaufen
//		db.setAutoCom (false);
//		
//		try {
//			// Titel speichern
//			if (titleList != null) {
//				for (Title title : titleList)
//					db.insertTitle(object_id, title.getQualifier(), title
//							.getTitle(), title.getLang());
//			}
//
//			// Datumswerte
//			if (dateValueList != null) {
//				for (DateValue dateValue : dateValueList) {
//					try {
//						db.insertDateValue(object_id, dateValue.getNumber(),
//								HelperMethods.extract_datestamp(dateValue
//										.getDateValue()));
//
//					} catch (ParseException ex) {
//						logger.error("Datestamp with datevalue incorrect");
//						ex.printStackTrace();
//					}
//				}
//			}
//
//			if (formatList != null) {
//				for (Format format : formatList)
//					db.insertFormat(object_id, format.getNumber(), format
//							.getSchema_f());
//			}
//
//			if (identifierList != null) {
//				for (Identifier identifier : identifierList)
//					db.insertIdentifier(object_id, identifier.getNumber(),
//							identifier.getIdentifier());
//			}
//
//			if (descriptionList != null) {
//				for (Description description : descriptionList)
//					db.insertDescription(object_id, description.getNumber(),
//							description.getDescription());
//			}
//
//			if (typeValueList != null) {
//				for (TypeValue typeValue : typeValueList)
//					db.insertTypeValue(object_id, typeValue.getTypeValue());
//			}
//
//			if (publisherList != null) {
//				for (Publisher publisher : publisherList)
//					db.insertPublisher(object_id, publisher.getNumber(),
//							publisher.getName());
//			}
//
//			if (authorList != null) {
//				for (Author author : authorList) {
//					BigDecimal person_id = null;
//					db.insertPerson(author.getFirstname(),
//							author.getLastname(), author.getTitle(), author
//									.getInstitution(), author.getEmail());
//					rs = db.selectLatestPerson(author.getFirstname(), author
//							.getLastname());
//					if (rs == null) {
//						System.out.println("Person nicht eingetragen");
//						logger.warn("resultset empty!");
//					} else {
//						while (rs.next()) {
//							person_id = rs.getBigDecimal(1);
//						}
//					}
//					db.insertObject2Author(object_id, person_id, author
//							.getNumber());
//				}
//			}
//			
//			if (editorList != null) {
//				for (Editor editor : editorList) {
//					BigDecimal person_id = null;
//					db.insertPerson(editor.getFirstname(),
//							editor.getLastname(), editor.getTitle(), editor
//									.getInstitution(), editor.getEmail());
//					rs = db.selectLatestPerson(editor.getFirstname(), editor
//							.getLastname());
//					if (rs == null) {
//						System.out.println("Person nicht eingetragen");
//						logger.warn("resultset empty!");
//					} else {
//						while (rs.next()) {
//							person_id = rs.getBigDecimal(1);
//						}
//					}
//					db.insertObject2Editor(object_id, person_id, editor
//							.getNumber());
//				}
//			}
//
//			
//			if (contributorList != null) {
//				for (Contributor contributor : contributorList) {
//					BigDecimal person_id = null;
//					db.insertPerson(contributor.getFirstname(), contributor
//							.getLastname(), contributor.getTitle(), contributor
//							.getInstitution(), contributor.getEmail());
//					rs = db.selectLatestPerson(contributor.getFirstname(),
//							contributor.getLastname());
//					if (rs == null) {
//						System.out.println("Person nicht eingetragen");
//						logger.warn("resultset empty!");
//					} else {
//						while (rs.next()) {
//							person_id = rs.getBigDecimal(1);
//						}
//					}
//					db.insertObject2Editor(object_id, person_id, contributor
//							.getNumber());
//				}
//			}
//
//			if (keywordList != null) {
//				for (Keyword keyword : keywordList) {
//					BigDecimal keyword_id = null;
//					db.insertKeyword(keyword.getKeyword(), keyword
//							.getLanguage());
//					rs = db.selectLatestKeyword(keyword.getKeyword(), keyword
//							.getLanguage());
//					if (rs == null) {
//						System.out.println("Keyword nicht eingetragen");
//						logger.warn("resultset empty!");
//					} else {
//						while (rs.next()) {
//							keyword_id = rs.getBigDecimal(1);
//						}
//					}
//					db.insertObject2Keyword(object_id, keyword_id);
//				}
//			}
//			
//			if (languageList != null) {
//				for (Language language : languageList) {
//					BigDecimal language_id = null;
//
//					rs = db.selectLanguageByName(language.getLanguage());
//					if (!rs.next()) {
//						// Sprache ist noch nicht vorhanden => einfügen und neue
//						// Sprach-ID auslesen
//						db.insertLanguage(language.getLanguage());
//					}
//					rs = db.selectLanguageByName(language.getLanguage());
//					if (rs == null) {
//						System.out.println("Sprache nicht eingetragen");
//					} else {
//						while (rs.next()) {
//							language_id = rs.getBigDecimal(1);
//						}
//					}
//					db.insertObject2Language(object_id, language_id, language
//							.getNumber());
//				}
//			}
//			
//			if (classificationList != null) {
//				for (Classification classification : classificationList) {
//					// fuer jeden Klassifikationstypen muessen unterschiedliche Aktionen erfolgen
//					if (classification instanceof DDCClassification) {
//						String ddcValue = null;
//						rs = db.selectDDCCategoriesByCategorie(classification.getValue());
//						while (rs.next()) {
//							ddcValue = rs.getString(1);
//						}
//						if (ddcValue == null) {
//							// Versuch, über den übergebenen Namen den richten DDC-Wert zu bestimmen
//							
//						}
//						// Daten zuordnen
//						db.insertDDCClassification(object_id, ddcValue);
//					}
//					if (classification instanceof DNBClassification) {
////						BigDecimal DNB_Categorie = null;
//						String DNB_Categorie = null;
//						rs = db.selectDNBCategoriesByCategorie(classification.getValue());
//						while (rs.next()) {
//							DNB_Categorie = rs.getString(1);
//						}
//						// Daten zuordnen
//						db.insertDNBClassification(object_id, DNB_Categorie);
//					}					
//					if (classification instanceof DINISetClassification) {
//						BigDecimal DINI_set_id = null;
//						rs = db.selectDINISetCategoriesByName(classification.getValue());
//						while (rs.next()) {
//							DINI_set_id = rs.getBigDecimal(1);
//						}
//						// Daten zuordnen
//						db.insertDINISetClassification(object_id, DINI_set_id);						
//					}
//					
//					if (classification instanceof OtherClassification) {
//						BigDecimal other_id = null;
//						// ID zum Klassifikationswort aus DB suchen 
//						rs = db.selectLatestOtherCategories(classification.getValue());
//						while (rs.next()) {
//							// Wort wohl vorhanden, sonst keine Rückgabe
//							other_id = rs.getBigDecimal(1);
//						} 
//						if (other_id == null) {
//							// Wort noch nicht vorhanden, neu eintragen
//							// Klassifikation eintragen
//							db.insertOtherCategories(classification.getValue());
//							rs = db.selectLatestOtherCategories(classification.getValue());
//							while (rs.next()) {
//								other_id = rs.getBigDecimal(1);
//							}
//						}
//						// ID dieser Klassifikation bestimmen und zuordnen
//						db.insertOtherClassification(object_id, other_id);
//					}
//				}
//			}
//			db.commit();
//			
//
//		} catch (SQLException sqlex) {
//			System.out.println("Exception geworfen");
//			logger.error (sqlex.getLocalizedMessage ( ));
//			sqlex.printStackTrace ( );
//			db.rollback();
//		}
//		
//		db.setAutoCom(true);
//		db.closeConnection ( );
//		
//
//
//	}
//	
//	
//	public static void main(String[] args) {
//	
//		InternalMetadata imf = InternalMetadata.createDummy();
//
//		System.out.println(imf);
//	
//		get();
////		delete(new BigDecimal("730"));			
////		put(imf);
//
//	}
//	
//	*/
//	/**
//	 * @param args
//	 */
///*	public static void get() {
//		// TODO Auto-generated method stub
//
//		
//		// erzeuge imf-Object, das Schrittweise mit Daten befüllt wird
//		InternalMetadata imf = new InternalMetadata();
//		
//		ResultSet rs;
//		DBAccessInterface db = DBAccess.createDBAccess ( );
//		db.createConnection ( );
//		db.setAutoCom(false);
//		
//		// hier die Test-ID
//		BigDecimal oid = new BigDecimal ("730");
//		
//		// Auswertung der Title-Informationen
//		
//
//		
//		try {
//			// Auswertung der Titel
//			rs = db.selectTitle (oid);
//			if (rs == null) {
//				System.out.println("kein Title vorhanden");
//				// logger.warn ("resultset empty!");
//			} else {
//				System.out.println("Title vorhanden");
//					while (rs.next ( )) {
//						Title temp = new Title ( );
//						temp.setTitle (rs.getString ("title"));
//						temp.setQualifier (rs.getString ("qualifier"));
//						temp.setLang (rs.getString ("lang"));
////						temp.setNumber (rs.getInt ("number"));
//						imf.addTitle (temp);
//				}
//			}
//
//
//			// Auswertung der Autoren
//			rs = db.selectAuthors(oid);
//			if (rs == null) {
//				System.out.println("kein Author vorhanden");
//			} else {
//				System.out.println("Author vorhanden");
//				try {
//					while (rs.next ( )) {
//						Author temp = new Author ( );
//						temp.setNumber(rs.getInt("number"));
//						temp.setFirstname(rs.getString("firstname"));
//						temp.setLastname(rs.getString("lastname"));
//						temp.setInstitution(rs.getString("institution"));
//						temp.setEmail(rs.getString("email"));
//						temp.setTitle(rs.getString("title"));
//						imf.addAuthor (temp);
//					}
//				} catch (SQLException ex) {
//					ex.printStackTrace ( );
//				}
//			}	
//
//
//			// Auswertung der Editoren
//			rs = db.selectEditors(oid);
//			if (rs == null) {
//				System.out.println("kein Editor vorhanden");
//			} else {
//				System.out.println("Editor vorhanden");
//				try {
//					while (rs.next ( )) {
//						Editor temp = new Editor ( );
//						temp.setNumber(rs.getInt("number"));
//						temp.setFirstname(rs.getString("firstname"));
//						temp.setLastname(rs.getString("lastname"));
//						temp.setInstitution(rs.getString("institution"));
//						temp.setEmail(rs.getString("email"));
//						temp.setTitle(rs.getString("title"));
//						imf.addEditor (temp);
//					}
//				} catch (SQLException ex) {
//					ex.printStackTrace ( );
//				}
//			}	
//			
//
//
//			// Auswertung der Bearbeiter
//			rs = db.selectContributors(oid);
//			if (rs == null) {
//				System.out.println("kein Contirbutor vorhanden");
//			} else {
//				System.out.println("Contributor vorhanden");
//				try {
//					while (rs.next ( )) {
//						Contributor temp = new Contributor ( );
//						temp.setNumber(rs.getInt("number"));
//						temp.setFirstname(rs.getString("firstname"));
//						temp.setLastname(rs.getString("lastname"));
//						temp.setInstitution(rs.getString("institution"));
//						temp.setEmail(rs.getString("email"));
//						temp.setTitle(rs.getString("title"));
//						imf.addContributor (temp);
//					}
//				} catch (SQLException ex) {
//					ex.printStackTrace ( );
//				}
//			}			
//			
//			
//			
//			// Auswertung des Formats
//			rs = db.selectFormat(oid);
//			if (rs == null) {
//				System.out.println("kein Format");
//			} else {
//				System.out.println("Format vorhanden");
//				try {
//					while (rs.next ( )) {
//						Format temp = new Format ( );
//						temp.setSchema_f(rs.getString ("schema_f"));
//						temp.setNumber (rs.getInt ("number"));
//						imf.addFormat (temp);
//					}
//				} catch (SQLException ex) {
//					ex.printStackTrace ( );
//				}
//			}
//			
//			
//			// Auswertung des Identifiers
//			rs = db.selectIdentifier(oid);
//			if (rs == null) {
//				System.out.println("kein Identifier");
//			} else {
//				System.out.println("Identifier vorhanden");
//				try {
//					while (rs.next ( )) {
//						Identifier temp = new Identifier ( );
//						temp.setIdentifier(rs.getString ("identifier"));
////						temp.setLanguage(rs.getString ("lang"));
//						temp.setNumber (rs.getInt ("number"));
//						imf.addIdentifier (temp);
//					}
//				} catch (SQLException ex) {
//					ex.printStackTrace ( );
//				}
//			}
//
//			
//			// Auswertung der Description
//			rs = db.selectDescription(oid);
//			if (rs == null) {
//				System.out.println("keine Description");
//			} else {
//				System.out.println("Description vorhanden");
//				try {
//					while (rs.next ( )) {
//						Description temp = new Description ( );
//						temp.setDescription(rs.getString ("abstract"));
//						temp.setLanguage(rs.getString ("lang"));
//						temp.setNumber (rs.getInt ("number"));
//						imf.addDescription (temp);
//					}
//				} catch (SQLException ex) {
//					ex.printStackTrace ( );
//				}
//			}
//
//			
//			// Auswertung der DateValue-Werte
//			rs = db.selectDateValues(oid);
//			if (rs == null) {
//				System.out.println("keine Date-Value-Werte vorhanden");
//			} else {
//				System.out.println("DateValue-Wert vorhanden");
//				try {
//					while (rs.next ( )) {
//						DateValue temp = new DateValue ( );
//						temp.setDateValue(rs.getString ("value"));
//						temp.setNumber (rs.getInt ("number"));
//						imf.addDateValue (temp);
//					}
//				} catch (SQLException ex) {
//					ex.printStackTrace ( );
//				}
//			}
//
//			
//			// Auswertung der TypeValue-Werte
//			rs = db.selectTypeValue(oid);
//			if (rs == null) {
//				System.out.println("keine TypeValue-Werte vorhanden");
//			} else {
//				System.out.println("TypeValue-Wert vorhanden");
//				try {
//					while (rs.next ( )) {
//						TypeValue temp = new TypeValue ( );
//						temp.setTypeValue(rs.getString ("value"));
////						temp.setNumber (rs.getInt ("number"));
//						imf.addTypeValue (temp);
//					}
//				} catch (SQLException ex) {
//					ex.printStackTrace ( );
//				}
//			}
//
//			
//			// Auswertung der Publisher-Werte
//			rs = db.selectPublisher(oid);
//			if (rs == null) {
//				System.out.println("keine Publisher-Werte vorhanden");
//			} else {
//				System.out.println("Publisher-Wert vorhanden");
//				try {
//					while (rs.next ( )) {
//						Publisher temp = new Publisher ( );
//						temp.setName(rs.getString ("name"));
//						temp.setNumber (rs.getInt ("number"));
//						imf.addPublisher (temp);
//					}
//				} catch (SQLException ex) {
//					ex.printStackTrace ( );
//				}
//			}			
//
//
//			// Auswertung der DCC-Classifications-Werte
//			rs = db.selectDDCClassification(oid);
//			if (rs == null) {
//				System.out.println("keine DDC-Werte vorhanden");
//			} else {
//				System.out.println("DDC-Wert vorhanden");
//				try {
//					while (rs.next ( )) {
//						Classification cl = new DDCClassification();
//						cl.setValue(rs.getString("D.DCC_Categorie"));
////						temp.setNumber (rs.getInt ("number"));
//						imf.addClassfication(cl);
//					}
//				} catch (SQLException ex) {
//					ex.printStackTrace ( );
//				}
//			}	
//			
//			// Auswertung der DNB-Classifications-Werte
//			rs = db.selectDNBClassification(oid);
//			if (rs == null) {
//				System.out.println("keine DNB-Werte vorhanden");
//			} else {
//				System.out.println("DNB-Wert vorhanden");
//				try {
//					while (rs.next ( )) {
//						Classification cl = new DNBClassification();
//						cl.setValue(rs.getString("D.DNB_Categorie"));
////						temp.setNumber (rs.getInt ("number"));
//						imf.addClassfication(cl);
//					}
//				} catch (SQLException ex) {
//					ex.printStackTrace ( );
//				}
//			}	
//
//			// Auswertung der DINI-Set-Classifications-Werte
//			rs = db.selectDINISetClassification(oid);
//			if (rs == null) {
//				System.out.println("keine DINI-Set-Werte vorhanden");
//			} else {
//				System.out.println("DINI-Set-Wert vorhanden");
//				try {
//					while (rs.next ( )) {
//						Classification cl = new DINISetClassification();
//						cl.setValue(rs.getString("name"));
////						temp.setNumber (rs.getInt ("number"));
//						imf.addClassfication(cl);
//					}
//				} catch (SQLException ex) {
//					ex.printStackTrace ( );
//				}
//			}
//
//			// Auswertung der Other-Classifications-Werte
//			rs = db.selectOtherClassification(oid);
//			if (rs == null) {
//				System.out.println("keine Other-Classifiation-Werte vorhanden");
//			} else {
//				System.out.println("Other-Classification-Wert vorhanden");
//				try {
//					while (rs.next ( )) {
//						Classification cl = new OtherClassification();
//						cl.setValue(rs.getString("name"));
////						temp.setNumber (rs.getInt ("number"));
//						imf.addClassfication(cl);
//					}
//				} catch (SQLException ex) {
//					ex.printStackTrace ( );
//				}
//			}
//
//			// Auswertung der Keywords-Werte
//			rs = db.selectKeywords(oid);
//			if (rs == null) {
//				System.out.println("keine Keyword-Werte vorhanden");
//			} else {
//				System.out.println("Keyword vorhanden");
//				try {
//					while (rs.next ( )) {
//						Keyword temp = new Keyword();
//						temp.setKeyword(rs.getString("keyword"));
//						temp.setLanguage(rs.getString("language"));
////						temp.setNumber (rs.getInt ("number"));
//						imf.addKeyword(temp);
//					}
//				} catch (SQLException ex) {
//					ex.printStackTrace ( );
//				}
//			}
//			
//			// Auswertung der Keywords-Werte
//			rs = db.selectLanguages(oid);
//			if (rs == null) {
//				System.out.println("keine Language-Werte vorhanden");
//			} else {
//				System.out.println("Language vorhanden");
//				try {
//					while (rs.next ( )) {
//						Language temp = new Language();
//						temp.setLanguage(rs.getString("L.language"));
//						temp.setNumber (rs.getInt ("number"));
//						imf.addLanguage(temp);
//					}
//				} catch (SQLException ex) {
//					ex.printStackTrace ( );
//				}
//			}
//			
//			
//
//		} catch (SQLException ex) {
//			ex.printStackTrace();
//		}  catch (RuntimeException ex) {
//			ex.printStackTrace ( );
//		}
//		
//		
//
//
//
//
//
//
//		db.closeConnection();
//		
//		
//
//		System.out.println(imf);
//		
////		String xmlData;
//// xmlData = imMarsch.marshall (imf);
//		
//// List <HashMap <String, String>> listentries = new ArrayList <HashMap <String,
//// String>> ( );
//// HashMap <String, String> mapEntry = new HashMap <String ,String> ( );
////		
//// // mapEntry.put ("internalmetadata", xmlData);
//// listentries.add (mapEntry);
//// String requestxml = RestXmlCodec.encodeEntrySetResponseBody (listentries,
//// "InternalMetadataEntry");
//		
//// return requestxml;
//
//		
//		
//		
//		
//		
//		
//	}*/
//}
