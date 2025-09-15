import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

import java.io.ObjectOutputStream.DebugTraceInfoStack;
import java.util.*;


/**
 * Prendo la lista di valori da visualizzare
 */

 //Fallback: se non abbiamo trovato risultati, restituiamo i tipi fiscali con isIndicatorUsed = Y
def entityName = null; 	
def glAccountTypeGlFiscalTypeView = null;
def fallbackConditions = [];

fallbackConditions.add(EntityCondition.makeCondition("isIndicatorUsed", "Y"));
glAccountTypeGlFiscalTypeView = delegator.findList("GlFiscalType", EntityCondition.makeCondition(fallbackConditions), null, null, null, true);
parameters.glAccountTypeGlFiscalTypeView = glAccountTypeGlFiscalTypeView;

if ("Y".equals(parameters.insertMode) || "W".equals(parameters.insertMode)) {
	
	def glAccountTypeId = UtilValidate.isNotEmpty(glAccount) ? glAccount.glAccountTypeId : parameters.glAccountTypeId;

	if (UtilValidate.isNotEmpty(glAccountTypeId)) {
		
		def conditionList = [];
		
		if (UtilValidate.isNotEmpty(accountTypeEnumId) && accountTypeEnumId == "INDICATOR") {
			entityName = "GlAccountTypeGlFiscalTypeView";
			conditionList.add(EntityCondition.makeCondition("glAccountTypeId", glAccountTypeId));
		} else {
			entityName = "GlFiscalType";
			conditionList.add(EntityCondition.makeCondition("isFinancialUsed", "Y"));
		}
		
		/***
		 * Vado a prendere il valModId
		 */

		if (UtilValidate.isEmpty(parameters.valModId)) {
			context.parameters.getValModId = "Y";
			GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/getValidateValModId.groovy", context);
			
		}
		if (UtilValidate.isNotEmpty(parameters.valModId)) {
			
			def glFiscalTypeId = "BUDGET";
			if (parameters.valModId == "ACTUAL_NOT_MOD") {
				glFiscalTypeId = "ACTUAL";
			}
			
			if (parameters.valModId != "ALL_MOD") {
				conditionList.add(EntityCondition.makeCondition("glFiscalTypeId", EntityOperator.NOT_EQUAL, glFiscalTypeId));
			}
			
		}
		
		if (parameters.valModId != "ALL_NOT_MOD") {
			glAccountTypeGlFiscalTypeView = delegator.findList(entityName , EntityCondition.makeCondition(conditionList), null, null, null, true);
		}
		
	}
	parameters.glAccountTypeGlFiscalTypeView = glAccountTypeGlFiscalTypeView;
	//Debug.log(".................................. glAccountTypeGlFiscalTypeView "+ glAccountTypeGlFiscalTypeView);
}