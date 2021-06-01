import org.ofbiz.base.util.*;

context.inputFields.workEffortId = parameters.workEffortId;
GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/executeChildPerformFind.groovy", context);
context.insertMode = context.insertMode = UtilValidate.isEmpty(context.insertMode) ? UtilValidate.isEmpty(parameters.insertMode) ? "N" : parameters.insertMode : context.insertMode;