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
dbUtil.modifyColumnTypeAndRedefinePk("WORK_EFFORT_ATTRIBUTE", "ATTR_NAME", "id-vlong-ne", ["WORK_EFFORT_ID", "ATTR_NAME"]);

return result;

