import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import javolution.util.FastMap;
import com.mapsengineering.workeffortext.util.WorkEffortTypeCntParamsEvaluator;

def localeSecondarySet = context.localeSecondarySet;

//nomi dei campi
context.workEffortNameField = "Y".equals(context.localeSecondarySet) ?  "workEffortNameLang" : "workEffortName";
context.workEffortTypeDescriptionField = "Y".equals(context.localeSecondarySet) ?  "weTypeDescriptionLang" : "weTypeDescription";
context.workEffortDescriptionField = "Y".equals(context.localeSecondarySet) ? "descriptionLang" : "description";
context.weOrgPartyDescrField = "Y".equals(context.localeSecondarySet) ?  "weOrgPartyDescrLang" : "weOrgPartyDescr";
context.orderByField = "weEtch, " + context.workEffortNameField;

Debug.log(" ********* getUniqueWorkeffortAssoc.groovy workEffortId " + context.workEffortId);
Debug.log(" ********* getUniqueWorkeffortAssoc.groovy workEffortTypeId " + context.workEffortTypeId);
if (UtilValidate.isNotEmpty(context.workEffortTypeId)) {
	def orderBy = "Y".equals(localeSecondarySet) ? ["commentsLang"] : ["comments"];
	
	def uniqueWorkEffortAssocIndexWETATO = 0;
	EntityCondition entityConditionWETATO = EntityCondition.makeCondition(EntityCondition.makeCondition("workEffortId", context.workEffortId), EntityCondition.makeCondition("workEffortTypeId", context.workEffortTypeId), 
			EntityCondition.makeCondition("isUnique", "Y"));
	def workEffortAssocTypeListWETATO = delegator.findList("WorkEffortAndTypeAssocToView2", entityConditionWETATO, null, orderBy, null, false);
	if (UtilValidate.isNotEmpty(workEffortAssocTypeListWETATO)) {
		workEffortAssocTypeListWETATO.each { workEffortAssocTypeWETATO ->
			if("Y".equals(workEffortAssocTypeWETATO.hasResp)) {
			    // sebbene potrebbero esistere piu' di una relazione con hasResp, per adesso ne gestiamo 1
                // default poi sovrascritti da paramas
                context["showAssocWeightTOHasResp"] = "N";
                
                context["workEffortIdFromHasResp"] = workEffortAssocTypeWETATO.fromWorkEffortId;
				context["assocWeightTOHasResp"] = workEffortAssocTypeWETATO.assocWeight;
				context["workEffortTypeDescTOHasResp"] = "Y".equals(localeSecondarySet) ? workEffortAssocTypeWETATO.commentsLang : workEffortAssocTypeWETATO.comments;
				
				// recupero params
                WorkEffortTypeCntParamsEvaluator paramsEvaluator = new WorkEffortTypeCntParamsEvaluator(null, null, delegator);
				def mapParams = paramsEvaluator.getParams(context.workEffortTypeId, workEffortAssocTypeWETATO.contentId, false);
				Debug.log(" ********* getUniqueWorkeffortAssoc.groovy WETATO hasResp mapParams " + mapParams);
                if (UtilValidate.isNotEmpty(mapParams)) {
				    context["showAssocWeightTOHasResp"] = mapParams.showAssocWeight;
				}
			} else {
			    Debug.log(" ********* getUniqueWorkeffortAssoc.groovy WETATO workEffortAssocType " + workEffortAssocTypeWETATO.workEffortAssocTypeId + " - " + workEffortAssocTypeWETATO.workEffortTypeIdRef + " - " + workEffortAssocTypeWETATO.fromWorkEffortId);
                // sempre
			    context["wefromWetoEnumId" + uniqueWorkEffortAssocIndexWETATO] = "WETATO";
                
			    // default poi valutati coi params
                context["workEffortNameValue" + uniqueWorkEffortAssocIndexWETATO] = "";
                context["weOrgPartyDescrValue" + uniqueWorkEffortAssocIndexWETATO] = "";
                context["weEtchValue" + uniqueWorkEffortAssocIndexWETATO] = "";
                context["workEffortNameDescription" + uniqueWorkEffortAssocIndexWETATO] = "";
                
			    // default poi sovrascritti da paramas
			    context["showAssocWeight" + uniqueWorkEffortAssocIndexWETATO] = "N";
				context["showEtch" + uniqueWorkEffortAssocIndexWETATO] = "Y";
                context["showOrgUnit" + uniqueWorkEffortAssocIndexWETATO] = "N";
                context["orgUnitIdRelation" + uniqueWorkEffortAssocIndexWETATO] = context.defaultOrganizationPartyId;
                context["entityNameExtended" + uniqueWorkEffortAssocIndexWETATO] = "";
                context["assA1" + uniqueWorkEffortAssocIndexWETATO] = "";
                context["assA2" + uniqueWorkEffortAssocIndexWETATO] = "";
                context["assA3" + uniqueWorkEffortAssocIndexWETATO] = "";
                context["assB1" + uniqueWorkEffortAssocIndexWETATO] = "";
                context["assB2" + uniqueWorkEffortAssocIndexWETATO] = "";
                // i valori di assocLevel* sono utilizzati per il orgUnitIdListAssoc<i> e si ricavano nella chiamata ajax getFilterPartyRoleWorkEffort
                // perche' cambiano in base all'unita organizzativa
                //context.assocLevelSameUO = "N";
                //context.assocLevelParentUO = "N";
                //context.assocLevelChildUO = "N";
                //context.assocLevelSisterUO = "N";
                
				context["workEffortIdFrom" + uniqueWorkEffortAssocIndexWETATO] = workEffortAssocTypeWETATO.fromWorkEffortId;
				context["fromWorkEffortParentId" + uniqueWorkEffortAssocIndexWETATO] = workEffortAssocTypeWETATO.fromWorkEffortParentId;
				context["workEffortTypeId" + uniqueWorkEffortAssocIndexWETATO] = workEffortAssocTypeWETATO.workEffortTypeIdRef;
				context["workEffortAssocTypeId" + uniqueWorkEffortAssocIndexWETATO] = workEffortAssocTypeWETATO.workEffortAssocTypeId;
				context["workEffortTypeDesc" + uniqueWorkEffortAssocIndexWETATO] = "Y".equals(localeSecondarySet) ? workEffortAssocTypeWETATO.commentsLang : workEffortAssocTypeWETATO.comments;
				context["assocWeight" + uniqueWorkEffortAssocIndexWETATO] = workEffortAssocTypeWETATO.assocWeight;
				
				// recupero params
				WorkEffortTypeCntParamsEvaluator paramsEvaluator = new WorkEffortTypeCntParamsEvaluator(null, null, delegator);
				// Debug.log(" - WETATO " + context.workEffortTypeId + " " + workEffortAssocTypeWETATO.contentId);
                def mapParams = paramsEvaluator.getParams(context.workEffortTypeId, workEffortAssocTypeWETATO.contentId, false);
				Debug.log(" ********* getUniqueWorkeffortAssoc.groovy WETATO mapParams " + mapParams);
				
				if (UtilValidate.isNotEmpty(mapParams)) {
                    context["showAssocWeight" + uniqueWorkEffortAssocIndexWETATO] = mapParams.showAssocWeight;
                    context["showEtch" + uniqueWorkEffortAssocIndexWETATO] = mapParams.showEtch;
                    context["showOrgUnit" + uniqueWorkEffortAssocIndexWETATO] = mapParams.showOrgUnit;
                    context["orgUnitIdRelation" + uniqueWorkEffortAssocIndexWETATO] = mapParams.orgUnitIdRelation;
                    context["entityNameExtended" + uniqueWorkEffortAssocIndexWETATO] = mapParams.entityNameExtended;
                    context["showRootLink" + uniqueWorkEffortAssocIndexWETATO] = mapParams.showRootLink;
                    context["assA1" + uniqueWorkEffortAssocIndexWETATO] = mapParams.assA1;
                    context["assA2" + uniqueWorkEffortAssocIndexWETATO] = mapParams.assA2;
                    context["assA3" + uniqueWorkEffortAssocIndexWETATO] = mapParams.assA3;
                    context["assB1" + uniqueWorkEffortAssocIndexWETATO] = mapParams.assB1;
                    context["assB2" + uniqueWorkEffortAssocIndexWETATO] = mapParams.assB2;
                }
                if ("Y".equals(context["showOrgUnit" + uniqueWorkEffortAssocIndexWETATO])) {
			        context["orderByField" + uniqueWorkEffortAssocIndexWETATO] = context.weOrgPartyDescrField;
			        context["weOrgPartyDescrValue" + uniqueWorkEffortAssocIndexWETATO] = context.weOrgPartyDescrField;
			        context["workEffortNameDescription" + uniqueWorkEffortAssocIndexWETATO] = "Y".equals(context.localeSecondarySet) ? "@{weOrgPartyDescrLang}" : "@{weOrgPartyDescr}";
			    } else {
			        context["orderByField" + uniqueWorkEffortAssocIndexWETATO] = context.workEffortNameField;
                    if ("Y".equals(context["showEtch" + uniqueWorkEffortAssocIndexWETATO])) {
			            context["weEtchValue" + uniqueWorkEffortAssocIndexWETATO] = "weEtch";
			            context["orderByField" + uniqueWorkEffortAssocIndexWETATO] = "weEtch";
			        }  else if ("C".equals(context["showEtch" + uniqueWorkEffortAssocIndexWETATO])){
			            context["weEtchValue" + uniqueWorkEffortAssocIndexWETATO] = "sourceReferenceId";
			            context["orderByField" + uniqueWorkEffortAssocIndexWETATO] = "sourceReferenceId";
			        }
			        context["workEffortNameValue" + uniqueWorkEffortAssocIndexWETATO] = context.workEffortNameField;
			        context["workEffortNameDescription" + uniqueWorkEffortAssocIndexWETATO] = context["workEffortNameDescription" + uniqueWorkEffortAssocIndexWETATO] + ("Y".equals(context.localeSecondarySet) ? " @{workEffortNameLang}" : " @{workEffortName}");
			    }
			}
			uniqueWorkEffortAssocIndexWETATO++;
		}
	}

	def uniqueWorkEffortAssocIndexWETAFROM = 5;
	EntityCondition entityConditionWETAFROM = EntityCondition.makeCondition(EntityCondition.makeCondition("workEffortId", context.workEffortId), EntityCondition.makeCondition("workEffortTypeId", context.workEffortTypeId), 
			EntityCondition.makeCondition("isUnique", "Y"));
	def workEffortAssocTypeListWETAFROM = delegator.findList("WorkEffortAndTypeAssocFromView", entityConditionWETAFROM, null, orderBy, null, false);
	if (UtilValidate.isNotEmpty(workEffortAssocTypeListWETAFROM)) {
		workEffortAssocTypeListWETAFROM.each { workEffortAssocTypeWETAFROM ->
			if("Y".equals(workEffortAssocTypeWETAFROM.hasResp)) {
			    // sebbene potrebbero esistere piu' di una relazione con hasResp, per adesso ne gestiamo 1
			    Debug.log(" ********* getUniqueWorkeffortAssoc.groovy WETAFROM workEffortAssocType " + workEffortAssocTypeWETAFROM.workEffortAssocTypeId + " - " + workEffortAssocTypeWETAFROM.assocWeight + " - " + workEffortAssocTypeWETAFROM.comments);
				context["showAssocWeightTOHasResp"] = "N";
				context["workEffortIdToHasResp"] = workEffortAssocTypeWETATO.toWorkEffortId;
				context["assocWeightTOHasResp"] = workEffortAssocTypeWETAFROM.assocWeight;
				context["workEffortTypeDescTOHasResp"] = "Y".equals(localeSecondarySet) ? workEffortAssocTypeWETAFROM.commentsLang : workEffortAssocTypeWETAFROM.comments;
				WorkEffortTypeCntParamsEvaluator paramsEvaluator = new WorkEffortTypeCntParamsEvaluator(null, null, delegator);
				def mapParams = paramsEvaluator.getParams(context.workEffortTypeId, workEffortAssocTypeWETAFROM.contentId, false);
				Debug.log(" ********* getUniqueWorkeffortAssoc.groovy WETAFROM hasResp mapParams " + mapParams);
                if (UtilValidate.isNotEmpty(mapParams)) {  
				    context["showAssocWeightTOHasResp"] = mapParams.showAssocWeight;
				}
			} else {
			    Debug.log(" ********* getUniqueWorkeffortAssoc.groovy WETAFROM workEffortAssocType " + workEffortAssocTypeWETAFROM.workEffortAssocTypeId + " - " + workEffortAssocTypeWETAFROM.workEffortTypeIdRef + " - " + workEffortAssocTypeWETAFROM.toWorkEffortId);
                // sempre
			    context["wefromWetoEnumId" + uniqueWorkEffortAssocIndexWETAFROM] = "WETAFROM";
                
                // default poi valutati coi params
                context["workEffortNameValue" + uniqueWorkEffortAssocIndexWETAFROM] = "";
                context["weOrgPartyDescrValue" + uniqueWorkEffortAssocIndexWETAFROM] = "";
                context["weEtchValue" + uniqueWorkEffortAssocIndexWETAFROM] = "";
                context["workEffortNameDescription" + uniqueWorkEffortAssocIndexWETAFROM] = "";
                
                // default poi sovrascritti da params
                context["showAssocWeight" + uniqueWorkEffortAssocIndexWETAFROM] = "N";
                context["showEtch" + uniqueWorkEffortAssocIndexWETAFROM] = "Y";
                context["showOrgUnit" + uniqueWorkEffortAssocIndexWETAFROM] = "N";
                context["orgUnitIdRelation" + uniqueWorkEffortAssocIndexWETAFROM] = context.defaultOrganizationPartyId;
                context["entityNameExtended" + uniqueWorkEffortAssocIndexWETAFROM] = "";
                context["assA1" + uniqueWorkEffortAssocIndexWETAFROM] = "";
                context["assA2" + uniqueWorkEffortAssocIndexWETAFROM] = "";
                context["assA3" + uniqueWorkEffortAssocIndexWETAFROM] = "";
                context["assB1" + uniqueWorkEffortAssocIndexWETAFROM] = "";
                context["assB2" + uniqueWorkEffortAssocIndexWETAFROM] = "";
                // i valori di assocLevel* sono utilizzati per il orgUnitIdListAssoc<i> e si ricavano nella chiamata ajax getFilterPartyRoleWorkEffort
                // perche' cambiano in base all'unita organizzativa
                //context.assocLevelSameUO = "N";
                //context.assocLevelParentUO = "N";
                //context.assocLevelChildUO = "N";
                //context.assocLevelSisterUO = "N";
                
                context["workEffortIdTo" + uniqueWorkEffortAssocIndexWETAFROM] = workEffortAssocTypeWETAFROM.toWorkEffortId;
                context["toWorkEffortParentId" + uniqueWorkEffortAssocIndexWETAFROM] = workEffortAssocTypeWETAFROM.toWorkEffortParentId;
                context["workEffortTypeId" + uniqueWorkEffortAssocIndexWETAFROM] = workEffortAssocTypeWETAFROM.workEffortTypeIdRef;
                context["workEffortAssocTypeId" + uniqueWorkEffortAssocIndexWETAFROM] = workEffortAssocTypeWETAFROM.workEffortAssocTypeId;
                context["workEffortTypeDesc" + uniqueWorkEffortAssocIndexWETAFROM] = "Y".equals(localeSecondarySet) ? workEffortAssocTypeWETAFROM.commentsLang : workEffortAssocTypeWETAFROM.comments;
                context["assocWeight" + uniqueWorkEffortAssocIndexWETAFROM] = workEffortAssocTypeWETAFROM.assocWeight;
                
                // recupero params
                WorkEffortTypeCntParamsEvaluator paramsEvaluator = new WorkEffortTypeCntParamsEvaluator(null, null, delegator);
                def mapParams = paramsEvaluator.getParams(context.workEffortTypeId, workEffortAssocTypeWETAFROM.contentId, false);
                Debug.log(" ********* getUniqueWorkeffortAssoc.groovy WETAFROM mapParams " + mapParams);
                if (UtilValidate.isNotEmpty(mapParams)) {  
                    context["showAssocWeight" + uniqueWorkEffortAssocIndexWETAFROM] = mapParams.showAssocWeight;
                    context["showEtch" + uniqueWorkEffortAssocIndexWETAFROM] = mapParams.showEtch;
                    context["showOrgUnit" + uniqueWorkEffortAssocIndexWETAFROM] = mapParams.showOrgUnit;
                    context["orgUnitIdRelation" + uniqueWorkEffortAssocIndexWETAFROM] = mapParams.orgUnitIdRelation;
                    context["entityNameExtended" + uniqueWorkEffortAssocIndexWETAFROM] = mapParams.entityNameExtended;
                    context["showRootLink" + uniqueWorkEffortAssocIndexWETAFROM] = mapParams.showRootLink;
                    context["assA1" + uniqueWorkEffortAssocIndexWETAFROM] = mapParams.assA1;
                    context["assA2" + uniqueWorkEffortAssocIndexWETAFROM] = mapParams.assA2;
                    context["assA3" + uniqueWorkEffortAssocIndexWETAFROM] = mapParams.assA3;
                    context["assB1" + uniqueWorkEffortAssocIndexWETAFROM] = mapParams.assB1;
                    context["assB2" + uniqueWorkEffortAssocIndexWETAFROM] = mapParams.assB2;
                }
                if ("Y".equals(context["showOrgUnit" + uniqueWorkEffortAssocIndexWETAFROM])) {
                    context["orderByField" + uniqueWorkEffortAssocIndexWETAFROM] = context.weOrgPartyDescrField;
                    context["weOrgPartyDescrValue" + uniqueWorkEffortAssocIndexWETAFROM] = context.weOrgPartyDescrField;
                    context["workEffortNameDescription" + uniqueWorkEffortAssocIndexWETAFROM] = "Y".equals(context.localeSecondarySet) ? "@{weOrgPartyDescrLang}" : "@{weOrgPartyDescr}";
                } else{
                    context["orderByField" + uniqueWorkEffortAssocIndexWETAFROM] = context.workEffortNameField;
                    if ("Y".equals(context["showEtch" + uniqueWorkEffortAssocIndexWETAFROM])) {
                        context["weEtchValue" + uniqueWorkEffortAssocIndexWETAFROM] = "weEtch";
                        context["orderByField" + uniqueWorkEffortAssocIndexWETAFROM] = "weEtch";
                    }  else if ("C".equals(context["showEtch" + uniqueWorkEffortAssocIndexWETAFROM])){
                        context["weEtchValue" + uniqueWorkEffortAssocIndexWETAFROM] = "sourceReferenceId";
                        context["orderByField" + uniqueWorkEffortAssocIndexWETAFROM] = "sourceReferenceId";
                    }
                    context["workEffortNameValue" + uniqueWorkEffortAssocIndexWETAFROM] = context.workEffortNameField;
                    context["workEffortNameDescription" + uniqueWorkEffortAssocIndexWETAFROM] = context["workEffortNameDescription" + uniqueWorkEffortAssocIndexWETAFROM] + ("Y".equals(context.localeSecondarySet) ? " @{workEffortNameLang}" : " @{workEffortName}");
                }
			}
			
			uniqueWorkEffortAssocIndexWETAFROM++;
		}
	}
}
