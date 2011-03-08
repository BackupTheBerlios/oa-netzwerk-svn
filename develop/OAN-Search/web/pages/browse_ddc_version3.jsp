<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t" %>

<html>
<f:view>
<f:loadBundle basename="messages" var="msg"/>

<head>
	<%@ include file="components/header.htm" %>
	<title><h:outputText value="#{msg.title_browse_ddc}" /></title>
	<!-- TODO: nach Testphase in CSS-Datei auslagern -->
	<style type="text/css">
	
		.colStyle1 { 
			width: 350px;
			vertical-align:top;
		}
		.colStyle2 { 
			width: 750px;
			vertical-align:top;
		}
	</style>
</head>
<body>
	<!-- TODO: generell alle Ausgabetexte für Internationalisierung in Language-Dateien auslagern -->
	
	<!-- include link navigation -->	         
  <%@ include file="components/mainnavigation.htm" %>

	<div id="box_grey">
		<div id="div_flat_search">
			<h:form>
				<H3><h:outputText value="#{msg.linkname_search}"/></H3>
				<h:inputText maxlength="2048" size="55" title="OAN-Suche" value="#{searchBean.strOneSlot}"/>
				<span class="span_selected_ddc"><h:outputText value="#{msg.category}"/>: <t:outputText value="#{searchBean.browse.selectedDDCCatName}"/></span>
				<h:commandButton value="#{msg.start_search}" action="#{searchBean.actionSearchWithDDCButton}"/>
			</h:form>
		</div>
	</div>
	<h:form>

		<t:panelGrid columns="2" columnClasses="colStyle1,colStyle2">
			<t:panelGroup >
				<div id="box_light_grey">
				
					<div id="div_simplebrowselist">
							<b><h:outputText value="#{msg.title_list_of_DDC}"/></b>
			
							<% /* 
								<div id="div_ddc_breadcrump">
					            PFAD:
					            <t:dataList id="ddc_breadcrump" value="#{searchBean.browse.pathDDCCategories}" var="item" layout="simple">
									<t:outputText value="#{item}"/> &gt; 
								</t:dataList>
								</div>	
							*/ %>
		            <div id="div_alle_dcc_cat" >
		            	<h:commandLink action="#{searchBean.browse.actionUnselectDDCCategoryLink}" title=""><span class="span_ddc_name"><t:outputText value="#{msg.all_categories_no_restrictions}"/></span></h:commandLink>
		            </div>                
		            <t:dataList id="ddcnavilist_lvl1" value="#{searchBean.browse.listDDCNaviNodes}" var="node_lvl1" layout="unorderedList">	
		                <t:div rendered="#{node_lvl1.inPath}">
												<div class="selected_category_wrapper">
													<span class="span_ddc_num"><t:outputText value="#{node_lvl1.strDDCValue}"/></span>&nbsp;
													<span class="span_ddc_selected_name"><t:outputText value="#{node_lvl1.strNameDE}"/></span>			
												</div>
		                </t:div>	            
		                <t:div rendered="#{!node_lvl1.inPath}">
											<span class="span_ddc_num"><t:outputText value="#{node_lvl1.strDDCValue}"/></span>&nbsp;
											<h:commandLink action="#{node_lvl1.actionSelectDDCCategoryLink}" title=""><span class="span_ddc_name"><t:outputText value="#{node_lvl1.strNameDE}"/></span></h:commandLink>&nbsp;
		                </t:div>
		                <t:div rendered="#{node_lvl1.inPath}">
					            <t:dataList id="ddcnavilist_lvl2" value="#{node_lvl1.listSubnodes}" var="node_lvl2" layout="unorderedList">	
				                    <t:div rendered="#{node_lvl2.inPath}">
									   					<div class="selected_category_wrapper">
																<span class="span_ddc_num"><t:outputText value="#{node_lvl2.strDDCValue}"/></span>&nbsp;
																<span class="span_ddc_selected_name"><t:outputText value="#{node_lvl2.strNameDE}"/></span>
																	<br/><h:commandLink value="#{msg.start_search}" action="#{searchBean.actionSearchFromDDCTreeDirectlyWithAutoHits}"/>	
															</div>
														</t:div>
				                    <t:div rendered="#{!node_lvl2.inPath}">
															<span class="span_ddc_num"><t:outputText value="#{node_lvl2.strDDCValue}"/></span>&nbsp;
															<h:commandLink action="#{node_lvl2.actionSelectDDCCategoryLink}" title=""><span class="span_ddc_name"><t:outputText value="#{node_lvl2.strNameDE}"/></span></h:commandLink>
														</t:div>	
											</t:dataList>
										</t:div>
								</t:dataList>			
					</div>
			</div>     
     </t:panelGroup>
     <t:panelGroup>
				<div id="div_inpagecenter_box_big">			
					<!-- include hitlist components -->
			        <%@ include file="components/hitlist_button_assisted_scroller.htm" %>
			        <%@ include file="components/hitlist_medium.htm" %>
				</div>
				</t:panelGroup>
				  <f:facet name="footer">
		    <t:panelGroup>

	
	
		    </t:panelGroup>
  </f:facet>
				
			</t:panelGrid> 	
	</h:form>
</body>
</f:view>
</html>
