/**
 * 
 */
package de.dini.oanetzwerk.admin;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.admin.utils.AbstractBean;


@ManagedBean(name = "user")
@SessionScoped
public class UserBean extends AbstractBean implements Serializable {

	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(UserBean.class);
	private static final SimpleDateFormat DATE_GER = new SimpleDateFormat("dd.MM.yyyy");
	
	FacesContext ctx = FacesContext.getCurrentInstance();
	HttpSession session = (HttpSession) ctx.getExternalContext().getSession(
			false);


	private Long id = null;
	private String username;
	private String password;
	private String password2;
	private String firstName;
	private String lastName;
	private String email;
	private boolean rightRepositoryManagement;
	private boolean rightServiceManagement;
	private boolean rightUserManagement;
	private boolean rightReportManagement;
	private Date lastLogin;

	public UserBean() {
		super();
		// initUserBean();
	}

	
//	@PostConstruct
//	public void init() {
//		
//		// do nothing
//	
//		if (ctx.getExternalContext().getRemoteUser() != null) {
//			System.out.println("remote user: " + FacesContext.getCurrentInstance().getExternalContext().getRemoteUser());
//		}
//			
//		if (ctx.getExternalContext().getUserPrincipal() != null) {
//			System.out.println(FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal().getName()); 
//		}
//		
//	}
	
	
	public String logout() {
		System.out.println("logging out...");
		if (FacesContext.getCurrentInstance().getExternalContext().getRemoteUser() != null) {
			System.out.println("remote user: " + FacesContext.getCurrentInstance().getExternalContext().getRemoteUser());
		}
			
		if (FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal() != null) {
			System.out.println(FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal().getName()); 
		}
		
		((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true)).invalidate();
		
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("overview.xhtml");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "logged_out";
	}
	
	public String getUser() {
		return FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getPassword2() {
		return password2;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isRightRepositoryManagement() {
		return rightRepositoryManagement;
	}

	public void setRightRepositoryManagement(boolean rightRepositoryManagement) {
		this.rightRepositoryManagement = rightRepositoryManagement;
	}

	public boolean isRightServiceManagement() {
		return rightServiceManagement;
	}

	public void setRightServiceManagement(boolean rightServiceManagement) {
		this.rightServiceManagement = rightServiceManagement;
	}

	public boolean isRightUserManagement() {
		return rightUserManagement;
	}

	public void setRightUserManagement(boolean rightUserManagement) {
		this.rightUserManagement = rightUserManagement;
	}

	public boolean isRightReportManagement() {
		return rightReportManagement;
	}

	public void setRightReportManagement(boolean rightReportManagement) {
		this.rightReportManagement = rightReportManagement;
	}

	public String getLastLogin() {
		return DATE_GER.format(lastLogin);
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getName() {
		return firstName + " " + lastName;
	}
	
	
	public String getRights() {
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(rightRepositoryManagement ? "Repositoryverwaltung, " : "");
		buffer.append(rightServiceManagement? "Diensteverwaltung, " : "");
		buffer.append(rightUserManagement ? "Benutzerverwaltung, " : "");
		buffer.append(rightReportManagement ? "Reportzugriff, " : "");
		String rights = buffer.toString();
		
		return rights.endsWith(", ") ? rights.substring(0, rights.length()-2) : rights;
	}
	
	
	public boolean registerUser()
	{
		return true;
	}
	
	// public String message() {
	// return ctx.getMessages("repositories");
	// }


}
