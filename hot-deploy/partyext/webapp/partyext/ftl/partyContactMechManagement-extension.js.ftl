PartyContactMechManagementExtension = {
    load: function(newContentToExplore, withoutResponder) {
        var form = Object.isElement(newContentToExplore) ? (newContentToExplore.tagName == 'FORM' ? $(newContentToExplore) : newContentToExplore.down('form.cachable')) : $('managementForm');
        if (Object.isElement(form)) {
            PartyContactMechManagementExtension.registerDropList(form);
            if (!withoutResponder) {
                UpdateAreaResponder.Responders.register(PartyContactMechManagementExtension.responder, 'PartyContactMechManagementExtension');
            }

            if (!Object.isElement($('justRegisters'))) {
                var newElement = new Element('input', {'type':'hidden', 'id': 'justRegisters', 'name':'justRegisters', 'value':'Y', 'class':'auxiliary-parameters'});
                form.insert(newElement);
            }
        }
    },

    responder : {
        onLoad : function(newContent) {
            PartyContactMechManagementExtension.load(newContent, true);
        },
        unLoad : function() {
            return typeof 'PartyContactMechManagementExtension' === 'undefined';
        }
    },

    registerDropList : function(form) {
        var formName = form.readAttribute('name');
        var dropList = DropListMgr.getDropList(formName + '_contactMechTypeId');
        if (dropList) {
            dropList.registerOnChangeListener(PartyContactMechManagementExtension.callFunction.curry(form), 'contactMechTypeId');
        }
    },

    callFunction : function(form){
        if (form) {
            form.action = '/partyext/control/managementContainerOnly?externalLoginKey=${context.externalLoginKey?if_exists}';
            var field = form.getInputs('hidden', 'saveView').first();
            if (field)
                field.value = 'N';
            var element = new Element('input', {'type': 'hidden', 'name': 'ajaxReloading', 'value': 'Y'});
            form.insert(element);
            ajaxSubmitFormUpdateAreas(form, 'common-container', '');
        }
    }
}

<#if !parameters.justRegisters?has_content>
PartyContactMechManagementExtension.load();
</#if>
