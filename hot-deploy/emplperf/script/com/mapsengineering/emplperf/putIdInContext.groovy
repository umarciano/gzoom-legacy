import org.ofbiz.base.util.*;

//Debug.log("putIdInContext " + parameters.id);


if (UtilValidate.isNotEmpty(parameters.id)) {
    //Debug.log("***************************************** putInputFieldInContext.groovy -> 1 = " + parameters.insertMode );
    if (parameters.id.startsWith("["))
        parameters.id = parameters.id.substring(1);
    if (parameters.id.endsWith("]"))
        parameters.id = parameters.id.substring(0, parameters.id.length()-1);

    idMap = StringUtil.strToMap(parameters.id);
    if (UtilValidate.isNotEmpty(idMap)) {
        idMap.each { fieldName, fieldValue ->
                 request.setAttribute(fieldName, fieldValue);
        }
    }
}
res = "success";
