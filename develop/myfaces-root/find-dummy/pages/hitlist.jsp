<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

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
                <h:commandLink value="#{index.linkname_projekt}" action="test1"/>&nbsp;
                <h:commandLink value="#{index.linkname_impressum}" action="test2"/>&nbsp; 
			</h:form>
            </div>
	
		<div id="div_flat_search">
			<h:form>
				<h:inputText maxlength="2048" size="55" title="OAN-Suche" value="#{searchParam.strOneSlot}"/>
				<h:commandButton value="#{index.find}" action="#{searchParam.actionSearchButton}"/>
			</h:form>
			</div>


		<div id="div_hitlist">
	
        <h3>Trefferanzeige 1 bis <h:outputText value="#{hitlist.sizeListHitOID}"/> (<h:outputText value="#{hitlist.sizeListHitOID}"/> insgesamt)</h3>

            <h:dataTable value="#{hitlist.listHitOID}" var="hitOID" columnClasses="colHit">
				<h:column>
					<h:form>
						<span class="list-identifier"><h:commandLink action="foo_show" value="#{hitlist.mapCompleteMetadata[hitOID].titleList[0].title}"/></span>
						<div class="list-authors">
							<h:dataTable value="#{hitlist.mapCompleteMetadata[hitOID].authorList}" var="author">
								<h:column>
									<h:outputText value="#{author.firstname}"/>&nbsp;								
									<h:outputText value="#{author.lastname}"/>,&nbsp;
								</h:column>
							</h:dataTable>							
						</div>
						<span class="internal_identifier"><h:outputText value="#{hitlist.mapCompleteMetadata[hitOID].oid}"/></span>						
					</h:form>
				</h:column>
			</h:dataTable>
        </div>     

	</body>
</f:view>
</html>
