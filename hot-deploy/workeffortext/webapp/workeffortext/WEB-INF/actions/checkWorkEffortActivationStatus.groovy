import org.ofbiz.base.util.*;

// Check global portal read-only mode first
GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/checkPortalReadOnlyMode.groovy", context);

/** Force isReadOnly = true if parameters.rootInqyTree = Y or parameters.snapshot = Y or parameters.forceReadOnly = Y */
if (!"Y".equals(context.get("insertMode"))) {

    context.isForcedReadOnly = "Y".equals(parameters.rootInqyTree) || "Y".equals(parameters.snapshot) || "Y".equals(parameters.forceReadOnly);

    if (context.isForcedReadOnly) {
        context.isReadOnly = true;
    } else {
		context.isReadOnly = false || (UtilValidate.isNotEmpty(context.isReadOnly) && context.isReadOnly) || "Y".equals(parameters.isReadOnly);
    }
} else {
	context.isReadOnly = false;
	context.isForcedReadOnly = false;
}