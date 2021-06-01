import org.ofbiz.base.util.*;

screenNameListIndex = "";

if (UtilValidate.isNotEmpty(parameters.screenNameListIndex)) {
	return parameters.screenNameListIndex;
}

def glAccountId = UtilValidate.isNotEmpty(context.glAccountId) ? context.glAccountId : parameters.glAccountId;
if(UtilValidate.isNotEmpty(glAccountId)) {
	def glAccount = delegator.findOne("GlAccount", ["glAccountId" : glAccountId], false);
	
	if(UtilValidate.isNotEmpty(glAccount) && "INDICATOR".equals(glAccount.accountTypeEnumId)) {
		//ToDo 3687 punto 14
		screenNameListIndex = "1";
	}
}

return screenNameListIndex;