import org.ofbiz.base.util.*;

def result = "success";
/*

Debug.log("managementServicePrintBirtAsync.groovy -> parameters.monitoringDate="+parameters.monitoringDate);
Debug.log("managementServicePrintBirtAsync.groovy -> parameters.outputFormat="+parameters.outputFormat);
Debug.log("managementServicePrintBirtAsync.groovy -> parameters.workEffortId="+parameters.workEffortId);
Debug.log("managementServicePrintBirtAsync.groovy -> parameters.workEffortTypeId="+parameters.workEffortTypeId);
Debug.log("managementServicePrintBirtAsync.groovy -> parameters.birtParameters="+parameters.birtParameters);
*/

def enableAsyncReport = UtilProperties.getPropertyValue("BaseConfig", "report.enableAsyncReport", "false");
if ("true".equals(enableAsyncReport)) {
    request.setAttribute("jobServiceName", "createBirtReportZip");
    result = "content_async";
}

return result;
