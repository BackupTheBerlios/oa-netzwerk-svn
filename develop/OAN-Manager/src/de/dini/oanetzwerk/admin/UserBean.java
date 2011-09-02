/**
 * 
 */
package de.dini.oanetzwerk.admin;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.admin.utils.AbstractBean;

/**
 * @author Sammy David
 * sammy.david@cms.hu-berlin.de
 * 
 */
public class UserBean extends AbstractBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(UserBean.class);
	private static final SimpleDateFormat DATE_GER = new SimpleDateFormat("dd.MM.yyyy");

	FacesContext ctx = FacesContext.getCurrentInstance();
	HttpSession session = (HttpSession) ctx.getExternalContext().getSession(false);

	private Long id = null;
	private String username;
	private String password;
	private String password2;
	private String firstName;
	private String lastName;
	private String fullName;
	private boolean rightRepositoryManagement;
	private boolean rightServiceManagement;
	private boolean rightUserManagement;
	private boolean rightValidator;
	private boolean rightReportManagement;
	private Date lastLogin;

	public UserBean() {
		super();
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

	public String getFullName() {
    	return fullName;
    }

	public void setFullName(String fullName) {
    	this.fullName = fullName;
    }

	public boolean isRightRepositoryManagement() {
		System.out.println("checking2" + rightRepositoryManagement);
		return rightRepositoryManagement;
	}

	public void setRightRepositoryManagement(boolean rightRepositoryManagement) {
		System.out.println("checking" + rightRepositoryManagement);
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

	public boolean isRightValidator() {
		return rightValidator;
	}

	public void setRightValidator(boolean rightValidator) {
		this.rightValidator = rightValidator;
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


	public String getRights() {

		StringBuffer buffer = new StringBuffer();
		buffer.append(rightRepositoryManagement ? LanguageSwitcherBean.getFacesMessage(ctx, FacesMessage.SEVERITY_INFO, "users_add_admin_repo", null).getDetail() + ", " : "");
		buffer.append(rightServiceManagement ? LanguageSwitcherBean.getFacesMessage(ctx, FacesMessage.SEVERITY_INFO, "users_add_admin_service", null).getDetail() + ", " : "");
		buffer.append(rightValidator? LanguageSwitcherBean.getFacesMessage(ctx, FacesMessage.SEVERITY_INFO, "users_add_admin_validator", null).getDetail() + ", " : "");
		buffer.append(rightUserManagement ? LanguageSwitcherBean.getFacesMessage(ctx, FacesMessage.SEVERITY_INFO, "users_add_admin_user", null).getDetail()+", " : "");
		buffer.append(rightReportManagement ? LanguageSwitcherBean.getFacesMessage(ctx, FacesMessage.SEVERITY_INFO, "users_add_admin_report", null).getDetail()+", " : "");
		String rights = buffer.toString();

		return rights.endsWith(", ") ? rights.substring(0, rights.length() - 2) : rights;
	}

	public boolean validate() {
		FacesContext context = FacesContext.getCurrentInstance();
		boolean valid = true;

		if (username == null || username.length() == 0) {
			context.addMessage("1",
			                LanguageSwitcherBean.getFacesMessage(ctx, FacesMessage.SEVERITY_INFO, "users_add_error_missingusername", null));
			valid = false;
		} else if (username.length() < 3) {
			context.addMessage("1",
			                LanguageSwitcherBean.getFacesMessage(ctx, FacesMessage.SEVERITY_INFO, "users_add_error_usernametooshort", null));
			valid = false;
		}

		if (firstName == null || firstName.length() == 0) {
			context.addMessage("1",
			                LanguageSwitcherBean.getFacesMessage(ctx, FacesMessage.SEVERITY_INFO, "users_add_error_missingfirstname", null));
			valid = false;
		}

		if (lastName == null || lastName.length() == 0) {
			context.addMessage("1",
			                LanguageSwitcherBean.getFacesMessage(ctx, FacesMessage.SEVERITY_INFO, "users_add_error_missinglastname", null));
			valid = false;
		}
		
		setFullName(firstName + " " + lastName);

		if (password == null || password.length() == 0) {
			context.addMessage("1",
			                LanguageSwitcherBean.getFacesMessage(ctx, FacesMessage.SEVERITY_INFO, "users_add_error_missingpassword", null));
			valid = false;
		} else if (password.length() < 6) {
			context.addMessage("1",
			                LanguageSwitcherBean.getFacesMessage(ctx, FacesMessage.SEVERITY_INFO, "users_add_error_passwordtooshort", null));
			valid = false;
		}

		if (password2 == null || password2.length() == 0) {
			context.addMessage("1",
			                LanguageSwitcherBean.getFacesMessage(ctx, FacesMessage.SEVERITY_INFO, "users_add_error_missingpassword2", null));
			valid = false;
		}

		if (password != null && password2 != null && !password.equals(password2)) {
			context.addMessage("1", LanguageSwitcherBean.getFacesMessage(ctx, FacesMessage.SEVERITY_INFO,
			                "users_add_error_passwordsnotequal", null));
			valid = false;
		}

		return valid;
	}

}
