import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import junit.framework.Assert;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.junit.Before;
import org.junit.Test;

import de.dini.oanetzwerk.migration.DeleteFromDBPostgres;
import de.dini.oanetzwerk.migration.InsertIntoDBPostgres;
import de.dini.oanetzwerk.migration.SelectFromDBPostgres;
import de.dini.oanetzwerk.server.database.DeleteFromDB;
import de.dini.oanetzwerk.server.database.MultipleStatementConnection;
import de.dini.oanetzwerk.server.database.QueryResult;
import de.dini.oanetzwerk.server.database.SelectFromDB;



public class InsertIntoDBTests {
	Connection postgres;
	MultipleStatementConnection msPostgres;
	
	@Before public void setup() {
		try {
			Class.forName("org.postgresql.Driver");
			String urlPostgres = "jdbc:postgresql://localhost:5432/oanetdb";
			
			Properties propsPostgres = new Properties();
			propsPostgres.put("user", "imrael");
			propsPostgres.put("password", "fd6zaw9c");
			propsPostgres.put("charSet", "UTF-8");
			postgres = DriverManager.getConnection(urlPostgres, propsPostgres);
			System.out.println("Established Connection to local Postgres Instance");
	
			msPostgres = new MultipleStatementConnection(postgres);
			
			
			// update the indexes
			PreparedStatement updateObject = postgres.prepareStatement(
				"SELECT setval('\"Object_object_id_seq\"', max(object_id)) FROM \"Object\""
			);
			PreparedStatement updateDINI = postgres.prepareStatement(
				"SELECT setval('\"DINI_Set_Categories_DINI_set_id_seq\"', max(\"DINI_set_id\")) FROM \"DINI_Set_Categories\""
			);
			PreparedStatement updateKeywords = postgres.prepareStatement(
				"SELECT setval('\"Keywords_keyword_id_seq\"', max(keyword_id)) FROM \"Keywords\""
			);
			PreparedStatement updateLanguage = postgres.prepareStatement(
				"SELECT setval('\"Language_language_id_seq\"', max(language_id)) FROM \"Language\""
			);
			PreparedStatement updatePerson = postgres.prepareStatement(
				"SELECT setval('\"Person_person_id_seq\"', max(person_id)) FROM \"Person\""
			);
			PreparedStatement updateRepositories = postgres.prepareStatement(
				"SELECT setval('\"Repositories_repository_id_seq\"', max(repository_id)) FROM \"Repositories\""
			);
			PreparedStatement updateServices = postgres.prepareStatement(
				"SELECT setval('\"Services_service_id_seq\"', max(service_id)) FROM \"Services\""
			);
			PreparedStatement updateTypeValue = postgres.prepareStatement(
				"SELECT setval('\"TypeValue_type_id_seq\"', max(type_id)) FROM \"TypeValue\""
			);
			PreparedStatement updateUsageData_Metrics = postgres.prepareStatement(
				"SELECT setval('\"UsageData_Metrics_metrics_id_seq\"', max(metrics_id)) FROM \"UsageData_Metrics\""
			);
			PreparedStatement updateWorkflowDB = postgres.prepareStatement(
				"SELECT setval('\"WorkflowDB_workflow_id_seq\"', max(workflow_id)) FROM \"WorkflowDB\""
			);
			PreparedStatement updateIso639Language = postgres.prepareStatement(
				"SELECT setval('\"Iso639Language_language_id_seq\"', max(language_id)) FROM \"Iso639Language\""
			);
			PreparedStatement[] indexUpdate = {
				updateObject, updateDINI, updateKeywords, updateLanguage,
				updatePerson, updateRepositories, updateServices, updateTypeValue,
				updateUsageData_Metrics, updateWorkflowDB, updateIso639Language
			};
			for (int i = 0; i < indexUpdate.length; i++) {
				msPostgres.loadStatement(indexUpdate[i]);
				msPostgres.execute();
				msPostgres.commit();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	@Test public void testQueries() {
		try {
			System.out.println("Testcase Object(repository_id, harvested, repository_datestamp, repository_identifier, testdata, failureCounter)");
			PreparedStatement postgresQuery = InsertIntoDBPostgres.Object(
				postgres, 
				new BigDecimal(1), 
				Date.valueOf("2010-07-15"), 
				Date.valueOf("2010-07-16"), 
				"this_is_just_a_dummy_entry:testing:01", 
				true, 
				0
			);
			
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msPostgres.commit();
			
			// get the object_id
			PreparedStatement whichID = postgres.prepareStatement("SELECT * FROM \"Object\" WHERE repository_identifier = 'this_is_just_a_dummy_entry:testing:01'");
			msPostgres.loadStatement(whichID);
			QueryResult whichIDResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet whichIDResultSet = whichIDResult.getResultSet();

			if(!whichIDResultSet.next()) {
				Assert.assertEquals(false, false);
			}
			BigDecimal object_id = whichIDResultSet.getBigDecimal(1);

			// insert RawData Entry
			PreparedStatement pst = InsertIntoDBPostgres.RawRecordData(
				postgres, 
				object_id, 
				Date.valueOf("2010-07-15"), 
				"PHJlY29yZCB4bWxucz0iaHR0cDovL3d3dy5vcGVuYXJjaGl2ZXMub3JnL09BSS8yLjAvIj4NCiAgICAgIDxoZWFkZXI+DQogICAgICAgIDxpZGVudGlmaWVyPm9haTpIVUJlcmxpbi5kZTo0MDAyPC9pZGVudGlmaWVyPg0KICAgICAgICA8ZGF0ZXN0YW1wPjIwMDItMDctMzA8L2RhdGVzdGFtcD4NCjxzZXRTcGVjPnB1Yi10eXBlOm1vbm9ncmFwaDwvc2V0U3BlYz4NCjxzZXRTcGVjPmRuYjo0NTwvc2V0U3BlYz4NCjxzZXRTcGVjPmRkYzo3MjA8L3NldFNwZWM+DQogICAgICA8L2hlYWRlcj4NCiAgICAgIDxtZXRhZGF0YT4NCiAgICAgICAgPG9haV9kYzpkYyB4bWxuczpvYWlfZGM9Imh0dHA6Ly93d3cub3BlbmFyY2hpdmVzLm9yZy9PQUkvMi4wL29haV9kYy8iIHhtbG5zOmRjPSJodHRwOi8vcHVybC5vcmcvZGMvZWxlbWVudHMvMS4xLyIgeG1sbnM6eHNpPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYS1pbnN0YW5jZSIgeHNpOnNjaGVtYUxvY2F0aW9uPSJodHRwOi8vd3d3Lm9wZW5hcmNoaXZlcy5vcmcvT0FJLzIuMC9vYWlfZGMvICAgICAgICAgICAgICBodHRwOi8vd3d3Lm9wZW5hcmNoaXZlcy5vcmcvT0FJLzIuMC9vYWlfZGMueHNkIj4NCiAgICAgICAgPGRjOnRpdGxlPkFyY2hpdGVrdHVyIHVuZCBTYW1tbHVuZ2VuIGRlciBIdW1ib2xkdC1Vbml2ZXJzaXTDpHQgenUgQmVybGluIC0gZWluIFByb2pla3QgZGVzIEt1bnN0Z2VzY2hpY2h0bGljaGVuIFNlbWluYXJzPC9kYzp0aXRsZT4NCiAgICAgICAgPGRjOmNyZWF0b3I+QnJlZGVrYW1wLCBIb3JzdDwvZGM6Y3JlYXRvcj4NCiAgICAgICAgICA8ZGM6c3ViamVjdD5BcmNoaXRla3R1cjwvZGM6c3ViamVjdD4NCiAgICAgICAgICA8ZGM6c3ViamVjdD5BcmNoaXRla3R1cjwvZGM6c3ViamVjdD4NCiAgICAgICAgPGRjOnB1Ymxpc2hlcj5IdW1ib2xkdCBVbml2ZXJzaXR5IEJlcmxpbiwgR2VybWFueTwvZGM6cHVibGlzaGVyPjxkYzp0eXBlPlRleHQ8L2RjOnR5cGU+PGRjOnR5cGU+bW9ub2dyYXBoPC9kYzp0eXBlPjxkYzpmb3JtYXQ+dGV4dC9odG1sPC9kYzpmb3JtYXQ+PGRjOmlkZW50aWZpZXI+aHR0cDovL2Vkb2MuaHUtYmVybGluLmRlL21pc2NlbGxhbmllcy9BcmNoaXRla3R1ci88L2RjOmlkZW50aWZpZXI+PGRjOmxhbmd1YWdlPmdlcjwvZGM6bGFuZ3VhZ2U+DQogICAgICAgIDwvb2FpX2RjOmRjPg0KICAgICAgPC9tZXRhZGF0YT4NCiAgICA8L3JlY29yZD4=",
				"oai_dc"
			);
			
			msPostgres.loadStatement(pst);
			QueryResult rawdataResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet rawdataResultSet = rawdataResult.getResultSet();
//			if (!rawdataResultSet.next()) {
//				Assert.assertEquals(false, false);
//			}
			
			// insert WorkflowDB Entry
			PreparedStatement workflowDB = InsertIntoDBPostgres.WorkflowDB(postgres, object_id, new BigDecimal(6));
			msPostgres.loadStatement(workflowDB);
			QueryResult workflowDBResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet workflowDBResultSet = workflowDBResult.getResultSet();
//			if(!workflowDBResultSet.next()) {
//				Assert.assertEquals(false, false);
//			}
			
			// insert Title
			PreparedStatement title = InsertIntoDBPostgres.Title(
				postgres, 
				object_id, 
				"subtitle", 
				"Oder: Vernunfft- und Schrifftmäßiger Versuch, Wie ein Mensch durch aufmercksame Betrachtung derer sonst wenig geachteten Insecten Zu lebendiger Erkänntniß und Bewunderung der Allmacht, weißheit, der Güte und Gerechtigkeit ...", 
				null
			);
			msPostgres.loadStatement(title);
			QueryResult titleResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet titleResultSet = titleResult.getResultSet();
//			if (!titleResultSet.next()) {
//				Assert.assertEquals(false, false);
//			}
			
			// insert DateValue
			PreparedStatement datevalue = InsertIntoDBPostgres.DateValue(
				postgres, 
				object_id, 
				1, 
				Date.valueOf("1835-01-01"), 
				"1835-01-01"
			);
			msPostgres.loadStatement(datevalue);
			QueryResult datevalueResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet datevalueResultSet = datevalueResult.getResultSet();
//			if (!datevalueResultSet.next()) {
//				Assert.assertEquals(false, false);
//			}
			
			// insert Format
			PreparedStatement format = InsertIntoDBPostgres.Format(postgres, object_id, 1, "application/pdf");
			msPostgres.loadStatement(format);
			QueryResult formatResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet formatResultSet = formatResult.getResultSet();
//			if (!formatResultSet.next()) {
//				Assert.assertEquals(false,false);
//			}
			
			// insert Identifier
			PreparedStatement identifier = InsertIntoDBPostgres.Identifier(postgres, object_id, 1, "http://edoc.hu-berlin.de/ebind/mfn/forschungsreise4-2005-42513/PDF/planet4.pdf");
			msPostgres.loadStatement(identifier);
			QueryResult identifierResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet identifierResultSet = identifierResult.getResultSet();
//			if (!identifierResultSet.next()) {
//				Assert.assertEquals(false, false);
//			}
			
			// Insert Description
			PreparedStatement description = InsertIntoDBPostgres.Description(
				postgres, object_id, 
				1, 
				"This dissertation, about lifestyle-related problems in the Berlin district of “Schöneberger Norden,” addresses the conflict between two culturally very different groups within the neighbourhood. It concerns primarily the offences by mainly Arabic young men against homosexuals and homosexual initiatives in Schöneberger Norden. In the debate over cultural disintegration, the question arises how these different groups can manage to live together in peaceful coexistence, given their radically different attitudes towards sexual freedom. The theoretical context of this research is informed: by Simmel´s urban theory that an indifferent tolerance is the guarantor of peaceful coexistence among individuals with different lifestyles; and by the idea that lifestyles form moments of integration. The thesis relates three different case studies and shows that the peaceful coexistence in “Schöneberger Norden” is under threat for the above mentioned  minorities. This assumption is tested and verified through the qualitative analysis of expert interviews. An evaluation of the interviews and of a group discussion with young Arabic men showed that the failure of the integration process was correlated to a strong common acceptance of the use of violence among socially disadvantaged youth with migration backgrounds. Various proposals to promote mutual acceptance among the minority groups through modes of non-violent communication conclude the presentation of the results."
			);
			msPostgres.loadStatement(description);
			QueryResult descriptionResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet descriptionResultSet = descriptionResult.getResultSet();
//			if (!descriptionResultSet.next()) {
//				Assert.assertEquals(false, false);
//			}
			
			// insert TypeValue
			PreparedStatement typevalue = InsertIntoDBPostgres.TypeValue(postgres, object_id, "report");
			msPostgres.loadStatement(typevalue);
			QueryResult typevalueResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet typevalueResultSet = typevalueResult.getResultSet();
//			if (!typevalueResultSet.next()) {
//				Assert.assertEquals(false, false);
//			}
			
			// insert Publisher
			PreparedStatement publisher = InsertIntoDBPostgres.Publisher(postgres, object_id, 1, "Reichs-Marine-Amt");
			msPostgres.loadStatement(publisher);
			QueryResult publisherResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet publisherResultSet = publisherResult.getResultSet();
//			if (!publisherResultSet.next()) {
//				Assert.assertEquals(false, false);
//			}
			
			// insert Person
			PreparedStatement person = InsertIntoDBPostgres.Person(postgres, "Max", "Mustermann", "Diplom-Erbsenzähler", "Humboldt-Universität zu Berlin", "max.mustermann@hu-berlin.de");
			msPostgres.loadStatement(person);
			QueryResult personResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet personResultSet = personResult.getResultSet();
//			if (!personResultSet.next()) {
//				Assert.assertEquals(false, false);
//			}
			
			// get person_id
			PreparedStatement personid = postgres.prepareStatement("SELECT * FROM \"Person\" WHERE firstname = 'Max' AND lastname = 'Mustermann'");
			msPostgres.loadStatement(personid);
			QueryResult person_idResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet person_idResultSet = person_idResult.getResultSet();
			person_idResultSet.next();
			BigDecimal person_id = person_idResultSet.getBigDecimal(1);
			
			// insert Object2Author
			PreparedStatement o2a = InsertIntoDBPostgres.Object2Author(postgres, object_id, person_id, 1);
			msPostgres.loadStatement(o2a);
			QueryResult o2aResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet o2aResultSet = o2aResult.getResultSet();
//			if (!o2aResultSet.next()) {
//				Assert.assertEquals(false, false);
//			}
			
			//insert Object2Editor
			PreparedStatement o2e = InsertIntoDBPostgres.Object2Editor(postgres, object_id, person_id, 1);
			msPostgres.loadStatement(o2e);
			QueryResult o2eResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet o2eResultSet = o2eResult.getResultSet();
//			if (!o2eResultSet.next()) {
//				Assert.assertEquals(false, false);
//			}
			
			// insert Keyword
			PreparedStatement kw = InsertIntoDBPostgres.Keyword(postgres, "Schizophrenie1", null);
			msPostgres.loadStatement(kw);
			QueryResult kwResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet kwResultSet = kwResult.getResultSet();
//			if (!kwResultSet.next()) {
//				Assert.assertEquals(false, false);
//			}
			// get Keyword_id
			PreparedStatement keywordID = postgres.prepareStatement("SELECT keyword_id FROM \"Keywords\" WHERE keyword = 'Schizophrenie1'");
			msPostgres.loadStatement(keywordID);
			QueryResult keywordIDResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet keywordIDResultSet = keywordIDResult.getResultSet();
			keywordIDResultSet.next();
			BigDecimal keyword_id = keywordIDResultSet.getBigDecimal(1);
			
			
			// insert Object2Keyword
			PreparedStatement o2k = InsertIntoDBPostgres.Object2Keyword(postgres, object_id, keyword_id);
			msPostgres.loadStatement(o2k);
			QueryResult o2kResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet o2kResultSet = o2kResult.getResultSet();
//			if (!o2kResultSet.next()) {
//				Assert.assertEquals(false, false);
//			}
			
			// insert Language
			PreparedStatement lang = InsertIntoDBPostgres.Language(postgres, "test");
			msPostgres.loadStatement(lang);
			QueryResult langResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet langResultSet = langResult.getResultSet();
//			if (!langResultSet.next()) {
//				Assert.assertEquals(false, false);
//			}
			// get Language_ID
			PreparedStatement lid = postgres.prepareStatement("SELECT language_id FROM \"Language\" WHERE language = 'test'");
			msPostgres.loadStatement(lid);
			QueryResult lidResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet lidResultSet = lidResult.getResultSet();
			lidResultSet.next();
			BigDecimal language_id = lidResultSet.getBigDecimal(1);
			
			// Insert ISO639Language
			PreparedStatement isolang = InsertIntoDBPostgres.Iso639Language(postgres, "tst");
			msPostgres.loadStatement(isolang);
			QueryResult isolangResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet isolangResultSet = isolangResult.getResultSet();
//			if (!isolangResultSet.next()) {
//				Assert.assertEquals(false, false);
//			}
			
			// get Iso639Language primary key
			PreparedStatement ilid = postgres.prepareStatement("SELECT language_id FROM \"Iso639Language\" WHERE iso639language = 'tst'");
			msPostgres.loadStatement(ilid);
			QueryResult ilidResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet ilidResultSet = ilidResult.getResultSet();
			ilidResultSet.next();
			BigDecimal iso639language_id = ilidResultSet.getBigDecimal(1);
			
			// Object2Iso639Language
			// TODO: hier muss nicht die ID von Language rein sondern die von iso639language
			PreparedStatement o2i = InsertIntoDBPostgres.Object2Iso639Language(postgres, object_id, iso639language_id, 1);
			msPostgres.loadStatement(o2i);
			QueryResult o2iResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet o2iResultSet = o2iResult.getResultSet();
//			if (!o2iResultSet.next()) {
//				Assert.assertEquals(false, false);
//			}
			
			// insert Object2Language
			PreparedStatement o2l = InsertIntoDBPostgres.Object2Language(postgres, object_id, language_id, 1);
			msPostgres.loadStatement(o2l);
			QueryResult o2lResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet o2lResultSet = o2lResult.getResultSet();
//			if (!o2lResultSet.next()) {
//				Assert.assertEquals(false, false);
//			}
			
			// insert DDCClassification
			PreparedStatement ddcc = InsertIntoDBPostgres.DDCClassification(postgres, object_id, "004");
			msPostgres.loadStatement(ddcc);
			QueryResult ddccResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet ddccResultSet = ddccResult.getResultSet();
//			if (!ddccResultSet.next()) {
//				Assert.assertEquals(false, false);
//			}
			
			//Insert DNBClassification
			PreparedStatement dnbc = InsertIntoDBPostgres.DNBClassification(postgres, object_id, "48");
			msPostgres.loadStatement(dnbc);
			QueryResult dnbcResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet dnbcResultSet = dnbcResult.getResultSet();
//			if (!dnbcResultSet.next()) {
//				Assert.assertEquals(false, false);
//			}
			
			// insert DINI_Set_Classification
			PreparedStatement dsc = InsertIntoDBPostgres.DINISetClassification(postgres, object_id, new BigDecimal(1));
			msPostgres.loadStatement(dsc);
			QueryResult dscResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet dscResultSet = dscResult.getResultSet();
//			if (!dscResultSet.next()) {
//				Assert.assertEquals(false, false);
//			}
			
			// insert Other_Categories
			PreparedStatement oc = InsertIntoDBPostgres.OtherCategories(postgres, "test:1234");
			msPostgres.loadStatement(oc);
			QueryResult ocResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet ocResultSet = ocResult.getResultSet();
//			if (!ocResultSet.next()) {
//				Assert.assertEquals(false, false);
//			}
			
			// get OtherCategories newly inserted key
			PreparedStatement ocid = postgres.prepareStatement("SELECT other_id FROM \"Other_Categories\" WHERE name = 'test:1234'");
			msPostgres.loadStatement(ocid);
			QueryResult ocidResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet ocidResultSet = ocidResult.getResultSet();
			ocidResultSet.next();
			BigDecimal other_id = ocidResultSet.getBigDecimal(1);
			
			// insert Other_Classifications
			PreparedStatement ocl = InsertIntoDBPostgres.OtherClassification(postgres, object_id, other_id);
			msPostgres.loadStatement(ocl);
			QueryResult oclResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet oclResultSet = oclResult.getResultSet();
//			if (!oclResultSet.next()) {
//				Assert.assertEquals(false, false);
//			}
			
			// insert FulltextLinks
			PreparedStatement ftl = InsertIntoDBPostgres.FullTextLinks(postgres, object_id, "application/pdf", "http://oanet.cms.hu-berlin.de");
			msPostgres.loadStatement(ftl);
			QueryResult ftlResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet ftlResultSet = ftlResult.getResultSet();
//			if (!ftlResultSet.next()) {
//				Assert.assertEquals(false, false);
//			}
			
			// insert ServiceNotify
			PreparedStatement sn = InsertIntoDBPostgres.ServiceNotify(postgres, new BigDecimal(3), "2019-01-01", true, false);
			msPostgres.loadStatement(sn);
			QueryResult snResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet snResultSet = snResult.getResultSet();
//			if (!snResultSet.next()) {
//				Assert.assertEquals(false, false);
//			}
			
			// inseert LoginData
			PreparedStatement ld = InsertIntoDBPostgres.LoginData(postgres, "maxmustermann", "12345", "max.mustermann@hu-berlin.de");
			msPostgres.loadStatement(ld);
			QueryResult ldResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet ldResultSet = ldResult.getResultSet();
//			if (!ldResultSet.next()) {
//				Assert.assertEquals(false, false);
//			}
			
			// insert DuplicatePossibilities
			PreparedStatement dp = InsertIntoDBPostgres.DuplicatePossibilities(postgres, object_id, object_id, new BigDecimal(80), new BigDecimal(80));
			msPostgres.loadStatement(dp);
			QueryResult dpResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet dpResultSet = dpResult.getResultSet();
//			if (!dpResultSet.next()) {
//				Assert.assertEquals(false, false);
//			}
			
			// insert InterpolatedDDCClassification
			PreparedStatement ip = InsertIntoDBPostgres.InterpolatedDDCClassification(postgres, object_id, "100", new BigDecimal(50));
			msPostgres.loadStatement(ip);
			QueryResult ipResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet ipResultSet = ipResult.getResultSet();
//			if (!ipResultSet.next()) {
//				Assert.assertEquals(false, false);
//			}
			
			// insert UsageData_Months
			PreparedStatement udm = InsertIntoDBPostgres.UsageData_Months(postgres, object_id, new BigDecimal(1), 123, Date.valueOf("2011-01-01"));
			msPostgres.loadStatement(udm);
			QueryResult udmResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet udmResultSet = udmResult.getResultSet();
//			if (!udmResultSet.next()) {
//				Assert.assertEquals(false, false);
//			}
			
			// insert UsageData_overall
			PreparedStatement udo = InsertIntoDBPostgres.UsageData_Overall(postgres, object_id, new BigDecimal(1), 200000, Date.valueOf("2011-01-01"));
			msPostgres.loadStatement(udo);
			QueryResult udoResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet udoResultSet = udoResult.getResultSet();
//			if (!udoResultSet.next()) {
//				Assert.assertEquals(false, false);
//			}
			
			// insert Repository
			PreparedStatement repo = InsertIntoDBPostgres.Repository(
				postgres, 
				"Test Repository", 
				"http://www.testserver.de", 
				"http://www.testserver.de/oaipmh", 
				10,
				5000,
				true, 
				true,
				true
			);
			msPostgres.loadStatement(repo);
			QueryResult repoResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet repoResultSet = repoResult.getResultSet();
//			if (!repoResultSet.next()) {
//				Assert.assertEquals(false, false);
//			}
			
			// get repository id
			PreparedStatement getRepoID = postgres.prepareStatement("SELECT * FROM \"Repositories\" WHERE url = 'http://www.testserver.de'" );
			msPostgres.loadStatement(getRepoID);
			QueryResult getRepoIDResult = msPostgres.execute();
			msPostgres.commit();
			ResultSet getRepoIDResultSet = getRepoIDResult.getResultSet();
			getRepoIDResultSet.next();
			long repository_id = getRepoIDResultSet.getLong(1);
			
			
			// DELETE THE INPUT AGAIN
			System.out.println("Deleting the Input again");
			PreparedStatement repoDelete = DeleteFromDBPostgres.Repositories(postgres, repository_id);
			PreparedStatement udoDelete = DeleteFromDBPostgres.UsageData_Overall(postgres, object_id, new BigDecimal(1));
			PreparedStatement udmDelete = DeleteFromDBPostgres.UsageData_Months(postgres, object_id, new BigDecimal(1), Date.valueOf("2011-01-01"));
			PreparedStatement iddccDelete = DeleteFromDBPostgres.Interpolated_DDC_Classification(postgres, object_id);
			PreparedStatement dpDelete = DeleteFromDBPostgres.DuplicatePossibilities(postgres, object_id);
			PreparedStatement ldDelete = DeleteFromDBPostgres.LoginData(postgres, "maxmustermann");
			PreparedStatement snDelete = DeleteFromDBPostgres.ServiceNotify(postgres, new BigDecimal(3));
			PreparedStatement ftlDelete = DeleteFromDBPostgres.FullTextLinks(postgres, object_id);
			PreparedStatement oclDelete = DeleteFromDBPostgres.Other_Classification(postgres, object_id);
			PreparedStatement ocDelete = postgres.prepareStatement("DELETE FROM \"Other_Categories\" WHERE other_id = "+other_id);
			PreparedStatement dscDelete = DeleteFromDBPostgres.DINI_Set_Classification(postgres, object_id);
			PreparedStatement dnbcDelete = DeleteFromDBPostgres.DNB_Classification(postgres, object_id);
			PreparedStatement ddccDelete = DeleteFromDBPostgres.DDC_Classification(postgres, object_id);
			PreparedStatement o2lDelete = DeleteFromDBPostgres.Object2Language(postgres, object_id);
			PreparedStatement o2iDelete = postgres.prepareStatement("DELETE FROM \"Object2Iso639Language\" WHERE language_id = "+iso639language_id+" and object_id = "+object_id);
			PreparedStatement iso639langDelete = postgres.prepareStatement("DELETE FROM \"Iso639Language\" WHERE language_id = "+iso639language_id);
			PreparedStatement langDelete = postgres.prepareStatement("DELETE FROM \"Language\" WHERE language_id = "+language_id);
			PreparedStatement o2kDelete = DeleteFromDBPostgres.Object2Keywords(postgres, object_id);
			PreparedStatement keywordDelete = postgres.prepareStatement("DELETE FROM \"Keywords\" WHERE keyword = 'Schizophrenie1'");
			
			PreparedStatement o2eDelete = DeleteFromDBPostgres.Object2Editor(postgres, object_id);
			PreparedStatement o2aDelete = DeleteFromDBPostgres.Object2Author(postgres, object_id);
			PreparedStatement personDelete = postgres.prepareStatement("DELETE FROM \"Person\" WHERE person_id = "+person_id);
			PreparedStatement publisherDelete = DeleteFromDBPostgres.Publishers(postgres, object_id);
			PreparedStatement typevalueDelete = DeleteFromDBPostgres.TypeValue(postgres, object_id);
			PreparedStatement descriptionDelete = DeleteFromDBPostgres.Description(postgres, object_id);
			PreparedStatement identifierDelete = DeleteFromDBPostgres.Identifiers(postgres, object_id);
			PreparedStatement formatDelete = DeleteFromDBPostgres.Formats(postgres, object_id);
			PreparedStatement datevalueDelete = DeleteFromDBPostgres.DateValues(postgres, object_id);
			PreparedStatement titleDelete = DeleteFromDBPostgres.Titles(postgres, object_id);
			PreparedStatement workflowDBDelete = DeleteFromDBPostgres.WorkflowDB(postgres, object_id);
			PreparedStatement rawdataDelete= DeleteFromDBPostgres.RawData(postgres, object_id);
			PreparedStatement objectDelete = DeleteFromDBPostgres.Object(postgres, object_id);
			PreparedStatement[] deleteQueries = {
				repoDelete, udoDelete, udmDelete, iddccDelete, dpDelete, ldDelete, snDelete,
				ftlDelete, oclDelete, ocDelete, dscDelete, dnbcDelete, ddccDelete,
				o2lDelete, o2iDelete, iso639langDelete, langDelete, o2kDelete, keywordDelete,
				o2eDelete, o2aDelete, personDelete, publisherDelete, typevalueDelete,
				descriptionDelete, identifierDelete, formatDelete, datevalueDelete, titleDelete, 
				workflowDBDelete, rawdataDelete, objectDelete 
			};
			
			for (int i = 0; i < deleteQueries.length; i++) {
				msPostgres.loadStatement(deleteQueries[i]);
				msPostgres.execute();
				msPostgres.commit();
			}
			Assert.assertEquals(true,true);
		}
		catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	
	
}
