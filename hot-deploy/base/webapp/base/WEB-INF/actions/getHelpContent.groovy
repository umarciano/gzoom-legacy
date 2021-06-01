import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.util.EntityUtil

def ownerContentId = UtilValidate.isNotEmpty(context.ownerContentId) ? context.ownerContentId : context.defaultPageContentId;
if (UtilValidate.isNotEmpty(ownerContentId)) {
    def contentAssocList = delegator.findList("ContentAssoc", EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("contentIdTo", ownerContentId), EntityCondition.makeCondition("contentAssocTypeId", "HELP"))), null, ["-fromDate"], null, false);
    contentAssocList = EntityUtil.filterByDate(contentAssocList);
    def contentHelp = EntityUtil.getFirst(contentAssocList);
    if (UtilValidate.isNotEmpty(contentHelp)) {
        context.helpContentId = contentHelp.contentId;
    }
}