var GlAccountFormInsert = Class.create({ });

GlAccountFormInsert.load = function(content, useResponder) {
	var form = null;
	if (content) {
		if (content.tagName == "FORM") {
			form = $(content);
		} else {
        	form = $(content).down('#GAMF001_GlAccountFormInsert');
        }
	} else {
        form = $('GAMF001_GlAccountFormInsert');
	}
    if (form) {
        GlAccountFormInsert.registerDropList(form);
        if (!useResponder) {
            UpdateAreaResponder.Responders.register(GlAccountFormInsert.responder, 'GlAccountFormInsert');
        }
		if (!Object.isElement(form.getInputs('hidden', 'justRegisters').first())) {
            var newElement = new Element('input', {'type':'hidden', 'id': 'justRegisters', 'name':'justRegisters', 'value':'Y', 'class':'auxiliary-parameters'});
            form.insert(newElement);
        }
    }
};

GlAccountFormInsert.responder = {
    onLoad : function(newContent) {
        GlAccountFormInsert.load(newContent, true);
    },
    unLoad : function() {
    	return typeof 'GlAccountFormInsert' === 'undefined';
    },
    unLoadCondition : function() {
    	return !Object.isElement($('GAMF001_GlAccountFormInsert'));
    }
};

GlAccountFormInsert.registerDropList = function(form) {

    var formName = form.readAttribute('name');
    var dropList = $(form).down('#' + formName + '_childFolderFile');
    if (dropList) {
    	dropList.observe('change', GlAccountFormInsert.callFunction.curry(form));
    }
};

GlAccountFormInsert.callFunction = function(form, event) {
    if (form) {
        form.action = '/accountingext/control/managementContainerOnly';														
        var field = form.getInputs('hidden', 'saveView').first();
        if (field)
            field.value = 'N';
        field = form.getInputs('hidden', 'entityName').first();
        if (field){
        	var childFolderFile = form.down('select[name=childFolderFile]');
        	if(childFolderFile && childFolderFile.value == 'FILE'){
        		field.value = 'GlAccountView';
        	} else if(childFolderFile && childFolderFile.value == 'FOLDER'){
        		field.value = 'GlAccountClassView';
        	}
        }    
        field = form.getInputs('hidden', 'ajaxReloading').first();
        if (field)
            field.value = 'Y';
        else {    
	        var element = new Element('input', {'type': 'hidden', 'name': 'ajaxReloading', 'value': 'Y'});
	        form.insert(element);
        }
        // anche se insertMode è presente nella form
        // imposto insertMode = W in modo che popoli bene i ccmapi della form
        field = form.getInputs('hidden', 'insertMode').first();
        if (field) {
        	field.value = 'Y';
        }    
        else {    
	        var element = new Element('input', {'type': 'hidden', 'name': 'insertMode', 'value': 'Y'});
	        form.insert(element);
	    }
        field = form.getInputs('hidden', 'justRegisters').first();
        if (field) {
        	field.value = 'Y';
        }    
        else {    
	        var element = new Element('input', {'type': 'hidden', 'name': 'justRegisters', 'value': 'Y'});
	        form.insert(element);
	    }
        Event.stop(event);
        ajaxSubmitFormUpdateAreas(form, 'treeview-detail-screen', '');
    }
};

<#if !parameters.justRegisters?has_content>
	GlAccountFormInsert.load();
</#if>
