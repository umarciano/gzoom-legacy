import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import org.ofbiz.entity.model.*;
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

dbUtil.modifyColumnType("GL_ACCOUNT", "ACCOUNT_NAME", "long-description");
dbUtil.modifyColumnType("GL_ACCOUNT", "ACCOUNT_NAME_LANG", "long-description");

dbUtil.modifyColumnType("WORK_EFFORT", "WORK_EFFORT_NAME", "long-description");
dbUtil.modifyColumnType("WORK_EFFORT", "WORK_EFFORT_NAME_LANG", "long-description");

return result;

