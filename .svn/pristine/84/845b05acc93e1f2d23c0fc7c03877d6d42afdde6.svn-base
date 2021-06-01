import org.ofbiz.base.util.*;
import org.ofbiz.service.ModelService;

import java.text.*;
import com.mapsengineering.base.util.MessageUtil;
import java.sql.Timestamp;

Debug.log("******************** STARTED SERVICE exportTransactionFromMeasure ******************");
def uiLabelMap = UtilProperties.getResourceBundleMap("WorkeffortExtUiLabels", locale)

def refDate = parameters.refDate;
if (UtilValidate.isNotEmpty(refDate)) {
	refDate = new SimpleDateFormat(UtilDateTime.getDateFormat(locale)).parse(refDate, new ParsePosition(0));
}
else {
	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(MessageUtil.BASE_ERROR_LABEL, "MandatoryField", UtilMisc.toMap("fieldName", uiLabelMap.FormFieldTitle_transactionDate), locale));
	return "Y".equals(parameters.ajaxCall) ? "error_json" : "error";
}
def workEffortPurposeTypeId = parameters.workEffortPurposeTypeId;
if (UtilValidate.isEmpty(workEffortPurposeTypeId)) {
	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(MessageUtil.BASE_ERROR_LABEL, "MandatoryField", UtilMisc.toMap("fieldName", uiLabelMap.WorkEffortPurposeType), locale));
	return "Y".equals(parameters.ajaxCall) ? "error_json" : "error";
}
def dataSourceId = parameters.dataSourceId;
def destination = parameters.destination;

//Debug.log("*** refDate= " + refDate);
//Debug.log("*** workEffortPurposeTypeId= " + workEffortPurposeTypeId);
//Debug.log("*** dataSourceId= " + dataSourceId);
//Debug.log("*** destination= " + destination);

if (refDate == null) {
	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(MessageUtil.BASE_ERROR_LABEL, "InvalidDateFormat", UtilMisc.toList(uiLabelMap.FormFieldTitle_transactionDate, locale.getDisplayName()), locale));
	return "Y".equals(parameters.ajaxCall) ? "error_json" : "error";
}

//Chiamo servizio che estrae i dati 
def serviceRes = dispatcher.runSync("getTransactionToExport", UtilMisc.toMap("refDate", new Timestamp(refDate.getTime()), "workEffortPurposeTypeId", workEffortPurposeTypeId, "dataSourceId", dataSourceId, "defaultOrganizationPartyId", parameters.defaultOrganizationPartyId, "userLogin", userLogin));
def toExportList = serviceRes.exportedItems;

Debug.log("*** exportTransactionFromMeasure found " + toExportList.size() + " records to export");

//if (UtilValidate.isNotEmpty(toExportList)) {
	//Export to destination
	
	def serviceContext = dispatcher.getDispatchContext().makeValidContext("writeTransactionForExport", ModelService.IN_PARAM, context);
	serviceContext.destination = destination;
	serviceContext.toExport = toExportList;
	//def res = dispatcher.runSync("writeTransactionForExport", UtilMisc.toMap("destination", destination, "toExport", toExportList, "userLogin", userLogin));
	def res = dispatcher.runSync("writeTransactionForExport", serviceContext);
	if(UtilValidate.isNotEmpty(res.stream)) {
		def stream = res.stream;
		UtilHttp.streamContentToBrowser(response, stream.toByteArray(), "application/vnd.ms-excel", "EsportazioneValoriMisure.xls");
	}
//}

Debug.log("******************** END SERVICE exportTransactionFromMeasure ******************");

return "success";