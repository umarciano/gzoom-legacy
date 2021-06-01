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
dbUtil.deletePrimaryKey(delegator.getModelEntity("GlAccountInterface"), null);
dbUtil.createPrimaryKey(delegator.getModelEntity("GlAccountInterface"), null);
dbUtil.addNotNullConstraint("GL_ACCOUNT_INTERFACE", "ACCOUNT_CODE", "name");
dbUtil.addNotNullConstraint("GL_ACCOUNT_INTERFACE_HIST", "ACCOUNT_CODE", "name");

return result;

