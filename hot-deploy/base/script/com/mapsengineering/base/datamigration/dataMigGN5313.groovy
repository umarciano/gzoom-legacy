import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.datamigration.util.DatabaseUtil;
import org.ofbiz.entity.model.*;

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

dbUtil.modifyColumnType("GL_ACCOUNT", "SOURCE", "long-description");
dbUtil.modifyColumnType("GL_ACCOUNT", "SOURCE_LANG", "long-description");

dbUtil.modifyColumnType("GL_ACCOUNT_INTERFACE", "SOURCE", "long-description");
dbUtil.modifyColumnType("GL_ACCOUNT_INTERFACE", "SOURCE_LANG", "long-description");

dbUtil.modifyColumnType("GL_ACCOUNT_INTERFACE_HIST", "SOURCE", "long-description");
dbUtil.modifyColumnType("GL_ACCOUNT_INTERFACE_HIST", "SOURCE_LANG", "long-description");


return result;