/**
 * 
 */

package de.dini.oanetzwerk.server.database;

import java.math.BigDecimal;
import java.sql.*;

import org.apache.log4j.Logger;

/**
 * @author Michael K&uuml;hn
 *
 */

class SybaseDBSelect implements DBSelectInterface {
	
	private static Connection connection = null;
	static Logger logger = Logger.getLogger (SybaseDBSelect.class);
	private PreparedStatement preparedstmt = null;
	
	/*public SybaseDBSelect (Connection conn) {
		
		connection = conn;
	}*/
	
	@Override
	public void prepareConnection (Connection conn) {
		
		connection = conn;
		if (logger.isDebugEnabled ( )) try {
			logger.debug ("connection prepared: " + connection.getMetaData ( ));
		} catch (SQLException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}
	
	@Override
	public ResultSet Object (BigDecimal oid) throws SQLException {
		
		this.preparedstmt = connection.prepareStatement ("SELECT * FROM dbo.Object o WHERE o.object_id = ?");
			
		this.preparedstmt.setBigDecimal (1, oid);
			
		return this.preparedstmt.executeQuery ( );
	}

	@Override
	public
	ResultSet ObjectEntryId (BigDecimal repositoryID, String externalOID) throws SQLException {
		
		if (logger.isDebugEnabled ( )) {
			
			logger.debug ("executing selectObjectEntryId");
			logger.debug ("repositoryID = " + repositoryID);
			logger.debug ("externalOID = " + externalOID);
		}
		
		this.preparedstmt = connection.prepareStatement ("SELECT o.object_id FROM dbo.Object o WHERE o.repository_id = ? and o.repository_identifier = ?");
		
		preparedstmt.setBigDecimal (1, repositoryID);
		preparedstmt.setString (2, externalOID);
		
		return preparedstmt.executeQuery ( );
	}
	
	@Override
	public ResultSet RawRecordData (BigDecimal internalOID, Date datestamp) throws SQLException {
		
		if (logger.isDebugEnabled ( )) {
			
			logger.debug ("executing selectRawRecordData");
			logger.debug ("internalOID: " + internalOID.toPlainString ( ));
			logger.debug ("datestamp :" + datestamp);
		}
		
		if (datestamp == null) {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("1 parameter for select RawRecordData");
			
			//TODO: TEST the enhanced SQL
			
			this.preparedstmt = connection.prepareStatement ("SELECT * FROM dbo.RawData rd WHERE rd.object_id = ? AND rd.repository_timestamp = (SELECT max(rdmax.repository_timestamp) FROM dbo.RawData rdmax WHERE rdmax.object_id = ?)");
			this.preparedstmt.setBigDecimal (1, internalOID);
			this.preparedstmt.setBigDecimal (2, internalOID);
			
		} else {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("2 parameter for select RawRecordData");
			
			this.preparedstmt = connection.prepareStatement ("SELECT * FROM dbo.RawData rd WHERE rd.object_id = ? AND rd.repository_timestamp = ?");
			this.preparedstmt.setBigDecimal (1, internalOID);
			this.preparedstmt.setDate (2, datestamp);
		}
		
		return this.preparedstmt.executeQuery ( );
	}
	
	@Override
	public ResultSet Title (BigDecimal object_id) throws SQLException {
		
			// number ?????
			this.preparedstmt = connection.prepareStatement ("SELECT title, qualifier, lang FROM dbo.Titles WHERE object_id = ?");
			
			//pstmt.setBigDecimal (1, predecessor_id);
			this.preparedstmt.setBigDecimal (1, object_id);
			
			return this.preparedstmt.executeQuery ( );
	}
	
	@Override
	public ResultSet Authors (BigDecimal object_id) throws SQLException {
		
		// number ?????
		this.preparedstmt = connection.prepareStatement ("SELECT O.number, P.firstname, P.lastname, P.title, P.institution, P.email FROM dbo.Person P JOIN dbo.Object2Author O ON P.person_id = O.person_id WHERE O.object_id = ?");
		
		//pstmt.setBigDecimal (1, predecessor_id);
		this.preparedstmt.setBigDecimal (1, object_id);
		
		return this.preparedstmt.executeQuery ( );
	}
	
	@Override
	public ResultSet Editors (BigDecimal oid) throws SQLException {

		// number ?????
		this.preparedstmt = connection.prepareStatement ("SELECT O.number, P.firstname, P.lastname, P.title, P.institution, P.email FROM dbo.Person P JOIN dbo.Object2Editor O ON P.person_id = O.person_id WHERE O.object_id = ?");
		this.preparedstmt.setBigDecimal (1, oid);
		return this.preparedstmt.executeQuery ( );
	}
	
	@Override
	public ResultSet Contributors (BigDecimal oid) throws SQLException {
		
		// number ?????
		this.preparedstmt = connection.prepareStatement ("SELECT O.number, P.firstname, P.lastname, P.title, P.institution, P.email FROM dbo.Person P JOIN dbo.Object2Contributor O ON P.person_id = O.person_id WHERE O.object_id = ?");
		
		//pstmt.setBigDecimal (1, predecessor_id);
		this.preparedstmt.setBigDecimal (1, oid);
		
		return this.preparedstmt.executeQuery ( );
	}
	
	@Override
	public ResultSet Format (BigDecimal object_id) throws SQLException {

		// number ?????
		this.preparedstmt = connection.prepareStatement ("SELECT schema_f, number FROM dbo.Format WHERE object_id = ?");
		
		//pstmt.setBigDecimal (1, predecessor_id);
		this.preparedstmt.setBigDecimal (1, object_id);
		
		return this.preparedstmt.executeQuery ( );
	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBSelectInterface#identifier(java.math.BigDecimal)
	 */
	@Override
	public ResultSet Identifier (BigDecimal object_id) throws SQLException {

		this.preparedstmt = connection.prepareStatement ("SELECT identifier, number FROM dbo.Identifier WHERE object_id = ?");
		
		//pstmt.setBigDecimal (1, predecessor_id);
		this.preparedstmt.setBigDecimal (1, object_id);
		
		return this.preparedstmt.executeQuery ( );
	}
	
	@Override
	public ResultSet Description (BigDecimal object_id) throws SQLException {
		
		// number ?????
		this.preparedstmt = connection.prepareStatement ("SELECT abstract, lang, number FROM dbo.Description WHERE object_id = ?");
		
		//pstmt.setBigDecimal (1, predecessor_id);
		this.preparedstmt.setBigDecimal (1, object_id);
		
		return this.preparedstmt.executeQuery ( );
	}
	
	@Override
	public ResultSet DateValues (BigDecimal oid) throws SQLException {
		
		// number ?????
		this.preparedstmt = connection.prepareStatement ("SELECT number, value FROM dbo.DateValues WHERE object_id = ?");
		this.preparedstmt.setBigDecimal (1, oid);
		return this.preparedstmt.executeQuery ( );
	}
	
	@Override
	public ResultSet TypeValue (BigDecimal oid) throws SQLException {

		// number ?????
		this.preparedstmt = connection.prepareStatement ("SELECT value FROM dbo.TypeValue WHERE object_id = ?");
		this.preparedstmt.setBigDecimal (1, oid);
		
		return this.preparedstmt.executeQuery ( );
	}
	
	@Override
	public ResultSet Publisher (BigDecimal oid) throws SQLException {

		this.preparedstmt = connection.prepareStatement ("SELECT name, number FROM dbo.Publisher WHERE object_id = ?");
		this.preparedstmt.setBigDecimal (1, oid);
		return this.preparedstmt.executeQuery ( );
	}
	
	@Override
	public ResultSet DDCClassification (BigDecimal oid) throws SQLException {
		
		this.preparedstmt = connection.prepareStatement ("SELECT name, D.DDC_Categorie FROM dbo.DDC_Classification D JOIN dbo.DDC_Categories C ON D.DDC_Categorie = C.DDC_Categorie WHERE D.object_id = ?");
		this.preparedstmt.setBigDecimal (1, oid);
		
		return this.preparedstmt.executeQuery ( );
	}
	
	@Override
	public ResultSet DNBClassification (BigDecimal oid) throws SQLException {
		
		this.preparedstmt = connection.prepareStatement ("SELECT name, D.DNB_Categorie FROM dbo.DNB_Classification D JOIN dbo.DNB_Categories C ON D.DNB_Categorie = C.DNB_Categorie WHERE D.object_id = ?");
		this.preparedstmt.setBigDecimal (1, oid);
		
		return this.preparedstmt.executeQuery ( );
	}
	
	@Override
	public ResultSet DINISetClassification (BigDecimal oid) throws SQLException {
		
		this.preparedstmt = connection.prepareStatement ("SELECT name, D.DINI_set_id FROM dbo.DINI_Set_Classification D JOIN dbo.DINI_Set_Categories C ON D.DINI_set_id = C.DINI_set_id WHERE D.object_id = ?");
		this.preparedstmt.setBigDecimal (1, oid);
		
		return this.preparedstmt.executeQuery ( );
	}
	
	@Override
	public ResultSet OtherClassification (BigDecimal oid) throws SQLException {
		
		this.preparedstmt = connection.prepareStatement ("SELECT name, D.other_id FROM dbo.Other_Classification D JOIN dbo.Other_Categories C ON D.other_id = C.other_id WHERE D.object_id = ?");
		this.preparedstmt.setBigDecimal (1, oid);
		
		return this.preparedstmt.executeQuery ( );
	}
	
	@Override
	public ResultSet Keywords (BigDecimal oid) throws SQLException {
		
		this.preparedstmt = connection.prepareStatement ("SELECT keyword, lang FROM dbo.Keywords K JOIN dbo.Object2Keywords O ON K.keyword_id = O.keyword_id WHERE O.object_id = ?");
		this.preparedstmt.setBigDecimal (1, oid);
		
		return this.preparedstmt.executeQuery ( );
	}
	
	@Override
	public ResultSet Languages (BigDecimal oid) throws SQLException {
		
		this.preparedstmt = connection.prepareStatement ("SELECT L.language, number FROM dbo.Language L JOIN dbo.Object2Language O ON L.language_id = O.language_id WHERE O.object_id = ?");
		this.preparedstmt.setBigDecimal (1, oid);
		
		return this.preparedstmt.executeQuery ( );
	}
	
	@Override
	public ResultSet LatestPerson (String firstname, String lastname) throws SQLException {
		
		this.preparedstmt = connection.prepareStatement ("SELECT MAX(person_id) FROM dbo.Person WHERE (firstname = ? AND lastname = ?)");
		this.preparedstmt.setString (1, firstname);
		this.preparedstmt.setString (2, lastname);
		
		return this.preparedstmt.executeQuery ( );
	}
	
	@Override
	public ResultSet LatestKeyword (String keyword, String lang) throws SQLException {
		
		this.preparedstmt = connection.prepareStatement ("SELECT MAX(keyword_id) FROM dbo.Keywords WHERE (keyword = ? AND lang = ?)");
		this.preparedstmt.setString (1, keyword);
		this.preparedstmt.setString (2, lang);
		
		return this.preparedstmt.executeQuery ( );
	}
	
	@Override
	public ResultSet LanguageByName (String language) throws SQLException {
		
		this.preparedstmt = connection.prepareStatement ("SELECT language_id FROM dbo.Language WHERE (language = ?)");
		this.preparedstmt.setString (1, language);
		
		return this.preparedstmt.executeQuery ( );
	}
	
	@Override
	public ResultSet LatestOtherCategories (String name) throws SQLException {
		
		this.preparedstmt = connection.prepareStatement ("SELECT MAX(other_id) FROM dbo.Other_Categories WHERE (name = ?)");
		this.preparedstmt.setString (1, name);
		
		return this.preparedstmt.executeQuery ( );
	}
	
	@Override
	public ResultSet DDCCategoriesByCategorie (String categorie) throws SQLException {
		
		this.preparedstmt = connection.prepareStatement ("SELECT DDC_Categorie FROM dbo.DDC_Categories WHERE (DDC_Categorie = ?)");
		this.preparedstmt.setString (1, categorie);
		
		return this.preparedstmt.executeQuery ( );
	}
	
	@Override
	public ResultSet DNBCategoriesByCategorie (String name) throws SQLException {
		
		this.preparedstmt = connection.prepareStatement ("SELECT DNB_Categorie FROM dbo.DNB_Categories WHERE (DNB_Categorie = ?)");
		this.preparedstmt.setString (1, name);
		
		return this.preparedstmt.executeQuery ( );
	}
	
	@Override
	public ResultSet Repository (BigDecimal repositoryID) throws SQLException {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("Trying to get RepositoryData");
		
		this.preparedstmt = connection.prepareStatement ("SELECT name, url, oai_url, test_data, harvest_amount, harvest_pause FROM dbo.Repositories WHERE (repository_id = ?)");
		this.preparedstmt.setBigDecimal (1, repositoryID);
		
		return this.preparedstmt.executeQuery ( );
	}
	
	@Override
	public ResultSet Service (BigDecimal service_id) throws SQLException {
		
		this.preparedstmt = connection.prepareStatement ("SELECT * FROM dbo.Services WHERE service_id = ?");
		this.preparedstmt.setBigDecimal (1, service_id);
		
		return this.preparedstmt.executeQuery ( );
	}
	
	@Override
	public ResultSet Service (String name) throws SQLException {
		
		this.preparedstmt = connection.prepareStatement ("SELECT * FROM dbo.Services WHERE name = ?");
		this.preparedstmt.setString (1, name);
		
		return this.preparedstmt.executeQuery ( );
	}
	
	@Override
	public ResultSet ServicesOrder (BigDecimal predecessor_id) throws SQLException {
		
		this.preparedstmt = connection.prepareStatement ("SELECT predecessor_id FROM dbo.ServicesOrder WHERE service_id = ?");
		this.preparedstmt.setBigDecimal (1, predecessor_id);
		
		return this.preparedstmt.executeQuery ( );
	}
	
	@Override
	public ResultSet Workflow (BigDecimal predecessor_id,	BigDecimal service_id) throws SQLException {
		
		this.preparedstmt = connection.prepareStatement ("SELECT w1.object_id FROM dbo.WorkflowDB w1 JOIN dbo.ServicesOrder so ON w1.service_id = so.predecessor_id AND so.service_id = ? " + 
										"WHERE (w1.time > (SELECT MAX(time) FROM dbo.WorkflowDB WHERE object_id = w1.object_id AND service_id = so.service_id) " +
										"OR w1.object_id NOT IN (SELECT object_id FROM dbo.WorkflowDB WHERE object_id = w1.object_id AND service_id = so.service_id)) GROUP BY w1.object_id");
		
		this.preparedstmt.setBigDecimal (1, service_id);
		
		return this.preparedstmt.executeQuery ( );
	}
}
