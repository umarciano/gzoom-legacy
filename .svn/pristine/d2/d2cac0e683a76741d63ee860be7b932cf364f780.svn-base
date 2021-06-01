import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;

globalContext.userLoginValidPartyRoleList = [];

if (UtilValidate.isNotEmpty(userLogin)) {
    globalContext.userLoginValidPartyRoleList = delegator.findList("UserLoginValidPartyRoleAndPartyGroupView", EntityCondition.makeCondition("userLoginId", userLogin.userLoginId), null, null, null, false);
}


