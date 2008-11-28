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
	
<!-- test  -->
<h:form>
		<div id="div_simplebrowselist">
			<h3>Einfache DDC-Liste</h3>
            <h:dataTable id="browselist" value="#{searchBean.browse.simpleDDCCategorySums}" var="entry" columnClasses="colCategory">
				<h:column>
					<f:facet name="header">
						<h:outputText value="DDC-Wert"/>
					</f:facet>
					<t:outputText value="#{entry[0]}"/>
				</h:column>
				<h:column>
					<f:facet name="header">
						<h:outputText value="Name der Kategorie"/>
					</f:facet>
					<t:outputText value="#{searchBean.browse.mapDDCNames_de[entry[0]]}"/>
				</h:column>
				<h:column>
					<f:facet name="header">
						<h:outputText value="Anzahl der Dokumente"/>
					</f:facet>
					<t:outputText value="#{entry[1]}"/>
				</h:column>
			</h:dataTable>
			<hr/>
            <h:dataTable id="browselist2" value="#{searchBean.browse.directDDCCategorySums}" var="entry" columnClasses="colCategory">
				<h:column>
					<f:facet name="header">
						<h:outputText value="DDC-Wert (wildcard)"/>
					</f:facet>
					<t:outputText value="#{entry[2]}"/>
				</h:column>
				<h:column>
					<f:facet name="header">
						<h:outputText value="Name der Kategorie"/>
					</f:facet>
					<t:outputText value="#{searchBean.browse.mapDDCNames_de[entry[0]]}"/>
				</h:column>
				<h:column>
					<f:facet name="header">
						<h:outputText value="Anzahl der Dokumente (wildcard match)"/>
					</f:facet>
					<t:outputText value="#{entry[1]}"/>
				</h:column>
			</h:dataTable>
        </div>     

</h:form>

	</body>
</f:view>
</html>
