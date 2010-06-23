<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t" %>
<%@ page session="false"%>

<f:loadBundle basename="finddummy.index" var="index"/>

<html>
	
	<f:view>
	
	<head>
		<%@ include file="components/header.htm" %>
		<title><h:outputText value="#{index.start_title}"/></title>
	</head>
	
	<body>
			         
			<!-- include link navigation -->	         
        	<%@ include file="components/mainnavigation.htm" %>
    		         
						
			<div id="div_main_search">

   			    <h:form>
				<span class="center">
                <img src="../img/Logo_oan_rgb_micro.png" />
				
				<div id="div_option_tabs">
					<div>
						<h:outputText value="#{index.linkname_search}" />
					</div>
					<div>
						<h:commandLink value="#{index.linkname_browse_ddc}" action="browse_ddc"/>
					</div>
				</div>

				<div id="div_search_widgets">
				<h:inputText maxlength="2048" size="55" title="OAN-Suche"
					         value="#{searchBean.strOneSlot}" />
				<h:commandButton value="#{index.find_meta}"
			                     action="#{searchBean.actionSearch2Button}" />
			    <h:commandButton value="#{index.find}"
			                     action="#{searchBean.actionSearchButton}" />
				</div>
 				<!-- ------------------- -->
				<t:div rendered='#{searchBean.strRepositoryFilterRID != ""}'>
					<small>Die Suchmaske wurde eingeschr&auml;nkt auf folgende Quelle:</small><br />
					<h:outputLink value="#{searchBean.strRepositoryFilterURL}">
						<h:outputText value="#{searchBean.strRepositoryFilterName}" />
						<small>(<h:outputText value="#{searchBean.strRepositoryFilterURL}" />)</small>
					</h:outputLink>
				</t:div>
 
                 <t:div id="div_search_error" rendered='#{searchBean.strErrorLastSearch != ""}'>
					Beim angeschlossenen Suchdienst ist leider ein Fehler aufgetreten:<br />
					<small>(<h:outputText value="#{searchBean.strErrorLastSearch}" />)</small>		
				 </t:div> 

				</span>
				</h:form>
	


			</div>

		</f:view>
	</body>
</html>
