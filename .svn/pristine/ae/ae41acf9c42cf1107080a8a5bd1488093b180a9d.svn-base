import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;
import java.text.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import com.mapsengineering.base.birt.service.ZipFiles;

def serviceName = request.getParameter("serviceName");

if (UtilValidate.isNotEmpty(parameters.monitoringDate)) {
	def monitoringDate = new SimpleDateFormat(UtilDateTime.getDateFormat(locale)).parse(parameters.monitoringDate);
	parameters.monitoringDate = monitoringDate;
}
Debug.log("managementServicePrintBirt.groovy -> serviceName="+serviceName);
def serviceInMap = dispatcher.getDispatchContext().makeValidContext(serviceName, ModelService.IN_PARAM, parameters);
serviceInMap.put("userLogin", context.userLogin);
serviceInMap.put("locale", context.locale);
def res = dispatcher.runSync(serviceName, serviceInMap);
Debug.log("managementServicePrintBirt.groovy -> res="+res);
//dato una lista di concent li carico tutti nello zip

if (res.contentList.size() > 0) {
	/**
	 * Addesso devo creare il file zip
	 */
        	 
	list = [];
	String nameZip = res.exportName != null ? res.exportName : "export.zip"; 
	
	cond = EntityCondition.makeCondition(EntityCondition.makeCondition("contentId", EntityOperator.IN, res.contentList));
    listGenericValue = delegator.findList("ContentDataResourceView",  cond, UtilMisc.toSet("drObjectInfo", "drDataResourceName"), null, null, false);
    for(GenericValue gv: listGenericValue){
    	list.add(UtilMisc.toMap("pathFile", gv.getString("drObjectInfo"), "nameFile", gv.getString("drDataResourceName")));
    }

	baos = ZipFiles.createZip(nameZip, list);
	
    response.setHeader("Content-Disposition", "attachment; filename=\"" + nameZip + "\"");
    response.setHeader("Content-Type", "application/zip");
    response.getOutputStream().write(baos.toByteArray());
    response.flushBuffer();
    baos.close();
    
}



if (ServiceUtil.isError(res)) {
	return "error";
} else {
	return "success";
}