import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

statusTypeId = parameters.statusTypeId;
if (UtilValidate.isEmpty(statusTypeId)) {
    statusTypeId = context.statusTypeId;
}

context.insertMode = UtilValidate.isEmpty(context.insertMode) ? UtilValidate.isEmpty(parameters.insertMode) ? "N" : parameters.insertMode : context.insertMode;

if ("Y".equals(context.insertMode)) {
	GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/executeChildPerformFind.groovy", context);
} else {
	if (UtilValidate.isNotEmpty(statusTypeId)) {
	    listStatusValidChangeTo = [];
	    listStatusValidChangeFrom = [];
	    // la lista con tiene tutte le transizioni in cui almeno uno dei due stati (iniziale o finale ha come stausTypeId quello selezionato)
	    // listStatusValidChangeTo contiene i passaggi di stato in cui lo stato finale ha come statusTypeid quello selezionato
	    listStatusValidChangeToDetail = delegator.findList("StatusValidChangeToDetail", EntityCondition.makeCondition("statusTypeId", EntityOperator.EQUALS, statusTypeId), null, null, null, true);
	    if (UtilValidate.isNotEmpty(listStatusValidChangeToDetail)) {
	        listStatusValidChangeTo = EntityUtil.getRelated("StatusValidChange", listStatusValidChangeToDetail);
	    }
	    //Debug.log("********************************** statusValidChangeList.groovy --> listStatusValidChangeTo=" + listStatusValidChangeTo);
	
	    // listStatusValidChangeFrom ottiene i passaggi in cui solo lo stato di partenza ha come statusTypeid quello selezionato
	    listStatusItem = delegator.findList("StatusItem", EntityCondition.makeCondition("statusTypeId", EntityOperator.EQUALS, statusTypeId), null, null, null, true);
	    if(UtilValidate.isNotEmpty(listStatusItem)) {
	        listStatusItemId = EntityUtil.getFieldListFromEntityList(listStatusItem, "statusId", true);
	        conditionOnFrom = EntityCondition.makeCondition("statusId", EntityOperator.IN, listStatusItemId);
	        conditionOnTo = EntityCondition.makeCondition("statusIdTo", EntityOperator.NOT_IN, listStatusItemId);
	        condition = EntityCondition.makeCondition(conditionOnFrom, EntityOperator.AND, conditionOnTo);
	        listStatusValidChangeFrom = delegator.findList("StatusValidChange", condition, null, null, null, true);
	    }
	    listStatusValidChangeTo.addAll(listStatusValidChangeFrom);
	
	    context.listIt = listStatusValidChangeTo;
	
	    if(!("Y".equals(context.contextManagement)))
	        context.managementChildExtraParams = "statusTypeId=" + statusTypeId;
	
	    //Debug.log("********************************** statusValidChangeList.groovy --> context.listIt=" + context.listIt);
	    //Debug.log("********************************** statusValidChangeList.groovy --> context.managementChildExtraParams=" + context.managementChildExtraParams);
	}
	else {
	    //Debug.log("errore");
	}
}