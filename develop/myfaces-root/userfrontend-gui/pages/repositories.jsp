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
                <h:commandLink value="Teilnehmende Repositorien" action="repositories"/>&nbsp;
			</h:form>
            </div>
	
<h:form>

		<div id="div_repositories">
            <div id="div_repositories_list">
    		<h2>Im Open Access Netzwerk nehmen zurzeit <t:outputText value="#{repositoriesBean.repoCount}"/> Repositories teil:</h2>
			
			<t:dataList value="#{repositoriesBean.listRepositoryConfig}" 
			                            var="repoConfig" layout="unorderedList" first="0" dir="LTR">
								<span class="span_reponame"><t:outputText value="#{repoConfig.repositoryName}"/></span><br/>
                                <h:outputLink value="#{repoConfig.repositoryURL}"><t:outputText value="#{repoConfig.repositoryURL}"/></h:outputLink><br/>                                
                                <span class="span_testbetrieb"><t:outputText value="(im Testbetrieb)" rendered="#{repoConfig.repositoryTEST_DATA}"/></span><br/>
			</t:dataList>
			
            </div>
        </div>     

		<div id="div_repositories_footer">
		  <hr/>
		  <p><small>(Diese &Uuml;bersicht wurde automatisiert aus der Datenbank des OA-Netzwerk-Systems erstellt. Stand: <t:outputText value="#{repositoriesBean.fromDate}"/>)</small></p>
        </div>

</h:form>


	</body>
</f:view>
</html>
