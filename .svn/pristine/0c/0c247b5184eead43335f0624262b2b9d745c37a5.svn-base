import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import com.mapsengineering.workeffortext.util.FromAndThruDatesProviderFromParams;

//Debug.log("*********************************** context.inputFields = " + context.inputFields);

condition = EntityCondition.makeCondition(context.inputFields);

if (UtilValidate.isEmpty(parameters.searchDate)) {
    condition = EntityCondition.makeCondition(
        condition,
        EntityCondition.makeCondition(
            EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS_FIELD, "wrFromThruDate"), // "B" field="estimatedCompletionDate" name="wrFromThruDate"/>
            EntityOperator.OR,
            EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS_FIELD, "wrToThruDate") // "B" field="estimatedCompletionDate" name="wrFromThruDate"/>
        )
    );
} else {
	FromAndThruDatesProviderFromParams fromAndThruDatesProvider = new FromAndThruDatesProviderFromParams(context, parameters, delegator, false, true);
	fromAndThruDatesProvider.run();
	
	def condDateList = [];
	if(UtilValidate.isNotEmpty(fromAndThruDatesProvider.getFromDate())) {
		condDateList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, fromAndThruDatesProvider.getFromDate()));
	}
	if(UtilValidate.isNotEmpty(fromAndThruDatesProvider.getThruDate())) {
		condDateList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromAndThruDatesProvider.getThruDate()));
	}
	
	if(UtilValidate.isNotEmpty(condDateList)) {
		condition = EntityCondition.makeCondition(
		        condition,
		        EntityCondition.makeCondition(condDateList)
		    );		
	}
}

if (! "Y".equals(parameters.snapshot)) {
    condition = EntityCondition.makeCondition(condition, EntityCondition.makeCondition("wrToSnapShotId", GenericValue.NULL_FIELD));
    condition = EntityCondition.makeCondition(condition, EntityCondition.makeCondition("wrToRevisionId", GenericValue.NULL_FIELD));
}
condition = EntityCondition.makeCondition(condition, EntityCondition.makeCondition("wrToActivation", EntityOperator.NOT_EQUAL, "ACTSTATUS_REPLACED"));

//ToDo 3845
list = delegator.findList(parameters.treeViewEntityName, condition, null, context.orderByFields, null, false);

foundList = [];
it = list.iterator();
while(it.hasNext()) {
	wAssoc = it.next();
	def map = wAssoc.getAllFields();
	def workEffortTypeIdTo = wAssoc.workEffortTypeIdTo;
	if(UtilValidate.isNotEmpty(workEffortTypeIdTo)) {
		def wToType = delegator.findOne("WorkEffortType", ["workEffortTypeId" : workEffortTypeIdTo], false);
		map.weTypeIconId = wToType.iconContentId;
		map.etch = wToType.etch;
	} 
	foundList.add(map);
} 

context.foundList = foundList;

// Debug.log("*********************************** parameters.workEffortId = " + parameters.workEffortId);
