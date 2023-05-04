import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import javolution.util.FastMap;
import com.mapsengineering.workeffortext.util.WorkEffortTypeCntParamsEvaluator;

def localeSecondarySet = context.localeSecondarySet;

//Gestione del workEffortIdFrom0, workEffortIdFrom1, workEffortIdTo5, ...

//nomi dei campi
context.workEffortNameField = "Y".equals(context.localeSecondarySet) ?  "workEffortNameLang" : "workEffortName";
context.workEffortTypeDescriptionField = "Y".equals(context.localeSecondarySet) ?  "weTypeDescriptionLang" : "weTypeDescription";
context.workEffortDescriptionField = "Y".equals(context.localeSecondarySet) ? "descriptionLang" : "description";
context.weOrgPartyDescrField = "Y".equals(context.localeSecondarySet) ?  "weOrgPartyDescrLang" : "weOrgPartyDescr";
context.orderByField = "weEtch, " + context.workEffortNameField;

Debug.log(" ********* getUniqueWorkeffortAssoc.groovy workEffortId " + context.workEffortId);
Debug.log(" ********* getUniqueWorkeffortAssoc.groovy workEffortTypeId " + context.workEffortTypeId);
if (UtilValidate.isNotEmpty(context.workEffortTypeId)) {
	def orderBy = "Y".equals(localeSecondarySet) ? ["-commentsLang"] : ["-comments"];
	
	// Iterazione per i primi 5 contentId
	for (int uniqueWorkEffortAssocIndexWETATO = 0; uniqueWorkEffortAssocIndexWETATO < 5; uniqueWorkEffortAssocIndexWETATO++) {
	    Debug.log(" ********* getUniqueWorkeffortAssoc.groovy WETATO uniqueWorkEffortAssocIndexWETATO " + uniqueWorkEffortAssocIndexWETATO);
        // Debug.log(" ********* getUniqueWorkeffortAssoc.groovy WETATO workEffortTypeId " + context["workEffortTypeId" + uniqueWorkEffortAssocIndexWETATO]);
	    // pk sono: 
        // - work_effort_type_id 
        // - work_effort_assoc_type_id
        // - wefrom_weto_enum_id
        // - work_effort_type_id_ref
	    def condList = [];
	    condList.add(EntityCondition.makeCondition("wefromWetoEnumId", "WETATO"));
	    condList.add(EntityCondition.makeCondition("workEffortTypeId", context.workEffortTypeId));
	    if(uniqueWorkEffortAssocIndexWETATO == 0 ) {
	        condList.add(EntityCondition.makeCondition("contentId", "WEFLD_WETO"));
	    } else {
	        condList.add(EntityCondition.makeCondition("contentId", "WEFLD_WETO" + (uniqueWorkEffortAssocIndexWETATO + 1)));
        }
	    condList.add(EntityCondition.makeCondition("isUnique", "Y"));

	    def workEffortTypeListAssoc = null;
	    def workEffortTypeAssocAndAssocTypeList = delegator.findList("WorkEffortTypeAssocAndAssocType", EntityCondition.makeCondition(condList), null, null, null, false);
	    // Debug.log(" ********* getUniqueWorkeffortAssoc.groovy WETATO workEffortTypeAssocAndAssocTypeList " + workEffortTypeAssocAndAssocTypeList);
	    if (UtilValidate.isNotEmpty(workEffortTypeAssocAndAssocTypeList)) {
	        workEffortTypeListAssoc = EntityUtil.getFieldListFromEntityList(workEffortTypeAssocAndAssocTypeList, "workEffortTypeIdRef", true);
	    } else {
	        workEffortTypeListAssoc = ["![null-field],"];
	    }

	    // Debug.log(" ********* getUniqueWorkeffortAssoc.groovy WETATO context.workEffortTypeListAssoc " + context["workEffortTypeListAssoc" + uniqueWorkEffortAssocIndexWETATO]);
	    // Debug.log(" ********* getUniqueWorkeffortAssoc.groovy WETATO context.workEffortTypeListAssoc " + context["workEffortTypeId" + uniqueWorkEffortAssocIndexWETATO]);
	    
	
    	// la relazione e' unica, per un dato work_effort_assoc_type_id, ma puo avere 2 workEffortTypeId diversi
	    def entityConditionWETATO = [];
	    entityConditionWETATO.add(EntityCondition.makeCondition("workEffortId", context.workEffortId));
        if(uniqueWorkEffortAssocIndexWETATO == 0 ) {
            entityConditionWETATO.add(EntityCondition.makeCondition("contentId", "WEFLD_WETO"));
        } else {
            entityConditionWETATO.add(EntityCondition.makeCondition("contentId", "WEFLD_WETO" + (uniqueWorkEffortAssocIndexWETATO + 1)));
        }
        entityConditionWETATO.add(EntityCondition.makeCondition("workEffortTypeIdRef", EntityOperator.IN, workEffortTypeListAssoc));
    	entityConditionWETATO.add(EntityCondition.makeCondition("workEffortTypeId", context.workEffortTypeId));
    	entityConditionWETATO.add(EntityCondition.makeCondition("isUnique", "Y"));
    	
    	def workEffortAssocTypeListWETATO = delegator.findList("WorkEffortAndTypeAssocToView3", EntityCondition.makeCondition(entityConditionWETATO), null, orderBy, null, false);
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
                    
    			    // si presuppone che ci sia una sola tipologia di relazione, quindi in caso di piu' record si prende l'ultima
                    context["workEffortAssocTypeId" + uniqueWorkEffortAssocIndexWETATO] = workEffortAssocTypeWETATO.workEffortAssocTypeId;
                    // si presuppone ci siano piu' tipologie di obiettivi, ma si visualizza solo 1 descrizione, in caso di piu' record si prende l'ultima
                    context["workEffortTypeId" + uniqueWorkEffortAssocIndexWETATO] = StringUtil.join(workEffortTypeListAssoc, ",");
                    context["workEffortTypeDesc" + uniqueWorkEffortAssocIndexWETATO] = "Y".equals(localeSecondarySet) ? workEffortAssocTypeWETATO.commentsLang : workEffortAssocTypeWETATO.comments;
                    
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
                    //context.assocLevelTopUO = "N";
                    
                    if (UtilValidate.isNotEmpty(workEffortAssocTypeWETATO.fromWorkEffortId)) {
                        context["workEffortIdFrom" + uniqueWorkEffortAssocIndexWETATO] = workEffortAssocTypeWETATO.fromWorkEffortId;
                        context["fromWorkEffortParentId" + uniqueWorkEffortAssocIndexWETATO] = workEffortAssocTypeWETATO.fromWorkEffortParentId;
                        context["assocWeight" + uniqueWorkEffortAssocIndexWETATO] = workEffortAssocTypeWETATO.assocWeight;
                    }
    				
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
                        context["Ass" + uniqueWorkEffortAssocIndexWETATO] = getAss(mapParams);
                        if ("Ref".equals(mapParams.entityNameExtended)) {
                        	context["workEffortIdRef" + uniqueWorkEffortAssocIndexWETATO] = context.workEffortId;
                        }
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
    		}
    	}
	}

	// Iterazione per gli ultimi 5 contentId
    for (int uniqueWorkEffortAssocIndexWETAFROM = 0; uniqueWorkEffortAssocIndexWETAFROM < 5; uniqueWorkEffortAssocIndexWETAFROM++) {
        // Debug.log(" ********* getUniqueWorkeffortAssoc.groovy WETAFROM uniqueWorkEffortAssocIndexWETAFROM " + uniqueWorkEffortAssocIndexWETAFROM);
        // pk sono: 
        // - work_effort_type_id 
        // - work_effort_assoc_type_id
        // - wefrom_weto_enum_id
        // - work_effort_type_id_ref
        def condList = [];
        condList.add(EntityCondition.makeCondition("wefromWetoEnumId", "WETAFROM"));
        condList.add(EntityCondition.makeCondition("workEffortTypeId", context.workEffortTypeId));
        if(uniqueWorkEffortAssocIndexWETAFROM == 5 ) {
            condList.add(EntityCondition.makeCondition("contentId", "WEFLD_WEFROM"));
        } else {
            condList.add(EntityCondition.makeCondition("contentId", "WEFLD_WEFROM"  + (uniqueWorkEffortAssocIndexWETAFROM + 1)));
        }
        condList.add(EntityCondition.makeCondition("isUnique", "Y"));

        def workEffortTypeListAssoc = null;
        def workEffortTypeAssocAndAssocTypeList = delegator.findList("WorkEffortTypeAssocAndAssocType", EntityCondition.makeCondition(condList), null, null, null, false);
        if (UtilValidate.isNotEmpty(workEffortTypeAssocAndAssocTypeList)) {
            workEffortTypeListAssoc = EntityUtil.getFieldListFromEntityList(workEffortTypeAssocAndAssocTypeList, "workEffortTypeIdRef", true);
        } else {
            workEffortTypeListAssoc = ["![null-field],"];
        }

        // Debug.log(" ********* getUniqueWorkeffortAssoc.groovy WETAFROM context.workEffortTypeListAssoc " + context["workEffortTypeListAssoc" + (uniqueWorkEffortAssocIndexWETAFROM + 5)]);
        // Debug.log(" ********* getUniqueWorkeffortAssoc.groovy WETAFROM context.workEffortTypeListAssoc " + context["workEffortTypeId" + (uniqueWorkEffortAssocIndexWETAFROM + 5)]);
        
    
        
        // la relazione e' unica, per un dato work_effort_assoc_type_id, ma puo avere 2 workEffortTypeId diversi
        def entityConditionWETAFROM = [];
        entityConditionWETAFROM.add(EntityCondition.makeCondition("workEffortId", context.workEffortId)) 
        if(uniqueWorkEffortAssocIndexWETAFROM == 0 ) {
            entityConditionWETAFROM.add(EntityCondition.makeCondition("contentId", "WEFLD_WEFROM"));
        } else {
            entityConditionWETAFROM.add(EntityCondition.makeCondition("contentId", "WEFLD_WEFROM" + (uniqueWorkEffortAssocIndexWETAFROM + 1)));
        }
        entityConditionWETAFROM.add(EntityCondition.makeCondition("workEffortTypeIdRef", EntityOperator.IN, workEffortTypeListAssoc));
        entityConditionWETAFROM.add(EntityCondition.makeCondition("workEffortTypeId", context.workEffortTypeId));
        entityConditionWETAFROM.add(EntityCondition.makeCondition("isUnique", "Y"));

        def workEffortAssocTypeListWETAFROM = delegator.findList("WorkEffortAndTypeAssocFromView", EntityCondition.makeCondition(entityConditionWETAFROM), null, orderBy, null, false);
    	if (UtilValidate.isNotEmpty(workEffortAssocTypeListWETAFROM)) {
    		workEffortAssocTypeListWETAFROM.each { workEffortAssocTypeWETAFROM ->
    			if("Y".equals(workEffortAssocTypeWETAFROM.hasResp)) {
    			    // sebbene potrebbero esistere piu' di una relazione con hasResp, per adesso ne gestiamo 1
    			    // Debug.log(" ********* getUniqueWorkeffortAssoc.groovy WETAFROM workEffortAssocType " + workEffortAssocTypeWETAFROM.workEffortAssocTypeId + " - " + workEffortAssocTypeWETAFROM.assocWeight + " - " + workEffortAssocTypeWETAFROM.comments);
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
                    
    			    // si presuppone che ci sia una sola tipologia di relazione, quindi in caso di piu' record si prende l'ultima
    			    context["workEffortAssocTypeId" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = workEffortAssocTypeWETAFROM.workEffortAssocTypeId;
                    // si presuppone ci siano piu' tipologie di obiettivi, ma si visualizza solo 1 descrizione, in caso di piu' record si prende l'ultima
                    context["workEffortTypeId" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = StringUtil.join(workEffortTypeListAssoc, ",");
                    context["workEffortTypeDesc" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = "Y".equals(localeSecondarySet) ? workEffortAssocTypeWETAFROM.commentsLang : workEffortAssocTypeWETAFROM.comments;
                    
    			    // sempre
    			    context["wefromWetoEnumId" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = "WETAFROM";
                    
                    // default poi valutati coi params
                    context["workEffortNameValue" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = "";
                    context["weOrgPartyDescrValue" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = "";
                    context["weEtchValue" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = "";
                    context["workEffortNameDescription" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = "";
                    
                    // default poi sovrascritti da params
                    context["showAssocWeight" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = "N";
                    context["showEtch" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = "Y";
                    context["showOrgUnit" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = "N";
                    context["orgUnitIdRelation" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = context.defaultOrganizationPartyId;
                    context["entityNameExtended" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = "";
                    context["assA1" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = "";
                    context["assA2" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = "";
                    context["assA3" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = "";
                    context["assB1" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = "";
                    context["assB2" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = "";
                    // i valori di assocLevel* sono utilizzati per il orgUnitIdListAssoc<i> e si ricavano nella chiamata ajax getFilterPartyRoleWorkEffort
                    // perche' cambiano in base all'unita organizzativa
                    //context.assocLevelSameUO = "N";
                    //context.assocLevelParentUO = "N";
                    //context.assocLevelChildUO = "N";
                    //context.assocLevelSisterUO = "N";
                    //context.assocLevelTopUO = "N";
                    
                    if (UtilValidate.isNotEmpty(workEffortAssocTypeWETAFROM.toWorkEffortId)) {
                        context["workEffortIdTo" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = workEffortAssocTypeWETAFROM.toWorkEffortId;
                        context["toWorkEffortParentId" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = workEffortAssocTypeWETAFROM.toWorkEffortParentId;
                        context["assocWeight" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = workEffortAssocTypeWETAFROM.assocWeight;
                    }
                    
                    // recupero params
                    WorkEffortTypeCntParamsEvaluator paramsEvaluator = new WorkEffortTypeCntParamsEvaluator(null, null, delegator);
                    def mapParams = paramsEvaluator.getParams(context.workEffortTypeId, workEffortAssocTypeWETAFROM.contentId, false);
                    Debug.log(" ********* getUniqueWorkeffortAssoc.groovy WETAFROM mapParams " + mapParams);
                    
                    if (UtilValidate.isNotEmpty(mapParams)) {  
                        context["showAssocWeight" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = mapParams.showAssocWeight;
                        context["showEtch" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = mapParams.showEtch;
                        context["showOrgUnit" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = mapParams.showOrgUnit;
                        context["orgUnitIdRelation" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = mapParams.orgUnitIdRelation;
                        context["entityNameExtended" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = mapParams.entityNameExtended;
                        context["showRootLink" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = mapParams.showRootLink;
                        context["assA1" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = mapParams.assA1;
                        context["assA2" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = mapParams.assA2;
                        context["assA3" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = mapParams.assA3;
                        context["assB1" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = mapParams.assB1;
                        context["assB2" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = mapParams.assB2;
                        context["Ass" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = getAss(mapParams);
                        if ("Ref".equals(mapParams.entityNameExtended)) {
                        	context["workEffortIdRef" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = context.workEffortId;
                        }
                    }
                    if ("Y".equals(context["showOrgUnit" + (uniqueWorkEffortAssocIndexWETAFROM + 5)])) {
                        context["orderByField" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = context.weOrgPartyDescrField;
                        context["weOrgPartyDescrValue" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = context.weOrgPartyDescrField;
                        context["workEffortNameDescription" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = "Y".equals(context.localeSecondarySet) ? "@{weOrgPartyDescrLang}" : "@{weOrgPartyDescr}";
                    } else{
                        context["orderByField" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = context.workEffortNameField;
                        if ("Y".equals(context["showEtch" + (uniqueWorkEffortAssocIndexWETAFROM + 5)])) {
                            context["weEtchValue" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = "weEtch";
                            context["orderByField" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = "weEtch";
                        }  else if ("C".equals(context["showEtch" + (uniqueWorkEffortAssocIndexWETAFROM + 5)])){
                            context["weEtchValue" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = "sourceReferenceId";
                            context["orderByField" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = "sourceReferenceId";
                        }
                        context["workEffortNameValue" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = context.workEffortNameField;
                        context["workEffortNameDescription" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] = context["workEffortNameDescription" + (uniqueWorkEffortAssocIndexWETAFROM + 5)] + ("Y".equals(context.localeSecondarySet) ? " @{workEffortNameLang}" : " @{workEffortName}");
                    }
    			}
    		}
    	}
	}
}

def getAss(mapParams) {
	if ("Y".equals(mapParams.assocLevelSameUOAss) || "Y".equals(mapParams.assocLevelParentUOAss) || "Y".equals(mapParams.assocLevelChildUOAss) || "Y".equals(mapParams.assocLevelSisterUOAss) || "Y".equals(mapParams.assocLevelTopUOAss)) {
		return "Ass";
	}
	return "";
}
