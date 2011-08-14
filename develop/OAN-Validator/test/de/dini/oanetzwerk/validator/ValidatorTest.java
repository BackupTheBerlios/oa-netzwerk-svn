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
import gr.uoa.di.validatorweb.actions.rulesets.Rule;
import gr.uoa.di.validatorweb.actions.rulesets.RuleSet;

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
	public void getDiniRules() {
		
		String diniRuleSetName = "DINI 2010";
		
		try {
			Validator validator = APIStandalone.getValidator();
			List<RuleSet> rulesets = validator.getAllRuleSets();
			
			RuleSet diniRuleSet = null;
			for (RuleSet ruleset : rulesets) {
				if (diniRuleSetName.equals(ruleset.getName())) {
					diniRuleSet = ruleset;
				}
			}
			
			List<String> diniRuleIds = validator.getRuleSetRuleIds(diniRuleSet.getId());			
			List<SgParameters> rules = new ArrayList<SgParameters>();

			for (String rule : diniRuleIds) {

				System.out.println(rule);
				rules.add(validator.getRule(rule));
			}
			System.out.println("start");
			System.out.println(FieldNames.RULE_NAME);
			System.out.println(FieldNames.RULE_DESCRIPTION);
		
		} catch (ValidatorException e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
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
				sb.append(strLine).append(",");
			}
			downloadableMimeTypes = sb.toString().substring(0, sb.toString().length() - 1);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {

			Validator validator = APIStandalone.getValidator();


			
			// mandatory protocolVersion 2.0

			SgParameters params1 = new SgParameters();
			params1.addParam(FieldNames.RULE_TYPE, "Vocabulary");
			params1.addParam(FieldNames.RULE_JOBTYPE, "OAI Usage Validation");
			params1.addParam(FieldNames.RULE_MANDATORY, "true");
			params1.addParam(FieldNames.RULE_SUCCESS, ">0");
			params1.addParam(FieldNames.RULE_WEIGHT, "1");
			params1.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "verb=Identify, field=protocolVersion");
			params1.addParam(FieldNames.RULE_NAME, "OAI-PMH Spezifikation in Version 2.0 (erforderlich)");
			params1.addParam(FieldNames.RULE_DESCRIPTION, "Die OAI-Schnittstelle verhält sich konform gemäß der Protokoll­spezifikation in der Version 2.0. Die Antwort auf die OAI-ANfrage Identify liefert eine abweichende Angabe. (siehe M.A.1-1)");
			params1.addParam(FieldNames.RULE_VOCABULARY_WORDS, "2.0");


			//incremental harvesting supported
			SgParameters params4 = new SgParameters();
			params4.addParam(FieldNames.RULE_TYPE, "Incremental Record Delivery");
			params4.addParam(FieldNames.RULE_JOBTYPE, "OAI Usage Validation");
			params4.addParam(FieldNames.RULE_MANDATORY, "true");
			params4.addParam(FieldNames.RULE_SUCCESS, "a");
			params4.addParam(FieldNames.RULE_WEIGHT, "1");
			params4.addParam(FieldNames.RULE_NAME, "Inkrementelles Harvesting (erforderlich)");
			params4.addParam(FieldNames.RULE_DESCRIPTION, "Die OAI-Schnittstelle unterstützt das inkrementelle Harvesting in korrekter Form. (siehe M.A.1-4)");
			
			
			// verb=Identify ; email mandatory, regex

			SgParameters params8 = new SgParameters();
			params8.addParam(FieldNames.RULE_TYPE, "Regular Expression");
			params8.addParam(FieldNames.RULE_JOBTYPE, "OAI Usage Validation");
			params8.addParam(FieldNames.RULE_MANDATORY, "true");
			params8.addParam(FieldNames.RULE_SUCCESS, ">0");
			params8.addParam(FieldNames.RULE_WEIGHT, "1");
			params8.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "verb=Identify, field=adminEmail");
			params8.addParam(FieldNames.RULE_NAME, "Gültige E-Mail-Adresse des Administrators (empfohlen)"); // Gültige E-Mail Adresse
			params8.addParam(FieldNames.RULE_DESCRIPTION,
					"Die E-Mail Adresse des Administrators entspricht keinem gültigen Format für E-Mail-Adressen. (siehe E.A.1-3)");
			params8.addParam(FieldNames.RULE_REGULAREXPRESSION_REGEXPR,
					"^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$");

			// mandatory repository name

			SgParameters params9 = new SgParameters();
			params9.addParam(FieldNames.RULE_TYPE, "Field Exists");
			params9.addParam(FieldNames.RULE_JOBTYPE, "OAI Usage Validation");
			params9.addParam(FieldNames.RULE_MANDATORY, "true");
			params9.addParam(FieldNames.RULE_SUCCESS, ">0");
			params9.addParam(FieldNames.RULE_WEIGHT, "1");
			params9.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "verb=Identify, field=repositoryName");
			params9.addParam(FieldNames.RULE_NAME, "Name des Repository  (empfohlen)");
			params9.addParam(FieldNames.RULE_DESCRIPTION, "Die Antwort auf die OAI-Anfrage Identify liefert keinen Namen des Dienstes/Repository. (siehe E.A.1-3)");
			

			// mandatory repository name

			SgParameters params10 = new SgParameters();
			params10.addParam(FieldNames.RULE_TYPE, "Field Exists");
			params10.addParam(FieldNames.RULE_JOBTYPE, "OAI Usage Validation");
			params10.addParam(FieldNames.RULE_MANDATORY, "true");
			params10.addParam(FieldNames.RULE_SUCCESS, ">0");
			params10.addParam(FieldNames.RULE_WEIGHT, "1");
			params10.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "verb=Identify, field=description");
			params10.addParam(FieldNames.RULE_NAME, "Beschreibung des Repository  (empfohlen)");
			params10.addParam(FieldNames.RULE_DESCRIPTION, "Die Antwort auf die OAI-Anfrage Identify liefert keine Beschreibung zum Repository. (siehe E.A.1-3)");
			
			
			
			
			// use of provenance element

			SgParameters params11 = new SgParameters();
			params11.addParam(FieldNames.RULE_TYPE, "Field Exists");
			params11.addParam(FieldNames.RULE_JOBTYPE, "OAI Content Validation");
			params11.addParam(FieldNames.RULE_MANDATORY, "false");
			params11.addParam(FieldNames.RULE_SUCCESS, ">0");
			params11.addParam(FieldNames.RULE_WEIGHT, "1");
			params11.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "provenance");
			params11.addParam(FieldNames.RULE_NAME, "Provenance-Element (empfohlen)");
			params11.addParam(FieldNames.RULE_DESCRIPTION,
					"Das provenance-Element sollte verwendet werden, um die Herkunft eines Dokuments anzuzeigen. (siehe E.A.1-4");
			
			
			// datestamp format

			SgParameters params12 = new SgParameters();
			params12.addParam(FieldNames.RULE_TYPE, "Vocabulary");
			params12.addParam(FieldNames.RULE_JOBTYPE, "OAI Usage Validation");
			params12.addParam(FieldNames.RULE_MANDATORY, "true");
			params12.addParam(FieldNames.RULE_SUCCESS, "a");
			params12.addParam(FieldNames.RULE_WEIGHT, "1");
			params12.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "verb=Identify, field=granularity");
			params12.addParam(FieldNames.RULE_NAME, "Datumsformat YYYY-MM-DD (erforderlich)");
			params12.addParam(FieldNames.RULE_DESCRIPTION, "Das erforderlich Datumsformat ist YYYY-MM-DD, z.B. 2010-12-31 (siehe M.A.3-8)");
			params12.addParam(FieldNames.RULE_VOCABULARY_WORDS, "YYYY-MM-DD");

			
			// valid base url

//			SgParameters params6 = new SgParameters();
//			params6.addParam(FieldNames.RULE_TYPE, "Valid Url");
//			params6.addParam(FieldNames.RULE_JOBTYPE, "OAI Usage Validation");
//			params6.addParam(FieldNames.RULE_MANDATORY, "true");
//			params6.addParam(FieldNames.RULE_SUCCESS, "a");
//			params6.addParam(FieldNames.RULE_WEIGHT, "1");
//			params6.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "verb=Identify, field=baseURL");
//			params6.addParam(FieldNames.RULE_NAME, "valid base url");
//			params6.addParam(FieldNames.RULE_DESCRIPTION, "Die Basis-URL muss eine gültige Internet-Adresse sein.");

			// open_access set
			
			SgParameters params13 = new SgParameters();
			params13.addParam(FieldNames.RULE_TYPE, "Vocabulary");
			params13.addParam(FieldNames.RULE_JOBTYPE, "OAI Usage Validation");
			params13.addParam(FieldNames.RULE_MANDATORY, "true");
			params13.addParam(FieldNames.RULE_SUCCESS, ">0");
			params13.addParam(FieldNames.RULE_WEIGHT, "1");
			params13.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "verb=ListSets, field=setSpec");
			params13.addParam(FieldNames.RULE_NAME, "Set \"open_access\" (erforderlich)");
			params13.addParam(FieldNames.RULE_DESCRIPTION,
					"Es sollte ein 'open_access'-Set geben, dem alle Open-Access Publikationen zugeordnet sind. M.A.2-1");
			params13.addParam(FieldNames.RULE_VOCABULARY_WORDS, "open_access");
			
			
			// ddc set exists
			
			SgParameters params14 = new SgParameters();
			params14.addParam(FieldNames.RULE_TYPE, "Regular Expression");
			params14.addParam(FieldNames.RULE_JOBTYPE, "OAI Usage Validation");
			params14.addParam(FieldNames.RULE_MANDATORY, "true");
			params14.addParam(FieldNames.RULE_SUCCESS, ">0");
			params14.addParam(FieldNames.RULE_WEIGHT, "1");
			params14.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "verb=ListSets, field=setSpec");
			params14.addParam(FieldNames.RULE_NAME, "Set-Struktur nach DDC-Kategorien (erforderlich)");
			params14.addParam(FieldNames.RULE_DESCRIPTION,
					"Es sollte eine Set-Struktur existieren, in die alle Meta­datensätze gemäß der fachlichen Zuordnung (DDC) der " +
					"dazugehörigen Dokumente eingeordnet sind. (Beispielformat 'ddc:004') siehe M.A.2-2");
			params14.addParam(FieldNames.RULE_REGULAREXPRESSION_REGEXPR, "ddc:[0-9]{3}.[0-9]{1}||ddc:[0-9]{3}");
			
			
			// at least one doc-type
			
			SgParameters params15 = new SgParameters();
			params15.addParam(FieldNames.RULE_TYPE, "Vocabulary");
			params15.addParam(FieldNames.RULE_JOBTYPE, "OAI Usage Validation");
			params15.addParam(FieldNames.RULE_MANDATORY, "true");
			params15.addParam(FieldNames.RULE_SUCCESS, ">0");
			params15.addParam(FieldNames.RULE_WEIGHT, "1");
			params15.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "verb=ListSets, field=setSpec");
			params15.addParam(FieldNames.RULE_NAME, "Set-Struktur nach Dokumenttypen (erforderlich)");
			params15.addParam(FieldNames.RULE_DESCRIPTION,
					"Es sollte wenigstens ein setSpec-Feld Auskunft darüber geben um was für einen Dokumenttyp es sich handelt. (Beispielformat 'doc-type:masterThesis') siehe M.A.2-3");
			params15.addParam(FieldNames.RULE_VOCABULARY_WORDS, docTypes);
			

			
			// deletion strategy

			SgParameters params16 = new SgParameters();
			params16.addParam(FieldNames.RULE_TYPE, "Vocabulary");
			params16.addParam(FieldNames.RULE_JOBTYPE, "OAI Usage Validation");
			params16.addParam(FieldNames.RULE_MANDATORY, "true");
			params16.addParam(FieldNames.RULE_SUCCESS, "a");
			params16.addParam(FieldNames.RULE_WEIGHT, "1");
			params16.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "verb=Identify, field=deletedRecord");
			params16.addParam(FieldNames.RULE_NAME, "Deletion-Strategie (erforderlich)");
			params16.addParam(FieldNames.RULE_DESCRIPTION,
					"Die Deletion-Strategie sollte den Status gelöschter Objekte mindestens einen Monat lang vorhalten. (transient oder persistent) siehe M.A.2-4");
			params16.addParam(FieldNames.RULE_VOCABULARY_WORDS, "transient\npersistent");

			
			
			// at least one status-type
			
			SgParameters params17 = new SgParameters();
			params17.addParam(FieldNames.RULE_TYPE, "Vocabulary");
			params17.addParam(FieldNames.RULE_JOBTYPE, "OAI Usage Validation");
			params17.addParam(FieldNames.RULE_MANDATORY, "true");
			params17.addParam(FieldNames.RULE_SUCCESS, ">0");
			params17.addParam(FieldNames.RULE_WEIGHT, "1");
			params17.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "verb=ListSets, field=setSpec");
			params17.addParam(FieldNames.RULE_NAME, "Set-Struktur nach Dokumentstatus (empfohlen)");
			params17.addParam(FieldNames.RULE_DESCRIPTION,
					"Es sollte wenigstens ein setSpec-Feld Auskunft darüber geben um welche Dokumentversion es sich handelt. (Beispielformat 'status-type:publishedVersion') siehe E.A.2-1");
			params17.addParam(FieldNames.RULE_VOCABULARY_WORDS, statusTypes);			

			// batch size 100-500

			SgParameters params18 = new SgParameters();
			params18.addParam(FieldNames.RULE_TYPE, "Cardinality");
			params18.addParam(FieldNames.RULE_JOBTYPE, "OAI Usage Validation");
			params18.addParam(FieldNames.RULE_MANDATORY, "true");
			params18.addParam(FieldNames.RULE_SUCCESS, "a");
			params18.addParam(FieldNames.RULE_WEIGHT, "1");
			params18.addParam(FieldNames.RULE_CARDINALITY_GREATERTHAN, "99");
			params18.addParam(FieldNames.RULE_CARDINALITY_LESSTHAN, "501");
			params18.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "verb=ListRecords&metadataPrefix=oai_dc, field=record");
			params18.addParam(FieldNames.RULE_NAME, "Batch-Größe zwischen 100 und 500 (empfohlen)");
			params18.addParam(FieldNames.RULE_DESCRIPTION,
					"Die Batch-Size (maximale Anzahl der Antwort-Records per Request) sollte zwischen 100 und 500 liegen. (siehe E.A.2-2)");

			
			// resumption token lief span 24h

			SgParameters params19 = new SgParameters();
			params19.addParam(FieldNames.RULE_TYPE, "Resumption Token Duration Check");
			params19.addParam(FieldNames.RULE_JOBTYPE, "OAI Usage Validation");
			params19.addParam(FieldNames.RULE_MANDATORY, "true");
			params19.addParam(FieldNames.RULE_SUCCESS, "a");
			params19.addParam(FieldNames.RULE_WEIGHT, "1");
			params19.addParam(FieldNames.RULE_NAME, "24h-Gültigkeit der Resumption Tokens (empfohlen)");
			params19.addParam(FieldNames.RULE_DESCRIPTION, "Die Gültigkeit von Resumption-Tokens sollte 24 Stunden betragen. (siehe E.A.2-3)");

			
			
			// support of oai_dc metadata type
			SgParameters params20 = new SgParameters();
			params20.addParam(FieldNames.RULE_TYPE, "Vocabulary");
			params20.addParam(FieldNames.RULE_JOBTYPE, "OAI Usage Validation");
			params20.addParam(FieldNames.RULE_MANDATORY, "true");
			params20.addParam(FieldNames.RULE_SUCCESS, ">0");
			params20.addParam(FieldNames.RULE_WEIGHT, "1");
			params20.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "verb=ListMetadataFormats, field=metadataPrefix");
			params20.addParam(FieldNames.RULE_NAME, "Metadatenformat oai_dc (erforderlich)");
			params20.addParam(FieldNames.RULE_DESCRIPTION, "Das Metadatenformat oai_dc sollte unterstützt werden.");
			params20.addParam(FieldNames.RULE_VOCABULARY_WORDS, "oai_dc");

			
			
			///
			/// Content Validation Rules
			///
			
			
			// mandatory fields oai_dc


			SgParameters params30 = new SgParameters();
			params30.addParam(FieldNames.RULE_TYPE, "Field Exists");
			params30.addParam(FieldNames.RULE_JOBTYPE, "OAI Content Validation");
			params30.addParam(FieldNames.RULE_MANDATORY, "true");
			params30.addParam(FieldNames.RULE_SUCCESS, "a");
			params30.addParam(FieldNames.RULE_WEIGHT, "3");
			params30.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "dc:creator");
			params30.addParam(FieldNames.RULE_NAME, "dc:creator Element (erforderlich)");
			params30.addParam(FieldNames.RULE_DESCRIPTION, "Das DC-Element dc:creator ist erforderlich. (siehe M.A.3-1)");
			
			SgParameters params31 = new SgParameters();
			params31.addParam(FieldNames.RULE_TYPE, "Field Exists");
			params31.addParam(FieldNames.RULE_JOBTYPE, "OAI Content Validation");
			params31.addParam(FieldNames.RULE_MANDATORY, "true");
			params31.addParam(FieldNames.RULE_SUCCESS, "a");
			params31.addParam(FieldNames.RULE_WEIGHT, "4");
			params31.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "dc:title");
			params31.addParam(FieldNames.RULE_NAME, "dc:title Element (erforderlich)");
			params31.addParam(FieldNames.RULE_DESCRIPTION, "Das DC-Element dc:title ist erforderlich. (siehe M.A.3-1)");
			
			SgParameters params32 = new SgParameters();
			params32.addParam(FieldNames.RULE_TYPE, "Field Exists");
			params32.addParam(FieldNames.RULE_JOBTYPE, "OAI Content Validation");
			params32.addParam(FieldNames.RULE_MANDATORY, "true");
			params32.addParam(FieldNames.RULE_SUCCESS, "a");
			params32.addParam(FieldNames.RULE_WEIGHT, "3");
			params32.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "dc:date");
			params32.addParam(FieldNames.RULE_NAME, "dc:date Element (erforderlich)");
			params32.addParam(FieldNames.RULE_DESCRIPTION, "Das DC-Element dc:date ist erforderlich. (siehe M.A.3-1)");
			
			SgParameters params33 = new SgParameters();
			params33.addParam(FieldNames.RULE_TYPE, "Field Exists");
			params33.addParam(FieldNames.RULE_JOBTYPE, "OAI Content Validation");
			params33.addParam(FieldNames.RULE_MANDATORY, "true");
			params33.addParam(FieldNames.RULE_SUCCESS, "a");
			params33.addParam(FieldNames.RULE_WEIGHT, "3");
			params33.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "dc:type");
			params33.addParam(FieldNames.RULE_NAME, "dc:type Element (erforderlich)");
			params33.addParam(FieldNames.RULE_DESCRIPTION, "Das DC-Element dc:type ist erforderlich. (siehe M.A.3-1)");

			SgParameters params34 = new SgParameters();
			params34.addParam(FieldNames.RULE_TYPE, "Field Exists");
			params34.addParam(FieldNames.RULE_JOBTYPE, "OAI Content Validation");
			params34.addParam(FieldNames.RULE_MANDATORY, "true");
			params34.addParam(FieldNames.RULE_SUCCESS, "a");
			params34.addParam(FieldNames.RULE_WEIGHT, "3");
			params34.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "dc:identifier");
			params34.addParam(FieldNames.RULE_NAME, "dc:identifier Element (erforderlich)");
			params34.addParam(FieldNames.RULE_DESCRIPTION, "Das DC-Element dc:identifier ist erforderlich. (siehe M.A.3-1)");

			
			// dc:identifier muss downloadbare Ressource enthalten

//			SgParameters params36 = new SgParameters();
//			params36.addParam(FieldNames.RULE_TYPE, "Retrievable Resource");
//			params36.addParam(FieldNames.RULE_JOBTYPE, "OAI Content Validation");
//			params36.addParam(FieldNames.RULE_MANDATORY, "true");
//			params36.addParam(FieldNames.RULE_SUCCESS, ">0");
//			params36.addParam(FieldNames.RULE_WEIGHT, "1");
//			params36.addParam(FieldNames.RULE_RETRIEVERESOURCE_MAXDEPTH, "0");
//			params36.addParam(FieldNames.RULE_RETRIEVERESOURCE_MIMETYPES, downloadableMimeTypes);			
//			params36.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "dc:identifier");
//			params36.addParam(FieldNames.RULE_NAME, "one dc:identifier should contain downloadable resource");
//			params36.addParam(FieldNames.RULE_DESCRIPTION,
//					"Für jeden Datensatz wird in mindestens einem identifier-Element eine operable URL auf der Basis eines Persistent Identifiers angegeben, die unmittelbar auf den Volltext des entsprechenden Dokuments führt. (siehe M.A.3-3)");


			// at least one doc-type
			
			SgParameters params38 = new SgParameters();
			params38.addParam(FieldNames.RULE_TYPE, "Vocabulary");
			params38.addParam(FieldNames.RULE_JOBTYPE, "OAI Content Validation");
			params38.addParam(FieldNames.RULE_MANDATORY, "true");
			params38.addParam(FieldNames.RULE_SUCCESS, ">0");
			params38.addParam(FieldNames.RULE_WEIGHT, "1");
			params38.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "dc:type");
			params38.addParam(FieldNames.RULE_NAME, "Mindestens eine Angabe zum Dokumenttyp (erforderlich)");
			params38.addParam(FieldNames.RULE_DESCRIPTION,
					"Allen Dokumenten sind Dokument- bzw. Publikationstypen gemäß den Vorgaben aus den DINI-Empfehlungen \"Gemeinsames Vokabular für Publikations- und Dokumenttypen\" in je eigenen type-Elementen zugewiesen. (Beispielformat 'doc-type:masterThesis'; siehe M.A.3-5) ");
			params38.addParam(FieldNames.RULE_VOCABULARY_WORDS, docTypes);

			
			// at least one ddc-category
			
			SgParameters params39 = new SgParameters();
			params39.addParam(FieldNames.RULE_TYPE, "Regular Expression");
			params39.addParam(FieldNames.RULE_JOBTYPE, "OAI Content Validation");
			params39.addParam(FieldNames.RULE_MANDATORY, "true");
			params39.addParam(FieldNames.RULE_SUCCESS, ">0");
			params39.addParam(FieldNames.RULE_WEIGHT, "1");
			params39.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "dc:subject");
			params39.addParam(FieldNames.RULE_NAME, "Mindestens eine Angabe zur DDC-Kategorie (erforderlich)");
			params39.addParam(FieldNames.RULE_DESCRIPTION,
					"Für jeden Datensatz wird in mindestens einem subject-Element eine DNB-Sachgruppe angegeben, in die das beschriebene Dokument eingeordnet ist. (Beispielformat 'ddc:004'; siehe M.A.3-6)");
			params39.addParam(FieldNames.RULE_REGULAREXPRESSION_REGEXPR, "ddc:[0-9]{3}.[0-9]{1}||ddc:[0-9]{3}");

			
			
			// dc:language should be ISO639-3

			SgParameters params40 = new SgParameters();
			params40.addParam(FieldNames.RULE_TYPE, "Vocabulary");
			params40.addParam(FieldNames.RULE_JOBTYPE, "OAI Content Validation");
			params40.addParam(FieldNames.RULE_MANDATORY, "true");
			params40.addParam(FieldNames.RULE_SUCCESS, "a");
			params40.addParam(FieldNames.RULE_WEIGHT, "3");
			params40.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "dc:language");
			params40.addParam(FieldNames.RULE_NAME, "dc:language nach dem Standard ISO 639-3 (erforderlich)");
			params40.addParam(FieldNames.RULE_DESCRIPTION, "Der Inhalt des Elements language wird gemäß der ISO-Norm 639-3 angegeben. (siehe M.A.3-7)");
			params40.addParam(FieldNames.RULE_VOCABULARY_WORDS, iso639_3);

			
			// dc:date should be in format YYYY-MM-DD
			SgParameters params41 = new SgParameters();
			params41.addParam(FieldNames.RULE_TYPE, "Regular Expression");
			params41.addParam(FieldNames.RULE_JOBTYPE, "OAI Content Validation");
			params41.addParam(FieldNames.RULE_MANDATORY, "true");
			params41.addParam(FieldNames.RULE_SUCCESS, "a");
			params41.addParam(FieldNames.RULE_WEIGHT, "1");
			params41.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "dc:date");
			params41.addParam(FieldNames.RULE_NAME, "Record-Datumsformat YYYY-MM-DD (erforderlich)");
			params41.addParam(FieldNames.RULE_DESCRIPTION,
					"Der Inhalt des Elements date wird gemäß der ISO-Norm 8601 angegeben. Das entspricht der Form YYYY-MM-DD. (siehe M.A.3-8)");
			params41.addParam(FieldNames.RULE_REGULAREXPRESSION_REGEXPR, "[0-9]{4}-[0-9]{2}-[0-9]{2}");

			
			// Recommendations
			
			SgParameters params43 = new SgParameters();
			params43.addParam(FieldNames.RULE_TYPE, "Field Exists");
			params43.addParam(FieldNames.RULE_JOBTYPE, "OAI Content Validation");
			params43.addParam(FieldNames.RULE_MANDATORY, "true");
			params43.addParam(FieldNames.RULE_SUCCESS, "a");
			params43.addParam(FieldNames.RULE_WEIGHT, "1");
			params43.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "dc:contributor");
			params43.addParam(FieldNames.RULE_NAME, "dc:contributor Element (empfohlen)");
			params43.addParam(FieldNames.RULE_DESCRIPTION, "Das Element contributor wird verwendet und enthält (pro Vorkommen) den Namen einer an der Erstellung des beschriebenen Dokuments beteiligten Person oder Institution. (Das kann beispielsweise der Gutachter einer Dissertation oder der Herausgeber eines Sammelbandes sein. siehe E.A.3-2");
			
			
			SgParameters params44 = new SgParameters();
			params44.addParam(FieldNames.RULE_TYPE, "Field Exists");
			params44.addParam(FieldNames.RULE_JOBTYPE, "OAI Content Validation");
			params44.addParam(FieldNames.RULE_MANDATORY, "true");
			params44.addParam(FieldNames.RULE_SUCCESS, "a");
			params44.addParam(FieldNames.RULE_WEIGHT, "1");
			params44.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "dc:source");
			params44.addParam(FieldNames.RULE_NAME, "dc:source Element (empfohlen)");
			params44.addParam(FieldNames.RULE_DESCRIPTION, "Für das Element source werden die Vorgaben der \"Guidelines for Encoding Bibliographic Citation Information in Dublin Core Metadata\" berücksichtigt. (Das Element dient zur Nennung einer Vorlage für die elektronische Version (Zitatangabe). Siehe http://dublincore.org/documents/dc-citation-guidelines/. siehe E.A.3-3");
			
			
			SgParameters params45 = new SgParameters();
			params45.addParam(FieldNames.RULE_TYPE, "Field Exists");
			params45.addParam(FieldNames.RULE_JOBTYPE, "OAI Content Validation");
			params45.addParam(FieldNames.RULE_MANDATORY, "true");
			params45.addParam(FieldNames.RULE_SUCCESS, "a");
			params45.addParam(FieldNames.RULE_WEIGHT, "1");
			params45.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "dc:relation");
			params45.addParam(FieldNames.RULE_NAME, "dc:relation Element (empfohlen)");
			params45.addParam(FieldNames.RULE_DESCRIPTION, "Das Element relation wird für die Nennung von Objekten verwendet, die mit dem beschriebenen Dokument in einer Beziehung stehen. (Derartige Beziehungen sind beispielsweise hierarchische Zugehörigkeit (isPartOf) oder Aktualisierungen (isVersionOf).) siehe E.A.3-4");
			
			SgParameters params46 = new SgParameters();
			params46.addParam(FieldNames.RULE_TYPE, "Field Exists");
			params46.addParam(FieldNames.RULE_JOBTYPE, "OAI Content Validation");
			params46.addParam(FieldNames.RULE_MANDATORY, "true");
			params46.addParam(FieldNames.RULE_SUCCESS, "a");
			params46.addParam(FieldNames.RULE_WEIGHT, "1");
			params46.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "dc:subject");
			params46.addParam(FieldNames.RULE_NAME, "dc:subject Element (empfohlen)");
			params46.addParam(FieldNames.RULE_DESCRIPTION, "Das Element subject wird für Angaben über das Thema des beschriebenen Dokuments verwendet. (Üblicherweise wird das Thema durch Stichwörter, Schlagwörter oder Notationen aus Klassifikationssystemen beschrieben.) siehe E.A.3-5");
						
			SgParameters params47 = new SgParameters();
			params47.addParam(FieldNames.RULE_TYPE, "Cardinality");
			params47.addParam(FieldNames.RULE_JOBTYPE, "OAI Content Validation");
			params47.addParam(FieldNames.RULE_MANDATORY, "true");
			params47.addParam(FieldNames.RULE_SUCCESS, "a");
			params47.addParam(FieldNames.RULE_WEIGHT, "1");
			params47.addParam(FieldNames.RULE_CARDINALITY_GREATERTHAN, "0");
			params47.addParam(FieldNames.RULE_CARDINALITY_LESSTHAN, "2");			
			params47.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "dc:date");
			params47.addParam(FieldNames.RULE_NAME, "dc:date genau einmal (empfohlen)");
			params47.addParam(FieldNames.RULE_DESCRIPTION, "Das Element date wird pro Metadatensatz nur einmalig angegeben. (Dabei ist das Publikationsdatum gegenüber anderen Daten – etwa dem Einstell- oder Erzeugungsdatum – zu bevorzugen, da es für den Endnutzer die größte Bedeutung hat.) siehe E.A.3-6");
						
			
			
			// dc:format should contain standard mime types

			SgParameters params48 = new SgParameters();
			params48.addParam(FieldNames.RULE_TYPE, "Vocabulary");
			params48.addParam(FieldNames.RULE_JOBTYPE, "OAI Content Validation");
			params48.addParam(FieldNames.RULE_MANDATORY, "true");
			params48.addParam(FieldNames.RULE_SUCCESS, "a");
			params48.addParam(FieldNames.RULE_WEIGHT, "3");
			params48.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "dc:format");
			params48.addParam(FieldNames.RULE_NAME, "dc:format enthält Standard MIME-Typen (empfohlen)");
			params48.addParam(FieldNames.RULE_DESCRIPTION, "Das Feld dc:format sollte Standard MIME-Typen enthalten.");
			params48.addParam(FieldNames.RULE_VOCABULARY_WORDS, mimeTypes);

			
			
			

			List<String> ruleIds = new ArrayList<String>();
			
			// add new rules to DB
			ruleIds.add(validator.addNewRule(params1));
			ruleIds.add(validator.addNewRule(params4));
			ruleIds.add(validator.addNewRule(params8));
			ruleIds.add(validator.addNewRule(params9));
			ruleIds.add(validator.addNewRule(params10));
			ruleIds.add(validator.addNewRule(params12));
			ruleIds.add(validator.addNewRule(params13));
			ruleIds.add(validator.addNewRule(params14));
			ruleIds.add(validator.addNewRule(params15));
			ruleIds.add(validator.addNewRule(params16));
			ruleIds.add(validator.addNewRule(params17));
			ruleIds.add(validator.addNewRule(params18));
			ruleIds.add(validator.addNewRule(params19));
			ruleIds.add(validator.addNewRule(params20));
			
			
			ruleIds.add(validator.addNewRule(params31));
			ruleIds.add(validator.addNewRule(params32));
			ruleIds.add(validator.addNewRule(params33));
			ruleIds.add(validator.addNewRule(params34));
//			ruleIds.add(validator.addNewRule(params36));
			ruleIds.add(validator.addNewRule(params38));
			ruleIds.add(validator.addNewRule(params39));
			ruleIds.add(validator.addNewRule(params40));
			ruleIds.add(validator.addNewRule(params41));
			ruleIds.add(validator.addNewRule(params43));
			ruleIds.add(validator.addNewRule(params44));
			ruleIds.add(validator.addNewRule(params45));
			ruleIds.add(validator.addNewRule(params46));
			ruleIds.add(validator.addNewRule(params47));
			ruleIds.add(validator.addNewRule(params48));
			ruleIds.add(validator.addNewRule(params11));	
			
			String[] ruleids = new String[ruleIds.size()];

			int i = 1;
			for (String string : ruleIds) {
				System.out.println(string);
				ruleids[i-1] = Integer.toString(i++);
			}

			
			validator.createRuleSet("DINI 2010", ruleids);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

//	@Test
	public void testNewJob() {

		boolean done = false;
		String baseUrl = "http://edoc.hu-berlin.de/OAI-2.0";
		
		SgParameters params = new SgParameters();
		
		params.addParam(FieldNames.JOB_GENERAL_USER, "sdavid");
		params.addParam(FieldNames.JOB_GENERAL_RULESET, "DINI 2010");
		params.addParam(FieldNames.JOB_GENERAL_TYPE, "OAI Usage Validation");
		params.addParam(FieldNames.JOB_OAIUSAGE_BASEURL, baseUrl);
		
		// rule definition
		List<Integer> ruleIds = new ArrayList<Integer>();
		for (int i = 1; i < 15; i++) {
			ruleIds.add(i);
		}
		
		
		///
		///
		///
		
		
		SgParameters params2 = new SgParameters();
		
		// general job definition
		params2.addParam(FieldNames.JOB_GENERAL_USER, "sdavid");
		params2.addParam(FieldNames.JOB_GENERAL_TYPE, "OAI Content Validation");
		params2.addParam(FieldNames.JOB_GENERAL_RULESET, "DINI 2010");
		params2.addParam(FieldNames.JOB_OAICONTENT_BASEURL, baseUrl);
		params2.addParam(FieldNames.JOB_OAICONTENT_RANDOM, "false");
		params2.addParam(FieldNames.JOB_OAICONTENT_RECORDS, "100");

		// rule definition
		List<Integer> ruleIds2 = new ArrayList<Integer>();
		for (int i = 15; i < 30; i++) {
			ruleIds2.add(i);
		}

		try {

			Validator val = APIStandalone.getValidator();

			IJob job = val.addNewJob(params, ruleIds);
			job.addListener(this);
			val.startJob(job);
			
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
