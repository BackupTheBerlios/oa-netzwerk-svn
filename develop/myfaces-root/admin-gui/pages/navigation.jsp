<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<f:loadBundle basename="admin.mainadmin" var="msg"/>

<h:form>
	<h:commandLink action="#{mainView.jump2Repos}" value="#{msg.repositories}"/><br/>
	<h:commandLink action="#{mainView.jump2Proc}" value="#{msg.procs}"/><br/>
	<h:commandLink action="#{mainView.jump2Serv}" value="#{msg.services}"/><br/>
	<h:commandLink action="#{mainView.jump2Dat}" value="#{msg.datas}"/><br/>
	<% if (request.isUserInRole ("oasysop")) { %>
	<h:commandLink action="#{mainView.jump2Manager}" value="#{msg.manager}"/><br/>
	<% } %>
</h:form>
