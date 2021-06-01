import org.ofbiz.base.util.*;

screenNameListIndex = "";
partyId = parameters.partyId;
if(UtilValidate.isEmpty(partyId)) {
	partyId = context.partyId;
}
if(UtilValidate.isNotEmpty(partyId)) {
    party = delegator.findOne("Party", ["partyId" : partyId ], false);
    if(UtilValidate.isNotEmpty(party)) {
    	if ("PARTY_GROUP".equals(party.partyTypeId) || "IMPERSONAL_PARTY".equals(party.partyTypeId)) {
    		screenNameListIndex = "1";
    	}
    }
}
return screenNameListIndex;