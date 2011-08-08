package de.dini.oanetzwerk.validator;

import gr.uoa.di.validator.api.Entry;
import gr.uoa.di.validator.api.IJob;
import gr.uoa.di.validator.api.SgParameters;
import gr.uoa.di.validator.api.Validator;
import gr.uoa.di.validator.api.ValidatorException;
import gr.uoa.di.validator.api.standalone.APIStandalone;
import gr.uoa.di.validator.constants.FieldNames;
import gr.uoa.di.validator.jobs.JobListener;
import gr.uoa.di.validatorweb.actions.browsejobs.Job;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import de.dini.oanetzwerk.server.database.MultipleStatementConnection;
import de.dini.oanetzwerk.server.database.QueryResult;

public class ValidatorTest implements JobListener {

	// @BeforeClass
	// public static void setup() {
	//
	// try {
	// ClassLoader currentThreadClassLoader =
	// Thread.currentThread().getContextClassLoader();
	//
	// // Add the conf dir to the classpath
	// // Chain the current thread classloader
	// URLClassLoader urlClassLoader = new URLClassLoader(new URL[] { new
	// File("conf").toURL() }, currentThreadClassLoader);
	//
	// // Replace the thread classloader - assumes
	// // you have permissions to do so
	// Thread.currentThread().setContextClassLoader(urlClassLoader);
	//
	// // This should work now!
	// Thread.currentThread().getContextClassLoader().getResourceAsStream("web/WEB-INF/lib/validator-standalone.properties");
	//
	// } catch (MalformedURLException e) {
	// e.printStackTrace();
	// }
	// }

	// @Test
	public void testCreateDINI2010RuleSet() {

		try {

			Validator val = APIStandalone.getValidator();

			val.createRuleSet("DINI-Zertifikat 2010", new String[] { "1", "2" });

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

//	public static void main(String[] args) {
//		StringBuffer sb = new StringBuffer();
//
//		FileInputStream fstream;
//		try {
//			fstream = new FileInputStream("test.txt");
//			// Get the object of DataInputStream
//			DataInputStream in = new DataInputStream(fstream);
//			BufferedReader br = new BufferedReader(new InputStreamReader(in));
//			String strLine;
//			// Read File Line By Line
//			while ((strLine = br.readLine()) != null) {
//				// Print the content on the console
//				System.out.print(strLine + ", ");
//
//			}
//
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}

//	@Test
	public void testCreateDINI2010Rules() {

		String iso639_3 = "aar, abk, afr, aka, amh, ara, arg, asm, ava, ave, aym, aze, bak, bam, bel, ben, "
				+ "bih, bis, bod, bos, bre, bul, cat, ces, cha, che, chu, chv, cor, cos, cre, cym, dan, deu, "
				+ "div, dzo, ell, eng, epo, est, eus, ewe, fao, fas, fij, fin, fra, fry, ful, gla, gle, glg, "
				+ "glv, grn, guj, hat, hau, hbs, heb, her, hin, hmo, hrv, hun, hye, ibo, ido, iii, iku, ile, "
				+ "ina, ind, ipk, isl, ita, jav, jpn, kal, kan, kas, kat, kau, kaz, khm, kik, kin, kir, kom, "
				+ "kon, kor, kua, kur, lao, lat, lav, lim, lin, lit, ltz, lub, lug, mah, mal, mar, mkd, mlg, "
				+ "mlt, mon, mri, msa, mya, nau, nav, nbl, nde, ndo, nep, nld, nno, nob, nor, nya, oci, oji, "
				+ "ori, orm, oss, pan, pli, pol, por, pus, que, roh, ron, run, rus, sag, san, sin, slk, slv, "
				+ "sme, smo, sna, snd, som, sot, spa, sqi, srd, srp, ssw, sun, swa, swe, tah, tam, tat, tel, "
				+ "tgk, tgl, tha, tir, ton, tsn, tso, tuk, tur, twi, uig, ukr, urd, uzb, ven, vie, vol, wln, "
				+ "wol, xho, yid, yor, zha, zho, zul";

		iso639_3 = iso639_3.replace(", ", "\n");
		
		StringBuffer sb = new StringBuffer();
		String mimeTypes = "";
		String docTypes = "";
		String statusTypes = "";
		String downloadableMimeTypes = "";
		FileInputStream fstream;
		
		try {
			fstream = new FileInputStream("mimeTypes.txt");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			while ((strLine = br.readLine()) != null) {
				sb.append(strLine).append("\n");
			}
			mimeTypes = sb.toString(); //.substring(0, sb.toString().length() - 2);
			
			// read doc-types
			sb = new StringBuffer();
			fstream = new FileInputStream("docTypes.txt");
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));

			while ((strLine = br.readLine()) != null) {
				sb.append(strLine).append("\n");
			}
			docTypes = sb.toString(); //.substring(0, sb.toString().length() - 2);
			
			
			// read status-types
			sb = new StringBuffer();
			fstream = new FileInputStream("statusTypes.txt");
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));

			while ((strLine = br.readLine()) != null) {
				sb.append(strLine).append("\n");
			}
			statusTypes = sb.toString(); //.substring(0, sb.toString().length() - 2);
			
			
			// read downloadable mimetypes
			sb = new StringBuffer();
			fstream = new FileInputStream("downloadableMimeTypes.txt");
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));

			while ((strLine = br.readLine()) != null) {
				sb.append(strLine).append("\n");
			}
			downloadableMimeTypes = sb.toString(); //.substring(0, sb.toString().length() - 2);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {

			Validator validator = APIStandalone.getValidator();

			// verb=Identify ; email mandatory, regex

			SgParameters params = new SgParameters();
			params.addParam(FieldNames.RULE_TYPE, "Regular Expression");
			params.addParam(FieldNames.RULE_JOBTYPE, "OAI Usage Validation");
			params.addParam(FieldNames.RULE_MANDATORY, "true");
			params.addParam(FieldNames.RULE_SUCCESS, ">0");
			params.addParam(FieldNames.RULE_WEIGHT, "1");
			params.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "verb=Identify, field=adminEmail");
			params.addParam(FieldNames.RULE_NAME, "valid administrator e-mail");
			params.addParam(FieldNames.RULE_DESCRIPTION,
					"Die E-Mail Adresse des Administrators entspricht keinem gültigen Format für E-Mail-Adressen.");
			params.addParam(FieldNames.RULE_REGULAREXPRESSION_REGEXPR,
					"^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$");



			
			// mandatory repository name

			SgParameters params2 = new SgParameters();
			params2.addParam(FieldNames.RULE_TYPE, "Field Exists");
			params2.addParam(FieldNames.RULE_JOBTYPE, "OAI Usage Validation");
			params2.addParam(FieldNames.RULE_MANDATORY, "true");
			params2.addParam(FieldNames.RULE_SUCCESS, ">0");
			params2.addParam(FieldNames.RULE_WEIGHT, "1");
			params2.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "verb=Identify, field=repositoryName");
			params2.addParam(FieldNames.RULE_NAME, "mandatory field repositoryName");
			params2.addParam(FieldNames.RULE_DESCRIPTION, "Das Repository muss einen Namen haben.");
			

			
			// mandatory protocolVersion 2.0

			SgParameters params3 = new SgParameters();
			params3.addParam(FieldNames.RULE_TYPE, "Vocabulary");
			params3.addParam(FieldNames.RULE_JOBTYPE, "OAI Usage Validation");
			params3.addParam(FieldNames.RULE_MANDATORY, "true");
			params3.addParam(FieldNames.RULE_SUCCESS, ">0");
			params3.addParam(FieldNames.RULE_WEIGHT, "1");
			params3.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "verb=Identify, field=protocolVersion");
			params3.addParam(FieldNames.RULE_NAME, "oaipmh version 2.0");
			params3.addParam(FieldNames.RULE_DESCRIPTION, "Der OAI-PMH Standard in Version 2.0 sollte unterstützt werden.");
			params3.addParam(FieldNames.RULE_VOCABULARY_WORDS, "2.0");

			
			
			// datestamp format

			SgParameters params15 = new SgParameters();
			params15.addParam(FieldNames.RULE_TYPE, "Vocabulary");
			params15.addParam(FieldNames.RULE_JOBTYPE, "OAI Usage Validation");
			params15.addParam(FieldNames.RULE_MANDATORY, "true");
			params15.addParam(FieldNames.RULE_SUCCESS, "a");
			params15.addParam(FieldNames.RULE_WEIGHT, "1");
			params15.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "verb=Identify, field=granularity");
			params15.addParam(FieldNames.RULE_NAME, "datestamp format should be YYYY-MM-DD");
			params15.addParam(FieldNames.RULE_DESCRIPTION, "Das empfohlene Zeitstempelformat ist JJJJ-MM-TT.");
			params15.addParam(FieldNames.RULE_VOCABULARY_WORDS, "YYYY-MM-DD");

			// datestamp granularities must match

			SgParameters params18 = new SgParameters();
			params18.addParam(FieldNames.RULE_TYPE, "Date Granularity");
			params18.addParam(FieldNames.RULE_JOBTYPE, "OAI Usage Validation");
			params18.addParam(FieldNames.RULE_MANDATORY, "true");
			params18.addParam(FieldNames.RULE_SUCCESS, "a");
			params18.addParam(FieldNames.RULE_WEIGHT, "1");
			params18.addParam(FieldNames.RULE_NAME, "datestamp granularities must match");
			params18.addParam(FieldNames.RULE_DESCRIPTION,
					"Verwenden sie das Zeitstempelformat das unter dem Identify-Verb ausgewiesen ist.");

			
			
			// valid base url

			SgParameters params4 = new SgParameters();
			params4.addParam(FieldNames.RULE_TYPE, "Valid Url");
			params4.addParam(FieldNames.RULE_JOBTYPE, "OAI Usage Validation");
			params4.addParam(FieldNames.RULE_MANDATORY, "true");
			params4.addParam(FieldNames.RULE_SUCCESS, "a");
			params4.addParam(FieldNames.RULE_WEIGHT, "1");
			params4.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "verb=Identify, field=baseURL");
			params4.addParam(FieldNames.RULE_NAME, "valid base url");
			params4.addParam(FieldNames.RULE_DESCRIPTION, "Die Basis-URL muss eine gültige Internet-Adresse sein.");

			
			// deletion strategy

			SgParameters params5 = new SgParameters();
			params5.addParam(FieldNames.RULE_TYPE, "Vocabulary");
			params5.addParam(FieldNames.RULE_JOBTYPE, "OAI Usage Validation");
			params5.addParam(FieldNames.RULE_MANDATORY, "true");
			params5.addParam(FieldNames.RULE_SUCCESS, "a");
			params5.addParam(FieldNames.RULE_WEIGHT, "1");
			params5.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "verb=Identify, field=deletedRecord");
			params5.addParam(FieldNames.RULE_NAME, "valid deletion strategy");
			params5.addParam(FieldNames.RULE_DESCRIPTION,
					"Die Deletion-Strategie sollte den Status gelöschter Objekte mindestens einen Monat lang vorhalten. (transient oder persistent)");
			params5.addParam(FieldNames.RULE_VOCABULARY_WORDS, "transient\npersistent");

			
			// resumption token lief span 24h

			SgParameters params6 = new SgParameters();
			params6.addParam(FieldNames.RULE_TYPE, "Resumption Token Duration Check");
			params6.addParam(FieldNames.RULE_JOBTYPE, "OAI Usage Validation");
			params6.addParam(FieldNames.RULE_MANDATORY, "true");
			params6.addParam(FieldNames.RULE_SUCCESS, "a");
			params6.addParam(FieldNames.RULE_WEIGHT, "1");
			params6.addParam(FieldNames.RULE_NAME, "resumption token life span is 24h");
			params6.addParam(FieldNames.RULE_DESCRIPTION, "Die Gültigkeit von Resumption-Tokens sollte 24 Stunden betragen.");

			
			// batch size 100-500

			SgParameters params7 = new SgParameters();
			params7.addParam(FieldNames.RULE_TYPE, "Cardinality");
			params7.addParam(FieldNames.RULE_JOBTYPE, "OAI Usage Validation");
			params7.addParam(FieldNames.RULE_MANDATORY, "true");
			params7.addParam(FieldNames.RULE_SUCCESS, "a");
			params7.addParam(FieldNames.RULE_WEIGHT, "1");
			params7.addParam(FieldNames.RULE_CARDINALITY_GREATERTHAN, "99");
			params7.addParam(FieldNames.RULE_CARDINALITY_LESSTHAN, "501");
			params7.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "verb=ListRecords&metadataPrefix=oai_dc, field=record");
			params7.addParam(FieldNames.RULE_NAME, "batch size between 100-500");
			params7.addParam(FieldNames.RULE_DESCRIPTION,
					"Die Batch-Size (maximale Anzahl der Antwort-Records per Request) sollte zwischen 100 und 500 liegen.");

			
			// use of provenance element

			SgParameters params8 = new SgParameters();
			params8.addParam(FieldNames.RULE_TYPE, "Schema Validity Check");
			params8.addParam(FieldNames.RULE_JOBTYPE, "OAI Usage Validation");
			params8.addParam(FieldNames.RULE_MANDATORY, "true");
			params8.addParam(FieldNames.RULE_SUCCESS, "a");
			params8.addParam(FieldNames.RULE_WEIGHT, "1");
			params8.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "verb=ListRecords&metadataPrefix=oai_dc");
			params8.addParam(FieldNames.RULE_NAME, "use of provenance element in records' metadata");
			params8.addParam(FieldNames.RULE_DESCRIPTION,
					"Das provenance-Element sollte verwendet werden, um die Herkunft eines Dokuments anzuzeigen.");

			
			
			// support of oai_dc metadata type
			SgParameters params24 = new SgParameters();
			params24.addParam(FieldNames.RULE_TYPE, "Vocabulary");
			params24.addParam(FieldNames.RULE_JOBTYPE, "OAI Usage Validation");
			params24.addParam(FieldNames.RULE_SUCCESS, ">0");
			params24.addParam(FieldNames.RULE_WEIGHT, "1");
			params24.addParam(FieldNames.RULE_PROVIDER_INFORMATION, " verb=ListMetadataFormats, field=metadataPrefix");
			params24.addParam(FieldNames.RULE_NAME, "supports oai_dc metadata format");
			params24.addParam(FieldNames.RULE_DESCRIPTION, "Das Metadatenformat oai_dc sollte unterstützt werden.");
			params24.addParam(FieldNames.RULE_VOCABULARY_WORDS, "oai_dc");

			
			// ddc set exists
			
			SgParameters params23 = new SgParameters();
			params23.addParam(FieldNames.RULE_TYPE, "Regular Expression");
			params23.addParam(FieldNames.RULE_JOBTYPE, "OAI Usage Validation");
			params23.addParam(FieldNames.RULE_MANDATORY, "true");
			params23.addParam(FieldNames.RULE_SUCCESS, ">0");
			params23.addParam(FieldNames.RULE_WEIGHT, "1");
			params23.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "verb=ListSets, field=setSpec");
			params23.addParam(FieldNames.RULE_NAME, "at least one ddc category");
			params23.addParam(FieldNames.RULE_DESCRIPTION,
					"Es sollte eine Set-Struktur existieren, in die alle Meta­datensätze gemäß der fachlichen Zuordnung (DDC) der " +
					"dazugehörigen Dokumente eingeordnet sind. (Beispielformat 'ddc:004')");
			params23.addParam(FieldNames.RULE_REGULAREXPRESSION_REGEXPR, "ddc:[0-9]{3}.[0-9]{1}||ddc:[0-9]{3}");
			
			
			// at least one doc-type
			
			SgParameters params20 = new SgParameters();
			params20.addParam(FieldNames.RULE_TYPE, "Vocabulary");
			params20.addParam(FieldNames.RULE_JOBTYPE, "OAI Usage Validation");
			params20.addParam(FieldNames.RULE_MANDATORY, "true");
			params20.addParam(FieldNames.RULE_SUCCESS, ">0");
			params20.addParam(FieldNames.RULE_WEIGHT, "1");
			params20.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "verb=ListSets, field=setSpec");
			params20.addParam(FieldNames.RULE_NAME, "at least one document type");
			params20.addParam(FieldNames.RULE_DESCRIPTION,
					"Es sollte wenigstens ein setSpec-Feld Auskunft darüber geben um was für einen Dokumenttyp es sich handelt. (Beispielformat 'doc-type:masterThesis')");
			params20.addParam(FieldNames.RULE_VOCABULARY_WORDS, docTypes);
			
			
			// at least one status-type
			
			SgParameters params21 = new SgParameters();
			params21.addParam(FieldNames.RULE_TYPE, "Vocabulary");
			params21.addParam(FieldNames.RULE_JOBTYPE, "OAI Usage Validation");
			params21.addParam(FieldNames.RULE_MANDATORY, "true");
			params21.addParam(FieldNames.RULE_SUCCESS, ">0");
			params21.addParam(FieldNames.RULE_WEIGHT, "1");
			params21.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "verb=ListSets, field=setSpec");
			params21.addParam(FieldNames.RULE_NAME, "at least one status type");
			params21.addParam(FieldNames.RULE_DESCRIPTION,
					"Es sollte wenigstens ein setSpec-Feld Auskunft darüber geben um welche Dokumentversion es sich handelt. (Beispielformat 'status-type:publishedVersion')");
			params21.addParam(FieldNames.RULE_VOCABULARY_WORDS, statusTypes);
			
			
			// open_access set
			
			SgParameters params22 = new SgParameters();
			params22.addParam(FieldNames.RULE_TYPE, "Vocabulary");
			params22.addParam(FieldNames.RULE_JOBTYPE, "OAI Usage Validation");
			params22.addParam(FieldNames.RULE_MANDATORY, "true");
			params22.addParam(FieldNames.RULE_SUCCESS, ">0");
			params22.addParam(FieldNames.RULE_WEIGHT, "1");
			params22.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "verb=ListSets, field=setSpec");
			params22.addParam(FieldNames.RULE_NAME, "open_access set");
			params22.addParam(FieldNames.RULE_DESCRIPTION,
					"Es sollte ein 'open_access'-Set geben, dem alle Open-Access Publikationen zugeordnet sind.");
			params22.addParam(FieldNames.RULE_VOCABULARY_WORDS, "open_access");
			
			
			///
			/// Content Validation Rules
			///
			
			
			// mandatory fields oai_dc

			SgParameters params9 = new SgParameters();
			params9.addParam(FieldNames.RULE_TYPE, "Field Exists");
			params9.addParam(FieldNames.RULE_JOBTYPE, "OAI Content Validation");
			params9.addParam(FieldNames.RULE_MANDATORY, "true");
			params9.addParam(FieldNames.RULE_SUCCESS, "a");
			params9.addParam(FieldNames.RULE_WEIGHT, "3");
			params9.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "dc:identifier");
			params9.addParam(FieldNames.RULE_NAME, "mandatory dc:identifier");
			params9.addParam(FieldNames.RULE_DESCRIPTION, "Das Feld dc:identifier ist erforderlich.");

			SgParameters params10 = new SgParameters();
			params10.addParam(FieldNames.RULE_TYPE, "Field Exists");
			params10.addParam(FieldNames.RULE_JOBTYPE, "OAI Content Validation");
			params10.addParam(FieldNames.RULE_MANDATORY, "true");
			params10.addParam(FieldNames.RULE_SUCCESS, "a");
			params10.addParam(FieldNames.RULE_WEIGHT, "4");
			params10.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "dc:title");
			params10.addParam(FieldNames.RULE_NAME, "mandatory dc:title");
			params10.addParam(FieldNames.RULE_DESCRIPTION, "Das Feld dc:title ist erforderlich.");

			SgParameters params11 = new SgParameters();
			params11.addParam(FieldNames.RULE_TYPE, "Field Exists");
			params11.addParam(FieldNames.RULE_JOBTYPE, "OAI Content Validation");
			params11.addParam(FieldNames.RULE_MANDATORY, "true");
			params11.addParam(FieldNames.RULE_SUCCESS, "a");
			params11.addParam(FieldNames.RULE_WEIGHT, "3");
			params11.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "dc:date");
			params11.addParam(FieldNames.RULE_NAME, "mandatory dc:date");
			params11.addParam(FieldNames.RULE_DESCRIPTION, "Das Feld dc:date ist erforderlich.");

			SgParameters params12 = new SgParameters();
			params12.addParam(FieldNames.RULE_TYPE, "Field Exists");
			params12.addParam(FieldNames.RULE_JOBTYPE, "OAI Content Validation");
			params12.addParam(FieldNames.RULE_MANDATORY, "true");
			params12.addParam(FieldNames.RULE_SUCCESS, "a");
			params12.addParam(FieldNames.RULE_WEIGHT, "3");
			params12.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "dc:creator");
			params12.addParam(FieldNames.RULE_NAME, "mandatory dc:creator");
			params12.addParam(FieldNames.RULE_DESCRIPTION, "Das Feld dc:creator ist erforderlich.");

			SgParameters params13 = new SgParameters();
			params13.addParam(FieldNames.RULE_TYPE, "Field Exists");
			params13.addParam(FieldNames.RULE_JOBTYPE, "OAI Content Validation");
			params13.addParam(FieldNames.RULE_MANDATORY, "true");
			params13.addParam(FieldNames.RULE_SUCCESS, "a");
			params13.addParam(FieldNames.RULE_WEIGHT, "3");
			params13.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "dc:type");
			params13.addParam(FieldNames.RULE_NAME, "mandatory dc:type");
			params13.addParam(FieldNames.RULE_DESCRIPTION, "Das Feld dc:type ist erforderlich.");

			// dc:language should be ISO639-3

			SgParameters params14 = new SgParameters();
			params14.addParam(FieldNames.RULE_TYPE, "Vocabulary");
			params14.addParam(FieldNames.RULE_JOBTYPE, "OAI Content Validation");
			params14.addParam(FieldNames.RULE_MANDATORY, "true");
			params14.addParam(FieldNames.RULE_SUCCESS, "a");
			params14.addParam(FieldNames.RULE_WEIGHT, "3");
			params14.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "dc:language");
			params14.addParam(FieldNames.RULE_NAME, "dc:language should be ISO 639-3");
			params14.addParam(FieldNames.RULE_DESCRIPTION, "Das Feld dc:language sollte nur Sprachcodes im ISO 639-3 Format enthalten.");
			params14.addParam(FieldNames.RULE_VOCABULARY_WORDS, iso639_3);

			
			// dc:identifier muss downloadbare Ressource enthalten

//			SgParameters params16 = new SgParameters();
//			params16.addParam(FieldNames.RULE_TYPE, "Retrievable Resource");
//			params16.addParam(FieldNames.RULE_JOBTYPE, "OAI Content Validation");
//			params16.addParam(FieldNames.RULE_MANDATORY, "true");
//			params16.addParam(FieldNames.RULE_SUCCESS, ">0");
//			params16.addParam(FieldNames.RULE_WEIGHT, "1");
//			params16.addParam(FieldNames.RULE_RETRIEVERESOURCE_MAXDEPTH, "1");
//			params16.addParam(FieldNames.RULE_RETRIEVERESOURCE_MIMETYPES, downloadableMimeTypes);			
//			params16.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "dc:identifier");
//			params16.addParam(FieldNames.RULE_NAME, "one dc:identifier should contain downloadable resource");
//			params16.addParam(FieldNames.RULE_DESCRIPTION,
//					"Mindestens ein dc:identifier Feld muss eine URL zu einer herunterladbaren Ressource führen.");

			
			// dc:format should contain standard mime types

			SgParameters params17 = new SgParameters();
			params17.addParam(FieldNames.RULE_TYPE, "Vocabulary");
			params17.addParam(FieldNames.RULE_JOBTYPE, "OAI Content Validation");
			params17.addParam(FieldNames.RULE_MANDATORY, "true");
			params17.addParam(FieldNames.RULE_SUCCESS, "a");
			params17.addParam(FieldNames.RULE_WEIGHT, "3");
			params17.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "dc:format");
			params17.addParam(FieldNames.RULE_NAME, "dc:format should contain standard mime types");
			params17.addParam(FieldNames.RULE_DESCRIPTION, "Das Feld dc:format sollte Standard MIME-Typen enthalten.");
			params17.addParam(FieldNames.RULE_VOCABULARY_WORDS, mimeTypes);

			
			
			// at least one ddc-category
			
			SgParameters params19 = new SgParameters();
			params19.addParam(FieldNames.RULE_TYPE, "Regular Expression");
			params19.addParam(FieldNames.RULE_JOBTYPE, "OAI Content Validation");
			params19.addParam(FieldNames.RULE_MANDATORY, "true");
			params19.addParam(FieldNames.RULE_SUCCESS, ">0");
			params19.addParam(FieldNames.RULE_WEIGHT, "1");
			params19.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "verb=ListRecords&metadataPrefix=oai_dc, field=setSpec");
			params19.addParam(FieldNames.RULE_NAME, "at least one ddc category");
			params19.addParam(FieldNames.RULE_DESCRIPTION,
					"Es sollte ein setSpec-Feld geben das Auskunft über die DDC-Kategorie gibt derer das Dokument zugehörig ist. (Beispielformat 'ddc:004')");
			params19.addParam(FieldNames.RULE_REGULAREXPRESSION_REGEXPR, "ddc:[0-9]{3}.[0-9]{1}||ddc:[0-9]{3}");



			
			
			// add new rules to DB
			validator.addNewRule(params);
			validator.addNewRule(params2);
			validator.addNewRule(params3);
			validator.addNewRule(params4);
			validator.addNewRule(params5);
			validator.addNewRule(params6);
			validator.addNewRule(params7);
			validator.addNewRule(params8);
			validator.addNewRule(params24);
			validator.addNewRule(params15);
			validator.addNewRule(params18);			
			validator.addNewRule(params20);
			validator.addNewRule(params21);
			validator.addNewRule(params22);
			validator.addNewRule(params23);
			
			
			validator.addNewRule(params9);
			validator.addNewRule(params10);
			validator.addNewRule(params11);
			validator.addNewRule(params12);
			validator.addNewRule(params13);
			validator.addNewRule(params14);
//			validator.addNewRule(params16);
			validator.addNewRule(params17);
			validator.addNewRule(params19);

			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testNewJob() {

		boolean done = false;
		String baseUrl = "http://edoc.hu-berlin.de/OAI-2.0";
		
		SgParameters params = new SgParameters();
		
		params.addParam(FieldNames.JOB_GENERAL_USER, "sdavid");
		params.addParam(FieldNames.JOB_GENERAL_TYPE, "OAI Usage Validation");
		params.addParam(FieldNames.JOB_OAIUSAGE_BASEURL, baseUrl);
		
		// rule definition
		List<Integer> ruleIds = new ArrayList<Integer>();
		for (int i = 170; i < 183; i++) {
			ruleIds.add(i);
		}
		
		
		///
		///
		///
		
		
		SgParameters params2 = new SgParameters();
		
		// general job definition
		params2.addParam(FieldNames.JOB_GENERAL_USER, "sdavid");
		params2.addParam(FieldNames.JOB_GENERAL_TYPE, "OAI Content Validation");
		params2.addParam(FieldNames.JOB_OAICONTENT_BASEURL, baseUrl);
		params2.addParam(FieldNames.JOB_OAICONTENT_RANDOM, "false");
		params2.addParam(FieldNames.JOB_OAICONTENT_RECORDS, "30");

		// rule definition
		List<Integer> ruleIds2 = new ArrayList<Integer>();
		for (int i = 183; i < 192; i++) {
			ruleIds2.add(i);
		}

		try {

			Validator val = APIStandalone.getValidator();

//			IJob job = val.addNewJob(params, ruleIds);
//			job.addListener(this);
//			val.startJob(job);
			
			IJob job2 = val.addNewJob(params2, ruleIds2);
			job2.addListener(this);
			val.startJob(job2);
			
			done = true;
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		synchronized (this) {

			try {
				this.wait(10000000); // just for simple junit testing purpose,
										// keeping the application alive
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		Assert.assertEquals(true, done);
	}

	// @Test
	public void newOaiPMHJob() {

		SgParameters params = new SgParameters();
		params.addParam(FieldNames.JOB_GENERAL_USER, "testuser");
		params.addParam(FieldNames.JOB_GENERAL_TYPE, "OAI Usage Validation");
		params.addParam(FieldNames.JOB_OAIUSAGE_BASEURL, "http://ubm.opus.hbz-nrw.de/phpoai/oai2.php ");

		// rule definition
		List<Integer> ruleIds = new ArrayList<Integer>();
		for (int i = 20; i < 32; i++) {
			ruleIds.add(i);
		}

		ruleIds.add(34);

		try {

			Validator val = APIStandalone.getValidator();

			IJob job = val.addNewJob(params, ruleIds);
			job.addListener(this);
			val.startJob(job);

		} catch (Exception e) {
			e.printStackTrace();
		}
		synchronized (this) {

			try {
				this.wait(10000000); // just for simple junit testing purpose,
										// keeping the application alive
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// @Test
	public void newJob() {

		SgParameters params = new SgParameters();
		params.addParam(FieldNames.JOB_GENERAL_USER, "testuser");
		params.addParam(FieldNames.JOB_GENERAL_TYPE, "OAI Usage Validation");
		params.addParam(FieldNames.JOB_OAIUSAGE_BASEURL, "http://ubm.opus.hbz-nrw.de/phpoai/oai2.php ");

		// rule definition
		List<Integer> ruleIds = new ArrayList<Integer>();
		// for (int i = 1; i < 24; i++) {
		// ruleIds.add(i);
		// }

		ruleIds.add(24); // all the checks fail with exception, see below

		try {

			Validator val = APIStandalone.getValidator();

			IJob job = val.addNewJob(params, ruleIds);
			job.addListener(this);
			val.startJob(job);

		} catch (Exception e) {
			e.printStackTrace();
		}
		synchronized (this) {

			try {
				this.wait(10000000); // just for simple junit testing purpose,
										// keeping the application alive
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// @Test
	public void getJobSummary() {

		try {
			Validator val = APIStandalone.getValidator();
			List<Entry> entries = val.getJobSummary("387");

			if (entries == null) {
				System.out.println("No summary available!");
			} else {
				System.out.println("Summary received!");

				for (Entry entry : entries) {

					System.out.println("Rule-ID: " + entry.getRuleId());
					System.out.println("Name: " + entry.getName());
					System.out.println("Description: " + entry.getDescription());
					System.out.println("Weight: " + entry.getWeight());
					System.out.println("#Successes: " + entry.getSuccesses());

					for (String error : entry.getErrors()) {
						System.out.println("err: " + error);
					}

					System.out.println("\n\n");

				}

			}

		} catch (ValidatorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// @Test
	public void getJobs() {

		try {
			Validator val = APIStandalone.getValidator();
			List<Job> jobs = val.getJobsOfUser("testuser");

			if (jobs == null) {
				System.out.println("No jobs available!");
			} else {
				System.out.println("jobs received!");

				for (Job job : jobs) {

					System.out.println("Job-ID: " + job.getId());
					System.out.println("Dauer: " + job.getDuration());
					System.out.println("Score: " + job.getScore());
					System.out.println("Status: " + job.getStatus());
					System.out.println("Typ: " + job.getType());

					System.out.println("\n\n");

				}

			}

		} catch (ValidatorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// @Test
	public void testOANOAIPMH() {

		SgParameters params = new SgParameters();

		params.addParam(FieldNames.JOB_GENERAL_USER, "testuser");
		params.addParam(FieldNames.JOB_GENERAL_TYPE, "OAI Content Validation");
		params.addParam(FieldNames.JOB_OAICONTENT_BASEURL, "http://oanet.cms.hu-berlin.de/oaipmh/oaipmh");
		params.addParam(FieldNames.JOB_OAICONTENT_RANDOM, "false");
		params.addParam(FieldNames.JOB_OAICONTENT_RECORDS, "100");
		// params.addParam(FieldNames.JOB_OAICONTENT_SET, "DRIVER");

		// params.addParam(FieldNames.JOB_GENERAL_USER, "user");
		// params.addParam(FieldNames.JOB_GENERAL_TYPE, "OAI Usage Validation");
		// params.addParam(FieldNames.JOB_OAIUSAGE_BASEURL,
		// "http://oanet.cms.hu-berlin.de/oaipmh/oaipmh");

		List<Integer> ruleIds = new ArrayList<Integer>();
		for (int i = 1; i < 20; i++) {
			ruleIds.add(i);
		}

		ruleIds.add(32);
		ruleIds.add(33);

		boolean done = false;
		try {

			Validator val = APIStandalone.getValidator();

			IJob job = val.addNewJob(params, ruleIds);
			job.addListener(this);
			val.startJob(job);
			done = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		synchronized (this) {

			try {
				this.wait(10000000); // just for simple junit testing purpose,
										// keeping the application alive
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		Assert.assertEquals(true, done);
	}

	// @Test
	public void connectToMySQL() {

		Connection conn;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/oan_validator";

			Properties props = new Properties();
			props.put("user", "oan_user");
			props.put("password", "oan123");
			props.put("charSet", "UTF-8");
			conn = DriverManager.getConnection(url, props);

			MultipleStatementConnection stmtconn = new MultipleStatementConnection(conn);

			// System.out.println("Leere Tabelle "+methodName+" vor dem neu befüllen");
			String sql = "SELECT count(*) FROM jobs";
			PreparedStatement statement = conn.prepareStatement(sql);
			stmtconn.loadStatement(statement);
			QueryResult qs = stmtconn.execute();

			stmtconn.commit();

			ResultSet rs = qs.getResultSet();
			rs.next();
			System.out.println("Result: " + rs.getInt(1));

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	@Override
	public void done(int arg0, double arg1) {

		System.out.println("done");
	}

	@Override
	public void failed(int arg0, Exception arg1) {

		System.out.println("failed");
	}
}
