<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<h:form>
	<h:outputText value="#{msg.footer}"/><br/>
	<h:commandLink action="#{mainView.jump2Logout}" value="#{msg.logout}"/><br/>
</h:form>
