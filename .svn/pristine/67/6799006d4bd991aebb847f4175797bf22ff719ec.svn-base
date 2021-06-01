import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;

if (UtilValidate.isNotEmpty(contactMeches)) {
    orderedContactMeches = [];

    contactMechTypeList = delegator.findList("ContactMechType", null, null, ["sequenceNum"], null, true);
    contactMechTypeIdList = EntityUtil.getFieldListFromEntityList(contactMechTypeList, "contactMechTypeId", true);
    
    for (contactMechTypeId in contactMechTypeIdList) {
        selectedContactMech = null;
        for (contactMech in contactMeches) {
            contactMechType = contactMech.contactMechType;
            
            if (UtilValidate.isNotEmpty(contactMech.partyContactMech) && contactMech.partyContactMech.thruDate == null && UtilValidate.isNotEmpty(contactMechType) && contactMechTypeId.equals(contactMechType.contactMechTypeId)) {
                orderedContactMeches.add(contactMech);

                selectedContactMech = contactMech;
                continue;
            }
        }

        if (UtilValidate.isNotEmpty(selectedContactMech)) {
            contactMeches.remove(selectedContactMech);
        }
    }

    context.contactMeches = orderedContactMeches;
}