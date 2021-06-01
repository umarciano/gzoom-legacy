import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

def getParameter = { name ->
	def res = "";
	
	if (UtilValidate.isNotEmpty(parameters[name])) {
		res = parameters[name];
	}
	
	return res;
}

birtParameters = [:];

birtParameters.put("workEffortId", getParameter("workEffortId"));
birtParameters.put("workEffortAnalysisId", getParameter("workEffortAnalysisId") );
birtParameters.put("weHierarchyTypeId", getParameter("weHierarchyTypeId") );
birtParameters.put("referenceDate", getParameter("referenceDate") );
birtParameters.put("glFiscalTypeId", getParameter("glFiscalTypeId") );

def monitoringDate = getParameter("monitoringDate")
if (UtilValidate.isNotEmpty(monitoringDate)) {
	monitoringDate = ObjectType.simpleTypeConvert(monitoringDate, "Timestamp", UtilDateTime.getDateTimeFormat(locale), locale);
	if (UtilValidate.isNotEmpty(monitoringDate)) {
		birtParameters.put("monitoringDate", monitoringDate.toString());
	}
}
birtParameters.put("workEffortIdChild", getParameter("workEffortIdChild") );
birtParameters.put("orgUnitId", getParameter("orgUnitId") );
birtParameters.put("roleTypeId", getParameter("roleTypeId") );
birtParameters.put("partyId", getParameter("partyId") );
birtParameters.put("organizationId", getParameter("organizationId") );

request.setAttribute("birtParameters", birtParameters);

return "success";