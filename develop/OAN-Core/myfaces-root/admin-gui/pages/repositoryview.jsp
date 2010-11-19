<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>


<f:view>
<f:loadBundle basename="admin.repositories" var="msg"/>
	<html>
		<head>
			<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
			<link rel="stylesheet" href="../css/teststyle.css" type="text/css"/>
			<title><h:outputText value="#{msg.title}"/></title>
		</head>
		<body>
			<h1 align="center"><h:outputText value="#{msg.title}"/></h1>
			<f:subview id="nav">
				<jsp:include page="navigation.jsp"/>
			</f:subview>
			<h:dataTable value="#{repoView.repositories}" var="row" border="1" headerClass="HEADING" columnClasses="COL1, COL2, COL3">
				<h:column>
					<f:facet name="header">
						<h:outputText value="#{msg.name}"/>
					</f:facet>
					<h:form>
						<h:commandLink action="#{row.detail}" value="#{row.name}"/>
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
			<f:subview id="footer">
				<jsp:include page="footer.jsp"/>
			</f:subview>
		</body>
	</html>
</f:view>
