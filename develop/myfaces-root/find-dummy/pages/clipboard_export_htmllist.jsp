<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t" %>

<f:loadBundle basename="finddummy.index" var="index"/>

<html>
<f:view>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
		<link rel="stylesheet" href="../css/teststyle.css" type="text/css"/>
		<title>Found</title>
	</head>
	<body>

    <h3>Merkzettel, aus Open-Access-Netzwerk exportiert</h3>

<h:form>
            <t:dataList id="clipboard_exported_html" value="#{searchBean.hitlist.listClipboardOID}" var="clipboardOID" layout="unorderedList" first="0" dir="LTR">
			   <h:outputLink value="#{searchBean.hitlist.mapHitBean[clipboardOID].bestLink}" target="_blank" title="#{searchBean.hitlist.mapHitBean[clipboardOID].trimmedTitle}">
               <t:outputText value="#{searchBean.hitlist.mapHitBean[clipboardOID].trimmedTitle}"/>
               </h:outputLink>
			</t:dataList>
</h:form>

	</body>
</f:view>
</html>
