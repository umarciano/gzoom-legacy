package com.mapsengineering.base.filter;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.ofbiz.base.util.UtilProperties;
import com.mapsengineering.base.authentication.OfbizHttpBasicAuth;

public class SamlFilter    implements Filter {

	private boolean isSamlEnabled;
	private String serviceProviderUrl;
	Base64.Decoder dec = Base64.getDecoder();

	public void init(FilterConfig filterConfig) throws ServletException {
		isSamlEnabled = false;
		if ("true".equals(UtilProperties.getPropertyValue("security.properties", "security.saml.enable"))) {
			isSamlEnabled = true;
		}
		serviceProviderUrl = UtilProperties.getPropertyValue("security.properties", "security.saml.serviceProvider");
		org.ofbiz.base.util.Debug.logInfo("SAML ENABLED: " + isSamlEnabled, SamlFilter.class.getName());
		org.ofbiz.base.util.Debug.logInfo("Service Provider URL: " + serviceProviderUrl, SamlFilter.class.getName());
		org.ofbiz.base.util.Debug.logInfo("Populating exclude list.... ", SamlFilter.class.getName());
		/*
		excludes.add("soa/images");
		excludes.add("soa/error");
		excludes.add("soa/birt");
		excludes.add("soa/ftl");
		excludes.add("soa/style");
		excludes.add("soa/index");
		*/
		excludes.add("rest");
		excludes.add("accountingext");
		excludes.add("bscperf");
		excludes.add("cdgperf");
		excludes.add("commondataext");
		excludes.add("corperf");
		excludes.add("custom");
		excludes.add("dirigperf");
		excludes.add("emplperf");
		excludes.add("gdprperf");
		excludes.add("humanresext");
		excludes.add("orgperf");
		excludes.add("partperf");
		excludes.add("partyext");
		excludes.add("procperf");
		excludes.add("productext");
		excludes.add("rendperf");
		excludes.add("stratperf");
		excludes.add("trasperf");
		excludes.add("workeffortext");
		excludes.add("theme_gplus");
		excludes.add("images");
		excludes.add("resources");
		excludes.add("gzbox");
		excludes.add("gzope");
		//excludes.add("webtools");
		
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {	
		//org.ofbiz.base.util.Debug.logInfo("*** SamFilter request : " + getRequestUrl(((HttpServletRequest)request)) + " ***", SamlFilter.class.getName());
		if (isSamlEnabled) {
			final HttpServletRequest httpRequest = (HttpServletRequest)request;
			if (!OfbizHttpBasicAuth.isUserLoggedIn(httpRequest)) {
				//serve per evitare che faccia 2 saml login consecutive
				if(((HttpServletRequest)request).getRequestURI().indexOf("main") > 0){
					//fix per parametri GET
					String parametersGet = null;
					Map <String, Object> paramMap = httpRequest.getParameterMap();
					if (paramMap!=null && !paramMap.isEmpty()){
						parametersGet = "?";
						//Set<String> keys = paramMap.keySet();
						for (String key : paramMap.keySet()){
							String[] valueArr=(String[])paramMap.get(key);
							for (int i=0; i<valueArr.length; i++){
								parametersGet=parametersGet.concat(key);
								parametersGet=parametersGet.concat("=");
								parametersGet=parametersGet.concat(valueArr[i]);
								parametersGet=parametersGet.concat("&");
							}
						}
						parametersGet=parametersGet.substring(0, parametersGet.length() - 1); //tolgo l'ultima &
						parametersGet = URLEncoder.encode(parametersGet,"UTF-8");
					}
					org.ofbiz.base.util.Debug.logInfo("SAML ENABLED --> verifying uri for exclusion......" + httpRequest.getRequestURI() , SamlFilter.class.getName());
					if (!exclude(httpRequest)) {
						org.ofbiz.base.util.Debug.logInfo("SAML ENABLED --> request to be processed", SamlFilter.class.getName());
						if (request.getParameter("saml_account")!=null) {
							org.ofbiz.base.util.Debug.logInfo("SAML ENABLED --> got saml_account: " + request.getParameter("saml_account"), SamlFilter.class.getName());
							String up = request.getParameter("saml_account");
							up = new String(dec.decode(up));
							SamlHttpRequest principal= new SamlHttpRequest(httpRequest,up);
							chain.doFilter( principal , response);
							return;
						}
						else {
							if (parametersGet!=null && !parametersGet.isEmpty()){
								org.ofbiz.base.util.Debug.logInfo("SAML ENABLED --> sending redirect to Service Provider with get parameters......" + serviceProviderUrl + "?returnUrl="+ httpRequest.getRequestURI()+parametersGet, SamlFilter.class.getName());
								((HttpServletResponse)response).sendRedirect(  serviceProviderUrl + "?returnUrl="+ httpRequest.getRequestURI()+parametersGet); //+ request get parameter ri encodati
							}
							else{
								org.ofbiz.base.util.Debug.logInfo("SAML ENABLED --> sending redirect to Service Provider......" + serviceProviderUrl + "?returnUrl="+ httpRequest.getRequestURI(), SamlFilter.class.getName());
								((HttpServletResponse)response).sendRedirect(  serviceProviderUrl + "?returnUrl="+ httpRequest.getRequestURI()); 
							}
							return;
						}
					} 
				}
			}
		}
		chain.doFilter(request, response);
	}



	
	   /*
	    private String getRequestUrl(final HttpServletRequest req){
	        final String scheme = req.getScheme();
	        final int port = req.getServerPort();
	        final StringBuilder url = new StringBuilder(256);
	        url.append(scheme);
	        url.append("://");
	        url.append(req.getServerName());
	        if(!(("http".equals(scheme) && (port == 0 || port == 80))
	                || ("https".equals(scheme) && port == 443))){
	            url.append(':');
	            url.append(port);
	        }
	        url.append(req.getRequestURI());
	        final String qs = req.getQueryString();
	        if(qs != null){
	            url.append('?');
	            url.append(qs);
	        }
	        final String result = url.toString();
	        return result;
	    }
	    */
	
	
	
	
	private List<String> excludes = new ArrayList<String>();

	private boolean exclude(HttpServletRequest httpRequest) {
		String uri = httpRequest.getRequestURI();
		for (String i: excludes) {
			if (uri.indexOf(i)>=0)
				return true;
		}
		if (httpRequest.getParameter("eventName")!=null) {
			org.ofbiz.base.util.Debug.logInfo("SAML ENABLED --> required event: " + httpRequest.getParameter("eventName"), SamlFilter.class.getName());

			if (httpRequest.getParameter("eventName").indexOf("getExternalLoginKey")>0) {
				return true;
			} 
		}
		return false;
	}
}
