
package de.dini.oanetzwerk.validator;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.validator.utils.AbstractBean;

@ManagedBean(name = "valiList")
@SessionScoped 
public class ValidationListBean extends AbstractBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ValidationListBean.class);
	
	FacesContext ctx = FacesContext.getCurrentInstance();
	HttpSession session = (HttpSession) ctx.getExternalContext().getSession(false);
	
	private ValidationBean vali;

	public ValidationListBean(){
		super();
	}
	
	public List<ValidationBean> getValidations(){
		String result = this.prepareRestTransmission("Validation/").GetData();
		List<ValidationBean> valiList = new ArrayList<ValidationBean>();
		RestMessage rms = RestXmlCodec.decodeRestMessage(result);
		
		if( rms == null || rms.getListEntrySets().isEmpty()){
			logger.error("received no Repository Details at all from the server");
			return null;
		}
		
		for (RestEntrySet res : rms.getListEntrySets()) {

			Iterator<String> it = res.getKeyIterator();
			String key = "";
			ValidationBean repo = new ValidationBean();

			while (it.hasNext()) {

				key = it.next();

				if (key.equalsIgnoreCase("oai_url")) {

					repo.setOaiUrl(res.getValue(key));

				} else if (key.equalsIgnoreCase("date")) {

					repo.setDate(res.getValue(key));

				} else if (key.equalsIgnoreCase("state")) {

					repo.setState(res.getValue(key));

//				} else if (key.equalsIgnoreCase("rule")) {

//					repo.setRule(res.getValue(key));

//				} else if (key.equalsIgnoreCase("rstate")) {

//					repo.setRState(res.getValue(key));

//				} else if (key.equalsIgnoreCase("detail")) {

//					repo.setDetail(res.getValue(key));

				} else
					continue;
			}

			valiList.add(repo);
			
		}
		System.out.println(valiList.size());
		
		if (logger.isDebugEnabled())
			logger.debug("DBAccessNG Instance will be prepared!");

		try {
			DataSource datasource = (DataSource) ((Context) new InitialContext().lookup("java:comp/env")).lookup("jdbc/oanetztest");
			Connection connection = datasource.getConnection();
			Connection conn2 = connection;
			System.out.println(conn2);
			
		} catch (NamingException ex) {
			logger.error(ex.getLocalizedMessage(), ex);
		} catch (SQLException ex) {
			
		}
		
		return valiList;
	}

}
