<!-- 
variabili
 mandatory = "mandatori" per definire il tipo mandatori
 workEffortIdAll = Y/N, per estrarre tutti i workEffort senza considerare il filtro parentTypeId

-->


<tr>
       
	<#assign mandatory="mandatory"/>
   <#assign workEffortIdAll=workEffortIdAll?default("N")/>
   
   <td class="label">${uiLabelMap.WorkeffortRoot}</td>
    <!--snapshot -->
   <#if parameters.snapshot?if_exists?default("N") == 'Y'>	
	<input  class="autocompleter_parameter" type="hidden" name="selectFields" value="${parameters.snapshot}"/> 	
   </#if>
   <td class="widget-area-style">
   <div id="select-report-work-effort-print-row">
	   <#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_reloadWorkEffortId.ftl" />
   </div>
   </td>
</tr>

<script>
    WorkEffortTypeIdWorkEffortIdExtPrintBirtExtraParameter = {
    	load: function() {
    	    var form = $('${printBirtFormId?default("ManagementPrintBirtForm")}');
    	    var checkedTypeEkement = form.select('input[name=workEffortTypeId]').find(function(element) {
                return element.checked;
            });
            
            if (checkedTypeEkement) {
            	ajaxUpdateArea('select-report-work-effort-print-row', '<@ofbizUrl>loadReportWorkEffortPrintBirtList</@ofbizUrl>', 
            		{
                		workEffortTypeId : checkedTypeEkement.getValue(), 
                		mandatory : '${mandatory?if_exists}',
                		workEffortIdAll :  '${workEffortIdAll?if_exists}',
                		parentTypeId: '${parameters.parentTypeId?if_exists}',
                		snapshot : '${parameters.snapshot?if_exists}',
                		useFilter : '${parameters.useFilter?if_exists}',
                		saveView : 'N',
                		<#-- Aggiungi parametri per filtering utenti Valutato dalla sessione -->
                		isEmplValutato: '${(session.getAttribute("isEmplValutato")!false)?string}',
                		useWorkEffortPartyView: '${(session.getAttribute("useWorkEffortPartyView")!false)?string}',
                		userPartyId: '${session.getAttribute("userPartyId")!""}'
            		}
        		);
        	}
            <#-- Performance Strategica (CTX_BS): mostra il selettore dei report (radio) per far scegliere
                 quale stampa generare (Assegnazione / Descrizione e razionali). Solo per CTX_BS: gli altri
                 tipi restano invariati (riga select-print-row nascosta come da default). -->
            if (checkedTypeEkement && checkedTypeEkement.getValue() == 'CTX_BS') {
                var _spr = $('select-print-row');
                if (_spr) { _spr.show(); }
            }
    	},
    
        changeRadioButtons : function(value) {   
        	//devo caricare la lista di stampa da visualizzare
            ajaxUpdateArea('select-report-work-effort-print-row', '<@ofbizUrl>loadReportWorkEffortPrintBirtList</@ofbizUrl>', 
            	{
                	workEffortTypeId : value, 
                	mandatory : '${mandatory?if_exists}',
                	workEffortIdAll :  '${workEffortIdAll?if_exists}',
                	parentTypeId: '${parameters.parentTypeId?if_exists}',
                	snapshot : '${parameters.snapshot?if_exists}',
                	useFilter : '${parameters.useFilter?if_exists}',
                	saveView : 'N',
                	<#-- Aggiungi parametri per filtering utenti Valutato dalla sessione -->
                	isEmplValutato: '${(session.getAttribute("isEmplValutato")!false)?string}',
                	useWorkEffortPartyView: '${(session.getAttribute("useWorkEffortPartyView")!false)?string}',
                	userPartyId: '${session.getAttribute("userPartyId")!""}'
            	}
        	);           
        }
    }
    
    //WorkEffortTypeIdWorkEffortIdExtPrintBirtExtraParameter.load();
    document.observe("dom:loaded", jQuery(WorkEffortTypeIdWorkEffortIdExtPrintBirtExtraParameter.load));   
</script>


