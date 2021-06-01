/* 
 * Utilizzatola selezione a cascata dell droplist responsibleRoleTypeId o weResponsibleRoleTypeId
 * 
 */
 
WorkEffortRootViewSearchExtension = {
    load: function() {
        var form = $('searchForm');
        if (form) {        
            WorkEffortRootViewSearchExtension.registerDropList(form);                        
        }
    },

   
    registerDropList : function(form) {

        var formName = form.readAttribute('name');
        
        //registro responsibleRoleTypeId        
        var responsibleRoleTypeIdList = DropListMgr.getDropList(formName + '_responsibleRoleTypeId');
        if (responsibleRoleTypeIdList) {
            responsibleRoleTypeIdList.registerOnChangeListener(WorkEffortRootViewSearchExtension.callFunction.curry(form, 'responsiblePartyId'), 'responsibleRoleTypeId');
        }
        
        //registro weResponsibleRoleTypeId
        var weResponsibleRoleTypeIdList = DropListMgr.getDropList(formName + '_weResponsibleRoleTypeId');
        if (weResponsibleRoleTypeIdList) {
            weResponsibleRoleTypeIdList.registerOnChangeListener(WorkEffortRootViewSearchExtension.callFunction.curry(form, 'weResponsiblePartyId'), 'weResponsibleRoleTypeId');
        }
	        
	  
    },
		
    callFunction : function(form, name, element) {  
        if (form) {
        	var formName = form.readAttribute('name');
        	var responsiblePartyId = form.down("div#" + formName + "_" + name);
        	//console.log("div#" + formName + name); 
        	      	
        	// controllo se ho veramente selezionato un Tipo Ruolo        	
        	if (element === undefined) {  
        		// sono nel caso che deve funzionare senza ruoli
        		//console.log(responsiblePartyId.hasClassName("PartyRoleViewGroupWithoutRole"));
        		
        		if (!responsiblePartyId.hasClassName("PartyRoleViewGroupWithoutRole")) {
        			//devo ricaricare la pagina
        			WorkEffortRootViewSearchExtension.reloadForm(form);
        		}
        		
        	} else {
        		// sono nel caso che deve ricercare per ruoli
        		if (!responsiblePartyId.hasClassName("PartyRoleView")) {
        			//devo ricaricare la pagina
        			WorkEffortRootViewSearchExtension.reloadForm(form);
        		}
        	}        	
            
        }   
    },
    
    reloadForm : function(form) {
		/**
		*  - Form search
		*  
		**/
		if (form) {
        	form.action = '<@ofbizUrl>searchSimpleContainerOnly</@ofbizUrl>';
            var field = form.getInputs('hidden', 'saveView').first();
            if (field)
                field.value = 'N';
            var element = new Element('input', {'type': 'hidden', 'name': 'ajaxReloading', 'value': 'Y'});
            form.insert(element);
            var field = form.getInputs('hidden', 'wizard').first();
            if (field) {
                field.value = 'Y';
            }    
            else {
	            var element = new Element('input', {'type': 'hidden', 'name': 'wizard', 'value': 'Y'});
	            form.insert(element);
            }
            ajaxSubmitFormUpdateAreas(form, '${searchPanelArea?if_exists}', '');
        }   
	}       
   
    
}
WorkEffortRootViewSearchExtension.load();
       
 