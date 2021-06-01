import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

def getParameter = { name ->
	def res = "";
	
	if (UtilValidate.isNotEmpty(parameters[name])) {
		res = parameters[name];
	}
	
	return res;
}

birtParameters = [:];

birtParameters.put("productId", getParameter("productId"));
def yearIndicator = getParameter("yearIndicator")
if (UtilValidate.isNotEmpty(yearIndicator)) {
	yearIndicator = ObjectType.simpleTypeConvert(yearIndicator, "Timestamp", UtilDateTime.getDateTimeFormat(locale), locale);
	if (UtilValidate.isNotEmpty(yearIndicator)) {
		birtParameters.put("yearIndicator", yearIndicator.toString());
	}
}

request.setAttribute("birtParameters", birtParameters);

return "success";