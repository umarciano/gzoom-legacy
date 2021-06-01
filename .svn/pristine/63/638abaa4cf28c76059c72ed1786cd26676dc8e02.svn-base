import org.ofbiz.base.util.*;

if (UtilValidate.isNotEmpty(parameters.screenNameListIndex)) {
	return parameters.screenNameListIndex;
}

screenNameListIndex = "";

partyTypeId = context.partyTypeId;
if (UtilValidate.isEmpty(partyTypeId)) {
	partyTypeId = parameters.partyTypeId;
}
if ("PERSON".equals(partyTypeId)) {
	screenNameListIndex = "1";
}

return screenNameListIndex;