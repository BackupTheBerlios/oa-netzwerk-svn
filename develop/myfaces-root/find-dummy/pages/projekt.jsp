<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t" %>
<%@ page session="false"%>

<f:loadBundle basename="finddummy.index" var="index"/>

<html>
	<f:view>
	    <head>
		   <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
		   <link rel="stylesheet" href="../css/teststyle.css" type="text/css"/>
		   <title><h:outputText value="#{index.title}"/></title>
  	    </head>
		<body>
			
			<div id="div_general_links">
			<h:form>
				<h:commandLink value="#{index.linkname_start}" action="start"/>&nbsp;
                <h:commandLink value="#{index.linkname_projekt}" action="projekt"/>&nbsp;
                <h:commandLink value="#{index.linkname_impressum}" action="impressum"/>&nbsp; 
			</h:form>
            </div>
			
			<h1>Projekt</h1>
		</body>
	</f:view>
</html>
