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

dbUtil.deleteDeclaredIndex("PARTY_PARENT_ROLE", "ak_PPR_roleCode");

ModelEntity me = delegator.getModelEntity("PartyParentRole");
dbUtil.createDeclaredIndex(me, "ak_PPR_roleCode");

return result;
