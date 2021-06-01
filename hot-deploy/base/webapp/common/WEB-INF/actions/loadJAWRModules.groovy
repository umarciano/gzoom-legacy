import org.ofbiz.base.util.*;

def layoutSettings = context.layoutSettings;
if (UtilValidate.isEmpty(layoutSettings)) {
	layoutSettings = [:];
}

def loadGooglemaps = UtilProperties.getPropertyValue("general.properties", "load.googlemaps", "Y");

def extrajavaScripts = layoutSettings.extrajavaScripts;
if (UtilValidate.isEmpty(layoutSettings) || UtilValidate.isEmpty(extrajavaScripts)) {
	extrajavaScripts = [];
	layoutSettings.extrajavaScripts = extrajavaScripts;
}
if ("Y".equals(loadGooglemaps)) {
	Debug.log("protocol=" + request.getProtocol())
	def key = UtilProperties.getPropertyValue("general.properties", "https." + request.getServerName());
	extrajavaScripts.add("http://www.google.com/jsapi?key=" + key);
}
extrajavaScripts.add("/resources/jawr_loader.js");

def jawrScriptResources = layoutSettings.jawrScriptResources;
if (UtilValidate.isEmpty(layoutSettings) || UtilValidate.isEmpty(jawrScriptResources)) {
	jawrScriptResources = [];
	layoutSettings.jawrScriptResources = jawrScriptResources;
}

jawrScriptResources.add("/bundles/resources.js");

if ("Y".equals(loadGooglemaps)) {
	jawrScriptResources.add("/bundles/resourcesGooglemaps.js");
}
jawrScriptResources.add("/bundles/jquery.js");
jawrScriptResources.add("/bundles/treeview.js");

context.layoutSettings = layoutSettings;