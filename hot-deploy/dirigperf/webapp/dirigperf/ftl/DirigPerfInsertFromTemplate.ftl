<div id="DirigPerfInsertFromTemplateSubmit" style="position: relative;">
    ${screens.render(submitFormScreenLocation, submitFormScreenName, context)}

    <div style="height: 10px;"></div>
</div>

<div style="height: 10px;"></div>

    <script type="text/javascript">

    
    	var container = $$("#DirigPerfInsertFromTemplateSubmit")[0];
        var button = $$(".search-execute")[0];
        
        if(Object.isElement(button) && Object.isElement(container)) {
            button.observe("click", function(e) {
        	    var myTabs = Control.Tabs.instances[0];
        	 	var containerSelected = null;
        	 	/* Se il tab non esiste, sono nel caso che nn ho tab */
        	 	if(!myTabs){
        	 		containerSelected = $('main-container')
        	 	}else{
        	 		containerSelected = $(myTabs.getActiveContainer());
        	 	}
        	 	if (containerSelected) {
        	     	
        	     	var form = null;
        	     	
        	     	var childDivContainer = $(containerSelected).down('div.child-management-container');
        	     	if(Object.isElement(childDivContainer)){
        	     		form = $(childDivContainer).down('form.basic-form');
        	     		// caso form di tipo lista
        	     		if(!Object.isElement(form)) {
        	                form = $(childDivContainer).down('div.child-management');
        	                
        	            }
        	     	}else{
        	     		form = $(containerSelected).down('form.basic-form');
        	     	}
        	     	
            		if(!Object.isElement(form)) {
        	     	    newContent = $(newContent);
        	     	    form = newContent.down('form.basic-form');
        	     	}
            		
        	     	var anchorTarget = form.select("a.execute-button");
        	        if(Object.isArray(anchorTarget) && anchorTarget.size() > 0){
        	            var anchor = anchorTarget[0];
        	            if(Object.isElement(anchor)){
        	            	var target = anchor.readAttribute("onclick");
        	            	target = target.substring(target.indexOf(',') + 1, target.lastIndexOf(','));
        	            }
        	        }
	            	var button = Event.element(e);
	                
	                // controllo campi obbligatori
	                var mandatoryFields = form.select("input.mandatory");
	                for(var i = 0; i < mandatoryFields.size() ; i++) {
	                    var input = mandatoryFields[i];
	                    if(input.getValue() == "") {
	                        modal_box_messages.alert(['BaseMessageExecuteMandatoryField']);
	                        return false;
	                    }
	                }
	
	                new Ajax.Request(target, {
	                    parameters: form.serialize(true),
	                    onSuccess: function(transport) {
	                        var data = transport.responseText.evalJSON(true);
	
	                        if (data._ERROR_MESSAGE_ != null || data._ERROR_MESSAGE_LIST_ != null) {
	                            modal_box_messages._resetMessages();
	                            modal_box_messages.onAjaxLoad(data, Prototype.K);
	                            return false;
	                        }
		                                              
	                        var jobLogId = data.jobLogId;	                        
	                        var msgResult = "";
	                        
	                        if (jobLogId) {
	                        	msgResult += "${uiLabelMap.EmplPerfUpdateNote_finished}<br><br>${uiLabelMap.StandardImport_recordElaborated}" +
		                            		data.recordElaborated + "<br><br>${uiLabelMap.StandardImport_blockingErrors}" + 
		                            		data.blockingErrors + "<br><br>${uiLabelMap.StandardImport_jobLogId}" + " " + 
		                            		jobLogId;
	                        }
	                        
	                        var sessionsMap = data.sessionsMap;
	                        
	                        if (sessionsMap) {
	                        	var acctgTransSessionId = sessionsMap.acctgTransSessionId;
	                        	var assocSessionId = sessionsMap.assocSessionId;
	                        	
	                        	if (acctgTransSessionId) {
	                        		if (msgResult && msgResult.length > 0) {
	                        			msgResult += "<br><br>";
	                        		}
	                        		msgResult += "${uiLabelMap.EmplPerfUpdateAcctgTrans_scheduled}" + " " + acctgTransSessionId;
	                        	}
	                        	if (assocSessionId) {
	                        		if (msgResult && msgResult.length > 0) {
	                        			msgResult += "<br><br>";
	                        		}	                        	
	                        		msgResult += "${uiLabelMap.EmplPerfUpdateAssoc_scheduled}" + " " + assocSessionId;
	                        	}                        
	                        }
	                        
	                        var sessionId = data.sessionId;
	                        
	                        if (sessionId) {
	                        	if (msgResult && msgResult.length > 0) {
	                        		msgResult += "<br><br>";
	                        	}	                        
	                        	msgResult += "${uiLabelMap.AsynchServiceExecution_sessionId}" + " " + sessionId;
	                        }
	                        
	                        if (msgResult && msgResult.length > 0) {
	                            modal_box_messages._resetMessages();
	                            modal_box_messages.alert(msgResult);
	                            return true;	                        
	                        }
	                    }
	                });
        		}
            });
            
        }
    </script>

