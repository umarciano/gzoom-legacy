import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import org.ofbiz.common.FindServices;
import com.mapsengineering.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import com.mapsengineering.base.find.WorkEffortFindServices;


/**
 * Le possibili viste dove esegue la ricer sono  (se mancano aggiungerle):
 * 
 * ***** View
 * WorkEffortRootView
 * WorkEffortRootInqyView
 * WorkEffortRootTemplateView
 * 
 * ***** RoleView
 * WorkEffortRootInqyRoleView
 * WorkEffortRootInqyOrgMgrRoleView
 * WorkEffortRootRoleView
 * WorkEffortRootInqySummaryRoleView
 * 
 * ****** OrgMgrRoleView
 * WorkEffortRootOrgMgrRoleView
 * WorkEffortRootInqySummaryOrgMgrRoleView
 * WorkEffortRootTemplateOrgMgrRoleView
 * 
 * ***** OrgMgrView
 * WorkEffortRootTemplateOrgMgrView
 * WorkEffortRootInqySummaryOrgMgrView
 * WorkEffortRootInqyPartySummaryOrgMgrView
 * WorkEffortRootInqyOrgMgrView
 * WorkEffortRootOrgMgrView
 * 
 */



def checkPermission(permission) {
    if (security.hasPermission(permission, userLogin))
        return permission;
    return null;
}
res = "success";

def isOrgMgr = false;
def isSup = false;
def isRole = false;
def isTop = false;
def searchDate = ObjectType.simpleTypeConvert(parameters.searchDate, "Timestamp", null, locale);
def sortField = parameters.sortField;
def weContextId = UtilValidate.isNotEmpty(parameters.weContextId) ? parameters.weContextId : parameters.weContextId_value;

if (UtilValidate.isNotEmpty(userLogin)) {
	parameters.uvUserLoginId = userLogin.userLoginId;
	if (UtilValidate.isEmpty(context.permission)) {
		if (UtilValidate.isNotEmpty(weContextId)) {
			context.permission = ContextPermissionPrefixEnum.getPermissionPrefix(weContextId);
		}
	    if (UtilValidate.isEmpty(context.permission)) {
	        context.permission = "WORKEFFORT";
	    }
	}
	
	entityNamePrefix = parameters.entityNamePrefix;
	if (UtilValidate.isEmpty(entityNamePrefix)) {
		entityNamePrefix = context.entityNamePrefix;
		if (UtilValidate.isEmpty(entityNamePrefix)) {
			entityNamePrefix = "WorkEffortRootInqy";
		}
	}
	
	parameters.entityName = entityNamePrefix + "View";
	
	userLoginPermissionOrg = checkPermission(context.permission + "ORG_ADMIN");
	userLoginPermissionRole = checkPermission(context.permission + "ROLE_ADMIN");	
	userLoginPermissionSup = checkPermission(context.permission + "SUP_ADMIN");
	userLoginPermissionTop = checkPermission(context.permission + "TOP_ADMIN");	
	userLoginPermissionViewAdmin = checkPermission(context.permission + "VIEW_ADMIN");
	
	
	if(!parameters.rootInqyTree.equals("Y") || UtilValidate.isEmpty(userLoginPermissionViewAdmin)){	
		
	
	    if(UtilValidate.isNotEmpty(userLoginPermissionOrg) && UtilValidate.isEmpty(userLoginPermissionRole)){
	    // solo permessi per vedere i workEffort tali che:
	    // si ha una relazione di tipo ORG_RESPONSIBLE oppure ORG_DELEGATE con orgUnitId del workEffort
	        parameters.entityName = entityNamePrefix + "OrgMgrView";
			parameters.partyIdTo = userLogin.partyId;
			
			isOrgMgr = true;
	    }
	    if(UtilValidate.isEmpty(userLoginPermissionOrg) && UtilValidate.isNotEmpty(userLoginPermissionRole)){
	    // solo permessi per vedere i workEffort tali che:
	    // si ha un'assegnazione per il workEffort
	        parameters.entityName = entityNamePrefix + "RoleView";
			parameters.partyId = userLogin.partyId;
			
			isRole = true;
	    }
	    if(UtilValidate.isNotEmpty(userLoginPermissionOrg) && UtilValidate.isNotEmpty(userLoginPermissionRole)){
			// la vista WorkEffortPartyRelAndAssignULValidPartyRole deve avere 
			// parameters.partyIdTo = userLogin.partyId OR parameters.partyId = userLogin.partyId
			// poichè non è possibile inserire in una LeftJoin una condition di variabili con un 'OR'
			// dopo la perform find la lista viene nuovamnte filtrata
	        parameters.entityName = entityNamePrefix + "OrgMgrRoleView";
	        
	        isOrgMgr = true;
	        isRole = true;
	    } 
	    if(UtilValidate.isNotEmpty(userLoginPermissionSup)) {
	    	isSup = true;
	    }
	    if(UtilValidate.isNotEmpty(userLoginPermissionTop)) {
	    	isTop = true;
	    }	
	    
	}
	
}

// Esistono diversi servizi

Debug.log("executePerformFindWorkEffortRootInqy.groovy entityNamePrefix = " + entityNamePrefix + " and is limited user " + (isOrgMgr || isRole || isSup || isTop));
if ((("WorkEffortRootInqySummary".equals(entityNamePrefix) || "WorkEffortRootInqyPartySummary".equals(entityNamePrefix) || "WorkEffortRootInqyPartySummaryStratOrg".equals(entityNamePrefix)) && (isOrgMgr || isRole || isSup || isTop)) || ("WorkEffortRootInqy".equals(entityNamePrefix)) || ("WorkEffortRoot".equals(entityNamePrefix))) {
	serviceMap = [:];
	serviceName = "";
	
	def workEffortName = UtilValidate.isNotEmpty(parameters.workEffortName) ? parameters.workEffortName : parameters.workEffortName_fld0_value;
	def workEffortNameLang = UtilValidate.isNotEmpty(parameters.workEffortNameLang) ? parameters.workEffortNameLang : parameters.workEffortNameLang_fld0_value;
	def sourceReferenceId = UtilValidate.isNotEmpty(parameters.sourceReferenceId) ? parameters.sourceReferenceId : parameters.sourceReferenceId_fld0_value;
	def weEtch = UtilValidate.isNotEmpty(parameters.weEtch) ? parameters.weEtch : parameters.weEtch_fld0_value;
	def withProcess = UtilValidate.isNotEmpty(parameters.withProcess) ? parameters.withProcess : "N";
	
	def organizationId = parameters.organizationId;
	if (UtilValidate.isEmpty(organizationId)) {
		def workEffortFindServices = new WorkEffortFindServices(delegator, dispatcher); 
		organizationId = workEffortFindServices.getOrganizationId(userLogin, false);
	}
	
	if (UtilValidate.isEmpty(parameters.currentStatusContains) && UtilValidate.isNotEmpty(parameters.currentStatusId_value)) {
		parameters.currentStatusContains = parameters.currentStatusId_value;
	}
	def queryOrderBy = parameters.queryOrderBy;
	
	if ("WorkEffortRoot".equals(entityNamePrefix)) {
		if ("estimatedStartDate".equals(sortField)) {
			if ("CTX_EP".equals(parameters.weContextId) || "CTX_DI".equals(parameters.weContextId)) {
				queryOrderBy = "A.ESTIMATED_START_DATE ASC";
			} else {
				queryOrderBy = "A.ESTIMATED_START_DATE ASC, B.SEQ_ESP ASC";	
			}
		}
		if ("-estimatedStartDate".equals(sortField)) {
			if ("CTX_EP".equals(parameters.weContextId) || "CTX_DI".equals(parameters.weContextId)) {
				queryOrderBy = "A.ESTIMATED_START_DATE DESC";
			} else {
				queryOrderBy = "A.ESTIMATED_START_DATE DESC, B.SEQ_ESP ASC";	
			}
		}	
		if ("workEffortName".equals(sortField)) {
			if ("CTX_EP".equals(parameters.weContextId) || "CTX_DI".equals(parameters.weContextId)) {
				queryOrderBy = "A.WORK_EFFORT_NAME ASC";
			} else {
				queryOrderBy = "A.WORK_EFFORT_NAME ASC, B.SEQ_ESP ASC";	
			}
		}
		if ("-workEffortName".equals(sortField)) {
			if ("CTX_EP".equals(parameters.weContextId) || "CTX_DI".equals(parameters.weContextId)) {
				queryOrderBy = "A.WORK_EFFORT_NAME DESC";
			} else {
				queryOrderBy = "A.WORK_EFFORT_NAME DESC, B.SEQ_ESP ASC";	
			}
		}
		if ("workEffortNameLang".equals(sortField)) {
			if ("CTX_EP".equals(parameters.weContextId) || "CTX_DI".equals(parameters.weContextId)) {
				queryOrderBy = "A.WORK_EFFORT_NAME_LANG ASC";
			} else {
				queryOrderBy = "A.WORK_EFFORT_NAME_LANG ASC, B.SEQ_ESP ASC";	
			}
		}
		if ("-workEffortNameLang".equals(sortField)) {
			if ("CTX_EP".equals(parameters.weContextId) || "CTX_DI".equals(parameters.weContextId)) {
				queryOrderBy = "A.WORK_EFFORT_NAME_LANG DESC";
			} else {
				queryOrderBy = "A.WORK_EFFORT_NAME_LANG DESC, B.SEQ_ESP ASC";	
			}
		}
		if ("weStatusDescr".equals(sortField)) {
			if ("CTX_EP".equals(parameters.weContextId) || "CTX_DI".equals(parameters.weContextId)) {
				queryOrderBy = "C.DESCRIPTION ASC";
			} else {
				queryOrderBy = "C.DESCRIPTION ASC, B.SEQ_ESP ASC";	
			}
		}
		if ("-weStatusDescr".equals(sortField)) {
			if ("CTX_EP".equals(parameters.weContextId) || "CTX_DI".equals(parameters.weContextId)) {
				queryOrderBy = "C.DESCRIPTION DESC";
			} else {
				queryOrderBy = "C.DESCRIPTION DESC, B.SEQ_ESP ASC";	
			}
		}
		if ("weStatusDescrLang".equals(sortField)) {
			if ("CTX_EP".equals(parameters.weContextId) || "CTX_DI".equals(parameters.weContextId)) {
				queryOrderBy = "C.DESCRIPTION_LANG ASC";
			} else {
				queryOrderBy = "C.DESCRIPTION_LANG ASC, B.SEQ_ESP ASC";	
			}
		}
		if ("-weStatusDescrLang".equals(sortField)) {
			if ("CTX_EP".equals(parameters.weContextId) || "CTX_DI".equals(parameters.weContextId)) {
				queryOrderBy = "C.DESCRIPTION_LANG DESC";
			} else {
				queryOrderBy = "C.DESCRIPTION_LANG DESC, B.SEQ_ESP ASC";	
			}
		}		
		if ("weFromName".equals(sortField)) {
			queryOrderBy = "PR.WORK_EFFORT_NAME ASC, B.SEQ_ESP ASC";
		}
		if ("-weFromName".equals(sortField)) {
			queryOrderBy = "PR.WORK_EFFORT_NAME DESC, B.SEQ_ESP ASC";
		}
		if ("weFromNameLang".equals(sortField)) {
			queryOrderBy = "PR.WORK_EFFORT_NAME_LANG ASC, B.SEQ_ESP ASC";
		}
		if ("-weFromNameLang".equals(sortField)) {
			queryOrderBy = "PR.WORK_EFFORT_NAME_LANG DESC, B.SEQ_ESP ASC";
		}
		serviceMap = ["isOrgMgr": isOrgMgr,
		              "isRole": isRole,
		              "isSup": isSup,
		              "isTop": isTop,
		              "orgUnitId": parameters.orgUnitId,
		              "currentStatusId": parameters.currentStatusId,
		              "weStatusDescr": parameters.weStatusDescr,
		              "weStatusDescrLang": parameters.weStatusDescrLang,
		              "workEffortTypeId": parameters.workEffortTypeId,
		              "sourceReferenceId": sourceReferenceId,
		              "workEffortName": workEffortName,
		              "workEffortNameLang": workEffortNameLang,
		              "weEtch": weEtch,
					  "searchDate": searchDate,
					  "childStruct": parameters.childStruct,
		              "weIsTemplate": parameters.weIsTemplate,
		              "weContextId": parameters.weContextId,
		              "gpMenuEnumId": parameters.gpMenuEnumId,
		              "currentStatusContains": parameters.currentStatusContains,
		              "localeSecondarySet": context.localeSecondarySet,
		              "queryOrderBy": queryOrderBy,
		              "withProcess": withProcess];
		
		serviceName = "executeChildPerformFindWorkEffortRoot";
	} 
	if ("WorkEffortRootInqy".equals(entityNamePrefix)) {
		if ("estimatedStartDate".equals(sortField)) {
			if ("CTX_EP".equals(parameters.weContextId) || "CTX_DI".equals(parameters.weContextId)) {
				queryOrderBy = "A.ESTIMATED_START_DATE ASC";
			} else {
				queryOrderBy = "A.ESTIMATED_START_DATE ASC, B.SEQ_ESP ASC";	
			}
		}
		if ("-estimatedStartDate".equals(sortField)) {
			if ("CTX_EP".equals(parameters.weContextId) || "CTX_DI".equals(parameters.weContextId)) {
				queryOrderBy = "A.ESTIMATED_START_DATE DESC";
			} else {
				queryOrderBy = "A.ESTIMATED_START_DATE DESC, B.SEQ_ESP ASC";	
			}
		}	
		if ("workEffortName".equals(sortField)) {
			if ("CTX_EP".equals(parameters.weContextId) || "CTX_DI".equals(parameters.weContextId)) {
				queryOrderBy = "A.WORK_EFFORT_NAME ASC";
			} else {
				queryOrderBy = "A.WORK_EFFORT_NAME ASC, B.SEQ_ESP ASC";	
			}
		}
		if ("-workEffortName".equals(sortField)) {
			if ("CTX_EP".equals(parameters.weContextId) || "CTX_DI".equals(parameters.weContextId)) {
				queryOrderBy = "A.WORK_EFFORT_NAME DESC";
			} else {
				queryOrderBy = "A.WORK_EFFORT_NAME DESC, B.SEQ_ESP ASC";	
			}
		}
		if ("workEffortNameLang".equals(sortField)) {
			if ("CTX_EP".equals(parameters.weContextId) || "CTX_DI".equals(parameters.weContextId)) {
				queryOrderBy = "A.WORK_EFFORT_NAME_LANG ASC";
			} else {
				queryOrderBy = "A.WORK_EFFORT_NAME_LANG ASC, B.SEQ_ESP ASC";	
			}
		}
		if ("-workEffortNameLang".equals(sortField)) {
			if ("CTX_EP".equals(parameters.weContextId) || "CTX_DI".equals(parameters.weContextId)) {
				queryOrderBy = "A.WORK_EFFORT_NAME_LANG DESC";
			} else {
				queryOrderBy = "A.WORK_EFFORT_NAME_LANG DESC, B.SEQ_ESP ASC";	
			}
		}
		if ("weStatusDescr".equals(sortField)) {
			if ("CTX_EP".equals(parameters.weContextId) || "CTX_DI".equals(parameters.weContextId)) {
				queryOrderBy = "C.DESCRIPTION ASC";
			} else {
				queryOrderBy = "C.DESCRIPTION ASC, B.SEQ_ESP ASC";	
			}
		}
		if ("-weStatusDescr".equals(sortField)) {
			if ("CTX_EP".equals(parameters.weContextId) || "CTX_DI".equals(parameters.weContextId)) {
				queryOrderBy = "C.DESCRIPTION DESC";
			} else {
				queryOrderBy = "C.DESCRIPTION DESC, B.SEQ_ESP ASC";	
			}
		}
		if ("weStatusDescrLang".equals(sortField)) {
			if ("CTX_EP".equals(parameters.weContextId) || "CTX_DI".equals(parameters.weContextId)) {
				queryOrderBy = "C.DESCRIPTION_LANG ASC";
			} else {
				queryOrderBy = "C.DESCRIPTION_LANG ASC, B.SEQ_ESP ASC";	
			}
		}
		if ("-weStatusDescrLang".equals(sortField)) {
			if ("CTX_EP".equals(parameters.weContextId) || "CTX_DI".equals(parameters.weContextId)) {
				queryOrderBy = "C.DESCRIPTION_LANG DESC";
			} else {
				queryOrderBy = "C.DESCRIPTION_LANG DESC, B.SEQ_ESP ASC";	
			}
		}		
		if ("weFromName".equals(sortField)) {
			queryOrderBy = "PR.WORK_EFFORT_NAME ASC, B.SEQ_ESP ASC";
		}
		if ("-weFromName".equals(sortField)) {
			queryOrderBy = "PR.WORK_EFFORT_NAME DESC, B.SEQ_ESP ASC";
		}
		if ("weFromNameLang".equals(sortField)) {
			queryOrderBy = "PR.WORK_EFFORT_NAME_LANG ASC, B.SEQ_ESP ASC";
		}
		if ("-weFromNameLang".equals(sortField)) {
			queryOrderBy = "PR.WORK_EFFORT_NAME_LANG DESC, B.SEQ_ESP ASC";
		}		
		serviceMap = ["isOrgMgr": isOrgMgr,
		              "isRole": isRole,
		              "isSup": isSup,
		              "isTop": isTop,
		              "orgUnitId": parameters.orgUnitId,
		              "currentStatusId": parameters.currentStatusId,
		              "weStatusDescr": parameters.weStatusDescr,
		              "weStatusDescrLang": parameters.weStatusDescrLang,
		              "workEffortTypeId": parameters.workEffortTypeId,
		              "sourceReferenceId": sourceReferenceId,
		              "workEffortName": workEffortName,
		              "workEffortNameLang": workEffortNameLang,
		              "weActivation": parameters.weActivation,
		              "workEffortRevisionId": parameters.workEffortRevisionId,
		              "weEtch": weEtch,
					  "searchDate": searchDate,
					  "childStruct": parameters.childStruct,
		              "weIsTemplate": parameters.weIsTemplate,
		              "weContextId": parameters.weContextId,
		              "isRootActive": parameters.isRootActive,
		              "localeSecondarySet": context.localeSecondarySet,
		              "queryOrderBy": queryOrderBy,
		              "withProcess": withProcess];
		
		serviceName = "executeChildPerformFindWorkEffortRootInqy";
	}
	if ("WorkEffortRootInqySummary".equals(entityNamePrefix)) {
		serviceMap = ["isOrgMgr": isOrgMgr,
		              "isRole": isRole,
		              "isSup": isSup,
		              "isTop": isTop,
		              "weContextId": parameters.weContextId,
                      "queryOrderBy": parameters.queryOrderBy];
		
		serviceName = "executeChildPerformFindWorkEffortRootInqySummary";		
	}
	if ("WorkEffortRootInqyPartySummary".equals(entityNamePrefix)) {
	    queryOrderBy = "A.SOURCE_REFERENCE_ID ASC";
        if ("estimatedStartDate".equals(sortField)) {
			queryOrderBy = "A.ESTIMATED_START_DATE ASC";
		}
		if ("-estimatedStartDate".equals(sortField)) {
			queryOrderBy = "A.ESTIMATED_START_DATE DESC";
		}	
		if ("workEffortName".equals(sortField)) {
			queryOrderBy = "A.WORK_EFFORT_NAME ASC";
		}
		if ("-workEffortName".equals(sortField)) {
			queryOrderBy = "A.WORK_EFFORT_NAME DESC";
		}
		if ("workEffortNameLang".equals(sortField)) {
			queryOrderBy = "A.WORK_EFFORT_NAME_LANG ASC";
		}
		if ("-workEffortNameLang".equals(sortField)) {
			queryOrderBy = "A.WORK_EFFORT_NAME_LANG DESC";
		}
		if ("weStatusDescr".equals(sortField)) {
			queryOrderBy = "C.DESCRIPTION ASC";
		}
		if ("-weStatusDescr".equals(sortField)) {
			queryOrderBy = "C.DESCRIPTION DESC";
		}
		if ("weStatusDescrLang".equals(sortField)) {
			queryOrderBy = "C.DESCRIPTION_LANG ASC";
		}
		if ("-weStatusDescrLang".equals(sortField)) {
			queryOrderBy = "C.DESCRIPTION_LANG DESC";
		}
		serviceMap = ["isOrgMgr": isOrgMgr,
		              "isRole": isRole,
		              "isSup": isSup,
		              "isTop": isTop,
		              "weContextId": UtilValidate.isNotEmpty(parameters.weContextId) ? parameters.weContextId : parameters.weContextId_value,
		              "orgUnitId": parameters.orgUnitId,
		              "queryOrderBy": queryOrderBy];
		
		serviceName = "executeChildPerformFindWorkEffortRootInqyPartySummary";		
	}
	if ("WorkEffortRootInqyPartySummaryStratOrg".equals(entityNamePrefix)) {
	    queryOrderBy = "A.SOURCE_REFERENCE_ID ASC";
		if ("estimatedStartDate".equals(sortField)) {
			queryOrderBy = "A.ESTIMATED_START_DATE ASC";
		}
		if ("-estimatedStartDate".equals(sortField)) {
			queryOrderBy = "A.ESTIMATED_START_DATE DESC";
		}	
		if ("workEffortName".equals(sortField)) {
			queryOrderBy = "A.WORK_EFFORT_NAME ASC";
		}
		if ("-workEffortName".equals(sortField)) {
			queryOrderBy = "A.WORK_EFFORT_NAME DESC";
		}
		if ("workEffortNameLang".equals(sortField)) {
			queryOrderBy = "A.WORK_EFFORT_NAME_LANG ASC";
		}
		if ("-workEffortNameLang".equals(sortField)) {
			queryOrderBy = "A.WORK_EFFORT_NAME_LANG DESC";
		}
		if ("weStatusDescr".equals(sortField)) {
			queryOrderBy = "C.DESCRIPTION ASC";
		}
		if ("-weStatusDescr".equals(sortField)) {
			queryOrderBy = "C.DESCRIPTION DESC";
		}
		if ("weStatusDescrLang".equals(sortField)) {
			queryOrderBy = "C.DESCRIPTION_LANG ASC";
		}
		if ("-weStatusDescrLang".equals(sortField)) {
			queryOrderBy = "C.DESCRIPTION_LANG DESC";
		}		
		serviceMap = ["isOrgMgr": isOrgMgr,
		              "isRole": isRole,
		              "isSup": isSup,
		              "isTop": isTop,
		              "weContextId": UtilValidate.isNotEmpty(parameters.weContextId) ? parameters.weContextId : parameters.weContextId_value,
		              "orgUnitId": parameters.orgUnitId,
		              "queryOrderBy": queryOrderBy];
		
		serviceName = "executeChildPerformFindWorkEffortRootInqyPartySummaryStratOrg";		
	}
	serviceMap.put("organizationId", organizationId);
	serviceMap.put("timeZone", context.timeZone);
    Debug.log(" Run sync service " + serviceName + " with "+ serviceMap + ", userLoginId =" + context.userLogin.userLoginId);
    serviceMap.put("userLogin", context.userLogin);
	
	def serviceRes = dispatcher.runSync(serviceName, serviceMap);
    def entitySearch = entityNamePrefix + "OrgMgrRoleView";
    if(UtilValidate.isNotEmpty(serviceRes) && UtilValidate.isNotEmpty(serviceRes.rowList)) {    	
    	def listIt = [];
    	serviceRes.rowList.each { rowItem ->
            if(UtilValidate.isNotEmpty(rowItem)) {   			
    			def gv = delegator.makeValue(entitySearch);
    			gv.putAll(rowItem);
    		    listIt.add(gv);
    		}
    	}
    	context.listIt = listIt;
    } else {
    	context.listIt = [];
    }
    //ricerca automatica per utenti non admin
	parameters.noConditionFind = 'Y'; 
	context.entityName = entitySearch;
	context.inputFields = parameters;
	 
	def prepareResult = FindServices.prepareFind(dispatcher.getDispatchContext(), context);
	// Debug.log(" prepareResult " + prepareResult);
	request.setAttribute("queryString", prepareResult.get("queryString"));
	request.setAttribute("queryStringMap", prepareResult.get("queryStringMap"));
} else {
	Debug.log(" Search WorkEffort with entityName " + parameters.entityName + " and order by " + parameters.orderBy + " entityNamePrefix = " + entityNamePrefix);
	if ("estimatedStartDate".equals(sortField)) {
		if (UtilValidate.isNotEmpty(parameters.entityName) && parameters.entityName.contains("PartySummary")) {
			parameters.orderBy = "estimatedStartDate";
		} else {
			parameters.orderBy = "estimatedStartDate|seqEsp";
		}
	}
	if ("-estimatedStartDate".equals(sortField)) {
		if (UtilValidate.isNotEmpty(parameters.entityName) && parameters.entityName.contains("PartySummary")) {
			parameters.orderBy = "-estimatedStartDate";
		} else {
			parameters.orderBy = "-estimatedStartDate|seqEsp";
		}		
	}	
	if ("workEffortName".equals(sortField)) {
		if (UtilValidate.isNotEmpty(parameters.entityName) && parameters.entityName.contains("PartySummary")) {
			parameters.orderBy = "workEffortName";
		} else {
			parameters.orderBy = "workEffortName|seqEsp";
		}
	}
	if ("-workEffortName".equals(sortField)) {
		if (UtilValidate.isNotEmpty(parameters.entityName) && parameters.entityName.contains("PartySummary")) {
			parameters.orderBy = "-workEffortName";
		} else {
			parameters.orderBy = "-workEffortName|seqEsp";
		}		
	}
	if ("workEffortNameLang".equals(sortField)) {
		if (UtilValidate.isNotEmpty(parameters.entityName) && parameters.entityName.contains("PartySummary")) {
			parameters.orderBy = "workEffortNameLang";
		} else {
			parameters.orderBy = "workEffortNameLang|seqEsp";
		}
	}
	if ("-workEffortNameLang".equals(sortField)) {
		if (UtilValidate.isNotEmpty(parameters.entityName) && parameters.entityName.contains("PartySummary")) {
			parameters.orderBy = "-workEffortNameLang";
		} else {
			parameters.orderBy = "-workEffortNameLang|seqEsp";
		}
	}
	if ("weStatusDescr".equals(sortField)) {
		if (UtilValidate.isNotEmpty(parameters.entityName) && parameters.entityName.contains("PartySummary")) {
			parameters.orderBy = "weStatusDescr";
		} else {
			parameters.orderBy = "weStatusDescr|seqEsp";
		}
	}
	if ("-weStatusDescr".equals(sortField)) {
		if (UtilValidate.isNotEmpty(parameters.entityName) && parameters.entityName.contains("PartySummary")) {
			parameters.orderBy = "-weStatusDescr";
		} else {
			parameters.orderBy = "-weStatusDescr|seqEsp";
		}		
	}
	if ("weStatusDescrLang".equals(sortField)) {
		if (UtilValidate.isNotEmpty(parameters.entityName) && parameters.entityName.contains("PartySummary")) {
			parameters.orderBy = "weStatusDescrLang";
		} else {
			parameters.orderBy = "weStatusDescrLang|seqEsp";
		}
	}
	if ("-weStatusDescrLang".equals(sortField)) {
		if (UtilValidate.isNotEmpty(parameters.entityName) && parameters.entityName.contains("PartySummary")) {
			parameters.orderBy = "-weStatusDescrLang";
		} else {
			parameters.orderBy = "-weStatusDescrLang|seqEsp";
		}
	}	
	if ("weFromName".equals(sortField)) {
		parameters.orderBy = "weFromName|seqEsp";
	}
	if ("-weFromName".equals(sortField)) {
		parameters.orderBy = "-weFromName|seqEsp";
	}
	if ("weFromNameLang".equals(sortField)) {
		parameters.orderBy = "weFromNameLang|seqEsp";
	}
	if ("-weFromNameLang".equals(sortField)) {
		parameters.orderBy = "-weFromNameLang|seqEsp";
	}
	res = GroovyUtil.runScriptAtLocation("com/mapsengineering/base/executePerformFind.groovy", context);
}

def tmpList = context.listIt;
def conditionList = [];

if (UtilValidate.isNotEmpty(context.listIt)){
	if(UtilValidate.isNotEmpty(parameters.searchDate)){
		EntityCondition conditionDate = EntityCondition.makeCondition(
	            EntityCondition.makeCondition("estimatedStartDate", EntityOperator.LESS_THAN_EQUAL_TO, searchDate),
	            EntityCondition.makeCondition("estimatedCompletionDate", EntityOperator.GREATER_THAN_EQUAL_TO, searchDate)
	        );
		conditionList.add(conditionDate);
	}
	
	// Condizione la per la ricerca dei snapshot
	if(parameters.snapshot == 'Y'){
		conditionList.add(EntityCondition.makeCondition(
				EntityCondition.makeCondition("workEffortSnapshotId", EntityOperator.NOT_EQUAL, null),
				EntityCondition.makeCondition("workEffortSnapshotId", EntityOperator.NOT_EQUAL, "")	
				));
		
	} else {
		conditionList.add(EntityCondition.makeCondition("workEffortSnapshotId", null));
	}


	// GN-758 gestione filtri ruolo su obiettivo
	def weAssRoleConditionList = [];
	// filtro tipo ruolo
	if (UtilValidate.isNotEmpty(parameters.weResponsibleRoleTypeId)) {
	    weAssRoleConditionList.add(EntityCondition.makeCondition("roleTypeId", parameters.weResponsibleRoleTypeId));
	}
	// filtro tipo ruolo
	if (UtilValidate.isNotEmpty(parameters.weResponsiblePartyId)) {
	    weAssRoleConditionList.add(EntityCondition.makeCondition("partyId", parameters.weResponsiblePartyId));
	}
	
	if (UtilValidate.isNotEmpty(weAssRoleConditionList)) {
		def weAssRoleList = delegator.findList("WorkEffortAssignmentRoleView", EntityCondition.makeCondition(weAssRoleConditionList), null, null, null, false);		    
	    def workEffortIdFromWeAssRoleList = EntityUtil.getFieldListFromEntityList(weAssRoleList, "workEffortId", true);
	    conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.IN, workEffortIdFromWeAssRoleList));
	}

	
	 if (UtilValidate.isNotEmpty(conditionList)){
		Debug.log(" - Filer list with condition = " + conditionList);
		context.listIt = EntityUtil.filterByCondition(tmpList, EntityCondition.makeCondition(conditionList));
	}
	/* 
     *  Adesso posso cercare sia per responsibile che per tipo
     *  Ricerca per responsabile, per ogni scheda controllo se è responsabile quello inserito e se appartiene alla data della scheda
     * */
     if ( (UtilValidate.isNotEmpty(parameters.responsibleRoleTypeId) || UtilValidate.isNotEmpty(parameters.responsiblePartyId)) && UtilValidate.isNotEmpty(context.listIt)) {
         
         Debug.log("********** ricerca per responsabile parameters.responsibleRoleTypeId="+parameters.responsibleRoleTypeId);
         Debug.log("********** ricerca per responsabile parameters.responsiblePartyId="+parameters.responsiblePartyId);
         def list = [];
         
         for (int i=0; i < context.listIt.size(); i++ ) {
             def element = context.listIt.get(i);
             
             def prCond = [];
             prCond.add(EntityCondition.makeCondition("partyRelationshipTypeId", "ORG_RESPONSIBLE"));             
             prCond.add(EntityCondition.makeCondition("partyIdFrom", element.orgUnitId));
             prCond.add(EntityCondition.makeCondition("roleTypeIdFrom", element.orgUnitRoleTypeId));
             prCond.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, element.estimatedCompletionDate));
             
             if (UtilValidate.isNotEmpty(parameters.responsibleRoleTypeId)) {
                 prCond.add(EntityCondition.makeCondition("roleTypeIdTo", parameters.responsibleRoleTypeId));
             }
             if (UtilValidate.isNotEmpty(parameters.responsiblePartyId)) {
                 prCond.add(EntityCondition.makeCondition("partyIdTo", parameters.responsiblePartyId));
             }
             
             prCond.add(EntityCondition.makeCondition(
                 EntityCondition.makeCondition("thruDate", null),
                 EntityOperator.OR,
                 EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, element.estimatedCompletionDate)
                 ));
             
             def prList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(prCond), null, null, null, false);
             if (UtilValidate.isNotEmpty(prList)) {
                 list.add(element);
             }
         }
         // rimmetto la lista filtrata!!
         context.listIt = list;
     }
      
}

request.setAttribute("listIt", context.listIt);

if (res == "success") {
	// check if this is massive-print-search or export-search 
    res = GroovyUtil.runScriptAtLocation("com/mapsengineering/base/checkExportSearchResult.groovy", context);
}

return res;
