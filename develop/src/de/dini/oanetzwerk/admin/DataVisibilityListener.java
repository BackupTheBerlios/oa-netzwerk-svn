/**
 * 
 */
package de.dini.oanetzwerk.admin;

import java.util.LinkedList;
import java.util.List;

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.servlet.http.HttpSession;

/**
 * @author Michael K&uuml;hn
 *
 */

public class DataVisibilityListener implements ActionListener {
	
	private static boolean showAll = false;
	
	/**
	 * @see javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent)
	 */
	
	public void processAction (ActionEvent arg0) throws AbortProcessingException {
		
		ApplicationFactory factory = (ApplicationFactory) FactoryFinder.getFactory (FactoryFinder.APPLICATION_FACTORY);
		Application application = factory.getApplication ( );
		FacesContext context = FacesContext.getCurrentInstance ( );
		HttpSession session = (HttpSession) context.getExternalContext ( ).getSession (false);
		
		if (session.getAttribute ("OIDItem") != null)
			return;
		
		toggleFields ((Long) session.getAttribute ("OIDItem"));
	}
	
	/**
	 * @param showAll2
	 */
	
	private void toggleFields (Long oid) {
		
		FacesContext context = FacesContext.getCurrentInstance ( );
		
		UIComponent formComponent = null;
		UIComponent tableComponent = null;
		UIComponent coldetailcomponent = null;
		UIComponent nodetailcomponent = null;
		UIComponent detailcomponent = null;
		List <UIComponent> compList = new LinkedList <UIComponent> ( );
		Long id = new Long (0);
		
		formComponent = context.getViewRoot ( ).findComponent ("dataform");
		tableComponent = formComponent.findComponent ("datatable");
		coldetailcomponent = tableComponent.findComponent ("coldetail");
		compList = tableComponent.getChildren ( );
		
		for (UIComponent uicomp : compList) {
			
			if (uicomp.getId ( ).matches ("[0-9]+")) {
				
				id = new Long (uicomp.getId ( ));
				break;
			}
		}
		
		nodetailcomponent = coldetailcomponent.findComponent ("nondetailform");
		detailcomponent = coldetailcomponent.findComponent ("detailform");
		
//		if (id.equals (oid)) {
			
			nodetailcomponent.setRendered (false);
			detailcomponent.setRendered (true);
			
//		} else {
			
//			nodetailcomponent.setRendered (true);
//			detailcomponent.setRendered (false);
//		}
			
	}
}
