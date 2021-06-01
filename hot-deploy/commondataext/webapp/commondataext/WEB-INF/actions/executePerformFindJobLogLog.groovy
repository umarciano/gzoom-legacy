import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import com.mapsengineering.base.util.*;



import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

res = "success";
//Creo una mappa locale 
localParameters = [:];
localParameters.putAll(parameters);



result = FindWorker.performFind(localParameters, dispatcher, timeZone, locale);

if (ServiceUtil.isError(result)) {
	res = "error";
	request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(result));
} else {
	if (UtilValidate.isNotEmpty(result.listIt) && result.listIt instanceof org.ofbiz.entity.util.EntityListIterator) {
		 lista = result.listIt.getCompleteList();
		 result.listIt.close();
		 result.listIt = lista;
		 if (UtilValidate.isNotEmpty(context))
			 context.listIt = lista;
	} else {
		request.setAttribute("noResult", "Y");
	}
	for(key in result.keySet()) {
		request.setAttribute(key, result[key]);
	}
}

return res;
