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
dbUtil.addForeignKey("EMPL_POSITION_TYPE", "TP", "TEMPLATE_ID", "WORK_EFFORT", "WORK_EFFORT_ID");
dbUtil.addForeignKey("STATUS_TYPE", "PT", "PORTAL_TYPE_ID", "ENUMERATION", "ENUM_ID");
dbUtil.addForeignKey("GL_ACCOUNT", "GL_WMT", "WE_MEASURE_TYPE_ENUM_ID", "ENUMERATION", "ENUM_ID");
dbUtil.addForeignKey("GL_ACCOUNT", "GL_SCRN", "WE_SCORE_RANGE_ENUM_ID", "ENUMERATION", "ENUM_ID");
dbUtil.addForeignKey("GL_ACCOUNT", "GL_CVN", "WE_SCORE_CONV_ENUM_ID", "ENUMERATION", "ENUM_ID");
dbUtil.addForeignKey("GL_ACCOUNT", "GL_ALRU", "WE_ALERT_RULE_ENUM_ID", "ENUMERATION", "ENUM_ID");
dbUtil.addForeignKey("GL_ACCOUNT", "GL_UORN", "UOM_RANGE_ID", "UOM_RANGE", "UOM_RANGE_ID");
dbUtil.addForeignKey("GL_ACCOUNT", "GL_NOPERF", "WE_WITHOUT_PERF", "ENUMERATION", "ENUM_ID");
dbUtil.addForeignKey("WORK_EFFORT_TYPE_STATUS_CNT", "CT", "CONTENT_ID", "CONTENT", "CONTENT_ID");

return result;

