// initWizardContextParams

import org.ofbiz.base.util.*;

def getParam(name) {
    value = context.get(name);
    if (UtilValidate.isEmpty(value))
        value = parameters.get(name);
    return value;
}

def concatParam(link, name) {
    value = getParam(name);
    if (UtilValidate.isNotEmpty(value)) {
        if (UtilValidate.isEmpty(link))
            link = "";
        else
            link += "&";
        link += name + "=" + value;
    }
    return link;
}

//Debug.log("************************************** initWizardContextParams.groocy -> " + context.entityName);

// wizardContextParams
context.wizardContextParams = concatParam(null, "messageContext");
context.wizardContextParams = concatParam(context.wizardContextParams, "wizardMainScreenName");
context.wizardContextParams = concatParam(context.wizardContextParams, "wizardMainScreenLocation");
context.wizardContextParams = concatParam(context.wizardContextParams, "breadcrumbs");
context.wizardContextParams = concatParam(context.wizardContextParams, "entityName");
context.wizardContextParams = concatParam(context.wizardContextParams, "managementFormType");
context.wizardContextParams = concatParam(context.wizardContextParams, "managementFormLocation");

//Debug.log("************************************** initWizardContextParams.groocy -> " + context.wizardContextParams);

//Debug.log("************************************** initWizardContextParams.groocy -> " + context.entityName);
