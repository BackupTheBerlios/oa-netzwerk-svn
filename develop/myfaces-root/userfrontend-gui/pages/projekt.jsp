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
			
<div id="div_project_main">
<h2>Projekt</h2>		
<h3>Überblick</h3>
<p><br/><img src="../img/Logo_oan_rgb_micro.png"/><br/><br/></p>				
<p>Das „Open-Access-Netzwerk“ (OA-Netzwerk) zielt auf eine verstärkte Vernetzung von Repositorien, um den deutschen Forschungsbeitrag national und international sichtbarer zu machen. Hintergrund ist, dass digitale Sammlungen im Hinblick auf Wahrnehmung und Nutzung erst in einer organisatorisch und technisch vernetzten Umgebung ihre optimale Wirkung entfalten.</p>
<p>Initiiert von der Deutschen Initiative für Netzwerkinformation e.V. (DINI), fördert die Deutsche Forschungsgemeinschaft (DFG) das Projekt. OA-Netzwerk bildet das Dach des virtuellen Projektverbundes für „OA-Statistik“ sowie „OA-Citation“, die zugehörige Dienste wie Nutzungsmessungen und Zitationsanalysen entwickeln werden.</p>
<h4>Projektseiten bei DINI</h4>
<p><a href="http://www.dini.de/projekte/oa-netzwerk/" target="_blank">http://www.dini.de/projekte/oa-netzwerk/</a></p>
<h3>Kontakt</h3>
<ul>
<li>
<h4>Ansprechpartner der Humboldt Universität zu Berlin</h4>
<p><b>Prof. Dr. Peter Schirmbacher</b><br/>
Direktor des <a href="http://www.hu-berlin.de/rz/" target="_blank">Computer- und Medienservice</a><br />
der <a href="http://www.hu-berlin.de/" target="_blank">Humboldt-Universität zu Berlin</a><br />
Email:&nbsp;schirmbacher(at)cms.hu-berlin.de</a><br />
Telefon: +49 30 2093-7010</p>
</li>
<li>
<h4>Ansprechpartner der SUB Göttingen</h4>
<p><b>Dr. Birgit Schmidt</b><br/>
Niedersächsische Staats- und Universitätsbibliothek Göttingen<br />
Elektronisches Publizieren<br />
Email:&nbsp;bschmidt(at)sub.uni-goettingen.de<br />
Telefon: +49 551 39 5228</p>
</li>
<li>
<h4>Ansprechpartner in der Universität Osnabrück</h4>
<p><b>Dr. Judith Plümer</b><br/>
Arbeitsgruppe Mathematische Fachinformation<br />
Fachbereich Mathematik/Informatik<br />
Email:&nbsp;judith.pluemer(at)uos.de<br />
Telefon: +49 541 969-2526</p>
</li>
</ul>			
</div>

		</body>
	</f:view>
</html>
