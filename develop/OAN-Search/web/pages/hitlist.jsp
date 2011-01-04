<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t" %>

<html>
<f:view>
<f:loadBundle basename="messages" var="msg"/>	
<head>
	<%@ include file="components/header.htm" %>
	<title>Ergebnisse - Open Access Netzwerk (OAN)</title>
</head>
<body>	
	<!-- include link navigation -->	         
   	<%@ include file="components/mainnavigation.htm" %>       	
   	<!-- include search bar -->
   	<%@ include file="components/flat_search.htm" %>
	<h:form>
		<div id="div_inpagecenter_box">			
			<!-- include hitlist components -->
	        <%@ include file="components/hitlist_scroller.htm" %>
	        <%@ include file="components/hitlist.htm" %>			 
	        <span class="command_link_bibtex"><h:commandLink action="#{searchBean.actionAddAllHitsToClipboard}" title="Alle #{searchBean.hitlist.sizeListHitOID} Treffer zur Merkliste hinzufügen">[alle Treffer zur Merkliste hinzufügen]</h:commandLink> (Achtung, dieser Vorgang kann bisweilen recht lange dauern!)</span>
		</div>			
		<t:div rendered="#{searchBean.hitlist.sizeListClipboardOID > 0}">			
		<div id="div_inpageright_box">			
			<!-- include clipboard components -->
			<%@ include file="components/clipboard_header.htm" %>
	        <%@ include file="components/clipboard_overview.htm" %>
	        <%@ include file="components/clipboard_footer.htm" %>			
		</div>
		</t:div>
	</h:form>
</body>
</f:view>
</html>
