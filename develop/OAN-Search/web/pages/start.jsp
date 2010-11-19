<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t" %>
<%@ page session="false"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">




<html>
	
	<f:view>
	<f:loadBundle basename="messages" var="msg"/>
	
	<head>
		<%@ include file="components/header.htm" %>
		<meta name="google-site-verification" content="mG0nzduAbIGfgT0sD-vRqMpsGvB07hhdbtp-iLwGUm0" />
		<meta name="description" content="Über diese Suchmaschine können sie nach wissenschaftlichen Publikationen (Diplomarbeiten, Dissertationen und sonstige Veröffentlichungen) suchen. " />
		<meta name="keywords" content="Open Access Netzwerk, OAN, Wissenschaftliche Publikationen, Diplomarbeiten, Dissertationen, Artikel" />
		<title><h:outputText value="#{msg.start_title}"/></title>
	</head>
	
	<body>
			         
			<!-- include link navigation -->	         
        	<%@ include file="components/mainnavigation.htm" %>
    		         
						
			<div id="div_main_search">

   			    <h:form>
				<span class="center">
                <img src="../img/Logo_oan_rgb_micro.png" />
                <div style="height: 25px;"></div>
                
				<div id="div_option_tabs">
					<div>
						<h:outputText value="#{msg.linkname_search}" />
					</div>
					<div>
						<h:commandLink value="#{msg.linkname_browse_ddc}" action="browse_ddc"/>
					</div>
				</div>

				<div id="div_search_widgets">
				<h:inputText maxlength="2048" size="55" title="OAN-Suche"
					         value="#{searchBean.strOneSlot}" />
				<h:commandButton value="#{msg.find_meta}"
			                     action="#{searchBean.actionSearch2Button}" />
			    <h:commandButton value="#{msg.find}"
			                     action="#{searchBean.actionSearchButton}" />
				</div>

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

				<span />
				</h:form>
	


			</div>

	</body>
	
	</f:view>
	
</html>