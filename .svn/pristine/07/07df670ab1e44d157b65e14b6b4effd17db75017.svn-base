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

def groupName = DatabaseUtil.DEFAULT_GROUP_NAME;
def helperInfo = delegator.getGroupHelperInfo(groupName);
def dbUtil = new DatabaseUtil(helperInfo);
dbUtil.modifyColumnType("PARTY_GROUP", "GROUP_NAME", "description");
dbUtil.modifyColumnType("PARTY_GROUP", "GROUP_NAME_LANG", "description");
dbUtil.modifyColumnType("PARTY_NAME_HISTORY", "GROUP_NAME", "description");
return result;

