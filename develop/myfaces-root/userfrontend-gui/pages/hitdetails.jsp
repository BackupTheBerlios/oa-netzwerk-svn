<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t" %>

<f:loadBundle basename="finddummy.index" var="index"/>

<html>
<f:view>
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
                <span class="span_selected_ddc">Kategorie: <h:commandLink value="#{searchBean.browse.selectedDDCCatName}" action="browse_ddc"/></span>
				<h:commandButton value="#{index.find}" action="#{searchBean.actionSearchButton}"/>
                <t:div id="div_search_error" rendered='#{searchBean.strErrorLastSearch != ""}'>
					Beim angeschlossenen Suchdienst ist leider ein Fehler aufgetreten:<br />
					<small>(<h:outputText value="#{searchBean.strErrorLastSearch}" />)</small>					
				</t:div> 
			</h:form>
			</div>

		<div id="div_hitdetails_navi">
			<h:form>
				<span class="backlink"><h:commandLink value="#{index.linkname_close_details}" action="close_details"/></span>		
			</h:form>
		</div>

<h:form>

		<div id="div_hitdetails">

    		<h3>Detailanzeige der Metadaten</h3>

			<span class="internal_identifier">(OID: <h:outputText value="#{searchBean.hitlist.selectedDetailsOID}"/>)</span>		
			<br/>
			
			<table>
				<tr>
				<td class="hitlist_head">Titel:</td>
				<td class="hitlist_content">
							<t:dataList value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].completeMetadata.titleList}" 
			                            var="titleItem" layout="unorderedList" first="0" dir="LTR" styleClass="title_li">
								<t:outputText value="#{titleItem.title}"/>
							</t:dataList>
				</td>
				</tr>
				<tr>
				<td class="hitlist_head">Autoren:</td>
				<td class="hitlist_content">			

							<t:dataList value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].completeMetadata.authorList}" 
			                            var="author" layout="unorderedList" first="0"	dir="LTR">
								<t:outputText value="#{author.firstname}"></t:outputText>&nbsp;<t:outputText value="#{author.lastname}"></t:outputText>&nbsp;
							</t:dataList>
				</td>
				</tr>
				<tr>
				<td class="hitlist_head">Mitwirkende:</td>
				<td class="hitlist_content">			

							<t:dataList value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].completeMetadata.contributorList}" 
			                            var="contributor" layout="unorderedList" first="0"	dir="LTR">
								<t:outputText value="#{contributor.firstname}"></t:outputText>&nbsp;<t:outputText value="#{contributor.lastname}"></t:outputText>&nbsp;
							</t:dataList>
				</td>
				</tr>
				<tr>
				<td class="hitlist_head">Herausgeber/Institution:</td>
				<td class="hitlist_content">			

							<t:dataList value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].completeMetadata.publisherList}" 
			                            var="publisher" layout="unorderedList" first="0"	dir="LTR">
								<t:outputText value="#{publisher.name}"></t:outputText>&nbsp;
							</t:dataList>
				</td>
				</tr>
				<tr>
				<td class="hitlist_head">Zusammenfassung:</td>
				<td class="hitlist_content">			
					<div id="div_hitdetails_abstract">
							<t:dataList value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].completeMetadata.descriptionList}" 
			                            var="desc" layout="simple" first="0">
								<div>
								<h:outputText value="#{desc.description}"/>
								</div>
							</t:dataList>
					</div>
				</td>
				</tr>
				<tr>
				<td class="hitlist_head">Schlagworte:</td>
				<td class="hitlist_content">			
					<% //<h:outputText value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].mergedKeywords}" />
					%>
					<t:dataList value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].trimmedKeywordList}" 
			                    var="item" layout="unorderedList" first="0"	dir="LTR" styleClass="keyword_li">
                        <h:outputLink value="#{item[1]}" target="_blank" title="diesen Begriff bei Google suchen"><img src="../img/google_G.png"/></h:outputLink>&nbsp;
                        <h:outputLink value="#{item[2]}" target="_blank" title="diesen Begriff bei Wikipedia nachschlagen"><img src="../img/wikipedia_W.png"/></h:outputLink>&nbsp;                       
						<h:commandLink action="#{item[3].actionSearchForKeywordLink}" title="nach diesem Schlagwort suchen"><t:outputText value="#{item[0]}"></t:outputText></h:commandLink>&nbsp;
					</t:dataList>					
				</td>
				</tr>
				<tr>
				<td class="hitlist_head">Klassifikation:</td>
				<td class="hitlist_content">			
							<t:dataList value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].trimmedClassificationList}" 
			                            var="classif" layout="unorderedList" first="0"	dir="LTR">
								<t:outputText value="#{classif}"></t:outputText>&nbsp;
							</t:dataList>
				</td>
				</tr>
				<tr>
				<td class="hitlist_head">Datum:</td>
				<td class="hitlist_content">			
							<t:dataList value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].completeMetadata.dateValueList}" 
			                            var="item" layout="unorderedList" first="0" dir="LTR">
								<t:outputText value="#{item.dateValue}"></t:outputText>&nbsp;
							</t:dataList>
				</td>
				</tr>
				<tr>
				<td class="hitlist_head">Format:</td>
				<td class="hitlist_content">			
							<t:dataList value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].completeMetadata.formatList}" 
			                            var="item" layout="unorderedList" first="0" dir="LTR">
								<t:outputText value="#{item.schema_f}"></t:outputText>&nbsp;
							</t:dataList>
				</td>
				</tr>
				<tr>
				<td class="hitlist_head">Type:</td>
				<td class="hitlist_content">			
							<t:dataList value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].completeMetadata.typeValueList}" 
			                            var="item" layout="unorderedList" first="0" dir="LTR">
								<t:outputText value="#{item.typeValue}"></t:outputText>&nbsp;
							</t:dataList>
				</td>
				</tr>
				<tr>
				<td class="hitlist_head">Sprache:</td>
				<td class="hitlist_content">			
<%
							//<t:dataList value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].completeMetadata.languageList}" 
			                //            var="item" layout="unorderedList" first="0" dir="LTR">
							//	<t:outputText value="#{item.language}"></t:outputText>&nbsp;
							//</t:dataList>
%>
							<t:dataList value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].trimmedLanguageList}" 
			                            var="item" layout="unorderedList" first="0" dir="LTR">
								<t:outputText value="#{item}"></t:outputText>&nbsp;
							</t:dataList>
				</td>
				</tr>
				<tr>
				<td class="hitlist_head">Identifier:</td>
				<td class="hitlist_content">			
							<t:dataList value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].completeMetadata.identifierList}" 
			                            var="identifier" layout="unorderedList" first="0" dir="LTR">
								<t:outputText value="#{identifier.identifier}"></t:outputText>&nbsp;
							</t:dataList>
				</td>
				</tr>

				<tr>
				<td class="hitlist_head">Volltext:</td>
				<td class="hitlist_content">	
                   <ul><li>		
                   <h:outputLink value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].bestLink}" target="_blank">
                   <t:outputText value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].bestLink}"/></h:outputLink>
<%
                            //<t:dataList value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].completeMetadata.fullTextLinkList}" 
			                //            var="ftl" layout="unorderedList" first="0"	dir="LTR">
							//	<h:outputLink value="#{ftl.url}" target="_blank"><t:outputText value="#{ftl.url}"/></h:outputLink>
							//</t:dataList>

							//<t:div rendered="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].fullTextLinkListSize == 0}">
                              //<h:outputLink value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].bestLink}" target="_blank">
                              //(<t:outputText value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].bestLink}"/>)</h:outputLink>
                            //</t:div>
%>                 
				</td>
				</tr>

				<tr>
				<td class="hitlist_head">Herkunft aus Repository:</td>
				<td class="hitlist_content">		
                  <ul><li>
                  &nbsp;<h:outputLink value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].completeMetadata.repositoryData.repositoryURL}" target="_blank">	
                  <t:outputText value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].completeMetadata.repositoryData.repositoryName}"/>
				  (<t:outputText value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].completeMetadata.repositoryData.repositoryURL}"/>)
  			      </h:outputLink>
                  </li></ul>
				</td>
				</tr>

				<tr>
				<td class="hitlist_head">Ähnlichkeiten:</td>
				<td class="hitlist_content">			
							<t:dataList value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].trimmedDuplicateProbabilityList}" 
			                            var="item" layout="unorderedList" first="0" dir="LTR">								                               
                                <span class="command_link_lupe"><h:commandLink action="#{searchBean.hitlist.mapHitBean[item.referToOID].actionDetailsLink}" title="Metadaten des Objektes betrachten"><img src="../img/16x16_lupe.png"/></h:commandLink></span>
								<h:outputLink value="#{searchBean.hitlist.mapHitBean[item.referToOID].bestLink}" target="_blank">
				                <t:outputText value="#{searchBean.hitlist.mapHitBean[item.referToOID].trimmedTitle}"/>
 					            </h:outputLink>                                
                                (<t:outputText value="#{item.probability}"/>% : <t:outputText value="#{item.reverseProbability}"/>%)
                                <span class="internal_identifier">(OID:<t:outputText value="#{item.referToOID}"/>)</span>
							</t:dataList>
				</td>
				</tr>

				<tr>
				<td class="hitlist_head">OAI/PMH-Record:</td>
				<td class="hitlist_content">
                  <ul><li>
			      &nbsp;<h:outputLink value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].trimmedFullOAIURL}" target="_blank">
				  <t:outputText value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].trimmedFullOAIURL}"/>
 			      </h:outputLink>
                  </li></ul>
				</td>
				</tr>

				<%
				//<tr>
				//<td class="hitlist_head">Metadatensatz:</td>
				//<td class="hitlist_content">			
				//	<h:outputText value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].metadatastring}"/>
				//</td>
				//</tr>
                %>

			</table>

        </div>     

</h:form>


	</body>
</f:view>
</html>
