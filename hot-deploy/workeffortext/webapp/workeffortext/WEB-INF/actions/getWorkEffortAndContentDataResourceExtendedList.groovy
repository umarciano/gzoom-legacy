import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;

//Debug.log("******************************* getCruscottoList -> context.justLoadList = " + context.justLoadList);
//Debug.log("******************************* getCruscottoList -> context.cruContent = " + context.cruContent);
//Debug.log("******************************* getCruscottoList -> context.listIt = " + context.listIt);

if ("Y".equals(context.cruContent)) {		
	if (UtilValidate.isNotEmpty(context.listIt)) {
	
		def currentList = [];
		
		context.listIt.each  { content ->
				//Debug.log("******************************* getCruscottoList -> content = " + content);
				def conditionList = [EntityCondition.makeCondition("contentIdTo", content.contentId),
				EntityCondition.makeCondition("contentId", "WE_CRU_ANALYSIS"),
				EntityCondition.makeCondition("contentAssocTypeId", "CRU_CONTENT")];
			
			def assocList = delegator.findList("ContentAssoc", EntityCondition.makeCondition(conditionList), null, ["sequenceNum"], null, true);
			//Debug.log("******************************* getCruscottoList -> assocList = " + assocList);
			if (UtilValidate.isNotEmpty(assocList)) {
				currentList.add(content);
			}
			
		}
		
		context.listIt = currentList;
	
	} 
}
//Debug.log("******************************* getCruscottoList -> context.listIt = " + context.listIt.size());

//Per ogni elemento aggiungo il percorso troncato!
def list = [];
for (GenericValue item: context.listIt) {
	Map fieldsMap = null;	
	if (item instanceof GenericValue)
		fieldsMap = item.getAllFields();
	else
		fieldsMap = item;	
		
	if (UtilValidate.isNotEmpty(fieldsMap.objectInfo)) {
		fieldsMap.objectInfoTruncate = fieldsMap.objectInfo.substring(fieldsMap.objectInfo.indexOf("runtime"));
	}
	list.add(fieldsMap);
}


context.listIt = list;

