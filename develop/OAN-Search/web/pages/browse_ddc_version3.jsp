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
</head>
<body>
	<!-- TODO: generell alle Ausgabetexte für Internationalisierung in Language-Dateien auslagern -->
	
	<!-- include link navigation -->	         
  <%@ include file="components/mainnavigation.htm" %>
	<%@ include file="components/flat_category_search.htm" %>

	<h:form>
		<t:panelGrid columns="3" columnClasses="colStyle340,colStyle10,colStyle750">
			<t:panelGroup >
				<div id="div_seperator_right">	
				<div id="div_simplebrowselist">
					    <div id="div_alle_dcc_cat" >
	            	<h:commandLink action="#{searchBean.browse.actionUnselectDDCCategoryLink}" title=""><span class="span_ddc_name"><t:outputText value="#{msg.all_categories_no_restrictions}"/></span></h:commandLink>
	            </div> 
						<b><h:outputText value="#{msg.title_list_of_DDC}"/></b>
		
						<% /* 
							<div id="div_ddc_breadcrump">
				            PFAD:
				            <t:dataList id="ddc_breadcrump" value="#{searchBean.browse.pathDDCCategories}" var="item" layout="simple">
								<t:outputText value="#{item}"/> &gt; 
							</t:dataList>
							</div>	
						*/ %>
              <div id="div_dcc_list" > 
	            <t:dataList id="ddcnavilist_lvl1" value="#{searchBean.browse.listDDCNaviNodes}" var="node_lvl1" layout="unorderedList">	
	                <t:div rendered="#{node_lvl1.inPath}">
											<div class="selected_category_wrapper">
												<span class="span_ddc_num"><t:outputText value="#{node_lvl1.strDDCValue}"/></span>&nbsp;<br />
												<span class="span_ddc_selected_name"><t:outputText value="#{node_lvl1.strNameDE}"/></span>			
											</div>
	                </t:div>	            
	                <t:div rendered="#{!node_lvl1.inPath}">
										<span class="span_ddc_num"><t:outputText value="#{node_lvl1.strDDCValue}"/></span>&nbsp;<br />
										<h:commandLink action="#{node_lvl1.actionSelectDDCCategoryLink}" title=""><span class="span_ddc_name"><t:outputText value="#{node_lvl1.strNameDE}"/></span></h:commandLink>&nbsp;
	                </t:div>
	                <t:div rendered="#{node_lvl1.inPath}">
				            <t:dataList id="ddcnavilist_lvl2" value="#{node_lvl1.listSubnodes}" var="node_lvl2" layout="unorderedList">	
			                    <t:div rendered="#{node_lvl2.inPath}">
								   					<div class="selected_category_wrapper">
															<span class="span_ddc_num"><t:outputText value="#{node_lvl2.strDDCValue}"/></span>&nbsp;<br />
															<span class="span_ddc_selected_name"><t:outputText value="#{node_lvl2.strNameDE}"/></span>
															(<t:outputText value="#{node_lvl2.longItemCount}"/>|<t:outputText value="#{node_lvl2.longItemSubCount}"/>)
																<br/><h:commandLink value="#{msg.start_search}" action="#{searchBean.actionSearchFromDDCTreeDirectlyWithAutoHits}"/>	
														</div>
													</t:div>
			                    <t:div rendered="#{!node_lvl2.inPath}">
														<span class="span_ddc_num"><t:outputText value="#{node_lvl2.strDDCValue}"/></span>&nbsp;<br />
														<h:commandLink action="#{node_lvl2.actionSelectDDCCategoryLink}" title=""><span class="span_ddc_name"><t:outputText value="#{node_lvl2.strNameDE}"/></span></h:commandLink>
													</t:div>	
										</t:dataList>
									</t:div>
							</t:dataList>
						</div>		
				</div> 
				</div>   
     </t:panelGroup>
     <t:panelGroup>
     <!-- Trennspalte -->
		 </t:panelGroup>
		<t:panelGroup>
				<div id="div_inpagecenter_box_big">			
					<!-- include hitlist components -->
			        <%@ include file="components/hitlist_button_assisted_scroller.htm" %>
			        <%@ include file="components/hitlist_medium.htm" %>
				</div>
		</t:panelGroup>
				  <f:facet name="footer"><t:panelGroup></t:panelGroup></f:facet>				
		</t:panelGrid>
	</h:form>
</body>
</f:view>
</html>
