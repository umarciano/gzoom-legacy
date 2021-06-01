import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;
import java.text.*;
import org.ofbiz.entity.condition.EntityCondition;
import com.mapsengineering.base.birt.service.ZipFiles;

returnMap = ServiceUtil.returnSuccess();

def workEffortId = parameters.workEffortId;
def condition = EntityCondition.makeCondition(EntityCondition.makeCondition("workEffortId", workEffortId));
def workEffortContentList = delegator.findList("WorkEffortContentView", condition, null, null, null, false);
if (UtilValidate.isNotEmpty(workEffortContentList)) {
	def fileList = [];
	for (GenericValue workEffortContent : workEffortContentList) {
		if (UtilValidate.isNotEmpty(workEffortContent)) {
			fileList.add(UtilMisc.toMap("pathFile", workEffortContent.getString("objectInfo"), "nameFile", workEffortContent.getString("contentName")));
		}	
	}
	if (UtilValidate.isNotEmpty(fileList)) {
		def nameZip = "export_" + workEffortId + ".zip";
		try {
		    def baos = ZipFiles.createZip(nameZip, fileList);
	        response.setHeader("Content-Disposition", "attachment; filename=\"" + nameZip + "\"");
	        response.setHeader("Content-Type", "application/zip");
	        response.getOutputStream().write(baos.toByteArray());
	        response.flushBuffer();
	        baos.close();
       } catch (FileNotFoundException e) {
            Debug.log(" FileNotFoundException " + e.getMessage());
		    request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        } catch (IOException e) {
            Debug.log(" IOException " + e.getMessage());
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        } catch (Exception e) {
            Debug.log(" Exception " + e.getMessage());
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        } 
	}
}

return returnMap;