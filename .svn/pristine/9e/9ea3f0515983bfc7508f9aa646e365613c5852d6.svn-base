import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

contactMechId = context.contactMechId;
if (UtilValidate.isEmpty(contactMechId)) {
    contactMechId = parameters.contactMechId;
}

partyId = context.partyId;
if (UtilValidate.isEmpty(partyId)) {
    partyId = parameters.partyId;
}

//Debug.log("**************************************** GetContactMech.groovy --> contactMechId = " + contactMechId);

if (UtilValidate.isNotEmpty(contactMechId) && UtilValidate.isNotEmpty(partyId)) {
    contactMech = delegator.findOne("ContactMech", ["contactMechId": contactMechId], true);
    //Debug.log("**************************************** GetContactMech.groovy --> contactMech = " + contactMech);
    context.putAll(contactMech);
    if ("POSTAL_ADDRESS".equals(contactMech.contactMechTypeId)) {
        postalAddress = delegator.findOne("PostalAddress", ["contactMechId": contactMechId], true);
        context.putAll(postalAddress);
    } else if ("TELECOM_NUMBER".equals(contactMech.contactMechTypeId)) {
        telecomNumber = delegator.findOne("TelecomNumber", ["contactMechId": contactMechId], true);
        context.putAll(telecomNumber);
    }
}