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
	private static final String ROLE_OANADMIN = "oadmin";

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

		
//		// to remove
//		MBeanServer mbs = Registry.getRegistry(null, null).getMBeanServer();
//
//		try {
//			ObjectName oname = new ObjectName("Users:type=UserDatabase,database=UserDatabase");
//
//			String[] roles = (String[]) mbs.getAttribute(oname, "roles");
//			for (String r : roles) {
//
//				System.out.println("roles: " + r);
//			}
//
//			String[] users = (String[]) mbs.getAttribute(oname, "users");
//			for (String u : users) {
//
//				System.out.println("users: " + u);
//				ObjectName userObject = new ObjectName(u);
//				String name = (String) mbs.getAttribute(userObject, "fullName");
//
//				System.out.println("fullName: " + name + "  , ");
//
//				String[] roles2 = (String[]) mbs.getAttribute(userObject, "roles");
//				for (String r : roles2) {
//
//					System.out.println("roles: " + r);
//				}
//
//				String[] groups = (String[]) mbs.getAttribute(userObject, "groups");
//				for (String group : groups) {
//
//					System.out.println("group: " + group);
//				}
//			}
//			System.out.println("finish");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	
	public List<UserBean> fetchUsers() {

		userList = new ArrayList<UserBean>();

		MBeanServer mbs = Registry.getRegistry(null, null).getMBeanServer();

		try {
			ObjectName oname = new ObjectName("Users:type=UserDatabase,database=UserDatabase");

			String[] users = (String[]) mbs.getAttribute(oname, "users");

			UserBean user = null;

			boolean manageableUser = true;

			for (String u : users) {

				// assume the user can be handled by us
				manageableUser = true;

				System.out.println("user: " + u);
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
			System.out.println("finish");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return userList;
	}
	


//	public String getUsers2() {
//		System.out.println("bla");
//
//		if (FacesContext.getCurrentInstance().getExternalContext().getRemoteUser() != null) {
//			System.out.println("remote user: " + FacesContext.getCurrentInstance().getExternalContext().getRemoteUser());
//		}
//
//		if (FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal() != null) {
//			System.out.println(FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal().getName());
//		}
//
//		if (FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal() != null) {
//			System.out.println(FacesContext.getCurrentInstance().getExternalContext().isUserInRole("oadmin"));
//		}
//
//		// createUser();
//
//		return "test";
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
			System.out.println("a " + newUser.getRights());
			if (newUser.isRightRepositoryManagement()) {
				System.out.println("adding role");
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

//			Attribute a = new Attribute();
//			String[] roles = (String[]) mbs.getAttribute(oname, "roles");
//			for (String r : roles) {
//
//				System.out.println("roles: " + r);
//			}
//
//			String[] users = (String[]) mbs.getAttribute(oname, "users");
//			for (String u : users) {
//
//				System.out.println("users: " + u);
//			}
//			System.out.println("finish");

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
