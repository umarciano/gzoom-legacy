FormKitExtension = {
    // call check modification for cachableForm
    // show confirm message for save modification
        
    // return false if there is not modification 
    // or if user click on "Cancel"
    // TODO What return if save?, now return object with modifcation
    checkModficationWithAlert : function(cachableForm) {
		var result = false;

        if(Object.isElement(cachableForm)){
        	result = FormKit.Cachable.checkModification(cachableForm.id, FormKitExtension.checkModficationFilterCallback);
        }
        // console.log("checkModficationWithAlert: result " + result); 
        if(result) {
            var saveMenuItem = null;
            
            var parentScreenlet = cachableForm.up("div.transactionPortlet");
            // console.log("checkModficationWithAlert : parentScreenlet " + parentScreenlet);
            if (parentScreenlet) {
                saveMenuItem = parentScreenlet.down("li.save");
            } else {
                var onsubmit = cachableForm.readAttribute("onsubmit");
                if (onsubmit) {
                    var containerId = onsubmit.split(",")[2].substring(1);
                    // console.log(" containerId " + containerId);
                    // check if containerId is correct,
                    // for some form the containerId is wrong or null
                    if (Object.isElement($(containerId))) {
                        saveMenuItem = Toolbar.getInstance(containerId).getItem(".save");
                    }
                }
            }
            // _loadMessage is async, so use a input[type="text"] with id, in "message-boxes-container
            var confirmMessageLabel = $('confirm-message-label');
            var msg = "Salvare le modifiche?"
            if (Object.isElement(confirmMessageLabel)) {
                msg = confirmMessageLabel.innerHTML;
            }
            // console.log("checkModficationWithAlert: chiedo confirm e result " + result); 
            if(confirm(msg)) {
                // console.log("checkModficationWithAlert: attenzione save e result " + result);
                if (Object.isElement(saveMenuItem))
                    saveMenuItem.fire('dom:click');
                
                FormKit.loadFields(cachableForm);
            } else {
                result = false;
                // console.log("checkModficationWithAlert: attenzione cancel e result " + result); 
                // Non utilizzare il clik sul reset perche si creano dei problemi di rendere nella pagina e il tab va in loop
            	FormKit.Cachable.resetForm(cachableForm);
            }
        }
        
        // console.log("checkModficationWithAlert: return result " + result);
  		return result;
	},

    checkModficationFilterCallback : function(element) {
        var name = element.readAttribute('name');
        if (name.indexOf('_o_') != -1) {
             name = name.substring(0, name.indexOf('_o_'));
        }
        return !['insertMode', 'operation', 'subFolder', 'entityName', 'parentEntityName', 'successCode', 'justRegisters'].include(name)
            && !element.hasClassName('ignore_check_modification')
            && !element.hasClassName('dateParams')
            && !element.hasClassName('droplist_edit_field')
            && !element.hasClassName('lookup_field_description') 
            && !element.hasClassName('autocompleter_option') 
            && !element.hasClassName('autocompleter_parameter') 
            && name.indexOf('el-') == -1;
    }
}

var FormInsertManagement = Class.create( {
    initialize : function(onclickStr, form) {
    	this.argument = "";
    	this.container = "";
    	if (onclickStr && onclickStr.indexOf('(') != -1 && onclickStr.indexOf(')') != -1) {
    		this.argument = onclickStr.substring(onclickStr.indexOf('(')+ 2, onclickStr.lastIndexOf(','));
    		var parameters = onclickStr.substring(onclickStr.lastIndexOf(',')+ 1, onclickStr.indexOf('\')'));
    		this.container = onclickStr.substring(onclickStr.indexOf('(\'')+ 2, onclickStr.indexOf(','));
        
    		if (parameters && Object.isString(parameters)) {
    			this.parametersMap = $H(parameters.toQueryParams());
    			if (this.parametersMap) {
    				this.parametersMap.each(function(pair) {
    					if ("extraParameters" == pair.key) {
    						var extraParameters = pair.value.gsub('\\[', '').gsub('\\]', '').gsub('\\|', '&');
    						extraParametersMap = $H(extraParameters.toQueryParams());
    						extraParametersMap.each(function(innerPair) {
    							var field = form.getInputs('hidden', innerPair.key).first();
    							if (field) {
    								field.writeAttribute('value', innerPair.value);
    							} else {
    								field = new Element('input', { 'type': 'hidden', 'name': innerPair.key, 'value': innerPair.value });
    								form.insert(field);
    							}
    						});
    					} else {
    						var field = form.getInputs('hidden', pair.key).first();
    						if (field) {
    							field.writeAttribute('value', pair.value);
    						} else {
    							field = new Element('input', { 'type': 'hidden', 'name': pair.key, 'value': pair.value });
    							form.insert(field);
    						}
    					}
    				});
    			}
    		}
    	}
    }
});
