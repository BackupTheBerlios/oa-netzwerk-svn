package de.dini.oanetzwerk.admin;

import java.io.Serializable;

import org.apache.log4j.Logger;

public class LoginBean implements Serializable{

	  private static Logger LOG = Logger.getLogger(LoginBean.class);

	public LoginBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	  
	public String login() {
		
		LOG.info("crazy stuff");
		System.out.println("login method executed!");
		return "logged_in";
	}

}
