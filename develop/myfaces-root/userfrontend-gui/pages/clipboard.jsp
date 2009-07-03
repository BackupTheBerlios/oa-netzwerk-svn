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
			<div id="div_general_links">
			<h:form>
				<h:commandLink value="#{index.linkname_start}" action="start"/>&nbsp;
                <h:commandLink value="#{index.linkname_projekt}" action="projekt"/>&nbsp;
                <h:commandLink value="#{index.linkname_impressum}" action="impressum"/>&nbsp; 
                <h:commandLink value="Teilnehmende Repositorien" action="repositories"/>&nbsp;
                <h:outputLink value="http://oanetzwerk.wordpress.com" target="_blank">Blog</h:outputLink>&nbsp;
                <h:outputLink value="http://oanetzwerk.wordpress.com/2009/07/03/worum-geht-es-eigentlich/" target="_blank">About</h:outputLink>&nbsp;
			</h:form>
            </div>
	
		<div id="div_flat_search">
			<h:form>
				<h:inputText maxlength="2048" size="55" title="OAN-Suche" value="#{searchBean.strOneSlot}"/>
				<h:commandButton value="#{index.find}" action="#{searchBean.actionSearchButton}"/>
			</h:form>
			</div>
	


<h:form>

<div id="div_opened_inpageright_box">

        <div id="div_clipboard_header">
            <span class="command_link">
               <h:commandLink action="close_clipboard" title="Merkzettel verkleinern"><img src="../img/16x16_minimize.png" /></h:commandLink>
            </span>
            <h:commandLink action="close_clipboard" title="Merkzettel verkleinern">
            Merkzettel
            </h:commandLink>
        </div>
		<div id="div_clipboard_overview">

            <t:dataList id="clipboard_overview" value="#{searchBean.hitlist.listClipboardOID}" var="clipboardOID" layout="unorderedList" first="0" dir="LTR">
			   
			   <div class="div_clipboard_hit_header">
               <span class="command_link">
               <h:commandLink action="#{searchBean.hitlist.mapHitBean[clipboardOID].actionDetailsLink}" title="Metadaten des Objektes betrachten"><img src="../img/16x16_lupe.png" /></h:commandLink>
               </span>
               <span class="command_link">
               <h:commandLink action="#{searchBean.hitlist.mapHitBean[clipboardOID].actionVerwerfenLink}" title="Objekt aus der Merkliste entfernen"><img src="../img/16x16_minus.png" /></h:commandLink>
               </span>		
			   <span class="list-identifier">
			   <h:outputLink value="#{searchBean.hitlist.mapHitBean[clipboardOID].bestLink}" target="_blank" title="#{searchBean.hitlist.mapHitBean[clipboardOID].trimmedTitle}">
               <t:outputText value="#{searchBean.hitlist.mapHitBean[clipboardOID].trimmedTitle}"/>
               </h:outputLink>
               </span>
				</div>

			   <div class="div_clipboard_hit_details">               
               <span class="hit-creators"><t:outputText value="#{searchBean.hitlist.mapHitBean[clipboardOID].trimmedCreators}"/> (<t:outputText value="#{searchBean.hitlist.mapHitBean[clipboardOID].trimmedDate}"/>):</span>
               <%//<br/><span class="hit-keywords"><t:outputText value="#{searchBean.hitlist.mapHitBean[clipboardOID].trimmedKeywords}"/></span><br/>%>
 			   <span class="hit-url"><t:outputText value="#{searchBean.hitlist.mapHitBean[clipboardOID].bestLink}"/></span>
			   <span class="internal_identifier">(OID: <h:outputText value="#{clipboardOID}"/>)</span>
			   </div>

			</t:dataList>

		</div>
        <div id="div_clipboard_footer">
            <span class="command_link">
               <h:commandLink action="clipboard_export_htmllist" title="Export des Merkzettels als HTML-Seite" target="_blank">[export list -- plain html]</h:commandLink>
            </span>
        </div>


</div>

</h:form>

	</body>
</f:view>
</html>
