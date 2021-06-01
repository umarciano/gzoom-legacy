import org.ofbiz.entity.GenericEntity;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

import com.mapsengineering.base.find.PartyEmailFindServices;

PartyEmailFindServices partyEmailFindServices = new PartyEmailFindServices(delegator);
context.emailAddressesList = partyEmailFindServices.getEmailAddress(userLogin.partyId);

defaultContactMechFrom = EntityUtil.getFirst(context.emailAddressesList);
if (UtilValidate.isNotEmpty(defaultContactMechFrom)) {
	context.defaultContactMechIdFrom = defaultContactMechFrom.contactMechId;
	Debug.log("********************************************** context.defaultContactMechIdFrom: " + context.defaultContactMechIdFrom);
}
