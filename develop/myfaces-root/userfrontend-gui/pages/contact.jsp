<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ page session="false"%>


<html>

	<f:view>
	<f:loadBundle basename="messages" var="msg" />

	<%@ include file="components/header.htm"%>

	<title><h:outputText value="#{msg.linkname_contact} - #{msg.project_name}" /></title>

	<body>

	<%@ include file="/pages/components/mainnavigation.htm"%>

	<div id="div_contact_main">
	<h2>Kontakt</h2>
	<h4>Unter folgender E-Mail Adresse können Sie uns erreichen:</h4>
	<p>E-Mail: oan(at)dini.de</p>
	<br />

	<!--<h4>Bei technischen Fragen wenden Sie sich bitte an:</h4>
				<p>
				<b>Sammy David</b><br />
				HU Berlin, Computer- und Medienservice (CMS)<br />
				E-Mail: sammy.david(at)cms.hu-berlin.de<br />
 				Telefon: 030 2093 70041
				</p>--></div>
	</body>
	
</f:view>

</html>