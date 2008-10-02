<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<f:loadBundle basename="admin.datas" var="msg"/>

<html>
	<f:view>
		<head>
			<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
			<title><h:outputText value="#{msg.title}"/></title>
		</head>
		<body>
			<f:subview id="nav">
				<jsp:include page="navigation.jsp"/>
			</f:subview>
			<h1 align="center"><h:outputText value="#{msg.title}"/></h1>
			
			<h:dataTable value="#{dataview.shortRepoData}" var="row" border="0">
				<h:column>
					<h:outputText value="Object ID "/>
				</h:column>
				<h:column>
					<h:outputText value="#{row.oid}"/>
				</h:column>
				<h:column>
					<h:form>
						<h:commandLink action="#{row.detail}" value="details"/>
					</h:form>
				</h:column>
			</h:dataTable>
			
			<f:subview id="footer">
				<jsp:include page="footer.jsp"/>
			</f:subview>
		</body>
	</f:view>
</html>
