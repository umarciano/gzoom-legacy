import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.datamigration.util.DatabaseUtil;
import org.ofbiz.entity.GenericEntity;

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

def helperInfo = delegator.getGroupHelperInfo(groupName);
def dbUtil = new DatabaseUtil(helperInfo);

dbUtil.addNotNullConstraint("STANDARD_IMPORT_FIELD_CONFIG", "INTERFACE_SEQ", "numeric");
def pkFields = ["DATA_SOURCE_ID", "STANDARD_INTERFACE", "INTERNAL_FIELD_NAME", "INTERFACE_SEQ"];
dbUtil.redefinePK("STANDARD_IMPORT_FIELD_CONFIG", "PK_STANDARD_IMPORT_FIELD_CONFI", pkFields);


return result;