import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.webapp.control.LoginWorker;

res = "success"; 

menuId = parameters.menuId;
portalPageId = parameters.portalPageId;
Debug.log("####################################### dispatcherRequest.groovy -> menuId =" + menuId);
Debug.log("####################################### dispatcherRequest.groovy -> portalPageId =" + portalPageId);

if(UtilValidate.isNotEmpty(menuId)) {
	def linkValue = delegator.findOne("ContentAttribute", ["contentId": menuId, "attrName" : "link"], false);
	def linkString = linkValue.getString("attrValue");
	if (linkString.indexOf("?") < 0) {
	    linkString = linkString.concat("?");
	}
	def contentAttributeList = delegator.findList("ContentAttribute", EntityCondition.makeCondition(
			EntityCondition.makeCondition("contentId", menuId),
			EntityCondition.makeCondition("attrName", EntityOperator.LIKE, "link-%")) , null, null, null, false);
	def urlString = "";
	contentAttributeList.each { itemLink ->
		// TODO alcuni parametri si possono eliminare 
		urlString += "&" + itemLink.attrValue;
	}
	attrValue = linkString + "&";

	def urlParams = [:];
	urlParams.put("noInfoToolbar", "true");
	urlParams.put("noMasthead", "true");
	urlParams.put("MainColumnStyle", "single-column-fullopen");
	urlParams.put("noLeftBar", "true");
	urlParams.put(LoginWorker.EXTERNAL_LOGIN_KEY_ATTR, LoginWorker.getExternalLoginKey(request));
	// ajaxRequest=Y poiche' viene dai ContentAttribute
	// TODO
	// ajaxCall funziona anche senza ?
	// clearSaveView=Y serve?
	
	String redirectUrl = attrValue + UtilHttp.urlEncodeArgs(urlParams, false) + urlString; //  per ora servono per non cmabiare troppe cose ma volend osi potrebber otogliere e settare negli screen
	Debug.log(" - redirectUrl " + redirectUrl);
	response.sendRedirect(redirectUrl);
} else if(UtilValidate.isNotEmpty(portalPageId)) {
	// TODO
	def contentAttributeList = delegator.findList("PortalPage", EntityCondition.makeCondition("portalPageId" , menuId), null, null, null, false);
} else {
    // TODO
    request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage("BaseErrorLabels", "UnknowRequest", locale));
    res = "error";
}
return res;