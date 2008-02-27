package technotest;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.dini.oanetzwerk.server.database.DBAccess;
import de.dini.oanetzwerk.server.database.DBAccessInterface;
import de.dini.oanetzwerk.utils.RestXmlCodec;
import de.dini.oanetzwerk.utils.imf.Author;
import de.dini.oanetzwerk.utils.imf.Description;
import de.dini.oanetzwerk.utils.imf.Format;
import de.dini.oanetzwerk.utils.imf.Identifier;
import de.dini.oanetzwerk.utils.imf.InternalMetadata;
import de.dini.oanetzwerk.utils.imf.Title;

public class TestInternal {


	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		// erzeuge imf-Object, das Schrittweise mit Daten befüllt wird
		InternalMetadata imf = new InternalMetadata();
		
		ResultSet rs;
		DBAccessInterface db = DBAccess.createDBAccess ( );
		db.createConnection ( );
		
		BigDecimal oid = new BigDecimal ("633");
		
		// Auswertung der Description
		
//		rs = db.selectDescription(oid);
//		if (rs == null) {
//			System.out.println("keine Description");
//		} else {
//			System.out.println("Description vorhanden");
//			try {
//				while (rs.next ( )) {
//					Description temp = new Description ( );
//					temp.setDescription(rs.getString ("abstract"));
//					temp.setLanguage(rs.getString ("lang"));
//					temp.setNumber (rs.getInt ("number"));
//					imf.addDescription (temp);
//				}
//			} catch (SQLException ex) {
//				ex.printStackTrace ( );
//			}
//		}

		rs = db.selectIdentifier(oid);
		if (rs == null) {
			System.out.println("kein Identifier");
		} else {
			System.out.println("Identifier vorhanden");
			try {
				while (rs.next ( )) {
					Identifier temp = new Identifier ( );
					temp.setIdentifier(rs.getString ("identifier"));
//					temp.setLanguage(rs.getString ("lang"));
					temp.setNumber (rs.getInt ("number"));
					imf.addIdentifier (temp);
				}
			} catch (SQLException ex) {
				ex.printStackTrace ( );
			}
		}

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

		rs = db.selectAuthors(oid);
		if (rs == null) {
			System.out.println("kein Author");
		} else {
			System.out.println("Author vorhanden");
			try {
				while (rs.next ( )) {
					Author temp = new Author ( );
					temp.setNumber(rs.getInt("number"));
					temp.setFirstname(rs.getString("firstname"));
					temp.setLastname(rs.getString("lastname"));
//					temp.setSchema_f(rs.getString ("schema_f"));
//					temp.setNumber (rs.getInt ("number"));
					imf.addAuthor (temp);
				}
			} catch (SQLException ex) {
				ex.printStackTrace ( );
			}
		}		
		
		
		// Auswertung der Titel
		rs = db.selectTitle (oid);
		
		if (rs == null) {
			System.out.println("kein Title vorhanden");
//			logger.warn ("resultset empty!");
	} else {
		try {
			System.out.println("Title vorhanden");
			while (rs.next ( )) {
				Title temp = new Title ( );
				temp.setTitle (rs.getString ("title"));
				temp.setQualifier (rs.getString ("qualifier"));
				temp.setLang (rs.getString ("lang"));
				//temp.setNumber (rs.getInt ("number"));
				imf.addTitle (temp);
			}
			
		} catch (SQLException ex) {
			
			ex.printStackTrace ( );
		}
	}
		System.out.println(imf);
		
		String xmlData;
//		xmlData = imMarsch.marshall (imf);
		
//		List <HashMap <String, String>> listentries = new ArrayList <HashMap <String, String>> ( );
//		HashMap <String, String> mapEntry = new HashMap <String ,String> ( );
//		
////		mapEntry.put ("internalmetadata", xmlData);
//		listentries.add (mapEntry);
//		String requestxml = RestXmlCodec.encodeEntrySetResponseBody (listentries, "InternalMetadataEntry");
		
//		return requestxml;

		
		
		
		
		
		
	}

}
