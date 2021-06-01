// initWizardData
import org.ofbiz.base.util.*;

//Debug.log("************************************** initWizardData.groocy -> " + context.entityName);

wizardData = parameters.wizardDataMap;

if (UtilValidate.isEmpty(context.wizardMapName)) {
    context.wizardMapName = context.wizardDefaultMapName;
    com.mapsengineering.base.events.WizardEvents.clearWizardData(request);
}

context.wizardMapList = com.mapsengineering.base.events.WizardEvents.getWizardMapList(request);
context.isEmptyWizardMapList = UtilValidate.isEmpty(context.wizardMapList);

if (UtilValidate.isNotEmpty(context.wizardMapName) && UtilValidate.isNotEmpty(wizardData)) {
    if (context.wizardMapName.equals("Summary")) {
        // nop
    } else {
        //Debug.log("**** wizardData=" + wizardData);
        //Debug.log("**** context.wizardMapName=" + context.wizardMapName);
        wizardMap = wizardData.get(context.wizardMapName);

//        Debug.log("************************************** initWizardData.groocy -> context.wizardMapName = " + context.wizardMapName);
//        Debug.log("************************************** initWizardData.groocy -> wizardMap = " + wizardMap);
        if (UtilValidate.isNotEmpty(wizardMap)) {
            context.putAll(wizardMap);
        }
    }
}

//Debug.log("************************************** initWizardData.groocy -> " + context.entityName);