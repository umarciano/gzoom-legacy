import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;

portalPageId = UtilValidate.isNotEmpty(context.portalPageId) ? context.portalPageId : parameters.portalPageId;
portalPortletId = UtilValidate.isNotEmpty(context.portalPortletId) ? context.portalPortletId : parameters.portalPortletId;
portletSeqId = UtilValidate.isNotEmpty(context.portletSeqId) ? context.portletSeqId : parameters.portletSeqId;

attrValues = [];
attrName = UtilValidate.isNotEmpty(context.attrName) ? context.attrName : parameters.attrName;
try {
    if (!attrName.startsWith("["))
        attrName = "[" + attrName;
    if (!attrName.endsWith("]")) {
        attrName += "]";
    }
    attrName = StringUtil.toList(attrName);
} catch (Exception e) {

}

//Debug.log("************************************************* getPortletAttributes.groovy -> attrName = " + attrName);
//Debug.log("************************************************* getPortletAttributes.groovy -> portalPageId = " + portalPageId);
//Debug.log("************************************************* getPortletAttributes.groovy -> portalPortletId = " + portalPortletId);
//Debug.log("************************************************* getPortletAttributes.groovy -> portletSeqId = " + portletSeqId);

if (UtilValidate.isNotEmpty(portalPageId) && UtilValidate.isNotEmpty(portalPortletId) && UtilValidate.isNotEmpty(portletSeqId)) {
    attrValues = delegator.findList("PortletAttribute", EntityCondition.makeCondition([EntityCondition.makeCondition(["portalPageId" : portalPageId]),
                                                                EntityCondition.makeCondition(["portalPortletId" : portalPortletId]),
                                                                EntityCondition.makeCondition(["portletSeqId" : portletSeqId]),
                                                                EntityCondition.makeCondition("attrName", EntityOperator.IN, attrName)]), null, null, null, true);

//    Debug.log("************************************************* getPortletAttributes.groovy -> attrValues = " + attrValues);
}

if (UtilValidate.isNotEmpty(attrValues)) {
    attrValues.each { value ->
        currentName = value.attrName;

        if (UtilValidate.isEmpty(context[currentName]) || "Y".equals(context.splitAttr)) {
            context[currentName] = value.attrValue;
			if ("Y".equals(context.alsoInParameters)) {
				parameters[currentName] = value.attrValue;
			}
        } else {
            context[currentName] += "&" + value.attrValue;
			if ("Y".equals(context.alsoInParameters)) {
				parameters[currentName] += "&" + value.attrValue;
			}
        }
//        Debug.log("************************************************* getPortletAttributes.groovy -> context["+currentName+"] = " + context[currentName]);
    }
}

