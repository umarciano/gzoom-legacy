import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

import java.io.ObjectOutputStream.DebugTraceInfoStack;
import java.util.*;


/**
 * Calcolo il valore di valModId solo inc aso di ins3erimento
 */

//Debug.log(".............. parameters.weTransMeasureId "+parameters.weTransMeasureId);
//Debug.log(".............. parameters.weTransAccountId "+parameters.weTransAccountId);
//Debug.log(".............. parameters.insertMode "+parameters.insertMode);
//Debug.log(".............. parameters.getValModId "+parameters.getValModId);
if ("Y".equals(parameters.insertMode) || "W".equals(parameters.insertMode) || "Y".equals(parameters.getValModId)) {
	
	def workEffortMeasureId = UtilValidate.isNotEmpty(context.weTransMeasureId) ? context.weTransMeasureId : parameters.weTransMeasureId;
	def glAccountId = UtilValidate.isNotEmpty(context.weTransAccountId) ? context.weTransAccountId : parameters.weTransAccountId;
	
	def glAccount = delegator.findOne("GlAccount", ["glAccountId" : glAccountId], false);
	def workEffortMeasure = delegator.findOne("WorkEffortMeasure", ["workEffortMeasureId" : workEffortMeasureId], false);
	
	if (UtilValidate.isNotEmpty(glAccount) && UtilValidate.isNotEmpty(workEffortMeasure)) {
		
		/**GN-1142 Valore modificabile in base al DATA_SOURCE*/
		def dataSourceId = glAccount.dataSourceId;
		if (glAccount.inputEnumId == "ACCINP_PRD") {
			dataSourceId = workEffortMeasure.dataSourceId;
		}
		
		def dataSource = delegator.findOne("DataSource", ["dataSourceId" : dataSourceId], true);
		if (UtilValidate.isNotEmpty(dataSource)) {
			parameters.valModId = dataSource.valModId;
		}
		
	}
	
}

