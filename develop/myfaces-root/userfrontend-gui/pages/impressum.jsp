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
			</h:form>
            </div>
			
			
<div id="div_impressum_main">
<h2>Impressum</h2>
<div class="halfbox">
<h3>Administration und Gestaltung</h3>
<p>Für die Gestaltung der Seiten ist das OA-Netzwerk-Projektteam zuständig. Betreuung der Hard- und Software der Plattform, insbesondere der Metadatenspeicher und die Nutzer-Oberflächen, findet am Computer- und Medienservice der Humboldt-Universität zu Berlin statt. Der verwendete Such-Dienst mit zugrunde liegendem Metadaten- und Volltext-Index sowie einige der Back-End-Dienste werden an der Universität Osnabrück bereit gestellt.</p>  
<h3>Impressum nach § 6 Teledienstgesetz</h3>
<p>Humboldt-Universität zu Berlin<br/>
Unter den Linden 6<br/>
10099 Berlin<br/>
Tel: +49 30 2093 - 0 (Zentrale)<br/>
Fax: + 49 30 2093 - 2770 (Zentrale)<br/>
<br/>
Die Humboldt-Universität ist eine Einrichtung des Öffentlichen Rechts. Sie wird vertreten durch ihren Präsidenten.<br/>
<br/>
Umsatzsteueridentifikationsnummer: DE 137176824<br/>
</p>
</div>
<div class="halfbox">
<h3>Haftungsausschluss</h3>
<p>Die veröffentlichten Informationen sind sorgfältig zusammengestellt, erheben aber keinen Anspruch auf Aktualität, sachliche Korrektheit oder Vollständigkeit; eine entsprechende Gewähr wird nicht übernommen. Alle kostenfreien Angebote sind unverbindlich. Die zuständigen Betreuer behalten sich vor, jederzeit ohne vorherige Ankündigung das Angebot oder Teile davon zu verändern, zu ergänzen oder zu löschen.</p>
<p>Der Computer- und Medienservice der Humboldt-Universität zu Berlin, als Betreuer der Plattform, ist nicht für Inhalte fremder Seiten verantwortlich, die über einen Link erreicht werden. Die veröffentlichten Links sind Teil der aggregierten Metadaten oder werden mit größtmöglicher Sorgfalt recherchiert und zusammengestellt. Die Betreiber haben keinen Einfluss auf die aktuelle und zukünftige Gestaltung und die Inhalte der dadurch verlinkten Seiten. Für illegale, fehlerhafte oder unvollständige Inhalte sowie für Schäden, die durch die Nutzung oder Nichtnutzung der Informationen entstehen, haftet allein der Anbieter der Web-Site, auf die verwiesen wurde. Die Haftung desjenigen, der lediglich auf die Veröffentlichung durch einen Link hinweist, ist ausgeschlossen.</p>
<p>Die Urheberrechte der auf diesem Server veröffentlichten Dokumente liegen bei den jeweiligen Autoren. Bei der Gestaltung der Webseiten sind die Betreiber bemüht, entweder selbst erstellte Texte und Grafiken zu nutzen oder auf lizenzfreies Material zurückzugreifen. Alle innerhalb des Internetangebotes genannten und ggf. durch Dritte geschützten Marken- und Warenzeichen unterliegen uneingeschränkt den Bestimmungen des jeweils gültigen Kennzeichenrechts und den Besitzrechten der jeweiligen eingetragenen Eigentümer.</p>
</div>
</div>

		</body>
	</f:view>
</html>
