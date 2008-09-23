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
			<h:form>
			<div id="general_links">
				<h:commandLink value="#{index.linkname_start}" action="start"/>&nbsp;
                <h:commandLink value="#{index.linkname_projekt}" action="test1"/>&nbsp;
                <h:commandLink value="#{index.linkname_impressum}" action="test2"/>&nbsp; 
            </div>
			<div id="flat_search">
				<h:inputText maxlength="2048" size="55" title="OAN-Suche" value="#{searchParam.strOneSlot}"/>
				<h:commandButton value="#{index.find}" action="#{searchParam.actionSearchButton}"/>
			</div>

		<h1><h:outputText value="#{searchParam.strOneSlot}"/></h1>

        <h3>Trefferanzeige 1 bis 2 (1000 insgesamt)</h3>
        <dl>
           <dt>1.   <span class="list-identifier"><a href="foo" title="Abstract">OAN:123453546</a> [<a href="foo" title="Download PostScript">ps</a>, <a href="foo" title="Download PDF">pdf</a>, <a href="foo" title="Other formats">other</a>]</span></dt>
           <dd>
              <span class="maintitle">Test-Titel</span>
              <div class="list-authors">
                <a href="foo">Joseph M. Hahn</a>, 
                <a href="foo">Renu Malhotra</a>
              </div>
              <div class="list-dubpro">
                Duplikat mit OID <a href="foo">123</a> zu 99%,<br/>
                Duplikat mit OID <a href="foo">456</a> zu 79%
              </div>
           </dd>
           <dt>2.   <span class="list-identifier"><a href="foo" title="Abstract">OAN:123453546</a> [<a href="foo" title="Download PostScript">ps</a>, <a href="foo" title="Download PDF">pdf</a>, <a href="foo" title="Other formats">other</a>]</span></dt>
           <dd>
              <span class="maintitle">Test-Titel</span>
              <div class="list-authors">
                <a href="foo">Joseph M. Hahn</a>, 
                <a href="foo">Renu Malhotra</a>
              </div>
              <div class="list-dubpro">
                Duplikat mit OID <a href="foo">123</a> zu 99%,<br/>
                Duplikat mit OID <a href="foo">456</a> zu 79%
              </div>
           </dd>
       </dl>
       
			</h:form>

	</body>
</f:view>
</html>
