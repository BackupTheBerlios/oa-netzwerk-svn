<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t" %>


<html>
	<f:view>
	<f:loadBundle basename="messages" var="msg"/>
	
	<head>
		<%@ include file="components/header.htm" %>
		<title>Ergebnisse - Open Access Netzwerk (OAN)</title>
	</head>
	<body>
			<!-- include link navigation -->	         
        	<%@ include file="components/mainnavigation.htm" %>
	
		<div id="div_flat_search">
			<h:form>
				<h:inputText maxlength="2048" size="55" title="OAN-Suche" value="#{searchBean.strOneSlot}"/>
                <span class="span_selected_ddc">Kategorie: <t:outputText value="#{searchBean.browse.selectedDDCCatName}"/>&nbsp;&nbsp;<a href="browse_ddc"><h:outputLabel value="Kategorie ändern" /></a>&nbsp;</span>
				<h:commandButton value="#{msg.find_meta}" action="#{searchBean.actionSearch2Button}"/>&nbsp;
				<h:commandButton value="#{msg.find}" action="#{searchBean.actionSearchButton}"/>&nbsp;
                
				<h:outputLink value="#{searchBean.linkForSearchFeed}" target="_blank" title="Die letzte Suche nach '#{searchBean.strOneSlot}' als RSS-Feed abonieren."><img src="../img/feed-icon-28x28.png"/></h:outputLink> <small>*) Das RSS-Alerting-Feature ist noch in der Beta-Phase.</small>
                <t:div id="div_search_error" rendered='#{searchBean.strErrorLastSearch != ""}'>
					Beim angeschlossenen Suchdienst ist leider ein Fehler aufgetreten:<br />
					<small>(<h:outputText value="#{searchBean.strErrorLastSearch}" />)</small>					
				</t:div>
			</h:form>
		</div>
	


<h:form>

<div id="div_inpagecenter_box">
	<div id="div_hitlist_scroller">
        <h3>Trefferanzeige (<h:outputText value="#{searchBean.hitlist.sizeListHitOID}"/> insgesamt)</h3>
		<t:dataScroller id="scroller_hitlist"
          for="hitlist"
          fastStep="10"
          pageCountVar="pageCount"
          pageIndexVar="pageIndex"
          styleClass="scroller"
          paginator="true"
          paginatorMaxPages="9"
          paginatorTableClass="paginator"
          paginatorActiveColumnStyle="font-weight:bold;">
          <f:facet name="previous">
			<t:outputText value="-"/>
          </f:facet>
          <f:facet name="next">
			<t:outputText value="+"/>
          </f:facet>
          <f:facet name="fastforward">
			<t:outputText value="++"/>
          </f:facet>
          <f:facet name="fastrewind">
			<t:outputText value="--"/>
          </f:facet>
        </t:dataScroller>
	</div>
		<div id="div_hitlist">

            <h:dataTable id="hitlist" value="#{searchBean.hitlist.listHitOID}" var="hitOID" columnClasses="colHit" rows="10">
				<h:column>
                        <div class="div_hit_header">
						  <span class="list-identifier"><h:outputLink value="#{searchBean.hitlist.mapHitBean[hitOID].bestLink}" target="_blank">
						  <t:outputText value="#{searchBean.hitlist.mapHitBean[hitOID].trimmedTitle}"/></h:outputLink></span>&nbsp;
                          <span class="command_link_lupe"><h:commandLink action="#{searchBean.hitlist.mapHitBean[hitOID].actionDetailsLink}" title="Metadaten des Objektes betrachten"><img src="../img/16x16_lupe.png"/></h:commandLink></span>
                          <span class="command_link_plus"><h:commandLink action="#{searchBean.hitlist.mapHitBean[hitOID].actionMerkenLink}" title="Objekt zur Merkliste hinzufügen"><img src="../img/16x16_plus.png"/></h:commandLink></span>
							<h:graphicImage url="../img/flags/#{searchBean.hitlist.mapHitBean[hitOID].flagIMG}" alt="#{searchBean.hitlist.mapHitBean[hitOID].flagALT}" title="#{searchBean.hitlist.mapHitBean[hitOID].flagALT}"/>
                        </div>						
                        <div class="div_hit_overview"><nosp>
                          <span class="hit-creators"><t:outputText value="#{searchBean.hitlist.mapHitBean[hitOID].trimmedCreators}"/> (<t:outputText value="#{searchBean.hitlist.mapHitBean[hitOID].trimmedDate}"/>):</span>
                          <span class="hit-keywords"><t:outputText value="#{searchBean.hitlist.mapHitBean[hitOID].trimmedKeywords}"/></span><br/>
						  <span class="hit-url"><t:outputText value="#{searchBean.hitlist.mapHitBean[hitOID].bestLink}"/></span><br/>
						  <span class="hit-url">							
						  <% /* <h:graphicImage url="#{searchBean.hitlist.mapHitBean[hitOID].urlIRIcon}" alt="#{searchBean.hitlist.mapHitBean[hitOID].completeMetadata.repositoryData.repositoryName}" title="#{searchBean.hitlist.mapHitBean[hitOID].completeMetadata.repositoryData.repositoryName}" width="16" height="16"/> */ %>
                          <span class="command_link_bibtex"><h:outputLink value="http://oanet.cms.hu-berlin.de/bibexport?type=bibtex&OIDs=#{hitOID}" title="Bibtex-Eintrag des Objekts anzeigen">[bibtex]</h:outputLink></span>&nbsp;
						  <h:outputLink value="#{searchBean.hitlist.mapHitBean[hitOID].completeMetadata.repositoryData.repositoryURL}" target="_blank">	
                  <t:outputText value="#{searchBean.hitlist.mapHitBean[hitOID].completeMetadata.repositoryData.repositoryName}"/>
				  (<t:outputText value="#{searchBean.hitlist.mapHitBean[hitOID].completeMetadata.repositoryData.repositoryURL}"/>)
  			      </h:outputLink></span><br/>	
						  <span class="internal_identifier">(OID: <h:outputText value="#{searchBean.hitlist.mapHitBean[hitOID].completeMetadata.oid}"/>)</span>						
						</div>
				</h:column>
			</h:dataTable>
        </div>     

        <span class="command_link_bibtex"><h:commandLink action="#{searchBean.actionAddAllHitsToClipboard}" title="Alle #{searchBean.hitlist.sizeListHitOID} Treffer zur Merkliste hinzufügen">[alle Treffer zur Merkliste hinzufügen]</h:commandLink> (Achtung, dieser Vorgang kann bisweilen recht lange dauern!)</span>
</div>

<t:div rendered="#{searchBean.hitlist.sizeListClipboardOID > 0}">
<div id="div_inpageright_box">
        <div id="div_clipboard_header">
            <span class="command_link">
               <h:commandLink action="show_clipboard" title="Merkzettel öffnen"><img src="../img/16x16_maximize.png" /></h:commandLink>
            </span>
            <h:commandLink action="show_clipboard" title="Merkzettel öffnen">
            Merkzettel
            </h:commandLink>
        </div>
        <div id="div_clipboard_overview">
            <t:dataList id="clipboard_overview" value="#{searchBean.hitlist.listClipboardOID}" var="clipboardOID" layout="unorderedList" first="0" dir="LTR">
               <span class="command_link_lupe">
               <h:commandLink action="#{searchBean.hitlist.mapHitBean[clipboardOID].actionDetailsLink}" title="Metadaten des Objektes betrachten"><img src="../img/16x16_lupe.png"/></h:commandLink>
               </span>
               <span class="command_link_minus">
               <h:commandLink action="#{searchBean.hitlist.mapHitBean[clipboardOID].actionVerwerfenLink}" title="Objekt aus der Merkliste entfernen"><img src="../img/16x16_minus.png"/></h:commandLink>
               </span>		
			   <span class="details_link">
			   <h:outputLink value="#{searchBean.hitlist.mapHitBean[clipboardOID].bestLink}" target="_blank" title="#{searchBean.hitlist.mapHitBean[clipboardOID].trimmedTitle}">
               <t:outputText value="#{searchBean.hitlist.mapHitBean[clipboardOID].trimmedClipboardTitle}"/>
               </h:outputLink>
               </span>
               <% // <span class="internal_identifier">(OID: <h:outputText value="#{clipboardOID}"/>)</span> %>
			</t:dataList>
        </div>
        <div id="div_clipboard_footer">
            <span class="command_link">
               <h:outputLink value="http://oanet.cms.hu-berlin.de/bibexport?type=bibtex&OIDs=#{searchBean.hitlist.kommaSepClipboardOIDs}" title="Export des Merkzettels als Bibtex-Datei" target="_blank">[bibtex]</h:outputLink>
            </span>
            <span class="command_link">
               <h:commandLink action="clipboard_export_htmllist" title="Export des Merkzettels als HTML-Seite" target="_blank">[plain html links]</h:commandLink>
            </span>
        </div>

</div>
</t:div>

</h:form>

	</body>
</f:view>
</html>