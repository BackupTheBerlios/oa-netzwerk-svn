<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t" %>
<%@ page session="false"%>

<f:loadBundle basename="finddummy.index" var="index"/>

<html>
	<f:view>
	    <head>
		   <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
		   <link rel="stylesheet" href="../css/teststyle.css" type="text/css"/>
		   <title><h:outputText value="#{index.title}"/></title>
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
			    <h:commandButton value="#{index.find}"
			                     action="#{searchBean.actionSearchButton}" />
                </div>
 
				<t:div rendered='#{searchBean.strRepositoryFilterRID != ""}'>
					<small>Die Suchmaske wurde eingeschr&auml;nkt auf folgende Quelle:</small><br />
					<h:outputLink value="#{searchBean.strRepositoryFilterURL}">
						<h:outputText value="#{searchBean.strRepositoryFilterName}" />
						<small>(<h:outputText value="#{searchBean.strRepositoryFilterURL}" />)</small>
					</h:outputLink>
				</t:div> 
				</span>
				</h:form>
	
			</div>

		</body>
	</f:view>
</html>
