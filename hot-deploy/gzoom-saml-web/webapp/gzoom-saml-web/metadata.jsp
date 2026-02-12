<%@page import="java.util.*,com.onelogin.saml2.Auth,com.onelogin.saml2.settings.Saml2Settings" language="java" contentType="application/xhtml+xml"%><%
request.setAttribute("req","gzoom");
Auth auth = com.maps.saml.util.AuthWrapper.getAuth(request, response);
Saml2Settings settings = auth.getSettings();
String metadata = settings.getSPMetadata();
List<String> errors = Saml2Settings.validateMetadata(metadata);
if (errors.isEmpty()) {
	out.println(metadata);
} else {
	response.setContentType("text/html; charset=UTF-8");

	for (String error : errors) {
	    out.println("<p>"+error+"</p>");
	}
}%>