import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;

def extraFieldContainerScreenLocation = context.extraFieldContainerScreenLocation;
def extraFieldContainerScreenName = context.extraFieldContainerScreenName;

if (UtilValidate.isEmpty(extraFieldContainerScreenName) && UtilValidate.isEmpty(extraFieldContainerScreenLocation) && UtilValidate.isNotEmpty(context.listIt)) {
	context.listIt.each { content ->
		def conditionList = [EntityCondition.makeCondition("contentIdStart", content.contentId),
							 EntityCondition.makeCondition("contentTypeId", "BIRT_LNCH_FLD_SCREEN"),
							 EntityCondition.makeCondition("caContentAssocTypeId", "BIRT_EXTRAFIELD_OF")];
		
		def assocList = delegator.findList("ContentAssocViewTo", EntityCondition.makeCondition(conditionList), null, ["caSequenceNum"], null, true);
		if (UtilValidate.isNotEmpty(assocList)) {
			assocList.each { contentAssocViewTo ->
				if (UtilValidate.isNotEmpty(contentAssocViewTo.dataResourceId)) {
					def dataResource = delegator.findOne("DataResource", ["dataResourceId" : contentAssocViewTo.dataResourceId], true);
					if (UtilValidate.isNotEmpty(dataResource)) {
						def objectInfo = dataResource.objectInfo;
						if (UtilValidate.isNotEmpty(objectInfo)) {
							def screenLocationSplitted = StringUtil.split(objectInfo, "#");
							
							/*if (UtilValidate.isNotEmpty(extraFieldContainerScreenLocation)) {
								if (extraFieldContainerScreenLocation.indexOf(screenLocationSplitted[0]) == -1) {
									extraFieldContainerScreenLocation.add(screenLocationSplitted[0]);
								}else{
									extraFieldContainerScreenLocation.add(screenLocationSplitted[0]);
								}
							} else {
								extraFieldContainerScreenLocation = [screenLocationSplitted[0]];
							}
							
							if (UtilValidate.isNotEmpty(extraFieldContainerScreenName)) {
								if (extraFieldContainerScreenName.indexOf(screenLocationSplitted[1]) == -1) {
									extraFieldContainerScreenName.add(screenLocationSplitted[1]);
								}
							} else {
								extraFieldContainerScreenName = [screenLocationSplitted[1]];
							}*/
							
						   //asma: controllo se il name o la location sono gia presente, altrimenti nn li inserisco!!	
						   if(UtilValidate.isEmpty(extraFieldContainerScreenName)){
						   		extraFieldContainerScreenName = [screenLocationSplitted[1]];
						   }							   
						   if (UtilValidate.isEmpty(extraFieldContainerScreenLocation)) {
						   		extraFieldContainerScreenLocation = [screenLocationSplitted[0]];
						   }						   
						   if (extraFieldContainerScreenName.indexOf(screenLocationSplitted[1]) == -1 || extraFieldContainerScreenLocation.indexOf(screenLocationSplitted[0]) == -1 ) {
						   		extraFieldContainerScreenName.add(screenLocationSplitted[1]);
						   		extraFieldContainerScreenLocation.add(screenLocationSplitted[0]);
						   }
						   							
						}
						
					}
				}
			}
		}
	}
}
Debug.log("........................... extraFieldContainerScreenLocation "+ extraFieldContainerScreenLocation);
Debug.log("........................... extraFieldContainerScreenName "+ extraFieldContainerScreenName);
if (UtilValidate.isNotEmpty(extraFieldContainerScreenLocation)) {
	context.extraFieldContainerScreenLocation = extraFieldContainerScreenLocation;
}
if (UtilValidate.isNotEmpty(extraFieldContainerScreenName)) {
	context.extraFieldContainerScreenName = extraFieldContainerScreenName;
}