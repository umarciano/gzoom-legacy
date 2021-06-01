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
Debug.log("index");
ModelEntity at = delegator.getModelEntity("AcctgTrans");
ModelField atField = at.getField("voucherRef");

ModelEntity ate = delegator.getModelEntity("AcctgTransEntry");
ModelField ateField = ate.getField("voucherRef");

Map<String, ModelEntity> modelEntities = delegator.getModelEntityMapByGroup(groupName);
       
dbUtil.modifyColumnSizeAndDeleteIndex(ate, ateField, modelEntities);
dbUtil.modifyColumnSizeAndDeleteIndex(at, atField, modelEntities);

return result;

