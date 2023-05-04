import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.condition.*;
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

context.menuItems = "GP_MENU_00539|GP_MENU_00540";
context.permissions = "ACCOUNTINGEXT_ADMIN";
GroovyUtil.runScriptAtLocation("com/mapsengineering/base/addSecurityGroupContent.groovy", context);

context.menuItems = "GP_MENU_00541|GP_MENU_00542";
context.permissions = "PARTYMGR_ADMIN";
GroovyUtil.runScriptAtLocation("com/mapsengineering/base/addSecurityGroupContent.groovy", context);

return result;