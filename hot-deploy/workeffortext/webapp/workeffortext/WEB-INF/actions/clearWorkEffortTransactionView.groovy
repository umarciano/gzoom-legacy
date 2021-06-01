import org.ofbiz.base.util.*;

if (((!"N".equals(parameters.insertMode) && UtilValidate.isEmpty(parameters.id)) || ("Y".equals(parameters.fromDelete) && UtilValidate.isNotEmpty(parameters.id)))  && !"Y".equals(parameters.fromLookup)) {
	parameters.remove("weTransMeasureId");
	context.weTransMeasureId="";
	parameters.remove("workEffortMeasureId");
	context.workEffortMeasureId="";
	parameters.remove("glAccountId");
	context.glAccountId="";
	parameters.remove("weTransAccountId");
	context.weTransAccountId="";
	
	if ("Y".equals(parameters.fromDelete) && UtilValidate.isNotEmpty(parameters.id)) {
		parameters.insertMode = "Y";
	}
}