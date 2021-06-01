import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.model.*;
import org.ofbiz.service.*;
import javolution.util.FastList;
import com.mapsengineering.base.datamigration.util.DatabaseUtil;

def result = ServiceUtil.returnSuccess();

def delegator = dctx.getDelegator();
def security = dctx.getSecurity();




def getDataResCntDynaViewEntity() {
	def dataResCntDynaViewEntity = new DynamicViewEntity();
	
	dataResCntDynaViewEntity.addMemberEntity("D", "DataResource");
	dataResCntDynaViewEntity.addMemberEntity("C", "Content");
	
	dataResCntDynaViewEntity.addAlias("D", "dataResourceId");
	dataResCntDynaViewEntity.addAlias("C", "contentId");
		
	dataResCntDynaViewEntity.addViewLink("D", "C", true, ModelKeyMap.makeKeyMapList("dataResourceId", "dataResourceId"));
		
	return dataResCntDynaViewEntity;
}


def removeDataResource(dataResourceId) {
	def dataResource = delegator.findOne("DataResource", ["dataResourceId": dataResourceId], false);
	if(UtilValidate.isNotEmpty(dataResource)) {
		delegator.removeValue(dataResource);
	}
}


// check permission
userLogin = (GenericValue) context.get("userLogin");
if (!security.hasPermission("ENTITY_MAINT", userLogin)) {
    return ServiceUtil.returnError(UtilProperties.getMessage("BaseErrorLabels", "ManagementErrorModelEntityNotSet", locale));
}

def groupName = (String) context.get("groupName");
if(UtilValidate.isEmpty(groupName)) {
	groupName = DatabaseUtil.DEFAULT_GROUP_NAME;
}

def helperInfo = delegator.getGroupHelperInfo(groupName);
def dbUtil = new DatabaseUtil(helperInfo);


def dataResCntDynaViewEntity = getDataResCntDynaViewEntity();
def eli = delegator.findListIteratorByCondition(dataResCntDynaViewEntity, EntityCondition.makeCondition("contentId", GenericEntity.NULL_FIELD), null, null, 
		null, null);

def dataResourceList = eli.getCompleteList();
if(UtilValidate.isNotEmpty(dataResourceList)) {
	dataResourceList.each{ dataResourceItem ->
		if(UtilValidate.isNotEmpty(dataResourceItem.dataResourceId)) {
			removeDataResource(dataResourceItem.dataResourceId);
		}
	}
}

return result;