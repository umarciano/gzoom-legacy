<script type="text/javascript">
	
	ReasonPopupMgr = Class.create({ });
	
	ReasonPopupMgr.getParametersToSubmit = function() {    
	    var parametersToSubmit = {};
	   	
	    var reason = $('reasonDescription');
	    if (reason != null) {
	        parametersToSubmit.reason = reason.getValue();
	    }
	    return parametersToSubmit;
	}
    
    ReasonPopupMgr.validate = function() {
    	// aggiunto hide perche la funzione show aggiunte MB_ all'id del content e poi il secondo click no ntrova piu la popup da mostrare.
    	// hide rimuove MB_ dalll'ide del content ma solo se e abbastanza veloce da comparire prima dell'altro messaggio
        parametersToSubmit = ReasonPopupMgr.getParametersToSubmit();
        
        options['transitions'] = false;
        Modalbox.hide(options);
                
    	
    	var newForm = new Element('form', {id : 'changeAllStatusForm', name : 'changeAllStatusForm'});
    	newForm.insert(new Element('input', {name : 'queryStringMap', type : 'hidden', value : '${parameters.queryStringMap?if_exists}'}));
    	newForm.insert(new Element('input', {name : 'entityNamePrefix', type : 'hidden', value : 'WorkEffortRoot'}));
    	newForm.insert(new Element('input', {name : 'entityName', type : 'hidden', value : 'WorkEffortRootView'}));
    	newForm.insert(new Element('input', {name : 'reason', type : 'text', id: 'reason', value : parametersToSubmit.reason}));
		newForm.insert(new Element('input', {name : 'responsiblePartyId', type : 'hidden', value : '${parameters.responsiblePartyId?if_exists}'}));
    	newForm.insert(new Element('input', {name : 'responsibleRoleTypeId', type : 'hidden', value : '${parameters.responsibleRoleTypeId?if_exists}'}));
    	newForm.insert(new Element('input', {name : 'weResponsiblePartyId', type : 'hidden', value : '${parameters.weResponsiblePartyId?if_exists}'}));
    	newForm.insert(new Element('input', {name : 'weResponsibleRoleTypeId', type : 'hidden', value : '${parameters.weResponsibleRoleTypeId?if_exists}'}));
    	newForm.insert(new Element('input', {name : 'searchDate', type : 'hidden', value : '${parameters.searchDate?if_exists}'}));
    	
    	newForm.insert(new Element('input', {name : 'evalManagerPartyId', type : 'hidden', value : '${parameters.evalManagerPartyId?if_exists}'}));
    	newForm.insert(new Element('input', {name : 'evalPartyId', type : 'hidden', value : '${parameters.evalPartyId?if_exists}'}));
    	
    	var gpMenuEnumId = '${parameters.gpMenuEnumId?if_exists?default("")}';
    	newForm.insert(new Element('input', {name : 'gpMenuEnumId', type : 'hidden', value : gpMenuEnumId}));
    	
    	var childStruct = '${parameters.childStruct?if_exists?default("N")}';
    	newForm.insert(new Element('input', {name : 'childStruct', type : 'hidden', value : childStruct}));
    	
    	/** Inserisco se sto facendo un NEXT o PREV**/
    	newForm.insert(new Element('input', {name : 'statusType', type : 'hidden', value : ReasonPopupMgr.statuType}));
    	
    	var target = '<@ofbizUrl>changeAllStatusForm</@ofbizUrl>';
        document.body.insert(newForm);
        
        new Ajax.Request(target, {
            parameters: newForm.serialize(true),
            content: 'common-container',
            onSuccess: function(transport) {
            	modal_box_messages._resetMessages();
                var data = transport.responseText.evalJSON(true);                
                if (data._ERROR_MESSAGE_ != null || data._ERROR_MESSAGE_LIST_ != null) {
					modal_box_messages.onAjaxLoad(data, Prototype.K);
                    return false;
                }
                modal_box_messages.alert("${uiLabelMap.ChangeStatusAll_finished}<br><br>${uiLabelMap.ChangeStatusAll_itemSuccess}" + data.itemSuccess + "<br><br>${uiLabelMap.ChangeStatusAll_itemFailed}" + data.itemFailed, null, function(element) {
                    ajaxSubmitFormUpdateAreas('searchForm','common-container','')
                    LookupProperties.afterHideModal();
        		});
            }
        });
        newForm.remove();
    }
       
    ReasonPopupMgr.validateSimplified = function() {
    	var reason = '';
	    var reasonField = $('reasonDescriptionSimplified');
	    if (reasonField != null) {
	        reason = reasonField.getValue();
	    }
	    
	    options['transitions'] = false;
        Modalbox.hide(options);
        
    	var newForm = new Element('form', {id : 'changeAllStatusSimplifiedForm', name : 'changeAllStatusSimplifiedForm'});
    	newForm.insert(new Element('input', {name : 'queryStringMap', type : 'hidden', value : '${parameters.queryStringMap?if_exists}'}));
    	newForm.insert(new Element('input', {name : 'entityNamePrefix', type : 'hidden', value : 'WorkEffortRoot'}));
    	newForm.insert(new Element('input', {name : 'entityName', type : 'hidden', value : 'WorkEffortRootView'}));
    	newForm.insert(new Element('input', {name : 'reason', type : 'text', id: 'reason', value : reason}));
		newForm.insert(new Element('input', {name : 'responsiblePartyId', type : 'hidden', value : '${parameters.responsiblePartyId?if_exists}'}));
    	newForm.insert(new Element('input', {name : 'responsibleRoleTypeId', type : 'hidden', value : '${parameters.responsibleRoleTypeId?if_exists}'}));
    	newForm.insert(new Element('input', {name : 'weResponsiblePartyId', type : 'hidden', value : '${parameters.weResponsiblePartyId?if_exists}'}));
    	newForm.insert(new Element('input', {name : 'weResponsibleRoleTypeId', type : 'hidden', value : '${parameters.weResponsibleRoleTypeId?if_exists}'}));
    	newForm.insert(new Element('input', {name : 'searchDate', type : 'hidden', value : '${parameters.searchDate?if_exists}'}));
    	
    	newForm.insert(new Element('input', {name : 'evalManagerPartyId', type : 'hidden', value : '${parameters.evalManagerPartyId?if_exists}'}));
    	newForm.insert(new Element('input', {name : 'evalPartyId', type : 'hidden', value : '${parameters.evalPartyId?if_exists}'}));
    	
    	var gpMenuEnumId = '${parameters.gpMenuEnumId?if_exists?default("")}';
    	newForm.insert(new Element('input', {name : 'gpMenuEnumId', type : 'hidden', value : gpMenuEnumId}));
    	
    	var childStruct = '${parameters.childStruct?if_exists?default("N")}';
    	newForm.insert(new Element('input', {name : 'childStruct', type : 'hidden', value : childStruct}));
    	
    	/** Inserisco se sto facendo un NEXT o PREV**/
    	newForm.insert(new Element('input', {name : 'statusType', type : 'hidden', value : ReasonPopupMgr.statuType}));
    	
    	var target = '<@ofbizUrl>changeAllStatusSimplifiedForm</@ofbizUrl>';
        document.body.insert(newForm);
        
        new Ajax.Request(target, {
            parameters: newForm.serialize(true),
            content: 'common-container',
            onSuccess: function(transport) {
            	modal_box_messages._resetMessages();
                var data = transport.responseText.evalJSON(true);                
                if (data._ERROR_MESSAGE_ != null || data._ERROR_MESSAGE_LIST_ != null) {
					modal_box_messages.onAjaxLoad(data, Prototype.K);
                    return false;
                }
                modal_box_messages.alert("${uiLabelMap.ChangeStatusAll_finished}<br><br>${uiLabelMap.ChangeStatusAll_itemSuccess}" + data.itemSuccess + "<br><br>${uiLabelMap.ChangeStatusAll_itemFailed}" + data.itemFailed, null, function(element) {
                    ajaxSubmitFormUpdateAreas('searchForm','common-container','')
                    LookupProperties.afterHideModal();
        		});
            }
        });
        newForm.remove();
    }    
    
    ReasonPopupMgr.statuType = null;

</script>
    
<#assign perm = "WORKEFFORTMGR_ADMIN"/>
<#if parameters.weContextId?if_exists == "CTX_BS">
    <#assign perm = "BSCPERFMGR_ADMIN"/>
<#elseif  parameters.weContextId?if_exists == "CTX_OR">
	<#assign perm = "ORGPERFMGR_ADMIN"/>
<#elseif  parameters.weContextId?if_exists == "CTX_CO">
	<#assign perm = "CORPERFMGR_ADMIN"/>
<#elseif  parameters.weContextId?if_exists == "CTX_PR">
	<#assign perm = "PROCPERFMGR_ADMIN"/>
<#elseif  parameters.weContextId?if_exists == "CTX_CG">
	<#assign perm = "CDGPERFMGR_ADMIN"/>
<#elseif  parameters.weContextId?if_exists == "CTX_TR">
	<#assign perm = "TRASPERFMGR_ADMIN"/>
<#elseif  parameters.weContextId?if_exists == "CTX_RE">
	<#assign perm = "RENDPERFMGR_ADMIN"/>
<#elseif  parameters.weContextId?if_exists == "CTX_EP">
	<#assign perm = "EMPLPERFMGR_ADMIN"/>
<#elseif  parameters.weContextId?if_exists == "CTX_GD">
	<#assign perm = "GDPRPERFMGR_ADMIN"/>
<#elseif  parameters.weContextId?if_exists == "CTX_PA">
	<#assign perm = "PARTPERFMGR_ADMIN"/>	
<#elseif  parameters.weContextId?if_exists == "CTX_DI">
	<#assign perm = "DIRIGPERFMGR_ADMIN"/>	
</#if>


<div align="right" style="margin-right: 100px;">
	<#if security.hasPermission(perm, context.userLogin)>
		<div class="smallSubmit button-all-select prev-status-simplified fa">
			<a href="#" class="fa-2x" title="${uiLabelMap.ChangeStatuPrevSimplified}" onclick="javascript: ReasonPopupMgr.statuType = 'PREV'; Modalbox.show($('popup-reason-simplified-boxs-container')); "></a>
		</div>
    </#if>
	<div class="smallSubmit button-all-select prev-status fa">
    	<a href="#" class="fa-2x" title="${uiLabelMap.ChangeStatuPrev}" onclick="javascript: ReasonPopupMgr.statuType = 'PREV'; Modalbox.show($('popup-reason-boxs-container')); "></a>
    </div>
    <b>${uiLabelMap.ChangeStatus}</b>
    <div class="smallSubmit button-all-select next-status fa">
    	<a href="#" class="fa-2x" title="${uiLabelMap.ChangeStatusNext}" onclick="javascript: ReasonPopupMgr.statuType = 'NEXT'; Modalbox.show($('popup-reason-boxs-container')); "></a>
	</div>
	<#if security.hasPermission(perm, context.userLogin)>
    	<div class="smallSubmit button-all-select next-status-simplified fa">
			<a href="#" class="fa-2x" title="${uiLabelMap.ChangeStatusNextSimplified}" onclick="javascript: ReasonPopupMgr.statuType = 'NEXT'; Modalbox.show($('popup-reason-simplified-boxs-container')); "></a>
		</div>	
    </#if>
</div>


<#-- POPUP per il cambio di stato!! -->
<div style="display: none;" id="popup-reason-boxs-container" class="popup-reason-boxs-container">
<span class="hidden-label" id="popup-reason-box-title">${uiLabelMap.BaseMenusHistoricizeTab}</span>
<div class="popup-reason-box-container">
    <div class="popup-reason-body-container">
        <div id="popup-reason-text-container" class="popup-reason-text-container">
        	${uiLabelMap.WorkEffortConfirmAllChangeStatus}
           <br/><br/>
           ${uiLabelMap.WorkEffortNumWorkEffortChangeStatus1} ${context.listIt.size()?if_exists}  ${uiLabelMap.WorkEffortNumWorkEffortChangeStatus2}
           <br/>
           <textarea rows="6" cols="75" name="reasonDescription" id="reasonDescription" class="mandatory"></textarea>
           <br/><br/><br/>
        </div>
    </div>
    <div class="popup-copy-all-buttons-container">
        <a href="#" class="smallSubmit button-cancel" onclick="Modalbox.hide()">${uiLabelMap.BaseButtonCancel}</a>
        <a href="#" class="smallSubmit button-ok" onclick="javascript: ReasonPopupMgr.validate(); ">${uiLabelMap.BaseButtonOK}</a>
    </div>
</div>
</div>



<div style="display: none;" id="popup-reason-simplified-boxs-container" class="popup-reason-simplified-boxs-container">
<span class="hidden-label" id="popup-reason-simplified-box-title">${uiLabelMap.BaseMenusHistoricizeTab}</span>
<div class="popup-reason-simplified-box-container">
    <div class="popup-reason-simplified-body-container">
        <div id="popup-reason-simplified-text-container" class="popup-reason-simplified-text-container">
        	${uiLabelMap.WorkEffortConfirmAllChangeStatus}
           <br/><br/>
           ${uiLabelMap.WorkEffortNumWorkEffortChangeStatus1Simplified} ${context.listIt.size()?if_exists}  ${uiLabelMap.WorkEffortNumWorkEffortChangeStatus2Simplified}
           <br/>
           <textarea rows="6" cols="75" name="reasonDescriptionSimplified" id="reasonDescriptionSimplified"></textarea>
           <br/><br/><br/>
        </div>
    </div>
    <div class="popup-copy-all-buttons-container">
        <a href="#" class="smallSubmit button-cancel" onclick="Modalbox.hide()">${uiLabelMap.BaseButtonCancel}</a>
        <a href="#" class="smallSubmit button-ok" onclick="javascript: ReasonPopupMgr.validateSimplified(); ">${uiLabelMap.BaseButtonOK}</a>
    </div>
</div>
</div>

