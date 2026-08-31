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
        
        // Consenti solo numeri compresi tra 1 e 60 con massimo 2 cifre decimali
        // (separatore decimale: virgola o punto)
        input.onkeypress = function(e) {
            var c = e.charCode || e.which;
            var ch = String.fromCharCode(c);
            // consenti le cifre
            if (c >= 48 && c <= 57) { return true; }
            // consenti un solo separatore decimale (virgola o punto)
            if (ch === ',' || ch === '.') {
                if (this.value.indexOf(',') === -1 && this.value.indexOf('.') === -1) {
                    return true;
                }
            }
            e.preventDefault();
            return false;
        };
        input.oninput = function() {
            // mantieni solo cifre e un separatore, normalizza il punto in virgola, max 2 decimali
            var v = this.value.replace(/\./g, ',').replace(/[^0-9,]/g, '');
            var parts = v.split(',');
            if (parts.length > 2) {
                v = parts[0] + ',' + parts.slice(1).join('');
                parts = v.split(',');
            }
            if (parts.length === 2) {
                v = parts[0] + ',' + parts[1].substring(0, 2);
            }
            this.value = v;
        };
        
        // Validazione al click su Salva
        var saveButton = document.querySelector('li.save.search-save a');
        if (saveButton) {
            saveButton.addEventListener('click', function(e) {
                var value = input.value.trim();
                
                // Se vuoto, permetti (verrà gestito lato server)
                if (value === '') {
                    return true;
                }
                
                // Normalizza il separatore decimale per la validazione numerica
                var numValue = parseFloat(value.replace(',', '.'));
                
                // Validazione range 1-60 (incluse 2 cifre decimali) solo se valorizzato
                if (isNaN(numValue) || numValue < 1 || numValue > 60) {
                    e.preventDefault();
                    e.stopPropagation();
                    alert('Il valore della Performance Strategica deve essere compreso tra 1 e 60 (sono ammesse fino a 2 cifre decimali).\n\nValore inserito: ' + value);
                    input.focus();
                    return false;
                }
            }, true);
        }
    }
    
    // Aggiungi icona info "Legenda Valutazione" vicino alla label "Valore"
    setTimeout(function() {
        var form = document.getElementById('WETPMF001${parameters.reloadRequestType}_${parameters.contentIdInd}_WorkEffortTransactionView');
        if (form) {
            // Trova la label "Valore" nel form
            var labels = form.querySelectorAll('td.label');
            for (var i = 0; i < labels.length; i++) {
                if (labels[i].textContent.trim() === 'Valore') {
                    // Aggiungi icona info vicino alla label
                    var infoIcon = document.createElement('a');
                    infoIcon.href = 'javascript:void(0);';
                    infoIcon.className = 'legenda-valutazione-form-trigger';
                    infoIcon.style.cssText = 'margin-left: 8px; color: #0066cc; font-size: 14px; cursor: pointer; text-decoration: none;';
                    infoIcon.title = 'Clicca per visualizzare la legenda valutazione';
                    infoIcon.innerHTML = '<i class="fa fa-info-circle" style="font-size: 16px;"></i>';
                    
                    // Event handler per mostrare la popup
                    infoIcon.addEventListener('click', function(e) {
                        e.preventDefault();
                        
                        // Recupera weContextId da parameters.parentWorkEffortTypeId (disponibile dal context Groovy)
                        var weContextId = '${parameters.parentWorkEffortTypeId!""}';
                        
                        // Fallback: CTX_EP (Performance Individuale)
                        if (!weContextId || weContextId === '') {
                            weContextId = 'CTX_EP';
                        }
                        
                        var message = '';
                        
                        if (weContextId === 'CTX_BS') {
                            // Performance Strategica
                            message = '<div style="padding: 15px;">' +
                                      '<h3 style="margin-top: 0; margin-bottom: 15px; color: #333; border-bottom: 2px solid #0066cc; padding-bottom: 10px;">Legenda Valutazione - Performance Strategica</h3>' +
                                      '<p style="font-size: 14px; line-height: 1.6; margin: 0;"><span style="font-weight: bold; color: #0066cc; font-size: 15px;">Inserire un Valore compreso tra 1 e 60 (sono ammesse fino a 2 cifre decimali)</span></p>' +
                                      '</div>';
                        } else {
                            // Performance Individuale
                            message = '<div style="padding: 15px;">' +
                                      '<h3 style="margin-top: 0; margin-bottom: 15px; color: #333; border-bottom: 2px solid #0066cc; padding-bottom: 10px;">Legenda Valutazione - Performance Individuale</h3>' +
                                      '<p style="font-size: 14px; line-height: 2; margin: 0;">' +
                                      '<span style="font-weight: bold; color: #d9534f;">1</span> - Insufficiente<br>' +
                                      '<span style="font-weight: bold; color: #f0ad4e;">2</span> - Mediocre<br>' +
                                      '<span style="font-weight: bold; color: #5bc0de;">3</span> - Sufficiente<br>' +
                                      '<span style="font-weight: bold; color: #5cb85c;">4</span> - Buono<br>' +
                                      '<span style="font-weight: bold; color: #0066cc;">5</span> - Eccellente' +
                                      '</p>' +
                                      '</div>';
                        }
                        
                        // Mostra la popup modale
                        modal_box_messages.alert(message);
                    });
                    
                    labels[i].appendChild(infoIcon);
                    break;
                }
            }
        }
    }, 200); // Timeout per garantire il rendering completo del form
})();
</script>

<#if showValuesPanel?has_content && showValuesPanel?default('N') == 'Y'>
</div>
</#if>