<#if showValuesPanel?has_content && showValuesPanel?default('N') == 'Y'>
<div id="WorkEffortTransactionStandardLayoutModelPortletContainer_${parameters.contentIdInd}" class="transactionPortlet">
</#if>
<div id="child-management-container-WorkEffortMeasureIndicatorModelPortlet_${parameters.contentIdInd?if_exists}">
    <div>
        <div class="management child-management" id="child-management-screenlet-container-WorkEffortMeasureIndicatorModelPortlet_${parameters.contentIdInd?if_exists}">
            <div id="child-management-screenlet-body-WorkEffortMeasureIndicatorModelPortlet_${parameters.contentIdInd?if_exists}" class="screenlet">
                <div class="screenlet-title-bar">
                    <ul>
                        <li class="h3">
                     		<#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
                    			${portletTitleLang?if_exists}
                    		<#else>
                    			${portletTitle?if_exists}
                    		</#if>                        
                        </li>
                        <style>
                            li.save::before, li.save::after, li.delete::before, li.delete::after,
                            li.save a::before, li.save a::after, li.delete a::before, li.delete a::after {
                                content: none !important;
                                display: none !important;
                            }
                        </style>
                        <#if showValuesPanel?has_content && showValuesPanel?default('N') == 'Y'>
                            <li title="${uiLabelMap.CommonRemove}" class="hidden delete search-delete" style="float: right !important; list-style: none; margin-right: 5px;"><a href="javascript:void(0);" style="width: auto !important; height: auto !important; padding: 5px 15px; display: inline-block; white-space: nowrap; color: #fff !important; text-decoration: none !important; background: none !important; cursor: pointer; font-family: Arial, sans-serif !important;">${uiLabelMap.CommonRemove}</a></li>
                        <#else>
                            <li title="${uiLabelMap.CommonRemove}" class="delete search-delete" style="float: right !important; list-style: none; margin-right: 5px;"><a href="javascript:void(0);" style="width: auto !important; height: auto !important; padding: 5px 15px; display: inline-block; white-space: nowrap; color: #fff !important; text-decoration: none !important; background: none !important; cursor: pointer; font-family: Arial, sans-serif !important;">${uiLabelMap.CommonRemove}</a></li>
                        </#if>
                        <li title="${uiLabelMap.CommonSave}" class="save search-save" style="float: right !important; list-style: none; margin-left: 10px; margin-right: 5px;"><a href="javascript:void(0);" style="width: auto !important; height: auto !important; padding: 5px 15px; display: inline-block; white-space: nowrap; color: #fff !important; text-decoration: none !important; background: none !important; cursor: pointer; font-family: Arial, sans-serif !important;">${uiLabelMap.CommonSave}</a></li>
                        <!-- Label "Consuntivo" rimossa per dare più spazio ai pulsanti Salva e Rimuovi
                        <li style="float: right !important" class="h3">
                      		<#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
                    			${glFiscalTypeDescriptionLang?if_exists}
                    		<#else>
                    			${glFiscalTypeDescription?if_exists}
                    		</#if>                        
                        </li>
                        -->
                    </ul>
                    <br class="clear">
                </div>
                <div class="screenlet-body">
                    <div id="child-management-container-body-WorkEffortMeasureIndicatorModelPortlet_${parameters.contentIdInd?if_exists}">

                    ${screens.render(managementFormScreenLocation, managementFormScreenName, Static["org.ofbiz.base.util.UtilMisc"].toMap("managementFormName", managementFormName, "managementFormLocation", managementFormLocation))}
 
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<#-- Validazione campo weTransValue (valutazione) per Performance Strategica (CTX_BS) -->
<script type="text/javascript">
(function() {
    var input = document.getElementById('WorkEffortTransactionViewPortletManagementForm_weTransValue');
    if (input && input.type === 'text') {
        // Blocca incolla
        input.onpaste = function(e) { e.preventDefault(); return false; };
        
        // Blocca lettere - solo numeri
        input.onkeypress = function(e) { var c = e.charCode || e.which; if (c < 48 || c > 57) { e.preventDefault(); return false; } };
        input.oninput = function() { this.value = this.value.replace(/[^0-9]/g, ''); };
        
        // Validazione al click su Salva
        var saveButton = document.querySelector('li.save.search-save a');
        if (saveButton) {
            saveButton.addEventListener('click', function(e) {
                var value = input.value.trim();
                
                // Se vuoto, permetti (verrà gestito lato server)
                if (value === '') {
                    return true;
                }
                
                var numValue = parseInt(value, 10);
                
                // Validazione range 1-60 solo se valorizzato
                if (isNaN(numValue) || numValue < 1 || numValue > 60) {
                    e.preventDefault();
                    e.stopPropagation();
                    alert('Il valore della Performance Strategica deve essere compreso tra 1 e 60.\n\nValore inserito: ' + value);
                    input.focus();
                    return false;
                }
            }, true);
        }
    }
})();
</script>

<#if showValuesPanel?has_content && showValuesPanel?default('N') == 'Y'>
</div>
</#if>