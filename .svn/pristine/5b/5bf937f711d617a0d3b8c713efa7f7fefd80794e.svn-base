import org.ofbiz.base.util.*;

/** Force isReadOnly = true if parameters.rootInqyTree = Y or parameters.snapshot = Y */
if (!"Y".equals(context.get("insertMode"))) {

    context.isForcedReadOnly = "Y".equals(parameters.rootInqyTree) || "Y".equals(parameters.snapshot);

    if (context.isForcedReadOnly) {
        context.isReadOnly = true;
    } else {
		context.isReadOnly = false || (UtilValidate.isNotEmpty(context.isReadOnly) && context.isReadOnly) || "Y".equals(parameters.isReadOnly);
    }
} else {
	context.isReadOnly = false;
	context.isForcedReadOnly = false;
}