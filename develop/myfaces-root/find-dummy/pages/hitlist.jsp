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
	

<h:form>
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
			<t:outputText value="zurück"/>
          </f:facet>
          <f:facet name="next">
			<t:outputText value="vorwärts"/>
          </f:facet>
          <f:facet name="fastforward">
			<t:outputText value=">>"/>
          </f:facet>
          <f:facet name="fastrewind">
			<t:outputText value="<<"/>
          </f:facet>
        </t:dataScroller>
	</div>
		<div id="div_hitlist">

            <h:dataTable id="hitlist" value="#{searchBean.hitlist.listHitOID}" var="hitOID" columnClasses="colHit" rows="4">
				<h:column>
						<span class="list-identifier"><h:outputLink value="#{searchBean.hitlist.mapHitBean[hitOID].bestLink}">
						  <t:outputText value="#{searchBean.hitlist.mapHitBean[hitOID].trimmedTitle}"/></h:outputLink></span>&nbsp;<span class="details_link"><h:commandLink action="#{searchBean.hitlist.mapHitBean[hitOID].actionDetailsLink}">[Details anzeigen]</h:commandLink></span>
						<div id="div_hit_overview"><nosp>
                          <span class="hit-creators"><t:outputText value="#{searchBean.hitlist.mapHitBean[hitOID].trimmedCreators}"/></span><br/>
						  <span class="hit-keywords"><t:outputText value="#{searchBean.hitlist.mapHitBean[hitOID].trimmedKeywords}"/></span><br/>
						  <span class="hit-url"><t:outputText value="#{searchBean.hitlist.mapHitBean[hitOID].bestLink}"/></span><br/>
						  <span class="internal_identifier">(OID: <h:outputText value="#{searchBean.hitlist.mapHitBean[hitOID].completeMetadata.oid}"/>)</span>						
						</div>
				</h:column>
			</h:dataTable>
        </div>     

</h:form>

	</body>
</f:view>
</html>
