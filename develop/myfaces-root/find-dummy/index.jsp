<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page session="false"%>

<f:loadBundle basename="finddummy.index" var="index"/>

<html>
	<f:view>
	    <head>
		   <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
		   <link rel="stylesheet" href="css/teststyle.css" type="text/css"/>
		   <title><h:outputText value="#{index.title}"/></title>
  	    </head>
		<body>
			<h:form>
			<div id="general_links">
				<h:commandLink value="#{index.linkname_start}" action="start"/>&nbsp;
                <h:commandLink value="#{index.linkname_projekt}" action="test1"/>&nbsp;
                <h:commandLink value="#{index.linkname_impressum}" action="test2"/>&nbsp; 
            </div>
			<div id="main_search">
                <span class="center">
				<img src="img/Logo_oan_rgb_micro.PNG"/>
				<h:inputText maxlength="2048" size="55" title="OAN-Suche" value="#{searchParam.strOneSlot}"/>
				<h:commandButton value="#{index.find}" action="#{searchParam.actionSearchButton}"/>
				</span>
			</div>
			</h:form>
		</body>
	</f:view>
</html>
