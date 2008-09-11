<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page session="false"%>

<f:loadBundle basename="admin.index" var="index"/>

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
		<title>OAdmiN</title>
	</head>
	<body>
		<f:view>
			<h1 align="center"><h:outputText value="#{index.welcome}"/></h1>
			<h:outputText value="#{index.main}"/></br>
			<a href="pages/mainAdmin.faces"><h:outputText value="#{index.login}"/></a>
		</f:view>
	</body>
</html>
