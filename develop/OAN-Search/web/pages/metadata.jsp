<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t" %>

<html>
<f:view>
<f:loadBundle basename="messages" var="msg"/>

<head>
	<%@ include file="components/header.htm" %>
	<title>Found</title>

</head>
<body>
	<!-- TODO: generell alle Ausgabetexte für Internationalisierung in Language-Dateien auslagern -->
	
	<!-- include link navigation -->	         
  <%@ include file="components/mainnavigation.htm" %>

	<%@ include file="components/flat_category_search.htm" %>
	<%@ include file="components/hitdetails_navi.htm" %>
	<h:form>
		<div id="div_hitdetails">
			<h3><h:outputText value="#{msg.overview_of_meta_data}"/></h3>	
			<span class="internal_identifier">(OID: <h:outputText value="#{searchBean.hitlist.selectedDetailsOID}"/>)</span>
			<table>
				<tr>
					<td>
					<table>
						<tr>
							<td class="hitlist_head"><h:outputText value="#{msg.title}"/>:</td>
							<td class="hitlist_content">
								<t:dataList value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].completeMetadata.titleList}" 
				                            var="titleItem" layout="unorderedList" first="0" dir="LTR" styleClass="title_li">
									<t:outputText value="#{titleItem.title}"/>
								</t:dataList>
							</td>
						</tr>
						<tr>
							<td class="hitlist_head"><h:outputText value="#{msg.authors}"/>:</td>
							<td class="hitlist_content">		
								<t:dataList value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].completeMetadata.authorList}" 
				                            var="author" layout="unorderedList" first="0"	dir="LTR">
									<t:outputText value="#{author.firstname}"></t:outputText>&nbsp;<t:outputText value="#{author.lastname}"></t:outputText>&nbsp;
								</t:dataList>
							</td>
						</tr>
						<tr>
							<td class="hitlist_head"><h:outputText value="#{msg.performers}"/>:</td>
							<td class="hitlist_content">	
								<t:dataList value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].completeMetadata.contributorList}" 
				                            var="contributor" layout="unorderedList" first="0"	dir="LTR">
									<t:outputText value="#{contributor.firstname}"></t:outputText>&nbsp;<t:outputText value="#{contributor.lastname}"></t:outputText>&nbsp;
								</t:dataList>
							</td>
						</tr>
						<tr>
							<td class="hitlist_head"><h:outputText value="#{msg.publisher}"/>/<h:outputText value="#{msg.institution}"/>:</td>
							<td class="hitlist_content">			
								<t:dataList value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].completeMetadata.publisherList}" 
				                            var="publisher" layout="unorderedList" first="0"	dir="LTR">
									<t:outputText value="#{publisher.name}"></t:outputText>&nbsp;
								</t:dataList>
							</td>
						</tr>
						<tr>
							<td class="hitlist_head"><h:outputText value="#{msg.summary}"/>:</td>
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
							<td class="hitlist_head"><h:outputText value="#{msg.classification}"/>:</td>
							<td class="hitlist_content">			
								<t:dataList value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].trimmedClassificationList}" 
				                            var="classif" layout="unorderedList" first="0"	dir="LTR">
									<t:outputText value="#{classif}"></t:outputText>&nbsp;
								</t:dataList>
							</td>
						</tr>
						<tr>
							<td class="hitlist_head"><h:outputText value="#{msg.date}"/>:</td>
							<td class="hitlist_content">			
								<t:dataList value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].completeMetadata.dateValueList}" 
				                            var="item" layout="unorderedList" first="0" dir="LTR">
									<t:outputText value="#{item.dateValue}"></t:outputText>&nbsp;
								</t:dataList>
							</td>
						</tr>
						<tr>
							<td class="hitlist_head"><h:outputText value="#{msg.format}"/>:</td>
							<td class="hitlist_content">			
								<t:dataList value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].completeMetadata.formatList}" 
				                            var="item" layout="unorderedList" first="0" dir="LTR">
									<t:outputText value="#{item.schema_f}"></t:outputText>&nbsp;
								</t:dataList>
							</td>
						</tr>
						<tr>
							<td class="hitlist_head"><h:outputText value="#{msg.type}"/>:</td>
							<td class="hitlist_content">			
								<t:dataList value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].completeMetadata.typeValueList}" 
				                            var="item" layout="unorderedList" first="0" dir="LTR">
									<t:outputText value="#{item.typeValue}"></t:outputText>&nbsp;
								</t:dataList>
							</td>
						</tr>
						<tr>
							<td class="hitlist_head"><h:outputText value="#{msg.language}"/>:</td>
							<td class="hitlist_content">			
								<%	//<t:dataList value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].completeMetadata.languageList}  var="item" layout="unorderedList" first="0" dir="LTR"> <t:outputText value="#{item.language}"></t:outputText>&nbsp;  </t:dataList>
								%>
								<t:dataList value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].trimmedLanguageList}" 
				                            var="item" layout="unorderedList" first="0" dir="LTR">
									<t:outputText value="#{item}"></t:outputText>&nbsp;
								</t:dataList>
							</td>
						</tr>
						<tr>
							<td class="hitlist_head"><h:outputText value="#{msg.identifier}"/>:</td>
							<td class="hitlist_content">			
								<t:dataList value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].completeMetadata.identifierList}" 
				                            var="identifier" layout="unorderedList" first="0" dir="LTR">
									<t:outputText value="#{identifier.identifier}"></t:outputText>&nbsp;
								</t:dataList>
							</td>
						</tr>
			
						<tr>
							<td class="hitlist_head"><h:outputText value="#{msg.full_text}"/>:</td>
							<td class="hitlist_content">	
								<ul><li>		
									<h:outputLink value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].bestLink}" target="_blank">
									<t:outputText value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].bestLink}"/></h:outputLink>
									<% //<t:dataList value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].completeMetadata.fullTextLinkList}" var="ftl" layout="unorderedList" first="0"	dir="LTR"> <h:outputLink value="#{ftl.url}" target="_blank"><t:outputText value="#{ftl.url}"/></h:outputLink> </t:dataList> <t:div rendered="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].fullTextLinkListSize == 0}"> <h:outputLink value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].bestLink}" target="_blank"> (<t:outputText value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].bestLink}"/>)</h:outputLink> </t:div>
									%>
								</li></ul>                
							</td>
						</tr>	
						<tr>
							<td class="hitlist_head"><h:outputText value="#{msg.origin_from_repo}"/>:</td>
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
							<td class="hitlist_head"><h:outputText value="#{msg.similarities}"/>:</td>
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
							<td class="hitlist_head"><h:outputText value="#{msg.oai_pmh_record}"/>:</td>
							<td class="hitlist_content">
								<ul><li>
									&nbsp;<h:outputLink value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].trimmedFullOAIURL}" target="_blank">
									<t:outputText value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].trimmedFullOAIURL}"/>
									</h:outputLink>
								</li></ul>
							</td>
						</tr>
					</table>
					</td>
					<td>
						<t:dataTable value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].filteredKeywordList}" 
				                    var="item"  dir="LTR" columnClasses="KeywordsCol2,KeywordsCol1,KeywordsCol2" >
				    		      <f:facet name="header">
												<h:outputText value="#{msg.keywords}" />
								      </f:facet>
				    		<td class="hitlist_content"><t:column>
				    		<t:outputText value="#{item[0]}"></t:outputText>
					      </t:column></td>
				    		<td class="hitlist_content"><t:column>
										<h:outputLink value="#{item[1]}" target="_blank" title="#{msg.search_at_google}"><img src="../img/google_G.png"/></h:outputLink>
								</t:column></td>
				    		<td class="hitlist_content"><t:column>
										<h:outputLink value="#{item[2]}" target="_blank" title="#{msg.search_at_wiki}"><img src="../img/wikipedia_W.png"/></h:outputLink>
					      </t:column></td>
						</t:dataTable>					
					</td>
				</tr>
			</table>
		</div>
	</h:form>
</body>
</f:view>
</html>
