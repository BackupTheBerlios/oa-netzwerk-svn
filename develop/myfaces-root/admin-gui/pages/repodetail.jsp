<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<f:loadBundle basename="admin.repositories" var="msg"/>

<f:view>
	<html>
		<head>
			<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
			<title><h:outputText value="#{msg.title}"/></title>
			<link rel="stylesheet" href="../css/repodetail.css" type="text/css"/>
		</head>
		<body>
			<ul style="list-style-type:none">
				<li style="float:left"><h:outputText value="#{repoView.detail.id}"/>&nbsp;</li>
				<li style="float:left"><h:outputText value="#{repoView.detail.name}"/>&nbsp;</li>
				<li style="float:left"><h:outputText value="#{repoView.detail.url}"/>&nbsp;</li>
				<li style="float:left"><h:outputText value="#{repoView.detail.oai_url}"/>&nbsp;</li>
				<li style="float:left"><h:outputText value="#{repoView.detail.test_data}"/>&nbsp;</li>
				<li style="float:left"><h:outputText value="#{repoView.detail.harvest_pause}"/>&nbsp;</li>
				<li style="float:left"><h:outputText value="#{repoView.detail.harvest_amount}"/>&nbsp;</li>
			</ul>
			<h:form>
				<h:commandLink action="#{mainView.jump2Logout}" value="#{msg.logout}"/><br/>
			</h:form>
		</body>
	</html>
</f:view>
