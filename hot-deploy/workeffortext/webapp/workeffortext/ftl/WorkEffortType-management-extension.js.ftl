WorkEffortTypeManagement = {
	
	load: function() {
        var form = WorkEffortTypeManagement.loadManagementForm();
        if(form) {
            WorkEffortTypeManagement.setParentRelAssocTypeDesc(form);      	
        }
	},
		
	loadManagementForm : function() {
	    var form = null;
	
     	var myTabs = Control.Tabs.instances[0];
     	var containerSelected = null;
     	if(!myTabs) {
     		containerSelected = $('main-container')
     	} else {
     		containerSelected = $(myTabs.getActiveContainer());
     	}
     	
    	if (containerSelected) {	     	
	     	form = $(containerSelected).down('form.basic-form');
	    }
	    return form;	
	},
	
	setParentRelAssocTypeDesc: function(form) {
		var formName = form.readAttribute('name');
		
		var parentRelTypeDesc = '';
		var parentRelTypeDescField = $(form.down("input[name='parentRelTypeDesc']"));
		if(parentRelTypeDescField) {
			parentRelTypeDesc = parentRelTypeDescField.getValue();
		}

	    var parentRelAssocTypeField = $(form.down("input#" + formName + "_parentRelAssocTypeId_edit_field"));
	    if(parentRelAssocTypeField) {
	    	parentRelAssocTypeField.setValue(parentRelTypeDesc);
	    }
	}
}

WorkEffortTypeManagement.load();	
