import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.model.*;
import com.mapsengineering.base.birt.util.*;
import com.mapsengineering.workeffortext.util.WorkEffortTypeCntParamsEvaluator;
import com.mapsengineering.base.util.FolderLayuotTypeExtractor;



/*
 * questo groovy contiene la parte della logica di ricerca comune ai folder Ruoli, Destinazioni
 * e Unita Organizzative
 */

def workEffortTypeId = parameters.workEffortTypeId;
if(UtilValidate.isEmpty(workEffortTypeId)) {
	if(UtilValidate.isNotEmpty(parameters.workEffortId)) {
		def workEffort = delegator.findOne("WorkEffort", ["workEffortId" : parameters.workEffortId], false);
		if(UtilValidate.isNotEmpty(workEffort)){
			workEffortTypeId = workEffort.workEffortTypeId;
		}
	}
}

def partyTitleValue = uiLabelMap["BaseParty"];

if(UtilValidate.isEmpty(parameters.isObiettivo) || !"Y".equals(parameters.isObiettivo)) {
	/** Recupero params */

	
	
	WorkEffortTypeCntParamsEvaluator paramsEvaluator = new WorkEffortTypeCntParamsEvaluator(context, parameters, delegator);
	def mapParams = paramsEvaluator.evaluateParams(workEffortTypeId, false);
	// inizio GN-5329
	def orderByPartyIdField = "parentRoleCode";
	if ("EXTCODE".equals(context.orderUoBy)) {
		orderByPartyIdField = "externalId";
	}
	if ("UONAME".equals(context.orderUoBy)) {
		orderByPartyIdField = "Y".equals(context.localeSecondarySet) ? "partyNameLang" : "partyName";
	}
	parameters.sortField = orderByPartyIdField;
	// fine GN-5329
	
	if (UtilValidate.isNotEmpty(mapParams)) {        
        modelEntity = delegator.getModelEntity(context.entityName);
        fieldNames = modelEntity.getAllFieldNames();

        def input = UtilMap.subMap(mapParams, fieldNames);
        context.inputFields.putAll(input);
        parameters.putAll(input);
	}
	
	/** Recupero title */
	def layoutType = new FolderLayuotTypeExtractor(context, parameters).getLayoutTypeFromContext();	
	if(UtilValidate.isNotEmpty(layoutType) && UtilValidate.isNotEmpty(context[layoutType + "_title"])) {
		partyTitleValue = context[layoutType + "_title"];
	}
}

context.PartyTitleValue = partyTitleValue;

GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/executeChildPerformFind.groovy", context);