// saveWizardData

import org.ofbiz.base.util.*;

eventReturn = "success";
wizardSaveEvent = parameters.wizardSaveEvent;

if (UtilValidate.isNotEmpty(wizardSaveEvent)) {
    idx = wizardSaveEvent.lastIndexOf("#");
    if (idx >= 0) {
        xmlResource = wizardSaveEvent.substring(0, idx);
        eventName = wizardSaveEvent.substring(idx + 1);
        eventReturn = org.ofbiz.minilang.SimpleMethod.runSimpleEvent(xmlResource, eventName, request, response);
		
		if ("success".equals(eventReturn)) {
			session.removeAttribute("wizardDataMap");
		}
    }
} else {
    // default wizard save action
}

return eventReturn;
