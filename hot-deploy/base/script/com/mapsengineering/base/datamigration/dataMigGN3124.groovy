import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.datamigration.util.DatabaseUtil;
import javolution.util.FastList;

def result = ServiceUtil.returnSuccess();

def delegator = dctx.getDelegator();
def security = dctx.getSecurity();

def condList = FastList.newInstance();
condList.add(EntityCondition.makeCondition("contentId", "GP_MENU_00410"));
condList.add(EntityCondition.makeCondition("contentIdTo", "GP_MENU_00443"));

    
def contentAttributeList = delegator.findList("ContentAssoc", EntityCondition.makeCondition(condList), null, null, null, false);
def contentAttribute = EntityUtil.getFirst(contentAttributeList);
if (UtilValidate.isNotEmpty(contentAttribute)) {
	contentAttribute.thruDate = null;
	delegator.store(contentAttribute);
}

return result;

