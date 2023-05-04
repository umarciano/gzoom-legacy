import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.datamigration.util.DatabaseUtil;

def result = ServiceUtil.returnSuccess();

def delegator = dctx.getDelegator();

def security = dctx.getSecurity();

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
dbUtil.addForeignKey("WORK_EFFORT_PARTY_ASSIGNMENT", "WEPA_ENUMC", "END_CAUSE_ENUM_ID", "ENUMERATION", "ENUM_ID");
dbUtil.addForeignKey("WORK_EFFORT_PARTY_ASSIGNMENT", "WEPA_ENUMR", "END_REPLACEMENT_ENUM_ID", "ENUMERATION", "ENUM_ID");
dbUtil.addForeignKey("WORK_EFFORT_PARTY_ASSIGNMENT", "WEPA_WEEND", "END_WORK_EFFORT_ID", "WORK_EFFORT", "WORK_EFFORT_ID");

return result;