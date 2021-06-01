import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;

if (UtilValidate.isNotEmpty(context.statusId)) {
    result = dispatcher.runSync("getStatusValidChangeToDetails", ["statusId" : context.statusId, "locale": locale, "userLogin": userLogin]);

    statusValidChangeToDetails = result.statusValidChangeToDetails;

    statusValidChangeList = EntityUtil.getRelatedCache("StatusValidChange", statusValidChangeToDetails);
    statusList = EntityUtil.getRelatedCache("ToStatusItem", statusValidChangeList);

    statusList.add(delegator.findOne("StatusItem", true, "statusId",context.statusId));

    context.statusList = statusList;
}