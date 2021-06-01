PartyRoleViewRelationshipRoleViewSearchExtension = {
    load: function() {
        var form = $('searchForm');
        if (form) {
        	PartyRoleViewRelationshipRoleViewSearchExtension.registerPartyRelationshipTypeIdFromField(form);
        	PartyRoleViewRelationshipRoleViewSearchExtension.clearPartyRelationshipTypeIdFromField();
        	PartyRoleViewRelationshipRoleViewSearchExtension.registerDropList(form);                        
        }
	},
	
	registerPartyRelationshipTypeIdFromField : function(form) {
		var partyRelationshipTypeIdFromField = form.down("input[name='partyRelationshipTypeIdFrom']");
		if (Object.isElement(partyRelationshipTypeIdFromField)) {
			PartyRoleViewRelationshipRoleViewSearchExtension.partyRelationshipTypeIdFromField = partyRelationshipTypeIdFromField;
			PartyRoleViewRelationshipRoleViewSearchExtension.partyRelationshipTypeIdFromValue = partyRelationshipTypeIdFromField.getValue();
		}
	},
	
    registerDropList : function(form) {
        var formName = form.readAttribute('name');           
        var dropList = DropListMgr.getDropList(formName + '_partyIdFrom');
        if (dropList) {
        	dropList.registerOnChangeListener(PartyRoleViewRelationshipRoleViewSearchExtension.setPartyRelationshipTypeIdFromField.curry(form), 'setPartyRelationshipTypeIdFromField');
        }
    },    
    
    setPartyRelationshipTypeIdFromField : function(form) {
		var partyIdFrom = PartyRoleViewRelationshipRoleViewSearchExtension.getFieldValue(form, 'partyIdFrom');

		if (Object.isElement(PartyRoleViewRelationshipRoleViewSearchExtension.partyRelationshipTypeIdFromField)) {
			if (partyIdFrom.empty()) {
				PartyRoleViewRelationshipRoleViewSearchExtension.clearPartyRelationshipTypeIdFromField();
			} else {
				var partyRelationshipTypeIdFromValue = PartyRoleViewRelationshipRoleViewSearchExtension.partyRelationshipTypeIdFromValue;
				PartyRoleViewRelationshipRoleViewSearchExtension.partyRelationshipTypeIdFromField.setValue(partyRelationshipTypeIdFromValue);
			}
		}
    },
    
    clearPartyRelationshipTypeIdFromField : function() {
    	if (Object.isElement(PartyRoleViewRelationshipRoleViewSearchExtension.partyRelationshipTypeIdFromField)) {
    		PartyRoleViewRelationshipRoleViewSearchExtension.partyRelationshipTypeIdFromField.clear();
    	}
    },
    
	getFieldValue: function(form, fieldName) {
	    var field = form.down("input[name='" + fieldName + "']");
	    if (Object.isElement(field)) {
	    	return field.getValue();
	    }
		return '';
	}
}

PartyRoleViewRelationshipRoleViewSearchExtension.load();