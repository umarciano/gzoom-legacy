<#import "/base/webapp/common/ftl/gzoomLogger.ftl" as gzoomLogger/>

<#if localeSecondarySet?if_exists == "Y">
	<#assign weStatusValue = weStatusDescrLang?if_exists/>
	<#assign weStatusFieldName = "weStatusDescrLang"/>
<#else>
	<#assign weStatusValue = weStatusDescr?if_exists/>
	<#assign weStatusFieldName = "weStatusDescr"/>
</#if>

<input type="hidden" id="workEffortRootId" value="${parameters.workEffortIdRoot?if_exists}"/>
<table id="IndividualPerformanceHeaderTable" cellspacing="0" cellpadding="0">
	<tbody>
	    <#if showParentAssoc?default("N") == "Y">
	        <tr>
	            <td class="label">${parentRel?if_exists}</td>
	            <td colspan="3"><input type="text" size="50" value="${parentRelWe?if_exists}" readonly="readonly"/></td>
	        </tr>
	    </#if>	
		<tr>
		    <td class="label">
		        <div style="margin-left: 30px;">${inChargeLabel?if_exists}</div>
		    </td>
			<td>
                <input type="text" size="50" value="${inChargeName?if_exists}" name="inChargeName" readonly="readonly"/>
			</td>
			<td class="label">${uiLabelMap.HeaderWorkEffortStatus}</td>
				<td>
					<table cellspacing="0" cellpadding="0">
						<tbody>
							<tr>
								<#if processId?has_content>
											
								<#if isProcessEnded == false && parameters.rootInqyTree != "Y">
									<td>
										<input style="float: left" type="text" size="30" value="${weStatusValue?if_exists}" name="${weStatusFieldName?if_exists}" readonly="readonly"/>
									</td>
									<#if destinationMap?has_content>
										<td>
											<select name="destinations" id="destinations">
											<#assign destinations=destinationMap.keySet()/>
											<#list destinations as destination>
												<option value="${destinationMap[destination]?default('')}">${destination}</option>
											</#list>
										</td>
									</#if>
									<td>
										<a href="#" id="statusNextStep" class="statusProgress" title="${uiLabelMap.StatusProgress}">${uiLabelMap.StatusProgress}</a>
										<script type="text/javascript">
											var nextStepLink = $("statusNextStep");
											if(Object.isElement(nextStepLink)) {
													 	
												Event.stopObserving(nextStepLink, "click");
												Event.observe(nextStepLink, "click", function(e) {
													modal_box_messages.confirm("${uiLabelMap.WorkEffortConfirmChangeStatus}",null, function() {
													 	var destinations = $("destinations");
													 	var transition = null;
				                                        if(Object.isElement(destinations)) {
				                                            var selectedIndex = destinations.selectedIndex;
				                                            transition = destinations.options[selectedIndex].value;
				                                        }
	                                                    
					                                    var newForm = new Element('form', {id : 'changeStatusForm', name : 'changeStatusForm'});
				                                        newForm.writeAttribute('action', '<@ofbizUrl>elaborateFormForUpdateAjax</@ofbizUrl>');
				                                        newForm.insert(new Element('input', {name : 'workEffortId', type : 'hidden', value : '${parameters.workEffortIdRoot}'}));
				                                        newForm.insert(new Element('input', {name : 'entityName', type : 'hidden', value : 'WorkEffortStatus'}));
				                                        newForm.insert(new Element('input', {name : 'crudService', type : 'hidden', value : 'crudServiceOperation_SignalProcessExecution'}));
				                                        newForm.insert(new Element('input', {name : 'operation', type : 'hidden', value : 'CREATE'}));
				                                        newForm.insert(new Element('input', {name : 'saveView', type : 'hidden', value : 'N'}));
				                                        newForm.insert(new Element('input', {name : 'ignoreAuxiliaryParameters', type : 'hidden', value : 'Y'}));
				                                        newForm.insert(new Element('input', {name : 'transition', type : 'hidden', value : transition}));
				                                                    
					                                    document.body.insert(newForm);
					                                                
					                                    // bug 5476 aggiunti parametri per il search Location, altrimenti dopo aver cambiato lo stato non riesce a caricare la lista di schede
		                                                ajaxSubmitFormUpdateAreas("changeStatusForm","","common-container,<@ofbizUrl>searchResultContainerOnly</@ofbizUrl>,searchResultContextFormName=${StringUtil.wrapString(parameters.searchResultContextFormName?if_exists)?replace("&#58;", ":")}&searchResultContextFormLocation=${StringUtil.wrapString(parameters.searchResultContextFormLocation?if_exists)?replace("&#58;", ":")}&searchFormScreenLocation=${StringUtil.wrapString(parameters.searchFormScreenLocation?if_exists)?replace("&#58;", ":")}&searchFormScreenName=${StringUtil.wrapString(parameters.searchFormScreenName?if_exists)?replace("&#58;", ":")}&advancedSearchFormLocation=${StringUtil.wrapString(parameters.advancedSearchFormLocation?if_exists)?replace("&#58;", ":")}&searchFormResultLocation=${StringUtil.wrapString(parameters.searchFormResultLocation?if_exists)?replace("&#58;", ":")}&searchFormLocation=${StringUtil.wrapString(parameters.searchFormLocation?if_exists)?replace("&#58;", ":")}&${parameters.searchFormLocationParameters?if_exists}&backAreaId=common-container&${StringUtil.wrapString(parameters.queryString?if_exists)?replace("&amp;", "&")}&orderBy=${StringUtil.wrapString(parameters.orderBy?if_exists)}&entityName=WorkEffortRootView&currentStatusContains=${parameters.currentStatusContains?if_exists}");
					                                                    
					                                    newForm.remove();
					                                }, function() {});
												});
											}
										</script>
									</td>
								<#else>
									<td>
										<input style="float: left" type="text" size="50" value="${weStatusValue?if_exists}" name="${weStatusFieldName?if_exists}" readonly="readonly"/>
									</td>
								</#if>
									<td>
										<!-- <a id="bo" href="<@ofbizUrl>downloadProcessImage</@ofbizUrl>?workEffortId=${parameters.workEffortIdRoot}" target="_blank">immagine</a> -->
										<#if jbpmServerEnabled == true>
    										<a id="processImage" href="#" class="processImage" title="${uiLabelMap.ProcessImage}">${uiLabelMap.ProcessImage}</a>
    										<script type="text/javascript">
    											var processImage = $("processImage");
    											if(Object.isElement(processImage)) {
    												Event.stopObserving(processImage, "click");
    												Event.observe(processImage, "click", function(e) {
    													var newForm = new Element('form', {id : 'processImageForm', name : 'processImageForm'});
    				                                    newForm.writeAttribute('action', '<@ofbizUrl>elaborateFormForUpdateAjax</@ofbizUrl>');
    				                                    newForm.insert(new Element('input', {name : 'workEffortId', type : 'hidden', value : '${parameters.workEffortIdRoot}'}));
    				                                    newForm.insert(new Element('input', {name : 'saveView', type : 'hidden', value : 'N'}));
    				                                    newForm.insert(new Element('input', {name : 'ignoreAuxiliaryParameters', type : 'hidden', value : 'Y'}));
    				                                                 
    				                                    newForm.insert(new Element('input', {name : 'crudService', type : 'hidden', value : 'crudServiceOperation_downloadProcessImage'}));
    				                                    newForm.insert(new Element('input', {name : 'entityName', type : 'hidden', value : 'WorkEffortStatus'}));
    				                                    newForm.insert(new Element('input', {name : 'operation', type : 'hidden', value : 'CREATE'}));
    				                                                 
    				                                    document.body.insert(newForm);
    				                                                 
    				                                    ajaxSubmitFormUpdateAreas("processImageForm","","", {
    				                                        onSuccess: function(transport) {
    				                                            var responseText = transport.responseText;
    				                                            var json = responseText.evalJSON(true);
    				                                            if(json && json.id && json.id.imageName) {
    				                                                var imageName = json.id.imageName;
    				                                                Utils.showModalBox("<img src='/workeffortext/images/tmp/" + imageName + "' width='970' height='670'/>", {width: '1000', height: '700'});
    				                                            }
    				                                                 		
    				                                        }
    				                                    });
    					                                                    
    					                                newForm.remove();
    												});
    											}
    										</script>
										</#if>
									</td>
								<#else>
								<#assign nextStatusItemList = delegator.findByAnd("StatusValidChange", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId",  weStatusId))>
								<#assign editableStatus = (parameters.rootInqyTree != "Y" && nextStatusItemList?has_content)>
								<#assign canGoBackStatus = (parameters.rootInqyTree != "Y" && backStatusId?has_content)>

								<td>
				                    <#if editableStatus>
				                        <select name="currentStatusId" id="currentStatusId">
					                        <option value="${weStatusId}" selected="selected">${weStatusValue?if_exists}</option>
					                        <#if nextStatusItemList?has_content>
						                        <#list nextStatusItemList as item>
						                            <#assign nextStatus = delegator.findOne("StatusItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", item.statusIdTo), true)>
						                            <#assign result = dispatcher.runSync("checkWorkEffortStatusHasMandatoryNoteEmpty", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", item.statusIdTo, "workEffortId", parameters.workEffortIdRoot, "userLogin", userLogin))/>
						                            <#assign hasMandatoryNoteEmpty = result.hasMandatoryNoteEmpty?string("hasMandatoryNoteEmpty", "") />
						                            <#if localeSecondarySet?if_exists == "Y">
						                                <option class="${hasMandatoryNoteEmpty}" value="${item.statusIdTo}">${nextStatus.descriptionLang?if_exists}</option>
						                            <#else>
						                                <option class="${hasMandatoryNoteEmpty}" value="${item.statusIdTo}">${nextStatus.description?if_exists}</option>
						                            </#if>
						                        </#list>
					                        </#if>
				                        </select>

				                    <#else>
					                    <#if canGoBackStatus>
					                        <input type="text" size="33" id="currentStatusId" value="${weStatusValue?if_exists}" name="${weStatusFieldName?if_exists}" readonly="readonly"/>
					                    <#else>
					                        <input type="text" size="50" id="currentStatusId" value="${weStatusValue?if_exists}" name="${weStatusFieldName?if_exists}" readonly="readonly"/>
					                    </#if>
									</#if>
								</td>
								<#if canGoBackStatus>
									<td>
									    &nbsp;<a href="#" id="statusBackStep" class="statusBack" title="${uiLabelMap.StatusBack}">${uiLabelMap.StatusBack}</a>										
									</td>											
								</#if>
											
								<#if (editableStatus || canGoBackStatus)>
									<script type="text/javascript">
										var initialStatusId = '';
										var statusField = $('currentStatusId');
				                        if (Object.isElement(statusField)) {
				                        	initialStatusId = statusField.getValue();
				                        }
									
					                    var isTextAreaVisible = false;
					                    var hasMandatoryComment = false; // valore iniziale di default

				                        ReasonPopupMgr = Class.create({ });
	
					                    ReasonPopupMgr.getParametersToSubmit = function(statusId) {    
					                        var parametersToSubmit = {};
					                        parametersToSubmit.statusId = statusId;
					                                       	
					                        var reason = $('reasonDescription');
					                        if (reason != null) {
					                            parametersToSubmit.reason = reason.getValue();
					                        }
					                        return parametersToSubmit;
					                    }
					                    				                    
					                    /*
				                    		GN-5187
				                    		Questo metodo e' invocato
				                    		- alla selezione dell'OK su cambio stato
				                    		- alla selezione dell'OK su 'stato precedente'
				                    		
				                    		Il risultato si basa sulla (gia' avvenuta) invocazione del server (checkFolderActivated)
				                    	*/
				                        ReasonPopupMgr.manageReasonDescriptionMandatory = function(targetStatusHasMandatoryComment) {
				                        	// console.log('[WorkEffortViewIndividualPerformanceHeader::manageReasonDescriptionMandatory] Lo stato target (input del metodo) ha il commento obbligatorio? ' + targetStatusHasMandatoryComment);
				                        	
				                        	var reason = $('reasonDescription'); // Riferimento al campo 'reason' (text area)
                                 			reason.removeClassName('mandatory');
				                        	this.hasMandatoryComment = 'N';
				                        	if (targetStatusHasMandatoryComment == 'Y') {
				                        		reason.writeAttribute("oninput", "javascript:ReasonPopupMgr.manageOkButton();");
				                        		this.hasMandatoryComment = 'Y';
				                        		// Assegnazione della obbligatorieta' o meno al campo text area (visualizzato giallo o sfondo di default)
	                                 			reason.addClassName('mandatory');
				                        	}
				                        	// console.log('[WorkEffortViewIndividualPerformanceHeader::manageReasonDescriptionMandatory] Conversione in this.hasMandatoryComment = ' + this.hasMandatoryComment);
				                        	
				                        	// console.log('[WorkEffortViewIndividualPerformanceHeader::manageReasonDescriptionMandatory] Invocazione di manageOkButton(), inizio');
				                        	ReasonPopupMgr.manageOkButton();
				                        	// console.log('[WorkEffortViewIndividualPerformanceHeader::manageReasonDescriptionMandatory] Invocazione di manageOkButton(), fine, this.canBeSaved? ' + this.canBeSaved);
				                        }
				                        
				                        /*
				                        	GN-5187
				                        	Questo metodo e' invocato
				                    		- ad ogni singola digitazione / cancellazione della text area, in due casi:
				                    			- in inizializzazione
				                    			- alla esecuzione di OK su cambio stato
				                    		
											L'unico riferimento da FE e' dalla definizione della text area col commento visibile.
				                        	NB: L'obbligatorieta' e' gia' stata valutata in manageReasonDescriptionMandatory
				                        */
				                        ReasonPopupMgr.manageOkButton = function() {
				                        	// console.log("[WorkEffortViewIndividualPerformanceHeader.ftl::manageOkButton] Commento obbligatorio? " + this.hasMandatoryComment);
				                        	
				                        	var reason = $('reasonDescription'); // Riferimento al campo 'reason' (text area)
                                 			var reasonValue = reason.value;
				                        	var reasonIsEmpty = (reasonValue == null || reasonValue === undefined || reasonValue == "");
				                        	
				                        	// console.log("[WorkEffortViewIndividualPerformanceHeader.ftl::manageOkButton] reasonValue = " + reasonValue);
				                        	// console.log("[WorkEffortViewIndividualPerformanceHeader.ftl::manageOkButton] reasonIsEmpty = " + reasonIsEmpty);
				                        	// console.log("[WorkEffortViewIndividualPerformanceHeader.ftl::manageOkButton] isMandatoryComment = " + isMandatoryComment);
				                        	
                                 			this.canBeSaved = true; // default
				                        	var okButton = $('popup-reason-box-container-ok-button');
				                        	if (this.hasMandatoryComment == 'Y' && reasonIsEmpty) {
				                        		// console.log("[WorkEffortViewIndividualPerformanceHeader.ftl::manageOkButton] Disabilitazione (cambio o conferma) del bottone OK");
				                        		this.canBeSaved = false; // salvo sull'istanza in modo che possa essere visto da validate
				                        		okButton.removeClassName("smallSubmit");
												okButton.addClassName("smallSubmit-disabled");
				                        	} else {
				                        		// console.log("[WorkEffortViewIndividualPerformanceHeader.ftl::manageOkButton] Abilitazione (cambio o conferma) del bottone OK");
				                        		
				                        		okButton.removeClassName("smallSubmit-disabled");
												okButton.addClassName("smallSubmit");
				                        	}
				                        }
				                        
				                        ReasonPopupMgr.validate = function() {
					                    	// GN-5187: controllo se il salvataggio e' abilitato
				                			// console.log("[WorkEffortViewIndividualPerformanceHeader.ftl::validate] Salvataggio abilitato (this.canBeSaved)? " + this.canBeSaved);
					                		if (!(this.canBeSaved === undefined) && !this.canBeSaved) {
					            				console.log("[WorkEffortViewIndividualPerformanceHeader.ftl::validate] Salvataggio NON abilitato, metodo validate si interrompe qui");
					                			return;
					            			}
					            			// console.log("[WorkEffortViewIndividualPerformanceHeader.ftl::validate] Salvataggio abilitato, metodo validate prosegue");
					                			
					                        parametersToSubmit = ReasonPopupMgr.getParametersToSubmit(Modalbox.options.statusId);
					                                    	
					                        var newForm = new Element('form', {id : 'changeStatusForm', name : 'changeStatusForm'});
		                                    newForm.writeAttribute('action', '<@ofbizUrl>elaborateFormForUpdateAjax</@ofbizUrl>');
		                                    newForm.insert(new Element('input', {name : 'workEffortId', type : 'hidden', value : '${parameters.workEffortIdRoot}'}));
		                                    newForm.insert(new Element('input', {name : 'entityName', type : 'hidden', value : 'WorkEffortStatus'}));
		                                    newForm.insert(new Element('input', {name : 'crudService', type : 'hidden', value : 'crudServiceDefaultOrchestration_WorkEffortRootStatus'}));
		                                    newForm.insert(new Element('input', {name : 'operation', type : 'hidden', value : 'CREATE'}));
		                                    newForm.insert(new Element('input', {name : 'saveView', type : 'hidden', value : 'N'}));
		                                    newForm.insert(new Element('input', {name : 'ignoreAuxiliaryParameters', type : 'hidden', value : 'Y'}));
		                                    <#assign defaultDateTime = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()>
		                                    newForm.insert(new Element('input', {name : 'statusDatetime', type : 'hidden', value : '${defaultDateTime}'}));
		                                    newForm.insert(new Element('input', {name : 'statusId', type : 'hidden', value : parametersToSubmit.statusId}));
		                                    var searchDate = '${parameters.searchDate?if_exists}';
		                                    newForm.insert(new Element('input', {name : 'searchDate', type : 'hidden', value : searchDate}));
		                                    newForm.insert(new Element('input', {name : 'reason', type : 'text', id: 'pippo_reason', value : parametersToSubmit.reason}));
			                                newForm.insert(new Element('input', {name : 'messageContext', type : 'hidden', value : 'BaseMessageSaveData'}));
					                    	
		                                    document.body.insert(newForm);
		                                    
		                                    var currentStatusContains = '${parameters.currentStatusContains?if_exists}';
		                                    if (currentStatusContains == '' || currentStatusContains == null || currentStatusContains == 'null') {
		                                    	if (initialStatusId && initialStatusId.indexOf('_PLAN') > -1) {
		                                    		currentStatusContains = '_PLAN';
		                                    	}
		                                    	if (initialStatusId && initialStatusId.indexOf('_MONIT') > -1) {
		                                    		currentStatusContains = '_MONIT';
		                                    	}
		                                    	if (initialStatusId && initialStatusId.indexOf('_EXEC') > -1) {
		                                    		currentStatusContains = '_EXEC';
		                                    	}
		                                    }
		                                    
		                                    var gpMenuEnumId = '';
			                                var mainForm = $('WorkEffortRootViewManagementForm');
			                                if (Object.isElement(mainForm)) {
			                                    var gpMenuEnumIdField = mainForm.down('input[name=gpMenuEnumId]');
												if (Object.isElement(gpMenuEnumIdField)) {
													gpMenuEnumId = gpMenuEnumIdField.getValue();
												} 
											}
											var childStruct = '${parameters.childStruct?if_exists?default("N")}';
											var hideIsRootActive = '${parameters.hideIsRootActive?if_exists?default("")}'; 
											var hideChildStruct = '${parameters.hideChildStruct?if_exists?default("")}';
					                        var noLeftBar = '${parameters.noLeftBar?if_exists?string}';
					                        var gpMenuOrgUnitRoleTypeId = '${parameters.gpMenuOrgUnitRoleTypeId?if_exists?default("N")}';

		                                    // bug 5476 aggiunti parametri per il search Location, altrimenti dopo aver cambiato lo stato non riesce a caricare la lista di schede
		                                    ajaxSubmitFormUpdateAreas('changeStatusForm','','common-container,<@ofbizUrl>searchResultContainerOnly</@ofbizUrl>,searchResultContextFormName=${StringUtil.wrapString(parameters.searchResultContextFormName?if_exists)?replace("&#58;", ":")}&searchResultContextFormLocation=${StringUtil.wrapString(parameters.searchResultContextFormLocation?if_exists)?replace("&#58;", ":")}&searchFormScreenLocation=${StringUtil.wrapString(parameters.searchFormScreenLocation?if_exists)?replace("&#58;", ":")}&searchFormScreenName=${StringUtil.wrapString(parameters.searchFormScreenName?if_exists)?replace("&#58;", ":")}&advancedSearchFormLocation=${StringUtil.wrapString(parameters.advancedSearchFormLocation?if_exists)?replace("&#58;", ":")}&searchFormResultLocation=${StringUtil.wrapString(parameters.searchFormResultLocation?if_exists)?replace("&#58;", ":")}&searchFormLocation=${StringUtil.wrapString(parameters.searchFormLocation?if_exists)?replace("&#58;", ":")}&${parameters.searchFormLocationParameters?if_exists}&backAreaId=common-container&${StringUtil.wrapString(parameters.queryString?if_exists)?replace("&amp;", "&")}&orderBy=${StringUtil.wrapString(parameters.orderBy?if_exists)}&responsiblePartyId=${parameters.responsiblePartyId?if_exists}&evalManagerPartyId=${parameters.evalManagerPartyId?if_exists}&evalPartyId=${parameters.evalPartyId?if_exists}&entityName=WorkEffortRootView&currentStatusContains=' +currentStatusContains + '&noLeftBar=' + noLeftBar + '&gpMenuEnumId=' + gpMenuEnumId + '&childStruct=' + childStruct + '&hideIsRootActive=' + hideIsRootActive + '&hideChildStruct=' + hideChildStruct + '&gpMenuOrgUnitRoleTypeId=' + encodeURIComponent(gpMenuOrgUnitRoleTypeId),
		                                        {onComplete: function(transport) {ReasonPopupMgr.checkErrorsAndSubmit(transport)}});
		                                    
		                                    newForm.remove();           
					                    }
					                                
					                    ReasonPopupMgr.checkErrorsAndSubmit = function(transport) {
		                                    var currentStatusContains = '${parameters.currentStatusContains?if_exists}';
		                                    if (currentStatusContains == '' || currentStatusContains == null || currentStatusContains == 'null') {
		                                    	if (initialStatusId && initialStatusId.indexOf('_PLAN') > -1) {
		                                    		currentStatusContains = '_PLAN';
		                                    	}
		                                    	if (initialStatusId && initialStatusId.indexOf('_MONIT') > -1) {
		                                    		currentStatusContains = '_MONIT';
		                                    	}
		                                    	if (initialStatusId && initialStatusId.indexOf('_EXEC') > -1) {
		                                    		currentStatusContains = '_EXEC';
		                                    	}
		                                    }					                    	
					                    	
					                        var data = transport.responseText.evalJSON(true);
					                        if (data._ERROR_MESSAGE_ != null || data._ERROR_MESSAGE_LIST_ != null) {
					                            Modalbox.hide();
					                            modal_box_messages._resetMessages();
					                            modal_box_messages.onAjaxLoad(data, Prototype.K);					                                		
					                            ReasonPopupMgr.reset();
					                        } else {
					                            Modalbox.hide();
					                            if ('${parameters.survey?if_exists}' == 'Y'){
					                                var form = $('WorkEffortRootViewManagementForm') ;
						                                ajaxSubmitFormUpdateAreas(form,'common-container','common-container,<@ofbizUrl>surveyComplete</@ofbizUrl>,MainColumnStyle=single-column-fullopen&noInfoToolbar=true&noMasthead=true',
					                                    {onComplete: function(transport) {
						                                    	UpdateAreaResponder._updateElement(transport, 'common-container,<@ofbizUrl>logoutSurvey</@ofbizUrl>,MainColumnStyle=single-column-fullopen&noLeftBar=true&noInfoToolbar=true&noMasthead=true');
		                                                }});
		                                        	
		                                     	} else {
		                                     	    var gpMenuEnumId = '';
			                                    	var mainForm = $('WorkEffortRootViewManagementForm');
			                                    	if (Object.isElement(mainForm)) {
			                                    		var gpMenuEnumIdField = mainForm.down('input[name=gpMenuEnumId]');
														if (Object.isElement(gpMenuEnumIdField)) {
															gpMenuEnumId = gpMenuEnumIdField.getValue();
														} 
													}
													var childStruct = '${parameters.childStruct?if_exists?default("N")}';
													var hideIsRootActive = '${parameters.hideIsRootActive?if_exists?default("")}'; 
													var hideChildStruct = '${parameters.hideChildStruct?if_exists?default("")}';
		                                     		var noLeftBar = '${parameters.noLeftBar?if_exists?string}';
		                                     		var gpMenuOrgUnitRoleTypeId = '${parameters.gpMenuOrgUnitRoleTypeId?if_exists?default("N")}';
													UpdateAreaResponder._updateElement(transport, 'common-container,<@ofbizUrl>searchResultContainerOnly</@ofbizUrl>,searchResultContextFormName=${StringUtil.wrapString(parameters.searchResultContextFormName?if_exists)?replace("&#58;", ":")}&searchResultContextFormLocation=${StringUtil.wrapString(parameters.searchResultContextFormLocation?if_exists)?replace("&#58;", ":")}&searchFormScreenLocation=${StringUtil.wrapString(parameters.searchFormScreenLocation?if_exists)?replace("&#58;", ":")}&searchFormScreenName=${StringUtil.wrapString(parameters.searchFormScreenName?if_exists)?replace("&#58;", ":")}&advancedSearchFormLocation=${StringUtil.wrapString(parameters.advancedSearchFormLocation?if_exists)?replace("&#58;", ":")}&searchFormResultLocation=${StringUtil.wrapString(parameters.searchFormResultLocation?if_exists)?replace("&#58;", ":")}&searchFormLocation=${StringUtil.wrapString(parameters.searchFormLocation?if_exists)?replace("&#58;", ":")}&${parameters.searchFormLocationParameters?if_exists}&backAreaId=common-container&${StringUtil.wrapString(parameters.queryString?if_exists)?replace("&amp;", "&")}&orderBy=${StringUtil.wrapString(parameters.orderBy?if_exists)}&responsiblePartyId=${parameters.responsiblePartyId?if_exists}&evalManagerPartyId=${parameters.evalManagerPartyId?if_exists}&evalPartyId=${parameters.evalPartyId?if_exists}&entityName=WorkEffortRootView&currentStatusContains=' +currentStatusContains  + '&noLeftBar=' + noLeftBar + '&gpMenuEnumId=' + gpMenuEnumId + '&childStruct=' + childStruct + '&hideIsRootActive=' + hideIsRootActive + '&hideChildStruct=' + hideChildStruct + '&gpMenuOrgUnitRoleTypeId=' + encodeURIComponent(gpMenuOrgUnitRoleTypeId));
		                                     	}  
		                                     	
		                                     	             
					                            
					                        }					                                	
					                    }					                                
					                                    
					                    ReasonPopupMgr.reset = function() {
				                        	var statusField = $('IndividualPerformanceHeaderTable').down('select[id="currentStatusId"]');
				                            if (Object.isElement(statusField)) {
				                            	statusField.setValue('${weStatusId?if_exists}');
				                            } else {
				                            	statusField = $('IndividualPerformanceHeaderTable').down('input[id="currentStatusId"]');
				                            	if (Object.isElement(statusField)) {
				                            		statusField.setValue('${weStatusValue?if_exists}');
				                            	}
				                            }														
					                    }	
					                 	
					                    var workEffortRootId = "";
				                        var workEffortRootIdField = $("workEffortRootId");
				                        if (Object.isElement(workEffortRootIdField)) {
				                        	workEffortRootId = workEffortRootIdField.getValue();
				                        }
					                                
				                        var backStepLink = $("statusBackStep");
										if(Object.isElement(backStepLink)) {
											Event.stopObserving(backStepLink, "click");
											Event.observe(backStepLink, "click", function(e) {	
 				                            	new Ajax.Request("<@ofbizUrl>checkFolderActivated</@ofbizUrl>", {
                                    				parameters: {"workEffortRootId": workEffortRootId, "folder": "WEFLD_STATUS", "targetStatusId": '${backStatusId}'},
                                    				onSuccess: function(response) {
                                    					var data = response.responseText.evalJSON(true);
                                    					// console.log("[WorkEffortViewIndividualPerformanceHeader.ftl] 0_back: Stato target (back) = ${backStatusId}, data.hasMandatoryComment? " + data.hasMandatoryComment);
                                    					if (data) {
                                    						if (data.isFolderVisible == "Y") {
                                    							Modalbox.show($('popup-reason-boxs-container'), {statusId : '${backStatusId}', transitions : false});
                                    							
	                                    						// GN-5187
	                                    						// console.log("[WorkEffortViewIndividualPerformanceHeader.ftl] 1_back: Invocazione di manageReasonDescriptionMandatory al restore dello stato precedente, hasMandatoryComment? " + data.hasMandatoryComment);
	                                    						ReasonPopupMgr.manageReasonDescriptionMandatory(data.hasMandatoryComment);
                                    						} else {
                                    							Modalbox.show($('popup-boxs-container'), {statusId : '${backStatusId}', transitions : false});
                                    						}
                                    					}
                                    				},
                                    				onFailure: function() {
                                    					Modalbox.show($('popup-boxs-container'), {statusId : '${backStatusId}', transitions : false});
                                    				}
                                    			});
											});
										}													
																								
										var currentStatusIdField = $('currentStatusId');
				                        if (Object.isElement(currentStatusIdField)) {
				                            Event.stopObserving(currentStatusIdField, 'change');
				                            Event.observe(currentStatusIdField, 'change', function(e) {
				                            	// console.log("[WorkEffortViewIndividualPerformanceHeader.ftl] 0_change: Stato target (conferma cambio): " + $('currentStatusId').getValue());
                                    			new Ajax.Request("<@ofbizUrl>checkFolderActivated</@ofbizUrl>", {
                                    				parameters: {"workEffortRootId": workEffortRootId, "folder": "WEFLD_STATUS", "targetStatusId": $('currentStatusId').getValue()},
                                    				onSuccess: function(response) {
                                    					var data = response.responseText.evalJSON(true);
                                    					// console.log("[WorkEffortViewIndividualPerformanceHeader.ftl] 0_change: Stato target (conferma cambio): " + $('currentStatusId').getValue() + ", data.hasMandatoryComment? " + data.hasMandatoryComment);
                                    					if (data) {
                                    						// Decide se deve visualizzare, nel popup, la text area relativa alla reason
                                    						if (data.isFolderVisible == "Y") {
                                    							Modalbox.show($('popup-reason-boxs-container'), {statusId : currentStatusIdField.getValue(), transitions : false,
                                    							beforeHide : ReasonPopupMgr.reset});

                                    							// GN-5187
																// console.log("[WorkEffortViewIndividualPerformanceHeader.ftl] 1_change: Invocazione di manageReasonDescriptionMandatory al cambiamento di stato, hasMandatoryComment? " + data.hasMandatoryComment);
																ReasonPopupMgr.manageReasonDescriptionMandatory(data.hasMandatoryComment);
															} else {
																Modalbox.show($('popup-boxs-container'), {statusId : currentStatusIdField.getValue(), transitions : false,
                                    							beforeHide : ReasonPopupMgr.reset});
                                    						}		
                                    					}
                                    				},
                                    				onFailure: function() {
                                    					Modalbox.show($('popup-boxs-container'), {statusId : currentStatusIdField.getValue(), transitions : false,
                                    					beforeHide : ReasonPopupMgr.reset});
                                    				}
                                    			});
				                            });
				                        }
				                    </script>											
								</#if>
											
							</#if>
						</tr>
					</tbody>
				</table>
			</td>
		</tr>
		<#if evalManagerPersonName?has_content || evalApproverPersonName?has_content>
		    <#if evalManagerPersonName?has_content && evalApproverPersonName?has_content>
		        <tr>
		            <td class="label">
		               <div style="margin-left: 30px;">${evalManagerPersonLabel?if_exists}</div>
				    <td>
			            <input type="text" size="50" value="${evalManagerPersonName?if_exists}" name="evalManagerPersonName" readonly="readonly"/>
			        </td>
			        <td class="label">${evalApproverPersonLabel?if_exists}</td>
			        <td><input type="text" size="50" value="${evalApproverPersonName?if_exists}" name="evalApproverPersonName" readonly="readonly"/></td>
			    </tr>
		    <#else>
		        <#if evalManagerPersonName?has_content>
		            <tr>
	                    <td class="label">
                            <div style="margin-left: 30px;">${evalManagerPersonLabel?if_exists}</div>
		                <td>
	                        <input type="text" size="50" value="${evalManagerPersonName?if_exists}" name="evalManagerPersonName" readonly="readonly"/>
	                    </td>
	                </tr>
		        </#if>
		        <#if evalApproverPersonName?has_content>
		            <tr>
                        <td class="label">
                            <div style="margin-left: 30px;">${evalApproverPersonLabel?if_exists}</div>
	                    <td>
                            <input type="text" size="50" value="${evalApproverPersonName?if_exists}" name="evalApproverPersonName" readonly="readonly"/>
                        </td>
                    </tr>
	            </#if>		        
		    </#if>
		</#if>
	</tbody>
</table>

<div style="display: none;" id="popup-reason-boxs-container" class="popup-reason-boxs-container">
<span class="hidden-label" id="popup-reason-box-title">${uiLabelMap.BaseMenusHistoricizeTab}</span>
<div class="popup-reason-box-container">
    <div class="popup-reason-body-container">
        <div id="popup-reason-text-container" class="popup-reason-text-container">
        	${uiLabelMap.WorkEffortConfirmChangeStatus}
        	<@gzoomLogger.gzoomInfo message="[WorkEffortViewIndividualPerformanceHeader.ftl] Visualizzazione popup con TextArea (popup-reason-...)" type="none"/>
           <br/>
           <textarea rows="6" cols="70" name="reasonDescription" id="reasonDescription" class="" ></textarea>
           <br/>
           <br/>
           <br/>
        </div>
    </div>
    <div class="popup-copy-all-buttons-container">
        <a href="#" class="smallSubmit button-cancel" onclick="javascript:ReasonPopupMgr.reset(); Modalbox.hide()">${uiLabelMap.BaseButtonCancel}</a>
        <a href="#" id="popup-reason-box-container-ok-button" class="smallSubmit button-ok" onclick="javascript: ReasonPopupMgr.validate();">${uiLabelMap.BaseButtonOK}</a>
    </div>
</div>
</div>

<div style="display: none;" id="popup-check-boxs-container" class="popup-check-boxs-container">
<span class="hidden-label" id="popup-check-box-title">${uiLabelMap.BaseMenusHistoricizeTab}</span>
<div class="popup-check-box-container">
    <div class="popup-check-body-container">
        <div id="popup-check-text-container" class="popup-check-text-container">
        	<@gzoomLogger.gzoomInfo message="[WorkEffortViewIndividualPerformanceHeader.ftl] Visualizzazione popup senza TextArea (popup-check-...)" type="none"/>
        </div>
    </div>
    <div class="popup-copy-all-buttons-container">
        <a href="#" class="smallSubmit button-ok" onclick="javascript:ReasonPopupMgr.reset(); Modalbox.hide()">${uiLabelMap.BaseButtonOK}</a>
    </div>
</div>
</div>

<div style="display: none;" id="popup-boxs-container" class="popup-reason-boxs-container">
<span class="hidden-label" id="popup-box-title">${uiLabelMap.BaseMenusHistoricizeTab}</span>
<div class="popup-box-container">
    <div class="popup-body-container">
        <div id="popup-text-container" class="popup-reason-text-container">
        	${uiLabelMap.WorkEffortConfirmChangeStatus}
            <@gzoomLogger.gzoomInfo message="[WorkEffortViewIndividualPerformanceHeader.ftl] Visualizzazione popup senza TextArea (popup-box-...)" type="none"/>
           <br/>
           <br/>
        </div>
    </div>
    <div class="popup-copy-all-buttons-container">
        <a href="#" class="smallSubmit button-cancel" onclick="javascript:ReasonPopupMgr.reset(); Modalbox.hide()">${uiLabelMap.BaseButtonCancel}</a>
        <a href="#" class="smallSubmit button-ok" onclick="javascript: ReasonPopupMgr.validate();">${uiLabelMap.BaseButtonOK}</a>
    </div>
</div>
</div>

