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
					    <%@ include file="components/ddc_category_list.htm" %>
					</div> 
				</div>   
     </t:panelGroup>
     <t:panelGroup>
     <!-- Trennspalte -->
		 </t:panelGroup>
		<t:panelGroup>
				<t:div id="div_inpagecenter_box_big" rendered="#{!searchBean.hitlist.booleanShowSearchHelpNotice}">			
			        <%@ include file="components/hitlist_button_assisted_scroller.htm" %>
			        <%@ include file="components/hitlist_medium.htm" %>
				</t:div>
				<t:div rendered="#{searchBean.hitlist.booleanShowSearchHelpNotice}">
							<br />
			        <t:outputText value="#{msg.hitlist_scroller_searchnotice}"/>
				</t:div>
		</t:panelGroup>
				  <f:facet name="footer"><t:panelGroup></t:panelGroup></f:facet>				
		</t:panelGrid>
	</h:form>
</body>
</f:view>
</html>
