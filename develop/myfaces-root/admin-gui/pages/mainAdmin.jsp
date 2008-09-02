<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<f:loadBundle basename="admin.mainadmin" var="msg"/>

<html>
	<f:view>
		<head>
			<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
			<title><h:outputText value="#{msg.mainPageTitle}"/></title>
		</head>
		<body>
			<h1 align="center"><h:outputText value="#{msg.mainPageTitle}"/></h1>
			<div class="nav">
				<h:form>
					<h:commandLink action="#{mainView.jump2Repos}" value="#{msg.repositories}"/><br/>
					<h:commandLink action="#{mainView.jump2Proc}" value="#{msg.procs}"/><br/>
					<h:commandLink action="#{mainView.jump2Serv}" value="#{msg.services}"/><br/>
					<h:commandLink action="#{mainView.jump2Dat}" value="#{msg.datas}"/><br/>
					<% if (request.isUserInRole ("oasysop")) { %>
					<h:commandLink action="#{mainView.jump2Manager}" value="#{msg.manager}"/><br/>
					<% } %>
				</h:form>
			</div>
			<div class="body">
				<h:outputText value="#{msg.blindText}"/>
			</div>
			<div class="footer">
			<h:form>
				<h:outputText value="#{msg.footer}"/><br/>
				<h:commandLink action="#{mainView.jump2Logout}" value="#{msg.logout}"/><br/>
				<!--
				<a href="logout.jsp">logout</a>
				 -->
			</h:form>
			</div>
		</body>
	</f:view>
</html>
