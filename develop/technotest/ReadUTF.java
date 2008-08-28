package technotest;

import java.sql.PreparedStatement;

import javax.naming.Context;

import de.dini.oanetzwerk.server.database.DBAccessNG;
import de.dini.oanetzwerk.server.database.QueryResult;
import de.dini.oanetzwerk.server.database.SingleStatementConnection;

public class ReadUTF {

	public static void main(String[] args) throws Exception {
	// Auswertung der Other-Classifications-Werte
		
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "de.dini.oanetzwerk.server.handler.OneShotInitialContextFactory");
	
		DBAccessNG db = new DBAccessNG();
		
		SingleStatementConnection stmt = (SingleStatementConnection) db.getSingleStatementConnection();
//		PreparedStatement pstmt = stmt.connection.prepareStatement("SELECT * FROM dbo.test2");
		PreparedStatement pstmt = stmt.connection.prepareStatement("INSERT INTO dbo.test2 (name) values (?)");
		pstmt.setString(1, "Каталог");
		
		stmt.loadStatement(pstmt);
		@SuppressWarnings("unused")
		QueryResult result = stmt.execute();
		
		
//		pstmt = stmt.connection.prepareStatement("SELECT * FROM dbo.test");
//		stmt.loadStatement(pstmt);
//		result = stmt.execute();
//		
//		while (result.getResultSet().next()) {
//			System.out.println("name=" + result.getResultSet().getString("name"));
//		}
		
	}
}
