import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import java.util.HashMap;

def map = new HashMap();
map.put("GP_MENU_00515", "BSCPERFMGR_ADMIN");
map.put("GP_MENU_00516", "BSCPERFMGR_ADMIN");
map.put("GP_MENU_00517", "ORGPERFMGR_ADMIN");
map.put("GP_MENU_00518", "ORGPERFMGR_ADMIN");
map.put("GP_MENU_00519", "EMPLPERFMGR_ADMIN");
map.put("GP_MENU_00520", "EMPLPERFMGR_ADMIN");
map.put("GP_MENU_00521", "DIRIGPERFMGR_ADMIN");
map.put("GP_MENU_00522", "DIRIGPERFMGR_ADMIN");
map.put("GP_MENU_00523", "PARTPERFMGR_ADMIN");
map.put("GP_MENU_00524", "PARTPERFMGR_ADMIN");
map.put("GP_MENU_00525", "CORPERFMGR_ADMIN");
map.put("GP_MENU_00526", "CORPERFMGR_ADMIN");
map.put("GP_MENU_00527", "PROCPERFMGR_ADMIN");
map.put("GP_MENU_00528", "PROCPERFMGR_ADMIN");
map.put("GP_MENU_00529", "GDPRPERFMGR_ADMIN");
map.put("GP_MENU_00530", "GDPRPERFMGR_ADMIN");
map.put("GP_MENU_00531", "CDGPERFMGR_ADMIN");
map.put("GP_MENU_00532", "CDGPERFMGR_ADMIN");
map.put("GP_MENU_00533", "TRASPERFMGR_ADMIN");
map.put("GP_MENU_00534", "TRASPERFMGR_ADMIN");
map.put("GP_MENU_00535", "RENDPERFMGR_ADMIN");
map.put("GP_MENU_00536", "RENDPERFMGR_ADMIN");

def iterator = map.keySet().iterator();
while(iterator.hasNext()) {
	def key = iterator.next();
	def val = map.get(key);
	def securityGroupList = delegator.findList("SecurityGroup", null, null, null, null, false);
	if (UtilValidate.isNotEmpty(securityGroupList)) {
		securityGroupList.each {securityGroupItem ->
		    def securityGroupPermissionCondList = [];
		    securityGroupPermissionCondList.add(EntityCondition.makeCondition("groupId", securityGroupItem.groupId));
		    securityGroupPermissionCondList.add(EntityCondition.makeCondition("permissionId", val));
		    def securityGroupPermissionList = delegator.findList("SecurityGroupPermission", EntityCondition.makeCondition(securityGroupPermissionCondList), null, null, null, false);
		    if (UtilValidate.isEmpty(securityGroupPermissionList)) {
		    	def securityGroupContentCondList = [];
		    	securityGroupContentCondList.add(EntityCondition.makeCondition("groupId", securityGroupItem.groupId));
		    	securityGroupContentCondList.add(EntityCondition.makeCondition("contentId", key));
		    	def securityGroupContentList = delegator.findList("SecurityGroupContent", EntityCondition.makeCondition(securityGroupContentCondList), null, null, null, false);
		    	if (UtilValidate.isNotEmpty(securityGroupContentList)) {
		    		securityGroupContentList.each {securityGroupContentItem ->
		    			Debug.log("Elimino SecurityGroupContent " + securityGroupContentItem.groupId + "; " + securityGroupContentItem.contentId);
		    			delegator.removeValue(securityGroupContentItem);
		    		}
		    	}
		    }
		}
	}
}