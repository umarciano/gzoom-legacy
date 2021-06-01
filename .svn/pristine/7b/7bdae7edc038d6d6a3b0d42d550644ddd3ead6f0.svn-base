import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;
import java.text.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import com.mapsengineering.base.birt.service.ZipFiles;
import com.mapsengineering.base.services.async.AsyncJobUtil;
import java.nio.ByteBuffer;

def result = ServiceUtil.returnSuccess();
def serviceName = parameters.serviceName;

if (UtilValidate.isNotEmpty(parameters.monitoringDate)) {
	def monitoringDate = new SimpleDateFormat(UtilDateTime.getDateFormat(locale)).parse(parameters.monitoringDate);
	parameters.monitoringDate = monitoringDate;
}

// Prepara mappa per il servizio (serviceName), e aggiunge in seguito gli altri campi, come per esempio outputFormat
def serviceInMap = dctx.makeValidContext(serviceName, ModelService.IN_PARAM, parameters.birtParameters);
serviceInMap.put("outputFormat", parameters.outputFormat);
serviceInMap.put("userLogin", context.userLogin);
serviceInMap.put("locale", context.locale);
def res = dctx.getDispatcher().runSync(serviceName, serviceInMap);

// Debug.log("createBirtReportZip.groovy -> res="+res);

//dato una lista di content li carico tutti nello zip
if (res.contentList.size() > 0) {
	/**
	 * Addesso devo creare il file zip
	 */
        	 
	list = [];
	def nameZip = res.exportName != null ? res.exportName : "export.zip"; 
	
	cond = EntityCondition.makeCondition(EntityCondition.makeCondition("contentId", EntityOperator.IN, res.contentList));
    listGenericValue = delegator.findList("ContentDataResourceView",  cond, UtilMisc.toSet("drObjectInfo", "drDataResourceName"), null, null, false);
    for(GenericValue gv: listGenericValue){
    	list.add(UtilMisc.toMap("pathFile", gv.getString("drObjectInfo"), "nameFile", gv.getString("drDataResourceName")));
    }

	baos = ZipFiles.createZip(nameZip, list);
	baos.flush();
    baos.close();
    buffer = ByteBuffer.wrap(baos.toByteArray());
    
    def serviceContext = dctx.makeValidContext("createContentFromUploadedFile", ModelService.IN_PARAM, parameters);
    serviceContext.put("_uploadedFile_contentType", "application/zip");
    serviceContext.put("_uploadedFile_fileName", nameZip);
    serviceContext.put("uploadedFile", buffer);
    serviceContext.put("isPublic", "N");
    serviceContext.put("contentTypeId", AsyncJobUtil.CONTENT_TYPE_TMP_ENCLOSE); // Allegati temporanei e cancellati dopo 24 ore, ma utilizzati per essere mostrati nella portlet
       
    serviceContext.put("roleTypeNotRequired", "Y");
	serviceContext.put("partyId", userLogin.partyId);
    serviceContext.put("createdDate", UtilDateTime.nowTimestamp());
     
    // crea allegato con lo zip
    def res2 = dctx.getDispatcher().runSync("createContentFromUploadedFile", serviceContext);
    def contentId = res2.contentId;
    // inserendo il contentId nel risultato, il browser propone il download del file
    result.put("contentId", contentId);
    Debug.log("report output contentId: " + contentId);
}

return result;
