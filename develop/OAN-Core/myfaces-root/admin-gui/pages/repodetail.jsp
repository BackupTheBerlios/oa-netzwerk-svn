<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>


<f:view>
<f:loadBundle basename="admin.repositories" var="msg"/>
	<html>
		<head>
			<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
			<title><h:outputText value="#{msg.title}"/></title>
			<link rel="stylesheet" href="../css/repodetail.css" type="text/css"/>
		</head>
		<body>
			<f:subview id="nav">
				<jsp:include page="navigation.jsp"/>
			</f:subview>
			<h:panelGrid border="0" columns="2">
				<h:outputText value="Id"/>
				<h:outputText value="#{repoView.detail.ID}"/>
				
				<h:outputText value="Name"/>
				<h:outputText value="#{repoView.detail.name}"/>
				
				<h:outputText value="URL"/>
				<h:outputText value="#{repoView.detail.url}"/>
				
				<h:outputText value="OAI URL"/>
				<h:outputText value="#{repoView.detail.oai_url}"/>
				
				<h:outputText value="Test Data"/>
				<h:outputText value="#{repoView.detail.test_data}"/>
				
				<h:outputText value="Harvest Pause"/>
				<h:outputText value="#{repoView.detail.harvest_pause}"/>
				
				<h:outputText value="Harvest Amount"/>
				<h:outputText value="#{repoView.detail.harvest_amount}"/>
			</h:panelGrid>
			<h:form>
				<h:commandButton action="#{repoView.data4repo}" value="Daten des Repositoriums"/>
			</h:form>
			
			<f:subview id="footer">
				<jsp:include page="footer.jsp"/>
			</f:subview>
		</body>
	</html>
</f:view>
