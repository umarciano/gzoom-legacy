WorkEffortAchieveAjax = {
   
    load : function(){
    
        var form = new Element('form', {"id" : "idFromAjax", "name" : "idFromAjax", 'action' : 'workEffortAchieveViewManagementContainerOnlyNotAuth'});
        
        field = new Element("input", { "type": "hidden", "name": "clearSaveView", "value": "Y" });
		form.insert(field);	
        field = new Element("input", { "type": "hidden", "name": "entityName", "value": "WorkEffortAchieveView" });
		form.insert(field);
		field = new Element("input", { "type": "hidden", "name": "fromManagement", "value": "N" });
		form.insert(field);
		field = new Element("input", { "type": "hidden", "name": "successCode", "value": "management" });
		form.insert(field);
		field = new Element("input", { "type": "hidden", "name": "managementFormType", "value": "list" });
		form.insert(field);
		field = new Element("input", { "type": "hidden", "name": "managementFormLocation", "value": "component://workeffortext/widget/forms/WorkEffortAchieveForms.xml" });
		form.insert(field);
		field = new Element("input", { "type": "hidden", "name": "filterByDate", "value":"N" });
		form.insert(field);
		field = new Element("input", { "type": "hidden", "name": "orderBy", "value": "sourceReferenceId|workEffortId" });
		form.insert(field);
		field = new Element("input", { "type": "hidden", "name": "userLoginId", "value": "_NA_" });
		form.insert(field);
		field = new Element("input", { "type": "hidden", "name": "operation", "value": "UPDATE" });
		form.insert(field);
		field = new Element("input", { "type": "hidden", "name": "entityPkFields", "value": "workEffortAnalysisId" });
		form.insert(field);
        field = new Element("input", { "type": "hidden", "name": "ajaxCall", "value": "Y" });
		form.insert(field);
		field = new Element("input", { "type": "hidden", "name": "backAreaId", "value": "common-container" });
		form.insert(field);
		field = new Element("input", { "type": "hidden", "name": "workEffortAnalysisId", "value": '${parameters.workEffortAnalysisId}' });
		form.insert(field);
	
		
        ajaxSubmitFormUpdateAreas(form, "anonymous-container");
    }
}


document.observe("dom:loaded", WorkEffortAchieveAjax.load);