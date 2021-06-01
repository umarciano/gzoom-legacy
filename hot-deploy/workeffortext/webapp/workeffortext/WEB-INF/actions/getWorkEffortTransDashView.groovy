import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;

wtDashMeasureType = context.wtDashMeasureType;
wtDashWeId = UtilValidate.isNotEmpty(context.workEffortId) ? context.workEffortId : parameters.workEffortId;
context.listIt = [];
viewIndex = "";
conditionList = [];

date = UtilValidate.isNotEmpty(context.date) ? context.date : parameters.date;

if (UtilValidate.isNotEmpty(wtDashWeId)) {
	conditionList.add(EntityCondition.makeCondition(["wtDashWeId": wtDashWeId]));
	if (UtilValidate.isNotEmpty(wtDashMeasureType)) {
		conditionList.add(EntityCondition.makeCondition(["wtDashMeasureType": wtDashMeasureType]));
	}

	if (UtilValidate.isNotEmpty(date)) {
		conditionList.add(EntityCondition.makeCondition("wtDashPeriodDate", EntityComparisonOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toSqlDate(date)));
	}
	
	workEffort = delegator.findOne("WorkEffort", ["workEffortId": wtDashWeId], true);
	if (UtilValidate.isNotEmpty(workEffort)) {
		workEffortType = delegator.findOne("WorkEffortType", ["workEffortTypeId": workEffort.workEffortTypeId], true);
		if (UtilValidate.isNotEmpty(workEffortType)) {
			typeId1 = workEffortType.typeId1;
			typeId2 = workEffortType.typeId2;
			typeId3 = workEffortType.typeId3;
			typeId4 = workEffortType.typeId4;
			
			if (UtilValidate.isNotEmpty(typeId4)) {
				viewIndex = "4";
			}
			else {
				if (UtilValidate.isNotEmpty(typeId3)) {
					viewIndex = "3";
				}
				else {
					if (UtilValidate.isNotEmpty(typeId2)) {
						viewIndex = "2";
					}
					else {
						if (UtilValidate.isNotEmpty(typeId1)) {
							viewIndex = "1";
						}
					}
				}
			}
			if (UtilValidate.isNotEmpty(viewIndex)) {
				resultList = delegator.findList("WorkEffortTransDashView" + viewIndex, EntityCondition.makeCondition(conditionList), null, null, null, true);
				context.listIt = resultList;

				if (UtilValidate.isNotEmpty(resultList)) {
					iter = resultList.listIterator();
					while(iter.hasNext()) {
						listItem = iter.next();
						for (i = 1; i < new Integer(viewIndex); i++) {
							labelName = "wtDashValue" + i.toString() + "Label";
							label = listItem.get(labelName);
							value = listItem.get("wtDashValue" + i.toString());
							if (UtilValidate.isNotEmpty(label) && UtilValidate.isNotEmpty(value)) {
								context.put(labelName, label);
							}
						}
					}
				}
			}
		}
	}
}

context.viewIndex = viewIndex;