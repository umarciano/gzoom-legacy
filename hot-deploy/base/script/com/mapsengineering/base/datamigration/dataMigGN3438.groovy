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

dbUtil.modifyColumnType("CONTENT", "MIME_TYPE_ID", "id-vlong");
dbUtil.modifyColumnType("DATA_RESOURCE", "MIME_TYPE_ID", "id-vlong");
dbUtil.modifyColumnType("FILE_EXTENSION", "MIME_TYPE_ID", "id-vlong-ne");
dbUtil.modifyColumnType("MIME_TYPE", "MIME_TYPE_ID", "id-vlong-ne");
dbUtil.modifyColumnType("MIME_TYPE_HTML_TEMPLATE", "MIME_TYPE_ID", "id-vlong-ne");
dbUtil.modifyColumnType("COMMUNICATION_EVENT", "CONTENT_MIME_TYPE_ID", "id-vlong");

return result;

