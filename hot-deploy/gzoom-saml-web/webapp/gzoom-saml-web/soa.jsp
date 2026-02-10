<%@page import="com.onelogin.saml2.Auth"%>
<%@page import="java.util.Collection"%>
<%@page import="java.util.Enumeration"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Base64"%>
<%@page import="org.apache.commons.lang3.StringUtils" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<script>
function loginOnSoa(user, fiscalCode, hrefUrl) {
	var form = document.createElement("form");
	var inputAccount = document.createElement("input");
	var inputFiscalCode = document.createElement("input");
	
	form.action = hrefUrl;
	form.method = "post";
	
	inputAccount.type = "hidden";
	inputAccount.name = "saml_account";
	inputAccount.value = user;
	
	inputFiscalCode.type = "hidden";
	inputFiscalCode.name = "saml_fiscalcode";
	inputFiscalCode.value = fiscalCode;
	
	form.appendChild(inputAccount);
	form.appendChild(inputFiscalCode);
	document.body.appendChild(form);
	form.submit();
};
</script>
<head>
	 <meta charset="utf-8">
	 <meta http-equiv="X-UA-Compatible" content="IE=edge">
     <meta name="viewport" content="width=device-width, initial-scale=1">
	 <title>GZOOM SAML App</title>
	 <link rel="stylesheet" href="//maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css">
     <%
		 	// Recupera gli attributi SAML dalla sessione
		 	@SuppressWarnings("unchecked")
		 	Map<String, List<String>> attributes = (Map<String, List<String>>) session.getAttribute("attributes");
		 	
		 	// Estrae matricola dal NameID
		 	String matricola = (String) session.getAttribute("nameId");
		 	if (matricola == null || matricola.trim().isEmpty()) {
		 		// Fallback: prova a leggere dall'attributo matricola
		 		if (attributes != null && attributes.containsKey("matricola")) {
		 			List<String> matricolaList = attributes.get("matricola");
		 			if (matricolaList != null && !matricolaList.isEmpty()) {
		 				matricola = matricolaList.get(0);
		 			}
		 		}
		 	}
		 	
		 	// Estrae fiscalCode dagli attributi
		 	String fiscalCode = "";
		 	if (attributes != null && attributes.containsKey("fiscalCode")) {
		 		List<String> fiscalCodeList = attributes.get("fiscalCode");
		 		if (fiscalCodeList != null && !fiscalCodeList.isEmpty()) {
		 			fiscalCode = fiscalCodeList.get(0);
		 		}
		 	}
		 	
		 	// URL di ritorno
		    String returnUrl = (String) session.getAttribute("soaReturnUrl");
		    
		    // Log per debug
		    System.out.println("soa.jsp - matricola: " + matricola);
		    System.out.println("soa.jsp - fiscalCode: " + fiscalCode);
		    System.out.println("soa.jsp - returnUrl: " + returnUrl);
	 %>
</head>
<body onload="loginOnSoa('<%=matricola%>', '<%=fiscalCode%>', '<%=returnUrl%>');">
	<div class="container">
		<h1>GZOOM SAML - Redirecting to Application...</h1>
		<p>Please wait...</p>
	</div>
</body>
</html>

