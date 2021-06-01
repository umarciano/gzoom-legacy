PartySkillManagementExtension = {
	load: function(newContentToExplore, withoutResponder) {
		var form = Object.isElement(newContentToExplore) ? newContentToExplore.up("form") : null;
		if(!Object.isElement(form)) {
        	form = $$("form#PSM0001_PartySkill")[0];
        }
        if (Object.isElement(form)) {
        	PartySkillManagementExtension.loadForm(form);
        } 
        
        if (!withoutResponder) {
            UpdateAreaResponder.Responders.register(PartySkillManagementExtension.responder, "PartySkillManagementExtension");
        }
    },
    
    /**
	* Responder functions
	**/
    responder : {
                onLoad : function(newContent) {
                    PartySkillManagementExtension.load(newContent, true);
                },
                unLoad : function() {
                    //TODO Prevedere eventuali deregistrazioni per liberare memoria
                    return typeof "PartySkillManagementExtension" === "undefined";
                }
    },
    
	loadForm: function(form) {
		if (Object.isElement(form)) {
			var formName = form.readAttribute("name");
			var skillTypeLookupDiv = form.down("div#" + formName + "_skillTypeId");
			if (Object.isElement(skillTypeLookupDiv)) {
				var skillTypeLookup = LookupMgr.getLookup(skillTypeLookupDiv.identify());
				var uomRatingIdField = form.down("[name=\"uomRatingId\"]");
				if (skillTypeLookup && Object.isElement(uomRatingIdField)) {
					skillTypeLookup.registerOnSetInputFieldValue(PartySkillManagementExtension.lookupListener.curry(uomRatingIdField, skillTypeLookupDiv), 'PartySkillManagementExtension_lookupListener');
				}
			}
		}
	},
	
	lookupListener: function(uomRatingIdField, skillTypeLookupDiv) {
		if (Object.isElement(uomRatingIdField) && Object.isElement(skillTypeLookupDiv)) {
			var uomRatingIdLookup = skillTypeLookupDiv.down("input#uomRatingId_skillTypeId");
			var uomRatingIdLookupValue = Object.isElement(uomRatingIdLookup) ? uomRatingIdLookup.readAttribute("value") : "";
			
			uomRatingIdField.writeAttribute("value", uomRatingIdLookupValue);
		}
	}
}

PartySkillManagementExtension.load();
