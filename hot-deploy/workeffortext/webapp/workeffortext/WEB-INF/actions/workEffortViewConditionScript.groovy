import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;

screenNameListIndex = "";
rootInqyTree = "N";
rootTree = "N";
conditionFields = [:];
foundGV = null;

def entityName = null;

childManagement = context.childManagement;
if("N".equals(childManagement) && UtilValidate.isNotEmpty(context.parentEntityName)) {
	entityName = context.parentEntityName;
}
else
	entityName = context.entityName;


if (UtilValidate.isNotEmpty(context.rootInqyTree)) {
	rootInqyTree = context.rootInqyTree;
} else if (UtilValidate.isNotEmpty(parameters.rootInqyTree)) {
	rootInqyTree = parameters.rootInqyTree;
}
Debug.log("workEffortViewConditionScript.groovy rootInqyTree " + rootInqyTree);
if (UtilValidate.isNotEmpty(context.rootTree)) {
	rootTree = context.rootTree;
} else if (UtilValidate.isNotEmpty(parameters.rootTree)) {
	rootTree = parameters.rootTree;
}

/*keys = parameters.keySet();

for (key in keys) {
	if (key.startsWith("workEffort")) {
		Debug.log(" parameters[" + key + "] = " + parameters[key]);
	}
}
			
Debug.log("parameters.selectedId " + parameters.selectedId);
Debug.log("parameters.ignoreSelectedIdFromCookie " + parameters.ignoreSelectedIdFromCookie);
Debug.log("parameters.isBack" + parameters.isBack);
*/

//Essendo questo script chiamato prima di tutto eseguo alcuni controlli in caso di cancellazione di obiettivo da scheda
if ("Y".equals(rootTree) || (!"Y".equals(rootTree) && ("Y".equals(rootInqyTree) || "Y".equals(parameters.specialized) || "Y".equals(parameters.weIsTemplate) || "WorkEffortRootInqyTree".equals(parameters.entityName) || "WorkEffortRootInqyTree".equals(parameters.treeViewName)))) {
	if ("Y".equals(parameters.fromDelete)) {
		if (UtilValidate.isNotEmpty(parameters.workEffortIdFrom)) {
			parameters.workEffortId = parameters.workEffortIdFrom;
		}
		
		def workEffort = delegator.findOne("WorkEffortView", ["workEffortId" : parameters.workEffortId], false);
		if (UtilValidate.isNotEmpty(workEffort)) {
			context.putAll(workEffort);
		}
		
		if (UtilValidate.isNotEmpty(parameters.workEffortId)) {
			def workEffortAssocForParentList = delegator.findList("WorkEffortAssoc", EntityCondition.makeCondition([EntityCondition.makeCondition("workEffortIdTo", parameters.workEffortId),
																												EntityCondition.makeCondition("workEffortAssocTypeId", parameters.weHierarchyTypeId)]), null, ["-fromDate"], null, false);
			if (UtilValidate.isNotEmpty(workEffortAssocForParentList)) {
				for (workEffortAssocForParent in workEffortAssocForParentList) {
					def workEffortAssocParentToRoot = delegator.findList("WorkEffortAssoc", EntityCondition.makeCondition([EntityCondition.makeCondition("workEffortIdFrom", parameters.workEffortIdRoot),
																												EntityCondition.makeCondition("workEffortIdTo", workEffortAssocForParent.workEffortIdFrom),
																												EntityCondition.makeCondition("workEffortAssocTypeId", "ROOT")]), null, null, null, false);
																											
					if (UtilValidate.isNotEmpty(workEffortAssocParentToRoot)) {
						parameters.workEffortIdFrom = workEffortAssocForParent.workEffortIdFrom;
						
						break;
					}
				}
			}
		}
	} else if (UtilValidate.isNotEmpty(parameters.selectedId) && (!"Y".equals(parameters.ignoreSelectedIdFromCookie) || "Y".equals(parameters.isBack)) && !"Y".equals(parameters.insertMode) && !"Y".equals(parameters.newInsert)){
		if ("Y".equals(parameters.ignoreSelectedIdFromCookie)) {
			parameters.remove("selectedId");
			if("Y".equals(parameters.isBack)) {
				// esiste questa request che se utilizzata contiene in worEffortParentIdFrom o worEffortParentIdTo il vero workEffortId (workEffortAssocExtViewFromManagementContainerOnly o workEffortAssocExtViewToManagementContainerOnly)
				if(UtilValidate.isNotEmpty(parameters.relationTitle) && UtilValidate.isNotEmpty(parameters.worEffortParentIdFrom) && "from".equals(parameters.relationTitle)) {
					parameters.workEffortId = parameters.worEffortParentIdFrom;
				} else if (UtilValidate.isNotEmpty(parameters.relationTitle) && UtilValidate.isNotEmpty(parameters.worEffortParentIdTo) && "to".equals(parameters.relationTitle)) {
					parameters.workEffortId = parameters.worEffortParentIdTo;
				}
			}
		} else {
			def localWorkEffortId = parameters.selectedId.substring(parameters.selectedId.lastIndexOf("_")+1);
		
			if (!parameters.workEffortId.equals(localWorkEffortId)) {
				if (UtilValidate.isEmpty(parameters.workEffortIdRoot)) {
					parameters.workEffortIdRoot = parameters.workEffortId;
				}
				parameters.workEffortId = localWorkEffortId;
			}
		}
	} else if ("Y".equals(parameters.ignoreSelectedIdFromCookie) && !"Y".equals(parameters.isBack)) {
		parameters.remove("selectedId");
	}
	context.workEffortId = parameters.workEffortId;
}

if (UtilValidate.isNotEmpty(parameters.screenNameListIndex) && !"Y".equals(parameters.newInsert) && !"Y".equals(parameters.fromDelete)) {
	return parameters.screenNameListIndex;
}

if (UtilValidate.isNotEmpty(parameters.screenNameListIndex) && !"WorkEffortView".equals(parameters.entityName) && "Y".equals(parameters.fromDelete)) {
	return parameters.screenNameListIndex;
}

if (UtilValidate.isNotEmpty(entityName)) {
	if (!"WorkEffortView".equals(entityName) && "WorkEffortView".equals(context.parentEntityName)) {
		entityName = context.parentEntityName;
	}
	
	if ("WorkEffortView".equals(entityName)) {
		conditionFields["workEffortId"] = parameters.workEffortId;
	}

	if (!conditionFields.isEmpty()) {
		foundGV = delegator.findOne(entityName, conditionFields, false);
	}
	
	if (foundGV && !"Y".equals(rootTree) && ("Y".equals(rootInqyTree) || "Y".equals(parameters.specialized) || "Y".equals(parameters.weIsTemplate) || "WorkEffortRootInqyTree".equals(parameters.entityName) || "WorkEffortRootInqyTree".equals(parameters.treeViewName))) {
		
		if ("WELAY_STANDARD".equals(foundGV.layoutTypeEnumId) && "CTX_EP".equals(foundGV.weContextId)) {
			screenNameListIndex = "1";
		}

		else if ("WELAY_STANDARD".equals(foundGV.layoutTypeEnumId)
			&& ("CTX_OR".equals(foundGV.weContextId)
				|| "CTX_BS".equals(foundGV.weContextId)
				|| "CTX_CO".equals(foundGV.weContextId)
				|| "CTX_PR".equals(foundGV.weContextId)
                || "CTX_CG".equals(foundGV.weContextId)
                || "CTX_TR".equals(foundGV.weContextId)
                || "CTX_RE".equals(foundGV.weContextId)
                || "CTX_PM".equals(foundGV.weContextId)
				|| "CTX_MA".equals(foundGV.weContextId)
				|| "CTX_GD".equals(foundGV.weContextId)
				|| "CTX_PA".equals(foundGV.weContextId)
				|| "CTX_DI".equals(foundGV.weContextId)
			   )
		   ) {
			screenNameListIndex = "2";
		}

		else if ("WELAY_SIMPLIFIED".equals(foundGV.layoutTypeEnumId) && "CTX_EP".equals(foundGV.weContextId)) {
			screenNameListIndex = "3";
		}

		//Visualizza solo HEADER
		if (screenNameListIndex.equals("") && ("Y".equals(rootInqyTree) || "Y".equals(parameters.specialized))) {
			if ("CTX_EP".equals(foundGV.weContextId) ) {
				screenNameListIndex = "4";
			}

			else if ("CTX_OR".equals(foundGV.weContextId)
				|| "CTX_BS".equals(foundGV.weContextId)
				|| "CTX_CO".equals(foundGV.weContextId)
				|| "CTX_PR".equals(foundGV.weContextId)
				|| "CTX_CG".equals(foundGV.weContextId)
                || "CTX_TR".equals(foundGV.weContextId)
                || "CTX_RE".equals(foundGV.weContextId)
                || "CTX_PM".equals(foundGV.weContextId)
				|| "CTX_MA".equals(foundGV.weContextId)
				|| "CTX_GD".equals(foundGV.weContextId)
				|| "CTX_PA".equals(foundGV.weContextId)
				|| "CTX_DI".equals(foundGV.weContextId)
				) {
				screenNameListIndex = "5";
			}
		}
		
		context.foundGV;
	} else if ("Y".equals(rootTree)){
		//Debug.log("screenNameListIndex: " + screenNameListIndex);
	
		//messo qui perche treeviewname e sbagliato, viene sempre valorizzato con WorkEffortRootInqyTree
		screenNameListIndex = "6";
		
		// aggiunta gestione per i modelli delle schede, visualizzare header per il cambio dello stato anche per i modelli
		if ("Y".equals(parameters.weIsTemplate)) {
			if ("CTX_EP".equals(foundGV.weContextId) ) {
				screenNameListIndex = "4";
			}

			else if ("CTX_OR".equals(foundGV.weContextId)
				|| "CTX_BS".equals(foundGV.weContextId)
				|| "CTX_CO".equals(foundGV.weContextId)
				|| "CTX_PR".equals(foundGV.weContextId)
                || "CTX_CG".equals(foundGV.weContextId)
                || "CTX_TR".equals(foundGV.weContextId)
                || "CTX_RE".equals(foundGV.weContextId)
                || "CTX_PM".equals(foundGV.weContextId)
				|| "CTX_MA".equals(foundGV.weContextId)
				|| "CTX_GD".equals(foundGV.weContextId)
				|| "CTX_PA".equals(foundGV.weContextId)
				|| "CTX_DI".equals(foundGV.weContextId)
				) {
				screenNameListIndex = "5";
			}
		} else {
			if ("WELAY_STANDARD".equals(foundGV.layoutTypeEnumId) && "CTX_EP".equals(foundGV.weContextId)) {
				screenNameListIndex = "1";
			}

			else if ("WELAY_STANDARD".equals(foundGV.layoutTypeEnumId)
				&& ("CTX_OR".equals(foundGV.weContextId)
					|| "CTX_BS".equals(foundGV.weContextId)
					|| "CTX_CO".equals(foundGV.weContextId)
					|| "CTX_PR".equals(foundGV.weContextId)
	                || "CTX_CG".equals(foundGV.weContextId)
	                || "CTX_TR".equals(foundGV.weContextId)
	                || "CTX_RE".equals(foundGV.weContextId)
	                || "CTX_PM".equals(foundGV.weContextId)
					|| "CTX_MA".equals(foundGV.weContextId)
					|| "CTX_GD".equals(foundGV.weContextId)
					|| "CTX_PA".equals(foundGV.weContextId)
					|| "CTX_DI".equals(foundGV.weContextId)
				   )
			   ) {
				screenNameListIndex = "2";
			}			
		}		
	}
}

Debug.log("screenNameListIndex: " + screenNameListIndex);

return screenNameListIndex;