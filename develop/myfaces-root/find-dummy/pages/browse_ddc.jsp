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

		<div id="div_simplebrowselist">
			<h3>Einfache DDC-Liste</h3>

            <t:dataList id="ddcnavilist_lvl1" value="#{searchBean.browse.listDDCNaviNodes}" var="node_lvl1" layout="unorderedList">
				<div class="div_simplebrowselist_lvl1_outer">
				<div class="div_simplebrowselist_lvl1_inner">
				<span class="span_ddc_num"><t:outputText value="#{node_lvl1.strDDCValue}"/></span>&nbsp;
				<span class="span_ddc_name"><t:outputText value="#{node_lvl1.strNameDE}"/></span>&nbsp;
				(<t:outputText value="#{node_lvl1.longItemCount}"/>)
				</div>
	            <t:dataList id="ddcnavilist_lvl2" value="#{node_lvl1.listSubnodes}" var="node_lvl2" layout="unorderedList">
					<div class="div_simplebrowselist_lvl2_outer">
					<div class="div_simplebrowselist_lvl2_inner">
					<span class="span_ddc_num"><t:outputText value="#{node_lvl2.strDDCValue}"/></span>&nbsp;
					<span class="span_ddc_name"><t:outputText value="#{node_lvl2.strNameDE}"/></span>&nbsp;
					(<t:outputText value="#{node_lvl2.longItemCount}"/>)
					</div>
  	 	            <t:dataList id="ddcnavilist_lvl3" value="#{node_lvl2.listSubnodes}" var="node_lvl3" layout="unorderedList">
						<t:outputText value="#{node_lvl3.strDDCValue}"/>&nbsp;<t:outputText value="#{node_lvl3.strNameDE}"/>&nbsp;(<t:outputText value="#{node_lvl3.longItemCount}"/>)
	            		<t:dataList id="ddcnavilist_lvl4" value="#{node_lvl1.listSubnodes}" var="node_lvl4" layout="unorderedList">
							<t:outputText value="#{node_lvl4.strDDCValue}"/>&nbsp;<t:outputText value="#{node_lvl4.strNameDE}"/>&nbsp;(<t:outputText value="#{node_lvl4.longItemCount}"/>)
						</t:dataList>
					</t:dataList>
					</div>
				</t:dataList>
				</div>
			</t:dataList>

			<hr/>

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
