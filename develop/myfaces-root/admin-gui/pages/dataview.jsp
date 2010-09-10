<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>


<html>
	<f:view>
	<f:loadBundle basename="admin.datas" var="msg"/>
		<head>
			<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
			<title><h:outputText value="#{msg.title}"/></title>
		</head>
		<body>
			<f:subview id="nav">
				<jsp:include page="navigation.jsp"/>
			</f:subview>
			<h1 align="center"><h:outputText value="#{msg.title}"/></h1>
			
			<h:form id="dataform">
			
				<h:dataTable id="datatable" value="#{dataview.shortRepoData}" var="object" border="0">
					<h:column>
						<h:outputText value="Object ID "/>
					</h:column>
					<h:column>
						<h:outputText value="#{object.oid}"/>
					</h:column>
					<h:column id="coldetail">
						<h:commandLink id="nondetailform" value="#{object.objectdetail}">
							<f:actionListener type="de.dini.oanetzwerk.admin.DataVisibilityListener"/>
						</h:commandLink>
						<h:outputText id="detailform">
							Failure Counter = 0 <br/>
							Harvested = 0 <br/>
							Repository Datestamp = 0 <br/>
							Repository Identifier = 0 <br/>
							Repository ID = 0 <br/>
							TestData = 0 <br/>
						</h:outputText>
					</h:column>
				</h:dataTable>
			</h:form>			
			<f:subview id="footer">
				<jsp:include page="footer.jsp"/>
			</f:subview>
		</body>
	</f:view>
</html>
