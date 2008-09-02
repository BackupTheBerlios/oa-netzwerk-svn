<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<f:loadBundle basename="admin.logout" var="msg"/>

<%javax.servlet.http.HttpSession oldSession = request.getSession (false);

if (oldSession != null) {

	oldSession.invalidate ( );
}

session = request.getSession (true);
%>

<html>
	<f:view>
		<head>
			<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
			<title><h:outputText value="#{msg.logout}"/></title>
		</head>
		<body>
			<h2 align="center"><h:outputText value="#{msg.logout}"/></h2>
			<h:outputText value="#{msg.logoutText}"/><br/>
			<a href="mainAdmin.faces"><h:outputText value="#{msg.relogin}"/></a>
		</body>
	</f:view>
</html>