import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;
import org.ofbiz.entity.util.*;
import com.mapsengineering.base.datamigration.util.DatabaseUtil;

def result = ServiceUtil.returnSuccess();

def delegator = dctx.getDelegator();
def security = dctx.getSecurity();

//check permission
userLogin = (GenericValue) context.get("userLogin");
if (!security.hasPermission("ENTITY_MAINT", userLogin)) {
    return ServiceUtil.returnError(UtilProperties.getMessage("BaseErrorLabels", "ManagementErrorModelEntityNotSet", locale));
}

def groupName = (String) context.get("groupName");
if(UtilValidate.isEmpty(groupName)) {
	groupName = DatabaseUtil.DEFAULT_GROUP_NAME;
}

def condList = [];
condList.add(EntityCondition.makeCondition("partyTypeId", "PERSON"));
def partyList = delegator.findList("Party", EntityCondition.makeCondition(condList), null, null, null, false);
if (UtilValidate.isNotEmpty(partyList)) {
	partyList.each{ partyItem ->
		if (UtilValidate.isNotEmpty(partyItem.partyName) && partyItem.partyName.indexOf(", ") >= 0) {
			def newPartyName = partyItem.partyName.replace(", ", " ");
			partyItem.partyName = newPartyName;
			delegator.store(partyItem);
		}
	}
}

return result;

