<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t" %>


<html>
	<f:view>
	<f:loadBundle basename="messages" var="msg"/>
	
	<head>
		<%@ include file="components/header.htm" %>
		<title>Found</title>
	</head>
	<body>
			<!-- include link navigation -->	         
        	<%@ include file="components/mainnavigation.htm" %>

			<div id="div_flat_search">
			<h:form>
				<h:inputText maxlength="2048" size="55" title="OAN-Suche" value="#{searchBean.strOneSlot}"/>
				<span class="span_selected_ddc">Kategorie: <t:outputText value="#{searchBean.browse.selectedDDCCatName}"/></span>
				<h:commandButton value="Finde!" action="#{searchBean.actionSearchWithDDCButton}"/>
			</h:form>
		</div>

<h:form>

		<div id="div_simplebrowselist">
			<h3>Liste der Fachgebiete nach Dewey-Dezimalklassifikation (DDC)</h3>
            <div class="div_howto">
            W&auml;hlen Sie eine Kategorie aus, auf die Sie die Suche einschr&auml;nken wollen!
            </div>

<% /* 
			<div id="div_ddc_breadcrump">
            PFAD:
            <t:dataList id="ddc_breadcrump" value="#{searchBean.browse.pathDDCCategories}" var="item" layout="simple">
				<t:outputText value="#{item}"/> &gt; 
			</t:dataList>
			</div>

*/ %>
            <div id="div_alle_dcc_cat">
            <h:commandLink action="#{searchBean.browse.actionUnselectDDCCategoryLink}" title=""><span class="span_ddc_name"><t:outputText value="Alle Kategorien (Keine Einschränkung)"/></span></h:commandLink>
            </div>                
            <t:dataList id="ddcnavilist_lvl1" value="#{searchBean.browse.listDDCNaviNodes}" var="node_lvl1" layout="unorderedList">
                <t:div rendered="#{node_lvl1.inPath}">
				<div class="selected_category_wrapper">
				<span class="span_ddc_num"><t:outputText value="#{node_lvl1.strDDCValue}"/></span>&nbsp;
				<span class="span_ddc_selected_name"><t:outputText value="#{node_lvl1.strNameDE}"/></span>
				(<t:outputText value="#{node_lvl1.longItemCount}"/>|<t:outputText value="#{node_lvl1.longItemSubCount}"/>)
				</div>
                </t:div>	            
                <t:div rendered="#{!node_lvl1.inPath}">
				<span class="span_ddc_num"><t:outputText value="#{node_lvl1.strDDCValue}"/></span>&nbsp;
				<h:commandLink action="#{node_lvl1.actionSelectDDCCategoryLink}" title=""><span class="span_ddc_name"><t:outputText value="#{node_lvl1.strNameDE}"/></span></h:commandLink>&nbsp;
				(<t:outputText value="#{node_lvl1.longItemCount}"/>|<t:outputText value="#{node_lvl1.longItemSubCount}"/>)
                </t:div>	            

                <t:div rendered="#{node_lvl1.inPath}">
	            <t:dataList id="ddcnavilist_lvl2" value="#{node_lvl1.listSubnodes}" var="node_lvl2" layout="unorderedList">

                    <t:div rendered="#{node_lvl2.inPath}">
   					<div class="selected_category_wrapper">
					<span class="span_ddc_num"><t:outputText value="#{node_lvl2.strDDCValue}"/></span>&nbsp;
					<span class="span_ddc_selected_name"><t:outputText value="#{node_lvl2.strNameDE}"/></span>
					(<t:outputText value="#{node_lvl2.longItemCount}"/>|<t:outputText value="#{node_lvl2.longItemSubCount}"/>)
					</div>
					</t:div>
                    <t:div rendered="#{!node_lvl2.inPath}">
					<span class="span_ddc_num"><t:outputText value="#{node_lvl2.strDDCValue}"/></span>&nbsp;
					<h:commandLink action="#{node_lvl2.actionSelectDDCCategoryLink}" title=""><span class="span_ddc_name"><t:outputText value="#{node_lvl2.strNameDE}"/></span></h:commandLink>
					(<t:outputText value="#{node_lvl2.longItemCount}"/>|<t:outputText value="#{node_lvl2.longItemSubCount}"/>)
					</t:div>

					<t:div rendered="#{node_lvl2.inPath}">
  	 	            <t:dataList id="ddcnavilist_lvl3" value="#{node_lvl2.listSubnodes}" var="node_lvl3" layout="unorderedList">

                        <t:div rendered="#{node_lvl3.inPath}">
						<div class="selected_category_wrapper">
						<span class="span_ddc_num"><t:outputText value="#{node_lvl3.strDDCValue}"/></span>&nbsp;
						<span class="span_ddc_selected_name"><t:outputText value="#{node_lvl3.strNameDE}"/></span>
                        (<t:outputText value="#{node_lvl3.longItemCount}"/>|<t:outputText value="#{node_lvl3.longItemSubCount}"/>)
						</div>
						</t:div>
                        <t:div rendered="#{!node_lvl3.inPath}">
						<span class="span_ddc_num"><t:outputText value="#{node_lvl3.strDDCValue}"/></span>&nbsp;
						<h:commandLink action="#{node_lvl3.actionSelectDDCCategoryLink}" title=""><span class="span_ddc_name"><t:outputText value="#{node_lvl3.strNameDE}"/></span></h:commandLink>
                        (<t:outputText value="#{node_lvl3.longItemCount}"/>|<t:outputText value="#{node_lvl3.longItemSubCount}"/>)
						</t:div>

					</t:dataList>
					</t:div>

				</t:dataList>
				</t:div>

			</t:dataList>
			
        </div>     

</h:form>



	</body>
	</f:view>
</html>
