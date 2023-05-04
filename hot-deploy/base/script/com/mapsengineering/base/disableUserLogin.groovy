import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;

def result = ServiceUtil.returnSuccess();

def delegator = dctx.getDelegator();
def security = dctx.getSecurity();

def daysInactivityUserLogin = 0;
def daysInactivityUserLoginProp = UtilProperties.getPropertyValue("BaseConfig", "daysInactivityUserLogin");
if (UtilValidate.isNotEmpty(daysInactivityUserLoginProp)) {
	try {
		daysInactivityUserLogin = Integer.parseInt(daysInactivityUserLoginProp);
	} catch (Exception e) {
		daysInactivityUserLogin = 0;
	}
}

if (daysInactivityUserLogin > 0) {
	def nowTimestamp = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(), timeZone, locale);
	def userLoginLastActivityList = delegator.findList("UserLoginLastActivity", null, null, null, null, false);
	if (UtilValidate.isNotEmpty(userLoginLastActivityList)) {
		for (userLoginLastActivityItem in userLoginLastActivityList) {
		    if ("admin".equals(userLoginLastActivityItem.userLoginId) || "old-admin".equals(userLoginLastActivityItem.userLoginId)
    		|| "system".equals(userLoginLastActivityItem.userLoginId) || "anonymous".equals(userLoginLastActivityItem.userLoginId)) {
    		    continue;
    		}
            def fromDate = UtilDateTime.getDayStart(userLoginLastActivityItem.fromDate, timeZone, locale);
		    def days = UtilDateTime.getIntervalInDays(fromDate, nowTimestamp);
		    if (days > daysInactivityUserLogin) {
		    	def userLogin = delegator.findOne("UserLogin", ["userLoginId": userLoginLastActivityItem.userLoginId], false);
		    	if (UtilValidate.isNotEmpty(userLogin) && "Y".equals(userLogin.enabled)) {
		    		Debug.log("disabilitazione userLogin " + userLogin.userLoginId);
		    		userLogin.enabled = "N";
		    		userLogin.disabledDateTime = null;
		    		delegator.store(userLogin);
		    	}
		    }
		}
	}
}