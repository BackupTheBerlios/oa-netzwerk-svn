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
               <h:commandLink action="close_clipboard" title="Merkzettel verkleinern"><img src="../img/16x16_minimize.PNG" /></h:commandLink>
            </span>
            <h:commandLink action="close_clipboard" title="Merkzettel verkleinern">
            Merkzettel
            </h:commandLink>
        </div>
        <div id="div_clipboard_overview">

            <t:dataList id="clipboard_overview" value="#{searchBean.hitlist.listClipboardOID}" var="clipboardOID" layout="unorderedList" first="0" dir="LTR">
               <span class="command_link">
               <h:commandLink action="#{searchBean.hitlist.mapHitBean[clipboardOID].actionDetailsLink}" title="Metadaten des Objektes betrachten"><img src="../img/16x16_lupe.PNG" /></h:commandLink>
               </span>
               <span class="command_link">
               <h:commandLink action="#{searchBean.hitlist.mapHitBean[clipboardOID].actionVerwerfenLink}" title="Objekt aus der Merkliste entfernen"><img src="../img/16x16_minus.PNG" /></h:commandLink>
               </span>		
			   <span class="list-identifier">
			   <h:outputLink value="#{searchBean.hitlist.mapHitBean[clipboardOID].bestLink}" target="_blank" title="#{searchBean.hitlist.mapHitBean[clipboardOID].trimmedTitle}">
               <t:outputText value="#{searchBean.hitlist.mapHitBean[clipboardOID].trimmedTitle}"/>
               </h:outputLink>
               </span>
               <span class="internal_identifier">(OID: <h:outputText value="#{clipboardOID}"/>)</span>
			</t:dataList>
        </div>
</div>

</h:form>

	</body>
</f:view>
</html>
