import org.ofbiz.base.util.*;

context.isReadOnly = context.get("isReadOnly");
if (UtilValidate.isEmpty(context.isReadOnly))
    context.isReadOnly = false;

if (!context.isReadOnly) {
    if (!"Y".equals(context.get("insertMode"))) {
        context.isReadOnly = ("WERV_APPROVED".equals(context.get("statusId")));
    }
}
