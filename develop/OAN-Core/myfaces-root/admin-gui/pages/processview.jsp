<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>


<html>
	<f:view>
	<f:loadBundle basename="admin.processes" var="msg"/>
		<head>
			<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
			<title><h:outputText value="#{msg.title}"/></title>
		</head>
		<body>
			<h1 align="center"><h:outputText value="#{msg.title}"/></h1>
			<h:form>
			</h:form>
		</body>
	</f:view>
</html>

