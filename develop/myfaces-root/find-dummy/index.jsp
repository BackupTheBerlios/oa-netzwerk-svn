<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page session="false"%>

<f:loadBundle basename="finddummy.index" var="index"/>

<html>
	<f:view>
		<head>
			<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
			<title><h:outputText value="#{index.title}"/></title>
		</head>
		<body>
			<h:form>
				<h:inputText/>
				<h:commandButton value="#{index.find}" action="#{find.find}"/>
			</h:form>
		</body>
	</f:view>
</html>
