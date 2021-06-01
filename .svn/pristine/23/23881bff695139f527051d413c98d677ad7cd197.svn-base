import org.ofbiz.base.util.*;

if (UtilValidate.isNotEmpty(context.parametersName)) {
    parameterNameList = [];
    try {
        parameterNameList = StringUtil.toList(context.parametersName, "\\|");
    } catch (Exception e) {}
    if (UtilValidate.isNotEmpty(parameterNameList)) {
        for (parameterName in parameterNameList) {
            if (UtilValidate.isEmpty(context[parameterName])) {
                if (UtilValidate.isNotEmpty(parameters[parameterName])) {
                    context[parameterName] = parameters[parameterName];
                } else {
                    context[parameterName] = context[parameterName+"DefaultValue"];
                }
            }
        }

    }
}