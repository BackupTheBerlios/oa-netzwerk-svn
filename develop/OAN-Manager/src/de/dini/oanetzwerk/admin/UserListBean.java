/**
 * 
 */
package de.dini.oanetzwerk.admin;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.myfaces.config.impl.digester.elements.Attribute;
import org.apache.tomcat.util.modeler.Registry;

import de.dini.oanetzwerk.admin.utils.AbstractBean;

/**
 * @author Sammy David
 * sammy.david@cms.hu-berlin.de
 * 
 */
@ManagedBean(name = "userList")
@RequestScoped
public class UserListBean extends AbstractBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(RepositoryListBean.class);

	private static final String ROLE_REPORT_MANAGER = "reportManager";
	private static final String ROLE_REPOSITORY_MANAGER = "repositoryManager";
	private static final String ROLE_SERVICE_MANAGER = "serviceManager";
	private static final String ROLE_USER_MANAGER = "userManager";
	private static final String ROLE_VALIDATOR_MANAGER = "validatorManager";
	private static final String ROLE_OANADMIN = "oanadmin";

	FacesContext ctx = FacesContext.getCurrentInstance();
	HttpSession session = (HttpSession) ctx.getExternalContext().getSession(false);

	private UserBean newUser = new UserBean();
	private List<UserBean> userList;

	public UserListBean() {

		super();
	}


	@PostConstruct
	public void init() {

		// fetch existing users
		fetchUsers();
		
		// check if a user id has been provided, for possible deletion
		HttpServletRequest request = (HttpServletRequest) ctx.getExternalContext().getRequest();

		String username = request.getParameter("uid");

		if (username != null
		                && (FacesContext.getCurrentInstance().getExternalContext().isUserInRole(ROLE_OANADMIN) || FacesContext
		                                .getCurrentInstance().getExternalContext().isUserInRole(ROLE_USER_MANAGER))) {

			logger.info("User-Deletion request received, deletion rights granted, performing requested user data deletion!");
			removeUser(username);
		}

	}

	
	public List<UserBean> fetchUsers() {

		System.out.println("fetching users...");
		System.out.println(FacesContext.getCurrentInstance().getExternalContext().getRemoteUser());
		userList = new ArrayList<UserBean>();

		MBeanServer mbs = Registry.getRegistry(null, null).getMBeanServer();

		try {
			ObjectName oname = new ObjectName("Users:type=UserDatabase,database=UserDatabase");

			String[] users = (String[]) mbs.getAttribute(oname, "users");

			if (users != null) {
				System.out.println(users.length);
			} else {
				System.out.println("users are null");
			}
			UserBean user = null;

			boolean manageableUser = true;

			for (String u : users) {

				System.out.println(u);
				// assume the user can be handled by us
				manageableUser = true;

				user = new UserBean();

				ObjectName userObject = new ObjectName(u);

				String username = (String) mbs.getAttribute(userObject, "username");
				user.setUsername(username);

				String fullname = (String) mbs.getAttribute(userObject, "fullName");
				user.setFullName(fullname);

				String[] roles = (String[]) mbs.getAttribute(userObject, "roles");
				for (String r : roles) {
					ObjectName roleObject = new ObjectName(r);
					String role = (String) mbs.getAttribute(roleObject, "rolename");

					if (role == null || role.length() == 0) {
						continue;
					}

					if (ROLE_VALIDATOR_MANAGER.equals(role)) {
						user.setRightValidator(true);
					} else if (ROLE_USER_MANAGER.equals(role)) {
						user.setRightUserManagement(true);
					} else if (ROLE_SERVICE_MANAGER.equals(role)) {
						user.setRightServiceManagement(true);
					} else if (ROLE_REPOSITORY_MANAGER.equals(role)) {
						user.setRightRepositoryManagement(true);
					} else if (ROLE_REPORT_MANAGER.equals(role)) {
						user.setRightReportManagement(true);
					} else if (ROLE_OANADMIN.equals(role)) {
						// TODO: add admin role
					} else {
						// unknown role, user should not be handled by us
						manageableUser = false;
					}
				}
				if (manageableUser) {
					userList.add(user);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return userList;
	}

	public String logout() {
		logger.info("logging out user...");

		((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true)).invalidate();

		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("overview.xhtml");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "logged_out";
	}

	public String addUser() {

		FacesContext context = FacesContext.getCurrentInstance();

		// validate user input
		boolean valid = newUser.validate();

		if (!valid) {
			context.validationFailed();
			return "";
		}

		MBeanServer mbs = Registry.getRegistry(null, null).getMBeanServer();

		try {
			ObjectName oname = new ObjectName("Users:type=UserDatabase,database=UserDatabase");

			String user = (String) mbs.invoke(oname, "createUser",
			                new Object[] { newUser.getUsername(), newUser.getPassword(), newUser.getFirstName() + " " + newUser.getLastName() },
			                new String[] { "java.lang.String", "java.lang.String", "java.lang.String" });

			
			mbs.invoke(oname, "save", new Object[0], new String[0]);

			ObjectName userObject = new ObjectName(user);

			if (newUser.isRightRepositoryManagement()) {

				mbs.invoke(userObject, "addRole",
				                new Object[] { ROLE_REPOSITORY_MANAGER },
				                new String[] { "java.lang.String" });
			}
			
//			mbs.invoke(userObject, "save", new Object[0], new String[0]);
			
			if (newUser.isRightServiceManagement()) {
				mbs.invoke(userObject, "addRole",
				                new Object[] { ROLE_SERVICE_MANAGER },
				                new String[] { "java.lang.String" });
			}
			
			if (newUser.isRightValidator()) {
				mbs.invoke(userObject, "addRole",
				                new Object[] { ROLE_VALIDATOR_MANAGER },
				                new String[] { "java.lang.String" });
			}
			
			if (newUser.isRightUserManagement()) {
				mbs.invoke(userObject, "addRole",
				                new Object[] { ROLE_USER_MANAGER },
				                new String[] { "java.lang.String" });
			}
			
			if (newUser.isRightReportManagement()) {
				mbs.invoke(userObject, "addRole",
				                new Object[] { ROLE_REPORT_MANAGER },
				                new String[] { "java.lang.String" });
			}
			
			mbs.invoke(oname, "save", new Object[0], new String[0]);

			context.addMessage("1", LanguageSwitcherBean.getFacesMessage(ctx, FacesMessage.SEVERITY_INFO, "users_add_success", null));
			FacesContext.getCurrentInstance().getExternalContext().redirect("users_main.xhtml");
			
		} catch (Exception e) {
			e.printStackTrace();

			context.addMessage("1", LanguageSwitcherBean.getFacesMessage(ctx, FacesMessage.SEVERITY_INFO, "users_add_failure", null));
		}
		
		return "users_main";
	}

	public String removeUser(String username) {
		FacesContext context = FacesContext.getCurrentInstance();
		if (username.equals("oanadmin")) {
			context.addMessage("1", LanguageSwitcherBean.getFacesMessage(ctx, FacesMessage.SEVERITY_INFO, "users_remove_oanadmin", null));
			return "users_main";
		}
		

		MBeanServer mbs = Registry.getRegistry(null, null).getMBeanServer();

		try {
			ObjectName oname = new ObjectName("Users:type=UserDatabase,database=UserDatabase");

			mbs.invoke(oname, "removeUser", new Object[] { username }, new String[] { "java.lang.String" });

			mbs.invoke(oname, "save", new Object[0], new String[0]);

			logger.info("User account for user '" + username + "' has been successfully deleted!");
			context.addMessage("1", LanguageSwitcherBean.getFacesMessage(ctx, FacesMessage.SEVERITY_INFO, "users_remove_success", null));
			FacesContext.getCurrentInstance().getExternalContext().redirect("users_main.xhtml");
		} catch (Exception e) {
			e.printStackTrace();

			context.addMessage("1", LanguageSwitcherBean.getFacesMessage(ctx, FacesMessage.SEVERITY_INFO, "users_remove_failure", null));
		}

		return "users_main";
	}

	public boolean isUserManager() {
		
		return  isOanAdmin() || FacesContext.getCurrentInstance().getExternalContext().isUserInRole(ROLE_USER_MANAGER);
	}
	public boolean isRepositoryManager() {
		
		return isOanAdmin() || FacesContext.getCurrentInstance().getExternalContext().isUserInRole(ROLE_REPOSITORY_MANAGER);
	}
	public boolean isReportManager() {
		
		return isOanAdmin() || FacesContext.getCurrentInstance().getExternalContext().isUserInRole(ROLE_REPORT_MANAGER);
	}
	public boolean isValidatorManager() {
		
		return isOanAdmin() || FacesContext.getCurrentInstance().getExternalContext().isUserInRole(ROLE_VALIDATOR_MANAGER);
	}
	public boolean isServiceManager() {
		
		return isOanAdmin() || FacesContext.getCurrentInstance().getExternalContext().isUserInRole(ROLE_SERVICE_MANAGER);
	}
	public boolean isOanAdmin() {
		return true;
		//return FacesContext.getCurrentInstance().getExternalContext().isUserInRole(ROLE_OANADMIN);
	}
	
	public String getUser() {
		return FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
	}
	
	public List<UserBean> getUsers() {
		return userList;
	}
	
	public UserBean getNewUser() {
		return newUser;
	}

	public void setNewUser(UserBean newUser) {
		this.newUser = newUser;
	}

}
