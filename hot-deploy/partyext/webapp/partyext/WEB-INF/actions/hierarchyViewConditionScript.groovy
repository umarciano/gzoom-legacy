import org.ofbiz.base.util.*;

screenNameListIndex = "";


def partyRelationshipTypeId = UtilValidate.isNotEmpty(parameters.partyRelationshipTypeId) ? parameters.partyRelationshipTypeId : parameters.valuePartyRelationshipTypeId;

switch (partyRelationshipTypeId) {
	case "AGV_ROLLUP" : screenNameListIndex = "1"
		break;
	case "STK_ROLLUP" : screenNameListIndex = "2"
		break;
	case "CDC_ROLLUP" : screenNameListIndex = ""
		break;
	default : screenNameListIndex = "3"
}

return screenNameListIndex;