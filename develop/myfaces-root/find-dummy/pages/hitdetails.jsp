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
			</h:form>
            </div>
	
		<div id="div_flat_search">
			<h:form>
				<h:inputText maxlength="2048" size="55" title="OAN-Suche" value="#{searchBean.strOneSlot}"/>
				<h:commandButton value="#{index.find}" action="#{searchBean.actionSearchButton}"/>
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
			                            var="titleItem" layout="unorderedList" first="0"	dir="LTR">
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
					<h:outputText value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].mergedKeywords}" />
				</td>
				</tr>
				<tr>
				<td class="hitlist_head">Klassifikation:</td>
				<td class="hitlist_content">			
							<t:dataList value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].completeMetadata.classificationList}" 
			                            var="classif" layout="unorderedList" first="0"	dir="LTR">
								<t:outputText value="#{classif.value}"></t:outputText>&nbsp;
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
							<t:dataList value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].completeMetadata.languageList}" 
			                            var="item" layout="unorderedList" first="0" dir="LTR">
								<t:outputText value="#{item.language}"></t:outputText>&nbsp;
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
							<t:dataList value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].completeMetadata.fullTextLinkList}" 
			                            var="ftl" layout="unorderedList" first="0"	dir="LTR">
								<h:outputLink value="#{ftl.url}" target="_blank"><t:outputText value="#{ftl.url}"/></h:outputLink>
							</t:dataList>
							<t:div rendered="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].fullTextLinkListSize == 0}">
                              <h:outputLink value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].bestLink}" target="_blank">
                              (<t:outputText value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].bestLink}"/>)</h:outputLink>
                            </t:div>
				</td>
				</tr>

				<tr>
				<td class="hitlist_head">Herkunft aus Repository:</td>
				<td class="hitlist_content">		
                  <h:outputLink value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].completeMetadata.repositoryData.repositoryURL}" target="_blank">	
                  <t:outputText value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].completeMetadata.repositoryData.repositoryName}"/>
				  (<t:outputText value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].completeMetadata.repositoryData.repositoryURL}"/>)
  			      </h:outputLink>
				</td>
				</tr>
				<tr>
				<td class="hitlist_head">originaler OAI/PMH-Record:</td>
				<td class="hitlist_content">
			      <h:outputLink value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].trimmedFullOAIURL}" target="_blank">
				  <t:outputText value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].trimmedFullOAIURL}"/>
 			      </h:outputLink>
				</td>
				</tr>

				<tr>
				<td class="hitlist_head">Ähnlichkeiten:</td>
				<td class="hitlist_content">			
							<t:dataList value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].completeMetadata.duplicateProbabilityList}" 
			                            var="item" layout="unorderedList" first="0" dir="LTR">
								OID:<t:outputText value="#{item.referToOID}"/> (<t:outputText value="#{item.probability}"/>%)
								<h:outputLink value="#{searchBean.hitlist.mapHitBean[item.referToOID].bestLink}" target="_blank">
				                <t:outputText value="#{searchBean.hitlist.mapHitBean[item.referToOID].trimmedTitle}"/>
 					            </h:outputLink>
                                <span class="command_link_lupe"><h:commandLink action="#{searchBean.hitlist.mapHitBean[item.referToOID].actionDetailsLink}" title="Metadaten des Objektes betrachten"><img src="../img/16x16_lupe.png"/></h:commandLink></span>
							</t:dataList>
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
