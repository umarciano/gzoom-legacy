import org.ofbiz.base.util.*;

def result = "success";

if (UtilValidate.isNotEmpty(parameters.outputFormat)) {
	// controllo che tipo di formato devo stampare

	if ("html" == parameters.outputFormat) {
	    request.setAttribute("contentType", "text/html");
	    result = "content_HTML";
	} else if ("xls" == parameters.outputFormat || "xls_native" == parameters.outputFormat) {
	    request.setAttribute("contentType", "application/vnd.ms-excel");
	    result = "content_XLS";
	} else if ("doc" == parameters.outputFormat) {
	    request.setAttribute("contentType", "application/vnd.ms-word");
	    result = "content_DOC";
    } else if ("odt" == parameters.outputFormat) {
        request.setAttribute("contentType", "application/vnd.oasis.opendocument.text");
        result = "content_ODT";
    } else {
        request.setAttribute("outputFormat", "pdf");
        request.setAttribute("contentType", "application/pdf");
        result = "content";
    }
}

def enableAsyncReport = UtilProperties.getPropertyValue("BaseConfig", "report.enableAsyncReport", "false");
if ("true".equals(enableAsyncReport)) {
    request.setAttribute("jobServiceName", "createBirtReport");
    result = "content_async";
}

return result;
