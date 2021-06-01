import org.ofbiz.base.util.*;
import org.ofbiz.content.content.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;

contentId = null;
if (UtilValidate.isNotEmpty(parameters.defaultPageContentId)) {
    contentId = parameters.defaultPageContentId;
} else if (UtilValidate.isNotEmpty(context.webSiteId)) {
    defaultPages = null;
    try {
        defaultPages = delegator.findList("WebSiteContent",
                EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("webSiteId", webSiteId), EntityCondition.makeCondition("webSiteContentTypeId", "DEFAULT_PAGE"))),
                null, ["-fromDate"], null, true);
    } catch (GenericEntityException e) {
        throw e;
    }

    defaultPages = EntityUtil.filterByDate(defaultPages);
    if (UtilValidate.isNotEmpty(defaultPages)) {
        defaultPage = EntityUtil.getFirst(defaultPages);
        if (UtilValidate.isNotEmpty(defaultPage)) {
            contentId = defaultPage.contentId;
        }
    }
}

if (UtilValidate.isNotEmpty(contentId) && UtilValidate.isNotEmpty(ContentWorker.getContentCache(delegator, contentId))) {
    context.defaultPageContentId = contentId;
}