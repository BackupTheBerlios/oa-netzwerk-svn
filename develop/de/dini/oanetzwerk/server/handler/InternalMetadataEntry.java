/**
 * 
 */

package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;
import java.sql.ResultSet;
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

public class InternalMetadataEntry extends AbstractKeyWordHandler implements
		KeyWord2DatabaseInterface {
	
	static Logger logger = Logger.getLogger (InternalMetadataEntry.class);
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
			throw new NotEnoughParametersException ("This method needs at least 2 parameters: the keyword and the internal object ID");
		
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
		
		DBAccessNG dbng = new DBAccessNG ( );
		MultipleStatementConnection stmtconn = null;
		RestEntrySet res = new RestEntrySet ( );
		
		try {
			
			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection ( );
			
			stmtconn.loadStatement (DeleteFromDB.Description (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
				
			}
			
			stmtconn.loadStatement (DeleteFromDB.DateValues (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
				
			}
			
			stmtconn.loadStatement (DeleteFromDB.Formats (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
				
			}

			stmtconn.loadStatement (DeleteFromDB.Identifiers (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
				
			}
			
			stmtconn.loadStatement (DeleteFromDB.TypeValue (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
				
			}
			
			stmtconn.loadStatement (DeleteFromDB.Titles (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
				
			}
			
			stmtconn.loadStatement (DeleteFromDB.Publishers (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
				
			}
			
			stmtconn.loadStatement (DeleteFromDB.Object2Author (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
				
			}
			
			stmtconn.loadStatement (DeleteFromDB.Object2Editor (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
				
			}
			
			stmtconn.loadStatement (DeleteFromDB.Object2Contributor (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
				
			}
			
			stmtconn.loadStatement (DeleteFromDB.Object2Language (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
				
			}
			
			stmtconn.loadStatement (DeleteFromDB.Object2Keywords (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
			}
			
			stmtconn.loadStatement (DeleteFromDB.Other_Classification (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
			}
			
			stmtconn.loadStatement (DeleteFromDB.DDC_Classification (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
			}
			
			stmtconn.loadStatement (DeleteFromDB.DNB_Classification (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
			}
			
			stmtconn.loadStatement (DeleteFromDB.DINI_Set_Classification (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
			}
			
			stmtconn.loadStatement (DeleteFromDB.PersonWithoutReference (stmtconn.connection));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
			}
			
			stmtconn.loadStatement (DeleteFromDB.KeywordsWithoutReference (stmtconn.connection));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
			}
			
			stmtconn.loadStatement (DeleteFromDB.Other_Categories (stmtconn.connection));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
			}
			
			stmtconn.commit ( );
			
			res.addEntry("oid", object_id.toPlainString());
			this.rms.setStatus(RestStatusEnum.OK);
			this.rms.addEntrySet(res);
			
		} catch (SQLException ex) {
			
			try {
				
				stmtconn.rollback ( );
				
			} catch (SQLException ex1) {
				
				
				ex1.printStackTrace ( );
			}
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} catch (WrongStatementException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			this.rms.setStatus (RestStatusEnum.WRONG_STATEMENT);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} finally {
			
			if (stmtconn != null) {
				
				try {
					
					stmtconn.close ( );
					stmtconn = null;
					
				} catch (SQLException ex) {
					
					ex.printStackTrace ( );
					logger.error (ex.getLocalizedMessage ( ));
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
			throw new NotEnoughParametersException ("This method needs at least 2 parameters: the keyword and the internal object ID");

		// erzeuge imf-Object, das Schrittweise mit Daten befüllt wird
		InternalMetadata imf = new InternalMetadata ( );
		
		BigDecimal oid;
		
		try {
			
			oid = new BigDecimal (path [0]);
			
		} catch (NumberFormatException ex) {
			
			logger.error (path [0] + " is NOT a number!");
			
			this.rms = new RestMessage (RestKeyword.InternalMetadataEntry);
			this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription (path [0] + " is NOT a number!");
			
			return RestXmlCodec.encodeRestMessage (this.rms);
		}
		
		DBAccessNG dbng = new DBAccessNG ( );
		MultipleStatementConnection stmtconn = null;
		RestEntrySet res = new RestEntrySet ( );
		
		try {
			
			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection ( );
			
			stmtconn.loadStatement (SelectFromDB.Title (stmtconn.connection, oid));
			this.result = stmtconn.execute ( );
			
			// Auswertung der Titel
			while (this.result.getResultSet ( ).next ( )) {
				
				Title temp = new Title ( );
				temp.setTitle (this.result.getResultSet ( ).getString ("title"));
				temp.setQualifier (this.result.getResultSet ( ).getString ("qualifier"));
				temp.setLang (this.result.getResultSet ( ).getString ("lang"));
//				temp.setNumber (this.result.getResultSet ( ).getInt ("number"));
				imf.addTitle (temp);
			}
			
			// Auswertung der Titel
			//rs = db.selectTitle (oid);
/*			if (rs == null) {
				System.out.println("kein Title vorhanden");
				// logger.warn ("resultset empty!");
			} else {
				System.out.println("Title vorhanden");
					while (rs.next ( )) {
						Title temp = new Title ( );
						temp.setTitle (rs.getString ("title"));
						temp.setQualifier (rs.getString ("qualifier"));
						temp.setLang (rs.getString ("lang"));
//						temp.setNumber (rs.getInt ("number"));
						imf.addTitle (temp);
				}
			}*/

			// Auswertung der Autoren
			stmtconn.loadStatement (SelectFromDB.Authors (stmtconn.connection, oid));
			this.result = stmtconn.execute ( );
			
			while (this.result.getResultSet ( ).next ( )) {
				
				Author temp = new Author ( );
				temp.setNumber(this.result.getResultSet ( ).getInt("number"));
				temp.setFirstname(this.result.getResultSet ( ).getString("firstname"));
				temp.setLastname(this.result.getResultSet ( ).getString("lastname"));
				temp.setInstitution(this.result.getResultSet ( ).getString("institution"));
				temp.setEmail(this.result.getResultSet ( ).getString("email"));
				temp.setTitle(this.result.getResultSet ( ).getString("title"));
				imf.addAuthor (temp);
			}
			
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


			// Auswertung der Editoren
			stmtconn.loadStatement (SelectFromDB.Editors (stmtconn.connection, oid));
			this.result = stmtconn.execute ( );
			
			while (this.result.getResultSet ( ).next ( )) {
				
				Editor temp = new Editor ( );
				temp.setNumber(this.result.getResultSet ( ).getInt("number"));
				temp.setFirstname(this.result.getResultSet ( ).getString("firstname"));
				temp.setLastname(this.result.getResultSet ( ).getString("lastname"));
				temp.setInstitution(this.result.getResultSet ( ).getString("institution"));
				temp.setEmail(this.result.getResultSet ( ).getString("email"));
				temp.setTitle(this.result.getResultSet ( ).getString("title"));
				imf.addEditor (temp);
			}
//			
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
			
			// Auswertung der Bearbeiter
			stmtconn.loadStatement (SelectFromDB.Contributors (stmtconn.connection, oid));
			this.result = stmtconn.execute ( );
			
			while (this.result.getResultSet ( ).next ( )) {
				
				Contributor temp = new Contributor ( );
				temp.setNumber(this.result.getResultSet ( ).getInt("number"));
				temp.setFirstname(this.result.getResultSet ( ).getString("firstname"));
				temp.setLastname(this.result.getResultSet ( ).getString("lastname"));
				temp.setInstitution(this.result.getResultSet ( ).getString("institution"));
				temp.setEmail(this.result.getResultSet ( ).getString("email"));
				temp.setTitle(this.result.getResultSet ( ).getString("title"));
				imf.addContributor (temp);
			}

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
			
			// Auswertung des Formats
			stmtconn.loadStatement (SelectFromDB.Format (stmtconn.connection, oid));
			this.result = stmtconn.execute ( );
			
			while (this.result.getResultSet ( ).next ( )) {
				
				Format temp = new Format ( );
				temp.setSchema_f(this.result.getResultSet ( ).getString ("schema_f"));
				temp.setNumber (this.result.getResultSet ( ).getInt ("number"));
				imf.addFormat (temp);
			}

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
			
			// Auswertung des Identifiers
			stmtconn.loadStatement (SelectFromDB.Identifier (stmtconn.connection, oid));
			this.result = stmtconn.execute ( );
			
			while (this.result.getResultSet ( ).next ( )) {
				
				Identifier temp = new Identifier ( );
				temp.setIdentifier(this.result.getResultSet ( ).getString ("identifier"));
//				temp.setLanguage(this.result.getResultSet ( ).getString ("lang"));
				temp.setNumber (this.result.getResultSet ( ).getInt ("number"));
				imf.addIdentifier (temp);
			}
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
			
			// Auswertung der Description
			stmtconn.loadStatement (SelectFromDB.Description (stmtconn.connection, oid));
			this.result = stmtconn.execute ( );
			
			while (this.result.getResultSet ( ).next ( )) {
				
				Description temp = new Description ( );
				temp.setDescription(this.result.getResultSet ( ).getString ("abstract"));
				temp.setLanguage(this.result.getResultSet ( ).getString ("lang"));
				temp.setNumber (this.result.getResultSet ( ).getInt ("number"));
				imf.addDescription (temp);
			}
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

			// Auswertung der DateValue-Werte
			stmtconn.loadStatement (SelectFromDB.DateValues (stmtconn.connection, oid));
			this.result = stmtconn.execute ( );
			
			while (this.result.getResultSet ( ).next ( )) {
				
				DateValue temp = new DateValue ( );
				temp.setDateValue(this.result.getResultSet ( ).getString ("value"));
				temp.setNumber (this.result.getResultSet ( ).getInt ("number"));
				imf.addDateValue (temp);
			}
			
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
			
			// Auswertung der TypeValue-Werte
			stmtconn.loadStatement (SelectFromDB.TypeValues (stmtconn.connection, oid));
			this.result = stmtconn.execute ( );
			
			while (this.result.getResultSet ( ).next ( )) {
				
				TypeValue temp = new TypeValue ( );
				temp.setTypeValue(this.result.getResultSet ( ).getString ("value"));
//				temp.setNumber (this.result.getResultSet ( ).getInt ("number"));
				imf.addTypeValue (temp);
			}
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

			// Auswertung der Publisher-Werte
			stmtconn.loadStatement (SelectFromDB.Publisher (stmtconn.connection, oid));
			this.result = stmtconn.execute ( );
			
			while (this.result.getResultSet ( ).next ( )) {
				
				Publisher temp = new Publisher ( );
				temp.setName(this.result.getResultSet ( ).getString ("name"));
				temp.setNumber (this.result.getResultSet ( ).getInt ("number"));
				imf.addPublisher (temp);
			}
			
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

			// Auswertung der DCC-Classifications-Werte
			stmtconn.loadStatement (SelectFromDB.DDCClassification (stmtconn.connection, oid));
			this.result = stmtconn.execute ( );
			
			while (this.result.getResultSet ( ).next ( )) {
				
				Classification cl = new DDCClassification();
				cl.setValue(this.result.getResultSet ( ).getString("D.DCC_Categorie"));
//				temp.setNumber (this.result.getResultSet ( ).getInt ("number"));
				imf.addClassfication(cl);
			}
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
			
			// Auswertung der DNB-Classifications-Werte
			stmtconn.loadStatement (SelectFromDB.DNBClassification (stmtconn.connection, oid));
			this.result = stmtconn.execute ( );
			
			while (this.result.getResultSet ( ).next ( )) {
				
				Classification cl = new DNBClassification();
				cl.setValue(this.result.getResultSet ( ).getString("D.DNB_Categorie"));
//				temp.setNumber (this.result.getResultSet ( ).getInt ("number"));
				imf.addClassfication(cl);
			}
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

			// Auswertung der DINI-Set-Classifications-Werte
			stmtconn.loadStatement (SelectFromDB.DINISetClassification (stmtconn.connection, oid));
			this.result = stmtconn.execute ( );
			
			while (this.result.getResultSet ( ).next ( )) {
				
				Classification cl = new DINISetClassification();
				cl.setValue(this.result.getResultSet ( ).getString("name"));
//				temp.setNumber (this.result.getResultSet ( ).getInt ("number"));
				imf.addClassfication(cl);
			}
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

			// Auswertung der Other-Classifications-Werte
			stmtconn.loadStatement (SelectFromDB.OtherClassification (stmtconn.connection, oid));
			this.result = stmtconn.execute ( );
			
			while (this.result.getResultSet ( ).next ( )) {
				
				Classification cl = new OtherClassification();
				cl.setValue(this.result.getResultSet ( ).getString("name"));
//				temp.setNumber (this.result.getResultSet ( ).getInt ("number"));
				imf.addClassfication(cl);
			}
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

			// Auswertung der Keywords-Werte
			stmtconn.loadStatement (SelectFromDB.Keywords (stmtconn.connection, oid));
			this.result = stmtconn.execute ( );
			
			while (this.result.getResultSet ( ).next ( )) {
				
				Keyword temp = new Keyword();
				temp.setKeyword(this.result.getResultSet ( ).getString("keyword"));
				temp.setLanguage(this.result.getResultSet ( ).getString("language"));
//				temp.setNumber (this.result.getResultSet ( ).getInt ("number"));
				imf.addKeyword(temp);
			}
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
			
			// Auswertung der Keywords-Werte
			stmtconn.loadStatement (SelectFromDB.Languages (stmtconn.connection, oid));
			this.result = stmtconn.execute ( );
			
			while (this.result.getResultSet ( ).next ( )) {
				
				Language temp = new Language();
				temp.setLanguage(this.result.getResultSet ( ).getString("L.language"));
				temp.setNumber (this.result.getResultSet ( ).getInt ("number"));
				imf.addLanguage(temp);
			}
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
			
			stmtconn.commit ( );
			
			//db.closeStatement ( );
			
			String xmlData;
			xmlData = imMarsch.marshall (imf);
			
//			RestEntrySet res = new RestEntrySet ( );
			
			res.addEntry ("internalmetadata", xmlData);
			this.rms.setStatus (RestStatusEnum.OK);

		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		}  catch (WrongStatementException ex) {
			
			logger.error ("An error occured while processing Get ObjectEntry: " + ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			this.rms.setStatus (RestStatusEnum.WRONG_STATEMENT);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} finally {
			
			if (stmtconn != null) {
				
				try {
					
					stmtconn.close ( );
					stmtconn = null;
					
				} catch (SQLException ex) {
					
					ex.printStackTrace ( );
					logger.error (ex.getLocalizedMessage ( ));
				}
			}
			
			this.rms.addEntrySet (res);
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
	
	@SuppressWarnings("unused")
	@Override
	protected String putKeyWord (String [ ] path, String data) throws NotEnoughParametersException {
		
		if (path.length < 1)
			throw new NotEnoughParametersException ("This method needs at least 2 parameters: the keyword and the internal object ID");
		
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
		
		DBAccessNG dbng = new DBAccessNG ( );		
		MultipleStatementConnection stmtconn = null;
		
		this.rms = RestXmlCodec.decodeRestMessage (data);
		RestEntrySet res = this.rms.getListEntrySets ( ).get (0);

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
			
			this.rms = new RestMessage (RestKeyword.InternalMetadataEntry);
			this.rms.setStatus (RestStatusEnum.REST_XML_DECODING_ERROR);
			this.rms.setStatusDescription("unable to unmarshall xml " + strXML + " :" + ex);
			
			return RestXmlCodec.encodeRestMessage (this.rms);			
		}
	
		// PREPARE SQL STATEMENTS
		
		List <Title> titleList = imf.getTitles ( );
		List <DateValue> dateValueList = imf.getDateValues ( );
		List <Format> formatList = imf.getFormatList ( );
		List <Identifier> identifierList = imf.getIdentifierList ( );
		List <Description> descriptionList = imf.getDescriptions();
		List <Publisher> publisherList = imf.getPublishers();
		
		List <Author> authorList = imf.getAuthors();
		List <Editor> editorList = imf.getEditorList();
		List <Contributor> contributorList = imf.getContributorList();
		
		List <Keyword> keywordList = imf.getKeywords ( );
		List <TypeValue> typeValueList = imf.getTypeValueList ( );

		List <Language> languageList = imf.getLanguageList();
		List<Classification> classificationList = imf.getClassificationList();
		
		ResultSet rs;

		try {
			
			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection ( );
			
			// Titel speichern
			if (titleList != null) {
				
				for (Title title : titleList) {
					
					//db.insertTitle(object_id, title.getQualifier(), title
					//		.getTitle(), title.getLang());
					
					stmtconn.loadStatement (InsertIntoDB.Title (stmtconn.connection, object_id, title.getQualifier(), title.getTitle(), title.getLang()));
					this.result = stmtconn.execute ( );
					
					if (this.result.getUpdateCount ( ) < 1) {
						
						//warn, error, rollback, nothing????
					}
				}
			}

			// Datumswerte
			if (dateValueList != null) {
				for (DateValue dateValue : dateValueList) {
					try {
						
//						db.insertDateValue(object_id, dateValue.getNumber(),
//								HelperMethods.extract_datestamp(dateValue
//										.getDateValue()));
						stmtconn.loadStatement (InsertIntoDB.DateValue (stmtconn.connection, object_id, dateValue.getNumber(), HelperMethods.extract_datestamp(dateValue.getDateValue())));
						this.result = stmtconn.execute ( );
						
						if (this.result.getUpdateCount ( ) < 1) {
							
							//warn, error, rollback, nothing????
						}

					} catch (ParseException ex) {
						logger.error("Datestamp with datevalue incorrect");
						ex.printStackTrace();
					}
				}
			}

			if (formatList != null) {
				for (Format format : formatList) {
					
					stmtconn.loadStatement (InsertIntoDB.Format (stmtconn.connection, object_id, format.getNumber(), format.getSchema_f()));
					this.result = stmtconn.execute ( );
					
					if (this.result.getUpdateCount ( ) < 1) {
						
						//warn, error, rollback, nothing????
					}

				}
//					db.insertFormat(object_id, format.getNumber(), format
//							.getSchema_f());
			}

			if (identifierList != null) {
				for (Identifier identifier : identifierList) {
					
					stmtconn.loadStatement (InsertIntoDB.Identifier (stmtconn.connection, object_id, identifier.getNumber(), identifier.getIdentifier()));
					this.result = stmtconn.execute ( );
					
					if (this.result.getUpdateCount ( ) < 1) {
						
						//warn, error, rollback, nothing????
					}
				}
//					db.insertIdentifier(object_id, identifier.getNumber(),
//							identifier.getIdentifier());
			}

			if (descriptionList != null) {
				for (Description description : descriptionList) {
					
					stmtconn.loadStatement (InsertIntoDB.Description (stmtconn.connection, object_id, description.getNumber(), description.getDescription()));
					this.result = stmtconn.execute ( );
					
					if (this.result.getUpdateCount ( ) < 1) {
						
						//warn, error, rollback, nothing????
					}
	
				}
//					db.insertDescription(object_id, description.getNumber(),
//							description.getDescription());
			}

			if (typeValueList != null) {
				for (TypeValue typeValue : typeValueList) {
					
					stmtconn.loadStatement (InsertIntoDB.TypeValue (stmtconn.connection, object_id, typeValue.getTypeValue()));
					this.result = stmtconn.execute ( );
					
					if (this.result.getUpdateCount ( ) < 1) {
						
						//warn, error, rollback, nothing????
					}
				}
					
//					db.insertTypeValue(object_id, typeValue.getTypeValue());
			}

			if (publisherList != null) {
				for (Publisher publisher : publisherList) {
					
					stmtconn.loadStatement (InsertIntoDB.Publisher (stmtconn.connection, object_id, publisher.getNumber(), publisher.getName()));
					this.result = stmtconn.execute ( );
					
					if (this.result.getUpdateCount ( ) < 1) {
						
						//warn, error, rollback, nothing????
					}
				}
//					db.insertPublisher(object_id, publisher.getNumber(),
//							publisher.getName());
			}

			if (authorList != null) {
				for (Author author : authorList) {
					
					BigDecimal person_id = null;
					
					stmtconn.loadStatement (InsertIntoDB.Person (stmtconn.connection, author.getFirstname(),
							author.getLastname(), author.getTitle(), author
							.getInstitution(), author.getEmail()));
					this.result = stmtconn.execute ( );
					
					if (this.result.getUpdateCount ( ) < 1) {
						
						//warn, error, rollback, nothing????
					}
//					db.insertPerson(author.getFirstname(),
//							author.getLastname(), author.getTitle(), author
//									.getInstitution(), author.getEmail());
					stmtconn.loadStatement (SelectFromDB.LatestPerson (stmtconn.connection, author.getFirstname(), author.getLastname()));
					this.result = stmtconn.execute ( );
					
					while (this.result.getResultSet ( ).next ( )) {
						
						person_id = this.result.getResultSet ( ).getBigDecimal(1);
					}
					
//					rs = db.selectLatestPerson(author.getFirstname(), author
//							.getLastname());
//					if (rs == null) {
//						System.out.println("Person nicht eingetragen");
//						logger.warn("resultset empty!");
//					} else {
//						while (rs.next()) {
//							person_id = rs.getBigDecimal(1);
//							
//							//TODO: personid wird immer wieder überschrieben!!! while macht eigentlich keinen sinn!?
//						}
//					}
					
					stmtconn.loadStatement (InsertIntoDB.Object2Author (stmtconn.connection, object_id, person_id, author.getNumber()));
					this.result = stmtconn.execute ( );
					
					if (this.result.getUpdateCount ( ) < 1) {
						
						//warn, error, rollback, nothing????
					}
//					db.insertObject2Author(object_id, person_id, author
//							.getNumber());
				}
			}
			
			if (editorList != null) {
				for (Editor editor : editorList) {
					
					BigDecimal person_id = null;
					
					stmtconn.loadStatement (InsertIntoDB.Person (stmtconn.connection, editor.getFirstname(),
							editor.getLastname(), editor.getTitle(), editor
							.getInstitution(), editor.getEmail()));
					this.result = stmtconn.execute ( );
					
					if (this.result.getUpdateCount ( ) < 1) {
						
						//warn, error, rollback, nothing????
					}
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
//					
					stmtconn.loadStatement (SelectFromDB.LatestPerson (stmtconn.connection, editor.getFirstname(), editor.getLastname()));
					this.result = stmtconn.execute ( );
					
					while (this.result.getResultSet ( ).next ( )) {
						
						person_id = this.result.getResultSet ( ).getBigDecimal(1);
					}
					
					stmtconn.loadStatement (InsertIntoDB.Object2Editor (stmtconn.connection, object_id, person_id, editor.getNumber()));
					this.result = stmtconn.execute ( );
					
					if (this.result.getUpdateCount ( ) < 1) {
						
						//warn, error, rollback, nothing????
					}
//					db.insertObject2Editor(object_id, person_id, editor
//							.getNumber());
				}
			}

			
			if (contributorList != null) {
				for (Contributor contributor : contributorList) {
					
					BigDecimal person_id = null;
					
					stmtconn.loadStatement (InsertIntoDB.Person (stmtconn.connection, contributor.getFirstname(), contributor
							.getLastname(), contributor.getTitle(), contributor
							.getInstitution(), contributor.getEmail()));
					this.result = stmtconn.execute ( );
					
					if (this.result.getUpdateCount ( ) < 1) {
						
						//warn, error, rollback, nothing????
					}
					
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
//					
					stmtconn.loadStatement (SelectFromDB.LatestPerson (stmtconn.connection, contributor.getFirstname(), contributor.getLastname()));
					this.result = stmtconn.execute ( );
					
					while (this.result.getResultSet ( ).next ( )) {
						
						person_id = this.result.getResultSet ( ).getBigDecimal(1);
					}
					
					stmtconn.loadStatement (InsertIntoDB.Object2Editor (stmtconn.connection, object_id, person_id, contributor.getNumber()));
					this.result = stmtconn.execute ( );
					
					if (this.result.getUpdateCount ( ) < 1) {
						
						//warn, error, rollback, nothing????
					}
//					db.insertObject2Editor(object_id, person_id, contributor
//							.getNumber());
					//TODO: Object2Editor für contributor???
				}
			}

			if (keywordList != null) {
				for (Keyword keyword : keywordList) {
					
					BigDecimal keyword_id = null;
					
					stmtconn.loadStatement (InsertIntoDB.Keyword (stmtconn.connection, keyword.getKeyword(), keyword.getLanguage()));
					this.result = stmtconn.execute ( );
					
					if (this.result.getUpdateCount ( ) < 1) {
						
						//warn, error, rollback, nothing????
					}
					
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
//					
					stmtconn.loadStatement (SelectFromDB.LatestKeyword (stmtconn.connection, keyword.getKeyword(), keyword.getLanguage()));
					this.result = stmtconn.execute ( );
					
					while (this.result.getResultSet ( ).next ( )) {
						
						keyword_id = this.result.getResultSet ( ).getBigDecimal(1);
					}
					
					stmtconn.loadStatement (InsertIntoDB.Object2Keyword (stmtconn.connection, object_id, keyword_id));
					this.result = stmtconn.execute ( );
					
					if (this.result.getUpdateCount ( ) < 1) {
						
						//warn, error, rollback, nothing????
					}
//					db.insertObject2Keyword(object_id, keyword_id);
				}
			}
			
			if (languageList != null) {
				for (Language language : languageList) {
					
					BigDecimal language_id = null;
					
					stmtconn.loadStatement (SelectFromDB.LanguageByName (stmtconn.connection, language.getLanguage()));
					this.result = stmtconn.execute ( );
					
					if (this.result.getResultSet ( ).next ( )) {
						
						stmtconn.loadStatement (InsertIntoDB.Language (stmtconn.connection, language.getLanguage()));
						this.result = stmtconn.execute ( );
						
						if (this.result.getUpdateCount ( ) < 1) {
							
							//warn, error, rollback, nothing????
						}
					}

					
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
					stmtconn.loadStatement (SelectFromDB.LanguageByName (stmtconn.connection, language.getLanguage()));
					this.result = stmtconn.execute ( );
					
					while (this.result.getResultSet ( ).next ( )) {
						
						language_id = this.result.getResultSet ( ).getBigDecimal(1);
					}
					
					stmtconn.loadStatement (InsertIntoDB.Object2Language (stmtconn.connection, object_id, language_id, language.getNumber()));
					this.result = stmtconn.execute ( );
					
					if (this.result.getUpdateCount ( ) < 1) {
						
						//warn, error, rollback, nothing????
					}

//					db.insertObject2Language(object_id, language_id, language
//							.getNumber());
				}
			}
			
			if (classificationList != null) {
				for (Classification classification : classificationList) {
					// fuer jeden Klassifikationstypen muessen unterschiedliche Aktionen erfolgen
					if (classification instanceof DDCClassification) {
						String ddcValue = null;
						
						stmtconn.loadStatement (SelectFromDB.DDCCategoriesByCategorie (stmtconn.connection, classification.getValue()));
						this.result = stmtconn.execute ( );
						
						while (this.result.getResultSet ( ).next ( )) {
							
							ddcValue = this.result.getResultSet ( ).getString(1);
						}
		
//						rs = db.selectDDCCategoriesByCategorie(classification.getValue());
//						while (rs.next()) {
//							ddcValue = rs.getString(1);
//						}
						if (ddcValue == null) {
							// Versuch, über den übergebenen Namen den richten DDC-Wert zu bestimmen
							
						}
						// Daten zuordnen
						
						stmtconn.loadStatement (InsertIntoDB.DDCClassification (stmtconn.connection, object_id, ddcValue));
						this.result = stmtconn.execute ( );
						
						if (this.result.getUpdateCount ( ) < 1) {
							
							//warn, error, rollback, nothing????
						}
//						db.insertDDCClassification(object_id, ddcValue);
					}
					if (classification instanceof DNBClassification) {
//						BigDecimal DNB_Categorie = null;
						String DNB_Categorie = null;
						
						stmtconn.loadStatement (SelectFromDB.DNBCategoriesByCategorie (stmtconn.connection, classification.getValue()));
						this.result = stmtconn.execute ( );
						
						while (this.result.getResultSet ( ).next ( )) {
							
							DNB_Categorie = this.result.getResultSet ( ).getString(1);
						}
						
//						rs = db.selectDNBCategoriesByCategorie(classification.getValue());
//						while (rs.next()) {
//							DNB_Categorie = rs.getString(1);
//						}
						// Daten zuordnen
						stmtconn.loadStatement (InsertIntoDB.DNBClassification (stmtconn.connection, object_id, DNB_Categorie));
						this.result = stmtconn.execute ( );
						
						if (this.result.getUpdateCount ( ) < 1) {
							
							//warn, error, rollback, nothing????
						}
//						db.insertDNBClassification (object_id, DNB_Categorie);
					}					
					if (classification instanceof DINISetClassification) {
						
						BigDecimal DINI_set_id = null;
						
						stmtconn.loadStatement (SelectFromDB.DINISetCategoriesByName (stmtconn.connection, classification.getValue()));
						this.result = stmtconn.execute ( );
						
						while (this.result.getResultSet ( ).next ( )) {
							
							DINI_set_id = this.result.getResultSet ( ).getBigDecimal(1);
						}
						
//						rs = db.selectDINISetCategoriesByName(classification.getValue());
//						while (rs.next()) {
//							DINI_set_id = rs.getBigDecimal(1);
//						}
						// Daten zuordnen
						stmtconn.loadStatement (InsertIntoDB.DINISetClassification (stmtconn.connection, object_id, DINI_set_id));
						this.result = stmtconn.execute ( );
						
						if (this.result.getUpdateCount ( ) < 1) {
							
							//warn, error, rollback, nothing????
						}
//						db.insertDINISetClassification(object_id, DINI_set_id);						
					}
					
					if (classification instanceof OtherClassification) {
						
						BigDecimal other_id = null;
						
						stmtconn.loadStatement (SelectFromDB.LatestOtherCategories (stmtconn.connection, classification.getValue()));
						this.result = stmtconn.execute ( );
						
						while (this.result.getResultSet ( ).next ( )) {
							
							other_id = this.result.getResultSet ( ).getBigDecimal(1);
						}
						// ID zum Klassifikationswort aus DB suchen 
//						rs = db.selectLatestOtherCategories(classification.getValue());
//						while (rs.next()) {
//							// Wort wohl vorhanden, sonst keine Rückgabe
//							other_id = rs.getBigDecimal(1);
//						} 
						if (other_id == null) {
							// Wort noch nicht vorhanden, neu eintragen
							// Klassifikation eintragen
							
							stmtconn.loadStatement (InsertIntoDB.OtherCategories (stmtconn.connection, classification.getValue()));
							this.result = stmtconn.execute ( );
							
							if (this.result.getUpdateCount ( ) < 1) {
								
								//warn, error, rollback, nothing????
							}
							stmtconn.loadStatement (SelectFromDB.LatestOtherCategories (stmtconn.connection, classification.getValue()));
							this.result = stmtconn.execute ( );
							
							while (this.result.getResultSet ( ).next ( )) {
								
								other_id = this.result.getResultSet ( ).getBigDecimal(1);
							}
							
//							db.insertOtherCategories(classification.getValue());
//							rs = db.selectLatestOtherCategories(classification.getValue());
//							while (rs.next()) {
//								other_id = rs.getBigDecimal(1);
//							}
						}
						// ID dieser Klassifikation bestimmen und zuordnen
//						db.insertOtherClassification(object_id, other_id);
						stmtconn.loadStatement (InsertIntoDB.OtherClassification (stmtconn.connection, object_id, other_id));
						this.result = stmtconn.execute ( );
						
						if (this.result.getUpdateCount ( ) < 1) {
							
							//warn, error, rollback, nothing????
						}
					}
				}
			}
//			db.commit();
			stmtconn.commit ( );
			res.addEntry ("oid", object_id.toPlainString ( ));
			this.rms.setStatus (RestStatusEnum.OK);
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} catch (WrongStatementException ex) {

			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			this.rms.setStatus (RestStatusEnum.WRONG_STATEMENT);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} finally {
			
			if (stmtconn != null) {
				
				try {
					
					stmtconn.close ( );
					stmtconn = null;
					
				} catch (SQLException ex) {
					
					ex.printStackTrace ( );
					logger.error (ex.getLocalizedMessage ( ));
				}
			}
			
			this.rms.addEntrySet (res);
			res = null;
			this.result = null;
			dbng = null;
		}
//		catch (SQLException sqlex) {
////			db.rollback();
////			db.setAutoCom (true);
//			try {
////				db.closeStatement ( );
//			} catch (SQLException ex) {
//				// TODO Auto-generated catch block
//				ex.printStackTrace();
//			}
////			db.closeConnection ( );
//			this.rms = new RestMessage (RestKeyword.InternalMetadataEntry);
//			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
//			this.rms.setStatusDescription("unable to commit: " + sqlex);
//			logger.error (sqlex.getLocalizedMessage ( ));
//			sqlex.printStackTrace ( );
//
//			return RestXmlCodec.encodeRestMessage (this.rms);
//		}
		
//		db.setAutoCom(true);
//		db.closeConnection ( );
		
		// RESPOND WITH OK
		
//		this.rms = new RestMessage (RestKeyword.InternalMetadataEntry);
//		res = new RestEntrySet ( );
//		
//		res.addEntry ("oid", object_id.toPlainString ( ));
//		this.rms.setStatus (RestStatusEnum.OK);
//		this.rms.addEntrySet (res);
		
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

}
