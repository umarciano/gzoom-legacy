/**
 * WorkEffort Redirect Handler
 * Gestisce il redirect automatico alla pagina di dettaglio dopo la creazione di una scheda
 */

// Estende la funzione ajaxSubmitFormUpdateAreas per gestire il redirect post-creazione
var originalAjaxSubmitFormUpdateAreas = window.ajaxSubmitFormUpdateAreas;

window.ajaxSubmitFormUpdateAreas = function(form, areaCsvString, extraAreaId, options) {
    submitFormDisableSubmits($(form));
    waitSpinnerShow();
    
    // Controllo se è una form di WorkEffort
    var isWorkEffortForm = $(form).hasClassName('basic-form') && 
                          $(form).down('input[name="entityName"][value="WorkEffort"]');
    
    var updateFunction = function(transport) {
        var data = transport.responseText.evalJSON(true);
        
        if (data._ERROR_MESSAGE_LIST_ != undefined || data._ERROR_MESSAGE_ != undefined) {
            if(!$('content-messages')) {
               //add this div just after app-navigation
               if($('content-main-section')){
                   $('content-main-section' ).insert({before: '<div id="content-messages"></div>'});
               }
            }
           $('content-messages').addClassName('errorMessage');
           $('content-messages' ).update(data._ERROR_MESSAGE_LIST_ + " " + data._ERROR_MESSAGE_);
           new Effect.Appear('content-messages',{duration: 0.5});
        } else {
            if($('content-messages')) {
                $('content-messages').removeClassName('errorMessage');
                new Effect.Fade('content-messages',{duration: 0.0});
            }
            
            // LOGICA REDIRECT PER WORKEFFORTS
            if (isWorkEffortForm && data.parameters && data.parameters.operation === 'CREATE' && 
                data.id && data.id.workEffortId) {
                
                console.log('[WorkEffort Redirect] Scheda creata con ID: ' + data.id.workEffortId);
                
                // Costruisci l'URL di redirect per la pagina di dettaglio
                var workEffortId = data.id.workEffortId;
                var redirectUrl = '/workeffortext/control/managementContainerOnly?' +
                    'workEffortId=' + workEffortId +
                    '&entityName=WorkEffortRootView' +
                    '&noLeftBar=true' +
                    '&noInfoToolbar=true' +
                    '&saveView=N' +
                    '&contextManagement=N' +
                    '&detail=Y' +
                    '&managementFormType=' +
                    '&searchFormLocation=component://workeffortext/widget/forms/WorkEffortRootViewForms.xml' +
                    '&searchFormResultLocation=component://workeffortext/widget/forms/WorkEffortRootViewForms.xml' +
                    '&advancedSearchFormLocation=component://workeffortext/widget/forms/WorkEffortRootViewForms.xml' +
                    '&managementFormLocation=component://workeffortext/widget/forms/WorkEffortRootViewForms.xml' +
                    '&backAreaId=common-container';
                
                console.log('[WorkEffort Redirect] Redirect URL: ' + redirectUrl);
                
                // Esegui il redirect alla pagina di dettaglio
                ajaxUpdateAreas('common-container,' + redirectUrl + ',');
                
            } else {
                // Comportamento normale per altri casi
                if (extraAreaId && extraAreaId.length > 0) {
                    ajaxUpdateAreas(areaCsvString + ',' + extraAreaId);
                } else {
                    ajaxUpdateAreas(areaCsvString);
                }
            }
        }
    }
    
    new Ajax.Request($(form).action, {
        parameters: $(form).serialize(true),
        onComplete: updateFunction 
    });
}
