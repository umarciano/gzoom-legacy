import org.ofbiz.base.util.*;

def setDefaultParameter(name, value) {
    if (UtilValidate.isEmpty(parameters.get(name)))
        parameters.put(name, value);
}

setDefaultParameter("entityName", "Enumeration");
setDefaultParameter("successCode", "success");
setDefaultParameter("managementFormType", "");
setDefaultParameter("searchFormLocation", "component://testapp/widget/TestappForms.xml");
setDefaultParameter("searchFormResultLocation", "component://testapp/widget/TestappForms.xml");
setDefaultParameter("advancedSearchFormLocation", "component://testapp/widget/TestappForms.xml");
setDefaultParameter("managementFormLocation", "component://testapp/widget/TestappForms.xml");

setDefaultParameter("breadcrumbs", "[Test|" + parameters.entityName + "]");
setDefaultParameter("clearSaveView", "Y");
setDefaultParameter("ajaxRequest", "N");
