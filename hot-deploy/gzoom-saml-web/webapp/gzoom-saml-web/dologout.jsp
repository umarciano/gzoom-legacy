<%@page import="com.onelogin.saml2.Auth"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
</head>
<body>
	<%
		Auth auth =   com.maps.saml.util.AuthWrapper.getAuth(request, response);
		//auth.logout();
		String nameId = null;
		String nameIdFormat = null;
		String sessionIndex = null;

		//Auth auth = new Auth("saml.properties.gzoom",request, response);
		//String nameId = null;
		if (session.getAttribute("nameId") != null) {
			nameId = session.getAttribute("nameId").toString();
		}
		//String nameIdFormat = null;
		if (session.getAttribute("nameIdFormat") != null) {
			nameIdFormat = session.getAttribute("nameIdFormat").toString();
		}
		String nameidNameQualifier = null;
		if (session.getAttribute("nameidNameQualifier") != null) {
			nameidNameQualifier = session.getAttribute("nameidNameQualifier").toString();
		}
		String nameidSPNameQualifier = null;
		if (session.getAttribute("nameidSPNameQualifier") != null) {
			nameidSPNameQualifier = session.getAttribute("nameidSPNameQualifier").toString();
		}
		//String sessionIndex = null;
		if (session.getAttribute("sessionIndex") != null) {
			sessionIndex = session.getAttribute("sessionIndex").toString();
		}
		//auth.logout(null, nameId, sessionIndex, nameIdFormat, null, null);
		auth.logout(null, nameId, sessionIndex, nameIdFormat);
	%>
</body>
</html>
