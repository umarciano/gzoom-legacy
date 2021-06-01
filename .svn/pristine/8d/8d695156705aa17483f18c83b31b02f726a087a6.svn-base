import org.ofbiz.base.util.*;

def loadGooglemaps = UtilProperties.getPropertyValue("general.properties", "load.googlemaps", "Y");
if ("Y".equals(loadGooglemaps)) {
	def layoutSettings = context.layoutSettings;
	if (UtilValidate.isEmpty(layoutSettings)) {
		layoutSettings = [:];
	}
	
	def javaScripts = layoutSettings.javaScripts;
	if (UtilValidate.isEmpty(layoutSettings) || UtilValidate.isEmpty(javaScripts)) {
		javaScripts = [];
		layoutSettings.javaScripts = javaScripts;
	}

	javaScripts.add("/images/GooglemapMarkers.js");

	context.layoutSettings = layoutSettings;
}