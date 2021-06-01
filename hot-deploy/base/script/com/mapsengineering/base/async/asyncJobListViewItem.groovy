import org.ofbiz.base.util.*;
import org.ofbiz.service.*;

context.action = "<span class=\"async-job-success fa\"><a target=\"_blank\" href=\"resultAsyncJob?jobId=" + item.jobId + "\" title=\"" +uiLabelMap.BaseDetail + "\"></a></span>";

if ("success".equals(item.responseMessage)) {
	if (UtilValidate.isNotEmpty(item.contentId)) {	           
	    context.action = "<span class=\"async-job-success fa\"><a target=\"_blank\" href=\"stream?contentId=" + item.contentId + "\" title=\"" +uiLabelMap.BaseDownload + "\"></a></span>";	    
	}
} else if ("queued".equals(item.responseMessage) || "running".equals(item.responseMessage)) {   
    context.action = "<span class=\"async-job-queued fa\"><a target=\"_blank\" href=\"stopAsyncJob?jobId=" + item.jobId + "\" title=\"" +uiLabelMap.BaseButtonCancel + "\"></a></span>";
}

return "success";
