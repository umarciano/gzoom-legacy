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

def groupName = DatabaseUtil.DEFAULT_GROUP_NAME;

def helperInfo = delegator.getGroupHelperInfo(groupName);
def dbUtil = new DatabaseUtil(helperInfo);

dbUtil.deleteDeclaredIndex("WORK_EFFORT_TYPE_PERIOD", "WTP_IDX_PK");

ModelEntity me = delegator.getModelEntity("WorkEffortTypePeriod");
dbUtil.createDeclaredIndex(me, "WTP_IDX_PK");

return result;
