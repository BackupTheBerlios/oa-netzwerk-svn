/**
 * 
 */
package de.dini.oanetzwerk.admin;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.admin.utils.AbstractBean;


@ManagedBean(name="repoValidator")
@RequestScoped 
public class RepositoryValidatorBean extends AbstractBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(RepositoryValidatorBean.class);
	private RepositoryValidator rv;
	
	/**
	 * URL zur REST-Schnittstelle eines Repositoriums
	 * Wird nur verwendet, wenn ein Repositorium getestet werden soll, das nicht in der server.xml beschrieben wird (Fremdtest oder Test mit anderem User)
	 * @default ""
	 */
	private String serverToValidate = "";
	
	/**
	 * Textuelle Umschreibung eines Repositoriums.
	 * Beispiel: REST-Schnittstelle des Repositoriums der HU Berlin
	 * Wird zur Auswahl eines Repositoriums aus der server.xml verwendet, wenn keine URL angegeben wird.
	 * Wird eine URL angegeben ist der Servername optional um das Log schöner zu machen.
	 * @default Servername-Wert aus der server.xml
	 */
	private String serverName = "";
	
	/**
	 * Benutzername für Authentifizierung an einem Repositorium
	 * Wird nur benötigt, wenn ein Repositorium manuell getestet werden soll.
	 * @default ""
	 */
	private String username = "";
	
	/**
	 * Passwort für Authentifizierung an einem Repositorium
	 * Wird nur benötigt, wenn ein Repositorium manuell getestet werden soll.
	 * @default ""
	 */
	private String password = "";
	
	/**
	 * Querystring auf für die Validierung eines Repositoriums
	 * Ist der Parameter nicht leer, so wird bei Auswahl eines Repositoriums aus der Select-Box das gewählte Repositorium mit diesem Query-String getestet.
	 * Ist eine URL vorhanden, wird das damit angegebene Repositorium mit diesem Query-String getestet.
	 * @default ""
	 */
	private String parameter = ""; 			// zu testender Parameter, optional
	private String parameter2 = "";
	
	/**
	 * Nummer des Testdatensatzes in der validationResults.xml
	 * Dieser Offset kann dazu verwendet werden ein bestimmtes Testset in der validationResults.xml noch einmal laufen zu lassen.
	 * @default 0
	 */
	private int testOffset = 0;		
	
	/**
	 * Nummer des Einzeltests in einem Testdatensatz
	 * Dieser Offset definiert einen Einzeltest in einem Testset und wird dazu verwendet um einzelne Tests aus einem Testset erneut laufen zu lassen.
	 * @default 0
	 */
	private int subTestOffset = 0;
	
	
	FacesContext ctx = FacesContext.getCurrentInstance();
	HttpSession session = (HttpSession) ctx.getExternalContext().getSession(false);
	
	private RepositoryBean repo;

	public RepositoryValidatorBean() {

		super();
		rv = new RepositoryValidator();

		HttpServletRequest request = (HttpServletRequest) ctx.getExternalContext().getRequest();
		String testOffset = request.getParameter("testOffset");
		String subTestOffset = request.getParameter("subTestOffset");
		String delete = request.getParameter("delete");
		
		System.out.println("Test Offset: "+testOffset);
		if (testOffset != null && !testOffset.isEmpty()) {
			System.out.println("Parameter gefunden");
			this.testOffset = Integer.parseInt(testOffset);
			System.out.println("TestOffset: "+this.testOffset);
			if (subTestOffset != null && !subTestOffset.isEmpty()) {
				// zwingend notwendig, da das XPath Prädikat bei 1 losgeht und man die 0 hier gut als Markierung verwenden kann dass alle Teiltests durchlaufen werden sollen
				// Leider ist die t:dataList Komponente zu dämlich den Laufindex bei 1 starten zu lassen, daher die Verwirrung hier
				this.subTestOffset = Integer.parseInt(subTestOffset) + 1; 	
			}
			else {
				this.subTestOffset = 0;
			}
			if (delete != null && !delete.isEmpty() && Integer.parseInt(delete) == 1) {
				System.out.println("Lösche Testset Nr. "+this.testOffset);
				rv.deleteTest(this.testOffset);
			}
			else {
				rv.rerunTest(this.testOffset, this.subTestOffset);
			}
		}
		
	}


	public String validateFromForm() {
		System.err.println("Test Fall 1");	
		/*
		 * Fall 1: Nur Repository aus Selectbox gewählt, restliche Felder leer (heißt keine URL und kein Parameter)
		 * In diesem Fall wird das gewählte Repository mit allen in der server.xml stehenden Tests durchgetestet.
		 */
		if (
			!this.serverName.isEmpty() && 
			this.serverToValidate.isEmpty() &&
			this.parameter.isEmpty()
		){
			System.err.println("Test Fall 1 - aktiv");
			rv.validateServer(this.serverName);
		}
		
		/*
		 * Fall 2: Repository aus Selectbox gewählt und noch zusätzlicher Parameter, aber keine URL
		 * In diesem Fall wird das gewählte Repository ausschließlich mit dem zusätzlichen Parameter getestet
		 */
		else if (
			!this.serverName.isEmpty() && 
			this.serverToValidate.isEmpty() && 
			!this.parameter.isEmpty()
		) {
			System.err.println("Test Fall 2 - aktiv");
			rv.validateServer(this.serverName, this.parameter);
		}
		
		/*
		 * Fall 3: URL wurde eingegeben, Parameter wurde eingegeben
		 * In diesem Fall wird der durch URL angegebene Server mit dem Parameter und den (optionalen) Authentifizierungsinformationen getestet.
		 */
		else if (
			!this.serverToValidate.isEmpty() &&
			!this.parameter2.isEmpty()
		) {
			System.err.println("Test Fall 3 - aktiv");
			rv.validateServer(this.serverToValidate, this.serverToValidate, this.username, this.password, this.parameter2);
		}
		return "keks";
	}
	
	


	public Boolean validateRepository() {
		 //return rv.validateServer("HU Berlin Restschnittstelle");
		return true;
	}
	
	public List<String> getServers() {
		List<String> serverList = Arrays.asList(rv.getServerList());
		//System.out.println(serverList.toString());
		return serverList;
	}
	
	public ValidationResultBean[] getResults() {
		ValidationResultBean[] data = rv.getResults(0);
		/*
		for (int i = 0; i < data.length; i++) {
			System.out.println("Timestamp = "+data[i].getTimestamp());
			System.out.println("Serveralias = "+data[i].getServerAlias());
			for (int j = 0; j < data[i].getValidationResults().length; j++) {
				System.out.println("	Validation Result = "+data[i].getValidationResults()[j]);
				System.out.println("	Parameter = "+data[i].getParameters()[j]);
			}
		}
		*/
		return data;
	}
	
	public String getServerToValidate() {
		return serverToValidate;
	}


	public void setServerToValidate(String serverToValidate) {
		this.serverToValidate = serverToValidate;
	}


	public String getServerName() {
		return serverName;
	}


	public void setServerName(String serverName) {
		this.serverName = serverName;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getParameter() {
		return parameter;
	}


	public void setParameter(String parameter) {
		this.parameter = parameter;
	}


	public String getParameter2() {
		return parameter2;
	}


	public void setParameter2(String parameter2) {
		this.parameter2 = parameter2;
	}
	
}