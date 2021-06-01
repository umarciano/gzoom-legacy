import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.importexport.*;
import java.io.*;
import java.nio.*;

Debug.log("******************************* BEGIN importTransactionFromMeasure **********************");

def result = ServiceUtil.returnSuccess();

def dispatcher = dctx.getDispatcher();

def origin = context.origin;
def uploadedFile = context.uploadedFile;

Debug.log("*** origin=" + origin);
Debug.log("*** uploadedFile=" + uploadedFile);

def serviceContext = dispatcher.getDispatchContext().makeValidContext("getTransactionToImport", ModelService.IN_PARAM, context);
serviceContext.origin = origin;
serviceContext.uploadedFile = uploadedFile;

def getRes = dispatcher.runSync("getTransactionToImport", serviceContext);
def importedItems = getRes.importedItems;
def importExportUtil = getRes.importExportUtil;

it = importedItems.iterator();
def tempInStream = null;
if(UtilValidate.isNotEmpty(uploadedFile)) {
	tempInStream = new ByteArrayInputStream(uploadedFile.array());
}

while(it.hasNext()) {
	item = it.next();
	
	def writeContext = [:];
	writeContext.userLogin = userLogin;
	writeContext.refDate = item.dataRiferimento;
	writeContext.accountCode = item.codiceIndicatore;
	writeContext.workEffortMeasureId = item.idMisura;
	
	def isDateMeasure = 0;
	if (UtilValidate.isNotEmpty(item.codiceIndicatore)) {
		def condition = EntityCondition.makeCondition("accountCode", item.codiceIndicatore);
		def glAccountList = delegator.findList("GlAccount", condition, null, null, null, false);
		if (UtilValidate.isNotEmpty(item.codiceIndicatore)) {
			def glAccount = EntityUtil.getFirst(glAccountList);
			if (UtilValidate.isNotEmpty(glAccount) && UtilValidate.isNotEmpty(glAccount.defaultUomId)) {
				def uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", glAccount.defaultUomId), false);
				if (UtilValidate.isNotEmpty(uom) && UtilValidate.isNotEmpty(uom.uomTypeId) && "DATE_MEASURE".equals(uom.uomTypeId)) {
					isDateMeasure = 1;
				} 
			}
		}
	}
	
	writeContext.actualValue = item.valoreConsuntivo != null ? item.valoreConsuntivo - isDateMeasure : null;
	writeContext.budgetValue = item.valoreBudget != null ? item.valoreBudget - isDateMeasure : null;
	writeContext.actualPyValue = item.valoreConsuntivoAp != null ? item.valoreConsuntivoAp - isDateMeasure : null;
	writeContext.scoreKpi = item.valoreRisultato != null ? item.valoreRisultato: null;
	writeContext.defaultOrganizationPartyId = context.defaultOrganizationPartyId;
    
	writeContext.locale = context.locale;
	def writeRes = dispatcher.runSync("writeTransactionToImport", writeContext);
	
	def outStream = importExportUtil.writeImportResult("ImportValoriMisure", "idMisura", item.idMisura, "risultatoImportazione", writeRes.result, tempInStream);
	if(UtilValidate.isNotEmpty(outStream)) {
		result = ServiceUtil.returnSuccess("downloadImportTransactionFromMeasure");
		result.stream = outStream;
		
		tempInStream = new ByteArrayInputStream(outStream.toByteArray());
	}
}

Debug.log("******************************* END importTransactionFromMeasure **********************");


return result;