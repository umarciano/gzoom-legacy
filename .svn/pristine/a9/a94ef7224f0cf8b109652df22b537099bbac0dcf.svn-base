import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.*;
import com.mapsengineering.base.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import com.mapsengineering.base.birt.util.Utils;

condition = EntityCondition.makeCondition("workEffortTypeId", EntityOperator.LIKE, "CTX%")
	
def workEffortTypeCtxList = delegator.findList("WorkEffortType", condition, 
	UtilMisc.toSet("workEffortTypeId", "description"), UtilMisc.toList("workEffortTypeId"), null, false);

finalList = [];

workEffortTypeCtxList.each { listItem ->
	if (!"CTX_AC".equalsIgnoreCase(listItem.workEffortTypeId) && !"CTX_PY".equalsIgnoreCase(listItem.workEffortTypeId)) {
		finalList.add(listItem);
	}
}

// Aggiunta altri contesti non presenti su tabella
def ctxAc = delegator.makeValue("WorkEffortType");
ctxAc.set("workEffortTypeId", "CTX_AC");
ctxAc.set("description", "Unita' contabili/extracontabili");

def ctxPy = delegator.makeValue("WorkEffortType");
ctxPy.set("workEffortTypeId", "CTX_PY");
ctxPy.set("description", "Soggetti");

finalList.add(ctxAc);
finalList.add(ctxPy);

finalList = EntityUtil.orderBy(finalList, UtilMisc.toList("workEffortTypeId"));

Debug.log("### executePerformFindQueryCtx -> workEffortTypeCtxList= " + finalList);

context.workEffortTypeCtxList = finalList;

