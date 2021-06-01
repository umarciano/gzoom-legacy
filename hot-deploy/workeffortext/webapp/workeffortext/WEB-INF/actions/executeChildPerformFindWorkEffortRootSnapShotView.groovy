import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;

context.listIt = delegator.findList("WorkEffortRootSnapShotView", EntityCondition.makeCondition(EntityCondition.makeCondition("workEffortId", parameters.workEffortId), EntityCondition.makeCondition("uvUserLoginId", userLogin.userLoginId)),
	null, null, null, false);
