<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t" %>

<f:loadBundle basename="admin.repositories" var="msg"/>
<f:view>
	<html>
		<head>
			<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
			<link rel="stylesheet" href="../css/teststyle.css" type="text/css"/>
			<title><h:outputText value="#{msg.title}"/></title>
		</head>
		<body>
			<h1 align="center"><h:outputText value="#{msg.title}"/></h1>
			<h:dataTable value="#{repoView.repositories}" var="row" border="1" headerClass="HEADING" columnClasses="COL1, COL2, COL3">
				<h:column>
					<f:facet name="header">
						<h:outputText value="#{msg.name}"/>
					</f:facet>
					<h:form>
						<t:saveState id="id" value="#{row.id}"></t:saveState>
						<h:commandLink action="#{repoView.showDetails}" value="#{row.name}"/>
					</h:form>
				</h:column>
				<h:column>
					<f:facet name="header">
						<h:outputText value="#{msg.url}"/>
					</f:facet>
					<h:outputText value="#{row.url}"/>
				</h:column>
				<h:column>
					<f:facet name="header">
						<h:outputText value="#{msg.id}"/>
					</f:facet>
					<h:outputText value="#{row.id}"/>
				</h:column>
			</h:dataTable>
			<h:form>
				<h:commandLink action="#{mainView.jump2Logout}" value="#{msg.logout}"/><br/>
			</h:form>
		</body>
	</html>
</f:view>
