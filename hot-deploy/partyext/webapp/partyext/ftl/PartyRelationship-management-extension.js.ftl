PartyRelationshipManagemetExtension = {
    load: function () {
		var tables = new Array();
		var cnt = $('main-container');
		if (Object.isElement(cnt)) {
	    	cnt.select('table').each(function(table) {
	    	    var idTable = table.identify();
	    	    if (idTable && idTable.startsWith('table_PRVMLF01_PartyRoleViewRelationship')) {
	    	        tables.push(table);
	    	    }
	    	});
	    	tables.each(function(table) {
				PartyRelationshipManagemetExtension.loadTable(table);
			});		
		}
    },
    
    loadTable: function(table) {
	    table.select('a.link-authorize').each(function(input) {
		    var row = input.up('tr');
			input.observe("click", PartyRelationshipManagemetExtension.clickAuthorizeListener.curry(row));
	    }); 

    },
    
    clickAuthorizeListener: function(row) {
        if (Object.isElement(row)) {
         	var partyRelationshipTypeId = row.down("input[name='partyRelationshipTypeId']").getValue();
	        
	        ReasonPopupMgr = Class.create({ });
         
        	ReasonPopupMgr.validate = function() {
					var partyIdFrom = row.down("input[name='partyIdFrom']").getValue();
	        		var partyIdTo = row.down("input[name='partyIdTo']").getValue();
	        		var roleTypeIdFrom = row.down("input[name='roleTypeIdFrom']").getValue();
	        		var roleTypeIdTo = row.down("input[name='roleTypeIdTo']").getValue();
	        		var fromDate = row.down("input[name='fromDate']").getValue();
	        		var thruDate = row.down("input[name='thruDate']").getValue();
					var ctxEnabled = $('ctxEnabled')
                    if (ctxEnabled != null) {
                        ctxEnabled = ctxEnabled.getValue();
                    }
					
	        		new Ajax.Request("<@ofbizUrl>authorizeBelowStructures</@ofbizUrl>", {
			    		parameters: {
			    			"partyIdFrom": partyIdFrom,
			        		"partyIdTo": partyIdTo,
			        		"roleTypeIdFrom": roleTypeIdFrom,
			        		"roleTypeIdTo": roleTypeIdTo,
			        		"fromDate": fromDate,
			        		"thruDate": thruDate,
			        		"ctxEnabled": ctxEnabled	    
			    		},
			    		onSuccess: function(response) {
				    		var data = response.responseText.evalJSON(true);
				    		if (data) {
				        		var message = data._ERROR_MESSAGE_ ? data._ERROR_MESSAGE_ : data.message;
	                    		modal_box_messages.alert(message);
				    		}
			    		},
			    		onFailure: function() {
			    		}
		    		});	                                    
			}
        	var ctxEnabled = row.down("input[name='ctxEnabled']").getValue();
	                                						    
	        var html = '<div class="MB_alert"><p>' + "${uiLabelMap.AuthorizeConfirmationMessage}?" + '</p>';
	        if(partyRelationshipTypeId == "ORG_DELEGATE") {
	        	html += '<br/>'+
	        	'<textarea rows="2" cols="50" name="ctxEnabled" id="ctxEnabled" class="">' + ctxEnabled + '</textarea>'+
	        	'<br/>'
	        } 
	        
	        html += '<input type="button" onclick="javascript: ReasonPopupMgr.validate();" value="${uiLabelMap.BaseButtonOK}" />'+
	        '<input type="button" onclick="Modalbox.hide()" value="${uiLabelMap.BaseButtonCancel}" />'+
	        '</div>';
	        Modalbox.show(html, {title: 'Modalbox', width: 600});
	    }
    }
}

PartyRelationshipManagemetExtension.load();