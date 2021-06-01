PeriodTypeList = {
	 load: function(newContentToExplore, withoutResponder) {
        var formToExplore = Object.isElement(newContentToExplore) ? newContentToExplore.up("form") : null;
        if(!Object.isElement(formToExplore)) {
            formToExplore = $("PTMM001_PeriodType");
        }
        if (Object.isElement(formToExplore) && formToExplore.identify() === "PTMM001_PeriodType") {
               PeriodTypeList.loadForm(formToExplore);
        }
        if (!withoutResponder) {
            UpdateAreaResponder.Responders.register(PeriodTypeList.responder, "PeriodTypeList");
        }
     },
     
     responder: {
                onLoad : function(newContent) {
                    PeriodTypeList.load(newContent, true);
                },
                unLoad : function() {
                    return typeof "PeriodTypeList" === "undefined";
                }
    },
    
    loadForm: function(form) {
    	if (Object.isElement(form)) {
    		var formName = form.readAttribute("name");
    		var lookups = form.select("div.lookup_field");
    		if (Object.isArray(lookups) && lookups.size() > 0) {
    			lookups.each(function(lookup) {
    				var toListen = LookupMgr.getLookup(lookup.identify());
    				toListen.registerOnSetInputFieldValue(PeriodTypeList.setValueUomIdDecimalDigits.curry(lookup.up("tr")), "PeriodTypeList_uomIdDropListListener"); 
    			});
    		}
    	}
    },
    
    setValueUomIdDecimalDigits : function(tr){
        var decimalScaleField = $(tr.down("input.decimalScale"));
        var valueUomIdField = $(tr.down("input.mask_field_decimalScale"));
        if(decimalScaleField && valueUomIdField)
        	valueUomIdField.setDecimalDigits(decimalScaleField);
    },

}

<#if !parameters.justRegisters?has_content>
PeriodTypeList.load();
</#if>