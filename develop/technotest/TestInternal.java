package technotest;

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
import de.dini.oanetzwerk.server.database.DBAccess;
import de.dini.oanetzwerk.server.database.DBAccessInterface;
import de.dini.oanetzwerk.servicemodule.aggregator.Aggregator;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.imf.*;

public class TestInternal {

	
	static Logger logger = Logger.getLogger(TestInternal.class);
	
	public static void put(InternalMetadata imf) {
		
		BigDecimal object_id = new BigDecimal ("634");
		
		DBAccessInterface db = DBAccess.createDBAccess ( );
		db.createConnection ( );
		
		String key = "";

		
		
		List <Title> titleList = imf.getTitles ( );
		List <DateValue> dateValueList = imf.getDateValues ( );
		List <Format> formatList = imf.getFormatList ( );
		List <Identifier> identifierList = imf.getIdentifierList ( );
		List <Description> descriptionList = imf.getDescriptions();
		List <Publisher> publisherList = imf.getPublishers();
		
		//		List <Author> authorList = imf.getAuthors();
		//List <Keyword> keywordsList = imf.getKeywords ( );


		List <TypeValue> typeValueList = imf.getTypeValueList ( );
		//List <Language> languageList;
		//List<Classification> classificationList;
		
		//ResultSet resultset;

		// Autocommit ausschalten, nur eine vollständige Transaktion darf durchlaufen
		db.setAutoCom (false);
		
		try {
			// Titel speichern
			for (Title title : titleList)			
				db.insertTitle (object_id, title.getQualifier ( ), title.getTitle ( ), title.getLang ( ));

			
			// Datumswerte
			for (DateValue dateValue : dateValueList) {
				try {
					db.insertDateValue(object_id, dateValue.getNumber(),
							HelperMethods.extract_datestamp(dateValue
									.getDateValue()));

				} catch (ParseException ex) {
					logger.error("Datestamp with datevalue incorrect");
					ex.printStackTrace();
				}
			}

			for (Format format : formatList)
				db.insertFormat(object_id, format.getNumber(), format
						.getSchema_f());

			for (Identifier identifier : identifierList)
				db.insertIdentifier(object_id, identifier.getNumber(),
						identifier.getIdentifier());

			for (Description description : descriptionList)
				db.insertDescription(object_id, description.getNumber(), description.getDescription());
			
			for (TypeValue typeValue : typeValueList)
				db.insertTypeValue(object_id, typeValue.getTypeValue());
			
			for (Publisher publisher : publisherList)
				db.insertPublisher(object_id, publisher.getNumber(), publisher.getName());			
			
		//for (Keyword keyword : keywords)
		//	db.insertkeyword ( );
		

			db.commit();
			

		} catch (SQLException sqlex) {
			System.out.println("Exception geworfen");
			db.rollback();
		}
		
		db.setAutoCom(true);
		db.closeConnection ( );
		
//		this.rms = new RestMessage (RestKeyword.InternalMetadataEntry);
//		res = new RestEntrySet ( );
//		
//		res.addEntry ("oid", object_id.toPlainString ( ));
//		this.rms.setStatus (RestStatusEnum.OK);
//		this.rms.addEntrySet (res);
//		
//		return RestXmlCodec.encodeRestMessage (this.rms);	}

	}
	
	
	public static void main(String[] args) {
	
		InternalMetadata imf = InternalMetadata.createDummy();

		System.out.println(imf);
	
			
		put(imf);
	}
	
	
	/**
	 * @param args
	 */
	public static void get() {
		// TODO Auto-generated method stub

		
		// erzeuge imf-Object, das Schrittweise mit Daten befüllt wird
		InternalMetadata imf = new InternalMetadata();
		
		ResultSet rs;
		DBAccessInterface db = DBAccess.createDBAccess ( );
		db.createConnection ( );
		
		// hier die Test-ID
		BigDecimal oid = new BigDecimal ("633");
		
		// Auswertung der Title-Informationen
		

		
		try {
			// Auswertung der Titel
			rs = db.selectTitle (oid);
			if (rs == null) {
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
			}


			// Auswertung der Autoren
			rs = db.selectAuthors(oid);
			if (rs == null) {
				System.out.println("kein Author vorhanden");
			} else {
				System.out.println("Author vorhanden");
				try {
					while (rs.next ( )) {
						Author temp = new Author ( );
						temp.setNumber(rs.getInt("number"));
						temp.setFirstname(rs.getString("firstname"));
						temp.setLastname(rs.getString("lastname"));
						temp.setInstitution(rs.getString("institution"));
						temp.setEmail(rs.getString("email"));
						temp.setTitle(rs.getString("title"));
						imf.addAuthor (temp);
					}
				} catch (SQLException ex) {
					ex.printStackTrace ( );
				}
			}	


			// Auswertung der Editoren
			rs = db.selectEditors(oid);
			if (rs == null) {
				System.out.println("kein Editor vorhanden");
			} else {
				System.out.println("Editor vorhanden");
				try {
					while (rs.next ( )) {
						Editor temp = new Editor ( );
						temp.setNumber(rs.getInt("number"));
						temp.setFirstname(rs.getString("firstname"));
						temp.setLastname(rs.getString("lastname"));
						temp.setInstitution(rs.getString("institution"));
						temp.setEmail(rs.getString("email"));
						temp.setTitle(rs.getString("title"));
						imf.addEditor (temp);
					}
				} catch (SQLException ex) {
					ex.printStackTrace ( );
				}
			}	
			


			// Auswertung der Bearbeiter
			rs = db.selectContributors(oid);
			if (rs == null) {
				System.out.println("kein Contirbutor vorhanden");
			} else {
				System.out.println("Contributor vorhanden");
				try {
					while (rs.next ( )) {
						Contributor temp = new Contributor ( );
						temp.setNumber(rs.getInt("number"));
						temp.setFirstname(rs.getString("firstname"));
						temp.setLastname(rs.getString("lastname"));
						temp.setInstitution(rs.getString("institution"));
						temp.setEmail(rs.getString("email"));
						temp.setTitle(rs.getString("title"));
						imf.addContributor (temp);
					}
				} catch (SQLException ex) {
					ex.printStackTrace ( );
				}
			}			
			
			
			
			// Auswertung des Formats
			rs = db.selectFormat(oid);
			if (rs == null) {
				System.out.println("kein Format");
			} else {
				System.out.println("Format vorhanden");
				try {
					while (rs.next ( )) {
						Format temp = new Format ( );
						temp.setSchema_f(rs.getString ("schema_f"));
						temp.setNumber (rs.getInt ("number"));
						imf.addFormat (temp);
					}
				} catch (SQLException ex) {
					ex.printStackTrace ( );
				}
			}
			
			
			// Auswertung des Identifiers
			rs = db.selectIdentifier(oid);
			if (rs == null) {
				System.out.println("kein Identifier");
			} else {
				System.out.println("Identifier vorhanden");
				try {
					while (rs.next ( )) {
						Identifier temp = new Identifier ( );
						temp.setIdentifier(rs.getString ("identifier"));
//						temp.setLanguage(rs.getString ("lang"));
						temp.setNumber (rs.getInt ("number"));
						imf.addIdentifier (temp);
					}
				} catch (SQLException ex) {
					ex.printStackTrace ( );
				}
			}

			
			// Auswertung der Description
			rs = db.selectDescription(oid);
			if (rs == null) {
				System.out.println("keine Description");
			} else {
				System.out.println("Description vorhanden");
				try {
					while (rs.next ( )) {
						Description temp = new Description ( );
						temp.setDescription(rs.getString ("abstract"));
						temp.setLanguage(rs.getString ("lang"));
						temp.setNumber (rs.getInt ("number"));
						imf.addDescription (temp);
					}
				} catch (SQLException ex) {
					ex.printStackTrace ( );
				}
			}

			
			// Auswertung der DateValue-Werte
			rs = db.selectDateValues(oid);
			if (rs == null) {
				System.out.println("keine Date-Value-Werte vorhanden");
			} else {
				System.out.println("DateValue-Wert vorhanden");
				try {
					while (rs.next ( )) {
						DateValue temp = new DateValue ( );
						temp.setDateValue(rs.getString ("value"));
						temp.setNumber (rs.getInt ("number"));
						imf.addDateValue (temp);
					}
				} catch (SQLException ex) {
					ex.printStackTrace ( );
				}
			}

			
			// Auswertung der TypeValue-Werte
			rs = db.selectTypeValue(oid);
			if (rs == null) {
				System.out.println("keine TypeValue-Werte vorhanden");
			} else {
				System.out.println("TypeValue-Wert vorhanden");
				try {
					while (rs.next ( )) {
						TypeValue temp = new TypeValue ( );
						temp.setTypeValue(rs.getString ("value"));
//						temp.setNumber (rs.getInt ("number"));
						imf.addTypeValue (temp);
					}
				} catch (SQLException ex) {
					ex.printStackTrace ( );
				}
			}

			
			// Auswertung der Publisher-Werte
			rs = db.selectPublisher(oid);
			if (rs == null) {
				System.out.println("keine Publisher-Werte vorhanden");
			} else {
				System.out.println("Publisher-Wert vorhanden");
				try {
					while (rs.next ( )) {
						Publisher temp = new Publisher ( );
						temp.setName(rs.getString ("name"));
						temp.setNumber (rs.getInt ("number"));
						imf.addPublisher (temp);
					}
				} catch (SQLException ex) {
					ex.printStackTrace ( );
				}
			}			


			// Auswertung der DCC-Classifications-Werte
			rs = db.selectDDCClassification(oid);
			if (rs == null) {
				System.out.println("keine DDC-Werte vorhanden");
			} else {
				System.out.println("DDC-Wert vorhanden");
				try {
					while (rs.next ( )) {
						Classification cl = new DDCClassification();
						cl.setValue(rs.getString("D.DCC_Categorie"));
//						temp.setNumber (rs.getInt ("number"));
						imf.addClassfication(cl);
					}
				} catch (SQLException ex) {
					ex.printStackTrace ( );
				}
			}	
			
			// Auswertung der DNB-Classifications-Werte
			rs = db.selectDNBClassification(oid);
			if (rs == null) {
				System.out.println("keine DNB-Werte vorhanden");
			} else {
				System.out.println("DNB-Wert vorhanden");
				try {
					while (rs.next ( )) {
						Classification cl = new DNBClassification();
						cl.setValue(rs.getString("D.DNB_Categorie"));
//						temp.setNumber (rs.getInt ("number"));
						imf.addClassfication(cl);
					}
				} catch (SQLException ex) {
					ex.printStackTrace ( );
				}
			}	

			// Auswertung der DINI-Set-Classifications-Werte
			rs = db.selectDINISetClassification(oid);
			if (rs == null) {
				System.out.println("keine DINI-Set-Werte vorhanden");
			} else {
				System.out.println("DINI-Set-Wert vorhanden");
				try {
					while (rs.next ( )) {
						Classification cl = new DINISetClassification();
						cl.setValue(rs.getString("name"));
//						temp.setNumber (rs.getInt ("number"));
						imf.addClassfication(cl);
					}
				} catch (SQLException ex) {
					ex.printStackTrace ( );
				}
			}

			// Auswertung der Other-Classifications-Werte
			rs = db.selectOtherClassification(oid);
			if (rs == null) {
				System.out.println("keine Other-Classifiation-Werte vorhanden");
			} else {
				System.out.println("Other-Classification-Wert vorhanden");
				try {
					while (rs.next ( )) {
						Classification cl = new OtherClassification();
						cl.setValue(rs.getString("name"));
//						temp.setNumber (rs.getInt ("number"));
						imf.addClassfication(cl);
					}
				} catch (SQLException ex) {
					ex.printStackTrace ( );
				}
			}

			// Auswertung der Keywords-Werte
			rs = db.selectKeywords(oid);
			if (rs == null) {
				System.out.println("keine Keyword-Werte vorhanden");
			} else {
				System.out.println("Keyword vorhanden");
				try {
					while (rs.next ( )) {
						Keyword temp = new Keyword();
						temp.setKeyword(rs.getString("keyword"));
						temp.setLanguage(rs.getString("language"));
//						temp.setNumber (rs.getInt ("number"));
						imf.addKeyword(temp);
					}
				} catch (SQLException ex) {
					ex.printStackTrace ( );
				}
			}
			
			// Auswertung der Keywords-Werte
			rs = db.selectLanguages(oid);
			if (rs == null) {
				System.out.println("keine Language-Werte vorhanden");
			} else {
				System.out.println("Language vorhanden");
				try {
					while (rs.next ( )) {
						Language temp = new Language();
						temp.setLanguage(rs.getString("L.language"));
						temp.setNumber (rs.getInt ("number"));
						imf.addLanguage(temp);
					}
				} catch (SQLException ex) {
					ex.printStackTrace ( );
				}
			}
			
			

		} catch (SQLException ex) {
			ex.printStackTrace();
		}  catch (RuntimeException ex) {
			ex.printStackTrace ( );
		}
		
		






		db.closeConnection();
		
		

		System.out.println(imf);
		
		String xmlData;
// xmlData = imMarsch.marshall (imf);
		
// List <HashMap <String, String>> listentries = new ArrayList <HashMap <String,
// String>> ( );
// HashMap <String, String> mapEntry = new HashMap <String ,String> ( );
//		
// // mapEntry.put ("internalmetadata", xmlData);
// listentries.add (mapEntry);
// String requestxml = RestXmlCodec.encodeEntrySetResponseBody (listentries,
// "InternalMetadataEntry");
		
// return requestxml;

		
		
		
		
		
		
	}
}
