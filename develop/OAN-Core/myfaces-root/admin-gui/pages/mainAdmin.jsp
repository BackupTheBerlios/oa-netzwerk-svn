<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>


<html>
	<f:view>
	<f:loadBundle basename="admin.mainadmin" var="msg"/>
		<head>
			<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
			<title><h:outputText value="#{msg.mainPageTitle}"/></title>
		</head>
		<body>
			<h1 align="center"><h:outputText value="#{msg.mainPageTitle}"/></h1>
			<f:subview id="nav">
				<jsp:include page="navigation.jsp"/>
			</f:subview>
			<div class="body">
				<h:outputText value="#{msg.blindText}"/>
			</div>
			<f:facet name="footer">
				<f:subview id="footer">
					<jsp:include page="footer.jsp"/>
				</f:subview>
			</f:facet>
		</body>
	</f:view>
</html>
