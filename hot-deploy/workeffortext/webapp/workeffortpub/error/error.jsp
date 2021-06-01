<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="org.ofbiz.base.util.*"%>
<%@ page import="org.ofbiz.base.util.collections.*"%>
<%@ page import="com.mapsengineering.base.util.*"%>
<%
    java.util.Locale locale = UtilHttp.getLocale(request);
    ResourceBundleMapWrapper uiLabelMap = MessageUtil.loadPropertyMap("BaseUiLabels", null, locale);
    String title = (String)uiLabelMap.get("BaseError");
    String errorMsg = (String)request.getAttribute("_ERROR_MESSAGE_");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title><%=title%></title>
<link rel="shortcut icon" href="/theme_gplus/ofbiz.ico"/>
</head>
<body style="background-color: #FFFFFF; font-family: sans-serif;">
	<p style="background-color: #CC6666; color: #FFFFFF; text-align: center; font-weight: bold; font-size: 1.5em;">
		<%=title.toUpperCase()%>
	</p>
	<p>
		<%=UtilFormatOut.replaceString(errorMsg, "\n", "<br/>")%>
	</p>
</body>
</html>
