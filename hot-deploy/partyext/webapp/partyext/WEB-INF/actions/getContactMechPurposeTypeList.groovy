import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

contactMech = delegator.findOne("ContactMech", ["contactMechId": parameters.contactMechId], true);
if (UtilValidate.isNotEmpty(contactMech)) {
    contactMechTypePurposeList = delegator.findList("ContactMechTypePurpose", EntityCondition.makeCondition("contactMechTypeId", contactMech.contactMechTypeId), null, null, null, true);
    if (UtilValidate.isNotEmpty(contactMechTypePurposeList)) {
        context.contactMechPurposeTypeList = EntityUtil.getRelated("ContactMechPurposeType", contactMechTypePurposeList);
    }
}