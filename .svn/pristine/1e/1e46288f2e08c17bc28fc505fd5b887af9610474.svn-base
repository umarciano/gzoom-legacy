import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.importexport.*;

Debug.log("******************** STARTED SERVICE writeTransactionForExport ******************");

def destination = context.destination;
def toExport = context.toExport;

Debug.log("*** destination = " + destination);
Debug.log("*** toExport = " + toExport.size());

importExportUtil = new ImportExportUtil();

def stream = importExportUtil.export("ExportValoriMisure", ResourceEnum.valueOf(destination), toExport, context);

Debug.log("******************** END SERVICE writeTransactionForExport ******************");

def res = ServiceUtil.returnSuccess();
res.stream = stream;
return res;