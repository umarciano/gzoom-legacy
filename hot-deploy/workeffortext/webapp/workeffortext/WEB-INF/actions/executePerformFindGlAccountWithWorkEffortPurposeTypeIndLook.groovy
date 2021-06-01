import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;
import javolution.util.FastMap;

res = "success";
def resultList = [];

lookup = context.lookup;
if (UtilValidate.isEmpty(lookup)) {
    lookup = parameters.lookup;
}

if ("Y".equals(lookup)) {
	
	   
    context.executePerformFindScriptName = null;
    context.executePerformFind = "Y";

    // prima ricerca su GlAccountWithWorkEffortPurposeTypeIndLook
	res = GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/executeLookupPerformFind.groovy", context);
	resultList = context.listIt;
	
	/**
	 * Inizializzaione contesto
	 */
	def groovyContext = [:];
	groovyContext.dispatcher = dispatcher;
	groovyContext.delegator = delegator;
	parameters.workEffortTypeIdInd = parameters.workEffortTypeIdInd;
	// groovyContext.parameters.insertMode = "N";
	def ctx = [:];
	// ctx.workEffortId = parameters.weTransWeId;
	ctx.locale = context.locale;
	ctx.timeZone = context.timeZone;
	groovyContext.timeZone = context.timeZone;
	groovyContext.locale = context.locale;
	
	
	/**
	 * Aggiungo tutti gli indicatori che contengono il productId nei prodotti / servizi cercando sempre per la stessa vista
	 */
	if(UtilValidate.isNotEmpty(parameters.productId)){
		
		parameters.INDProductId = parameters.productId;
		parameters.remove("productId");
		
		groovyContext.parameters = parameters;
		groovyContext.context = ctx;
		
		res = GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/executeLookupPerformFind.groovy", groovyContext);
		addList2 = groovyContext.listIt;
		Debug.log(" addList2 " + addList2);
		
		
		if(UtilValidate.isNotEmpty(addList2)) {
			/* Non aggiungo tutti i dati ma solo quelli nn presenti nella lista */
			listIdGlAccount = EntityUtil.getFieldListFromEntityList(resultList, "glAccountId", true);
			addListOtherGlAccountId = EntityUtil.filterByCondition(addList2, EntityCondition.makeCondition("glAccountId", EntityOperator.NOT_IN, listIdGlAccount));
			resultList.addAll(addListOtherGlAccountId);
		}
	}
	
	
	
	//Oltre alla lista della query su GlAccountWithWorkEffortPurposeTypeIndLook aggiungo gli elementi che trovo su GlAccountWithWorkEffortPurposeMeasureIndLook
	
	for (key in parameters.keySet()) {
		//every parameter of our interest starts with accountName -> INDUomDescr
		if ((key.indexOf("accountName")>-1)) {
			value = parameters.get(key);
			String suffixName = key.substring(key.indexOf("accountName") + 11);
			parameters["INDUomDescr"+suffixName] = value;
			parameters.remove(key);
		}
	}
	for (key in parameters.keySet()) {
		// productId -> INDUomDescr
		if (key.equals("productId")) {
			value = parameters.get(key);
			parameters.INDProductId = value;
			parameters.remove(key);
		}
		
	}
	
    parameters.entityName = "GlAccountWithWorkEffortPurposeMeasureIndLook";
	groovyContext.parameters = parameters;
	groovyContext.context = ctx;
	
	res = GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/executeLookupPerformFind.groovy", groovyContext);
    addList = groovyContext.listIt;
	Debug.log(" addList " + addList);
	
	if(UtilValidate.isNotEmpty(addList)) {
		
		/* Non aggiungo tutti i dati ma solo quelli nn presenti nella lista */
		listIdGlAccount = EntityUtil.getFieldListFromEntityList(resultList, "glAccountId", true);
		addListOtherGlAccountId = EntityUtil.filterByCondition(addList, EntityCondition.makeCondition("glAccountId", EntityOperator.NOT_IN, listIdGlAccount));
		resultList.addAll(addListOtherGlAccountId);
			
	}
	
	if((UtilValidate.isNotEmpty(parameters.partyId) || UtilValidate.isNotEmpty(parameters.roleTypeId)) && UtilValidate.isNotEmpty(context.listIt)) {
		def completeList = context.listIt;
		resultList = [];
		
		for(item in completeList) {
			
			// GN-101
			def PartyRoleCondList = [];
			def RespPartyRoleCondList = [];
			PartyRoleCondList.add(EntityCondition.makeCondition("glAccountId", item.glAccountId));
			RespPartyRoleCondList.add(EntityCondition.makeCondition("glAccountId", item.glAccountId));
			
			if(UtilValidate.isNotEmpty(parameters.partyId)){
				PartyRoleCondList.add(EntityCondition.makeCondition("partyId", parameters.partyId));
				RespPartyRoleCondList.add(EntityCondition.makeCondition("respCenterId", parameters.partyId));
			}
			if(UtilValidate.isNotEmpty(parameters.roleTypeId)){
				PartyRoleCondList.add(EntityCondition.makeCondition("roleTypeId", parameters.roleTypeId));
				RespPartyRoleCondList.add(EntityCondition.makeCondition("respCenterId", parameters.partyId));
			}
					
			def glAcountList = delegator.findList("GlAccount", EntityCondition.makeCondition(RespPartyRoleCondList), null, null, null, true);
			if(UtilValidate.isNotEmpty(glAcountList)) {
				resultList.add(item);
			}
			def glAcountRoleList = delegator.findList("GlAccountRole", EntityCondition.makeCondition(PartyRoleCondList), null, null, null, true);
			if(UtilValidate.isNotEmpty(glAcountRoleList)) {
				resultList.add(item);
			}
			// momdello su prodotto
			def workEffortMeasurePartyRoleList = delegator.findList("WorkEffortMeasure", EntityCondition.makeCondition(PartyRoleCondList), null, null, null, true);
			if(UtilValidate.isNotEmpty(workEffortMeasurePartyRoleList)) {
				resultList.add(item);
			}
			def WorkEffortMeasureRoleList = delegator.findList("WorkEffortMeasureAndRole", EntityCondition.makeCondition(PartyRoleCondList), null, null, null, true);
			if(UtilValidate.isNotEmpty(WorkEffortMeasureRoleList)) {
				resultList.add(item);
			}
			
			if("ACCINP_PRD".equals(item.inputEnumId)) {
				//cerco il titolo nell'uomdesc WorkEffortMeasure
				
				//Provo sulla misura
				def wemCondList = [];
				wemCondList.add(EntityCondition.makeCondition("glAccountId", item.glAccountId));
				wemCondList.add(EntityCondition.makeCondition("orgUnitId", parameters.partyId));
				wemCondList.add(EntityCondition.makeCondition("productId", item.INDProductId)); // todo titolo
				def workEffortMeasureList = delegator.findList("WorkEffortMeasure", EntityCondition.makeCondition(wemCondList),	null, null, null, true);
				if(UtilValidate.isNotEmpty(workEffortMeasureList)) {
					resultList.add(item);
				}
				else {
					//Provo con WorkEffortMeasureRole
					workEffortMeasureList = delegator.findList("WorkEffortMeasure", EntityCondition.makeCondition(EntityCondition.makeCondition("glAccountId", item.glAccountId), EntityCondition.makeCondition("productId", item.INDProductId)), null, null, null, true);
					if(UtilValidate.isNotEmpty(workEffortMeasureList)) {
						// GN-101 rimosso perche spostato piu in alto...
						//def workEffortMeasureRoleList = delegator.findList("WorkEffortMeasureRole", EntityCondition.makeCondition(EntityCondition.makeCondition("workEffortMeasureId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(workEffortMeasureList, "workEffortMeasureId", false)), EntityCondition.makeCondition("partyId", parameters.partyId)), null, null, null, true);
						//if(UtilValidate.isNotEmpty(workEffortMeasureList)) {
							resultList.add(item);
						//}
					}
				}
			}
		}
	}
}

context.listIt = resultList;
