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
	<!-- TODO: nach Testphase in CSS-Datei auslagern -->
	<style type="text/css">
	
		.colStyle1 { 
			width: 450px;
			vertical-align:top;
		}
		.colStyle2 { 
			width: 750px;
			vertical-align:top;
		}
	</style>
</head>
<body>
	<!-- TODO: generell alle Ausgabetexte für Internationalisierung in Language-Dateien auslagern -->
	
	<!-- include link navigation -->	         
  <%@ include file="components/mainnavigation.htm" %>

	<h:form>
		<t:panelGrid columns="2" columnClasses="colStyle1,colStyle2">
		<t:panelGroup >
			<div id="div_simplebrowselist">
					<h3>Liste der Fachgebiete nach Dewey-Dezimalklassifikation (DDC)</h3>
			        <div class="div_howto">
			            W&auml;hlen Sie eine Kategorie aus, auf die Sie die Suche starten wollen!
			        </div>	
					<% /* 
						<div id="div_ddc_breadcrump">
			            PFAD:
			            <t:dataList id="ddc_breadcrump" value="#{searchBean.browse.pathDDCCategories}" var="item" layout="simple">
							<t:outputText value="#{item}"/> &gt; 
						</t:dataList>
						</div>	
					*/ %>
		            <div id="div_alle_dcc_cat" >
		            	<h:commandLink action="#{searchBean.browse.actionUnselectDDCCategoryLink}" title=""><span class="span_ddc_name"><t:outputText value="Alle Kategorien (Keine Einschränkung)"/></span></h:commandLink>
		            </div>                
		            <t:dataList id="ddcnavilist_lvl1" value="#{searchBean.browse.listDDCNaviNodes}" var="node_lvl1" layout="unorderedList">	
		                <t:div rendered="#{node_lvl1.inPath}">
												<div class="selected_category_wrapper">
													<span class="span_ddc_num"><t:outputText value="#{node_lvl1.strDDCValue}"/></span>&nbsp;
													<span class="span_ddc_selected_name"><t:outputText value="#{node_lvl1.strNameDE}"/></span>			
												</div>
		                </t:div>	            
		                <t:div rendered="#{!node_lvl1.inPath}">
											<span class="span_ddc_num"><t:outputText value="#{node_lvl1.strDDCValue}"/></span>&nbsp;
											<h:commandLink action="#{node_lvl1.actionSelectDDCCategoryLink}" title=""><span class="span_ddc_name"><t:outputText value="#{node_lvl1.strNameDE}"/></span></h:commandLink>&nbsp;
		                </t:div>
		                <t:div rendered="#{node_lvl1.inPath}">
					            <t:dataList id="ddcnavilist_lvl2" value="#{node_lvl1.listSubnodes}" var="node_lvl2" layout="unorderedList">	
				                    <t:div rendered="#{node_lvl2.inPath}">
									   					<div class="selected_category_wrapper">
																<span class="span_ddc_num"><t:outputText value="#{node_lvl2.strDDCValue}"/></span>&nbsp;
																<span class="span_ddc_selected_name"><t:outputText value="#{node_lvl2.strNameDE}"/></span>
																	<br/><h:commandLink value=" Suche starten" action="#{searchBean.actionSearchFromDDCTreeDirectlyWithAutoHits}"/>	
															</div>
														</t:div>
				                    <t:div rendered="#{!node_lvl2.inPath}">
															<span class="span_ddc_num"><t:outputText value="#{node_lvl2.strDDCValue}"/></span>&nbsp;
															<h:commandLink action="#{node_lvl2.actionSelectDDCCategoryLink}" title=""><span class="span_ddc_name"><t:outputText value="#{node_lvl2.strNameDE}"/></span></h:commandLink>
														</t:div>	
											</t:dataList>
										</t:div>
								</t:dataList>			
		     </div>
		     </t:panelGroup>
		     <t:panelGroup>
				<div id="div_inpagecenter_box_big">			
					<!-- include hitlist components -->
			        <%@ include file="components/hitlist_scroller.htm" %>
			        <%@ include file="components/hitlist_small.htm" %>
				</div>
				</t:panelGroup>
				  <f:facet name="footer">
		    <t:panelGroup>

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
						<%	//<t:dataList value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].completeMetadata.languageList}  var="item" layout="unorderedList" first="0" dir="LTR"> <t:outputText value="#{item.language}"></t:outputText>&nbsp;  </t:dataList>
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
							<% //<t:dataList value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].completeMetadata.fullTextLinkList}" var="ftl" layout="unorderedList" first="0"	dir="LTR"> <h:outputLink value="#{ftl.url}" target="_blank"><t:outputText value="#{ftl.url}"/></h:outputLink> </t:dataList> <t:div rendered="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].fullTextLinkListSize == 0}"> <h:outputLink value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].bestLink}" target="_blank"> (<t:outputText value="#{searchBean.hitlist.mapHitBean[searchBean.hitlist.selectedDetailsOID].bestLink}"/>)</h:outputLink> </t:div>
							%>
						</li></ul>                
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
		    </t:panelGroup>
  </f:facet>
				
			</t:panelGrid> 	
	</h:form>
</body>
</f:view>
</html>
