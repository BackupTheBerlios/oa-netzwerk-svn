<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<f:loadBundle basename="admin.login" var="loginmsg"/>
<%
if (session.isNew ( )) {
	
	String referer = request.getHeader ("Referer");
	
	if (referer == null) {
	
		response.sendRedirect ("pages/mainAdmin.faces");
		
	} else {
	
		response.sendRedirect (referer);
	}
}
%>

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
		<title></title>
	</head>
	<body>
		<f:view>
		<!--form method="POST" action="<%= response.encodeURL("j_security_check")%>"-->
		<form method="POST" action="j_security_check">
			<h:outputText value="#{loginmsg.username}"/><input type="text" name="j_username"/><br/>
			<h:outputText value="#{loginmsg.password}"/><input type="password" name="j_password"/><br/>
			<input type="submit" value="${loginmsg.login}"/>
			<input type="reset" value="${loginmsg.reset}"/>
		</form>
		</f:view>
	</body>
</html>

