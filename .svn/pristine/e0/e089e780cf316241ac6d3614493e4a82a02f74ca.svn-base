import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import org.ofbiz.entity.condition.*;
import com.mapsengineering.base.util.importexport.*;
import java.io.*;
import java.nio.*;

Debug.log("******************************* BEGIN getTransactionToImport **********************");

def origin = context.origin;
def uploadedFile = context.uploadedFile;
if (UtilValidate.isNotEmpty(uploadedFile)) {
	if (uploadedFile instanceof ByteBuffer) {
		uploadedFile = new ByteArrayInputStream(uploadedFile.array());
	}
}
def importedItems = [];

Debug.log("*** origin=" + origin);
Debug.log("*** uploadedFile=" + uploadedFile);

importExportUtil = new ImportExportUtil();
conditionList = [];
//conditionList.add(EntityCondition.makeCondition("dataRiferimento", refDate));
importedItems = importExportUtil.read("ImportValoriMisure", EntityCondition.makeCondition(conditionList), ResourceEnum.valueOf(origin), uploadedFile, context)

Debug.log("******************************* END getTransactionToImport **********************");

def res = ServiceUtil.returnSuccess();
res.importedItems = importedItems;
res.importExportUtil = importExportUtil;
return res;